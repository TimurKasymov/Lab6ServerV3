package src.commands;

import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.models.Role;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ShowUsersCommand extends CommandBase implements Command {
    private ReentrantLock lock = new ReentrantLock();

    public ShowUsersCommand(CommandManagerCustom commandManager) {
        super(commandManager, List.of(Role.MIN_USER, Role.MIDDLE_USER));
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        var requestToSend = new Request(MessageType.SHOW_USERS);
        requestToSend.userName = args[0];
        requestToSend.userPassword = args[1];
        return execute(requestToSend);
    }

    @Override
    public boolean execute(Request request) {
        var users = commandManager.getUsersRepo().getUsers();
        var str = new StringBuilder();
        lock.lock();
        try{
            users.forEach(u-> str.append(u.toString()).append("\n\n"));
        }
        finally {
            lock.unlock();
        }
        var response = new Response(str.toString());
        sendToClient(response, request);
        return false;
    }

    @Override
    public String getInfo() {
        return "print all users and their roles with id";
    }

}
