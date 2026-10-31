package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.LoggerUtils;

import java.time.Duration;

/**
 * LoginPage POM representing https://train-mate-blond.vercel.app/login.
 * Enhanced with asynchronous authentication synchronization to support
 * cloud backend wake-ups and avoid racing against Angular route guards.
 */
public class LoginPage extends BasePage {

    // Locators verified from live Angular app source code
    private final By loginIdInput = By.id("loginId");
    private final By passwordInput = By.id("password");
    private final By signInButton = By.cssSelector("button.btn-signin");
    private final By ssoButton = By.cssSelector("button.btn-sso");
    private final By errorMessageAlert = By.cssSelector("div.login-alert.alert-danger span, div.alert-danger");
    private final By infoMessageAlert = By.cssSelector("div.login-alert.alert-info span");
    private final By togglePasswordBtn = By.cssSelector("button.eye-btn");
    private final By demoAccountsToggle = By.cssSelector("button.demo-toggle");
    private final By demoPills = By.cssSelector("button.demo-pill");
    private final By loginCard = By.cssSelector(".login-card");
    private final By dashboardIndicator = By.cssSelector(".page-title, .user-profile-chip, header.top-navbar");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void enterUsername(String username) {
        enterText(loginIdInput, username);
    }

    public void enterPassword(String password) {
        enterText(passwordInput, password);
    }

    public void clickSignIn() {
        click(signInButton);
    }

    /**
     * Performs login and waits for authentication outcome (either dashboard redirection
     * or validation error display). Avoids navigating away while async auth request is pending.
     */
    public void login(String username, String password) {
        LoggerUtils.info(getClass(), "Attempting login for user: " + username);
        enterUsername(username);
        enterPassword(password);
        clickSignIn();

        // Safely dismiss any unexpected native dialogs or password prompts
        dismissBrowserWarningsIfAny();

        // Wait up to explicitTimeout for either dashboard redirection or an error alert
        try {
            WebDriverWait authWait = new WebDriverWait(driver, Duration.ofSeconds(explicitTimeout));
            authWait.until(d -> {
                dismissBrowserWarningsIfAny();
                String currentUrl = d.getCurrentUrl();
                boolean redirectedToDashboard = currentUrl.contains("/coach") ||
                        currentUrl.contains("/trainer") ||
                        currentUrl.contains("/admin");
                if (redirectedToDashboard) {
                    return true;
                }
                // Check if an error message has appeared
                return isElementDisplayed(errorMessageAlert);
            });
            LoggerUtils.info(getClass(), "Authentication outcome reached; URL=" + getCurrentUrl());
        } catch (TimeoutException e) {
            LoggerUtils.warn(getClass(), "Auth wait timed out; URL=" + getCurrentUrl());
        }
    }

    /**
     * Safely dismisses any unexpected browser warnings, password breach alerts, or dialog overlays
     * without changing account credentials or interrupting automation.
     */
    public void dismissBrowserWarningsIfAny() {
        // 1. Check for native browser alert
        try {
            driver.switchTo().alert().accept();
            LoggerUtils.info(getClass(), "Dismissed unexpected native browser alert.");
        } catch (Exception ignored) {}

        // 2. Check for any web-based password manager or warning dialog OK/Dismiss button
        try {
            java.util.List<WebElement> warningBtns = driver.findElements(By.xpath(
                    "//div[contains(@class, 'modal') or contains(@class, 'alert') or contains(@class, 'dialog') or contains(@class, 'toast')]" +
                    "//button[translate(normalize-space(text()), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='ok' " +
                    "or translate(normalize-space(text()), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='dismiss' " +
                    "or translate(normalize-space(text()), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='close' " +
                    "or contains(@class, 'btn-close')]"
            ));
            for (WebElement btn : warningBtns) {
                if (btn.isDisplayed()) {
                    clickViaJs(btn);
                    LoggerUtils.info(getClass(), "Dismissed browser warning dialog button: " + btn.getText());
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * Logs in and verifies that the destination role dashboard has loaded cleanly.
     */
    public void loginAndVerifyDashboard(String username, String password) {
        login(username, password);
        dismissBrowserWarningsIfAny();
        waitForVisibility(dashboardIndicator, explicitTimeout);
    }

    public String getErrorMessage() {
        try {
            return getText(errorMessageAlert);
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessageAlert);
    }

    public boolean isLoginPageLoaded() {
        return isElementDisplayed(loginIdInput) && isElementDisplayed(signInButton);
    }

    public void clickQuickDemoAccount(String role) {
        if (!isElementDisplayed(demoPills)) {
            click(demoAccountsToggle);
        }
        By pillLocator = By.xpath("//button[contains(@class, 'demo-pill') and contains(text(), '" + role + "')]");
        click(pillLocator);
    }
}
