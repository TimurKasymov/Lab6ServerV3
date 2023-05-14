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

public class HistoryCommand extends CommandBase implements Command {

    public HistoryCommand(CommandManagerCustom commandManager){
        super(commandManager);
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new Request(MessageType.HISTORY));
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
        var response = new Response(null);
        response.serverResponseToCommand = str.toString();
        sendToClient(response, request);
        return true;
    }

    @Override
    public String getInfo() {
        return "output the last 9 used src.commands";
    }
}
