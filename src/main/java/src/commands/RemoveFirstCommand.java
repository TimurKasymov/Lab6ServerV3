package src.commands;

import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.models.Product;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.models.Role;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class RemoveFirstCommand extends CommandBase implements Command {
    private final ReentrantLock lock = new ReentrantLock();

    public RemoveFirstCommand(CommandManagerCustom commandManager) {
        super(commandManager, List.of(Role.MIN_USER, Role.MIDDLE_USER));
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        var request = new Request(MessageType.REMOVE_FIRST);
        request.userName = args[0];
        request.userPassword = args[1];
        return execute(request);
    }

    @Override
    public boolean execute(Request request) {
        var resp = new Response();
        if (commandManager.getProductsRepo().getProducts().size() == 0) {
            resp.serverResponseToCommand = "collection is empty";
            sendToClient(resp, request);
            return true;
        }
        Product fisrtEl = null;
        lock.lock();
        try{
            fisrtEl  = commandManager.getProductsRepo().getProducts().stream().sorted().toList().get(0);
        }
        finally {
            lock.unlock();
        }
        commandManager.getDbProductManager().delete(fisrtEl);
        var removeCommand = "remove_by_id " + fisrtEl.getId() + " " + request.userName + " " + request.userPassword;
        commandManager.executeCommand(removeCommand);
        resp.serverResponseToCommand = "first product successfully removed";
        sendToClient(resp, request);
        return true;
    }

    @Override
    public String getInfo() {
        return "removes first element in the collection";
    }
}
