package src.network_utils;

import src.loggerUtils.LoggerManager;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import com.google.common.primitives.Bytes;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class ReceivingManager {

    private HashSet<SocketChannel> sessions = new HashSet<>();
    private final HashMap<Integer, byte[]> receivedData;
    public Integer comingFromClientPort;

    public ReceivingManager(){
        this.receivedData = new HashMap<>();
    }

    public void setSessions(HashSet<SocketChannel> sessions){
        this.sessions = sessions;
    }
    public Pair<byte[], SocketChannel> read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int numRead = channel.read(byteBuffer);
            if (numRead == -1) {
                this.sessions.remove(channel);
                LoggerManager.getLogger(TCPServer.class)
                        .info("client " + channel.socket().getRemoteSocketAddress() + " disconnected");
                channel.close();
                key.cancel();
                return null;
            }
            //being sent from server
            var strB = new StringBuilder();
            for(int i = 0; i < 5; i++){
                strB.append(((Byte) byteBuffer.array()[numRead-2-i]));
            }
            comingFromClientPort = Integer.parseInt(strB.reverse().toString());
            if(!receivedData.containsKey(comingFromClientPort)){
                receivedData.put(comingFromClientPort, Arrays.copyOf(byteBuffer.array(), byteBuffer.array().length - 6));
            }
            else{
                var arr = receivedData.get(comingFromClientPort);
                arr = Bytes.concat(arr, Arrays.copyOf(byteBuffer.array(), byteBuffer.array().length - 6));
            }
            // reached the end of the object being sent
            if(byteBuffer.array()[numRead-1] == 1){
                var pair = ImmutablePair.of(receivedData.get(comingFromClientPort), channel);
                receivedData.remove(comingFromClientPort);
                return pair;
            }
        }
        catch (IOException e){
            LoggerManager.getLogger(TCPServer.class).error(e.getMessage());
            // server has crashed, and we got isConnectionPending - true and isOpen - true as well
            // and so we close the connection, because this connection is already closed by the server and create another one
            if(Objects.equals(e.getMessage(), "Connection reset"))
            {
                try{
                    Thread.sleep(3000);
                    channel.close();
                }
                catch (Exception e1){
                    LoggerManager.getLogger(TCPServer.class).error(e.getMessage());
                }
            }
        }
        return null;
    }
}
