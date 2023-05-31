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

public class HelpCommand extends CommandBase implements Command {

    public HelpCommand(CommandManagerCustom commandManager){
        super(commandManager, List.of());
        arguments = new LinkedList<>();
    }


    @Override
    public boolean execute(String[] request) {
        var requestToSend = new Request(MessageType.HELP);
        requestToSend.userName = request[0];
        requestToSend.userPassword = request[1];
        return execute(requestToSend);
    }

    @Override
    public synchronized boolean execute(Request request) {
        var response = new Response();
        StringBuilder sb = new StringBuilder();
        var foundUser = commandManager.getUsersRepo().getUser(request);
        if (foundUser.isEmpty())
            return false;
        for (var commInfo : commandManager.getCommandsInfo(foundUser.get().role)
             ) {
            sb.append(commInfo).append('\n');
        }
        response.serverResponseToCommand = sb.toString();
        sendToClient(response, request);
        return true;
    }

    @Override
    public String getInfo() {
        return "print all elements in string representation to standard output";
    }
}
