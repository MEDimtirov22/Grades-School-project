package business;

import java.util.ArrayList;
import java.io.*;
import presentation.AuthService;
import data.User;

public class GradeService {

    private static final ArrayList<Grade> grades = new ArrayList<>();
    private static final String GRADE_FILE = "data/grades.txt";

    public static class Grade {
        public final int id;
        public final int studentId;
        public final String courseName;
        public final double value;

        public Grade(int id, int studentId, String courseName, double value) {
            this.id = id;
            this.studentId = studentId;
            this.courseName = courseName;
            this.value = value;
        }

        public String toString() {
            User s = AuthService.findById(studentId);
            String studentName = (s != null) ? s.getName() : "unknown";
            return id + " | " + studentName + " | " + courseName + " = " + value;
        }
    }

    public static boolean validGrade(double v) {
        return v >= 2.0 && v <= 6.0;
    }

    public static Grade addGrade(int sid, String cname, double val) {
        Grade g = new Grade(grades.size() + 1, sid, cname, val);
        grades.add(g);
        saveGrades();
        return g;
    }

    public static boolean updateGrade(int id, double val) {
        for (int i = 0; i < grades.size(); i++) {
            Grade g = grades.get(i);
            if (g.id == id) {
                grades.set(i, new Grade(id, g.studentId, g.courseName, val));
                saveGrades();
                return true;
            }
        }
        return false;
    }

    public static boolean deleteGrade(int id) {
        boolean removed = grades.removeIf(g -> g.id == id);
        if (removed) {
            reassignIds();
        }
        saveGrades();
        return removed;
    }

    private static void reassignIds() {
        for (int i = 0; i < grades.size(); i++) {
            Grade g = grades.get(i);
            grades.set(i, new Grade(i + 1, g.studentId, g.courseName, g.value));
        }
    }

    public static ArrayList<Grade> getAllGrades() {
        return new ArrayList<>(grades);
    }

    public static ArrayList<Grade> getGradesByStudent(int studentId) {
        ArrayList<Grade> out = new ArrayList<>();
        for (Grade g : grades) {
            if (g.studentId == studentId) {
                out.add(g);
            }
        }
        return out;
    }

    public static double getAverage(int studentId) {
        double sum = 0;
        int count = 0;
        for (Grade g : grades) {
            if (g.studentId == studentId) {
                sum += g.value;
                count++;
            }
        }
        if (count == 0) {
            return 0;
        } else {
            return sum / count;
        }
    }

    public static void printAveragePerCourse() {
        ArrayList<String> courses = new ArrayList<>();
        for (Grade g : grades) {
            if (!courses.contains(g.courseName)) {
                courses.add(g.courseName);
            }
        }

        for (String c : courses) {
            double sum = 0;
            int count = 0;
            for (Grade g : grades) {
                if (g.courseName.equals(c)) {
                    sum += g.value;
                    count++;
                }
            }
            if (count == 0) {
                System.out.printf("%s => 0%n", c);
            } else {
                System.out.printf("%s => %.2f%n", c, sum / count);
            }
        }
    }

    public static void saveGrades() {
        try {
            File dir = new File("data");
            if (!dir.exists()) {
                boolean created = dir.mkdir();
                if (!created) {
                    System.out.println("Failed to create data directory.");
                    return;
                }
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(GRADE_FILE));
            for (Grade g : grades) {
                bw.write(g.id + "|" + g.studentId + "|" + g.courseName + "|" + g.value);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Failed to save grades due to an I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to save grades: " + e.getMessage());
        }
    }

    public static void loadGrades() {
        File f = new File(GRADE_FILE);
        if (!f.exists()) {
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            grades.clear();

            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length != 4) {
                    System.out.println("Skipping invalid line in grades file: " + line);
                    continue;
                }
                int sid;
                double val;
                try {
                    sid = Integer.parseInt(p[1]);
                    val = Double.parseDouble(p[3]);
                } catch (NumberFormatException nfe) {
                    System.out.println("Skipping line with invalid numbers: " + line);
                    continue;
                }
                String cname = p[2];
                grades.add(new Grade(0, sid, cname, val));
            }

            reassignIds();
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("Grades file not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to load grades due to an I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to load grades: " + e.getMessage());
        }
    }
}
