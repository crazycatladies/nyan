package ftc.crazycatladies.nyan.sensors;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ftc.crazycatladies.nyan.subsystem.Subsystem;
import ftc.crazycatladies.schrodinger.opmode.OpModeTime;

public class TouchSensorEx extends Subsystem {
    String name;
    int hubNum, portNum;
    TouchSensor rt;
    Boolean isPressed;
    Class hwMapClass;

    public TouchSensorEx(String name, int hubNum, int portNum, Class hwMapClass) {
        this.name = name;
        this.hubNum = hubNum;
        this.portNum = portNum;
        this.hwMapClass = hwMapClass;
    }

    @Override
    public void init(HardwareMap hwMap, OpModeTime time) {
        super.init(hwMap, time);
        rt = (TouchSensor) hwMap.get(hwMapClass, name);
    }

    @Override
    public void loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        super.loop(bulkDataResponse);
        _loop(bulkDataResponse);
    }

    @Override
    public void initLoop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        super.initLoop(bulkDataResponse);
        _loop(bulkDataResponse);
    }

    private void _loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) {
        isPressed = null;
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
            isPressed = rt.isPressed();
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
