import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExamScheduler {

    // Define a class to represent a student
    static class Student {
        String name;
        String rollNo;
        Set<String> courseIds;

        public Student(String name, String rollNo, Set<String> courseIds) {
            this.name = name;
            this.rollNo = rollNo;
            this.courseIds = courseIds;
        }
    }

    // Main method to schedule exams
    public static void main(String[] args) {
        // Read data from Excel sheets and organize into suitable data structure
        if (args.length == 0) {
            System.out.println("Please provide the Excel file name.");
            return;
        }

        String fileName = args[0];
        List<Student> students = readDataFromExcel(fileName);

        if (students.isEmpty()) {
            System.out.println("No data found in the Excel file.");
            return;
        }

        // Create a map to store the count of students for each course
        Map<String, Integer> courseCountMap = new HashMap<>();
        for (Student student : students) {
            for (String courseId : student.courseIds) {
                courseCountMap.put(courseId, courseCountMap.getOrDefault(courseId, 0) + 1);
            }
        }

        // Sort the courses by the number of students in ascending order
        List<String> sortedCourses = new ArrayList<>(courseCountMap.keySet());
        sortedCourses.sort(Comparator.comparingInt(courseCountMap::get));

        // Create a map to store the exam schedule
        Map<String, Integer> schedule = new HashMap<>();

        // Schedule exams
        int day = 1;
        for (String courseId : sortedCourses) {
            boolean scheduled = false;
            while (!scheduled) {
                boolean conflict = false;
                for (Student student : students) {
                    if (student.courseIds.contains(courseId) && schedule.containsValue(day)) {
                        conflict = true;
                        break;
                    }
                }
                if (!conflict) {
                    schedule.put(courseId, day);
                    scheduled = true;
                } else {
                    day++;
                }
            }
        }

        // Print schedule
        for (Map.Entry<String, Integer> entry : schedule.entrySet()) {
            System.out.println("Course ID: " + entry.getKey() + " - Day " + entry.getValue());
        }
    }

    // Method to read data from Excel file and return a list of students
    private static List<Student> readDataFromExcel(String fileName) {
        List<Student> students = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(new File(fileName));
             Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            for (Row row : sheet) {
                String name = row.getCell(0).getStringCellValue();
                String rollNo = row.getCell(1).getStringCellValue();
                String courseIdsStr = row.getCell(2).getStringCellValue();
                String[] courseIdsArr = courseIdsStr.split(","); // Assuming course IDs are comma-separated

                Set<String> courseIds = new HashSet<>(Arrays.asList(courseIdsArr));
                students.add(new Student(name, rollNo, courseIds));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return students;
    }
}
