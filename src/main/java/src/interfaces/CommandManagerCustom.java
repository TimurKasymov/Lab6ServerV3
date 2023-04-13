package src.interfaces;

import src.converters.SerializationManager;
import src.models.Product;
import src.network.requests.Request;
import src.RealUndoManager;
import src.network_utils.SendingManager;
import src.service.InputService;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public interface CommandManagerCustom {
    /** executes given command */
    boolean executeCommand(Request userInput);
    /** executes given command */
    boolean executeCommand(String userInput);
    /** gets the history of executed src.commands */
    List<String> getCommandHistory();
    /** gets the info about each command */
    List<String> getCommandsInfo();
    Scanner getScanner();

        /** gets collection manager */
    CollectionCustom<Product> getCollectionManager();
    /** gets undoManager */
    RealUndoManager getUndoManager();
    public InputService getInputService();
    SendingManager getSendingManager();
    SerializationManager getSerializationManager();
    SocketChannel getClientChannel();
    void setExecuteScriptHandyMap(HashMap<String, List<String>> executeScriptHandyMap);
    HashMap<String, List<String>> getExecuteScriptHandyMap();
    void setSocketChannel(SocketChannel socketChannel);
    void setCurrentScriptBeingExecuted(String name);
    String getCurrentScriptBeingExecuted();
}
