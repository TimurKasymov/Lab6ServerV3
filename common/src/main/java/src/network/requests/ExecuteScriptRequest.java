package src.network.requests;

import src.utils.Commands;

import java.util.HashMap;
import java.util.List;

public class ExecuteScriptRequest extends Request {
    public String scriptName;
    public int recDepth;
    public HashMap<String, List<String>> nameAndContents;
    public ExecuteScriptRequest(int recDepth, HashMap<String, List<String>> nameAndContents) {
        super(Commands.EXECUTE_SCRIPT);
        this.recDepth = recDepth;
        this.nameAndContents = nameAndContents;
    }
}
