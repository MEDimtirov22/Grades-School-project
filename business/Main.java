package business;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static class Grade {
        final int id;
        final int studentId;
        final String courseName;
        final double value;

        Grade(int id, int studentId, String courseName, double value) {
            this.id = id;
            this.studentId = studentId;
            this.courseName = courseName;
            this.value = value;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(id);
            sb.append(" | studentId=");
            sb.append(studentId);
            sb.append(" | ");
            sb.append(courseName);
            sb.append(" = ");
            sb.append(value);
            return sb.toString();
        }
    }

    static final ArrayList<Grade> grades = new ArrayList<Grade>();
    static final AtomicInteger gradeSeq = new AtomicInteger(1);

    public static void main(String[] args) {
        User teacher = Auth.register("ivan", "teacher", "pass");
        User student = Auth.register("petar", "student", "p");
        addGrade(student.getId(), "Mathematics", 5.5);

        Scanner scanner = new Scanner(System.in);

        User current = null;

        while (current == null) {
            printAuthMenu();
            String cmd = scanner.nextLine().trim();
            if (cmd.equals("0")) {
                System.out.println("Bye.");
                scanner.close();
                return;
            } else if (cmd.equals("1")) {
                System.out.print("Name: ");
                String name = scanner.nextLine().trim();
                String role;
                while (true) {
                    System.out.print("Role (student/teacher): ");
                    role = scanner.nextLine().trim();
                    String rl = role.toLowerCase();
                    if (rl.equals("student") || rl.equals("teacher")) {
                        role = rl;
                        break;
                    } else {
                        System.out.println("Please enter 'student' or 'teacher'.");
                    }
                }
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();
                User newUser = Auth.register(name, role, password);
                if (newUser == null) {
                    System.out.println("Registration failed (name exists or invalid data).");
                } else {
                    System.out.println("Registered: " + newUser.toString());
                }
            } else if (cmd.equals("2")) {
                System.out.print("Name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();
                User logged = Auth.login(name, password);
                if (logged == null) {
                    System.out.println("Invalid credentials.");
                } else {
                    current = logged;
                    System.out.println("Logged in as: " + current.toString());
                }
            } else {
                System.out.println("Unknown command");
            }
        }

        while (true) {
            printMenu();
            String command = scanner.nextLine().trim();
            if (command.equals("0")) {
                break;
            } else if (command.equals("1")) {
                System.out.print("Student id: ");
                int sid = readInt(scanner);
                System.out.print("Course name: ");
                String cname = scanner.nextLine().trim();
                System.out.print("Value (e.g. 5.5): ");
                double val = readDouble(scanner);
                Grade newGrade = addGrade(sid, cname, val);
                System.out.println("Added: " + newGrade.toString());
            } else if (command.equals("2")) {
                listUsers();
            } else if (command.equals("3")) {
                listGrades();
            } else if (command.equals("4")) {
                printAverages();
            } else if (command.equals("5")) {
                System.out.print("Student name contains (empty = any): ");
                String studentPart = scanner.nextLine().trim();
                System.out.print("Course name contains (empty = any): ");
                String coursePart = scanner.nextLine().trim();
                ArrayList<Grade> found = searchGrades(studentPart, coursePart);
                System.out.println("Found " + found.size() + " grades:");
                for (int i = 0; i < found.size(); i++) {
                    Grade g = found.get(i);
                    System.out.println(g.toString());
                }
            } else {
                System.out.println("Unknown command");
            }
        }

        scanner.close();
        System.out.println("Bye.");
    }

    static void printAuthMenu() {
        System.out.println();
        System.out.println("--- Authentication ---");
        System.out.println("1 - Register");
        System.out.println("2 - Login");
        System.out.println("0 - Exit");
        System.out.print("Choose: ");
    }

    static void printMenu() {
        System.out.println();
        System.out.println("--- Menu ---");
        System.out.println("1 - Add grade");
        System.out.println("2 - List users");
        System.out.println("3 - List grades");
        System.out.println("4 - Show average per course");
        System.out.println("5 - Search grades");
        System.out.println("0 - Exit");
        System.out.print("Choose: ");
    }

    static Grade addGrade(int studentId, String courseName, double value) {
        int id = gradeSeq.getAndIncrement();
        Grade g = new Grade(id, studentId, courseName, value);
        synchronized (grades) {
            grades.add(g);
        }
        return g;
    }

    static void listUsers() {
        ArrayList<User> list = Auth.getUsers();
        System.out.println("Users:");
        for (int i = 0; i < list.size(); i++) {
            User u = list.get(i);
            System.out.println(u.toString());
        }
    }

    static void listGrades() {
        synchronized (grades) {
            System.out.println("Grades:");
            for (int i = 0; i < grades.size(); i++) {
                Grade g = grades.get(i);
                System.out.println(g.toString());
            }
        }
    }

    static void printAverages() {
        ArrayList<String> courses = new ArrayList<String>();
        synchronized (grades) {
            for (int i = 0; i < grades.size(); i++) {
                Grade g = grades.get(i);
                String key = g.courseName;
                boolean found = false;
                for (int j = 0; j < courses.size(); j++) {
                    String existing = courses.get(j);
                    if (existing.equals(key)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    courses.add(key);
                }
            }
        }
        if (courses.size() == 0) {
            System.out.println("No grades yet.");
            return;
        }
        System.out.println("Average per course:");
        for (int i = 0; i < courses.size(); i++) {
            String course = courses.get(i);
            double sum = 0.0;
            int count = 0;
            synchronized (grades) {
                for (int j = 0; j < grades.size(); j++) {
                    Grade g = grades.get(j);
                    if (g.courseName.equals(course)) {
                        sum = sum + g.value;
                        count = count + 1;
                    }
                }
            }
            double average = 0.0;
            if (count > 0) {
                average = sum / count;
            } else {
                average = 0.0;
            }
            System.out.printf("%s => %.2f%n", course, average);
        }
    }

    static ArrayList<Grade> searchGrades(String studentPart, String coursePart) {
        String sp;
        if (studentPart == null) {
            sp = "";
        } else {
            sp = studentPart.toLowerCase();
        }
        String cp;
        if (coursePart == null) {
            cp = "";
        } else {
            cp = coursePart.toLowerCase();
        }

        ArrayList<Grade> out = new ArrayList<Grade>();
        synchronized (grades) {
            for (int i = 0; i < grades.size(); i++) {
                Grade g = grades.get(i);
                String cname = g.courseName.toLowerCase();
                String sname = findUserNameById(g.studentId).toLowerCase();
                boolean sm;
                if (sp.isEmpty()) {
                    sm = true;
                } else {
                    sm = sname.contains(sp);
                }
                boolean cm;
                if (cp.isEmpty()) {
                    cm = true;
                } else {
                    cm = cname.contains(cp);
                }
                if (sm && cm) {
                    out.add(g);
                }
            }
        }
        return out;
    }

    static String findUserNameById(int id) {
        User u = Auth.findById(id);
        if (u == null) {
            return "unknown";
        }
        return u.getName();
    }

    static int readInt(Scanner sc) {
        try {
            String line = sc.nextLine().trim();
            int value = Integer.parseInt(line);
            return value;
        } catch (Exception e) {
            return -1;
        }
    }

    static double readDouble(Scanner sc) {
        try {
            String line = sc.nextLine().trim();
            double value = Double.parseDouble(line);
            return value;
        } catch (Exception e) {
            return 0.0;
        }
    }
}