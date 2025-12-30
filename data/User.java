package data;

public class User {
    private final int id;
    private final String name;
    private final String role;
    private final String password;

    public User(int id, String name, String role, String password) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.password = password;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPassword() { return password; }

    public String toString() {
        return id + " | " + name + " (" + role + ")";
    }
}