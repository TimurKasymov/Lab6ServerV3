package src.commands;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.utils.Argument;

import java.util.LinkedList;
import java.util.List;

public class UndoCommand extends CommandBase implements Command {


    public UndoCommand(CommandManagerCustom commandManager) {
        super(commandManager);
        arguments = new LinkedList<>();
        arguments.add(ImmutablePair.of(Argument.NUMBER, 1));

    }

    @Override
    public boolean execute(String[] args) {
        var request = new Request(MessageType.UNDO_COMMANDS);
        request.requiredArguments.add(Integer.parseInt(args[0]));
        return execute(request);
    }

    @Override
    public boolean execute(Request request) {
        var resp = new Response();
        var num = (Integer)request.requiredArguments.get(0);
        commandManager.getUndoManager().undoCommands(num);
        resp.serverResponseToCommand = "commands were successfully canceled";
        sendToClient(resp);
        return true;
    }

    @Override
    public String getInfo() {
        return "return to the collection state that it wast in N src.commands ago";
    }
}
