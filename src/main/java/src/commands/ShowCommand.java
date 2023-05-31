package src.commands;

import src.models.Product;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;

import java.util.LinkedList;
import java.util.List;


public class ShowCommand extends CommandBase implements Command {
    public ShowCommand(CommandManagerCustom commandManager) {
        super(commandManager, List.of());
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        var request = new Request(MessageType.SHOW);
        request.userName = args[0];
        request.userPassword = args[1];
        return execute(request);
    }

    @Override
    public synchronized boolean execute(Request request) {
        var resp = new Response();
        var prods = new StringBuilder();
        var products = commandManager.getProductsRepo().getProducts();
        if (products == null || products.size() == 0) {
            resp.serverResponseToCommand = "there is no products yet.. add a new one";
            sendToClient(resp, request);
            return true;
        }
        for (Product product : products) {
            String mess = product.toString() + '\n' + '\n';
            prods.append(mess);
        }
        resp.serverResponseToCommand = prods.toString();
        sendToClient(resp, request);
        return true;
    }

    @Override
    public String getInfo() {
        return "printing collection elements into the string representation";
    }
}
