package src.commands;

import org.apache.commons.lang3.tuple.Pair;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.utils.Argument;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ReorderCommand extends CommandBase implements Command {

    public ReorderCommand(CommandManagerCustom commandManager){
        super(commandManager);
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new Request(MessageType.REORDER));
    }

    @Override
    public boolean execute(Request request) {
        var resp = new Response();
        Collections.reverse(commandManager.getProducts());
        commandManager.getDbProductManager().markReversedCollection();
        //commandManager
        //        .getUndoManager()
        //        .logReorderCommand();
        resp.serverResponseToCommand = "collection was reordered successfully";
        sendToClient(resp, request);
        return true;
    }

    @Override
    public String getInfo() {
        return "reorder the collection";
    }
}
