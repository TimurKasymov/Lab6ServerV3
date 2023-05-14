package src.network;


import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class Request implements Serializable {
    public transient SocketChannel interlayerChannel;
    public transient int clientPort;
    public MessageType messageType;
    public List<Object> requiredArguments;

    public Request(MessageType messageType){
        this.messageType = messageType;
        requiredArguments = new LinkedList<>();
    }
}
