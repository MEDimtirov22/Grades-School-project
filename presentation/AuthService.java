package presentation;

import data.User;
import java.util.ArrayList;
import java.io.*;

public class AuthService {

    private static ArrayList<User> users = new ArrayList<>();
    private static String USER_FILE = "data/users.txt";

    public static synchronized int register(String name, String role, String password) {
        if (name == null || name.isBlank() || password == null || password.isBlank()) {
            return -2;
        }

        if (role == null) {
            return -1;
        }
        role = role.toLowerCase();

        if (!role.equals("student") && !role.equals("teacher")) {
            return -1;
        }

        for (User u : users) {
            if (u.getName().equals(name) && u.getRole().equals(role)) {
                return 0;
            }
        }

        int newId = users.size() + 1;
        User nu = new User(newId, name, role, password);
        users.add(nu);
        saveUsers();
        return 1;
    }

    public static synchronized User login(String name, String password, String role) {
        for (User u : users) {
            if (u.getName().equals(name) && u.getPassword().equals(password) && u.getRole().equals(role)) {
                return u;
            }
        }
        return null;
    }

    public static synchronized boolean deleteUser(int id) {
        boolean removed = users.removeIf(u -> u.getId() == id);
        if (removed) {
            reassignIds();
        }
        saveUsers();
        return removed;
    }

    private static void reassignIds() {
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            users.set(i, new User(i + 1, u.getName(), u.getRole(), u.getPassword()));
        }
    }

    public static synchronized boolean changePassword(int id, String newPass) {
        if (newPass == null || newPass.isBlank()) {
            return false;
        }
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
        for (User u : users) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public static synchronized void saveUsers() {
        try {
            File dir = new File("data");
            if (!dir.exists()) {
                boolean created = dir.mkdir();
                if (!created) {
                    System.out.println("Failed to create data directory.");
                    return;
                }
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE));
            for (User u : users) {
                bw.write(u.getId() + "|" + u.getName() + "|" + u.getRole() + "|" + u.getPassword());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Failed to save users due to an I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to save users: " + e.getMessage());
        }
    }

    public static synchronized void loadUsers() {
        File f = new File(USER_FILE);
        if (!f.exists()) {
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            users.clear();

            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length != 4) {
                    System.out.println("Skipping invalid line in users file: " + line);
                    continue;
                }
                users.add(new User(0, p[1], p[2], p[3]));
            }

            reassignIds();
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("Users file not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to load users due to an I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to load users: " + e.getMessage());
        }
    }
}