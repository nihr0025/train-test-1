package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtils for capturing failure screenshots and saving them in screenshots/ directory.
 */
public class ScreenshotUtils {

    public static String captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            return null;
        }

        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String screenshotDir = ConfigReader.getProperty("screenshot.path", "screenshots/");

            Path dirPath = Paths.get(screenshotDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = sanitizedTestName + "_" + timestamp + ".png";
            Path targetPath = dirPath.resolve(fileName);

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(srcFile.toPath(), targetPath);

            LoggerUtils.info(ScreenshotUtils.class, "Captured failure screenshot: " + targetPath.toAbsolutePath());
            return targetPath.toAbsolutePath().toString();
        } catch (IOException e) {
            LoggerUtils.error(ScreenshotUtils.class, "Failed to capture screenshot: " + e.getMessage(), e);
            return null;
        }
    }
}
