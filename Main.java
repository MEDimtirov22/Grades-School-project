import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static class User {
        final int id;
        final String name;
        final String role;

        User(int id, String name, String role) {
            this.id = id;
            this.name = name;
            this.role = role;
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

    static final ArrayList<User> users = new ArrayList<User>();
    static final ArrayList<Grade> grades = new ArrayList<Grade>();
    static final AtomicInteger userSeq = new AtomicInteger(1);
    static final AtomicInteger gradeSeq = new AtomicInteger(1);

    public static void main(String[] args) {
        User teacher = addUser("ivan", "teacher");
        User student = addUser("petar", "student");
        addGrade(student.id, "Mathematics", 5.5);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String command = scanner.nextLine().trim();
            if (command.equals("0")) {
                break;
            } else if (command.equals("1")) {
                System.out.print("Name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Role (student/teacher): ");
                String role = scanner.nextLine().trim();
                User newUser = addUser(name, role);
                System.out.println("Added: " + newUser.toString());
            } else if (command.equals("2")) {
                System.out.print("Student id: ");
                int sid = readInt(scanner);
                System.out.print("Course name: ");
                String cname = scanner.nextLine().trim();
                System.out.print("Value (e.g. 5.5): ");
                double val = readDouble(scanner);
                Grade newGrade = addGrade(sid, cname, val);
                System.out.println("Added: " + newGrade.toString());
            } else if (command.equals("3")) {
                listUsers();
            } else if (command.equals("4")) {
                listGrades();
            } else if (command.equals("5")) {
                printAverages();
            } else if (command.equals("6")) {
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

    static void printMenu() {
        System.out.println();
        System.out.println("--- Menu ---");
        System.out.println("1 - Register user");
        System.out.println("2 - Add grade");
        System.out.println("3 - List users");
        System.out.println("4 - List grades");
        System.out.println("5 - Show average per course");
        System.out.println("6 - Search grades");
        System.out.println("0 - Exit");
        System.out.print("Choose: ");
    }

    static User addUser(String name, String role) {
        int id = userSeq.getAndIncrement();
        User u = new User(id, name, role);
        synchronized (users) {
            users.add(u);
        }
        return u;
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
        synchronized (users) {
            System.out.println("Users:");
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                System.out.println(u.toString());
            }
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
        synchronized (users) {
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                if (u.id == id) {
                    return u.name;
                }
            }
        }
        return "unknown";
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