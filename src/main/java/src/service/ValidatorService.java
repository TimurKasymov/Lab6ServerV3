package src.service;

import src.models.Product;

public class ValidatorService {

    public static synchronized boolean validateProduct(Product product){
        if(product.getId() == null || product.getName() == null || product.getCoordinates() == null
                || product.getManufactureCost() == null)
            return false;
        if(product.getPrice() < 1)
            return false;
        var coor = product.getCoordinates();
        if(coor.getX() == null)
            return false;
        if(product.getManufacturer() != null){
            var man = product.getManufacturer();
            if(man.getAnnualTurnover() == null || man.getAnnualTurnover() < 1)
                return false;
            if(man.getId() == null || man.getId() < 1)
                return false;
            if(man.getName() == null || man.getOrganizationType() == null)
                return false;
        }
        return true;
    }
}
