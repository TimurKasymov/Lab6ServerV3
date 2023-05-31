package src.commands;

import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.models.Product;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.models.Role;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class InfoCommand extends CommandBase implements Command {
    public InfoCommand(CommandManagerCustom commandManager) {
        super(commandManager, List.of(Role.MIN_USER, Role.MIDDLE_USER));
        arguments = new LinkedList<>();
    }

    @Override
    public boolean execute(String[] args) {
        var request = new Request(MessageType.INFO);
        request.userName = args[0];
        request.userPassword = args[1];
        return execute(request);
    }

    @Override
    public boolean execute(Request request) {
        var products = commandManager.getProductsRepo().getProducts();
        var time = commandManager.getInitializationTime();
        var timeFormatted = time == null ? "Collection was not initialized" : time
                .atZone(ZoneId.of("Europe/Moscow"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        var toPrint = String.format("type of colleciton: %s\ninitialization date: %s\nnumber of elements: %s", Product.class,
                timeFormatted, products.size());
        var response = new Response();
        response.serverResponseToCommand = toPrint;
        sendToClient(response, request);
        return true;
    }

    @Override
    public String getInfo() {
        return "prints information about the collection to the standard output stream (type, initialization date, number of elements, etc.)";
    }
}
