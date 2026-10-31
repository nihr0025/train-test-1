package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * CohortPage POM for Coach's My Cohorts view (/coach/cohorts).
 * Locators verified against live Angular app component (app-coach-cohorts).
 */
public class CohortPage extends BasePage {

    private final By pageTitle = By.xpath("//h1[contains(@class, 'page-title') and contains(., 'My Managed Cohorts')] | //h1[contains(., 'My Managed Cohorts')]");
    // Live placeholder: "Search by cohort code, skill, or trainer..."
    private final By searchInput = By.cssSelector("input[placeholder*='Search by cohort code'], .content-card input[type='text']");
    private final By statusFilterSelect = By.xpath("//select[contains(@class, 'form-select')][.//option[@value='ASSIGNED'] or .//option[text()='All Statuses']]");
    private final By clearFiltersBtn = By.xpath("//button[contains(., 'Clear Filters') or contains(., 'Reset')]");
    private final By cohortTableRows = By.cssSelector("table tbody tr");
    private final By emptyStateRow = By.xpath("//table//tr//td[contains(., 'No matching cohorts')] | //table//h6[contains(text(), 'No matching cohorts')]");
    private final By createCohortBtn = By.xpath("//button[contains(., 'Create Cohort')]");

    // Action buttons inside rows
    private final By viewDetailsBtn = By.cssSelector("button.btn-view");
    private final By editCohortBtn = By.cssSelector("button.btn-edit");
    private final By reallocateBtn = By.cssSelector("button.btn-reallocate");
    private final By deleteBtn = By.cssSelector("button.btn-delete");

    // Modal: Edit Cohort (app-edit-cohort-modal)
    private final By editModal = By.cssSelector("app-edit-cohort-modal .modal-card, app-edit-cohort-modal");
    private final By editModalTitle = By.xpath("//app-edit-cohort-modal//h5[contains(text(), 'Edit Cohort')] | //app-edit-cohort-modal .modal-title");
    private final By editSkillsInput = By.cssSelector("app-edit-cohort-modal input[name='requiredSkill']");
    private final By editTraineesInput = By.cssSelector("app-edit-cohort-modal input[name='numberOfTrainees']");
    private final By editStartDateInput = By.cssSelector("app-edit-cohort-modal input[name='startDate']");
    private final By editEndDateInput = By.cssSelector("app-edit-cohort-modal input[name='endDate']");
    private final By editLocationSelect = By.cssSelector("app-edit-cohort-modal select[name='location']");
    private final By editSaveBtn = By.xpath("//app-edit-cohort-modal//button[contains(., 'Save Changes')]");

    // Modal: Cohort Details (app-cohort-details-modal)
    private final By detailsModal = By.cssSelector("app-cohort-details-modal .modal-card, app-cohort-details-modal");
    private final By detailsModalTitle = By.cssSelector("app-cohort-details-modal .modal-title");
    private final By detailsAssignedTrainer = By.xpath("//app-cohort-details-modal//small[contains(text(), 'Assigned Trainer')]/..//h6 | //app-cohort-details-modal//h6[contains(@class, 'fw-bold')]");
    private final By detailsScoreCircle = By.cssSelector(".score-hero-value, app-cohort-details-modal .badge");
    private final By detailsScoreBreakdown = By.xpath("//app-cohort-details-modal//*[contains(text(), '100-Point') or contains(text(), 'Breakdown')]");
    private final By detailsUnassignedMsg = By.xpath("//app-cohort-details-modal//*[contains(text(), 'No trainer assigned') or contains(text(), 'Unassigned')]");
    private final By detailsCloseBtn = By.xpath("//app-cohort-details-modal//button[contains(., 'Close') or contains(@class, 'btn-close')]");

    public CohortPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        waitForPageReady(pageTitle, "CoachCohortPage");
        waitForVisibility(searchInput);
    }

    public boolean isLoaded() {
        try {
            waitForVisibility(pageTitle, 10);
            return getCurrentUrl().contains("/coach/cohorts") && isElementDisplayed(searchInput);
        } catch (Exception e) {
            return false;
        }
    }

    public void searchCohort(String query) {
        LoggerUtils.info(getClass(), "Searching cohorts with query: '" + query + "'");
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
        LoggerUtils.info(getClass(), "Filtering cohorts by status: " + status);
        selectDropdown(statusFilterSelect, status);
        try {
            Thread.sleep(400);
        } catch (InterruptedException ignored) {}
        waitForAngularNetworkIdle();
    }

    public int getDisplayedCohortsCount() {
        List<WebElement> rows = findElements(cohortTableRows);
        if (rows.isEmpty()) {
            return 0;
        }
        if (rows.size() == 1 && isElementDisplayed(emptyStateRow)) {
            return 0;
        }
        return rows.size();
    }

    public List<String> getCohortCodesInTable() {
        List<String> codes = new ArrayList<>();
        List<WebElement> rows = findElements(cohortTableRows);
        for (WebElement row : rows) {
            try {
                WebElement codeCell = row.findElement(By.cssSelector("td[data-label='Cohort Code']"));
                codes.add(codeCell.getText().trim());
            } catch (Exception ignored) {}
        }
        return codes;
    }

    public void openEditCohort(String cohortCode) {
        By editBtnLocator;
        if (cohortCode == null || cohortCode.trim().isEmpty()) {
            editBtnLocator = By.cssSelector("table tbody tr button.btn-edit, table tbody tr .btn-edit");
        } else {
            editBtnLocator = By.xpath("//tr[.//td[contains(text(), '" + cohortCode + "')]]//button[contains(@class, 'btn-edit')]");
        }
        click(editBtnLocator);
        waitForVisibility(editModal);
    }

    public boolean isEditModalOpen() {
        return isElementDisplayed(editModal);
    }

    public void fillCohortSkillsAndDates(String newSkills, String newStartDate, String newEndDate) {
        if (newSkills != null) enterText(editSkillsInput, newSkills);
        if (newStartDate != null) enterDate(editStartDateInput, newStartDate);
        if (newEndDate != null) enterDate(editEndDateInput, newEndDate);
    }

    public void saveEditCohort() {
        click(editSaveBtn);
        waitForAngularNetworkIdle();
    }

    public void cancelEditCohort() {
        By cancelBtn = By.xpath("//app-edit-cohort-modal//button[contains(., 'Cancel') or contains(@class, 'btn-close')]");
        try {
            click(cancelBtn);
            waitForInvisibility(editModal);
        } catch (Exception ignored) {}
    }

    public void updateCohortSkillsAndDates(String newSkills, String newStartDate, String newEndDate) {
        fillCohortSkillsAndDates(newSkills, newStartDate, newEndDate);
        saveEditCohort();
        waitForInvisibility(editModal);
    }

    public void viewCohortDetails(String cohortCode) {
        By viewBtnLocator;
        if (cohortCode == null || cohortCode.trim().isEmpty()) {
            viewBtnLocator = By.cssSelector("table tbody tr button.btn-view, table tbody tr .btn-view");
        } else {
            viewBtnLocator = By.xpath("//tr[.//td[contains(text(), '" + cohortCode + "')]]//button[contains(@class, 'btn-view')]");
        }
        click(viewBtnLocator);
        waitForVisibility(detailsModal);
    }

    public String getAssignedTrainerNameInDetails() {
        try {
            return getText(detailsAssignedTrainer);
        } catch (Exception e) {
            return "";
        }
    }

    public int getSuitabilityScore() {
        try {
            String scoreText = getText(detailsScoreCircle).replaceAll("[^0-9]", "");
            return scoreText.isEmpty() ? 0 : Integer.parseInt(scoreText);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean is100PointScoreBreakdownDisplayed() {
        return isElementDisplayed(detailsScoreBreakdown);
    }

    public boolean isUnassignedMessageDisplayed() {
        return isElementDisplayed(detailsUnassignedMsg);
    }

    public void closeDetailsModal() {
        click(detailsCloseBtn);
        waitForInvisibility(detailsModal);
    }
}
