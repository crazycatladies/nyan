package ftc.crazycatladies.nyan.sensors;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import ftc.crazycatladies.nyan.subsystem.Subsystem;
import ftc.crazycatladies.schrodinger.log.DataLogger;

public class AnalogSensorEx extends Subsystem {
    private int hubNum, portNum;
    private Integer analogInput;
    private Integer lastAnalogInput;
    private final PolynomialFunction toAnglePoly;

    public AnalogSensorEx(String name, int hubNum, int portNum) {
        super(name);
        this.hubNum = hubNum;
        this.portNum = portNum;
        toAnglePoly = new PolynomialFunction(new double[]{ 270, -.168967, .00004075, -4.31938E-09});
    }

    @Override
    public void initLoop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        _loop(bulkDataResponse);
    }

    @Override
    public void loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        _loop(bulkDataResponse);
    }

    private void _loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) {
        if (analogInput != null)
            lastAnalogInput = analogInput;
        if (bulkDataResponse != null) {
            LynxGetBulkInputDataResponse response = bulkDataResponse.get(hubNum);
            analogInput = response.getAnalogInput(portNum);
        }
    }

    @Override
    public void log() {
        if (lastAnalogInput == null || !lastAnalogInput.equals(analogInput)) {
            JSONObject json = DataLogger.createJsonObject(this.getClass().getSimpleName(), name);
            DataLogger.putOpt(json, "value", analogInput);
            logger.log(json);
        }
    }

    @Override
    public List<String> status() {
        List<String> status = super.status();
        status.add(getDetailedName() + ":" + analogInput + ":"
                + (analogInput == null ? null : getAngle()));
        return status;
    }

    public Integer getAnalogInput() {
        return analogInput;
    }

    public double getAngle() {
        return toAnglePoly.value(analogInput);
    }
}
