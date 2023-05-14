package src.commands;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import src.loggerUtils.LoggerManager;
import src.models.Product;
import src.network.MessageType;
import src.network.Request;
import org.slf4j.Logger;
import src.exceptions.CommandInterruptionException;
import src.exceptions.InterruptionCause;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.Response;
import src.service.ValidatorService;
import src.utils.Argument;

import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class AddCommand extends CommandBase implements Command {

    private final Logger logger;
    private Lock lock = new ReentrantLock();
    public AddCommand(CommandManagerCustom commandManager) {
        super(commandManager);
        logger = LoggerManager.getLogger(AddCommand.class);
        arguments = new LinkedList<>();
        arguments.add(ImmutablePair.of(Argument.PRODUCT, 1));
    }

    @Override
    public boolean execute(String[] args) {
        return execute(new Request(MessageType.ADD));
    }

    private Product fillInProduct() throws CommandInterruptionException {
        var inputService = commandManager.getInputService();
        var maxId = Long.MIN_VALUE;
        var products = commandManager.getProducts();

        lock.lock();
        try {
            for (var product : products) {
                maxId = Long.max(maxId, product.getId());
            }
        }
        finally {
            lock.unlock();
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

        return prod;
    }

    @Override
    public boolean execute(Request request) {
        try {
            Product product;
            if (request.requiredArguments.size() == 0) {
                // fill in the product from script
                product = fillInProduct();
            }
            product = (Product)request.requiredArguments.get(0);

            var maxId = Long.MIN_VALUE;
            var products = commandManager.getProducts();
            lock.lock();
            try {
                for (var prod : products) {
                    maxId = Long.max(maxId, prod.getId());
                }
            }
            finally {
                lock.unlock();
            }
            var id = products.size() == 0 ? 1 : maxId + 1;
            product.setId(id);

            if(!ValidatorService.validateProduct(product))
            {
                var resp = new Response("product has not met validation criteria");
                sendToClient(resp, request);
                return true;
            }
            //commandManager.getUndoManager().logAddCommand(id);
            lock.lock();
            try {
                commandManager.getDbProductManager().insert(product);
                if (products.size() == 0) {
                    products.add(product);
                    var resp = new Response("product with id " + id + " added");
                } else if (products.peekLast().getId() == maxId)
                    products.add(product);
                else
                    products.addFirst(product);
                var response = new Response("product with id " + id + " added");
                sendToClient(response, request);
                return true;
            }
            finally {
                lock.unlock();
            }
        } catch (NoSuchElementException exception) {
            var response = new Response("adding product was canceled");
            sendToClient(response, request);
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
