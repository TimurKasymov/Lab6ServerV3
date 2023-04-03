package src.network.requests;

import src.utils.Commands;

public class FilterByManufactureCostRequest extends Request {

    private Double cost;

    public Double getCost() {
        return cost;
    }

    public FilterByManufactureCostRequest(double cost) {
        super(Commands.FILTER_BY_MANUFACTURE_COST);
        this.cost = cost;
    }
}
