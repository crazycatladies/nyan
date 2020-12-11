package ftc.crazycatladies.nyan.actuators;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.RobotLog;
import ftc.crazycatladies.nyan.sensors.TouchSensorEx;

import java.util.List;
import java.util.Map;

/**
 * Moves a motor to set positions defined in the DcMotorPosition enum
 * @param <P>
 */
public class MultiPositionMotor<P extends DcMotorPosition> extends DcMotorEx {
    P currentPosition;
    TouchSensorEx homeSensor;
    boolean isHomed = false;

    /**
     * @param name
     * @param hubNum
     * @param isForward
     */
    public MultiPositionMotor(String name, int hubNum, boolean isForward) {
        super(name, hubNum, isForward);
    }

    public MultiPositionMotor(String name, int hubNum, boolean isForward, TouchSensorEx homeSensor) {
        super(name, hubNum, isForward);
        this.homeSensor = homeSensor;
    }

    /**
     * @param position
     * @param timeout
     * @param power
     */
    public void moveTo(P position, double timeout, double power) {
        moveTo(position.getPosition(), timeout, power);
        currentPosition = position;
    }

    public P getCurrentPos() {
        return currentPosition;
    }

    @Override
    public void loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        super.loop(bulkDataResponse);
    }

    @Override
    public void initLoop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        super.initLoop(bulkDataResponse);

        if (homeSensor != null) {
            tryToHome();
        }
    }

    private void tryToHome() {
        if (!isHomed && isDone() && homeSensor.isPressed()) {
            setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            isHomed = true;
        }
    }

    public boolean isHomed() {
        return isHomed;
    }

    @Override
    public List<String> status() {
        List<String> status = super.status();
        if (homeSensor != null) {
            status.add(getDetailedName() + ":isHomed:" + isHomed);
        }
        return status;
    }
}
