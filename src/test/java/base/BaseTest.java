package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.LoggerUtils;

/**
 * BaseTest providing robust browser initialization and teardown across test runs.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        LoggerUtils.info(getClass(), "Starting test setup, initializing browser...");
        driver = DriverFactory.initDriver();
        String appUrl = ConfigReader.getAppUrl();
        driver.get(appUrl);
        LoggerUtils.info(getClass(), "Navigated to: " + appUrl);
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
