package src.network.requests;

import src.utils.Commands;

public class FilterGreaterThanPriceRequest extends Request{

    private float price;

    public float getPrice() {
        return price;
    }

    public FilterGreaterThanPriceRequest(float price) {
        super(Commands.FILTER_GREATER_THAN_PRICE);
    }
}
