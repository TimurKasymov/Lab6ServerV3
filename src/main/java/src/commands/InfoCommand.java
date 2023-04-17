package src.commands;

import org.apache.commons.lang3.tuple.Pair;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.utils.Argument;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class InfoCommand extends CommandBase implements Command {
    public InfoCommand(CommandManagerCustom commandManager){
        super(commandManager);
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new Request(MessageType.INFO));
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
        var response = new Response();
        response.serverResponseToCommand = toPrint;
        sendToClient(response);
        return true;
    }

    @Override
    public String getInfo() {
        return "prints information about the collection to the standard output stream (type, initialization date, number of elements, etc.)";
    }
}
