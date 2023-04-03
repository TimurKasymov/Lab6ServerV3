package src.commands;

import src.loggerUtils.LoggerManager;
import src.models.Product;
import src.network.requests.UpdateByIdRequest;
import src.network.responses.UpdateByIdResponse;
import org.slf4j.Logger;
import src.exceptions.CommandInterruptionException;
import src.exceptions.InterruptionCause;
import src.interfaces.Command;
import src.interfaces.CommandManagerCustom;
import src.network.requests.Request;
import src.service.InputService;

import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

/**
 * Class for updating the element by it`s ID
 */
public class UpdateByIdCommand extends CommandBase implements Command {
    private final InputService inputService;
    private final Logger logger;

    {
        inputService = commandManager.getInputService();

    }

    public UpdateByIdCommand(CommandManagerCustom commandManager) {
        super(commandManager);
        logger = LoggerManager.getLogger(UpdateByIdRequest.class);
    }

    @Override
    public boolean execute(String[] args) {
        try {
            var products = commandManager.getCollectionManager().get();
            var name = inputService.inputName();
            var coord = inputService.inputCoordinates();
            var price = inputService.inputPrice();
            var manufCost = inputService.inputManufactureCost();
            var unit = inputService.inputUnitOfMeasure();

            int yesOrNo = 0;
            for (; ; ) {
                try {
                    Scanner scanner = new Scanner(System.in);
                    yesOrNo = scanner.nextInt();
                    break;
                } catch (InputMismatchException e) {
                }
            }
            var prod = new Product(Long.parseLong(args[0]), name, coord, price, manufCost,
                    unit, yesOrNo == 1 ? inputService.inputOrganization(products) : null);

            return execute(new UpdateByIdRequest(prod));

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
    public boolean execute(Request request) {
        var resp = new UpdateByIdResponse(null);

        try {
            var prod = ((UpdateByIdRequest) request).productToUpdate;
            var id = prod.getId();
            var products = commandManager.getCollectionManager().get();
            if (id <= 0) {
                resp.setError("ID must be an number greater than 0. Try typing this command again");
                sendToClient(resp);
                return false;
            }
            logger.info("updating product with id: " + id);
            for (Product product : products) {
                Long intId = product.getId();
                if (Objects.equals(intId, id)) {
                    resp.setMessageForClient("Element was updated successfully");
                    sendToClient(resp);
                    return true;
                }
            }
        } catch (NumberFormatException exception) {
            resp.setError("ID must be an number. Try typing this command again");
        }
        resp.setError("Element with this ID is not defined. Try again.");
        sendToClient(resp);
        return true;
    }

    @Override
    public String getInfo() {
        return "update the element`s value, whose ID is equal to the given." +
                " You should enter ID after entering a command.";
    }
}
