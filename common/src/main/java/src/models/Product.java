package src.models;

import src.DateTimeAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.NONE)
public class Product implements Comparable<Product>, Serializable {

    public Product(){}
    public Product(Long id, String name, Coordinates coordinates,
                   float price, Double manufactureCost, UnitOfMeasure unitOfMeasure, Organization manufacturer){
        this.id = id;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
        this.price = price;
        this.manufactureCost = manufactureCost;
        this.unitOfMeasure = unitOfMeasure;
        this.name = name;
        this.manufacturer = manufacturer;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public Coordinates getCoordinates(){
        return coordinates;
    }
    public LocalDateTime getCreationDate(){
        return creationDate;
    }
    public float getPrice(){
        return price;
    }
    public Double getManufactureCost(){
        return manufactureCost;
    }
    public UnitOfMeasure getUnitOfMeasure(){
        return unitOfMeasure;
    }
    public Organization getManufacturer(){
        return manufacturer;
    }
    @XmlElement
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    @XmlElement
    private String name; //Поле не может быть null, Строка не может быть пустой
    @XmlElement
    private Coordinates coordinates; //Поле не может быть null
    @XmlElement
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @XmlElement
    private float price; //Значение поля должно быть больше 0
    @XmlElement
    private Double manufactureCost; //Поле не может быть null
    @XmlElement
    private UnitOfMeasure unitOfMeasure; //Поле может быть null
    @XmlElement
    private Organization manufacturer; //Поле может быть null

    @Override
    public int compareTo(Product prod) {
        return (int)(prod.getId() - getId());
    }

    @Override
    public String toString(){
        var output = "id: " + id + "\n" +
        "name: " + name + "\n" +
        "coordinates:\n" +
        " ".repeat(2) + "X: " + coordinates.getX() + "\n" +
         " ".repeat(2) + "Y: " + coordinates.getY() + "\n" +
        "creation date: " + creationDate.format(DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm")) + "\n" +
                "price: " + price + "\n" + "manufacture cost: " + manufactureCost;

        if(unitOfMeasure != null){
            output += "\n" + "unit of measure: " + unitOfMeasure.toString();
        }
        if(manufacturer != null){
            output += "\n" + "organization: " + "\n" +
            " ".repeat(2) + "id: " + manufacturer.getId() + "\n" +
                    " ".repeat(2) + "name: " + manufacturer.getName() + "\n" +
                    " ".repeat(2) + "annual turnover: " + manufacturer.getAnnualTurnover() + "\n" +
                    " ".repeat(2) + "organization type: " + manufacturer.getOrganizationType().toString();
        }
        return output;
    }
}
