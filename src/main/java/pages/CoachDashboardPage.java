package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.LoggerUtils;

import java.util.List;

/**
 * CoachDashboardPage POM for Coach role dashboard (/coach/dashboard).
 * Includes reusable page readiness checks and UI navigation methods that wait for destination pages.
 */
public class CoachDashboardPage extends BasePage {

    private final By pageTitle = By.xpath("//h1[contains(@class, 'page-title') and contains(text(), 'Coach Dashboard')] | //h1[contains(@class, 'page-title')]");
    private final By createCohortBtn = By.xpath("//button[contains(., 'Create Cohort') or .//i[contains(@class, 'bi-plus-circle-fill')]]");
    private final By uploadBatchBtn = By.xpath("//a[contains(@href, '/coach/upload') or contains(., 'Upload / Batch') or contains(., 'Upload')]");
    private final By myCohortsViewAllBtn = By.xpath("//a[contains(@href, '/coach/cohorts') and contains(text(), 'View All')]");
    private final By sidebarMyCohortsLink = By.xpath("//aside//a[contains(@href, '/coach/cohorts') or contains(., 'My Cohorts')]");
    private final By sidebarAddCohortLink = By.xpath("//aside//a[contains(@href, '/coach/upload') or contains(., 'Add Cohort')]");

    private final By totalCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Total Cohorts']]//div[contains(@class, 'stat-value')]");
    private final By assignedCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Assigned Cohorts']]//div[contains(@class, 'stat-value')]");
    private final By unassignedCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Unassigned Cohorts']]//div[contains(@class, 'stat-value')]");
    private final By recentCohortsTableRows = By.cssSelector("table tbody tr");

    // Modal: Create Cohort (app-create-cohort-modal)
    private final By modal = By.cssSelector("app-create-cohort-modal .modal-card, app-create-cohort-modal");
    private final By modalTitle = By.cssSelector("app-create-cohort-modal .modal-title");
    private final By cohortCodeInput = By.cssSelector("app-create-cohort-modal input[name='cohortCode']");
    private final By numberOfTraineesInput = By.cssSelector("app-create-cohort-modal input[name='numberOfTrainees']");
    private final By requiredSkillInput = By.cssSelector("app-create-cohort-modal input[name='requiredSkill']");
    private final By serviceLineSelect = By.cssSelector("app-create-cohort-modal select[name='serviceLine']");
    private final By streamSelect = By.cssSelector("app-create-cohort-modal select[name='stream']");
    private final By startDateInput = By.cssSelector("app-create-cohort-modal input[name='startDate']");
    private final By endDateInput = By.cssSelector("app-create-cohort-modal input[name='endDate']");
    private final By verticalSelect = By.cssSelector("app-create-cohort-modal select[name='vertical']");
    private final By locationSelect = By.cssSelector("app-create-cohort-modal select[name='location']");
    private final By modalSubmitBtn = By.cssSelector("app-create-cohort-modal button[type='submit']");
    private final By modalCancelBtn = By.xpath("//app-create-cohort-modal//button[contains(., 'Cancel')]");
    private final By modalErrorAlert = By.cssSelector("app-create-cohort-modal .alert-danger");

    public CoachDashboardPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        waitForPageReady(pageTitle, "CoachDashboardPage");
    }

    public boolean isDashboardLoaded() {
        try {
            waitForVisibility(pageTitle, 10);
            return getCurrentUrl().contains("/coach") && isElementDisplayed(pageTitle);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Navigates to CohortPage via UI link and ensures page readiness.
     */
    public CohortPage navigateToCohorts() {
        LoggerUtils.info(getClass(), "Navigating to Coach My Cohorts page via UI...");
        By navTarget = isElementDisplayed(myCohortsViewAllBtn) ? myCohortsViewAllBtn : sidebarMyCohortsLink;
        click(navTarget);
        CohortPage cohortPage = new CohortPage(driver);
        cohortPage.waitUntilLoaded();
        return cohortPage;
    }

    /**
     * Navigates to BatchUploadPage via UI link and ensures page readiness.
     */
    public BatchUploadPage navigateToUpload() {
        LoggerUtils.info(getClass(), "Navigating to Batch Upload page via UI...");
        By navTarget = isElementDisplayed(uploadBatchBtn) ? uploadBatchBtn : sidebarAddCohortLink;
        click(navTarget);
        BatchUploadPage uploadPage = new BatchUploadPage(driver);
        uploadPage.waitUntilLoaded();
        return uploadPage;
    }

    public String getPageTitleText() {
        return getText(pageTitle);
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

    public void openCreateCohortModal() {
        click(createCohortBtn);
        waitForVisibility(modal);
    }

    public void fillCohortDetails(String code, String trainees, String skills, String serviceLine,
                                  String stream, String startDate, String endDate, String vertical, String location) {
        if (code != null) enterText(cohortCodeInput, code);
        if (trainees != null) enterText(numberOfTraineesInput, trainees);
        if (skills != null) enterText(requiredSkillInput, skills);
        if (serviceLine != null) selectDropdown(serviceLineSelect, serviceLine);
        if (stream != null) selectDropdown(streamSelect, stream);
        if (startDate != null) enterDate(startDateInput, startDate);
        if (endDate != null) enterDate(endDateInput, endDate);
        if (vertical != null) selectDropdown(verticalSelect, vertical);
        if (location != null) selectDropdown(locationSelect, location);
    }

    public void submitCreateCohort() {
        click(modalSubmitBtn);
        waitForAngularNetworkIdle();
    }

    public String getModalErrorMessage() {
        return getText(modalErrorAlert);
    }

    public boolean isModalErrorDisplayed() {
        return isElementDisplayed(modalErrorAlert);
    }

    public boolean isCreateCohortModalOpen() {
        return isElementDisplayed(modal);
    }

    public void closeCreateCohortModal() {
        try {
            click(modalCancelBtn);
            waitForInvisibility(modal);
        } catch (Exception ignored) {}
    }

    public List<WebElement> getRecentCohortRows() {
        return findElements(recentCohortsTableRows);
    }
}
