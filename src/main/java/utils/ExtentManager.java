package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Centralized ExtentReports Manager ensuring consistent report initialization,
 * absolute path resolution, directory creation, synchronized flushing,
 * and mirrored copy generation for reliable IDE and browser access.
 */
public class ExtentManager {

    private static ExtentReports extent;
    private static File canonicalReportFile;

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            initReports();
        }
        return extent;
    }

    private static void initReports() {
        // Resolve absolute canonical report file path relative to project root
        String configuredPath = ConfigReader.getProperty("report.path", "reports/ExtentReport.html");
        Path projectRoot = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path reportPath = projectRoot.resolve(configuredPath).normalize();
        canonicalReportFile = reportPath.toFile();

        // Ensure parent directory exists
        File parentDir = canonicalReportFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (created) {
                LoggerUtils.info(ExtentManager.class, "Created report directory: " + parentDir.getAbsolutePath());
            }
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(canonicalReportFile);
        sparkReporter.config().setDocumentTitle("TrainMate Automation Test Report");
        sparkReporter.config().setReportName("TrainMate Role-Based Regression Suite Execution");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setEncoding("UTF-8");
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("Application", "TrainMate Auto-Allocation Portal");
        extent.setSystemInfo("Application URL", ConfigReader.getAppUrl());
        extent.setSystemInfo("Environment", "QA / Vercel Live");
        extent.setSystemInfo("Browser", ConfigReader.getBrowser());
        extent.setSystemInfo("Headless Mode", String.valueOf(ConfigReader.isHeadless()));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("OS", System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")");
        extent.setSystemInfo("Report Path", canonicalReportFile.getAbsolutePath());

        LoggerUtils.info(ExtentManager.class, "ExtentReports initialized at: " + canonicalReportFile.getAbsolutePath());
    }

    public static synchronized void flushReport() {
        if (extent != null) {
            extent.flush();
            LoggerUtils.info(ExtentManager.class, "ExtentReports flushed and saved successfully.");

            // Mirror report to target/ directory for Maven and IDE surefire compatibility
            mirrorReportToTargetDirectories();

            printReportLocationBanner();
        }
    }

    private static void mirrorReportToTargetDirectories() {
        if (canonicalReportFile == null || !canonicalReportFile.exists()) {
            return;
        }

        Path projectRoot = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path[] targetPaths = new Path[]{
                projectRoot.resolve("target").resolve("reports").resolve(canonicalReportFile.getName()),
                projectRoot.resolve("target").resolve("surefire-reports").resolve(canonicalReportFile.getName())
        };

        for (Path target : targetPaths) {
            try {
                if (!Files.exists(target.getParent())) {
                    Files.createDirectories(target.getParent());
                }
                Files.copy(canonicalReportFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                LoggerUtils.info(ExtentManager.class, "Mirrored report copy to: " + target.toAbsolutePath());
            } catch (IOException e) {
                LoggerUtils.warn(ExtentManager.class, "Could not mirror report to " + target + ": " + e.getMessage());
            }
        }
    }

    public static String getReportAbsolutePath() {
        if (canonicalReportFile == null) {
            getInstance();
        }
        return canonicalReportFile != null ? canonicalReportFile.getAbsolutePath() : "";
    }

    public static String getReportFileUri() {
        if (canonicalReportFile == null) {
            getInstance();
        }
        return canonicalReportFile != null ? canonicalReportFile.toURI().toString() : "";
    }

    private static void printReportLocationBanner() {
        String absPath = getReportAbsolutePath();
        String fileUri = getReportFileUri();

        System.out.println("\n==========================================================================================");
        System.out.println("  EXTENTREPORTS HTML REPORT GENERATED & FLUSHED SUCCESSFULLY");
        System.out.println("==========================================================================================");
        System.out.println("  Local File Path: " + absPath);
        System.out.println("  Direct File URI: " + fileUri);
        System.out.println("  To open in browser without 404 errors, paste the Direct File URI into Chrome/Edge:");
        System.out.println("    " + fileUri);
        System.out.println("==========================================================================================\n");
    }
}
