package src.network.requests;

import src.models.Product;
import src.utils.Commands;

public class AddRequest extends Request {
  public Product product;
  public static String commandName = Commands.ADD;

  public AddRequest(Product product) {
    super(Commands.ADD);
    this.product = product;
  }
}

