package ftc.crazycatladies.nyan.actuators;

public class MultiPositionMotor<P extends DcMotorPosition> extends DcMotorEx {
    P currentPosition;

    public MultiPositionMotor(String name, int hubNum, boolean isForward) {
        super(name, hubNum, isForward);
    }

    public void moveTo(P position, double timeout, double power) {
        moveTo(position.getPosition(), timeout, power);
        currentPosition = position;
    }

    public P getCurrentPos() {
        return currentPosition;
    }
}
