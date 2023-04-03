package src.network.requests;

import src.models.Product;
import src.utils.Commands;

public class UpdateByIdRequest extends Request{

    public Product productToUpdate;
    public UpdateByIdRequest(Product prod) {
        super(Commands.UPDATE_BY_ID);
        this.productToUpdate = prod;
    }
}
