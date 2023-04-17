package src.commands;

import org.apache.commons.lang3.tuple.Pair;
import src.network.Response;
import src.interfaces.CommandManagerCustom;
import src.utils.Argument;

import java.util.List;

public class CommandBase {
    protected CommandManagerCustom commandManager;

    public CommandBase(CommandManagerCustom commandManager){
        this.commandManager = commandManager;
    }
    protected List<Pair<Argument, Integer>> arguments;

    protected void sendToClient(Response response){
        var data = commandManager.getSerializationManager().serialize(response);
        commandManager.getSendingManager().send(data, commandManager.getClientChannel(), commandManager.getClientPort());
    }
    public List<Pair<Argument, Integer>> getRequiredArguments(){
        return arguments;
    }
}
