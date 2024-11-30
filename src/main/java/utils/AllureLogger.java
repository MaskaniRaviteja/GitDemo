package utils;
import io.qameta.allure.Step;

public class AllureLogger {

    @Step("Starting method: {methodName}")
    public static void logMethodStart(String methodName) {
        // This step logs the start of a method
    }

    @Step("Ending method: {methodName}")
    public static void logMethodEnd(String methodName) {
        // This step logs the end of a method
    }
}