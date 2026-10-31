package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.LoggerUtils;

import java.util.List;

/**
 * TrainerCohortsPage POM for Trainer's assigned batches (/trainer/cohorts).
 * Locators verified against live Angular app component (app-trainer-cohorts).
 */
public class TrainerCohortsPage extends BasePage {

    private final By pageTitle = By.xpath("//h1[contains(@class, 'page-title') and contains(., 'My Assigned Cohorts')] | //h1[contains(., 'My Assigned Cohorts')]");
    // Live placeholder: "Search by cohort code or skill..."
    private final By searchInput = By.cssSelector("input[placeholder*='Search by cohort code'], .content-card input[type='text']");
    private final By statusFilterSelect = By.xpath("//select[contains(@class, 'form-select')][.//option[@value='ASSIGNED'] or .//option[text()='All Statuses']]");
    private final By cohortRows = By.cssSelector("table tbody tr");
    private final By emptyStateRow = By.xpath("//table//tr//td[contains(., 'No cohorts assigned') or contains(., 'No cohorts match')] | //table//h6[contains(text(), 'No cohorts')]");

    // Modal: Cohort Details (app-cohort-details-modal)
    private final By detailsModal = By.cssSelector("app-cohort-details-modal .modal-card, app-cohort-details-modal");
    private final By modalCohortCode = By.cssSelector("app-cohort-details-modal .modal-title");
    private final By modalSkills = By.xpath("//app-cohort-details-modal//*[contains(text(), 'Skill')]/..//span | //app-cohort-details-modal .badge-tag");
    private final By modalLocation = By.xpath("//app-cohort-details-modal//*[contains(text(), 'Location')]/..//span");
    private final By modalDuration = By.xpath("//app-cohort-details-modal//*[contains(text(), 'Duration')]/..//span");
    private final By modalCloseBtn = By.xpath("//app-cohort-details-modal//button[contains(., 'Close') or contains(@class, 'btn-close')]");

    public TrainerCohortsPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        waitForPageReady(pageTitle, "TrainerCohortsPage");
        waitForVisibility(searchInput);
    }

    public boolean isLoaded() {
        try {
            waitForVisibility(pageTitle, 10);
            return getCurrentUrl().contains("/trainer/cohorts") && isElementDisplayed(searchInput);
        } catch (Exception e) {
            return false;
        }
    }

    public void searchCohort(String query) {
        LoggerUtils.info(getClass(), "Searching trainer cohorts with query: '" + query + "'");
        waitForVisibility(searchInput);
        clearField(searchInput);
        if (query != null && !query.trim().isEmpty()) {
            enterText(searchInput, query);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
        waitForAngularNetworkIdle();
    }

    public void filterByStatus(String status) {
        LoggerUtils.info(getClass(), "Filtering trainer cohorts by status: " + status);
        selectDropdown(statusFilterSelect, status);
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

    public void viewCohortDetails(String cohortCode) {
        By viewBtn;
        if (cohortCode == null || cohortCode.trim().isEmpty()) {
            viewBtn = By.cssSelector("table tbody tr button.btn-view, table tbody tr .btn-view");
        } else {
            viewBtn = By.xpath("//tr[.//td[contains(text(), '" + cohortCode + "')]]//button[contains(@class, 'btn-view') or .//i[contains(@class, 'bi-eye')]]");
        }
        click(viewBtn);
        waitForVisibility(detailsModal);
    }

    public String getModalCohortCode() {
        return getText(modalCohortCode);
    }

    public String getModalSkills() {
        return getText(modalSkills);
    }

    public String getModalLocation() {
        return getText(modalLocation);
    }

    public String getModalDuration() {
        return getText(modalDuration);
    }

    public void closeModal() {
        click(modalCloseBtn);
        waitForInvisibility(detailsModal);
    }
}
