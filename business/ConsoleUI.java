package business;

import java.util.Scanner;
import data.User;
import presentation.AuthService;

public class ConsoleUI {

    public static void start() {
        Scanner sc = new Scanner(System.in);
        AuthService.loadUsers();
        GradeService.loadGrades();

        User current = null;
        while (current == null) {
            System.out.println("\n1-Register 2-Login 0-Exit");
            String cmd = sc.nextLine();
            if (cmd.equals("0")) { sc.close(); return; }

            if (cmd.equals("1")) {
                System.out.print("Name: "); String n = sc.nextLine();
                System.out.print("Role (student/teacher): "); String r = sc.nextLine();
                System.out.print("Password: "); String p = sc.nextLine();
                System.out.println(AuthService.register(n, r, p) != null ? "Registered" : "Failed");
            }

            if (cmd.equals("2")) {
                System.out.print("Name: "); String n = sc.nextLine();
                System.out.print("Password: "); String p = sc.nextLine();
                current = AuthService.login(n, p);
                if (current == null) System.out.println("Invalid login");
            }
        }

        if (current.getRole().equals("teacher"))
            teacherMenu(sc, current);
        else
            studentMenu(sc, current);

        AuthService.saveUsers();
        GradeService.saveGrades();
        sc.close();
    }

    private static void teacherMenu(Scanner sc, User t) {
        while (true) {
            System.out.println("\n--- Teacher ---");
            System.out.println("1-Add grade");
            System.out.println("2-List users");
            System.out.println("3-List grades");
            System.out.println("4-Averages");
            System.out.println("5-Edit grade");
            System.out.println("6-Delete grade");
            System.out.println("7-Change user password");
            System.out.println("8-Delete user");
            System.out.println("0-Logout");
            String c = sc.nextLine();

            if (c.equals("0")) break;

            if (c.equals("1")) {
                System.out.print("Student ID: "); int sid = readInt(sc);
                System.out.print("Course: "); String cn = sc.nextLine();
                System.out.print("Value: "); double v = readDouble(sc);
                GradeService.addGrade(sid, cn, v);
            }

            if (c.equals("2")) {
                for (User u : AuthService.getAllUsers()) System.out.println(u);
            }

            if (c.equals("3")) {
                for (GradeService.Grade g : GradeService.getAllGrades()) System.out.println(g);
            }

            if (c.equals("4")) GradeService.printAveragePerCourse();

            if (c.equals("5")) {
                System.out.print("Grade ID: "); int id = readInt(sc);
                System.out.print("New value: "); double v = readDouble(sc);
                System.out.println(GradeService.updateGrade(id, v) ? "Updated" : "Not found");
            }

            if (c.equals("6")) {
                System.out.print("Grade ID: "); int id = readInt(sc);
                System.out.println(GradeService.deleteGrade(id) ? "Deleted" : "Not found");
            }

            if (c.equals("7")) {
                System.out.print("User ID: "); int uid = readInt(sc);
                System.out.print("New password: "); String np = sc.nextLine();
                System.out.println(AuthService.changePassword(uid, np) ? "Changed" : "Not found");
            }

            if (c.equals("8")) {
                System.out.print("User ID: "); int uid = readInt(sc);
                boolean hasGrades = false;
                for (GradeService.Grade g : GradeService.getAllGrades())
                    if (g.studentId == uid) { hasGrades = true; break; }
                if (hasGrades) { System.out.println("Cannot delete: user has grades"); continue; }
                System.out.println(AuthService.deleteUser(uid) ? "Deleted" : "Not found");
            }
        }
    }

    private static void studentMenu(Scanner sc, User u) {
        while (true) {
            System.out.println("\n--- Student ---");
            System.out.println("1-My grades");
            System.out.println("2-My average");
            System.out.println("0-Logout");
            String c = sc.nextLine();

            if (c.equals("0")) break;
            if (c.equals("1")) {
                for (GradeService.Grade g : GradeService.getGradesByStudent(u.getId())) System.out.println(g);
            }
            if (c.equals("2")) {
                double avg = GradeService.getAverageByStudent(u.getId());
                System.out.println(avg == 0 ? "No grades" : "Average: " + avg);
            }
        }
    }

    private static int readInt(Scanner sc) {
        try { return Integer.parseInt(sc.nextLine()); } catch (Exception e) { return -1; }
    }

    private static double readDouble(Scanner sc) {
        try { return Double.parseDouble(sc.nextLine()); } catch (Exception e) { return -1; }
    }
}
