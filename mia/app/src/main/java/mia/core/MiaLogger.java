package mia.core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MiaLogger implements IMiaShutdownable{

    private FileOutputStream logWriter = null;

    private LocalDate lastLogCall;

    private boolean defaultShouldPrintOut = true;

    private String ANSI_RESET = "\u001B[0m";
    private String ANSI_RED = "\u001B[31m";
    private String ANSI_GREEN = "\u001B[32m";
    private String ANSI_YELLOW = "\u001B[33m";


    public MiaLogger(){
            createLogFile();
            lastLogCall = LocalDate.now();
            logInfo("MIA-Logger initialized...");
    }

    private void createLogFile(){
        try {
            new File("../logs").mkdir();
            String logFileName = "mia"+getDatestamp();
            File logFile = new File("../logs/" + logFileName + ".log");
            logFile.createNewFile();
            logWriter = new FileOutputStream("../logs/" + logFileName + ".log", true);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String msg){
        checkTime();
        lastLogCall = LocalDate.now();
        try {
            logWriter.write(msg.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkTime(){
        LocalDate now = LocalDate.now();
        if(now.isAfter(lastLogCall) && (lastLogCall.getDayOfMonth() != now.getDayOfMonth())){
            try {
                logWriter.write("---Logging continues on the next day---".getBytes(StandardCharsets.UTF_8));
                logWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            createLogFile();
        }
    }

    public void logWarning(String msg){
        logWarning(msg, defaultShouldPrintOut);
    }

    public void logWarning(String msg, boolean printToConsole){
        String logMsg = getTimestamp() + "WARNING: \t" + msg + "\n";
        if(printToConsole)
            System.out.print(ANSI_YELLOW + logMsg + ANSI_RESET);
        log(logMsg);
    }

    public void logInfo(String msg){
        logInfo(msg, defaultShouldPrintOut);
    }

    public void logInfo(String msg, boolean printToConsole){
        String logMsg = getTimestamp() + "INFO: \t" + msg + "\n";
        if(printToConsole)
            System.out.print(ANSI_GREEN + logMsg + ANSI_RESET);
        log(logMsg);
    }

    public void logError(String msg){
        logError(msg, defaultShouldPrintOut);
    }

    public void logError(String msg, boolean printToConsole){
        String logMsg = getTimestamp() + "ERROR: \t" + msg + "\n";
        if(printToConsole)
            System.out.print(ANSI_RED + logMsg + ANSI_RESET);
        log(logMsg);
    }

    private String getTimestamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timestamp = LocalTime.now().format(dtf);
        return "[" +timestamp+"]";
    }

    private String getDatestamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMdd");
        String dateStamp = LocalDate.now().format(dtf);
        return dateStamp;
    }

    public void shutdown(){
        try {
            logInfo("Shutting down....");
            log("=============================================\n");
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
