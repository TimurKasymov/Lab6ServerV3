package src.commands;

import src.network.requests.ReorderRequest;
import src.network.responses.ReorderResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.requests.Request;

import java.util.Collections;

public class ReorderCommand extends CommandBase implements Command {

    public ReorderCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new ReorderRequest());
    }

    @Override
    public boolean execute(Request request) {
        var resp = new ReorderResponse(null);
        Collections.reverse(commandManager.getCollectionManager().get());
        commandManager
                .getUndoManager()
                .logReorderCommand();
        resp.setMessageForClient("collection was reordered successfully");
        sendToClient(resp);
        return true;
    }

    @Override
    public String getInfo() {
        return "reorder the collection";
    }
}
