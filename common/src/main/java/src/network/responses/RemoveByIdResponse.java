package src.network.responses;

import src.utils.Commands;

public class RemoveByIdResponse extends Response {
  public RemoveByIdResponse(String error) {
    super(Commands.REMOVE_BY_ID, error);
  }
}
