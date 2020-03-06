package ftc.crazycatladies.nyan.sensors;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ftc.crazycatladies.nyan.subsystem.Subsystem;
import ftc.crazycatladies.schrodinger.opmode.OpModeTime;

public class RevTouchSensorEx extends Subsystem {
    String name;
    int hubNum, portNum;
    RevTouchSensor rts;
    Boolean isPressed;

    public RevTouchSensorEx(String name, int hubNum, int portNum) {
        this.name = name;
        this.hubNum = hubNum;
        this.portNum = portNum;
    }

    @Override
    public void init(HardwareMap hwMap, OpModeTime time) {
        super.init(hwMap, time);
        rts = hwMap.get(RevTouchSensor.class, name);
    }

    @Override
    public void loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) {
        isPressed = null;
        super.loop(bulkDataResponse);
        if (bulkDataResponse != null) {
            LynxGetBulkInputDataResponse bulkData = bulkDataResponse.get(hubNum);
            if (bulkData != null) {
                isPressed = !bulkData.getDigitalInput(portNum);
            }
        }
    }

    public boolean isPressed() {
        if (isPressed == null) {
            RobotLog.i("Getting touch sensor isPressed outside of bulk call");
            isPressed = rts.isPressed();
        }
        return isPressed;
    }

    @Override
    public List<String> status() {
        List<String> status = new LinkedList<>();
        status.add(this.getClass().getSimpleName() + ":" + isPressed());
        return status;
    }
}
