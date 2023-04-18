package src;

import src.container.SettingsContainer;
import src.converters.SerializationManager;
import src.network_utils.ReceivingManager;
import src.network_utils.TCPServer;
import src.service.InputService;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {

        TCPServer server = null;
        try {
            SettingsContainer.loadSettings("settings.json");
            var settings = SettingsContainer.getSettings();
            var loader = new XmlFileHandler();
            var collection = new CollectionManager(loader);
            var commandManager = new CommandManager(collection, new InputService(), new SerializationManager());
            var receivingManager = new ReceivingManager();
            server = new TCPServer(settings.localPort, receivingManager, commandManager);
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