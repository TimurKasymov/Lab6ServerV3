package src.commands;

import src.network.requests.HelpRequest;
import src.network.responses.HelpResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.requests.Request;

public class HelpCommand extends CommandBase implements Command {

    public HelpCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }


    @Override
    public boolean execute(String[] request) {
        return execute(new HelpRequest());
    }

    @Override
    public boolean execute(Request request) {
        var response = new HelpResponse(null);
        StringBuilder sb = new StringBuilder();
        for (var commInfo : commandManager.getCommandsInfo()
             ) {
            sb.append(commInfo).append('\n');
        }
        response.helps = sb.toString();
        sendToClient(response);
        return true;
    }

    @Override
    public String getInfo() {
        return "print all elements in string representation to standard output";
    }
}
