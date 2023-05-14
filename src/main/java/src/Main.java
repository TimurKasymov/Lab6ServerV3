package src;

import src.container.SettingsContainer;
import src.converters.SerializationManager;
import src.network_utils.ReceivingManager;
import src.network_utils.TCPServer;
import src.service.InputService;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) throws IOException {

        TCPServer server = null;
        try {
            SettingsContainer.loadSettings("settings.json");
            var settings = SettingsContainer.getSettings();
            var loader = new XmlFileHandler();
            var collection = new CollectionManager(loader);
            var commandManager = new CommandManager(new SerializationManager());
            var receivingManager = new ReceivingManager(new ReentrantLock());
            server = new TCPServer(settings.localPort, receivingManager, commandManager);
            receivingManager.setSessions(server.getSessions());
            server.start();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
        }
    }

}