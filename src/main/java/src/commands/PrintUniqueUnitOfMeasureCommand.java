package src.commands;

import src.models.UnitOfMeasure;
import src.network.requests.PrintUniqueUnitOfMeasureRequest;
import src.network.responses.PrintUniqueUnitOfMeasureResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.requests.Request;
import java.util.HashSet;

public class PrintUniqueUnitOfMeasureCommand extends CommandBase implements Command {
    public PrintUniqueUnitOfMeasureCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new PrintUniqueUnitOfMeasureRequest());
    }

    @Override
    public boolean execute(Request request) {
        var response = new PrintUniqueUnitOfMeasureResponse(null);
        var products = commandManager.getCollectionManager().get();
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
        response.setMessageForClient(res.toString());
        sendToClient(response);
        return true;
    }

    @Override
    public String getInfo() {
        return "displays unique values of the unitOfMeasure field of all elements in the collection";
    }
}
