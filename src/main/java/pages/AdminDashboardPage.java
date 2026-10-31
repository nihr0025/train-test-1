package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.LoggerUtils;

/**
 * AdminDashboardPage POM for Admin oversight portal (/admin/dashboard).
 * Includes reusable page readiness checks and UI navigation methods that wait for destination pages.
 */
public class AdminDashboardPage extends BasePage {

    private final By pageTitle = By.xpath("//h1[contains(@class, 'page-title') and contains(text(), 'Admin Dashboard')] | //h1[contains(@class, 'page-title')]");
    private final By exportReportBtn = By.xpath("//button[contains(., 'Export Report')]");
    private final By manageCohortsBtn = By.xpath("//a[contains(@href, '/admin/cohorts') or contains(., 'Manage Cohorts')]");
    private final By inspectTrainersBtn = By.xpath("//a[contains(@href, '/admin/trainers') or contains(., 'Inspect Trainer Workloads')]");
    private final By sidebarAllCohortsLink = By.xpath("//aside//a[contains(@href, '/admin/cohorts') or contains(., 'All Cohorts')]");
    private final By sidebarTrainersPoolLink = By.xpath("//aside//a[contains(@href, '/admin/trainers') or contains(., 'Trainers Pool')]");

    // Stats Grid
    private final By totalCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Total Cohorts']]//div[contains(@class, 'stat-value')]");
    private final By assignedCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Assigned Cohorts']]//div[contains(@class, 'stat-value')]");
    private final By unassignedCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Unassigned Cohorts']]//div[contains(@class, 'stat-value')]");
    private final By availableTrainersStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Available Trainers']]//div[contains(@class, 'stat-value')]");

    // Distribution
    private final By activeCohortsDistribution = By.xpath("//span[contains(text(), 'Active (In Progress)')]/following-sibling::span");
    private final By totalRegisteredTrainers = By.xpath("//span[contains(text(), 'Total Registered Trainers')]/following-sibling::span");

    public AdminDashboardPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        waitForPageReady(pageTitle, "AdminDashboardPage");
    }

    public boolean isLoaded() {
        try {
            waitForVisibility(pageTitle, 10);
            return getCurrentUrl().contains("/admin");
        } catch (Exception e) {
            return false;
        }
    }

    public void clickExportReport() {
        click(exportReportBtn);
    }

    /**
     * Navigates to AdminCohortsPage through the real UI button or sidebar link
     * and guarantees the destination page is ready before returning.
     */
    public AdminCohortsPage navigateToCohorts() {
        LoggerUtils.info(getClass(), "Navigating to Admin Cohorts page via UI...");
        By navTarget = By.xpath("//a[contains(@href, '/admin/cohorts') and contains(., 'All Cohorts')] | //aside//a[contains(@href, '/admin/cohorts')] | //a[contains(@href, '/admin/cohorts')]");
        clickNav(navTarget);
        AdminCohortsPage cohortsPage = new AdminCohortsPage(driver);
        cohortsPage.waitUntilLoaded();
        return cohortsPage;
    }

    /**
     * Navigates to TrainerManagementPage through the real UI button or sidebar link
     * and guarantees the destination page is ready before returning.
     */
    public TrainerManagementPage navigateToTrainers() {
        LoggerUtils.info(getClass(), "Navigating to Trainer Management page via UI...");
        By navTarget = By.xpath("//a[contains(@href, '/admin/trainers') and contains(., 'Trainers Pool')] | //aside//a[contains(@href, '/admin/trainers')] | //a[contains(@href, '/admin/trainers')]");
        clickNav(navTarget);
        TrainerManagementPage trainersPage = new TrainerManagementPage(driver);
        trainersPage.waitUntilLoaded();
        return trainersPage;
    }

    public int getTotalCohorts() {
        try {
            return Integer.parseInt(getText(totalCohortsStat).replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getAssignedCohorts() {
        try {
            return Integer.parseInt(getText(assignedCohortsStat).replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getUnassignedCohorts() {
        try {
            return Integer.parseInt(getText(unassignedCohortsStat).replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public String getAvailableTrainersRatio() {
        return getText(availableTrainersStat).trim();
    }

    public int getTotalRegisteredTrainers() {
        try {
            return Integer.parseInt(getText(totalRegisteredTrainers).replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
