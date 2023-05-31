package src.interfaces;

import src.Repositories.DI.ProductRepo;
import src.Repositories.DI.UserRepo;
import src.converters.SerializationManager;
import src.db.DI.DbCollectionManager;
import src.models.Product;
import src.models.Role;
import src.models.User;
import src.network.Request;
import src.network_utils.SendingManager;
import src.service.InputService;

import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;


public interface CommandManagerCustom {
    /** executes given command */
    void executeCommand(Request userInput);
    ProductRepo getProductsRepo();
    /** executes given command */
    void executeCommand(String userInput);
    /** gets the history of executed src.commands */
    List<String> getCommandHistory();
    /** gets the info about each command */
    List<String> getCommandsInfo(Role role);
    Scanner getScanner();
    SendingManager getSendingManager();
    SerializationManager getSerializationManager();
    void setExecuteScriptHandyMap(HashMap<String, List<String>> executeScriptHandyMap);
    HashMap<String, List<String>> getExecuteScriptHandyMap();
    void setCurrentScriptBeingExecuted(String name);
    String getCurrentScriptBeingExecuted();
    ExecutorService getExecutorService();
    InputService getInputService();
    LocalDateTime getInitializationTime();
    UserRepo getUsersRepo();
    DbCollectionManager<Product> getDbProductManager();
    DbCollectionManager<User> getDbUserManager();
}
