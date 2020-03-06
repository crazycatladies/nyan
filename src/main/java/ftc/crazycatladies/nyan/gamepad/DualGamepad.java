package ftc.crazycatladies.nyan.gamepad;

public class DualGamepad extends GamepadEx {
    private final GamepadEx g1;
    private final GamepadEx g2;

    public DualGamepad(GamepadEx g1, GamepadEx g2) {
        this.g1 = g1;
        this.g2 = g2;
    }

    @Override
    public boolean a() {
        return g1.a() || g2.a();
    }

    @Override
    public boolean b() {
        return g1.b() || g2.b();
    }

    @Override
    public boolean x() {
        return g1.x() || g2.x();
    }

    @Override
    public boolean y() {
        return g1.y() || g2.y();
    }

    @Override
    public boolean dpad_down() {
        return g1.dpad_down() || g2.dpad_down();
    }

    @Override
    public boolean dpad_up() {
        return g1.dpad_up() || g2.dpad_up();
    }

    @Override
    public boolean dpad_left() {
        return g1.dpad_left() || g2.dpad_left();
    }

    @Override
    public boolean dpad_right() {
        return g1.dpad_right() || g2.dpad_right();
    }

    @Override
    public boolean left_bumper() {
        return g1.left_bumper() || g2.left_bumper();
    }

    @Override
    public boolean right_bumper() {
        return g1.right_bumper() || g2.right_bumper();
    }

    @Override
    public boolean left_stick_button() {
        return g1.left_stick_button() || g2.left_stick_button();
    }

    @Override
    public boolean right_stick_button() {
        return g1.right_stick_button() || g2.right_stick_button();
    }

    @Override
    public boolean start() {
        return g1.start() || g2.start();
    }
}
