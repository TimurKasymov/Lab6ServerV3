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

public class RemoveFirstCommand extends CommandBase implements Command {

    public RemoveFirstCommand(CommandManagerCustom commandManager) {
        super(commandManager);
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new Request(MessageType.REMOVE_FIRST));

    }

    @Override
    public boolean execute(Request request) {
        var resp = new Response();
        if (commandManager.getCollectionManager().get().size() == 0) {
            resp.serverResponseToCommand = "collection is empty";
            return true;
        }
        var removeCommand = "remove_by_id 1";
        commandManager.executeCommand(removeCommand);
        resp.serverResponseToCommand = "first product successfully removed";
        sendToClient(resp);
        return true;
    }

    @Override
    public String getInfo() {
        return "removes first element in the collection";
    }
}
