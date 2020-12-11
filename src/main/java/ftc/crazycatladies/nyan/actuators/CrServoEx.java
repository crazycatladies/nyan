package ftc.crazycatladies.nyan.actuators;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import ftc.crazycatladies.nyan.subsystem.Subsystem;
import ftc.crazycatladies.schrodinger.log.DataLogger;
import ftc.crazycatladies.schrodinger.opmode.OpModeTime;
import org.json.JSONObject;

import java.util.Map;

public class CrServoEx extends Subsystem {
    String name;
    protected CRServo servo;
    Double power;
    boolean isPowerSetThisLoop;
    private DcMotorSimple.Direction direction;

    public CrServoEx(String name) {
        this.name = name;
    }

    public CrServoEx(String name, DcMotorSimple.Direction direction) {
        this.name = name;
        this.direction = direction;
    }

    @Override
    public void init(HardwareMap hwMap, OpModeTime time) {
        super.init(hwMap, time);
        servo = hwMap.crservo.get(name);
        servo.setDirection(direction);
    }

    public void setPower(double power) {
        this.power = power;
        servo.setPower(power);
        isPowerSetThisLoop = true;
    }

    public double getPower() {
        return power;
    }

    public boolean isPowerSet() {
        return power != null;
    }

    @Override
    public void log() {
        super.log();

        if (isPowerSetThisLoop) {
            JSONObject log = DataLogger.createJsonObject(this.getClass().getSimpleName(), name);
            DataLogger.putOpt(log, "power", "" + power);
            logger.log(log);
        }
    }

    @Override
    public void loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        isPowerSetThisLoop = false;
        super.loop(bulkDataResponse);
    }
}
