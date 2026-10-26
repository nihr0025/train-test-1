package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility to generate/populate src/test/resources/testdata.xlsx with the exact worksheets,
 * columns, and rows captured from the reference PDF screenshots.
 */
public class ExcelDataInitializer {

    public static void main(String[] args) throws IOException {
        String filePath = "src/test/resources/testdata.xlsx";
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("Excel file already exists at: " + file.getAbsolutePath());
            return;
        }

        file.getParentFile().mkdirs();
        try (Workbook workbook = new XSSFWorkbook()) {

            // 1. LoginData
            Sheet loginSheet = workbook.createSheet("LoginData");
            createLoginData(loginSheet);

            // 2. Cohort_Creation
            Sheet cohortCreationSheet = workbook.createSheet("Cohort_Creation");
            createCohortCreationData(cohortCreationSheet);

            // 3. Cohort_Editing_By_Coach
            Sheet editSheet = workbook.createSheet("Cohort_Editing_By_Coach");
            createCohortEditingData(editSheet);

            // 4. Cohort_Search_By_Coach
            Sheet coachSearchSheet = workbook.createSheet("Cohort_Search_By_Coach");
            createCoachSearchData(coachSearchSheet);

            // 5. CohortStatusfilter_By_Coach
            Sheet coachStatusSheet = workbook.createSheet("CohortStatusfilter_By_Coach");
            createCoachStatusFilterData(coachStatusSheet);

            // 6. Cohort_or_Skill_SearchByTrainer
            Sheet trainerSearchSheet = workbook.createSheet("Cohort_or_Skill_SearchByTrainer");
            createTrainerSearchData(trainerSearchSheet);

            // 7. CohortStatusFilter_By_Trainer
            Sheet trainerStatusSheet = workbook.createSheet("CohortStatusFilter_By_Trainer");
            createTrainerStatusFilterData(trainerStatusSheet);

            // 8. Cohort_Search_By_Admin
            Sheet adminSearchSheet = workbook.createSheet("Cohort_Search_By_Admin");
            createAdminSearchData(adminSearchSheet);

            // 9. CohortStatusfilter_By_Admin
            Sheet adminStatusSheet = workbook.createSheet("CohortStatusfilter_By_Admin");
            createAdminStatusFilterData(adminStatusSheet);

            // 10. CohortSLfilter_By_Admin
            Sheet adminSlSheet = workbook.createSheet("CohortSLfilter_By_Admin");
            createAdminSlFilterData(adminSlSheet);

            // 11. Trainer_Search_By_Admin
            Sheet adminTrainerSearchSheet = workbook.createSheet("Trainer_Search_By_Admin");
            createAdminTrainerSearchData(adminTrainerSearchSheet);

            // 12. TrainerFilterByStatus_By_Admin
            Sheet adminTrainerStatusSheet = workbook.createSheet("TrainerFilterByStatus_By_Admin");
            createAdminTrainerStatusFilterData(adminTrainerStatusSheet);

            // 13. TrainerRegistration_By_Admin
            Sheet adminTrainerRegSheet = workbook.createSheet("TrainerRegistration_By_Admin");
            createAdminTrainerRegData(adminTrainerRegSheet);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            System.out.println("Successfully generated testdata.xlsx at: " + file.getAbsolutePath());
        }
    }

    private static void createLoginData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("username");
        h.createCell(1).setCellValue("password");
        h.createCell(2).setCellValue("Expected output");

        Object[][] rows = {
                {"admin01", "admin123", "Valid"},
                {"trainer01", "trainer123", "Valid"},
                {"trainer02", "trainer123", "Valid"},
                {"ramya", "", "Invalid"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            r.createCell(0).setCellValue((String) rows[i][0]);
            r.createCell(1).setCellValue((String) rows[i][1]);
            r.createCell(2).setCellValue((String) rows[i][2]);
        }
    }

    private static void createCohortCreationData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("Cohort Code");
        h.createCell(1).setCellValue("Trainees Count");
        h.createCell(2).setCellValue("Required Skills");
        h.createCell(3).setCellValue("Service Line");
        h.createCell(4).setCellValue("Stream");
        h.createCell(5).setCellValue("Start Date");
        h.createCell(6).setCellValue("End Date");
        h.createCell(7).setCellValue("Industry Vertical");
        h.createCell(8).setCellValue("Training Location");
        h.createCell(9).setCellValue("Expected Output");

        Object[][] rows = {
                {"ADM26SD001", "10", "Java", "ADM", "Development", "2026-10-02", "2026-12-31", "BFS", "Chennai", "Cohort Created"},
                {"QEA26NF006", "", "Java", "QEA", "Testing", "2026-08-14", "2026-09-30", "HC", "Hyderabad", "Invalid"},
                {"CS26CS004", "20", "", "CS", "Security", "2026-08-14", "2026-12-31", "INS", "Delhi", "Invalid"},
                {"ADM26CS004", "35", "Spring,SQL", "ADM", "Cloud", "2025-05-26", "2025-04-20", "BFS", "Chennai", "Invalid"},
                {"QEA24SD007", "5", "Python", "QEA", "SDET", "2024-02-29", "2024-05-30", "HC", "Hyderabad", "Invalid"},
                {"QEA26CIS001", "34", "Spring,SQL", "", "Cloud", "2026-10-02", "2027-01-30", "INS", "Delhi", "Invalid"},
                {"CS26CS001", "40", "MongoDB,React", "CS", "", "2025-05-26", "2025-08-26", "BFS", "Chennai", "Invalid"},
                {"QEA26SD002", "20", "Java,Spring", "QEA", "SDET", "2026-01-15", "2026-04-25", "INS", "Hyderabad", "Cohort Created"},
                {"ADM26SD001", "15", "Java,Spring", "ADM", "Development", "2026-10-05", "2027-01-30", "HC", "Delhi", "Invalid"},
                {"CS26CIS01", "25", "Python", "CS", "Security", "2026-08-17", "2026-11-25", "", "Chennai", "Invalid"},
                {"QEA26NF001", "7", "Python", "QEA", "Testing", "2026-05-03", "2026-08-04", "BFS", "", "Invalid"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            for (int j = 0; j < rows[i].length; j++) {
                r.createCell(j).setCellValue((String) rows[i][j]);
            }
        }
    }

    private static void createCohortEditingData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("Cohort Code");
        h.createCell(1).setCellValue("Trainees Count");
        h.createCell(2).setCellValue("Required Skills");
        h.createCell(3).setCellValue("Service Line");
        h.createCell(4).setCellValue("Stream");
        h.createCell(5).setCellValue("Start Date");
        h.createCell(6).setCellValue("End Date");
        h.createCell(7).setCellValue("Training Location");
        h.createCell(8).setCellValue("Expected Output");

        Object[][] rows = {
                {"ADM26SD001", "10", "Java", "ADM", "Development", "2026-10-02", "2026-12-31", "Chennai", "Cohort Created"},
                {"", "", "Java", "QEA", "Testing", "2026-08-14", "2026-09-30", "Hyderabad", "Invalid"},
                {"", "20", "", "CS", "Security", "2026-08-14", "2026-12-31", "Delhi", "Invalid"},
                {"", "35", "Spring,SQL", "ADM", "Cloud", "2025-05-26", "2025-04-20", "Chennai", "Invalid"},
                {"", "5", "Python", "QEA", "SDET", "2024-02-29", "2024-05-30", "Hyderabad", "Invalid"},
                {"", "34", "Spring,SQL", "CS", "Cloud", "2026-10-02", "2027-01-30", "Delhi", "Invalid"},
                {"", "40", "MongoDB,React", "CS", "Cloud", "2025-05-26", "2025-08-26", "Chennai", "Invalid"},
                {"", "20", "Java,Spring", "QEA", "SDET", "2026-01-15", "2026-04-25", "Hyderabad", "Cohort Created"},
                {"", "25", "Python", "CS", "Security", "2026-09-23", "2026-12-28", "Chennai", "Invalid"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            for (int j = 0; j < rows[i].length; j++) {
                r.createCell(j).setCellValue((String) rows[i][j]);
            }
        }
    }

    private static void createCoachSearchData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("CohortCode_Or_Skill_Or_Trainer");
        h.createCell(1).setCellValue("Expected Output");

        Object[][] rows = {
                {"QEA26SD122", "Shows Cohort"},
                {"ADM26CS003", "Empty List"},
                {"Rahul", "Shows 1 Cohort"},
                {"Nihar", "Shows 2 cohorts"},
                {"Ramya", "Empty List"},
                {"Java", "List of Cohorts having java"},
                {"JavaScript", "Empty List"},
                {"Spring", "List of Cohorts having Spring"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            r.createCell(0).setCellValue((String) rows[i][0]);
            r.createCell(1).setCellValue((String) rows[i][1]);
        }
    }

    private static void createCoachStatusFilterData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("Status");
        h.createCell(1).setCellValue("Expected Output");

        Object[][] rows = {
                {"Assigned", "Cohorts List with Assigned Status"},
                {"Unassigned", "Cohorts List with Unassigned Status"},
                {"Pending", "Cohorts List with Pending Status"},
                {"Active", "Cohorts List with Active Status"},
                {"Completed", "Cohorts List with Completed Status"},
                {"Cancelled", "Cohorts List with Cancelled Status"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            r.createCell(0).setCellValue((String) rows[i][0]);
            r.createCell(1).setCellValue((String) rows[i][1]);
        }
    }

    private static void createTrainerSearchData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("CohortCode_or_Skill");
        h.createCell(1).setCellValue("Expected Output");

        Object[][] rows = {
                {"QEA26SD009", "Shows Cohort"},
                {"QEA26SD004", "Shows Cohort"},
                {"Java, Spring Boot", "List of cohort having Java, Spring Boot"},
                {"SQL, python", "Empty List"},
                {"QEA26SD010", "Empty List"},
                {"QEA26SD939", "Shows Cohort"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            r.createCell(0).setCellValue((String) rows[i][0]);
            r.createCell(1).setCellValue((String) rows[i][1]);
        }
    }

    private static void createTrainerStatusFilterData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("Status");
        h.createCell(1).setCellValue("Expected Output");

        Object[][] rows = {
                {"All Statuses", "List of all cohorts"},
                {"Assigned", "List of cohort assigned"},
                {"Active", "List of active cohorts"},
                {"Completed", "List of completed cohorts"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            r.createCell(0).setCellValue((String) rows[i][0]);
            r.createCell(1).setCellValue((String) rows[i][1]);
        }
    }

    private static void createAdminSearchData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("CohortCode_Or_Skill_Or_Trainer_Or_Coach");
        h.createCell(1).setCellValue("Expected Output");

        Object[][] rows = {
                {"QEA26SD122", "Shows Cohort"},
                {"ADM26CS003", "Empty List"},
                {"Rahul", "Shows 1 Cohort"},
                {"Nihar", "Shows 2 cohorts"},
                {"Ramya", "Empty List"},
                {"Java", "List of Cohorts having java"},
                {"JavaScript", "Empty List"},
                {"Spring", "List of Cohorts having Spring"},
                {"Amit", "Shows 6 cohorts"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            r.createCell(0).setCellValue((String) rows[i][0]);
            r.createCell(1).setCellValue((String) rows[i][1]);
        }
    }

    private static void createAdminStatusFilterData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("Status");
        h.createCell(1).setCellValue("Expected Output");

        Object[][] rows = {
                {"Assigned", "Cohorts List with Assigned Status"},
                {"Unassigned", "Cohorts List with Unassigned Status"},
                {"Pending", "Cohorts List with Pending Status"},
                {"Active", "Cohorts List with Active Status"},
                {"Completed", "Cohorts List with Completed Status"},
                {"Cancelled", "Cohorts List with Cancelled Status"},
                {"Processing", "Cohorts List with Processing Status"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            r.createCell(0).setCellValue((String) rows[i][0]);
            r.createCell(1).setCellValue((String) rows[i][1]);
        }
    }

    private static void createAdminSlFilterData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("Service Line");
        h.createCell(1).setCellValue("Expected Output");

        Object[][] rows = {
                {"ADM", "Cohorts List with ADM Service Line"},
                {"QEA", "Cohorts List with QEA Service Line"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            r.createCell(0).setCellValue((String) rows[i][0]);
            r.createCell(1).setCellValue((String) rows[i][1]);
        }
    }

    private static void createAdminTrainerSearchData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("Search by skill/name/service line");
        h.createCell(1).setCellValue("Expected Output");

        Object[][] rows = {
                {"Java", "Shows 2 trainers list"},
                {"java, spring", "Shows 1 trainers list"},
                {"Cobal", "Shows empty list"},
                {"Ramesh", "Shows the trainer"},
                {"Megha", "Shows empty list"},
                {"QEA", "Trainers list in QEA"},
                {"ADM", "Trainers list in ADM"},
                {"XYZ", "Shows empty list"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            r.createCell(0).setCellValue((String) rows[i][0]);
            r.createCell(1).setCellValue((String) rows[i][1]);
        }
    }

    private static void createAdminTrainerStatusFilterData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("Status");
        h.createCell(1).setCellValue("Expected Output");

        Object[][] rows = {
                {"All status", "List of all trainers"},
                {"Available", "List of available trainers"},
                {"Unavailable", "List of unavailable trainers"},
                {"At capacity", "List of trainers with at capacity"},
                {"All status", "List of all trainers"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            r.createCell(0).setCellValue((String) rows[i][0]);
            r.createCell(1).setCellValue((String) rows[i][1]);
        }
    }

    private static void createAdminTrainerRegData(Sheet sheet) {
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("Full Name");
        h.createCell(1).setCellValue("Email");
        h.createCell(2).setCellValue("Skill Set");
        h.createCell(3).setCellValue("Experience");
        h.createCell(4).setCellValue("Available from");
        h.createCell(5).setCellValue("Available Till");
        h.createCell(6).setCellValue("Max Workload");
        h.createCell(7).setCellValue("Expected output");

        Object[][] rows = {
                {"", "ramesh.c@cognizant.com", "Java, SpringBoot", "3", "2026-10-06", "2027-04-04", "5", "Validation failed"},
                {"Rajesh", "", "Python, Machine Learning", "6", "2026-11-17", "2027-03-08", "4", "Validation failed"},
                {"Pooja", "Pooja@cognizant.com", "", "1", "2026-07-13", "2026-12-09", "5", "Validation failed"},
                {"Manu", "manu@cognizant.com", "Data Science, Python", "2", "2026-03-27", "2029-09-17", "-2", "Validation failed"},
                {"Vaishnavi", "vaishnavi@cognizant.com", "Java, SpringBoot, SQL", "3", "2026-06-15", "", "3", "Validation failed"},
                {"ramesh", "ramesh.c@cognizant.com", "Java, SpringBoot", "3", "", "2027-04-04", "5", "Validation failed"},
                {"Mani", "mani@cognizant.com", "Python, ETL", "2", "2027-07-28", "2026-08-15", "3", "Validation failed(start date>end date)"},
                {"Akash", "akash@cognizant.com", "Cobal", "4", "2026-10-06", "2027-04-04", "5", "Trainer added successfully"}
        };
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            for (int j = 0; j < rows[i].length; j++) {
                r.createCell(j).setCellValue((String) rows[i][j]);
            }
        }
    }
}
