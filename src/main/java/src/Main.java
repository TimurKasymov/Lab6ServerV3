package src;

import src.container.SettingsContainer;
import src.converters.SerializationManager;
import src.network_utils.ReceivingManager;
import src.network_utils.TCPServer;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    //  "db_user": "s368282",
    //  "db_psw": "myp2VgcTHHCiE9VO",
    //"db_url": "jdbc:postgresql://localhost:5432/tests4",
    public static void main(String[] args) throws IOException {

        TCPServer server = null;
        try {
            var str = File.listRoots()[0];
            //System.out.println(System.getProperty("user.dir"));
            SettingsContainer.loadSettings("settings.json");
            var settings = SettingsContainer.getSettings();
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