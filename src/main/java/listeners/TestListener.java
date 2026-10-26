package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.LoggerUtils;
import utils.ScreenshotUtils;

import java.io.File;

/**
 * TestListener generating ExtentReports HTML, capturing automatic failure screenshots,
 * and maintaining test execution metrics.
 */
public class TestListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

    @Override
    public synchronized void onStart(ITestContext context) {
        if (extent == null) {
            String reportPath = ConfigReader.getProperty("report.path", "reports/ExtentReport.html");
            File reportFile = new File(reportPath);
            File parentDir = reportFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("TrainMate Automation Test Report");
            sparkReporter.config().setReportName("TrainMate Role-Based Regression Suite Execution");
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setEncoding("UTF-8");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("Application", "TrainMate Auto-Allocation Portal");
            extent.setSystemInfo("Environment", "QA / Vercel Live");
            extent.setSystemInfo("Browser", ConfigReader.getBrowser());
            extent.setSystemInfo("Headless", String.valueOf(ConfigReader.isHeadless()));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));

            LoggerUtils.info(TestListener.class, "ExtentReports initialized at: " + reportFile.getAbsolutePath());
        }
    }

    @Override
    public synchronized void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        if (description == null || description.isEmpty()) {
            description = testName;
        }

        ExtentTest test = extent.createTest(description);
        testNode.set(test);
        System.out.println("\n--------------------------------------------------");
        System.out.println("[TEST STARTED] " + testName + " | " + description);
        System.out.println("--------------------------------------------------");
        LoggerUtils.info(TestListener.class, "=== STARTED TEST: " + testName + " (" + description + ") ===");
    }

    @Override
    public synchronized void onTestSuccess(ITestResult result) {
        ExtentTest test = testNode.get();
        if (test != null) {
            test.log(Status.PASS, "Test executed successfully: " + result.getName());
        }
        System.out.println("[TEST PASSED]  " + result.getName() + " (Duration: " + (result.getEndMillis() - result.getStartMillis()) + " ms)");
        LoggerUtils.info(TestListener.class, "=== PASSED TEST: " + result.getName() + " ===");
    }

    @Override
    public synchronized void onTestFailure(ITestResult result) {
        ExtentTest test = testNode.get();
        WebDriver driver = DriverFactory.getDriver();
        String screenshotPath = null;

        if (driver != null) {
            screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getName());
        }

        if (test != null) {
            test.log(Status.FAIL, "Test Failed: " + result.getThrowable().getMessage());
            if (screenshotPath != null) {
                try {
                    test.fail("Failure Screenshot",
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                } catch (Exception e) {
                    test.log(Status.INFO, "Could not attach screenshot to ExtentReport: " + e.getMessage());
                }
            }
        }
        System.err.println("[TEST FAILED]  " + result.getName() + " -> Reason: " + result.getThrowable().getMessage());
        if (screenshotPath != null) {
            System.err.println("               Screenshot saved at: " + screenshotPath);
        }
        LoggerUtils.error(TestListener.class, "=== FAILED TEST: " + result.getName() + " ===", result.getThrowable());
    }

    @Override
    public synchronized void onTestSkipped(ITestResult result) {
        ExtentTest test = testNode.get();
        if (test != null) {
            test.log(Status.SKIP, "Test Skipped: " + (result.getThrowable() != null ? result.getThrowable().getMessage() : "Preconditions"));
        }
        System.out.println("[TEST SKIPPED] " + result.getName());
        LoggerUtils.warn(TestListener.class, "=== SKIPPED TEST: " + result.getName() + " ===");
    }

    @Override
    public synchronized void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
            LoggerUtils.info(TestListener.class, "ExtentReports flushed and saved.");
        }
    }
}
