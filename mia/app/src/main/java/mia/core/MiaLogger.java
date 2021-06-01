package mia.core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/***
 * This class provides a logger which can log to console and file
 */
public class MiaLogger implements IMiaShutdownable{

    private FileOutputStream logWriter = null;

    private LocalDate lastLogCall;

    private String ANSI_RESET = "\u001B[0m";
    private String ANSI_RED = "\u001B[31m";
    private String ANSI_GREEN = "\u001B[32m";
    private String ANSI_YELLOW = "\u001B[33m";

    private String logPath = System.getProperty("user.dir") + "/logs";

    public MiaLogger(){
            createLogFile();
            lastLogCall = LocalDate.now();
            logInfo("MIA-Logger initialized...");
    }

    /***
     * Creates a new logfile for the current date named "miaYEAR_MONTH_DAY".
     */
    private void createLogFile(){
        try {
            new File(logPath).mkdir();
            String logFileName = "mia"+getDatestamp();
            File logFile = new File(logPath + "/" + logFileName + ".log");
            logFile.createNewFile();
            logWriter = new FileOutputStream(logPath + "/" + logFileName + ".log", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Checks if a logfile exists via 'checkTime()' and updates the lastLogCall Timestamp. After that the passed message will be written to the logfile.
     * @param msg the message to write to the logfile
     */
    private void log(String msg){
        checkTime();
        lastLogCall = LocalDate.now();
        try {
            logWriter.write(msg.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Checks if the last log call is from yesterday. If it is, a new logfile is created for the current day.
     */
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

    /***
     * Logs a warning message to the logfile and to the console.
     * @param msg the warning message to log
     */
    public void logWarning(String msg){
        String logMsg = getTimestamp() + "WARNING: \t" + msg + "\n";
        System.out.print(ANSI_YELLOW + logMsg + ANSI_RESET);
        log(logMsg);
    }

    /***
     * Logs a info message to the logfile and to the console.
     * @param msg the info message to log
     */
    public void logInfo(String msg){
        String logMsg = getTimestamp() + "INFO: \t" + msg + "\n";
        System.out.print(ANSI_GREEN + logMsg + ANSI_RESET);
        log(logMsg);
    }
    /***
     * Logs a error message to the logfile and to the console. If 'shouldShutdown' is true, the system will shutdown
     * @param msg the error message to log
     * @param shouldShutdown if the error should initiate a shutdown of the system
     */
    public void logError(String msg, boolean shouldShutdown){
        String logMsg = getTimestamp() + "ERROR: \t" + msg + "\n";
        System.out.print(ANSI_RED + logMsg + ANSI_RESET);
        log(logMsg);
        if(shouldShutdown) Mia.getShutdownManager().shutdownSystem();
    }

    /***
     * Get a timestamp for the current LocalTime
     * @return timestamp as String [HH:mm:ss]
     */
    private String getTimestamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timestamp = LocalTime.now().format(dtf);
        return "[" +timestamp+"]";
    }

    /***
     * Get a datestamp for the current LocalDate
     * @return timestamp as String uuuu_MM_dd
     */
    private String getDatestamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd");
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
