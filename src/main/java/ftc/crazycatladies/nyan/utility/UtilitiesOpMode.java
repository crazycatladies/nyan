package ftc.crazycatladies.nyan.utility;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

import ftc.crazycatladies.nyan.gamepad.GamepadEx;

import java.io.File;
import java.io.FilenameFilter;

@TeleOp(name="Utilities")
@Disabled
public class UtilitiesOpMode extends OpMode {
    protected GamepadEx g1;

    @Override
    public void init() {
        g1 = new GamepadEx(gamepad1);
    }

    @Override
    public void loop() {
        if (g1.b()) {
            File listFile = new File("sdcard", "listing.log");
            listFile.delete();
        } else if (g1.x()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File[] files = new File("sdcard").listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String s) {
                            return s.endsWith(".log");
                        }
                    });
                    for (File file : files) {
                        RobotLog.i("" + file);
                        file.delete();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
}
