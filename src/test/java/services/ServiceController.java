//package services;
//
//import org.sikuli.script.Key;
//import org.sikuli.script.Screen;
//import utils.Common;
//
//import java.awt.*;
//import java.awt.event.KeyEvent;
//import java.io.IOException;
//import java.util.Properties;
//
//public class ServiceController {
//
//    private Screen screen;
//    private Robot robot;
//
//    public ServiceController() throws AWTException {
//        this.screen = new Screen();
//        this.robot = new Robot();
//    }
//
//    public void restartIDAssigner(String serviceName) throws AWTException, InterruptedException, IOException {
//        openCMD();
//        JpsCommandReader jpsCommandReader = new JpsCommandReader();
//        jpsCommandReader.checkAndTerminateProcess(serviceName);
//        Thread.sleep(10000);
//        activateService(serviceName);
//    }
//
//    public void openCMD() throws AWTException, InterruptedException {
//        robot.keyPress(KeyEvent.VK_WINDOWS);
//        robot.keyPress(KeyEvent.VK_R);
//        robot.keyRelease(KeyEvent.VK_WINDOWS);
//        robot.keyRelease(KeyEvent.VK_R);
//        robot.delay(1000);
//        screen.type("cmd");
//        robot.keyPress(KeyEvent.VK_ENTER);
//        robot.keyRelease(KeyEvent.VK_ENTER);
//        Thread.sleep(500);
//        System.out.println("Opened CMD console");
//    }
//
//    public void activateService(String serviceName) throws IOException, InterruptedException, AWTException {
//        Properties properties = Common.readPropertyFile();
//        String drive = properties.getProperty("installationPath").split(":")[0];
//        String path = properties.getProperty("installationPath").split(":")[1];
//        Thread.sleep(1000);
//        screen.type(drive + ":" + Key.ENTER);
//        Thread.sleep(1000);
//        screen.type("cd " + path + Key.ENTER);
//        Thread.sleep(1000);
//        type(robot, "java -jar " + serviceName);
//        robot.delay(1000);
//        robot.keyPress(KeyEvent.VK_TAB);
//        robot.keyRelease(KeyEvent.VK_TAB);
//        robot.keyPress(KeyEvent.VK_TAB);
//        robot.keyRelease(KeyEvent.VK_TAB);
//        robot.keyPress(KeyEvent.VK_ENTER);
//        robot.keyRelease(KeyEvent.VK_ENTER);
//    }
//
//    public void type(Robot robot, String serviceName) {
//        for (char c : serviceName.toCharArray()) {
//            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
//            robot.keyPress(keyCode);
//            robot.keyRelease(keyCode);
//            System.out.println(keyCode);
//        }
//    }
//}

package services;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;
import utils.Common;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Properties;

public class ServiceController {

    private Screen screen;
    private Robot robot;

    public ServiceController() throws AWTException {
        this.screen = new Screen();
        this.robot = new Robot();
    }

    @Step("Restarting the IDAssigner service: {serviceName}")
    public void restartIDAssigner(String serviceName) throws AWTException, InterruptedException, IOException {
        openCMD();
        JpsCommandReader jpsCommandReader = new JpsCommandReader();
        jpsCommandReader.checkAndTerminateProcess(serviceName);
        Thread.sleep(10000);
        activateService(serviceName);
    }

    @Step("Opening CMD console")
    public void openCMD() throws AWTException, InterruptedException {
        robot.keyPress(KeyEvent.VK_WINDOWS);
        robot.keyPress(KeyEvent.VK_R);
        robot.keyRelease(KeyEvent.VK_WINDOWS);
        robot.keyRelease(KeyEvent.VK_R);
        robot.delay(1000);
        screen.type("cmd");
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(500);
        Allure.step("CMD console opened");
    }

    @Step("Activating service: {serviceName}")
    public void activateService(String serviceName) throws IOException, InterruptedException, AWTException {
        Properties properties = Common.readPropertyFile();
        String drive = properties.getProperty("installationPath").split(":")[0];
        String path = properties.getProperty("installationPath").split(":")[1];
        Thread.sleep(1000);
        screen.type(drive + ":" + Key.ENTER);
        Thread.sleep(1000);
        screen.type("cd " + path + Key.ENTER);
        Thread.sleep(1000);
        Allure.step("Typing command to start service: java -jar " + serviceName);

        type(robot, "java -jar " + serviceName);

        robot.delay(1000);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    @Step("Typing service name: {serviceName}")
    public void type(Robot robot, String serviceName) {
        for (char c : serviceName.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
//            Allure.step("Pressed key code: " + keyCode);
        }
    }
}
