package src.commands;

import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.models.Role;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ReorderCommand extends CommandBase implements Command {

    public ReorderCommand(CommandManagerCustom commandManager){
        super(commandManager, List.of(Role.MIN_USER));
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        var request = new Request(MessageType.REORDER);
        request.userName = args[0];
        request.userPassword = args[1];
        return execute(request);
    }

    @Override
    public boolean execute(Request request) {
        var resp = new Response();
        Collections.reverse(commandManager.getProductsRepo().getProducts());
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
