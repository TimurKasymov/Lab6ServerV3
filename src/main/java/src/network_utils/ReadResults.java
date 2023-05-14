package src.network_utils;

import java.nio.channels.SocketChannel;

public class ReadResults {
    public SocketChannel socketChannel;
    public byte[] data;
    public int port;

    public ReadResults(SocketChannel socketChannel,
                       byte[] data,
                       int port) {
        this.data = data;
        this.socketChannel = socketChannel;
        this.port = port;
    }
}
