package presentation;

import data.User;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;

public class AuthService {

    private static final ArrayList<User> users = new ArrayList<>();
    private static final AtomicInteger userSeq = new AtomicInteger(1);
    private static final String USER_FILE = "data/users.txt";

    public static synchronized User register(String name, String role, String password) {
        if (name == null || role == null) return null;
        role = role.toLowerCase();
        if (!role.equals("student") && !role.equals("teacher")) return null;
        for (User u : users) if (u.getName().equals(name)) return null;
        User nu = new User(userSeq.getAndIncrement(), name, role, password);
        users.add(nu);
        return nu;
    }

    public static synchronized User login(String name, String password) {
        for (User u : users)
            if (u.getName().equals(name) && u.getPassword().equals(password))
                return u;
        return null;
    }

    public static synchronized boolean deleteUser(int id) {
        for (int i = 0; i < users.size(); i++)
            if (users.get(i).getId() == id) { users.remove(i); return true; }
        return false;
    }

    public static synchronized boolean changePassword(int id, String newPass) {
        User u = findById(id);
        if (u == null) return false;
        users.set(users.indexOf(u), new User(u.getId(), u.getName(), u.getRole(), newPass));
        return true;
    }

    public static synchronized ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public static synchronized User findById(int id) {
        for (User u : users) if (u.getId() == id) return u;
        return null;
    }

    public static void saveUsers() {
        try {
            File dir = new File("data"); if (!dir.exists()) dir.mkdir();
            BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE));
            for (User u : users)
                bw.write(u.getId() + "|" + u.getName() + "|" + u.getRole() + "|" + u.getPassword() + "\n");
            bw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void loadUsers() {
        File f = new File(USER_FILE);
        if (!f.exists()) return;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line; int maxId = 0;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length != 4) continue;
                int id = Integer.parseInt(p[0]);
                String name = p[1], role = p[2], pass = p[3];
                users.add(new User(id, name, role, pass));
                if (id > maxId) maxId = id;
            }
            userSeq.set(maxId + 1);
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
