package src.network.responses;

import src.utils.Commands;

public class ClearResponse extends Response {
  public ClearResponse(String error) {
    super(Commands.CLEAR, error);
  }
}
