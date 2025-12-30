package business;

import java.util.Scanner;
import data.User;
import presentation.AuthService;

public class ConsoleUI {

    public static void start() {
        Scanner sc = new Scanner(System.in);
        AuthService.loadUsers();
        GradeService.loadGrades();

        while (true) {
            User current = null;

            while (current == null) {
                System.out.println("\n--- Login Menu ---");
                System.out.println("1 - Register");
                System.out.println("2 - Login");
                System.out.println("0 - Exit");
                System.out.print("Choice: ");
                String c = sc.nextLine();

                if (c.equals("0")) return;

                if (c.equals("1")) {
                    System.out.print("Name: ");
                    String n = sc.nextLine();
                    System.out.print("Role (student/teacher): ");
                    String r = sc.nextLine();
                    System.out.print("Password: ");
                    String p = sc.nextLine();

                    int res = AuthService.register(n, r, p);

                    if (res == 1)
                        System.out.println("Registered successfully.");
                    else if (res == 0)
                        System.out.println("Such account already exists.");
                    else if (res == -1)
                        System.out.println("Invalid role. Please enter student or teacher.");
                    else
                        System.out.println("Invalid data. Name and password cannot be empty.");
                }

                if (c.equals("2")) {
                    System.out.print("Name: ");
                    String n = sc.nextLine();
                    System.out.print("Role (student/teacher): ");
                    String r = sc.nextLine();
                    System.out.print("Password: ");
                    String p = sc.nextLine();

                    current = AuthService.login(n, p, r);
                    if (current == null)
                        System.out.println("Invalid credentials.");
                }
            }

            if (current.getRole().equals("teacher"))
                teacherMenu(sc, current);
            else
                studentMenu(sc, current);
        }
    }

    private static void teacherMenu(Scanner sc, User t) {
        while (true) {
            System.out.println("\n--- Teacher Menu ---");
            System.out.println("1 - Add grade");
            System.out.println("2 - Update grade");
            System.out.println("3 - Delete grade");
            System.out.println("4 - List grades");
            System.out.println("5 - List users");
            System.out.println("6 - Delete user");
            System.out.println("7 - Change password");
            System.out.println("0 - Logout");
            System.out.print("Choice: ");
            String c = sc.nextLine();

            if (c.equals("0")) return;

            if (c.equals("1")) {
                System.out.println("0 - Back");
                AuthService.getAllUsers().stream()
                        .filter(u -> u.getRole().equals("student"))
                        .forEach(System.out::println);

                int sid = readInt(sc);
                if (sid == 0) continue;

                User s = AuthService.findById(sid);
                if (s == null || !s.getRole().equals("student")) {
                    System.out.println("Invalid student.");
                    continue;
                }

                System.out.print("Course: ");
                String course = sc.nextLine();

                System.out.print("Grade (2.00 - 6.00): ");
                double v = readDouble(sc);
                if (!GradeService.validGrade(v)) {
                    System.out.println("Please input values between 2.00 and 6.00");
                    continue;
                }

                GradeService.addGrade(sid, course, v);
                System.out.println("Grade added.");
            }

            if (c.equals("2")) {
                GradeService.getAllGrades().forEach(System.out::println);
                System.out.print("Grade ID (0-back): ");
                int id = readInt(sc);
                if (id == 0) continue;

                System.out.print("New value: ");
                double v = readDouble(sc);
                if (!GradeService.validGrade(v)) {
                    System.out.println("Please input values between 2.00 and 6.00");
                    continue;
                }

                System.out.println(
                        GradeService.updateGrade(id, v) ? "Updated." : "Grade not found."
                );
            }

            if (c.equals("3")) {
                GradeService.getAllGrades().forEach(System.out::println);
                System.out.print("Grade ID (0-back): ");
                int id = readInt(sc);
                if (id != 0)
                    System.out.println(
                            GradeService.deleteGrade(id) ? "Deleted." : "Grade not found."
                    );
            }

            if (c.equals("4"))
                GradeService.getAllGrades().forEach(System.out::println);

            if (c.equals("5"))
                AuthService.getAllUsers().forEach(System.out::println);

            if (c.equals("6")) {
                AuthService.getAllUsers().forEach(System.out::println);
                System.out.print("User ID (0-back): ");
                int id = readInt(sc);
                if (id != 0)
                    System.out.println(
                            AuthService.deleteUser(id) ? "User deleted." : "User not found."
                    );
            }

            if (c.equals("7")) {
                System.out.print("New password: ");
                System.out.println(
                        AuthService.changePassword(t.getId(), sc.nextLine())
                                ? "Password changed."
                                : "Invalid password."
                );
            }
        }
    }

    private static void studentMenu(Scanner sc, User u) {
        while (true) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1 - My grades");
            System.out.println("2 - My average");
            System.out.println("3 - Change password");
            System.out.println("0 - Logout");
            System.out.print("Choice: ");
            String c = sc.nextLine();

            if (c.equals("0")) return;

            if (c.equals("1")) {
                var g = GradeService.getGradesByStudent(u.getId());
                if (g.isEmpty())
                    System.out.println("No grades.");
                else
                    g.forEach(System.out::println);
            }

            if (c.equals("2")) {
                double avg = GradeService.getAverage(u.getId());
                System.out.println(avg == 0 ? "No grades." : "Average: " + avg);
            }

            if (c.equals("3")) {
                System.out.print("New password: ");
                System.out.println(
                        AuthService.changePassword(u.getId(), sc.nextLine())
                                ? "Password changed."
                                : "Invalid password."
                );
            }
        }
    }

    private static int readInt(Scanner sc) {
        try { return Integer.parseInt(sc.nextLine()); }
        catch (Exception e) { return -1; }
    }

    private static double readDouble(Scanner sc) {
        try { return Double.parseDouble(sc.nextLine()); }
        catch (Exception e) { return -1; }
    }
}