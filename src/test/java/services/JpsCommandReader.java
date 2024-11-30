package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class JpsCommandReader {

    Map<String, String> jpsResult = new HashMap<>();

    public void checkAndTerminateProcess(String processName) {
        try {
            // Execute the jps command and read its output
            ProcessBuilder processBuilder = new ProcessBuilder("jps", "-l");
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains the process name (case-insensitive)
                if (line.toLowerCase().contains(processName.toLowerCase())) {
                    // Extract the PID from the line (assuming PID is the first token)
                    String[] jpsResults = line.split("\\s+");
                    String pid = jpsResults[0];
                    processName = jpsResults[1];

                    System.out.println(pid + " " + processName);

                    // Check if the process is running in the background
                    if (isProcessRunningInBackground(pid)) {
                        // Terminate the process
                        System.out.println("Terminating process with PID: " + pid);
                        terminateProcess(pid);
                    } else {
                        System.out.println("Process with PID " + pid + " is not running in the background.");
                    }
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);

            // Close resources
            reader.close();
            inputStream.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to check if a process is running in the background
    private boolean isProcessRunningInBackground(String pid) {
        // Placeholder implementation: check actual process state here
        // For demonstration, always return true
        return true;
    }

    // Method to terminate a process by its PID
    private void terminateProcess(String pid) {
        String os = System.getProperty("os.name").toLowerCase();
        String killCommand;

        // Determine the appropriate kill command based on the operating system
        if (os.contains("win")) {
            killCommand = "taskkill /F /PID " + pid;
        } else {
            killCommand = "kill -9 " + pid;
        }

        try {
            // Execute the kill command
            Runtime.getRuntime().exec(killCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
