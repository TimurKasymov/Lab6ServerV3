package src.commands;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.utils.Argument;
import java.util.LinkedList;
import java.util.List;

public class FilterGreaterThanPriceCommand extends CommandBase implements Command {
    public FilterGreaterThanPriceCommand(CommandManagerCustom commandManager) {
        super(commandManager, List.of());
        arguments = new LinkedList<>();
        arguments.add(ImmutablePair.of(Argument.PRICE, 1));
    }

    public boolean execute(String[] args) {
        var cost = Float.parseFloat(args[0]);
        var request = new Request(MessageType.FILTER_GREATER_THAN_PRICE);
        request.userName = args[1];
        request.userPassword = args[2];
        request.requiredArguments.add(cost);
        return execute(request);
    }

    @Override
    public boolean execute(Request request) {
        var price = (Float) request.requiredArguments.get(0);
        var response = new Response();
        var products = commandManager.getProductsRepo().getProducts();
        try {
            var flag = false;
            for (var prod : products) {
                if (prod.getPrice() > price) {
                    response.serverResponseToCommand += prod.toString() + "\n\n";
                    flag = true;
                }
            }
            if (!flag)
                response.serverResponseToCommand = "no such elements found";
            sendToClient(response, request);
            return true;
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            response.serverResponseToCommand = "ID must be provided and it must be a number. Try typing this command again";
            sendToClient(response, request);
            return false;
        }
    }


    @Override
    public String getInfo() {
        return "display elements whose price field value is greater than the specified one";
    }
}
