package ftc.crazycatladies.nyan.gamepad;

import com.qualcomm.robotcore.hardware.Gamepad;

// Prevents single button presses from being treated as double (or triple) presses
public class GamepadEx {
    private final Gamepad gamepad;
    long lastPressTime;
    String lastPressButton;

    public GamepadEx(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    protected GamepadEx() {
        gamepad = null;
    }

    public boolean a() { return evaluate(gamepad.a, "a"); }
    public boolean b() { return evaluate(gamepad.b, "b"); }
    public boolean x() { return evaluate(gamepad.x, "x"); }
    public boolean y() { return evaluate(gamepad.y, "y"); }
    public boolean dpad_down() { return evaluate(gamepad.dpad_down, "dpad_down"); }
    public boolean dpad_up() { return evaluate(gamepad.dpad_up, "dpad_up"); }
    public boolean dpad_left() { return evaluate(gamepad.dpad_left, "dpad_left"); }
    public boolean dpad_right() { return evaluate(gamepad.dpad_right, "dpad_right"); }
    public boolean left_bumper() { return evaluate(gamepad.left_bumper, "left_bumper"); }
    public boolean right_bumper() { return evaluate(gamepad.right_bumper, "right_bumper"); }
    public boolean left_stick_button() { return evaluate(gamepad.left_stick_button, "left_stick_button"); }
    public boolean right_stick_button() { return evaluate(gamepad.right_stick_button, "right_stick_button"); }
    public boolean start() { return evaluate(gamepad.start, "start"); }

    private boolean evaluate(boolean pressed, String buttonName) {
        // Wasn't pressed anyway
        if (!pressed) {
            // If this was the last pressed, clear out
            if (buttonName.equals(lastPressButton)) {
                lastPressButton = null;
                lastPressTime = 0;
            }

            return false;
        }

        long t = System.currentTimeMillis();
        if (buttonName.equals(lastPressButton) && t - lastPressTime < 300) {
            // Too soon
            return false;
        }

//        RobotLog.i("& " + pressed + "," + buttonName + "," + lastPressTime + "," + lastPressButton);

        lastPressTime = t;
        lastPressButton = buttonName;
        return true;
    }
}
