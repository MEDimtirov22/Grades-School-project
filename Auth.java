import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Auth {

    static final ArrayList<User> users = new ArrayList<User>();
    static final AtomicInteger userSeq = new AtomicInteger(1);

    public static synchronized User register(String name, String role, String password) {
        if (name == null) {
            return null;
        }
        if (role == null) {
            return null;
        }
        String rl = role.toLowerCase();
        if (!rl.equals("student") && !rl.equals("teacher")) {
            return null;
        }
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getName().equals(name)) {
                return null;
            }
        }
        int id = userSeq.getAndIncrement();
        User nu = new User(id, name, rl, password);
        users.add(nu);
        return nu;
    }

    public static synchronized User login(String name, String password) {
        if (name == null || password == null) {
            return null;
        }
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getName().equals(name) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public static synchronized User findById(int id) {
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public static synchronized ArrayList<User> getUsers() {
        ArrayList<User> copy = new ArrayList<User>();
        for (int i = 0; i < users.size(); i++) {
            copy.add(users.get(i));
        }
        return copy;
    }
}