package src.commands;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import src.loggerUtils.LoggerManager;
import org.slf4j.Logger;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.utils.Argument;

import java.util.*;

public class ExecuteScriptCommand extends CommandBase implements Command {
    private final List<String> scriptFilesBeingExecuted;
    private int recDepth = -1;
    private Logger logger;

    public ExecuteScriptCommand(CommandManagerCustom commandManager) {
        super(commandManager);
        scriptFilesBeingExecuted = Collections.synchronizedList(new LinkedList<String>());
        logger = LoggerManager.getLogger(ExecuteScriptCommand.class);
        arguments = new LinkedList<>();
        arguments.add(ImmutablePair.of(Argument.SCRIPT_HASH_MAP, 1));
        arguments.add(ImmutablePair.of(Argument.NUMBER, 1));
    }

    @Override
    public synchronized boolean execute(String[] args){
        var request = new Request(MessageType.EXECUTE_SCRIPT);
        request.requiredArguments.add(commandManager.getExecuteScriptHandyMap());
        request.requiredArguments.add(recDepth);
        request.requiredArguments.add(args[0]);
        return execute(request);
    }

    @Override
    public synchronized boolean execute(Request request) {
        var scripts = (LinkedHashMap<String, List<String>>) request.requiredArguments.get(0);
        recDepth = (Integer) request.requiredArguments.get(1);
        commandManager.setExecuteScriptHandyMap(scripts);
        String scriptName;
        if(request.requiredArguments.size() == 3){
            scriptName = (String)request.requiredArguments.get(2);
        }
        else{
            scriptName = scripts.keySet().iterator().next();
        }
        try {
            if (scriptFilesBeingExecuted.contains(scriptName)) {
                var currentRecursionDepth = scriptFilesBeingExecuted.stream().filter(s -> s.equals(scriptName)).count();
                if (currentRecursionDepth >= recDepth) {
                    return true;
                }
            }
            //if (scriptFilesBeingExecuted.size() == 0) {
            //    commandManager.getUndoManager().startOrEndTransaction();
            //}
            scriptFilesBeingExecuted.add(scriptName);

            Iterator<String> reader = scripts.get(scriptName).listIterator();
            String command;
            commandManager.getInputService().setIterator(reader);
            while (reader.hasNext() && (command = reader.next()) != null) {
                commandManager.executeCommand(command);
                commandManager.getInputService().setIterator(reader);
            }
            logger.info("Commands ended.");

            scriptFilesBeingExecuted.remove(scriptName);
            //if (scriptFilesBeingExecuted.size() == 0) {
                //commandManager.getUndoManager().startOrEndTransaction();
            //}
        } catch (Exception fileNotFoundException) {
            logger.info("File not found. Try again.");
            var response = new
                    Response("File not found. Try again.");
            sendToClient(response, request);
            return true;
        }
        var response = new
                Response("file was executed successfully");
        sendToClient(response, request);
        return true;
    }

    @Override
    public String getInfo() {
        return "read and execute a script from specified file. You should enter path to file after entering a command.";
    }
}