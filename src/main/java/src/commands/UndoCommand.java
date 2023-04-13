package src.commands;

import src.network.requests.UndoRequest;
import src.network.responses.UndoResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.requests.Request;

public class UndoCommand extends CommandBase implements Command {


    public UndoCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new UndoRequest(Integer.parseInt(args[0])));
    }

    @Override
    public boolean execute(Request request) {
        var undoReq = (UndoRequest)request;
        var resp = new UndoResponse(null);
        commandManager.getUndoManager().undoCommands(undoReq.number);
        resp.setMessageForClient("commands were successfully canceled");
        sendToClient(resp);
        return true;
    }

    @Override
    public String getInfo() {
        return "return to the collection state that it wast in N src.commands ago";
    }
}
