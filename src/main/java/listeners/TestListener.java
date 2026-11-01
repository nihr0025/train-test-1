package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.DriverFactory;
import utils.ExtentManager;
import utils.LoggerUtils;
import utils.ScreenshotUtils;

/**
 * TestListener generating ExtentReports HTML, capturing automatic failure screenshots,
 * and maintaining test execution metrics.
 */
public class TestListener implements ITestListener, ISuiteListener {

    private static final ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

    @Override
    public void onStart(ISuite suite) {
        ExtentManager.getInstance();
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentManager.flushReport();
    }

    @Override
    public synchronized void onStart(ITestContext context) {
        ExtentManager.getInstance();
    }

    @Override
    public synchronized void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        if (description == null || description.isEmpty()) {
            description = testName;
        }

        ExtentTest test = ExtentManager.getInstance().createTest(description);
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
            test.log(Status.FAIL, "Test Failed: " + (result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown Failure"));
            if (screenshotPath != null) {
                try {
                    test.fail("Failure Screenshot",
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                } catch (Exception e) {
                    test.log(Status.INFO, "Could not attach screenshot to ExtentReport: " + e.getMessage());
                }
            }
        }
        System.err.println("[TEST FAILED]  " + result.getName() + " -> Reason: " + (result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown Failure"));
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
        ExtentManager.flushReport();
    }
}
