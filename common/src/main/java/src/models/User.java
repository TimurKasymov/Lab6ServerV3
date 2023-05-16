package src.models;

import java.io.Serial;

public class User {
    private int id;
    private String password;
    private String name;

    public User(int id, String password, String name){
        this.id = id;
        this.password = password;
        this.name = name;
    }

    public int getId(){
        return id;
    }
    public String getPassword(){
        return password;
    }
    public String getName(){
        return name;
    }
}
