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
        super(commandManager);
        arguments = new LinkedList<>();
        arguments.add(ImmutablePair.of(Argument.PRICE, 1));
    }


    @Override
    public boolean execute(String[] request) {
        return execute(new Request(MessageType.HELP));
    }

    @Override
    public boolean execute(Request request) {
        var response = new Response();
        StringBuilder sb = new StringBuilder();
        for (var commInfo : commandManager.getCommandsInfo()
             ) {
            sb.append(commInfo).append('\n');
        }
        response.serverResponseToCommand = sb.toString();
        sendToClient(response);
        return true;
    }

    @Override
    public String getInfo() {
        return "print all elements in string representation to standard output";
    }
}
