package ftc.crazycatladies.nyan.actuators;

public class MultiPositionServo<P extends ServoPosition> extends ServoEx {
    protected final P startingPosition;
    protected P currentPosition;

    /**
     * @param name
     * @param startingPosition Position which servo will assume to be in to begin. Does not cause servo to move to this position during init or start
     */
    public MultiPositionServo(String name, P startingPosition) {
        super(name);
        this.startingPosition = startingPosition;
    }

    public void moveTo(P position, double posPerSec) {
        initStartingPos();
        moveTo(position.getPosition(), posPerSec);
        currentPosition = position;
    }

    protected void initStartingPos() {
        if (!isPositionSet())
            setPosition(startingPosition.getPosition());
    }

    protected void initStartingPosWithoutEffect() {
        if (!isPositionSet())
            setPositionWithoutEffect(startingPosition.getPosition());
    }

    public void moveImmedTo(P position) {
        initStartingPosWithoutEffect();
        moveImmedTo(position.getPosition());
        currentPosition = position;
    }

    public void moveTopSpeedTo(P position) {
        initStartingPos();
        moveTopSpeedTo(position.getPosition());
        currentPosition = position;
    }

    public void setPosition(P position) {
        setPosition(position.getPosition());
        currentPosition = position;
    }

    public void setPositionWithoutEffect(P position) {
        setPositionWithoutEffect(position.getPosition());
        currentPosition = position;
    }

    public P getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public String toString() {
        return "MultiPositionServo{" +
                "currentPosition=" + currentPosition +
                ", name='" + name + '\'' +
                ", position=" + position +
                '}';
    }
}
