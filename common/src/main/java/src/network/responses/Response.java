package src.network.responses;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Objects;

public abstract class Response implements Serializable {
  private final String name;
  protected String messageForClient = null;
  private String error;
  private SocketAddress clientSocketChannel;

  public Response(String name, String error) {
    this.name = name;
    this.error = error;
  }
  public SocketAddress getClientSocketChannel(){
    return clientSocketChannel;
  }
  public void setClientSocketChannel(SocketAddress socketChannel){
    clientSocketChannel = socketChannel;
  }

  public String getName() {
    return name;
  }

  public String getError() {
    return error;
  }
  public void setError(String err){
    this.error= err;
  }
  public String getMessageForClient() {
    return messageForClient;
  }
  public void setMessageForClient(String messageForClient) {
    this.messageForClient = messageForClient;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Response response = (Response) o;
    return Objects.equals(name, response.name) && Objects.equals(error, response.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, error);
  }

  @Override
  public String toString() {
    return "Response{" +
      "name='" + name + '\'' +
      ", error='" + error + '\'' +
      '}';
  }
}
