package src;

import src.converters.SerializationManager;
import src.network_utils.ReceivingManager;
import src.network_utils.TCPServer;
import src.service.InputService;

import java.io.*;

public class Main {

    private static final int PORT = 23586;

    public static void main(String[] args) throws IOException {

        TCPServer server = null;
        try {
            var loader = new XmlFileHandler();
            var collection = new CollectionManager(loader);
            var commandManager = new CommandManager(collection, new InputService(), new SerializationManager());
            var receivingManager = new ReceivingManager();
            server = new TCPServer(PORT, receivingManager, commandManager);
            receivingManager.setSessions(server.getSessions());
            server.start();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
        }
    }

}