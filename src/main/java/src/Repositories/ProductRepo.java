package src.Repositories;

import src.models.Product;

import java.util.List;

public class ProductRepo implements src.Repositories.DI.ProductRepo {
    private final List<Product> products;

    public ProductRepo(List<Product> products) {
        this.products = products;
    }

    @Override
    public List<Product> getProducts() {
        return products;
    }
}
