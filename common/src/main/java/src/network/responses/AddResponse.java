package src.network.responses;

import src.utils.Commands;

public class AddResponse extends Response {
  public final Long newId;
  public String message;
  public AddResponse(Long newId, String message, String error) {
    super(Commands.ADD, error);
    this.message = message;
    this.newId = newId;
  }
}
