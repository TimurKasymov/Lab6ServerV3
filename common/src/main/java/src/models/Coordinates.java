package src.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlType(name = "coordinates")
@XmlRootElement()
public class Coordinates implements Serializable {
    @XmlElement
    private Double x; //Поле не может быть null
    @XmlElement
    private float y;

    public Coordinates(){}
    public Coordinates(Double x, Float y) {
        this.x = x;
        this.y = y;
    }

    /** Method for printing this field into a string representation */
    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Double getX() {
        return x;
    }

    public Float getY() {
        return y;
    }
}


