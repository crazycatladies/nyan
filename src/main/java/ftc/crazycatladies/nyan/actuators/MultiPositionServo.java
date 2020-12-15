package ftc.crazycatladies.nyan.actuators;

public class MultiPositionServo<P extends ServoPosition> extends ServoEx {
    private final P startingPosition;
    P currentPosition;

    /**
     * @param name
     * @param startingPosition Position which servo will assume to be in to begin. Does not cause servo to move to this position during init or start
     */
    public MultiPositionServo(String name, P startingPosition) {
        super(name);
        this.startingPosition = startingPosition;
    }

    public void moveTo(P position, double posPerSec) {
        if (!isPositionSet())
            setPosition(startingPosition.getPosition());

        moveTo(position.getPosition(), posPerSec);
        currentPosition = position;
    }

    public void moveImmedTo(P position) {
        if (!isPositionSet())
            setPosition(startingPosition.getPosition());

        moveImmedTo(position.getPosition());
        currentPosition = position;
    }

    public void setPosition(P position) {
        setPosition(position.getPosition());
        currentPosition = position;
    }

    public P getCurrentPosition() {
        return currentPosition;
    }
}
