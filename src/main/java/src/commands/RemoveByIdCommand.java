package src.commands;

import src.models.Product;
import src.network.requests.RemoveByIdRequest;
import src.network.responses.RemoveByIdResponse;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.requests.Request;

import java.util.Objects;

public class RemoveByIdCommand extends CommandBase implements Command {

    public RemoveByIdCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new RemoveByIdRequest(Long.parseLong(args[0])));
    }

    @Override
    public boolean execute(Request request) {
        var removeReq = (RemoveByIdRequest) request;
        var resp = new RemoveByIdResponse(null);
        try {
            var id = removeReq.id;
            var prods = commandManager.getCollectionManager().get();
            if (prods.stream().map(Product::getId).toList().contains(id)) {
                var prodWithId = prods.stream().filter(p -> Objects.equals(p.getId(), id)).findFirst();
                if (prodWithId.isEmpty())
                    throw new NumberFormatException();
                commandManager.getUndoManager().logRemoveCommand(prodWithId.get());
                prods.remove(prodWithId.get());
                resp.setMessageForClient(String.format("product with id: %s was successfully removed", prodWithId.get().getId()));
                return true;
            }
            resp.setMessageForClient("Element with this id doesnt exist");
        } catch (NumberFormatException exception) {
            resp.setError("ID must be an number. Try typing this command again");
            sendToClient(resp);
            return false;
        }
        sendToClient(resp);
        return true;
    }

    @Override
    public String getInfo() {
        return "remove an element from the collection by its ID.";
    }
}
