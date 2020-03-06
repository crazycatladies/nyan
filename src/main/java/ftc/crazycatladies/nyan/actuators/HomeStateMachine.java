package ftc.crazycatladies.nyan.actuators;

import com.qualcomm.robotcore.hardware.DcMotor;

import ftc.crazycatladies.nyan.sensors.RevTouchSensorEx;
import ftc.crazycatladies.schrodinger.state.StateMachine;

public class HomeStateMachine extends StateMachine<Object> {
    public HomeStateMachine(String name, final DcMotorEx motor, final RevTouchSensorEx homeSwitch, final boolean reverse, double power) {
        super(name);

        once((state, context) -> {
            // If homed already, raise gently (lower if reversed)
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            if (homeSwitch.isPressed())
                motor.setPower(power * (reverse ? -1 : 1));
        }).repeat((state, context) -> {
            // Wait until not at home, then lower to home (raise if reversed)
            if (!homeSwitch.isPressed()) {
                motor.setPower(-power * (reverse ? -1 : 1));
                state.transition();
            }
        }).repeat((state, context) -> {
            // Once home switch is activated, stop and reset encoder
            if (homeSwitch.isPressed()) {
                motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                state.transition();
            }
        });
    }
}