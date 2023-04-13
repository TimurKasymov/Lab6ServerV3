package src.commands;

import src.loggerUtils.LoggerManager;
import src.network.requests.ExecuteScriptRequest;
import src.network.requests.Request;
import src.network.responses.ExecuteScriptResponse;
import org.slf4j.Logger;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ExecuteScriptCommand extends CommandBase implements Command {
    private final LinkedList<String> scriptFilesBeingExecuted;
    private int recDepth = -1;
    private Logger logger;

    public ExecuteScriptCommand(CommandManagerCustom commandManager) {
        super(commandManager);
        scriptFilesBeingExecuted = new LinkedList<>();
        logger = LoggerManager.getLogger(ExecuteScriptCommand.class);
    }

    @Override
    public boolean execute(String[] args){
        var request = new ExecuteScriptRequest(recDepth, commandManager.getExecuteScriptHandyMap());
        request.scriptName = args[0];
        return execute(request);
    }

    @Override
    public boolean execute(Request request) {
        var executeScriptRequest = (ExecuteScriptRequest)request;
        commandManager.setExecuteScriptHandyMap(executeScriptRequest.nameAndContents);
        recDepth = executeScriptRequest.recDepth;
        try {
            if (scriptFilesBeingExecuted.contains(executeScriptRequest.scriptName)) {
                var currentRecursionDepth = scriptFilesBeingExecuted.stream().filter(s -> s.equals(executeScriptRequest.scriptName)).count();
                if (currentRecursionDepth >= recDepth) {
                    return true;
                }
            }
            if (scriptFilesBeingExecuted.size() == 0) {
                commandManager.getUndoManager().startOrEndTransaction();
            }
            scriptFilesBeingExecuted.add(executeScriptRequest.scriptName);

            Iterator<String> reader = executeScriptRequest.nameAndContents.get(executeScriptRequest.scriptName).listIterator();
            String command;
            commandManager.getInputService().setIterator(reader);
            while (reader.hasNext() && (command = reader.next()) != null) {
                commandManager.executeCommand(command);
                commandManager.getInputService().setIterator(reader);
            }
            logger.info("Commands ended.");

            scriptFilesBeingExecuted.remove(executeScriptRequest.scriptName);
            if (scriptFilesBeingExecuted.size() == 0) {
                commandManager.getUndoManager().startOrEndTransaction();
            }
        } catch (Exception fileNotFoundException) {
            logger.info("File not found. Try again.");
            var response = new ExecuteScriptResponse(false, "File not found. Try again.");
            sendToClient(response);
            return true;
        }
        var response = new ExecuteScriptResponse(false, null);
        response.setMessageForClient("file was executed successfully");
        sendToClient(response);
        return true;
    }

    @Override
    public String getInfo() {
        return "read and execute a script from specified file. You should enter path to file after entering a command.";
    }
}