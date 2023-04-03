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
import java.util.LinkedList;
import java.util.Scanner;

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
        var request = new ExecuteScriptRequest(args[0], 5);
        return execute(request);
    }

    @Override
    public boolean execute(Request request) {
        var executeScriptRequest = (ExecuteScriptRequest)request;
        recDepth = executeScriptRequest.recDepth;
        try {
            if (scriptFilesBeingExecuted.contains(executeScriptRequest.scriptName)) {
                var currentRecursionDepth = scriptFilesBeingExecuted.stream().filter(s -> s.equals(executeScriptRequest.scriptName)).count();
                if (currentRecursionDepth >= recDepth) {
                    recDepth = -1;
                    var response = new ExecuteScriptResponse(true, null);
                    sendToClient(response);
                }
            }
            if (scriptFilesBeingExecuted.size() == 0) {
                commandManager.getUndoManager().startOrEndTransaction();
            }
            scriptFilesBeingExecuted.add(executeScriptRequest.scriptName);

            Scanner reader = new Scanner(new FileReader(executeScriptRequest.scriptName));
            commandManager.getInputService().setScanner(reader);
            String[] finalUserCommand;
            String command;
            while (reader.hasNext() && (command = reader.nextLine()) != null) {
                commandManager.executeCommand(command);
                commandManager.getInputService().setScanner(reader);
            }
            logger.info("Commands ended.");
            reader.close();

            scriptFilesBeingExecuted.remove(executeScriptRequest.scriptName);
            if (scriptFilesBeingExecuted.size() == 0) {
                commandManager.getUndoManager().startOrEndTransaction();
            }
        } catch (FileNotFoundException fileNotFoundException) {
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