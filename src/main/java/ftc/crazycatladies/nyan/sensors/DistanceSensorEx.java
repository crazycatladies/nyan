package ftc.crazycatladies.nyan.sensors;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import ftc.crazycatladies.nyan.subsystem.Subsystem;
import ftc.crazycatladies.schrodinger.log.DataLogger;
import ftc.crazycatladies.schrodinger.opmode.OpModeTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DistanceSensorEx extends Subsystem {
    String name;
    DistanceSensor distanceSensor;
    private Double distance;

    public DistanceSensorEx(String name) {
        this.name = name;
    }

    @Override
    public void init(HardwareMap hwMap, OpModeTime time) {
        super.init(hwMap, time);
        distanceSensor = hwMap.get(DistanceSensor.class, name);
    }

    public double getDistance() {
        if (distance == null) {
            distance = distanceSensor.getDistance(DistanceUnit.CM);
        }
        return distance;
    }

    @Override
    public void loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        super.loop(bulkDataResponse);
        distance = null;
    }

    @Override
    public void log() {
        super.log();
        JSONObject json = DataLogger.createJsonObject(this.getClass().getSimpleName(), null);
        DataLogger.putOpt(json, "distance", distance);
        if (json.length() >= 2) {
            logger.log(json);
        }
    }

    @Override
    public List<String> status() {
        List<String> status = new LinkedList<>();
        status.add(this.getClass().getSimpleName() + ":" + getDistance());
        return status;
    }
}
