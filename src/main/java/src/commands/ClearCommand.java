package src.commands;

import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.models.Role;
import src.utils.Commands;

import java.util.LinkedList;
import java.util.List;

public class ClearCommand extends CommandBase implements Command {

    public ClearCommand(CommandManagerCustom commandManager) {
        super(commandManager, List.of(Role.MIDDLE_USER, Role.MIN_USER));
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(Request request) {
        var prods = commandManager.getProductsRepo().getProducts();
        var numbOFLoops = prods.size();
        //commandManager.getUndoManager().startOrEndTransaction();
        for (src.models.Product prod : prods) {
            commandManager.executeCommand(Commands.REMOVE_BY_ID + " " + prod.getId() + " "
                    + request.userName + " " + request.userPassword);
        }
        //commandManager.getUndoManager().startOrEndTransaction();
        var response = new Response("all elements were removed");
        sendToClient(response, request);
        return true;
    }

    @Override
    public boolean execute(String[] request) {
        var requestToSend = new Request(MessageType.CLEAR);
        requestToSend.userName = request[0];
        requestToSend.userPassword = request[1];
        return execute(requestToSend);
    }

    @Override
    public String getInfo() {
        return "clear the collection";
    }
}
