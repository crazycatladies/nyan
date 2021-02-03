package ftc.crazycatladies.nyan.actuators;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import ftc.crazycatladies.nyan.subsystem.Subsystem;
import ftc.crazycatladies.schrodinger.log.DataLogger;
import ftc.crazycatladies.schrodinger.opmode.OpModeTime;
import ftc.crazycatladies.schrodinger.state.StateMachine;

import org.json.JSONObject;

import java.util.Map;

public class ServoEx extends Subsystem {

    public static class ServoMoveContext {
        double start, end, posPerSec;

        public ServoMoveContext(double start, double end, double posPerSec) {
            this.start = start;
            this.end = end;
            this.posPerSec = posPerSec;
        }

        public double getStart() {
            return start;
        }

        public double getEnd() {
            return end;
        }

        @Override
        public String toString() {
            return "ServoMoveContext{start=" + start +
                    ", end=" + end +
                    ", posPerSec=" + posPerSec + '}';
        }
    }

    protected Servo servo;
    Double position;
    boolean isPositionSetThisLoop;
    private final StateMachine<ServoMoveContext> servoMoveSM;
    private final StateMachine<ServoMoveContext> servoMoveImmedSM;
    private final StateMachine<ServoMoveContext> servoMoveTopSpeedSM;

    public ServoEx(String name) {
        super(name);
        servoMoveSM = new StateMachine<ServoMoveContext>("ServoEx(" + name + ").servoMove");
        servoMoveSM.repeat((state, smc) -> {
            double pos = calcPos(smc.start, smc.end, state.getTimeInState().seconds() * smc.posPerSec);
            setPosition(pos);
            if (pos == smc.end)
                state.next();
        });

        servoMoveImmedSM = new StateMachine<ServoMoveContext>("ServoEx(" + name + ").servoMoveImmed");
        servoMoveImmedSM.once((state, context) -> setPosition(context.end));
        servoMoveImmedSM.repeat((state, context) -> {
            if (state.getTimeInState().seconds() > Math.abs(context.end - context.start))
                state.next();
        });

        servoMoveTopSpeedSM = new StateMachine<ServoMoveContext>("ServoEx(" + name + ").servoMoveTopSpeed");
        servoMoveTopSpeedSM.once((state, context) -> {
            if (context.end > context.start)
                setPosition(1.0);
            else
                setPosition(0.0);
        });
        servoMoveTopSpeedSM.repeat((state, context) -> {
            if (state.getTimeInState().seconds() > Math.abs(context.end - context.start)) {
                setPosition(context.end);
                state.next();
            }
        });
    }

    @Override
    public void init(HardwareMap hwMap, OpModeTime time) {
        super.init(hwMap, time);
        servo = hwMap.servo.get(name);
    }

    public void setPosition(double position) {
        this.position = position;
        servo.setPosition(position);
        isPositionSetThisLoop = true;
    }

    public double getPosition() {
        return position;
    }

    public boolean isPositionSet() {
        return position != null;
    }

    @Override
    public void log() {
        super.log();

        if (isPositionSetThisLoop) {
            JSONObject log = DataLogger.createJsonObject(this.getClass().getSimpleName(), name);
            DataLogger.putOpt(log, "position", "" + position);
            logger.log(log);
        }
    }

    @Override
    public void loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        isPositionSetThisLoop = false;
        super.loop(bulkDataResponse);
    }

    // Calculate the new position for a servo, going a certain distance towards, but not past a target
    private static double calcPos(double start, double target, double dist) {
        double signum = Math.signum(target - start);
        double pos = target;
        if (signum == -1.0)
            pos = Range.clip(start + signum * dist, target, start);
        else
            pos = Range.clip(start + signum * dist, start, target);
        return pos;
    }

    /**
     * Starts a state machine which will sweep the servo to the end position. The SM ends when the
     * servo position has been set to the target position. It may not have reached that position
     * yet if sweeping at high speed
     * @param end the target position
     * @param posPerSec speed at which to sweep (example posPerSec = 1.0 for 0.0 to 1.0 in one second)
     */
    public void moveTo(double end, double posPerSec) {
        runSM(servoMoveSM, new ServoMoveContext(getPosition(), end, posPerSec));
    }

    /**
     * Starts a state machine which will move the servo (without sweeping) to the end position.
     * The SM ends after an estimated time for the move to complete: # of seconds = abs(end - start).
     * Example: move from 0.25 to 0.5 is estimated to take 0.5 seconds
     * @param end
     */
    public void moveImmedTo(double end) {
        runSM(servoMoveImmedSM, new ServoMoveContext(getPosition(), end, -1));
    }

    /**
     * Starts a state machine which will move the servo to the end position as quickly as possible
     * by intentionally overshooting as far as possible in the desired direction. The SM ends after
     * an estimated time for the move to complete: # of seconds = abs(end - start).
     * Example: move from 0.25 to 0.5 is estimated to take 0.5 seconds. On completion, the state
     * machine will set position to the provided target (instead of the
     * overshoot value).
     *
     * One use of this state machine is where moving the servo as quickly as possible to physical
     * stops (where mechanically safe).
     *
     * @param end
     */
    public void moveTopSpeedTo(double end) {
        runSM(servoMoveTopSpeedSM, new ServoMoveContext(getPosition(), end, -1));
    }
}
