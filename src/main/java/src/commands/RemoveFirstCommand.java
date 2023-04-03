package src.commands;

import src.network.requests.RemoveFirstRequest;
import src.network.responses.RemoveFirstResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.requests.Request;

public class RemoveFirstCommand extends CommandBase implements Command {

    public RemoveFirstCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new RemoveFirstRequest());
    }

    @Override
    public boolean execute(Request request) {
        var resp = new RemoveFirstResponse(null);
        if (commandManager.getCollectionManager().get().size() == 0) {
            resp.setMessageForClient("collection is empty");
            return true;
        }
        var removeCommand = "remove_by_id 1";
        commandManager.executeCommand(removeCommand);
        resp.setMessageForClient("first product successfully removed");
        sendToClient(resp);
        return true;
    }

    @Override
    public String getInfo() {
        return "removes first element in the collection";
    }
}
