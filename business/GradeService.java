package business;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;

public class GradeService {

    public static final String GRADE_FILE = "data/grades.txt";

    private static final ArrayList<Grade> grades = new ArrayList<>();
    private static final AtomicInteger gradeSeq = new AtomicInteger(1);

    public static class Grade {
        public final int id, studentId;
        public final String courseName;
        public final double value;

        public Grade(int id, int studentId, String courseName, double value) {
            this.id = id;
            this.studentId = studentId;
            this.courseName = courseName;
            this.value = value;
        }

        public String toString() {
            return id + " | studentId=" + studentId + " | " + courseName + " = " + value;
        }
    }

    public static Grade addGrade(int sid, String cname, double val) {
        Grade g = new Grade(gradeSeq.getAndIncrement(), sid, cname, val);
        grades.add(g);
        return g;
    }

    public static boolean updateGrade(int id, double val) {
        for (int i = 0; i < grades.size(); i++) {
            Grade g = grades.get(i);
            if (g.id == id) {
                grades.set(i, new Grade(id, g.studentId, g.courseName, val));
                return true;
            }
        }
        return false;
    }

    public static boolean deleteGrade(int id) {
        for (int i = 0; i < grades.size(); i++) {
            if (grades.get(i).id == id) {
                grades.remove(i);
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Grade> getAllGrades() {
        return new ArrayList<>(grades);
    }

    public static ArrayList<Grade> getGradesByStudent(int studentId) {
        ArrayList<Grade> out = new ArrayList<>();
        for (Grade g : grades) if (g.studentId == studentId) out.add(g);
        return out;
    }

    public static double getAverageByStudent(int studentId) {
        double sum = 0; int cnt = 0;
        for (Grade g : grades) if (g.studentId == studentId) { sum += g.value; cnt++; }
        return cnt == 0 ? 0 : sum / cnt;
    }

    public static void printAveragePerCourse() {
        ArrayList<String> courses = new ArrayList<>();
        for (Grade g : grades) if (!courses.contains(g.courseName)) courses.add(g.courseName);

        for (String c : courses) {
            double sum = 0; int cnt = 0;
            for (Grade g : grades) if (g.courseName.equals(c)) { sum += g.value; cnt++; }
            System.out.printf("%s => %.2f%n", c, sum / cnt);
        }
    }

    public static void saveGrades() {
        try {
            File dir = new File("data"); if (!dir.exists()) dir.mkdir();
            BufferedWriter bw = new BufferedWriter(new FileWriter(GRADE_FILE));
            for (Grade g : grades)
                bw.write(g.id + "|" + g.studentId + "|" + g.courseName + "|" + g.value + "\n");
            bw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void loadGrades() {
        File f = new File(GRADE_FILE);
        if (!f.exists()) return;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line; int maxId = 0;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length != 4) continue;
                int id = Integer.parseInt(p[0]);
                int sid = Integer.parseInt(p[1]);
                String cname = p[2];
                double val = Double.parseDouble(p[3]);
                grades.add(new Grade(id, sid, cname, val));
                if (id > maxId) maxId = id;
            }
            gradeSeq.set(maxId + 1);
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
