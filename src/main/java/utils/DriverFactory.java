package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * DriverFactory providing ThreadLocal WebDriver instances for isolated and parallel-safe browser execution.
 * Configured with isolated temporary user profiles and disabled password leak detection/save password bubbles.
 */
public class DriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<File> tempProfileThreadLocal = new ThreadLocal<>();

    public static WebDriver initDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        boolean headless = ConfigReader.isHeadless();

        WebDriver driver;
        switch (browser) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("-headless");
                }
                firefoxOptions.addPreference("signon.rememberSignons", false);
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless=new");
                }
                edgeOptions.addArguments("--disable-gpu");
                edgeOptions.addArguments("--no-sandbox");
                edgeOptions.addArguments("--disable-save-password-bubble");
                edgeOptions.addArguments("--disable-features=PasswordLeakDetection,PasswordCheck,AutofillServerCommunication");

                Map<String, Object> edgePrefs = new HashMap<>();
                edgePrefs.put("credentials_enable_service", false);
                edgePrefs.put("profile.password_manager_enabled", false);
                edgePrefs.put("profile.password_manager_leak_detection", false);
                edgePrefs.put("autofill.profile_enabled", false);
                edgePrefs.put("autofill.credit_card_enabled", false);
                edgeOptions.setExperimentalOption("prefs", edgePrefs);

                driver = new EdgeDriver(edgeOptions);
                break;

            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                // Core performance and UI flags
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--window-size=1920,1080");
                chromeOptions.addArguments("--remote-allow-origins=*");

                // Suppress Chrome Password Manager, breach leak detection, and save-password bubbles
                chromeOptions.addArguments("--disable-save-password-bubble");
                chromeOptions.addArguments("--disable-features=PasswordLeakDetection,PasswordCheck,AutofillServerCommunication");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");

                // Use an isolated temporary user profile directory per test session
                try {
                    File tempProfile = Files.createTempDirectory("trainmate_chrome_profile_").toFile();
                    tempProfile.deleteOnExit();
                    tempProfileThreadLocal.set(tempProfile);
                    chromeOptions.addArguments("--user-data-dir=" + tempProfile.getAbsolutePath());
                } catch (IOException e) {
                    LoggerUtils.warn(DriverFactory.class, "Could not create temporary profile directory: " + e.getMessage());
                }

                // Chrome preferences: completely disable credential manager, password saving, and leak alerts
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.password_manager_leak_detection", false);
                prefs.put("autofill.profile_enabled", false);
                prefs.put("autofill.credit_card_enabled", false);
                chromeOptions.setExperimentalOption("prefs", prefs);

                // Exclude automation switch to prevent info bar popups
                chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

                driver = new ChromeDriver(chromeOptions);
                break;
        }

        int pageLoadTimeout = ConfigReader.getIntProperty("timeout.pageload", 30);
        int implicitTimeout = ConfigReader.getIntProperty("timeout.implicit", 10);

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitTimeout));
        driver.manage().window().maximize();

        driverThreadLocal.set(driver);
        LoggerUtils.info(DriverFactory.class, "Initialized " + browser + " driver (Headless: " + headless + ")");
        return driver;
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                LoggerUtils.info(DriverFactory.class, "Driver session closed successfully.");
            } catch (Exception e) {
                LoggerUtils.warn(DriverFactory.class, "Error while quitting driver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }

        // Clean up temporary isolated user profile directory
        File tempProfile = tempProfileThreadLocal.get();
        if (tempProfile != null && tempProfile.exists()) {
            try {
                Files.walk(tempProfile.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                LoggerUtils.info(DriverFactory.class, "Cleaned up temp profile directory.");
            } catch (Exception e) {
                LoggerUtils.warn(DriverFactory.class, "Could not completely delete temp profile directory: " + e.getMessage());
            } finally {
                tempProfileThreadLocal.remove();
            }
        }
    }
}
