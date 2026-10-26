package base;

import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;
import utils.ExcelUtils;
import utils.LoggerUtils;
import utils.ScreenshotUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Enhanced BasePage implementing resilient, diagnostic-rich Selenium interactions.
 * Provides bounded explicit waits, automatic failure triage & diagnostics,
 * Angular ngModel event dispatching, and page readiness synchronization.
 */
public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected int explicitTimeout;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.explicitTimeout = ConfigReader.getIntProperty("timeout.explicit", 20);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitTimeout));
    }

    /**
     * Bounded explicit wait for element visibility with comprehensive diagnostics on failure.
     */
    protected WebElement waitForVisibility(By locator) {
        return waitForVisibility(locator, this.explicitTimeout);
    }

    protected WebElement waitForVisibility(By locator, int timeoutSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            return customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            String diagnostics = captureDiagnostics(locator, "waitForVisibility", e);
            throw new TimeoutException("Timed out after " + timeoutSeconds + "s waiting for visibility of: " + locator
                    + "\n--- Diagnostic Information ---\n" + diagnostics, e);
        }
    }

    /**
     * Bounded explicit wait for element clickability with diagnostics.
     */
    protected WebElement waitForClickable(By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            String diagnostics = captureDiagnostics(locator, "waitForClickable", e);
            throw new TimeoutException("Timed out waiting for element to be clickable: " + locator
                    + "\n--- Diagnostic Information ---\n" + diagnostics, e);
        }
    }

    protected boolean waitForInvisibility(By locator) {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reusable page readiness check waiting for an identifying element and document readyState.
     */
    public void waitForPageReady(By identifyingElement, String pageName) {
        LoggerUtils.info(getClass(), "Waiting for page ready: " + pageName + " using " + identifyingElement);
        try {
            // 1. Wait for HTML document readiness
            wait.until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
            // 2. Wait for identifying element to be visible
            waitForVisibility(identifyingElement, this.explicitTimeout);
            // 3. Wait for any temporary blocking spinners to clear
            waitForAngularNetworkIdle();
        } catch (Exception e) {
            captureDiagnostics(identifyingElement, "waitForPageReady[" + pageName + "]", e);
            throw e;
        }
    }

    /**
     * Wait for Angular background syncing or full-page blocking spinners to subside.
     */
    public void waitForAngularNetworkIdle() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".blocking-action-overlay")));
        } catch (Exception ignored) {}
    }

    /**
     * Click element with bounded wait, stale element retry, and intercepted-click JS fallback.
     */
    protected void click(By locator) {
        try {
            waitForClickable(locator).click();
        } catch (StaleElementReferenceException e) {
            LoggerUtils.warn(getClass(), "StaleElementReference on " + locator + ", retrying click once.");
            waitForClickable(locator).click();
        } catch (ElementClickInterceptedException e) {
            LoggerUtils.warn(getClass(), "Click intercepted on " + locator + ". Scrolling into center and trying JS click.");
            try {
                WebElement element = driver.findElement(locator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                clickViaJs(element);
            } catch (Exception ex) {
                captureDiagnostics(locator, "click[JS Fallback]", ex);
                throw ex;
            }
        } catch (Exception e) {
            captureDiagnostics(locator, "click", e);
            throw e;
        }
    }

    protected void clickViaJs(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Safely clear an input field ensuring it is visible and interactable,
     * then dispatches Angular input/change events to update two-way [(ngModel)] bindings.
     */
    protected void clearField(By locator) {
        try {
            WebElement element = waitForVisibility(locator);
            if (!element.isEnabled()) {
                throw new IllegalStateException("Cannot clear disabled element: " + locator);
            }
            element.click();
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.BACK_SPACE);
            element.clear();
            // Dispatch Angular events to notify [(ngModel)]
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true })); " +
                    "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", element);
        } catch (Exception e) {
            captureDiagnostics(locator, "clearField", e);
            throw e;
        }
    }

    /**
     * Enter text safely with visibility wait, enabled check, clearing, and Angular event dispatching.
     */
    protected void enterText(By locator, String text) {
        try {
            WebElement element = waitForVisibility(locator);
            if (!element.isEnabled()) {
                throw new IllegalStateException("Cannot enter text into disabled element: " + locator);
            }
            try {
                element.click();
            } catch (Exception ignored) {}
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.BACK_SPACE);
            element.clear();
            if (text != null && !text.isEmpty()) {
                element.sendKeys(text);
            }
            // Always dispatch input, change, and blur events so Angular model and validation reflect current state
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true })); " +
                    "arguments[0].dispatchEvent(new Event('change', { bubbles: true })); " +
                    "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));", element);
        } catch (Exception e) {
            captureDiagnostics(locator, "enterText[" + (text != null ? text : "null") + "]", e);
            throw e;
        }
    }

    /**
     * Reusable, robust date-entry method supporting HTML5 <input type="date"> and standard inputs.
     * Prevents Chrome keystroke-appending bugs (e.g. 5-to-8-digit years like 202026),
     * validates 4-digit years, dispatches Angular change events, and asserts DOM reflection.
     */
    public void enterDate(By locator, String dateStr) {
        try {
            WebElement element = waitForVisibility(locator);
            if (!element.isEnabled()) {
                throw new IllegalStateException("Cannot enter date into disabled element: " + locator);
            }

            // Handle blank date (e.g. testing mandatory field validation)
            if (dateStr == null || dateStr.trim().isEmpty()) {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].value = ''; " +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true })); " +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", element);
                LoggerUtils.info(getClass(), "Cleared date field: " + locator);
                return;
            }

            String formattedDate = normalizeDateString(dateStr);

            // 1. Clear any existing value first via JS
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", element);

            // 2. Set the ISO formatted value (yyyy-MM-dd) and dispatch Angular events (including blur for validation)
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1]; " +
                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true })); " +
                    "arguments[0].dispatchEvent(new Event('change', { bubbles: true })); " +
                    "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));",
                    element, formattedDate);

            // 3. Fallback check: If value is not reflected, try keyboard clear + sendKeys
            String actualVal = element.getAttribute("value");
            if (actualVal == null || !actualVal.equals(formattedDate)) {
                try {
                    element.click();
                } catch (Exception ignored) {}
                element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                element.sendKeys(Keys.BACK_SPACE);
                element.clear();
                element.sendKeys(formattedDate);
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true })); " +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", element);
                actualVal = element.getAttribute("value");
            }

            // 4. Verify value
            if (actualVal == null || !actualVal.equals(formattedDate)) {
                throw new AssertionError(String.format(
                        "Date verification failed for element %s: expected '%s' but found '%s'",
                        locator, formattedDate, actualVal));
            }
            LoggerUtils.info(getClass(), "Successfully set and verified date '" + formattedDate + "' in " + locator);
        } catch (Exception e) {
            captureDiagnostics(locator, "enterDate[" + dateStr + "]", e);
            throw e;
        }
    }

    /**
     * Normalizes and validates a date string to ensure a 4-digit year and yyyy-MM-dd format.
     */
    public static String normalizeDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return "";
        }
        String trimmed = dateStr.trim();
        // If already yyyy-MM-dd with 4-digit year
        if (trimmed.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            int year = Integer.parseInt(trimmed.substring(0, 4));
            if (year >= 1900 && year <= 2100) {
                return trimmed;
            }
        }
        LocalDate parsed = ExcelUtils.parseDateSafely(trimmed);
        if (parsed != null) {
            int year = parsed.getYear();
            if (year >= 1900 && year <= 2100) {
                return parsed.format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
        }
        throw new IllegalArgumentException("Invalid date string: '" + dateStr + "'. Year must be 4 digits between 1900 and 2100.");
    }

    protected String getText(By locator) {
        try {
            return waitForVisibility(locator).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    protected boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected boolean isElementEnabled(By locator) {
        try {
            return driver.findElement(locator).isEnabled();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Resilient dropdown selection supporting exact text, exact value, case-insensitive, and partial matching.
     */
    public void selectDropdown(By locator, String valueOrText) {
        if (valueOrText == null || valueOrText.trim().isEmpty()) {
            return;
        }
        WebElement element = waitForVisibility(locator);
        Select select = new Select(element);
        String target = valueOrText.trim();

        // 1. Try exact visible text
        try {
            select.selectByVisibleText(target);
            return;
        } catch (Exception ignored) {}

        // 2. Try exact value attribute
        try {
            select.selectByValue(target);
            return;
        } catch (Exception ignored) {}

        // 3. Try case-insensitive matching on text or value
        for (WebElement option : select.getOptions()) {
            String optText = option.getText().trim();
            String optVal = option.getAttribute("value");
            if (optText.equalsIgnoreCase(target) || (optVal != null && optVal.equalsIgnoreCase(target))) {
                option.click();
                return;
            }
        }

        // 4. Try partial matching
        for (WebElement option : select.getOptions()) {
            String optText = option.getText().trim();
            String optVal = option.getAttribute("value");
            if (optText.toLowerCase().contains(target.toLowerCase()) ||
                    (optVal != null && optVal.toLowerCase().contains(target.toLowerCase()))) {
                option.click();
                return;
            }
        }
    }

    protected void selectByVisibleText(By locator, String visibleText) {
        selectDropdown(locator, visibleText);
    }

    protected void selectByValue(By locator, String value) {
        selectDropdown(locator, value);
    }

    protected String getSelectedOptionText(By locator) {
        WebElement element = waitForVisibility(locator);
        Select select = new Select(element);
        return select.getFirstSelectedOption().getText().trim();
    }

    protected void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    protected void uploadFile(By fileInputLocator, String absoluteFilePath) {
        WebElement fileInput = driver.findElement(fileInputLocator);
        fileInput.sendKeys(absoluteFilePath);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public void navigateTo(String url) {
        LoggerUtils.info(getClass(), "Navigating to URL: " + url);
        driver.get(url);
    }

    public List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    /**
     * Captures exhaustive diagnostic state whenever an element interaction fails:
     * - Current URL & Page Title
     * - Locator details
     * - Whether element exists in DOM (and if visible/enabled/obscured)
     * - Whether redirected to /login or blocked by overlay
     * - Browser console errors
     * - Full failure screenshot
     */
    public String captureDiagnostics(By locator, String action, Exception ex) {
        StringBuilder sb = new StringBuilder();
        try {
            String currentUrl = driver.getCurrentUrl();
            String title = driver.getTitle();
            sb.append("\n  Failed Action: ").append(action);
            sb.append("\n  Target Locator: ").append(locator != null ? locator.toString() : "null");
            sb.append("\n  Current URL: ").append(currentUrl);
            sb.append("\n  Page Title: ").append(title);

            // Check if redirected to login
            if (currentUrl.contains("/login")) {
                sb.append("\n  ** WARNING: Current page is LOGIN PAGE. Authentication may have expired or navigation occurred before login finished.");
            }

            // Check if element exists anywhere in DOM
            if (locator != null) {
                List<WebElement> elements = driver.findElements(locator);
                if (elements.isEmpty()) {
                    sb.append("\n  DOM Presence: Element DOES NOT EXIST in the current DOM.");
                } else {
                    WebElement el = elements.get(0);
                    sb.append("\n  DOM Presence: Element EXISTS (matches=").append(elements.size()).append(").");
                    sb.append(" isDisplayed=").append(el.isDisplayed());
                    sb.append(", isEnabled=").append(el.isEnabled());
                    sb.append(", Location=").append(el.getLocation());
                    sb.append(", Size=").append(el.getSize());
                }
            }

            // Check for blocking overlays or alert dialogs
            List<WebElement> overlays = driver.findElements(By.cssSelector(".blocking-action-overlay, .modal-backdrop, .server-banner"));
            if (!overlays.isEmpty()) {
                sb.append("\n  Active Overlays Detected: ").append(overlays.size()).append(" blocking overlay elements found.");
            }

            // Capture screenshot
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, "DIAGNOSTIC_" + action);
            if (screenshotPath != null) {
                sb.append("\n  Screenshot Captured: ").append(screenshotPath);
            }

            // Capture browser console logs if available
            try {
                LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
                sb.append("\n  Browser Console Errors:");
                boolean hasErrors = false;
                for (LogEntry entry : logEntries) {
                    if (entry.getLevel().getName().equals("SEVERE") || entry.getLevel().getName().equals("WARNING")) {
                        sb.append("\n    [").append(entry.getLevel()).append("] ").append(entry.getMessage());
                        hasErrors = true;
                    }
                }
                if (!hasErrors) {
                    sb.append(" None");
                }
            } catch (Exception ignored) {}

        } catch (Exception e) {
            sb.append("\n  [Diagnostic capture partially failed: ").append(e.getMessage()).append("]");
        }

        String report = sb.toString();
        LoggerUtils.error(getClass(), "=== FAILURE DIAGNOSTIC REPORT ===" + report, ex);
        return report;
    }
}
