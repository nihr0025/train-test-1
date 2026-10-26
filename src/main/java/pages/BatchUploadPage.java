package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.LoggerUtils;

import java.util.List;

/**
 * BatchUploadPage POM for Coach batch Excel uploads (/coach/upload).
 * Locators verified against live Angular app component (app-coach-upload).
 */
public class BatchUploadPage extends BasePage {

    private final By pageTitle = By.xpath("//h1[contains(text(), 'Add & Allocate Cohorts')] | //h1[contains(@class, 'page-title')]");
    private final By manualTabBtn = By.xpath("//button[contains(., 'Create Single Cohort')]");
    private final By batchTabBtn = By.xpath("//button[contains(., 'Batch Upload')]");
    private final By fileInput = By.cssSelector("input[type='file']");
    private final By uploadAndAllocateBtn = By.xpath("//button[contains(., 'Upload & Allocate All Rows') or contains(., 'Processing Batch')]");
    private final By downloadTemplateBtn = By.xpath("//button[contains(., 'Download Sample Excel')]");
    private final By uploadSuccessSummary = By.xpath("//h3[contains(text(), 'Upload Completed')]");
    private final By uploadedRowsCount = By.xpath("//div[text()='Uploaded']/following-sibling::div");
    private final By failedRowsCount = By.xpath("//div[text()='Failed']/following-sibling::div");
    private final By validationErrorsTable = By.xpath("//h6[contains(text(), 'Validation Errors')]/..//table");
    private final By errorMessageAlert = By.cssSelector(".alert-danger");

    public BatchUploadPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        waitForPageReady(pageTitle, "BatchUploadPage");
        waitForVisibility(manualTabBtn);
    }

    public boolean isLoaded() {
        try {
            waitForVisibility(pageTitle, 10);
            return getCurrentUrl().contains("/coach/upload");
        } catch (Exception e) {
            return false;
        }
    }

    public void selectBatchUploadTab() {
        click(batchTabBtn);
    }

    public void uploadFile(String absoluteFilePath) {
        LoggerUtils.info(getClass(), "Selecting Batch Upload tab and setting file: " + absoluteFilePath);
        selectBatchUploadTab();
        // File input is in DOM with class 'd-none'
        WebElement inputEl = driver.findElement(fileInput);
        inputEl.sendKeys(absoluteFilePath);
    }

    public void clickUploadAndAllocate() {
        click(uploadAndAllocateBtn);
        // Wait for upload progress to finish
        try {
            wait.until(d -> isElementDisplayed(uploadSuccessSummary) ||
                    isElementDisplayed(errorMessageAlert) ||
                    isElementDisplayed(validationErrorsTable));
        } catch (Exception ignored) {}
    }

    public boolean isUploadCompleted() {
        return isElementDisplayed(uploadSuccessSummary);
    }

    public int getSuccessfulUploadedCount() {
        try {
            return Integer.parseInt(getText(uploadedRowsCount).trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public int getFailedRowsCount() {
        try {
            return Integer.parseInt(getText(failedRowsCount).trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean areValidationErrorsDisplayed() {
        return isElementDisplayed(validationErrorsTable) || isElementDisplayed(errorMessageAlert);
    }

    public String getErrorMessage() {
        return getText(errorMessageAlert);
    }
}
