package ftc.crazycatladies.nyan.sensors;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import ftc.crazycatladies.nyan.subsystem.Subsystem;
import ftc.crazycatladies.schrodinger.log.DataLogger;
import ftc.crazycatladies.schrodinger.opmode.OpModeTime;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ColorSensorEx extends Subsystem {
    ColorSensor colorSensor;
    protected Integer red;
    protected Integer green;
    protected Integer blue;
    protected Integer alpha;

    public ColorSensorEx(String name) {
        super(name);
    }

    @Override
    public void init(HardwareMap hwMap, OpModeTime time) {
        super.init(hwMap, time);
        colorSensor = hwMap.colorSensor.get(name);
    }

    @Override
    public void loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        super.loop(bulkDataResponse);
        red = null;
        green = null;
        blue = null;
        alpha = null;
    }

    @Override
    public void log() {
        super.log();
        JSONObject json = DataLogger.createJsonObject(this.getClass().getSimpleName(), name);
        DataLogger.putOpt(json, "red", red);
        DataLogger.putOpt(json, "green", green);
        DataLogger.putOpt(json, "blue", blue);
        DataLogger.putOpt(json, "alpha", alpha);
        if (json.length() > 2) {
            logger.log(json);
        }
    }

    public int red() {
        if (red == null)
            red = colorSensor.red();
        return red;
    }

    public int green() {
        if (green == null)
            green = colorSensor.green();
        return green;
    }

    public int blue() {
        if (blue == null)
            blue = colorSensor.blue();
        return blue;
    }

    public int alpha() {
        if (alpha == null)
            alpha = colorSensor.alpha();
        return alpha;
    }

    @Override
    public List<String> status() {
        List<String> status = new LinkedList<>();
        if (red != null || green != null || blue != null || alpha != null)
            status.add(this.getClass().getSimpleName() + ":"
                    + red + ":" + green + ":" + blue + ":" + alpha);
        return status;
    }
}
