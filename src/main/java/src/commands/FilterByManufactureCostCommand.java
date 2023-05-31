package src.commands;

import org.apache.commons.lang3.tuple.ImmutablePair;
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

public class FilterByManufactureCostCommand extends CommandBase implements Command {
    public FilterByManufactureCostCommand(CommandManagerCustom commandManager){
        super(commandManager, List.of());
        arguments = new LinkedList<>();
        arguments.add(ImmutablePair.of(Argument.COST, 1));
    }

    @Override
    public boolean execute(String[] args) {
        var cost = Double.parseDouble(args[0]);
        var request = new Request(MessageType.FILTER_BY_MANUFACTURE_COST);
        request.userName = args[1];
        request.userPassword = args[2];
        request.requiredArguments.add(cost);
        return execute(request);
    }

    @Override
    public boolean execute(Request request) {
        Response response;
        try {
            response = new Response(null);
            var manufactureCost = (Double) request.requiredArguments.get(0);
            var products = commandManager.getProductsRepo().getProducts();
            for (Product product : products) {
                if(product.getManufactureCost().doubleValue() == manufactureCost)
                    response.serverResponseToCommand += product.toString() + "\n\n";
            }
        }
        catch (Exception exception){
            response = new Response(String.format("Manufacture cost must be from %s to %s. Try typing this command again", 0, Double.MAX_VALUE));
        }
        sendToClient(response, request);
        return true;
    }

    @Override
    public String getInfo() {
        return "display items whose manufactureCost field value is equal to the given on enter the manufacture cost right after the command name";
    }
}
