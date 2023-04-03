package src.network.requests;

import src.utils.Commands;

public class ExecuteScriptRequest extends Request {
    public String scriptName;
    public int recDepth;
    public ExecuteScriptRequest(String scriptName, int recDepth) {
        super(Commands.EXECUTE_SCRIPT);
        this.scriptName = scriptName;
        this.recDepth = recDepth;
    }
}
