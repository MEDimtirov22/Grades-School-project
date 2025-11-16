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
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        sb.append(" | ");
        sb.append(name);
        sb.append(" (");
        sb.append(role);
        sb.append(")");
        return sb.toString();
    }
}