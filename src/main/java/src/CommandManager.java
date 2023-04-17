package src;

import org.apache.commons.lang3.tuple.Pair;
import src.converters.SerializationManager;
import src.loggerUtils.LoggerManager;
import src.models.Product;
import src.commands.*;
import src.commands.AddCommand;
import src.container.CommandsContainer;
import src.interfaces.*;
import src.interfaces.CollectionCustom;
import src.interfaces.Command;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.network_utils.SendingManager;
import src.service.InputService;
import src.utils.Argument;
import src.utils.Commands;

import java.io.File;
import java.nio.channels.SocketChannel;
import java.util.*;

public class CommandManager implements CommandManagerCustom {

    private RealUndoManager undoManager;
    private String currentScriptBeingExecuted;
    private HashMap<String, List<String>> executeScriptHandyMap;
    private CollectionCustom<Product> collectionManager = null;
    private final InputService inputService;
    private Scanner scanner;
    private SocketChannel clientChannel;
    private final HashMap<String, Command> commandsMap;
    private final SendingManager sendingManager;
    private final LinkedList<String> commandHistory;
    public Integer sendingToClientPort;
    private final SerializationManager serializationManager;

    /**
     * Constructor for making a src.src.CommandManager
     *
     * @param manager - collection with objects to manipulate
     */
    public CommandManager(CollectionCustom<Product> manager, InputService inputService, SerializationManager serializationManager) {
        this.collectionManager = manager;
        this.serializationManager = serializationManager;
        this.inputService = inputService;
        this.sendingManager = new SendingManager();
        commandHistory = new LinkedList<>();
        commandsMap = new HashMap<>();
        commandsMap.put("add", new AddCommand(this));
        commandsMap.put("clear", new ClearCommand(this));
        commandsMap.put("filter_greater_than_price", new FilterGreaterThanPriceCommand(this));
        commandsMap.put("print_unique_unit_of_measure", new PrintUniqueUnitOfMeasureCommand(this));
        commandsMap.put("remove_by_id", new RemoveByIdCommand(this));
        commandsMap.put("remove_first", new RemoveFirstCommand(this));
        commandsMap.put("reorder", new ReorderCommand(this));
        commandsMap.put("show", new ShowCommand(this));
        commandsMap.put("update_by_id", new UpdateByIdCommand(this));
        commandsMap.put("history", new HistoryCommand(this));
        commandsMap.put("help", new HelpCommand(this));
        commandsMap.put("info", new InfoCommand(this));
        commandsMap.put("execute_script", new ExecuteScriptCommand(this));
        commandsMap.put("filter_by_manufacture_cost", new FilterByManufactureCostCommand(this));
        commandsMap.put("undo_commands", new UndoCommand(this));
        CommandsContainer.setCommands(commandsMap.keySet().stream().toList());
        try {
            this.undoManager = new RealUndoManager(new File("product_logging.xml"), new File("command_logging.txt"), collectionManager);
        } catch (Exception e) {
            LoggerManager.getLogger(CommandManager.class).error("fatal error, logging files can not be created or opened");
            System.exit(0);
        }
    }

    public void setCurrentScriptBeingExecuted(String name) {
        this.currentScriptBeingExecuted = name;
    }

    public String getCurrentScriptBeingExecuted() {
        return this.currentScriptBeingExecuted;
    }

    @Override
    public int getClientPort(){
        return sendingToClientPort;
    }
    @Override
    public void setSendingToClientPort(Integer sendingToClientPort) {
        this.sendingToClientPort = sendingToClientPort;
    }

    public void setExecuteScriptHandyMap(HashMap<String, List<String>> executeScriptHandyMap) {
        this.executeScriptHandyMap = executeScriptHandyMap;
    }

    public HashMap<String, List<String>> getExecuteScriptHandyMap() {
        return executeScriptHandyMap;
    }

    public SocketChannel getClientChannel() {
        return clientChannel;
    }

    @Override
    public void setSocketChannel(SocketChannel socketChannel) {
        this.clientChannel = socketChannel;
    }

    public SendingManager getSendingManager() {
        return this.sendingManager;
    }

    @Override
    public SerializationManager getSerializationManager() {
        return this.serializationManager;
    }

    public Scanner getScanner() {
        return this.scanner;
    }

    public boolean executeCommand(String userInput) {
        var commandUnits = userInput.trim().toLowerCase().split(" ", 2);
        if (!commandsMap.containsKey(commandUnits[0])) {
            return false;
        }
        var enteredCommand = commandUnits[0].trim().toLowerCase();
        var command = commandsMap.get(enteredCommand);
        commandHistory.add(enteredCommand);
        command.execute(Arrays.copyOfRange(commandUnits, 1, commandUnits.length));
        return true;
    }

    /**
     * executes the command in userInput
     *
     * @return the execution was successful
     */
    public boolean executeCommand(Request request) {
        if (request.messageType == MessageType.ALL_AVAILABLE_COMMANDS) {
            var commandPlsArguments = new HashMap<String, List<Pair<Argument, Integer>>>();
            for (var comm : commandsMap.keySet()) {
                commandPlsArguments.put(comm, commandsMap.get(comm).getRequiredArguments());
            }
            var response = new Response();
            response.messageType = MessageType.ALL_AVAILABLE_COMMANDS;
            response.commandRequirements = commandPlsArguments;
            var data = serializationManager.serialize(response);
            sendingManager.send(data, getClientChannel(), sendingToClientPort);
            return true;
        }
        if (request.messageType == MessageType.LOAD_COLLECTION) {
            var successfully = collectionManager.load(new File((String) request.requiredArguments.get(0)));
            var response = new Response("collection was loaded successfully");
            var data = serializationManager.serialize(response);
            sendingManager.send(data, getClientChannel(), sendingToClientPort);
            return true;
        }
        var commandName = request.messageType;
        var command = commandsMap.get(commandName.getCommandDesc());
        commandHistory.add(commandName.getCommandDesc());
        var result = command.execute(request);
        collectionManager.save();
        undoManager.saveLoggingFiles();
        return result;
    }


    @Override
    public List<String> getCommandHistory() {
        return commandHistory;
    }

    @Override
    public List<String> getCommandsInfo() {
        var commandInfos = new ArrayList<String>(commandsMap.size());
        commandsMap.forEach((key, value) -> commandInfos.add(key + " - " + value.getInfo()));
        return commandInfos;
    }

    @Override
    public InputService getInputService() {
        return inputService;
    }

    @Override
    public CollectionCustom<Product> getCollectionManager() {
        return collectionManager;
    }

    @Override
    public RealUndoManager getUndoManager() {
        return undoManager;
    }

}
