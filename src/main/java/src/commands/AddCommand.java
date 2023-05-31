package src.commands;

import org.apache.commons.lang3.tuple.ImmutablePair;
import src.db.SeqNames;
import src.loggerUtils.LoggerManager;
import src.models.Product;
import src.models.User;
import src.network.MessageType;
import src.network.Request;
import org.slf4j.Logger;
import src.exceptions.CommandInterruptionException;
import src.exceptions.InterruptionCause;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.Response;
import src.models.Role;
import src.service.HashingService;
import src.service.ValidatorService;
import src.utils.Argument;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class AddCommand extends CommandBase implements Command {

    private final Logger logger;
    private Lock lock = new ReentrantLock();

    public AddCommand(CommandManagerCustom commandManager) {
        super(commandManager, List.of(Role.MIDDLE_USER));
        logger = LoggerManager.getLogger(AddCommand.class);
        arguments = new LinkedList<>();
        arguments.add(ImmutablePair.of(Argument.PRODUCT, 1));
    }

    @Override
    public boolean execute(String[] args) {
        var request = new Request(MessageType.ADD);
        request.userName = args[0];
        request.userPassword = args[1];
        return execute(request);
    }

    private Product fillInProduct() throws CommandInterruptionException {
        var inputService = commandManager.getInputService();
        var maxId = Long.MIN_VALUE;
        var products = commandManager.getProductsRepo().getProducts();

        lock.lock();
        try {
            for (var product : products) {
                maxId = Long.max(maxId, product.getId());
            }
        } finally {
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
            else
                product = (Product) request.requiredArguments.get(0);

            product.setId(commandManager.getDbProductManager().getNextId(SeqNames.productSeq).longValue());
            Optional<User> foundUser;
            lock.lock();
            try {
                var hashService = new HashingService();
                foundUser = commandManager.getUsersRepo().getUsers()
                        .stream()
                        .filter(u -> u.getPassword()
                                .equals(hashService.hash(request.userPassword))
                                && u.getName().equals(request.userName))
                        .findFirst();
            } finally {
                lock.unlock();
            }
            foundUser.ifPresent(product::setUser);

            if (product.getCoordinates() != null)
                product.getCoordinates().setId(commandManager.getDbProductManager().getNextId(SeqNames.coordSeq));
            if (product.getManufacturer() != null)
                product.getManufacturer().setId(commandManager.getDbProductManager().getNextId(SeqNames.orgSeq).longValue());

            if (!ValidatorService.validateProduct(product)) {
                var resp = new Response("product has not met validation criteria");
                sendToClient(resp, request);
                return true;
            }
            //commandManager.getUndoManager().logAddCommand(id);
            lock.lock();
            try {
                var products = commandManager.getProductsRepo().getProducts();
                var maxId = Long.MIN_VALUE;
                for (var prod : products) {
                    maxId = Long.max(maxId, prod.getId());
                }

                commandManager.getDbProductManager().insert(product);
                if (products.size() == 0) {
                    products.add(product);
                    var resp = new Response("product with id " + product.getId() + " added");
                } else if (products.get(products.size() - 1).getId() == maxId)
                    products.add(product);
                else
                    products.add(0, product);
                var response = new Response("product with id " + product.getId() + " added");
                sendToClient(response, request);
                return true;
            } finally {
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
