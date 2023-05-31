package src.models;


public class User {
    private final int id;
    private String password;
    private String name;
    public Role role;

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
    public void setPassword(String psw){
        password = psw;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    @Override
    public String toString(){
        return "ID: " + id + "\nName: " + getName() + "\nRole: " + role.toString();
    }

}
