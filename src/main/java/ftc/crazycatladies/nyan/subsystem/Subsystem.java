package ftc.crazycatladies.nyan.subsystem;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ftc.crazycatladies.schrodinger.log.DataLogger;
import ftc.crazycatladies.schrodinger.opmode.OpModeTime;
import ftc.crazycatladies.schrodinger.state.StateMachine;

public abstract class Subsystem {
//    protected OpModeTime time;
    protected DataLogger logger;
    protected List<Subsystem> subsystems = new LinkedList<>();
    protected StateMachine<?> currentSM;

    public void init(HardwareMap hwMap, OpModeTime time) {
//        this.time = time;
        this.logger = DataLogger.getLogger();

        for (Subsystem s : subsystems)
            s.init(hwMap, time);
    }

    public void start() {
        for (Subsystem s : subsystems)
            s.start();
    }

    public void loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) {
        loopChildren(bulkDataResponse);
        runCurrentSM();
    }

    protected void loopChildren(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) {
        for (Subsystem s : subsystems)
            s.loop(bulkDataResponse);
    }

    protected void runCurrentSM() {
        if (currentSM != null) {
            if (currentSM.isDone()) {
                currentSM.run();
                currentSM = null;
            } else {
                currentSM.run();
            }
        }
    }

    public void log() {
        for (Subsystem s : subsystems)
            s.log();
    }

    public void stop() {
        for (Subsystem s : subsystems)
            s.stop();
    }

    protected <C> void runSM(StateMachine<C> sm, C context) {
        currentSM = sm.init(context);
    }

    public boolean isDone() {
        return currentSM == null || currentSM.isDone();
    }

    public List<String> status() {
        List<String> status = new LinkedList<>();
        for (Subsystem s : subsystems) {
            List<String> sStatus = s.status();
            if (sStatus != null)
                status.addAll(sStatus);
        }
        return status;
    }
}
