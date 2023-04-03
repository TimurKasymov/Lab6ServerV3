package src.network.requests;

import src.utils.Commands;

public class RemoveByIdRequest extends Request {
  public final Long id;

  public RemoveByIdRequest(Long id) {
    super(Commands.REMOVE_BY_ID);
    this.id = id;
  }
}
