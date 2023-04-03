package src.commands;

import src.models.Product;
import src.network.requests.ShowRequest;
import src.network.responses.ShowResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.requests.Request;


public class ShowCommand extends CommandBase implements Command {
    public ShowCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new ShowRequest());
    }

    @Override
    public boolean execute(Request request) {
        var resp = new ShowResponse(null);
        var prods = new StringBuilder();
        var products = commandManager.getCollectionManager().get();
        if(products == null || products.size() == 0){
            resp.setMessageForClient("there is no products yet.. add a new one");
            return true;
        }
        for (Product product : products) {
            String mess = product.toString() + '\n' + '\n';
            prods.append(mess);
        }
        resp.setMessageForClient(prods.toString());
        sendToClient(resp);
        return true;
    }

    @Override
    public String getInfo() {
        return "printing collection elements into the string representation";
    }
}
