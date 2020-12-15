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

        @Override
        public String toString() {
            return "ServoMoveContext{" +
                    "start=" + start +
                    ", end=" + end +
                    ", posPerSec=" + posPerSec +
                    '}';
        }
    }

    String name;
    protected Servo servo;
    Double position;
    boolean isPositionSetThisLoop;
    private final StateMachine<ServoMoveContext> servoMoveSM;
    private final StateMachine<ServoMoveContext> servoMoveImmedSM;

    public ServoEx(String name) {
        this.name = name;
        servoMoveSM = new StateMachine<ServoMoveContext>("ServoMove");
        servoMoveSM.repeat((state, smc) -> {
            double pos = calcPos(smc.start, smc.end, state.getTimeInState().seconds() * smc.posPerSec);
            setPosition(pos);
            if (pos == smc.end)
                state.next();
        });

        servoMoveImmedSM = new StateMachine<ServoMoveContext>("ServoMoveImmed");
        servoMoveImmedSM.once((state, context) -> setPosition(context.end));
        servoMoveImmedSM.repeat((state, context) -> {
            if (state.getTimeInState().seconds() > context.end - context.start)
                state.next();
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
//        DataLogger.addIfNotNull(log, );
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

    public void moveTo(double end, double posPerSec) {
        runSM(servoMoveSM, new ServoMoveContext(getPosition(), end, posPerSec));
    }

    public void moveImmedTo(double end) {
        runSM(servoMoveImmedSM, new ServoMoveContext(getPosition(), end, -1));
    }
}
