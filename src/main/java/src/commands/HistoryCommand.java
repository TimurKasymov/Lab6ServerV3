package src.commands;

import src.network.requests.HistoryRequest;
import src.network.responses.HistoryResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.requests.Request;

public class HistoryCommand extends CommandBase implements Command {

    public HistoryCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new HistoryRequest());
    }
    @Override
    public boolean execute(Request request) {
        var str = new StringBuilder();
        str.append("9 last used src.commands:\n");
        var commandHistory = commandManager.getCommandHistory();
        int counter = 0;
        for(int i = commandHistory.size() - 1; i >= 0 && counter < 9; i--){
            str.append(commandHistory.get(i)).append("\n");
            counter++;
        }
        var response = new HistoryResponse(null);
        response.history = str.toString();
        sendToClient(response);
        return true;
    }

    @Override
    public String getInfo() {
        return "output the last 9 used src.commands";
    }
}
