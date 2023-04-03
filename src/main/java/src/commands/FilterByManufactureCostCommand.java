package src.commands;

import src.models.Product;
import src.network.requests.FilterByManufactureCostRequest;
import src.network.requests.Request;
import src.network.responses.FilterByManufactureCostResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;

public class FilterByManufactureCostCommand extends CommandBase implements Command {
    public FilterByManufactureCostCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }


    @Override
    public boolean execute(String[] args) {
        var cost = Double.parseDouble(args[0]);
        return execute(new FilterByManufactureCostRequest(cost));
    }

    @Override
    public boolean execute(Request request) {
        var filterRequest = (FilterByManufactureCostRequest)request;
        FilterByManufactureCostResponse response;
        try {
            response = new FilterByManufactureCostResponse(null);
            var manufactureCost = filterRequest.getCost();
            var products = commandManager.getCollectionManager().get();
            for (Product product : products) {
                if(product.getManufactureCost().doubleValue() == manufactureCost)
                    response.add(product);
            }
        }
        catch (Exception exception){
            response = new FilterByManufactureCostResponse(String.format("Manufacture cost must be from %s to %s. Try typing this command again", 0, Double.MAX_VALUE));
        }
        sendToClient(response);

        return true;
    }

    @Override
    public String getInfo() {
        return "display items whose manufactureCost field value is equal to the given on enter the manufacture cost right after the command name";
    }
}
