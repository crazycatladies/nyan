package ftc.crazycatladies.nyan.sensors;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.RobotLog;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ftc.crazycatladies.nyan.subsystem.Subsystem;
import ftc.crazycatladies.schrodinger.log.DataLogger;
import ftc.crazycatladies.schrodinger.opmode.OpModeTime;
import ftc.crazycatladies.schrodinger.state.StateMachine;

public class TouchSensorEx extends Subsystem {
    int hubNum, portNum;
    TouchSensor rt;
    Boolean isPressed, lastIsPressed;
    Class hwMapClass;
    StateMachine<Integer> waitOnPressSM;

    public TouchSensorEx(String name, int hubNum, int portNum, Class hwMapClass) {
        super(name);
        this.hubNum = hubNum;
        this.portNum = portNum;
        this.hwMapClass = hwMapClass;

        waitOnPressSM = new StateMachine<>("TouchSensorEx(" + name + ").waitForPress");
        waitOnPressSM.repeat((state, context) -> {
            if (state.getTimeInState().milliseconds() > context || isPressed()) {
                state.next();
            }
        });
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
        if (isPressed != null) {
            lastIsPressed = isPressed;
        }
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

    public void waitOnPress(int timeoutMS) {
        runSM(waitOnPressSM, timeoutMS);
    }

    public String getDetailedName() {
        return this.getClass().getSimpleName() + ":" + name;
    }

    @Override
    public List<String> status() {
        List<String> status = new LinkedList<>();
        status.add(getDetailedName() + ":" + isPressed());
        return status;
    }

    @Override
    public void log() {
        super.log();
        JSONObject json = DataLogger.createJsonObject(this.getClass().getSimpleName(), name);
        if (isPressed != null) {
            if (!isPressed.equals(lastIsPressed)) {
                DataLogger.putOpt(json, "pressed", isPressed);
                logger.log(json);
            }
        }
    }
}
