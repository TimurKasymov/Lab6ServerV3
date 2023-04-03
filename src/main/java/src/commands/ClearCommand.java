package src.commands;

import src.network.requests.ClearRequest;
import src.network.requests.RemoveByIdRequest;
import src.network.requests.Request;
import src.network.responses.ClearResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;

public class ClearCommand extends CommandBase implements Command {

    public ClearCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    @Override
    public boolean execute(Request request) {
        var prods = commandManager.getCollectionManager().get();
        var numbOFLoops = prods.size();
        commandManager.getUndoManager().startOrEndTransaction();
        for (int i = 0; i < numbOFLoops; i++) {
            commandManager.executeCommand(new RemoveByIdRequest((long)i));
        }
        commandManager.getUndoManager().startOrEndTransaction();

        var response = new ClearResponse(null);
        sendToClient(response);
        return true;
    }

    @Override
    public boolean execute(String[] request) {
        return execute(new ClearRequest());
    }


    @Override
    public String getInfo() {
        return "clear the collection";
    }
}
