package src.network_utils;

import src.loggerUtils.LoggerManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import com.google.common.primitives.Bytes;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReceivingManager {

    private Set<SocketChannel> sessions;
    private final HashMap<Integer, byte[]> receivedData;
    private final Lock lock;

    public ReceivingManager(ReentrantLock lock){
        this.receivedData = new HashMap<>();
        this.lock = lock;
    }

    public void setSessions(Set<SocketChannel> sessions){
        this.sessions = sessions;
    }
    public void read(SelectionKey key, ReceivedRequestHandlerFuncInterface methodToCallOnRequestBeingDoneTransferring) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int numRead = channel.read(byteBuffer);
            if (numRead == -1) {
                lock.lock();
                try{
                    this.sessions.remove(channel);
                    LoggerManager.getLogger(TCPServer.class)
                            .info("client " + channel.socket().getRemoteSocketAddress() + " disconnected");
                    channel.close();
                    key.cancel();
                }
                finally {
                    lock.unlock();
                }
                return;
            }
            //being sent from server
            var strB = new StringBuilder();
            for(int i = 0; i < 5; i++){
                strB.append(((Byte) byteBuffer.array()[numRead-2-i]));
            }
            lock.lock();
            try{
                var comingFromClientPort = Integer.parseInt(strB.reverse().toString());
                if(!receivedData.containsKey(comingFromClientPort)){
                    receivedData.put(comingFromClientPort, Arrays.copyOf(byteBuffer.array(), byteBuffer.array().length - 6));
                }
                else{
                    var arr = receivedData.get(comingFromClientPort);
                    receivedData.put(comingFromClientPort, Bytes.concat(arr, Arrays.copyOf(byteBuffer.array(), byteBuffer.array().length - 6)));
                }
                // четко
                // reached the end of the object being sent
                if(byteBuffer.array()[numRead-1] == 1){
                    var readResults = new ReadResults(channel, receivedData.get(comingFromClientPort), comingFromClientPort);
                    receivedData.remove(comingFromClientPort);
                    methodToCallOnRequestBeingDoneTransferring.receivedRequestHandler(readResults);
                    return;
                }
            }
            finally {
                lock.unlock();
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
        return;
    }
}
