package ftc.crazycatladies.nyan.actuators;

/**
 * Moves a motor to set positions defined in the DcMotorPosition enum
 * @param <P>
 */
public class MultiPositionMotor<P extends DcMotorPosition> extends DcMotorEx {
    P currentPosition;

    /**
     * @param name
     * @param hubNum
     * @param isForward
     */
    public MultiPositionMotor(String name, int hubNum, boolean isForward) {
        super(name, hubNum, isForward);
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
}
