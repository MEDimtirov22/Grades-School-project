package presentation;

import data.User;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;

public class AuthService {

    private static final ArrayList<User> users = new ArrayList<>();
    private static final AtomicInteger userSeq = new AtomicInteger(1);
    private static final String USER_FILE = "data/users.txt";

    public static synchronized int register(String name, String role, String password) {
        if (name == null || name.isBlank() || password == null || password.isBlank())
            return -2;

        if (role == null) return -1;
        role = role.toLowerCase();

        if (!role.equals("student") && !role.equals("teacher"))
            return -1;

        for (User u : users)
            if (u.getName().equals(name) && u.getRole().equals(role))
                return 0;

        User nu = new User(userSeq.getAndIncrement(), name, role, password);
        users.add(nu);
        saveUsers();
        return 1;
    }

    public static synchronized User login(String name, String password, String role) {
        for (User u : users)
            if (u.getName().equals(name)
                    && u.getPassword().equals(password)
                    && u.getRole().equals(role))
                return u;
        return null;
    }

    public static synchronized boolean deleteUser(int id) {
        boolean removed = users.removeIf(u -> u.getId() == id);
        if (removed) saveUsers();
        return removed;
    }

    public static synchronized boolean changePassword(int id, String newPass) {
        if (newPass == null || newPass.isBlank()) return false;

        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getId() == id) {
                users.set(i, new User(id, u.getName(), u.getRole(), newPass));
                saveUsers();
                return true;
            }
        }
        return false;
    }

    public static synchronized ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public static synchronized User findById(int id) {
        for (User u : users)
            if (u.getId() == id)
                return u;
        return null;
    }

    public static void saveUsers() {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdir();

            BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE));
            for (User u : users) {
                bw.write(u.getId() + "|" + u.getName() + "|" + u.getRole() + "|" + u.getPassword());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Failed to save users.");
        }
    }

    public static void loadUsers() {
        File f = new File(USER_FILE);
        if (!f.exists()) return;

        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            int maxId = 0;

            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length != 4) continue;

                int id = Integer.parseInt(p[0]);
                users.add(new User(id, p[1], p[2], p[3]));
                if (id > maxId) maxId = id;
            }

            userSeq.set(maxId + 1);
            br.close();
        } catch (Exception e) {
            System.out.println("Failed to load users.");
        }
    }
}