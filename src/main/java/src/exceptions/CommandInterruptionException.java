package src.exceptions;

public class CommandInterruptionException extends Exception {
    private final String enteredCommand;
    private InterruptionCause interruptionCause;
    public String getEnteredCommand(){
        return enteredCommand;
    }
    public InterruptionCause getInterruptionCause(){
        return interruptionCause;
    }

    public CommandInterruptionException(String enteredCommand, InterruptionCause cause){
        this.enteredCommand = enteredCommand;
        this.interruptionCause = cause;
    }
    public CommandInterruptionException(String enteredCommand){
        this.enteredCommand = enteredCommand;
        this.interruptionCause = InterruptionCause.ENTERED_COMMAND;
        if(enteredCommand.equals("exit"))
            this.interruptionCause = InterruptionCause.EXIT;
    }
}
