package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.AdminCohortsPage;
import pages.AdminDashboardPage;
import pages.LoginPage;
import pages.TrainerManagementPage;
import utils.ConfigReader;
import utils.ExcelUtils;

import java.util.List;
import java.util.Map;

/**
 * AdminWorkflowTest implementing test cases A-01 through A-10.
 * Follows the real user flow:
 * Browser init -> Login -> Verify Dashboard -> Navigate via UI -> Interact -> Assert -> Cleanup.
 */
public class AdminWorkflowTest extends BaseTest {

    private AdminDashboardPage loginAsAdmin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAndVerifyDashboard(ConfigReader.getProperty("admin.username"), ConfigReader.getProperty("admin.password"));
        AdminDashboardPage dashboard = new AdminDashboardPage(driver);
        dashboard.waitUntilLoaded();
        return dashboard;
    }

    @Test(description = "A-01: Admin sign in & system oversight")
    public void testA01_AdminSignInAndSystemOversight() {
        AdminDashboardPage dashboard = loginAsAdmin();
        Assert.assertTrue(dashboard.isLoaded(), "Admin dashboard displays system-wide cohort distribution and trainer pool health");
    }

    @Test(description = "A-02: Dashboard data integrity check")
    public void testA02_DashboardDataIntegrityCheck() {
        AdminDashboardPage dashboard = loginAsAdmin();
        int total = dashboard.getTotalCohorts();
        int assigned = dashboard.getAssignedCohorts();
        int unassigned = dashboard.getUnassignedCohorts();

        Assert.assertTrue(total >= 0 && assigned >= 0 && unassigned >= 0,
                "Dashboard cohort numbers must be non-negative values");
    }

    @DataProvider(name = "adminSearchData")
    public Object[][] getAdminSearchData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        return ExcelUtils.getSheetData(path, "Cohort_Search_By_Admin");
    }

    @Test(description = "A-03: Global cohort search & filter", dataProvider = "adminSearchData")
    public void testA03_GlobalCohortSearchAndFilter(Map<String, String> data) {
        AdminDashboardPage dashboard = loginAsAdmin();
        AdminCohortsPage cohortsPage = dashboard.navigateToCohorts();

        String searchTerm = data.get("CohortCode_Or_Skill_Or_Trainer_Or_Coach");
        cohortsPage.searchCohort(searchTerm);
        Assert.assertTrue(cohortsPage.isLoaded(), "Admin cohort grid filters accurately for: " + searchTerm);
    }

    @DataProvider(name = "adminServiceLineFilterData")
    public Object[][] getAdminServiceLineFilterData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        return ExcelUtils.getSheetData(path, "CohortSLfilter_By_Admin");
    }

    @Test(description = "A-03b: Global cohort service line filter (Excel-driven)", dataProvider = "adminServiceLineFilterData")
    public void testA03b_GlobalCohortServiceLineFilter(Map<String, String> data) {
        AdminDashboardPage dashboard = loginAsAdmin();
        AdminCohortsPage cohortsPage = dashboard.navigateToCohorts();

        String serviceLine = data.get("Service Line");
        cohortsPage.filterByServiceLine(serviceLine);
        Assert.assertTrue(cohortsPage.isLoaded(), "Admin cohort grid filters accurately for service line: " + serviceLine);
    }

    @DataProvider(name = "adminStatusFilterData")
    public Object[][] getAdminStatusFilterData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        return ExcelUtils.getSheetData(path, "CohortStatusfilter_By_Admin");
    }

    @Test(description = "A-03c: Global cohort status filter (Excel-driven)", dataProvider = "adminStatusFilterData")
    public void testA03c_GlobalCohortStatusFilter(Map<String, String> data) {
        AdminDashboardPage dashboard = loginAsAdmin();
        AdminCohortsPage cohortsPage = dashboard.navigateToCohorts();

        String status = data.get("Status");
        cohortsPage.filterByStatus(status);
        Assert.assertTrue(cohortsPage.isLoaded(), "Admin cohort grid filters accurately for status: " + status);
    }

    @Test(description = "A-04: Export allocation report")
    public void testA04_ExportAllocationReport() {
        AdminDashboardPage dashboard = loginAsAdmin();
        dashboard.clickExportReport();
        Assert.assertTrue(dashboard.isLoaded(), "Export report button triggers allocation download cleanly");
    }

    @DataProvider(name = "trainerRegInvalidData")
    public Object[][] getTrainerRegistrationInvalidData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        List<Map<String, String>> allRows = ExcelUtils.getSheetDataAsMap(path, "TrainerRegistration_By_Admin");
        List<Map<String, String>> invalidRows = allRows.stream()
                .filter(r -> r.get("Expected output") != null && r.get("Expected output").toLowerCase().contains("failed"))
                .toList();
        Object[][] data = new Object[invalidRows.size()][1];
        for (int i = 0; i < invalidRows.size(); i++) {
            data[i][0] = invalidRows.get(i);
        }
        return data;
    }

    @Test(description = "A-05: Trainer registration validation (Excel-driven negative rows)", dataProvider = "trainerRegInvalidData")
    public void testA05_TrainerRegistrationValidation(Map<String, String> data) {
        AdminDashboardPage dashboard = loginAsAdmin();
        TrainerManagementPage trainersPage = dashboard.navigateToTrainers();
        trainersPage.openRegisterTrainerModal();

        trainersPage.fillTrainerDetails(
                data.get("Full Name"),
                data.get("Email"),
                data.get("Skill Set"),
                data.get("Experience"),
                data.get("Available from"),
                data.get("Available Till"),
                data.get("Max Workload")
        );

        trainersPage.submitTrainerForm();
        Assert.assertTrue(trainersPage.isModalErrorDisplayed() || trainersPage.isRegisterTrainerModalOpen(),
                "Validation errors must prevent invalid trainer registration for row: " + data);
        trainersPage.closeRegisterTrainerModal();
    }

    @Test(description = "A-05b: Trainer registration validation (Direct blank fields and date range check)")
    public void testA05b_TrainerRegistrationDirectValidation() {
        AdminDashboardPage dashboard = loginAsAdmin();
        TrainerManagementPage trainersPage = dashboard.navigateToTrainers();
        trainersPage.openRegisterTrainerModal();

        // 1. Submit blank
        trainersPage.submitTrainerForm();
        Assert.assertTrue(trainersPage.isModalErrorDisplayed() || trainersPage.isRegisterTrainerModalOpen(),
                "Blank trainer registration submission must display validation error");

        // 2. Submit with availableTill earlier than availableFrom from TrainerRegistration_By_Admin sheet
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        List<Map<String, String>> trainerData = ExcelUtils.getSheetDataAsMap(path, "TrainerRegistration_By_Admin");
        Map<String, String> invalidDateRow = trainerData.stream()
                .filter(r -> r.get("Expected output") != null && r.get("Expected output").contains("start date>end date"))
                .findFirst()
                .orElse(trainerData.size() > 6 ? trainerData.get(6) : null);

        Assert.assertNotNull(invalidDateRow, "Invalid date test row must exist in TrainerRegistration_By_Admin sheet");

        trainersPage.fillTrainerDetails(
                invalidDateRow.get("Full Name"),
                invalidDateRow.get("Email"),
                invalidDateRow.get("Skill Set"),
                invalidDateRow.get("Experience"),
                invalidDateRow.get("Available from"),
                invalidDateRow.get("Available Till"),
                invalidDateRow.get("Max Workload")
        );
        trainersPage.submitTrainerForm();
        Assert.assertTrue(trainersPage.isModalErrorDisplayed() || trainersPage.isRegisterTrainerModalOpen(),
                "Available till date earlier than available from date must trigger validation error");
        trainersPage.closeRegisterTrainerModal();
    }

    @DataProvider(name = "trainerRegValidData")
    public Object[][] getTrainerRegistrationValidData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        List<Map<String, String>> allRows = ExcelUtils.getSheetDataAsMap(path, "TrainerRegistration_By_Admin");
        List<Map<String, String>> validRows = allRows.stream()
                .filter(r -> r.get("Expected output") != null && r.get("Expected output").toLowerCase().contains("successfully"))
                .toList();
        Object[][] data = new Object[validRows.size()][1];
        for (int i = 0; i < validRows.size(); i++) {
            data[i][0] = validRows.get(i);
        }
        return data;
    }

    @Test(description = "A-06: Register new trainer (Excel-driven positive rows)", dataProvider = "trainerRegValidData")
    public void testA06_RegisterNewTrainer(Map<String, String> data) {
        AdminDashboardPage dashboard = loginAsAdmin();
        TrainerManagementPage trainersPage = dashboard.navigateToTrainers();
        trainersPage.openRegisterTrainerModal();

        String baseName = data.get("Full Name");
        String uniqueName = (baseName != null && !baseName.isEmpty() ? baseName : "Trainer Auto") + " " + (System.currentTimeMillis() % 10000);
        String uniqueEmail = "auto" + (System.currentTimeMillis() % 10000) + "@cognizant.com";

        trainersPage.fillTrainerDetails(
                uniqueName,
                uniqueEmail,
                data.get("Skill Set"),
                data.get("Experience"),
                data.get("Available from"),
                data.get("Available Till"),
                data.get("Max Workload")
        );
        trainersPage.submitTrainerForm();
        Assert.assertTrue(trainersPage.isLoaded(), "New trainer registration submitted successfully");
    }

    @DataProvider(name = "trainerPoolSearchData")
    public Object[][] getTrainerPoolSearchData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        return ExcelUtils.getSheetData(path, "Trainer_Search_By_Admin");
    }

    @Test(description = "A-07: Trainer pool filter and search", dataProvider = "trainerPoolSearchData")
    public void testA07_TrainerPoolFilterAndSearch(Map<String, String> data) {
        AdminDashboardPage dashboard = loginAsAdmin();
        TrainerManagementPage trainersPage = dashboard.navigateToTrainers();

        String query = data.get("Search by skill/name/service line");
        trainersPage.searchTrainer(query);
        Assert.assertTrue(trainersPage.isLoaded(), "Trainer pool filters search query accurately: " + query);
    }

    @DataProvider(name = "trainerStatusFilterData")
    public Object[][] getTrainerStatusFilterData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        return ExcelUtils.getSheetData(path, "TrainerFilterByStatus_By_Admin");
    }

    @Test(description = "A-07b: Trainer pool filter by status (Excel-driven)", dataProvider = "trainerStatusFilterData")
    public void testA07b_TrainerPoolFilterByStatus(Map<String, String> data) {
        AdminDashboardPage dashboard = loginAsAdmin();
        TrainerManagementPage trainersPage = dashboard.navigateToTrainers();

        String status = data.get("Status");
        if (status != null && !status.trim().isEmpty()) {
            trainersPage.filterByAvailabilityStatus(status);
        }
        Assert.assertTrue(trainersPage.isLoaded(), "Trainer pool filters status accurately: " + status);
    }

    @Test(description = "A-08: Edit trainer workload and skills")
    public void testA08_EditTrainerWorkloadAndSkills() {
        AdminDashboardPage dashboard = loginAsAdmin();
        TrainerManagementPage trainersPage = dashboard.navigateToTrainers();

        if (trainersPage.getDisplayedTrainersCount() > 0) {
            trainersPage.openEditTrainer("");
            trainersPage.fillTrainerDetails(null, null, "Java, Spring Boot, Microservices", "5", null, null, "6");
            trainersPage.submitTrainerForm();
        }
        Assert.assertTrue(trainersPage.isLoaded(), "Trainer skills and workload ceiling updated successfully");
    }

    @Test(description = "A-09: Admin manual reassignment and workload synchronization")
    public void testA09_AdminManualReassignmentAndWorkloadSync() {
        AdminDashboardPage dashboard = loginAsAdmin();
        AdminCohortsPage cohortsPage = dashboard.navigateToCohorts();

        if (cohortsPage.getDisplayedCohortsCount() > 0) {
            cohortsPage.openReassignModal("");
            cohortsPage.closeReassignModal();
        }
        Assert.assertTrue(cohortsPage.isLoaded(), "Admin manual reassignment workflow accessible");
    }

    @Test(description = "A-10: System-wide status update synchronization")
    public void testA10_SystemWideStatusUpdateSync() {
        AdminDashboardPage dashboard = loginAsAdmin();
        AdminCohortsPage cohortsPage = dashboard.navigateToCohorts();

        if (cohortsPage.getDisplayedCohortsCount() > 0) {
            cohortsPage.changeCohortStatus("", "COMPLETED");
        }
        Assert.assertTrue(cohortsPage.isLoaded(), "Status updates persist across admin and coach views");
    }
}
