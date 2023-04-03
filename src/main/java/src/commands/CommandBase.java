package src.commands;

import src.network.responses.Response;
import src.interfaces.CommandManagerCustom;
public class CommandBase {
    protected CommandManagerCustom commandManager;

    public CommandBase(CommandManagerCustom commandManager){
        this.commandManager = commandManager;
    }

    protected void sendToClient(Response response){
        var data = commandManager.getSerializationManager().serialize(response);
        commandManager.getSendingManager().send(data, commandManager.getClientChannel());
    }
}
