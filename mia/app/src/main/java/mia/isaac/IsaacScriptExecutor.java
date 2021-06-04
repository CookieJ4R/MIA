package mia.isaac;

import mia.core.IMiaShutdownable;
import mia.core.Mia;
import mia.util.Season;
import mia.util.SeasonHelper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/***
 * This class is responsible for executing IsaacScripts
 */
public class IsaacScriptExecutor implements IMiaShutdownable {

    private boolean active_condition = false;
    private int condition_depth_tracker = 0;

    private volatile Timer delayTimer;
    private volatile boolean shouldDelay = false;

    private Stack<Boolean> condition_stack = new Stack<>();

    private volatile boolean shutdownInitiated = false;

    /***
     * Runs a script in a new Thread. The thread will check regularly if a system shutdown was initiated and will exit accordingly.
     * @param script the script to execute
     */
    public void runScript(IsaacScript script){
        Thread t = new Thread(() -> {
            Mia.getLogger().logInfo("ISAAC: Starting to execute script: " + script.getScriptCallID());
            Iterator<String> scriptIterator = Arrays.stream(script.getScriptLines()).iterator();
            while (scriptIterator.hasNext()){
                if(shutdownInitiated) return;
                while(shouldDelay){if (shutdownInitiated) return; }
                try {
                    executeInstruction(scriptIterator.next());
                } catch (IsaacScriptSyntaxException e) {
                    e.printStackTrace();
                }

            }
            Mia.getLogger().logInfo("ISAAC: Finished executing script");
        });
        t.start();
    }

    /***
     * Will execute the passed instruction (a line from an IsaacScript)
     * @param instruction the instruction to execute
     * @throws IsaacScriptSyntaxException
     */
    private void executeInstruction(String instruction) throws IsaacScriptSyntaxException {
        String[] instructionParts = instruction.split(" ");
        for(int i = 0; i < instructionParts.length; i++){
            instructionParts[i] = instructionParts[i].replace("##", " ");
        }
        switch (instructionParts[0]) {
            case "action" -> {
                if (active_condition && !condition_stack.peek())
                    break;
                handleAction(instructionParts);
            }
            case "is" -> {
                if (active_condition && !condition_stack.peek())
                    break;
                active_condition = true;
                condition_depth_tracker++;
                handleCondition(instructionParts);
            }
            case "si" -> {
                condition_depth_tracker--;
                condition_stack.pop();
                if (condition_depth_tracker == 0) active_condition = false;
            }
            case "delay" -> handleDelay(instructionParts[1]);
            default -> throw new IsaacScriptSyntaxException();
        }
    }

    /***
     * This method handles the delay instruction
     * @param delayTime the time the script execution will be delayed for in seconds
     * @throws IsaacScriptSyntaxException
     */
    private void handleDelay(String delayTime) throws IsaacScriptSyntaxException {
        int delay;
        try{
            delay = Integer.parseInt(delayTime);
            delay *= 1000;
        }catch (Exception e){
            throw new IsaacScriptSyntaxException();
        }
        shouldDelay = true;
        delayTimer = new Timer();
        delayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                shouldDelay = false;
                delayTimer.cancel();
            }
        }, delay);
    }

    /***
     * This method handles the action instruction. An action will emit a cmd on a channel of the command bus with optional additional data.
     * @param instructionParts the instruction parts that follow directly behind they action instruction.
     *                         Since the action instruction will always emit a cmd on the command bus the instructionParts are handled as follows:
     *                         instructionParts[1] will always be the channel and instructionsParts[2] will always be the command.
     *                         Every other element of instructionsParts[] will be considered additional data
     */
    private void handleAction(String[] instructionParts) {

        List<String> data = new ArrayList<>(Arrays.asList(instructionParts).subList(2, instructionParts.length));
        Mia.getLogger().logInfo("Script emitting on channel " + instructionParts[1] + " command: " + instructionParts[2]);
        Mia.getCommandBus().emit(instructionParts[1], instructionParts[2], data);
    }

    /***
     * This method handles conditional instructions
     * @param instructionParts the instructionParts of the instruction
     */
    private void handleCondition(String[] instructionParts){
        boolean is_negated = instructionParts[1].equals("not");
        try{
            if(is_negated){
                handleConditionToken(instructionParts[2], true, 2, instructionParts);
            }else{
                handleConditionToken(instructionParts[1], false, 1, instructionParts);
            }
        }catch (IsaacScriptSyntaxException e){
            e.printStackTrace();
        }
    }

    /***
     * This method checks the conditions recognized by handleCondition()
     * @param token the condition to check
     * @param is_negated if the condition is negated
     * @param curTokenPos the current position in allInstructionParts since it may differ depending on if the condition is negated
     *                    and if the condition takes more than one token (after, before)
     * @param allInstructionParts all instructionParts including the token and the potential negation keyword
     * @throws IsaacScriptSyntaxException
     */
    private void handleConditionToken(String token, boolean is_negated, int curTokenPos, String[] allInstructionParts) throws IsaacScriptSyntaxException {
        switch (token) {
            case "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday" -> evaluateWeekdayCondition(token, is_negated);
            case "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december" -> evaluateMonthCondition(token, is_negated);
            case "winter", "spring", "summer", "fall" -> evaluateSeasonCondition(token, is_negated);
            case "after" -> evaluateTimeCondition(allInstructionParts[curTokenPos + 1], false, is_negated);
            case "before" -> evaluateTimeCondition(allInstructionParts[curTokenPos + 1], true, is_negated);
            default -> throw new IsaacScriptSyntaxException();
        }
    }

    /***
     * Evaluates a weekday condition (example: is wednesday)
     * @param weekdayToken the weekday to check for
     * @param is_negated if the condition is negated
     */
    private void evaluateWeekdayCondition(String weekdayToken, boolean is_negated){
        String weekday = weekdayToken.substring(0, 1).toUpperCase() + weekdayToken.substring(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE").withLocale(Locale.ENGLISH);
        TemporalAccessor accessor = formatter.parse(weekday);
        if(LocalDate.now().getDayOfWeek() == DayOfWeek.from(accessor)){
            if(is_negated) condition_stack.push(false);
            else condition_stack.push(true);
        }else{
            if(is_negated) condition_stack.push(true);
            else condition_stack.push(false);
        }
    }

    /***
     * Evaluates a month condition (example: is june)
     * @param monthToken the month to check for
     * @param is_negated if the condition is negated
     */
    private void evaluateMonthCondition(String monthToken, boolean is_negated){
        String month = monthToken.substring(0,1).toUpperCase() + monthToken.substring(1);
        LocalDate curDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dMMMMuuuu").withLocale(Locale.ENGLISH);
        LocalDate date = LocalDate.parse(curDate.getDayOfMonth()+month+curDate.getYear(), formatter);

        if(curDate.getMonth().equals(date.getMonth())){
            if(is_negated) condition_stack.push(false);
            else condition_stack.push(true);
        }else{
            if(is_negated) condition_stack.push(true);
            else condition_stack.push(false);
        }
    }

    /***
     * Evaluates a season condition
     * @param seasonToken the season to check for (example: is spring)
     * @param is_negated if the condition is negated
     * @throws IsaacScriptSyntaxException
     */
    private void evaluateSeasonCondition(String seasonToken, boolean is_negated) throws IsaacScriptSyntaxException {
            Season season = SeasonHelper.getSeasonFromName(seasonToken);
            Season curSeason = SeasonHelper.getSeasonFromDate(LocalDate.now());

            if(season == Season.ERROR || curSeason == Season.ERROR) throw new IsaacScriptSyntaxException();

            if(season.equals(curSeason)){
                if(is_negated) condition_stack.push(false);
                else condition_stack.push(true);
            }else{
                if(is_negated) condition_stack.push(true);
                else condition_stack.push(false);
            }
    }

    /***
     * evaluates a time condition
     * @param timeToken the time to parse and check for
     * @param is_before if the method should check if the current time is before or after the timeToken
     * @param is_negated if the condition is negated
     */
    private void evaluateTimeCondition(String timeToken, boolean is_before, boolean is_negated){
        LocalTime time = LocalTime.parse(timeToken);
        boolean condition_true;
        if(is_before){
            condition_true = LocalTime.now().isBefore(time);
        }else{
            condition_true = LocalTime.now().isAfter(time);
        }
        if(condition_true){
            if(is_negated) condition_stack.push(false);
            else condition_stack.push(true);
        }else{
            if(is_negated) condition_stack.push(true);
            else condition_stack.push(false);
        }

    }

    @Override
    public void shutdown() {
        delayTimer.cancel();
        shutdownInitiated = true;
    }
}
