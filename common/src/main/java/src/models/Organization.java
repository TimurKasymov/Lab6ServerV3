package src.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlType(name = "organization")
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class Organization implements Serializable {

    public Organization(){}
    public Organization(Long id, String name, Integer annualTurnover, OrganizationType organizationType){
        this.name = name;
        this.annualTurnover = annualTurnover;
        this.type = organizationType;
        this.id = id;
    }
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    public void setId(Long id){
        this.id = id;
    }
    public Long getId(){
        return id;
    }
    private String name; //Поле не может быть null, Строка не может быть пустой
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    private Integer annualTurnover; //Поле не может быть null, Значение поля должно быть больше 0
    public void setAnnualTurnover(Integer annualTurnover){
        this.annualTurnover = annualTurnover;
    }
    public Integer getAnnualTurnover(){
        return annualTurnover;
    }

    private OrganizationType type; //Поле не может быть null
    public void setOrganizationType(OrganizationType organizationType){
        this.type = organizationType;
    }
    public OrganizationType getOrganizationType(){
        return type;
    }
}
