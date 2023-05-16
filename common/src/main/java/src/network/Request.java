package src.network;


import java.io.Serial;
import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567L;
    public transient SocketChannel interlayerChannel;
    public transient int clientPort;
    public MessageType messageType;
    public List<Object> requiredArguments;
    public String userPassword;
    public String userName;
    public boolean createNewUser = false;
    public Request(MessageType messageType){
        this.messageType = messageType;
        requiredArguments = new LinkedList<>();
    }
}
