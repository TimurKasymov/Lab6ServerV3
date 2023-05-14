package src.network_utils;

import src.converters.SerializationManager;
import src.interfaces.CommandManagerCustom;
import src.loggerUtils.LoggerManager;
import src.network.Request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class TCPServer {
    private final int port;
    private HashSet<SocketChannel> sessions;
    private ReceivingManager receivingManager;
    private CommandManagerCustom commandManager;
    private SendingManager sendingManager;
    private SerializationManager serializationManager;
    private Selector selector;
    private ForkJoinPool forkJoinPool;
    private ExecutorService executorService;

    public TCPServer(int port, ReceivingManager receivingManager, CommandManagerCustom commandManager) {
        executorService = Executors.newFixedThreadPool(3);
        forkJoinPool = new ForkJoinPool();
        this.port = port;
        this.receivingManager = receivingManager;
        this.commandManager = commandManager;
        this.sendingManager = new SendingManager();
        this.serializationManager = new SerializationManager();
        this.sessions = (HashSet<SocketChannel>)Collections.synchronizedSet(new HashSet<SocketChannel>());
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            try {
                System.out.println("closing selector...");
                selector.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }));
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
                        executorService.submit(() -> receivingManager.read(key, this::receivedRequestHandler));
                    }
                }
            }
        } catch (IOException e) {
            LoggerManager.getLogger(TCPServer.class).error(e.getMessage());
        }
    }

    public void receivedRequestHandler(ReadResults result){
        if(result == null)
            return;
        Request request;
        // object is done being transferred
        var res = result.data;
        int p = 1023;
        if (res == null)
            return;
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
            request.clientPort = result.port;
            request.interlayerChannel = result.socketChannel;
            forkJoinPool.submit(()-> commandManager.executeCommand(request));
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
}
