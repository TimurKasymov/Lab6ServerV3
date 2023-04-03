package src.network.responses;

import src.models.Product;
import src.utils.Commands;

import java.util.LinkedList;
import java.util.List;

public class FilterByManufactureCostResponse extends Response {
    private final List<Product> products;
    public FilterByManufactureCostResponse(String error) {
        super(Commands.FILTER_BY_MANUFACTURE_COST, error);
        products = new LinkedList<>();
    }
    public void add(Product product){
        this.products.add(product);
    }
    public List<Product> getProducts(){
        return products;
    }
}
