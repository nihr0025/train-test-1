package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Explicit wait utilities for reliable Selenium automation without hardcoded thread sleeps.
 */
public class WaitUtils {

    public static WebDriverWait getWait(WebDriver driver, int timeoutInSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    }

    public static WebDriverWait getDefaultWait(WebDriver driver) {
        int timeout = ConfigReader.getIntProperty("timeout.explicit", 15);
        return new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public static WebElement waitForVisibility(WebDriver driver, By locator) {
        return getDefaultWait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForVisibility(WebDriver driver, WebElement element) {
        return getDefaultWait(driver).until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForClickability(WebDriver driver, By locator) {
        return getDefaultWait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForClickability(WebDriver driver, WebElement element) {
        return getDefaultWait(driver).until(ExpectedConditions.elementToBeClickable(element));
    }

    public static boolean waitForInvisibility(WebDriver driver, By locator) {
        return getDefaultWait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean waitForUrlContains(WebDriver driver, String fraction) {
        return getDefaultWait(driver).until(ExpectedConditions.urlContains(fraction));
    }

    public static List<WebElement> waitForAllVisible(WebDriver driver, By locator) {
        return getDefaultWait(driver).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }
}
