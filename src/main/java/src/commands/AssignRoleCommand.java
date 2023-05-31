package src.commands;

import org.apache.commons.lang3.tuple.ImmutablePair;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.models.Role;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.utils.Argument;

import java.util.LinkedList;
import java.util.List;

public class AssignRoleCommand extends CommandBase implements Command {

    public AssignRoleCommand(CommandManagerCustom commandManager) {
        super(commandManager, List.of(Role.MIDDLE_USER, Role.MIN_USER));
        arguments = new LinkedList<>();
        arguments.add(ImmutablePair.of(Argument.ID, 1));
        arguments.add(ImmutablePair.of(Argument.ROLE, 1));
    }

    @Override
    public boolean execute(String[] args) {
        var requestToSend = new Request(MessageType.CLEAR);
        requestToSend.requiredArguments.add(Integer.valueOf(args[0]));
        requestToSend.requiredArguments.add(Integer.valueOf(args[1]));
        requestToSend.userName = args[2];
        requestToSend.userPassword = args[3];
        return execute(requestToSend);
    }

    @Override
    public boolean execute(Request request) {
        var resp = new Response(null);
        var userId = (Long) request.requiredArguments.get(0);
        var roleToAssign = (Role)request.requiredArguments.get(1);
        var user = commandManager.getUsersRepo().getUser(userId.intValue());
        if(user.isEmpty())
        {
            resp.serverResponseToCommand = "user with that id not found";
            sendToClient(resp, request);
            return true;
        }
        resp.serverResponseToCommand = "done";
        user.get().role = roleToAssign;
        commandManager.getDbUserManager().update(user.get());
        sendToClient(resp, request);
        return true;
    }

    @Override
    public String getInfo() {
        return "assigns role to the specified user";
    }
}
