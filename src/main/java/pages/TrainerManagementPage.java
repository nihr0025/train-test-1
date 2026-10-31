package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.LoggerUtils;

import java.util.List;

/**
 * TrainerManagementPage POM for Admin's trainer pool management (/admin/trainers).
 * Locators verified against live Angular app component (app-admin-trainers).
 */
public class TrainerManagementPage extends BasePage {

    private final By pageTitle = By.xpath("//h1[contains(@class, 'page-title') and contains(., 'Trainers Management')] | //h1[contains(., 'Trainers Management')]");
    private final By registerTrainerBtn = By.xpath("//button[contains(., 'Register New Trainer')]");
    // Live placeholder: "Search by trainer name, skill, or service line..."
    private final By searchInput = By.cssSelector("input[placeholder*='Search by trainer name'], .content-card input[type='text']");
    private final By statusFilterSelect = By.xpath("//select[contains(@class, 'form-select')][.//option[@value='AVAILABLE'] or .//option[text()='All Statuses']]");
    private final By trainerRows = By.cssSelector("table tbody tr");
    private final By emptyStateRow = By.xpath("//table//tr//td[contains(., 'No trainers found')] | //table//h6[contains(text(), 'No trainers found')]");

    // Modal: Trainer Form (app-trainer-form-modal)
    private final By formModal = By.cssSelector("app-trainer-form-modal .modal-card, app-trainer-form-modal");
    private final By modalTitle = By.cssSelector("app-trainer-form-modal .modal-title");
    private final By nameInput = By.cssSelector("app-trainer-form-modal input[name='name']");
    private final By emailInput = By.cssSelector("app-trainer-form-modal input[name='email']");
    private final By skillSetInput = By.cssSelector("app-trainer-form-modal input[name='skillSet']");
    private final By experienceYearsInput = By.cssSelector("app-trainer-form-modal input[name='experienceYears']");
    private final By availableFromInput = By.cssSelector("app-trainer-form-modal input[name='availableFrom']");
    private final By availableTillInput = By.cssSelector("app-trainer-form-modal input[name='availableTill']");
    private final By maxWorkloadInput = By.cssSelector("app-trainer-form-modal input[name='maxWorkload']");
    private final By submitTrainerBtn = By.cssSelector("app-trainer-form-modal button[type='submit']");
    private final By modalErrorAlert = By.cssSelector("app-trainer-form-modal .alert-danger");

    public TrainerManagementPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        waitForPageReady(pageTitle, "TrainerManagementPage");
        waitForVisibility(searchInput);
    }

    public boolean isLoaded() {
        try {
            waitForVisibility(pageTitle, 10);
            return getCurrentUrl().contains("/admin/trainers") && isElementDisplayed(searchInput);
        } catch (Exception e) {
            return false;
        }
    }

    public void openRegisterTrainerModal() {
        clickNav(registerTrainerBtn);
        waitForVisibility(formModal);
    }

    public void searchTrainer(String query) {
        LoggerUtils.info(getClass(), "Searching trainers with query: '" + query + "'");
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

    public void filterByAvailabilityStatus(String status) {
        LoggerUtils.info(getClass(), "Filtering trainers by status: " + status);
        selectDropdown(statusFilterSelect, status);
        try {
            Thread.sleep(400);
        } catch (InterruptedException ignored) {}
        waitForAngularNetworkIdle();
    }

    public int getDisplayedTrainersCount() {
        List<WebElement> rows = findElements(trainerRows);
        if (rows.isEmpty()) {
            return 0;
        }
        if (rows.size() == 1 && isElementDisplayed(emptyStateRow)) {
            return 0;
        }
        return rows.size();
    }

    public void fillTrainerDetails(String name, String email, String skills, String exp, String from, String till, String maxWorkload) {
        if (name != null) enterText(nameInput, name);
        if (email != null) enterText(emailInput, email);
        if (skills != null) enterText(skillSetInput, skills);
        if (exp != null) enterText(experienceYearsInput, exp);
        if (from != null) enterDate(availableFromInput, from);
        if (till != null) enterDate(availableTillInput, till);
        if (maxWorkload != null) enterText(maxWorkloadInput, maxWorkload);
    }

    public void submitTrainerForm() {
        click(submitTrainerBtn);
        waitForAngularNetworkIdle();
    }

    public String getModalErrorMessage() {
        return getText(modalErrorAlert);
    }

    public boolean isModalErrorDisplayed() {
        return isElementDisplayed(modalErrorAlert);
    }

    public boolean isRegisterTrainerModalOpen() {
        return isElementDisplayed(formModal);
    }

    public void closeRegisterTrainerModal() {
        By cancelBtn = By.xpath("//app-trainer-form-modal//button[contains(., 'Cancel') or contains(@class, 'btn-close')]");
        try {
            click(cancelBtn);
            waitForInvisibility(formModal);
        } catch (Exception ignored) {}
    }

    public void openEditTrainer(String trainerName) {
        By editBtn;
        if (trainerName == null || trainerName.trim().isEmpty()) {
            editBtn = By.cssSelector("table tbody tr button.btn-edit, table tbody tr .btn-edit");
        } else {
            editBtn = By.xpath("//tr[.//td[contains(text(), '" + trainerName + "')]]//button[contains(@class, 'btn-edit')]");
        }
        clickNav(editBtn);
        waitForVisibility(formModal);
    }
}
