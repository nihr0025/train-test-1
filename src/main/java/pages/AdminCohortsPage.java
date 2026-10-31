package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.LoggerUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminCohortsPage POM for Admin's global cohort management and manual reassignment (/admin/cohorts).
 * Locators verified against live Angular application template (app-admin-cohorts).
 */
public class AdminCohortsPage extends BasePage {

    // Locators verified from live app bundle (chunk-LNZ2M7KH.js)
    private final By pageTitle = By.xpath("//h1[contains(@class, 'page-title') and contains(., 'All System Cohorts')] | //h1[contains(., 'All System Cohorts')]");
    // Live placeholder: "Search code, skill, coach, trainer..."
    private final By searchInput = By.cssSelector("input[placeholder*='Search code'], input[placeholder*='Search code, skill'], .content-card input[type='text']");
    private final By statusFilterSelect = By.xpath("//select[contains(@class, 'form-select')][.//option[@value='ASSIGNED'] or .//option[text()='All Statuses']]");
    private final By serviceLineFilterSelect = By.xpath("//select[contains(@class, 'form-select')][.//option[text()='All Service Lines']]");
    private final By exportReportBtn = By.xpath("//button[contains(., 'Export Allocation Report')]");
    private final By cohortRows = By.cssSelector("table tbody tr");
    private final By emptyStateRow = By.xpath("//table//tr//td[contains(., 'No matching cohorts')] | //table//h6[contains(text(), 'No matching cohorts')]");
    private final By resetFiltersBtn = By.xpath("//button[contains(., 'Reset Filters') or contains(., 'Clear All Filters')]");

    // Modal: Reassign Trainer (app-reassign-modal)
    private final By reassignModal = By.cssSelector("app-reassign-modal .modal-card, app-reassign-modal");
    private final By reassignTrainerSelect = By.cssSelector("app-reassign-modal select.form-select");
    private final By reassignReasonInput = By.cssSelector("app-reassign-modal input[type='text']");
    private final By reviewReassignmentBtn = By.xpath("//app-reassign-modal//button[contains(., 'Review Reassignment')]");
    private final By confirmReassignmentBtn = By.xpath("//app-reassign-modal//button[contains(., 'Confirm Reassignment')]");

    public AdminCohortsPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        waitForPageReady(pageTitle, "AdminCohortsPage");
        waitForVisibility(searchInput);
    }

    public boolean isLoaded() {
        try {
            waitForVisibility(pageTitle, 10);
            return getCurrentUrl().contains("/admin/cohorts") && isElementDisplayed(searchInput);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Executes cohort search:
     * 1. Waits for search field to be visible and interactable.
     * 2. Safely clears field and dispatches Angular events.
     * 3. Types search query.
     * 4. Waits for Angular 300ms debounce and server-side filter response.
     */
    public void searchCohort(String query) {
        LoggerUtils.info(getClass(), "Searching cohorts with query: '" + query + "'");
        waitForVisibility(searchInput);
        clearField(searchInput);
        if (query != null && !query.trim().isEmpty()) {
            enterText(searchInput, query);
        }
        // Angular component has debounceTime(300) before triggering loadCohorts()
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
        waitForAngularNetworkIdle();
    }

    public void filterByStatus(String status) {
        LoggerUtils.info(getClass(), "Filtering cohorts by status: " + status);
        selectDropdown(statusFilterSelect, status);
        try {
            Thread.sleep(400);
        } catch (InterruptedException ignored) {}
        waitForAngularNetworkIdle();
    }

    public void filterByServiceLine(String serviceLine) {
        LoggerUtils.info(getClass(), "Filtering cohorts by service line: " + serviceLine);
        selectDropdown(serviceLineFilterSelect, serviceLine);
        try {
            Thread.sleep(400);
        } catch (InterruptedException ignored) {}
        waitForAngularNetworkIdle();
    }

    public int getDisplayedCohortsCount() {
        List<WebElement> rows = findElements(cohortRows);
        if (rows.isEmpty()) {
            return 0;
        }
        if (rows.size() == 1 && isElementDisplayed(emptyStateRow)) {
            return 0;
        }
        return rows.size();
    }

    public void clickExportAllocationReport() {
        click(exportReportBtn);
    }

    public void openReassignModal(String cohortCode) {
        By reassignBtn;
        if (cohortCode == null || cohortCode.trim().isEmpty()) {
            reassignBtn = By.cssSelector("table tbody tr button.btn-reassign, table tbody tr .btn-reassign");
        } else {
            reassignBtn = By.xpath("//tr[.//td[contains(text(), '" + cohortCode + "')]]//button[contains(@class, 'btn-reassign') or .//i[contains(@class, 'bi-arrow-left-right')]]");
        }
        clickNav(reassignBtn);
        waitForVisibility(reassignModal);
    }

    public void reassignTrainer(String trainerName, String reason) {
        selectDropdown(reassignTrainerSelect, trainerName);
        if (reason != null && !reason.trim().isEmpty()) {
            enterText(reassignReasonInput, reason);
        }
        click(reviewReassignmentBtn);
        click(confirmReassignmentBtn);
        waitForInvisibility(reassignModal);
    }

    public void closeReassignModal() {
        By cancelBtn = By.xpath("//app-reassign-modal//button[contains(., 'Cancel') or contains(@class, 'btn-close')]");
        try {
            click(cancelBtn);
            waitForInvisibility(reassignModal);
        } catch (Exception ignored) {}
    }

    public void changeCohortStatus(String cohortCode, String newStatus) {
        By statusSelectLocator;
        if (cohortCode == null || cohortCode.trim().isEmpty()) {
            statusSelectLocator = By.cssSelector("table tbody tr select.status-select");
        } else {
            statusSelectLocator = By.xpath("//tr[.//td[contains(text(), '" + cohortCode + "')]]//select[contains(@class, 'status-select')]");
        }
        selectDropdown(statusSelectLocator, newStatus);
    }
}
