package business;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import data.User;
import presentation.AuthService;

public class GradeService {

    private static final ArrayList<Grade> grades = new ArrayList<>();
    private static final AtomicInteger gradeSeq = new AtomicInteger(1);
    private static final String FILE = "data/grades.txt";

    public static class Grade {
        public final int id, studentId;
        public final String course;
        public final double value;

        public Grade(int id, int sid, String course, double value) {
            this.id = id;
            this.studentId = sid;
            this.course = course;
            this.value = value;
        }

        public String toString() {
            User s = AuthService.findById(studentId);
            return id + " | " + (s != null ? s.getName() : "unknown")
                    + " | " + course + " = " + value;
        }
    }

    public static boolean validGrade(double v) {
        return v >= 2.0 && v <= 6.0;
    }

    public static void addGrade(int sid, String course, double v) {
        grades.add(new Grade(gradeSeq.getAndIncrement(), sid, course, v));
        saveGrades();
    }

    public static ArrayList<Grade> getAllGrades() {
        grades.sort(Comparator.comparingInt(g -> g.id));
        return new ArrayList<>(grades);
    }

    public static ArrayList<Grade> getGradesByStudent(int sid) {
        ArrayList<Grade> out = new ArrayList<>();
        for (Grade g : grades) if (g.studentId == sid) out.add(g);
        return out;
    }

    public static boolean updateGrade(int id, double v) {
        for (int i = 0; i < grades.size(); i++)
            if (grades.get(i).id == id) {
                Grade g = grades.get(i);
                grades.set(i, new Grade(id, g.studentId, g.course, v));
                saveGrades();
                return true;
            }
        return false;
    }

    public static boolean deleteGrade(int id) {
        boolean r = grades.removeIf(g -> g.id == id);
        if (r) saveGrades();
        return r;
    }

    public static double getAverage(int sid) {
        double sum = 0; int c = 0;
        for (Grade g : grades)
            if (g.studentId == sid) { sum += g.value; c++; }
        return c == 0 ? 0 : sum / c;
    }

    public static void saveGrades() {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdir();
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE));
            for (Grade g : grades)
                bw.write(g.id + "|" + g.studentId + "|" + g.course + "|" + g.value + "\n");
            bw.close();
        } catch (Exception e) { }
    }

    public static void loadGrades() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line; int max = 0;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                Grade g = new Grade(
                        Integer.parseInt(p[0]),
                        Integer.parseInt(p[1]),
                        p[2],
                        Double.parseDouble(p[3])
                );
                grades.add(g);
                max = Math.max(max, g.id);
            }
            gradeSeq.set(max + 1);
            br.close();
        } catch (Exception e) { }
    }
}