package src.commands;

import org.apache.commons.lang3.tuple.Pair;
import src.models.Product;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.utils.Argument;

import java.util.LinkedList;
import java.util.List;


public class ShowCommand extends CommandBase implements Command {
    public ShowCommand(CommandManagerCustom commandManager){
        super(commandManager);
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new Request(MessageType.SHOW));
    }

    @Override
    public synchronized boolean execute(Request request) {
        var resp = new Response();
        var prods = new StringBuilder();
        var products = commandManager.getProducts();
        if(products == null || products.size() == 0){
            resp.serverResponseToCommand = "there is no products yet.. add a new one";
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
