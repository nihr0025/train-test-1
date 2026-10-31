package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.LoggerUtils;

/**
 * BaseTest providing robust browser initialization and teardown across test runs.
 */
@Listeners(listeners.TestListener.class)
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        LoggerUtils.info(getClass(), "Starting test setup, initializing browser...");
        driver = DriverFactory.initDriver();
        String appUrl = ConfigReader.getAppUrl();

        int attempts = 3;
        while (attempts > 0) {
            try {
                driver.get(appUrl);
                LoggerUtils.info(getClass(), "Navigated to: " + appUrl);
                break;
            } catch (Exception e) {
                attempts--;
                LoggerUtils.warn(getClass(), "Initial navigation to " + appUrl + " failed (" + e.getMessage() + "). Retrying... Remaining attempts: " + attempts);
                if (attempts == 0) {
                    throw e;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        LoggerUtils.info(getClass(), "Tearing down browser session...");
        DriverFactory.quitDriver();
    }

    public WebDriver getDriver() {
        return driver;
    }
}
