package src.network.requests;

import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public abstract class Request implements Serializable {
  public String commandName;
  private boolean executingScript = false;

  private void switchToExecutingFromScript() {executingScript = true;}

  public boolean isBeingExecutedFromScript(){return executingScript;}

  public Request(String name) {
    this.commandName = name;
  }

  public String getCommandName() {
    return commandName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Request response = (Request) o;
    return Objects.equals(commandName, response.commandName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commandName);
  }

  @Override
  public String toString() {
    return "Request{" +
      "name='" + commandName + '\'' +
      '}';
  }


}
