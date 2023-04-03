package src.network.requests;

import src.utils.Commands;

public class ClearRequest extends Request {
  public static String commandName = Commands.ADD;

  public ClearRequest() {
    super(Commands.CLEAR);
  }
}
