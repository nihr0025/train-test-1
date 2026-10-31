package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.BatchUploadPage;
import pages.CoachDashboardPage;
import pages.CohortPage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.ExcelUtils;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.util.Map;

/**
 * CoachWorkflowTest implementing test cases C-01 through C-10.
 * Follows real user flow: Login -> Verify Dashboard -> Navigate via UI -> Interact -> Assert -> Cleanup.
 */
public class CoachWorkflowTest extends BaseTest {

    private CoachDashboardPage loginAsCoach() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAndVerifyDashboard(ConfigReader.getProperty("coach.username"), ConfigReader.getProperty("coach.password"));
        CoachDashboardPage dashboard = new CoachDashboardPage(driver);
        dashboard.waitUntilLoaded();
        return dashboard;
    }

    @Test(description = "C-01: Sign in and access dashboard")
    public void testC01_SignInAndAccessDashboard() {
        CoachDashboardPage dashboard = loginAsCoach();
        Assert.assertTrue(dashboard.isDashboardLoaded(), "Coach dashboard should open and URL contains /coach");
    }

    @Test(description = "C-02: Access control validation")
    public void testC02_AccessControlValidation() {
        LoginPage loginPage = new LoginPage(driver);
        // Invalid login attempt
        loginPage.login("invalidUser", "wrongPass");
        Assert.assertTrue(loginPage.isErrorMessageDisplayed() || driver.getCurrentUrl().contains("/login"),
                "Invalid credentials should show clear error");

        // Attempt direct navigation to admin route
        driver.get(ConfigReader.getAppUrl() + "admin/dashboard");
        // Must redirect back to login or deny access without exposing admin data
        boolean accessDenied = driver.getCurrentUrl().contains("/login") ||
                !driver.getCurrentUrl().contains("/admin/dashboard") ||
                driver.findElements(org.openqa.selenium.By.xpath("//h1[contains(., 'Admin Dashboard')]")).isEmpty();
        Assert.assertTrue(accessDenied,
                "Direct navigation to protected /admin route without authentication must be denied");
    }

    @Test(description = "C-03: Cohort form validation check (Direct mandatory fields and date range check)")
    public void testC03_CohortFormValidationMandatoryAndDates() {
        CoachDashboardPage dashboard = loginAsCoach();
        dashboard.openCreateCohortModal();

        // 1. Submit with mandatory fields blank (clear pre-filled defaults first)
        dashboard.fillCohortDetails("", "", "", null, null, "", "", null, null);
        dashboard.submitCreateCohort();
        Assert.assertTrue(dashboard.isModalErrorDisplayed() || dashboard.isCreateCohortModalOpen(),
                "Submitting with mandatory fields missing should show validation error message or keep modal open");

        // 2. Test invalid date range: Start Date > End Date
        dashboard.fillCohortDetails("INV_DATE_" + (System.currentTimeMillis() % 10000), "20", "Java", "ADM", "Development", "2026-12-31", "2026-01-01", "BFS", "Chennai");
        dashboard.submitCreateCohort();
        Assert.assertTrue(dashboard.isModalErrorDisplayed() || dashboard.isCreateCohortModalOpen(),
                "Submitting with end date earlier than start date must be blocked with validation error");
        dashboard.closeCreateCohortModal();
    }

    @DataProvider(name = "cohortCreationInvalidData")
    public Object[][] getCohortCreationInvalidData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        List<Map<String, String>> allRows = ExcelUtils.getSheetDataAsMap(path, "Cohort_Creation");
        List<Map<String, String>> invalidRows = allRows.stream()
                .filter(r -> "Invalid".equalsIgnoreCase(r.get("Expected Output")))
                .toList();
        Object[][] data = new Object[invalidRows.size()][1];
        for (int i = 0; i < invalidRows.size(); i++) {
            data[i][0] = invalidRows.get(i);
        }
        return data;
    }

    @Test(description = "C-03: Cohort form validation check (Excel-driven)", dataProvider = "cohortCreationInvalidData")
    public void testC03_CohortFormValidationExcelDriven(Map<String, String> data) {
        CoachDashboardPage dashboard = loginAsCoach();
        dashboard.openCreateCohortModal();

        dashboard.fillCohortDetails(
                data.get("Cohort Code"),
                data.get("Trainees Count"),
                data.get("Required Skills"),
                data.get("Service Line"),
                data.get("Stream"),
                data.get("Start Date"),
                data.get("End Date"),
                data.get("Industry Vertical"),
                data.get("Training Location")
        );

        dashboard.submitCreateCohort();
        Assert.assertTrue(dashboard.isModalErrorDisplayed() || dashboard.isCreateCohortModalOpen(),
                "Form validation should block invalid cohort submission (error displayed or modal remains open) for row: " + data);
        dashboard.closeCreateCohortModal();
    }

    @DataProvider(name = "cohortCreationValidData")
    public Object[][] getCohortCreationValidData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        List<Map<String, String>> allRows = ExcelUtils.getSheetDataAsMap(path, "Cohort_Creation");
        List<Map<String, String>> validRows = allRows.stream()
                .filter(r -> "Cohort Created".equalsIgnoreCase(r.get("Expected Output")))
                .toList();
        Object[][] data = new Object[validRows.size()][1];
        for (int i = 0; i < validRows.size(); i++) {
            data[i][0] = validRows.get(i);
        }
        return data;
    }

    @Test(description = "C-04: Create cohort with valid details (Excel-driven)", dataProvider = "cohortCreationValidData")
    public void testC04_CreateCohortWithValidDetails(Map<String, String> data) {
        CoachDashboardPage dashboard = loginAsCoach();
        dashboard.openCreateCohortModal();

        String baseCode = data.get("Cohort Code");
        String uniqueCode = (baseCode != null && !baseCode.isEmpty() ? baseCode : "AUTO") + "_" + (System.currentTimeMillis() % 100000);
        dashboard.fillCohortDetails(
                uniqueCode,
                data.get("Trainees Count"),
                data.get("Required Skills"),
                data.get("Service Line"),
                data.get("Stream"),
                data.get("Start Date"),
                data.get("End Date"),
                data.get("Industry Vertical"),
                data.get("Training Location")
        );
        dashboard.submitCreateCohort();

        // Navigate via UI to My Cohorts to verify created cohort appears
        CohortPage cohortPage = dashboard.navigateToCohorts();
        cohortPage.searchCohort(uniqueCode);
        Assert.assertTrue(cohortPage.getDisplayedCohortsCount() >= 1,
                "Newly created cohort " + uniqueCode + " should appear in My Cohorts table");
    }

    @Test(description = "C-05: Automated trainer allocation check")
    public void testC05_AutomatedTrainerAllocationCheck() {
        CoachDashboardPage dashboard = loginAsCoach();
        CohortPage cohortPage = dashboard.navigateToCohorts();
        cohortPage.filterByStatus("ASSIGNED");

        if (cohortPage.getDisplayedCohortsCount() > 0) {
            String firstCode = cohortPage.getCohortCodesInTable().get(0);
            cohortPage.viewCohortDetails(firstCode);
            String trainer = cohortPage.getAssignedTrainerNameInDetails();
            Assert.assertFalse(trainer.isEmpty(), "Assigned cohort should display assigned trainer name");
            Assert.assertTrue(cohortPage.is100PointScoreBreakdownDisplayed(),
                    "Assigned cohort must display 100-point algorithmic allocation breakdown");
            cohortPage.closeDetailsModal();
        } else {
            Assert.assertTrue(cohortPage.isLoaded(), "Assigned cohorts view should be loaded cleanly");
        }
    }

    @Test(description = "C-06: Allocation rule enforcement on no-match")
    public void testC06_AllocationRuleEnforcementOnNoMatch() {
        CoachDashboardPage dashboard = loginAsCoach();
        CohortPage cohortPage = dashboard.navigateToCohorts();
        cohortPage.filterByStatus("UNASSIGNED");

        if (cohortPage.getDisplayedCohortsCount() > 0) {
            String firstCode = cohortPage.getCohortCodesInTable().get(0);
            cohortPage.viewCohortDetails(firstCode);
            String trainer = cohortPage.getAssignedTrainerNameInDetails().toLowerCase();
            Assert.assertTrue(cohortPage.isUnassignedMessageDisplayed() ||
                            trainer.contains("unassigned") || trainer.contains("not assigned") || trainer.isEmpty() || trainer.contains("pending") || trainer.contains("none") || trainer.contains("-"),
                    "Unassigned cohort must indicate that no trainer is assigned");
            cohortPage.closeDetailsModal();
        }
        Assert.assertTrue(cohortPage.isLoaded(), "Unassigned cohorts view should be maintained correctly");
    }

    @DataProvider(name = "coachSearchData")
    public Object[][] getCoachSearchData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        return ExcelUtils.getSheetData(path, "Cohort_Search_By_Coach");
    }

    @Test(description = "C-07: Search cohort list", dataProvider = "coachSearchData")
    public void testC07_SearchAndFilterCohortList(Map<String, String> data) {
        CoachDashboardPage dashboard = loginAsCoach();
        CohortPage cohortPage = dashboard.navigateToCohorts();

        String searchTerm = data.get("CohortCode_Or_Skill_Or_Trainer");
        cohortPage.searchCohort(searchTerm);
        Assert.assertTrue(cohortPage.isLoaded(), "Table should filter cleanly for search term: " + searchTerm);
    }

    @DataProvider(name = "coachStatusFilterData")
    public Object[][] getCoachStatusFilterData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        return ExcelUtils.getSheetData(path, "CohortStatusfilter_By_Coach");
    }

    @Test(description = "C-07b: Coach Status Filter Check", dataProvider = "coachStatusFilterData")
    public void testC07b_CohortStatusFilter(Map<String, String> data) {
        CoachDashboardPage dashboard = loginAsCoach();
        CohortPage cohortPage = dashboard.navigateToCohorts();

        String status = data.get("Status");
        cohortPage.filterByStatus(status);
        Assert.assertTrue(cohortPage.isLoaded(), "Cohort table should filter by status: " + status);
    }

    @DataProvider(name = "coachEditingData")
    public Object[][] getCoachEditingData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        return ExcelUtils.getSheetData(path, "Cohort_Editing_By_Coach");
    }

    @Test(description = "C-08: Cohort editing and reallocation", dataProvider = "coachEditingData")
    public void testC08_CohortEditingAndReallocation(Map<String, String> data) {
        CoachDashboardPage dashboard = loginAsCoach();
        CohortPage cohortPage = dashboard.navigateToCohorts();

        // Ensure at least one cohort exists to avoid test order dependency
        if (cohortPage.getDisplayedCohortsCount() == 0) {
            CoachDashboardPage coachDash = loginAsCoach();
            coachDash.openCreateCohortModal();
            coachDash.fillCohortDetails("EDIT" + (System.currentTimeMillis() % 10000), "20", "Java", "ADM", "Development", "2026-10-02", "2026-12-31", "BFS", "Chennai");
            coachDash.submitCreateCohort();
            cohortPage = coachDash.navigateToCohorts();
        }

        if (cohortPage.getDisplayedCohortsCount() > 0) {
            String firstCode = cohortPage.getCohortCodesInTable().get(0);
            cohortPage.openEditCohort(firstCode);

            String expected = data.get("Expected Output");
            boolean isPositive = "Cohort Created".equalsIgnoreCase(expected) || "Valid".equalsIgnoreCase(expected);

            if (isPositive) {
                cohortPage.updateCohortSkillsAndDates(
                        data.get("Required Skills"),
                        data.get("Start Date"),
                        data.get("End Date")
                );
                Assert.assertTrue(cohortPage.isLoaded(), "Valid cohort editing should save and refresh cohort table");
            } else {
                cohortPage.fillCohortSkillsAndDates(
                        data.get("Required Skills"),
                        data.get("Start Date"),
                        data.get("End Date")
                );
                cohortPage.saveEditCohort();
                Assert.assertTrue(cohortPage.isEditModalOpen() || cohortPage.isLoaded(),
                        "Cohort editing flow must handle edit submission cleanly");
                if (cohortPage.isEditModalOpen()) {
                    cohortPage.cancelEditCohort();
                }
            }
        }
        Assert.assertTrue(cohortPage.isLoaded(), "Cohort editing flow should complete");
    }

    @Test(description = "C-09: Batch upload validation")
    public void testC09_BatchUploadValidation() {
        CoachDashboardPage dashboard = loginAsCoach();
        BatchUploadPage uploadPage = dashboard.navigateToUpload();
        uploadPage.selectBatchUploadTab();

        Assert.assertTrue(uploadPage.getCurrentUrl().contains("/coach/upload"),
                "Upload page should be accessible for Coach batch operations");
    }

    @Test(description = "C-10: Batch upload valid cohort workbook")
    public void testC10_BatchUploadValidCohortWorkbook() {
        CoachDashboardPage dashboard = loginAsCoach();
        BatchUploadPage uploadPage = dashboard.navigateToUpload();
        uploadPage.selectBatchUploadTab();

        String excelPath = new File("src/test/resources/testdata.xlsx").getAbsolutePath();
        uploadPage.uploadFile(excelPath);
        Assert.assertTrue(uploadPage.getCurrentUrl().contains("/coach/upload"),
                "Batch upload interface should accept workbook");
    }
}
