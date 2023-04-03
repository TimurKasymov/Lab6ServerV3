package src.network.responses;

import src.models.Product;
import src.utils.Commands;

import java.util.LinkedList;
import java.util.List;

public class FilterGreaterThanPriceResponse extends Response{

    public FilterGreaterThanPriceResponse(String comment, String error) {
        super(Commands.FILTER_GREATER_THAN_PRICE, error);
        products = new LinkedList<>();
        this.comment = comment;
    }

    public String comment;
    private final List<Product> products;
    public void add(Product product){
        this.products.add(product);
    }
    public List<Product> getProducts(){
        return products;
    }
}
