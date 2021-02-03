package ftc.crazycatladies.nyan.composite;

import ftc.crazycatladies.nyan.actuators.MultiPositionServo;
import ftc.crazycatladies.schrodinger.state.StateMachine;

public class MultiPositionServoWithFeedback<P extends ServoPositionWithFeedback> extends MultiPositionServo<P> {

    private final StateMachine<ServoPositionWithFeedback> moveTopSpeedWithFeedbackSM;
    private final StateMachine<ServoPositionWithFeedback> moveImmedWithFeedbackSM;

    /**
     * @param name
     * @param startingPosition Position which servo will assume to be in to begin. Does not cause servo to move to this position during init or start
     */
    public MultiPositionServoWithFeedback(String name, P startingPosition) {
        super(name, startingPosition);

        moveTopSpeedWithFeedbackSM = new StateMachine<ServoPositionWithFeedback>("ServoEx(" + name + ").servoMoveTopSpeedWithFeedback");
        moveTopSpeedWithFeedbackSM.once((state, context) -> {
            if (context.getPosition() > getPosition())
                setPosition(1.0);
            else
                setPosition(0.0);
        });
        moveTopSpeedWithFeedbackSM.repeat((state, context) -> {
            if (context.getFeedbackDevice().isPressed())
                state.next();
        });

        moveImmedWithFeedbackSM = new StateMachine<ServoPositionWithFeedback>("ServoEx(" + name + ").servoMoveImmed");
        moveImmedWithFeedbackSM.once((state, context) -> setPosition(context.getPosition()));
        moveImmedWithFeedbackSM.repeat((state, context) -> {
            if (context.getFeedbackDevice().isPressed())
                state.next();
        });
    }

    @Override
    public void moveImmedTo(P position) {
        initStartingPos();
        moveImmedWithFeedbackTo(position);
        currentPosition = position;
    }

    private void moveImmedWithFeedbackTo(P position) {
        runSM(moveImmedWithFeedbackSM, position);
    }

    @Override
    public void moveTopSpeedTo(P position) {
        initStartingPos();
        moveTopSpeedWithFeedbackTo(position);
        currentPosition = position;
    }

    private void moveTopSpeedWithFeedbackTo(P position) {
        runSM(moveTopSpeedWithFeedbackSM, position);
    }
}
