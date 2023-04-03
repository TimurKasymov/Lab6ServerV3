package src.commands;

import src.network.requests.FilterGreaterThanPriceRequest;
import src.network.responses.FilterGreaterThanPriceResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.requests.Request;

public class FilterGreaterThanPriceCommand extends CommandBase implements Command {
    public FilterGreaterThanPriceCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    public boolean execute(String[] args) {
        return execute(new FilterGreaterThanPriceRequest(Float.parseFloat(args[0])));
    }

    @Override
    public boolean execute(Request request) {
        var filterRequest = (FilterGreaterThanPriceRequest)request;
        var price = filterRequest.getPrice();
        FilterGreaterThanPriceResponse response;
        var filterResponse =  new FilterGreaterThanPriceResponse(null, null);
        var products = commandManager.getCollectionManager().get();
        try {
            var flag = false;
            for (var prod : products) {
                if (prod.getPrice() > price) {
                    filterResponse.add(prod);
                    flag = true;
                }
            }
            if (!flag)
                filterResponse.comment = "no such elements found";
            sendToClient(filterResponse);
            return true;
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            filterResponse.setError("ID must be provided and it must be a number. Try typing this command again");
            sendToClient(filterResponse);
            return false;
        }
    }


    @Override
    public String getInfo() {
        return "display elements whose price field value is greater than the specified one";
    }
}
