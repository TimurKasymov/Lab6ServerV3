package src.network_utils;

import src.converters.SerializationManager;
import src.interfaces.CommandManagerCustom;
import src.loggerUtils.LoggerManager;
import src.network.requests.Request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;


public class TCPServer {
    private final int port;
    private HashSet<SocketChannel> sessions;
    private ReceivingManager receivingManager;
    private CommandManagerCustom commandManager;
    private SendingManager sendingManager;
    private SerializationManager serializationManager;
    private Selector selector;

    public TCPServer(int port, ReceivingManager receivingManager, CommandManagerCustom commandManager) {
        this.port = port;
        this.receivingManager = receivingManager;
        this.commandManager = commandManager;
        this.sendingManager = new SendingManager();
        this.serializationManager = new SerializationManager();
        this.sessions = new HashSet<>();
    }

    public HashSet<SocketChannel> getSessions() {
        return sessions;
    }

    public void stop() throws IOException {
        for (var se: sessions
             ) {
            se.close();
        }
    }
    public void start() {
        try {
            this.selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            var socketAddress = new InetSocketAddress("localhost", port);
            serverSocketChannel.bind(socketAddress, port);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server started...");
            while (true) {
                // blocking, wait for events
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) continue;
                    if (key.isAcceptable()) accept(key);
                    else if (key.isReadable()) {
                        var result = receivingManager.read(key);
                        if(result == null)
                            continue;
                        Request request;
                        // object is done being transferred
                        var res = result.getLeft();
                        int p = 1023;
                        if (res == null)
                            continue;
                        for (int i = res.length - 1; i > -1; i--) {
                            if (res[i] != 0) {
                                p = i;
                                break;
                            }
                        }
                        var cutres = Arrays.copyOfRange(res, 0, p+1);
                        var  obj =  serializationManager.deserialize(cutres);
                        if ((Request)obj != null) {
                            request = (Request)obj;
                            commandManager.setSocketChannel(result.getRight());
                            var response = commandManager.executeCommand(request);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LoggerManager.getLogger(TCPServer.class).error(e.getMessage());
        }
    }

    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            channel.register(this.selector, SelectionKey.OP_READ);
            this.sessions.add(channel);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleRequest() {

    }
}
