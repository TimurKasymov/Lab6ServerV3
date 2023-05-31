package src.commands;

import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.models.Role;

import java.util.LinkedList;
import java.util.List;

public class HistoryCommand extends CommandBase implements Command {

    public HistoryCommand(CommandManagerCustom commandManager){
        super(commandManager, List.of(Role.MIN_USER));
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        var requestToSend = new Request(MessageType.HISTORY);
        requestToSend.userName = args[0];
        requestToSend.userPassword = args[1];
        return execute(requestToSend);
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
