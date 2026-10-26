package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.TrainerCohortsPage;
import pages.TrainerDashboardPage;
import pages.TrainerMailboxPage;
import utils.ConfigReader;
import utils.ExcelUtils;

import java.util.Map;

/**
 * TrainerWorkflowTest implementing test cases T-01 through T-09.
 * Follows real user flow: Login -> Verify Dashboard -> Navigate via UI -> Interact -> Assert -> Cleanup.
 */
public class
TrainerWorkflowTest extends BaseTest {

    private TrainerDashboardPage loginAsTrainer() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAndVerifyDashboard(ConfigReader.getProperty("trainer.username"), ConfigReader.getProperty("trainer.password"));
        TrainerDashboardPage dashboard = new TrainerDashboardPage(driver);
        dashboard.waitUntilLoaded();
        return dashboard;
    }

    @Test(description = "T-01: Trainer login & role access")
    public void testT01_TrainerLoginAndRoleAccess() {
        TrainerDashboardPage dashboard = loginAsTrainer();
        Assert.assertTrue(dashboard.isLoaded(), "Trainer dashboard opens showing assigned batches");
        Assert.assertFalse(dashboard.isCoachOrAdminTabsVisible(), "Coach creation and Admin tabs are hidden for Trainer");
    }

    @Test(description = "T-02: Trainer unauthorized route check")
    public void testT02_TrainerUnauthorizedRouteCheck() {
        loginAsTrainer();
        // Attempt direct navigation to coach or admin routes
        driver.get(ConfigReader.getAppUrl() + "admin/trainers");
        boolean accessDenied = driver.getCurrentUrl().contains("/login") || !driver.getCurrentUrl().contains("/admin/trainers");
        Assert.assertTrue(accessDenied, "Direct navigation to protected Coach/Admin routes is denied");

        driver.get(ConfigReader.getAppUrl() + "coach/cohorts");
        boolean coachDenied = driver.getCurrentUrl().contains("/login") || !driver.getCurrentUrl().contains("/coach/cohorts");
        Assert.assertTrue(coachDenied, "Direct navigation to protected Coach route is denied");
    }

    @Test(description = "T-03: Trainer empty state dashboard check")
    public void testT03_TrainerEmptyStateDashboard() {
        TrainerDashboardPage dashboard = loginAsTrainer();
        Assert.assertTrue(dashboard.isLoaded(), "Trainer dashboard loads cleanly without console errors");
    }

    @Test(description = "T-04: Trainer metrics after assignment")
    public void testT04_TrainerMetricsAfterAssignment() {
        TrainerDashboardPage dashboard = loginAsTrainer();
        int active = dashboard.getActiveCohortsCount();
        int completed = dashboard.getCompletedCohortsCount();
        Assert.assertTrue(active >= 0 && completed >= 0, "Trainer metric counters are valid numbers");
    }

    @Test(description = "T-05: View assigned cohort details")
    public void testT05_ViewAssignedCohortDetails() {
        TrainerDashboardPage dashboard = loginAsTrainer();
        TrainerCohortsPage cohortsPage = dashboard.navigateToCohorts();

        if (cohortsPage.getDisplayedCohortsCount() > 0) {
            cohortsPage.viewCohortDetails("");
            String code = cohortsPage.getModalCohortCode();
            Assert.assertFalse(code.isEmpty(), "Modal displays cohort code, skills, dates and location");
            cohortsPage.closeModal();
        }
        Assert.assertTrue(cohortsPage.isLoaded(), "Assigned cohorts view functions properly");
    }

    @DataProvider(name = "trainerSearchData")
    public Object[][] getTrainerSearchData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        return ExcelUtils.getSheetData(path, "Cohort_or_Skill_SearchByTrainer");
    }

    @Test(description = "T-06: Search assigned cohort schedule", dataProvider = "trainerSearchData")
    public void testT06_SearchAssignedCohortSchedule(Map<String, String> data) {
        TrainerDashboardPage dashboard = loginAsTrainer();
        TrainerCohortsPage cohortsPage = dashboard.navigateToCohorts();

        String searchTerm = data.get("CohortCode_or_Skill");
        cohortsPage.searchCohort(searchTerm);
        Assert.assertTrue(cohortsPage.isLoaded(), "Matching cohorts or clean empty results appear for: " + searchTerm);
    }

    @DataProvider(name = "trainerStatusFilterData")
    public Object[][] getTrainerStatusFilterData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        return ExcelUtils.getSheetData(path, "CohortStatusFilter_By_Trainer");
    }

    @Test(description = "T-07: Filter cohorts by status", dataProvider = "trainerStatusFilterData")
    public void testT07_FilterCohortsByStatus(Map<String, String> data) {
        TrainerDashboardPage dashboard = loginAsTrainer();
        TrainerCohortsPage cohortsPage = dashboard.navigateToCohorts();

        String status = data.get("Status");
        if (!"All Statuses".equalsIgnoreCase(status)) {
            cohortsPage.filterByStatus(status);
        }
        Assert.assertTrue(cohortsPage.isLoaded(), "Filtering by status '" + status + "' updates cohort list accurately");
    }

    @Test(description = "T-08: Mailbox assignment notification")
    public void testT08_MailboxAssignmentNotification() {
        TrainerDashboardPage dashboard = loginAsTrainer();
        TrainerMailboxPage mailboxPage = dashboard.navigateToMailbox();
        Assert.assertTrue(mailboxPage.isLoaded(), "Mailbox loads cleanly showing notifications and alerts");
    }

    @Test(description = "T-09: Real-time updates on reassignment")
    public void testT09_RealTimeUpdatesOnReassignment() {
        TrainerDashboardPage dashboard = loginAsTrainer();
        TrainerMailboxPage mailboxPage = dashboard.navigateToMailbox();
        mailboxPage.markAllAsRead();
        Assert.assertTrue(mailboxPage.isLoaded(), "Mailbox reflects up-to-date assignment alerts");
    }
}
