package src.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "products")
@XmlAccessorType (XmlAccessType.NONE)
public class Products implements Serializable {

    @XmlElement(name = "product")
    private List<Product> products = null;

    public LinkedList<Product> getProducts() {
        return new LinkedList<Product>(products);
    }

    public void setProducts(List<Product> persons) {
        this.products = persons;
    }

}


