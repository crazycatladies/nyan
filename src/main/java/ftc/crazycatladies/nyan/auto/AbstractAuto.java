package ftc.crazycatladies.nyan.auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.json.JSONException;
import org.json.JSONObject;

import ftc.crazycatladies.nyan.gamepad.GamepadEx;
import ftc.crazycatladies.nyan.subsystem.Subsystem;
import ftc.crazycatladies.schrodinger.log.DataLogger;
import ftc.crazycatladies.schrodinger.opmode.OpModeTime;
import ftc.crazycatladies.schrodinger.state.StateMachine;

public abstract class AbstractAuto<R extends Subsystem> extends LinearOpMode {
    final ElapsedTime time = new ElapsedTime();
    protected GamepadEx g1;
    protected GamepadEx g2;
    protected R robot;

    void initialization() throws InterruptedException {
        telemetry.addData(">", "start init");
        telemetry.update();

        readConfig();
        DataLogger.createDataLogger(new OpModeTime(this), this.getClass().getSimpleName());

        robot.init(hardwareMap, new OpModeTime(this));

        g1 = new GamepadEx(gamepad1);
        g2 = new GamepadEx(gamepad2);

        autoInit();

        while (!isStopRequested() && !isStarted()) {
            configConsole();
            robot.initLoop(null);
            autoInitLoop();

            telemetry.addData("time", time.seconds());
            telemetry.update();
            idle();
        }
        waitForStart();
    }

    protected void configConsole() {
    }

    protected void readConfig() {
    }

    protected void autoInit() {}

    protected void autoInitLoop() {}

    protected void autoStop() {}

    @Override
    public void runOpMode() throws InterruptedException {
        initialization();
        robot.start();

        StateMachine<?> sm = stateMachine();

        while (opModeIsActive() && !sm.isDone()) {
            robot.loop(null);
            sm.run();
            robot.log();
            telemetry.addData("time", time.seconds());
            telemetry.addData("status", robot.status());
            telemetry.update();
        }
        autoStop();

        JSONObject json = new JSONObject();
        DataLogger.putOpt(json, "done", "true");
        DataLogger.getLogger().log(json);
        DataLogger.getLogger().stop();
    }

    protected abstract StateMachine<?> stateMachine();
}