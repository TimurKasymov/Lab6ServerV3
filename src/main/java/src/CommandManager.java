package src;

import org.apache.commons.lang3.tuple.Pair;
import src.container.SettingsContainer;
import src.converters.SerializationManager;
import src.db.DI.DbCollectionManager;
import src.models.Product;
import src.commands.*;
import src.commands.AddCommand;
import src.container.CommandsContainer;
import src.interfaces.*;
import src.interfaces.CollectionCustom;
import src.interfaces.Command;
import src.models.User;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.network_utils.SendingManager;
import src.service.InputService;
import src.utils.Argument;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CommandManager implements CommandManagerCustom {

    public LocalDateTime initializationTime = LocalDateTime.now();
    private DbCollectionManager<Product> productDbCollectionManager;
    private DbCollectionManager<User> userDbCollectionManager;
    private List<Product> products;
    private InputService inputService;
    private List<User> users;
    private String currentScriptBeingExecuted;
    private HashMap<String, List<String>> executeScriptHandyMap;
    private Scanner scanner;
    private final HashMap<String, Command> commandsMap;
    private final SendingManager sendingManager;
    private final LinkedList<String> commandHistory;
    private final SerializationManager serializationManager;
    private final ExecutorService executorService;
    private final Lock lock;
    /**
     * Constructor for making a src.src.CommandManager
     *
     */
    public CommandManager(SerializationManager serializationManager) {
        this.lock = new ReentrantLock();
        this.inputService = new InputService();
        this.serializationManager = serializationManager;
        this.sendingManager = new SendingManager();
        commandHistory = (LinkedList<String>)Collections.synchronizedCollection(new LinkedList<String>());
        executorService = Executors.newFixedThreadPool(3);
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
        /*try {
            this.undoManager = new RealUndoManager(new File("product_logging.xml"), new File("command_logging.txt"), collectionManager);
        } catch (Exception e) {
            LoggerManager.getLogger(CommandManager.class).error("fatal error, logging files can not be created or opened");
            System.exit(0);
        }
         */
    }

    @Override
    public InputService getInputService(){
        return inputService;
    }

    @Override
    public LocalDateTime getInitializationTime() {
        return initializationTime;
    }

    @Override
    public DbCollectionManager<Product> getDbProductManager() {
        return productDbCollectionManager;
    }

    @Override
    public DbCollectionManager<User> getDbUserManager() {
        return userDbCollectionManager;
    }

    @Override
    public LinkedList<Product> getProducts(){
        return (LinkedList<Product>)this.products;
    }
    public ExecutorService getExecutorService() {return this.executorService;}
    public void setCurrentScriptBeingExecuted(String name) {
        this.currentScriptBeingExecuted = name;
    }

    public String getCurrentScriptBeingExecuted() {
        return this.currentScriptBeingExecuted;
    }

    public void setExecuteScriptHandyMap(HashMap<String, List<String>> executeScriptHandyMap) {
        this.executeScriptHandyMap = executeScriptHandyMap;
    }

    public HashMap<String, List<String>> getExecuteScriptHandyMap() {
        return executeScriptHandyMap;
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

    // sync
    public void executeCommand(String userInput) {
        var commandUnits = userInput.trim().toLowerCase().split(" ", 2);
        if (!commandsMap.containsKey(commandUnits[0])) {
            return;
        }
        var enteredCommand = commandUnits[0].trim().toLowerCase();
        var command = commandsMap.get(enteredCommand);
        commandHistory.add(enteredCommand);
        command.execute(Arrays.copyOfRange(commandUnits, 1, commandUnits.length));
        return;
    }

    /**
     * executes the command in userInput
     *
     * @return the execution was successful
     */
    public void executeCommand(Request request) {
        // if it is the last server that accessed the db then do not reload collection
        // otherwise reload to get new products inserted by other servers
        lock.lock();
        try{
            if(! productDbCollectionManager.isThisLastServerToTouchDB(SettingsContainer.getSettings().localPort)){
                productDbCollectionManager.markThatThisServerHasMadeChangesToDb();
                products = productDbCollectionManager.load();
                users = userDbCollectionManager.load();
            }
        }finally {
            lock.unlock();
        }
        // sync
        if (request.messageType == MessageType.ALL_AVAILABLE_COMMANDS) {
            var commandPlsArguments = new HashMap<String, List<Pair<Argument, Integer>>>();
            for (var comm : commandsMap.keySet()) {
                commandPlsArguments.put(comm, commandsMap.get(comm).getRequiredArguments());
            }
            var response = new Response();
            response.messageType = MessageType.ALL_AVAILABLE_COMMANDS;
            response.commandRequirements = commandPlsArguments;
            var data = serializationManager.serialize(response);
            sendingManager.send(data, request.interlayerChannel, request.clientPort);
            return;
        }
        // sync
        if (request.messageType == MessageType.LOAD_COLLECTION) {
            //var successfully = collectionManager.load(new File((String) request.requiredArguments.get(0)));
            var response = new Response("collection was loaded successfully");
            var data = serializationManager.serialize(response);
            sendingManager.send(data, request.interlayerChannel, request.clientPort);
            return;
        }
        // sync
        var commandName = request.messageType;
        var command = commandsMap.get(commandName.getCommandDesc());
        commandHistory.add(commandName.getCommandDesc());
        var result = command.execute(request);
        // collectionManager.save();
        //undoManager.saveLoggingFiles();
        return;
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

}
