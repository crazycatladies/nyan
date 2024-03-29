package ftc.crazycatladies.nyan.subsystem;

import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ftc.crazycatladies.schrodinger.log.DataLogger;
import ftc.crazycatladies.schrodinger.opmode.OpModeTime;
import ftc.crazycatladies.schrodinger.state.State;
import ftc.crazycatladies.schrodinger.state.StateMachine;
import ftc.crazycatladies.schrodinger.state.StateFunction;

/**
 * Abstract base class for subsystems following the composite design pattern. Has methods like
 * {@link #init(HardwareMap, OpModeTime)}, {@link #start()}, {@link #stop()}, etc. which are
 * intended to be invoked during the execution of these same methods in the OpMode. Methods like
 * these cascade down to child subsystems automatically in the provided implementation of the methods.
 */
public abstract class Subsystem {
    protected DataLogger logger;
    protected List<Subsystem> subsystems = new LinkedList<>();
    protected StateMachine<?> currentSM;
    protected StateMachine<Subsystem> waitSM;
    protected String name;

    public Subsystem(String name) {
        waitSM = new StateMachine<>(this.getClass().getSimpleName() + "WaitSM");
        waitSM.repeat((state, context) -> {
            if (context.isDone()) {
                state.next();
            }
        });
        this.name = name;
    }

    public void addSubsystems(Subsystem ... children) {
        for (Subsystem s : children)
            subsystems.add(s);
    }

    /**
     * Initializes the subsystem (and its children) with information from the OpMode and setup the logger.
     * Intended to be called during OpMode.init. Override to implement actual init activities,
     * but in most cases preserve logging setup and base class cascading to children using super.init(hwMap, time)
     * @param hwMap hardware map from the OpMode
     * @param time time from the OpMode
     */
    public void init(HardwareMap hwMap, OpModeTime time) {
        this.logger = DataLogger.getLogger();

        for (Subsystem s : subsystems)
            s.init(hwMap, time);
    }

    public void initLoop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        for (Subsystem s : subsystems)
            s.initLoop(bulkDataResponse);
        runCurrentSM();
    }

    /**
     * Start the subsystem (and its children). Intended to be called during OpMode.start.
     * Override to implement actual start activities, but in most cases preserve base class
     * cascading to children using super.start()
     */
    public void start() {
        for (Subsystem s : subsystems)
            s.start();
    }

    /**
     * Perform OpMode loop activities. Intended to be called during OpMode.loop or inside a
     * while loop in a LinearOpMode. Override to implement additional loop activities, but in
     * most cases preserve base class cascading to children and state machine execution
     * using super.loop(bulkDataResponse).
     * This method also invokes the subsystem's current state machine if one is set and not yet done.
     * @param bulkDataResponse contains bulk data from expansion/control hub for increased performance.
     */
    public void loop(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
        loopChildren(bulkDataResponse);
        runCurrentSM();
    }

    protected void loopChildren(Map<Integer, LynxGetBulkInputDataResponse> bulkDataResponse) throws InterruptedException {
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

    /**
     * Peform logging for the subsystem (and its children) - none in default implementation.
     * Intended to be called during OpMode.loop or inside a while loop in a LinearOpMode.
     * Override to actually implement log activities, but in most cases also preserve base class
     * cascading to children using super.start()
     */
    public void log() {
        for (Subsystem s : subsystems)
            s.log();
    }

    /**
     * Stop the subsystem (and its children). Intended to be called during OpMode.stop.
     * Override to implement actual stop activities, but in most cases preserve base class
     * cascading to children using super.stop()
     */
    public void stop() {
        for (Subsystem s : subsystems)
            s.stop();
    }

    protected <C> void runSM(StateMachine<C> sm, C context) {
        currentSM = sm.init(context);
    }

    /**
     * @return true if there is not a current state machine or if the current state machine is done
     */
    public boolean isDone() {
        return currentSM == null || currentSM.isDone();
    }

    /**
     * Returns current statuses which can be used in telemetry, for example
     * @return a list of status strings from this subsystem (none by default) and children
     */
    public List<String> status() {
        List<String> status = new LinkedList<>();
        for (Subsystem s : subsystems) {
            List<String> sStatus = s.status();
            if (sStatus != null)
                status.addAll(sStatus);
        }
        return status;
    }

    public StateMachine<?> getCurrentStateMachine() {
        return currentSM;
    }

    // Allows a different state machine to know when this one is done
    public <T> StateFunction<T> waitFor() {
        return (state, context) -> {
            if (isDone())
                state.next();
        };
    }

    public <T> StateFunction<T> waitFor(State<T> ... states) {
        return (state, context) -> {
            for (State<T> s : states)
                if (s.equals(currentSM.getCurrentState()))
                    state.next();
        };
    }

    public void waitOn(Subsystem subsystem) {
        runSM(waitSM, subsystem);
    }

    public String getDetailedName() {
        return this.getClass().getSimpleName() + ":" + name;
    }

    public static boolean isExternalInput(Set<ExternalInput> set, ExternalInput ei) {
        boolean found = set.contains(ei);
        set.clear();
        return found;
    }

    public ExternalInput getExternalInput(Set<ExternalInput> set, ExternalInput... filter) {
        for (ExternalInput f : filter) {
            if (set.contains(f)) {
                set.clear();
                return f;
            }
        }
        set.clear();
        return null;
    }

    public State getCurrentState() {
        return getCurrentStateMachine().getCurrentState();
    }
}
