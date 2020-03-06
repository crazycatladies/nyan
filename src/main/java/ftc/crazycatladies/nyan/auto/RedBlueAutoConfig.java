package ftc.crazycatladies.nyan.auto;

import com.qualcomm.robotcore.util.RobotLog;

import ftc.crazycatladies.nyan.gamepad.GamepadEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class RedBlueAutoConfig {
    boolean isBlueSide = true;
    public final String configFile;
    
    public RedBlueAutoConfig(String configFile) {
		this.configFile = configFile;
	}

    void readConfig() {
        RobotLog.i("Read Config");
        try (Scanner sc = new Scanner(new File("sdcard", configFile))) {
            isBlueSide = sc.nextBoolean();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (RuntimeException r) {
            r.printStackTrace();
        }
    }

    void configConsole(Telemetry telemetry, GamepadEx g1, GamepadEx g2) {
        telemetry.addData("Side (A)", isBlueSide ? "Blue" : "Red");
        telemetry.addData(">", "Press Start to Save");

        if (g1.a() || g2.a()) {
            isBlueSide = !isBlueSide;
        } else if (g1.start() || g2.start()) {
            RobotLog.i("Saving config");
            try (FileWriter fw = new FileWriter(new File("sdcard", configFile))) {
                fw.write(isBlueSide + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
