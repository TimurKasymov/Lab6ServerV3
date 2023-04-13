package src.commands;

import src.network.requests.InfoRequest;
import src.network.responses.InfoResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.requests.Request;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InfoCommand extends CommandBase implements Command {
    public InfoCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new InfoRequest());
    }

    @Override
    public boolean execute(Request request) {
        var products = commandManager.getCollectionManager().get();
        var collection = commandManager.getCollectionManager();
        var time = collection.getInitializationTime();
        var timeFormatted = time == null ? "Collection was not initialized" : time
                .atZone(ZoneId.of("Europe/Moscow"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        var toPrint = String.format("type of colleciton: %s\ninitialization date: %s\nnumber of elements: %s", collection.getElementType().getName(),
                timeFormatted, products.size() );
        var response = new InfoResponse(null);
        response.setMessageForClient(toPrint);
        sendToClient(response);
        return true;
    }

    @Override
    public String getInfo() {
        return "prints information about the collection to the standard output stream (type, initialization date, number of elements, etc.)";
    }
}
