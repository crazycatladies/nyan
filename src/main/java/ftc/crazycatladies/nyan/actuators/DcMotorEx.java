package ftc.crazycatladies.nyan.actuators;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.RobotLog;

import ftc.crazycatladies.nyan.subsystem.Subsystem;
import ftc.crazycatladies.schrodinger.log.DataLogger;
import ftc.crazycatladies.schrodinger.opmode.OpModeTime;
import ftc.crazycatladies.schrodinger.state.StateMachine;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// Class maintains information each cycle to reduce time spent talking to hub
public class DcMotorEx extends Subsystem {

    private static class MoveContext {
        private final int position;
        private final double timeout;
        private final double power;

        private MoveContext(int position, double timeout, double power) {
            this.position = position;
            this.timeout = timeout;
            this.power = power;
        }
    }

    private String name;
    private int hubNum;
    private boolean isForward;
    private com.qualcomm.robotcore.hardware.DcMotorEx motor;
    private Integer targetPosition;
    private Integer lastTargetPosition;
    private Integer currentPosition;
    private Integer lastPosition;
    private Double power;
    private Double lastLoggedPower;
    private Double lastPower;
    private Boolean isBusy;
    private Boolean lastIsBusy;
    private DcMotor.RunMode mode;


    public DcMotorEx(String name, int hubNum, boolean isForward) {
        this.name = name;
        this.hubNum = hubNum;
        this.lastPower = 0.0;
        this.isForward = isForward;
    }

    @Override
    public void init(HardwareMap hwMap, OpModeTime time) {
        super.init(hwMap, time);
        motor = (com.qualcomm.robotcore.hardware.DcMotorEx) hwMap.dcMotor.get(name);
        motor.setDirection(isForward ? DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) {
        if (power != null)
            lastPower = power;

        lastPosition = currentPosition;
        lastTargetPosition = targetPosition;
        lastIsBusy = isBusy;
        lastLoggedPower = power;

        currentPosition = null;
        power = null;
        isBusy = null;

        if (bulkDataResponse != null) {
            LynxGetBulkInputDataResponse bulkData = bulkDataResponse.get(hubNum);
            if (bulkData != null) {
                int portNumber = motor.getPortNumber();
                currentPosition = bulkData.getEncoder(portNumber);
                isBusy = !bulkData.isAtTarget(portNumber);
            }
        }

        super.loop(bulkDataResponse);
    }

    @Override
    public void log() {
        super.log();

        JSONObject json = DataLogger.createJsonObject(this.getClass().getSimpleName(), name);

        if (currentPosition != null && !currentPosition.equals(lastPosition))
            DataLogger.putOpt(json, "currentPosition", currentPosition);

        if (targetPosition != null && !targetPosition.equals(lastTargetPosition))
            DataLogger.putOpt(json, "targetPosition", targetPosition);

        if (power != null && !power.equals(lastLoggedPower))
            DataLogger.putOpt(json, "power", power);

        if (isBusy != null && !isBusy.equals(lastIsBusy))
            DataLogger.putOpt(json, "isBusy", isBusy);

        if (json.length() > 2) {
            logger.log(json);
        }
    }

    public void setDirection(DcMotor.Direction direction) {
        motor.setDirection(direction);
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zpb) {
        motor.setZeroPowerBehavior(zpb);
    }

    public void setMode(DcMotor.RunMode mode) {
        if (!mode.equals(this.mode)) {
            motor.setMode(this.mode = mode);
        }
    }

    public int getCurrentPosition() {
        if (currentPosition == null) {
            RobotLog.i("Getting motor armPosition outside of bulk call");
            currentPosition = motor.getCurrentPosition();
        }

        return currentPosition;
    }

    public int getAdjCurrentPosition() {
        return getCurrentPosition() * (isForward ? 1 : -1);
    }

    public boolean isTargetAscending() {
        return targetPosition > getAdjCurrentPosition();
    }

    public int getLastPosition() {
        if (lastPosition == null)
            return getCurrentPosition();
        else
            return lastPosition;
    }

    public void setPower(double power, double adjustment) {
//        RobotLog.i("p" + power + ",a" + adjustment + ",ta" + isTargetAscending());
        setPower(power + adjustment * (isTargetAscending() ? 1 : -1));
    }

    public void setPower(double power) {
        if (this.power != null) {
            RobotLog.e("Setting power twice - probable issue");
            if (this.power == power)
                return;
            else
                motor.setPower(power);
        }

        this.power = power;

        if (this.lastPower != power)
            motor.setPower(power);
    }

    public void setTargetPosition(int position) {
        this.targetPosition = position;
        motor.setTargetPosition(position);
    }

    public boolean isBusy() {
        if (isBusy == null) {
            RobotLog.e("Getting motor isBusy outside of bulk call");
            isBusy = motor.isBusy();
        }

        return isBusy;
    }

    public int getPosChange() {
        return getCurrentPosition() - getLastPosition();
    }

    protected StateMachine<MoveContext> moveToSM =
        new StateMachine<MoveContext>("MotorMoveTo").once((state, mc) -> {
            setTargetPosition(mc.position);
            setMode(DcMotor.RunMode.RUN_TO_POSITION);
            setPower(mc.power);
        }).repeat((state, mc) -> {
            if (!isBusy() || state.getTimeInState().seconds() > mc.timeout) {
                state.transition();
            }
        });

    public void moveTo(int position, double timeout, double power) {
        runSM(moveToSM, new MoveContext(position, timeout, power));
    }

    public void moveBy(int position, double timeout, double power) {
        runSM(moveToSM, new MoveContext(-getAdjCurrentPosition() + position, timeout, power));
    }

    public void setPIDFCoefficients(DcMotor.RunMode runMode, PIDFCoefficients pidfCoefficients) {
        motor.setPIDFCoefficients(runMode, pidfCoefficients);
    }

    @Override
    public List<String> status() {
        List<String> status = new LinkedList<>();
        status.add(this.getClass().getSimpleName() + ":" + getCurrentPosition());
        return status;
    }
}
