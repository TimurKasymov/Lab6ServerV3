package src.network.responses;

import src.utils.Commands;

public class ExecuteScriptResponse extends Response {
    public boolean recursionDetected;

    public ExecuteScriptResponse(boolean recursionDetected, String error) {
        super(Commands.EXECUTE_SCRIPT, error);
        this.recursionDetected = recursionDetected;
    }
}
