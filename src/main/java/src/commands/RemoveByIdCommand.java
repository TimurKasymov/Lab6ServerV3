package src.commands;

import org.apache.commons.lang3.tuple.ImmutablePair;
import src.models.Product;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.models.Role;
import src.service.HashingService;
import src.utils.Argument;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RemoveByIdCommand extends CommandBase implements Command {

    public RemoveByIdCommand(CommandManagerCustom commandManager) {
        super(commandManager,  List.of(Role.MIN_USER, Role.MIDDLE_USER));
        arguments = new LinkedList<>();
        arguments.add(ImmutablePair.of(Argument.ID, 1));
    }

    @Override
    public boolean execute(String[] args) {
        var request = new Request(MessageType.REMOVE_BY_ID);
        request.requiredArguments.add(Long.parseLong(args[0]));
        request.userName = args[1];
        request.userPassword = args[2];
        return execute(request);
    }

    @Override
    public synchronized boolean execute(Request request) {
        var resp = new Response();
        try {
            var id = (Long)request.requiredArguments.get(0);
            var prods = commandManager.getProductsRepo().getProducts();
            if (prods.stream().map(Product::getId).toList().contains(id)) {
                var prodWithId = prods.stream().filter(p -> Objects.equals(p.getId(), id)).findFirst();
                if (prodWithId.isEmpty())
                    throw new NumberFormatException();
                var hashingService = new HashingService();
                var hashedUserPassword = hashingService.hash(request.userPassword);
                var user = prodWithId.get().getUser();
                if(!hashedUserPassword.equals(user.getPassword()) || !request.userName.equals(user.getName()))
                    resp.serverResponseToCommand = String.format("product with id: %s was not removed," +
                            " because you are not the creator of that product", prodWithId.get().getId());
                else{
                    //commandManager.getUndoManager().logRemoveCommand(prodWithId.get());
                    commandManager.getDbProductManager().delete(prodWithId.get());
                    prods.remove(prodWithId.get());
                    resp.serverResponseToCommand = String.format("product with id: %s was successfully removed", prodWithId.get().getId());
                }
                sendToClient(resp, request);
                return true;
            }
            resp.serverResponseToCommand = "Element with this id doesnt exist";
        } catch (NumberFormatException exception) {
            resp.serverResponseToCommand = "ID must be an number. Try typing this command again";
            sendToClient(resp, request);
            return false;
        }
        sendToClient(resp, request);
        return true;
    }

    @Override
    public String getInfo() {
        return "remove an element from the collection by its ID.";
    }
}
