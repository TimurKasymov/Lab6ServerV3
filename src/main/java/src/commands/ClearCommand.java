package src.commands;

import org.apache.commons.lang3.tuple.Pair;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.utils.Argument;

import java.util.LinkedList;
import java.util.List;

public class ClearCommand extends CommandBase implements Command {

    public ClearCommand(CommandManagerCustom commandManager) {
        super(commandManager);
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(Request request) {
        var prods = commandManager.getProducts();
        var numbOFLoops = prods.size();
        //commandManager.getUndoManager().startOrEndTransaction();
        for (int i = 0; i < numbOFLoops; i++) {
            var req = new Request(MessageType.REMOVE_FIRST);
            commandManager.executeCommand(req);
        }
        //commandManager.getUndoManager().startOrEndTransaction();
        var response = new Response();
        sendToClient(response, request);
        return true;
    }

    @Override
    public boolean execute(String[] request) {
        return execute(new Request(MessageType.CLEAR));
    }

    @Override
    public String getInfo() {
        return "clear the collection";
    }
}
