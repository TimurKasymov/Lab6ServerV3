package src;

import org.apache.commons.lang3.tuple.Pair;
import src.Repositories.DI.ProductRepo;
import src.Repositories.DI.UserRepo;
import src.container.SettingsContainer;
import src.converters.SerializationManager;
import src.db.DI.DbCollectionManager;
import src.db.ProductCollectionInDbManager;
import src.db.SeqNames;
import src.db.TableManager;
import src.db.UserCollectionInDbManager;
import src.models.Product;
import src.commands.*;
import src.commands.AddCommand;
import src.container.CommandsContainer;
import src.interfaces.*;
import src.interfaces.Command;
import src.models.Role;
import src.models.User;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.network_utils.SendingManager;
import src.service.Auth.AuthenticationManager;
import src.service.HashingService;
import src.service.InputService;
import src.utils.Argument;
import src.utils.Commands;

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
    private final InputService inputService;
    private String currentScriptBeingExecuted;
    private HashMap<String, List<String>> executeScriptHandyMap;
    private Scanner scanner;
    private final HashMap<String, Command> commandsMap;
    private final SendingManager sendingManager;
    private final List<String> commandHistory;
    private final SerializationManager serializationManager;
    private final ExecutorService executorService;
    private final Lock lock;
    private UserRepo userRepo;

    private ProductRepo prodRepo;

    private final HashingService hashingService;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor for making a src.src.CommandManager
     */
    public CommandManager(SerializationManager serializationManager) {
        this.hashingService = new HashingService();
        this.lock = new ReentrantLock();
        this.inputService = new InputService();
        this.serializationManager = serializationManager;
        this.sendingManager = new SendingManager();
        commandHistory = Collections.synchronizedList(new LinkedList<String>());
        executorService = Executors.newFixedThreadPool(3);
        commandsMap = new HashMap<>();
        commandsMap.put(Commands.ADD, new AddCommand(this));
        commandsMap.put(Commands.CLEAR, new ClearCommand(this));
        commandsMap.put(Commands.FILTER_GREATER_THAN_PRICE, new FilterGreaterThanPriceCommand(this));
        commandsMap.put(Commands.PRINT_UNIQUE_UNIT_OF_MEASURE, new PrintUniqueUnitOfMeasureCommand(this));
        commandsMap.put(Commands.REMOVE_BY_ID, new RemoveByIdCommand(this));
        commandsMap.put(Commands.REMOVE_FIRST, new RemoveFirstCommand(this));
        commandsMap.put(Commands.REORDER, new ReorderCommand(this));
        commandsMap.put(Commands.SHOW, new ShowCommand(this));
        commandsMap.put(Commands.UPDATE_BY_ID, new UpdateByIdCommand(this));
        commandsMap.put(Commands.HISTORY, new HistoryCommand(this));
        commandsMap.put(Commands.HELP, new HelpCommand(this));
        commandsMap.put(Commands.INFO, new InfoCommand(this));
        commandsMap.put(Commands.EXECUTE_SCRIPT, new ExecuteScriptCommand(this));
        commandsMap.put(Commands.FILTER_BY_MANUFACTURE_COST, new FilterByManufactureCostCommand(this));
        commandsMap.put(Commands.SHOW_USERS, new ShowUsersCommand(this));
        commandsMap.put(Commands.ASSIGN_ROLE, new AssignRoleCommand(this));

        CommandsContainer.setCommands(commandsMap.keySet().stream().toList());
        productDbCollectionManager = new ProductCollectionInDbManager();
        userDbCollectionManager = new UserCollectionInDbManager();
        TableManager.ensureTablesExist();
        prodRepo = new src.Repositories.ProductRepo(productDbCollectionManager.load());
        userRepo = new src.Repositories.UserRepo(userDbCollectionManager.load());
        this.authenticationManager = new AuthenticationManager(userRepo.getUsers(), userDbCollectionManager);
    }

    @Override
    public InputService getInputService() {
        return inputService;
    }

    @Override
    public LocalDateTime getInitializationTime() {
        return initializationTime;
    }

    @Override
    public UserRepo getUsersRepo() {
        return userRepo;
    }

    @Override
    public DbCollectionManager<Product> getDbProductManager() {
        return productDbCollectionManager;
    }

    @Override
    public DbCollectionManager<User> getDbUserManager() {
        return userDbCollectionManager;
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

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
        var commandUnits = userInput.trim().toLowerCase().split(" ");
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

        var wasAuthenticated = authenticationManager
                .authenticate(request.userName, request.userPassword, request.createNewUser);
        var sending = false;
        Response authResponse = null;
        if (request.messageType == MessageType.SIGNUP) {
            var id = userDbCollectionManager.getNextId(SeqNames.userSeq);
            var user = new User(id, hashingService.hash(request.userPassword), request.userName);
            user.role = Role.MIN_USER;
            userRepo.getUsers().add(user);
            userDbCollectionManager.insert(user);
            authResponse = new Response("You are signed up");
            sending = true;
        } else if (request.messageType == MessageType.LOGIN) {
            if (wasAuthenticated)
                authResponse = new Response("you are logged in");
            else
                authResponse = new Response("password or user name do not match, try again");
            sending = true;
        } else {
            if (!wasAuthenticated) {
                authResponse = new Response("the credentials you are trying to log in with " +
                        "are not correct, try again");
                sending = true;
            }
        }
        if (sending) {
            if(wasAuthenticated)
                authResponse.messageType = MessageType.LOGGED;
            else
                authResponse.messageType = MessageType.LOGGING_FAILED;
            var data = serializationManager.serialize(authResponse);
            sendingManager.send(data, request.interlayerChannel, request.clientPort);
            return;
        }

        // if it is the last server that accessed the db then do not reload collection
        // otherwise reload to get new products inserted by other servers
        lock.lock();
        try {
            if (!productDbCollectionManager.isThisLastServerToTouchDB(SettingsContainer.getSettings().localPort)) {
                productDbCollectionManager.markThatThisServerHasMadeChangesToDb();
                prodRepo = new src.Repositories.ProductRepo(productDbCollectionManager.load());
                userRepo = new src.Repositories.UserRepo(userDbCollectionManager.load());
            }
        } finally {
            lock.unlock();
        }

        // sync
        if (request.messageType == MessageType.LOAD_COLLECTION) {
            var response = new Response("collection was loaded successfully");
            var data = serializationManager.serialize(response);
            sendingManager.send(data, request.interlayerChannel, request.clientPort);
            return;
        }
        // sync
        var commandName = request.messageType;
        var command = commandsMap.get(commandName.getCommandDesc());
        var foundUser = userRepo.getUser(request);
        if (foundUser.isEmpty())
            return;
        if (command.isAllowedToExecute(foundUser.get().role))
            commandHistory.add(commandName.getCommandDesc());
        else
        {
            var response = new Response("you have got no rights honey");
            var data = serializationManager.serialize(response);
            sendingManager.send(data, request.interlayerChannel, request.clientPort);
            return;
        }
        var result = command.execute(request);
    }

    @Override
    public ProductRepo getProductsRepo() {
        return prodRepo;
    }


    @Override
    public List<String> getCommandHistory() {
        return commandHistory;
    }

    @Override
    public List<String> getCommandsInfo(Role role) {
        var commandInfos = new ArrayList<String>(commandsMap.size());
        commandsMap.forEach((key, value) -> {
                    if (value.isAllowedToExecute(role))
                        commandInfos.add(key + " - " + value.getInfo());
                }
        );
        return commandInfos;
    }

}
