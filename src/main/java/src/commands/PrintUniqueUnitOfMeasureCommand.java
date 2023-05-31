package src.commands;

import org.apache.commons.lang3.tuple.Pair;
import src.models.UnitOfMeasure;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.utils.Argument;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class PrintUniqueUnitOfMeasureCommand extends CommandBase implements Command {
    public PrintUniqueUnitOfMeasureCommand(CommandManagerCustom commandManager){
        super(commandManager, List.of());
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        var request = new Request(MessageType.PRINT_UNIQUE_UNIT_OF_MEASURE);
        request.userName = args[0];
        request.userPassword = args[1];
        return execute(request);
    }

    @Override
    public synchronized boolean execute(Request request) {
        var response = new Response();
        var products = commandManager.getProductsRepo().getProducts();
        var set = new HashSet<UnitOfMeasure>();
        var res = new StringBuilder();
        for(var prod : products){
            if(prod.getUnitOfMeasure() != null)
                set.add(prod.getUnitOfMeasure());
        }
        for (var unit: set
        ) {
            res.append(unit.toString()).append('\n');
        }
        response.serverResponseToCommand = res.toString();
        sendToClient(response, request);
        return true;
    }

    @Override
    public String getInfo() {
        return "displays unique values of the unitOfMeasure field of all elements in the collection";
    }
}
