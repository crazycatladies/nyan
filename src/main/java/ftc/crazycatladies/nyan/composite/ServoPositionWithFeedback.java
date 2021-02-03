package ftc.crazycatladies.nyan.composite;

import com.qualcomm.robotcore.hardware.TouchSensor;

import ftc.crazycatladies.nyan.actuators.ServoPosition;

public interface ServoPositionWithFeedback extends ServoPosition {
    TouchSensor getFeedbackDevice();
}
