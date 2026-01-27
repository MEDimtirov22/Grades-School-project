package data;

public class User {
    private int id;
    private String name;
    private String role;
    private String password;

    public User(int id, String name, String role, String password) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.password = password;
    }

    public int getId() { 
        return id; 
    }
    public String getName() { 
        return name;
     }
    public String getRole() {
         return role; 

    }
    public String getPassword() {
         return password; 
        }

    public String toString() {
        return id + " | " + name + " (" + role + ")";
    }
}
