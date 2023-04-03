package src.commands;

import src.loggerUtils.LoggerManager;
import src.models.Product;
import src.network.requests.AddRequest;
import src.network.requests.Request;
import src.network.responses.AddResponse;
import org.slf4j.Logger;
import src.exceptions.CommandInterruptionException;
import src.exceptions.InterruptionCause;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;


public class AddCommand extends CommandBase implements Command {

    private final Logger logger;

    public AddCommand(CommandManagerCustom commandManager) {
        super(commandManager);
        logger = LoggerManager.getLogger(AddCommand.class);
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new AddRequest(null));
    }

    private Product fillInProduct() throws CommandInterruptionException {
        var inputService = commandManager.getInputService();
        var maxId = Long.MIN_VALUE;
        var products = commandManager.getCollectionManager().get();
        for (var product : products) {
            maxId = Long.max(maxId, product.getId());
        }
        var id = products.size() == 0 ? 1 : maxId + 1;

        var name = inputService.inputName();
        var coord = inputService.inputCoordinates();
        var price = inputService.inputPrice();
        var manufCost = inputService.inputManufactureCost();
        var unit = inputService.inputUnitOfMeasure();

        int yesOrNo = 0;
        for (; ; ) {
            try {
                yesOrNo = commandManager.getInputService().getInt();
                if (yesOrNo != 1 && yesOrNo != 2)
                    continue;
                break;
            } catch (InputMismatchException e) {
                //commandMessageHandler.displayToUser("enter a number: ");
            }
        }
        var prod = new Product(id, name, coord, price, manufCost,
                unit, yesOrNo == 1 ? inputService.inputOrganization(products) : null);
        commandManager.getUndoManager().logAddCommand(id);
        return prod;
    }

    @Override
    public boolean execute(Request request) {
        var addRequest = (AddRequest) request;
        try {
            if (addRequest.product == null) {
                // fill in the product from script
                addRequest.product = fillInProduct();
            }
            var maxId = Long.MIN_VALUE;
            var products = commandManager.getCollectionManager().get();
            for (var product : products) {
                maxId = Long.max(maxId, product.getId());
            }
            var id = products.size() == 0 ? 1 : maxId + 1;
            addRequest.product.setId(id);

            commandManager.getUndoManager().logAddCommand(id);
            if (products.size() == 0) {
                products.add(addRequest.product);
                var resp = new AddResponse(id, null, "product was added");
            }
            else if (products.peekLast().getId() == maxId)
                products.add(addRequest.product);
            else
                products.addFirst(addRequest.product);
            var response = new AddResponse(id, "adding product was successful", null);
            sendToClient(response);
            return true;
        } catch (NoSuchElementException exception) {
            var response = new AddResponse(0L, "adding product was canceled", exception.getMessage());
            sendToClient(response);
        } catch (CommandInterruptionException e) {
            if (e.getInterruptionCause() == InterruptionCause.EXIT)
                logger.info("adding product was successfully canceled");
            else {
                logger.info("adding product was canceled by entered command");
                commandManager.executeCommand(e.getEnteredCommand());
            }
        }
        return false;
    }

    @Override
    public String getInfo() {
        return "add new element to the collection";
    }
}
