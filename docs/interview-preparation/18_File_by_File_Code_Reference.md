# 18. File-by-File Code Reference

## Overview

This document provides an exhaustive, class-by-class and method-by-method technical reference of the entire **TrainMate Selenium Automation Framework**.

Use this reference when an interviewer asks:
- *"What exact methods exist in your `BasePage` class?"*
- *"Show me the signature and implementation of your date entry utility."*
- *"How does your `DriverFactory` instantiate Chrome vs Firefox?"*
- *"What are the locators and actions defined inside `AdminCohortsPage`?"*

---

## 1. Directory Architecture

```
trainmate-test-1/
├── pom.xml
├── logs/
│   └── automation.log
├── test-output/
│   └── ExtentReport.html
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── base/
│   │   │   │   └── BasePage.java
│   │   │   ├── driver/
│   │   │   │   └── DriverFactory.java
│   │   │   ├── listeners/
│   │   │   │   └── TestListener.java
│   │   │   ├── pages/
│   │   │   │   ├── LoginPage.java
│   │   │   │   ├── CoachDashboardPage.java
│   │   │   │   ├── CreateCohortPage.java
│   │   │   │   ├── EditCohortPage.java
│   │   │   │   ├── RequestAllocationPage.java
│   │   │   │   ├── CoachCohortsPage.java
│   │   │   │   ├── TrainerDashboardPage.java
│   │   │   │   ├── TrainerCohortsPage.java
│   │   │   │   ├── AdminDashboardPage.java
│   │   │   │   └── AdminCohortsPage.java
│   │   │   └── utils/
│   │   │       ├── ConfigReader.java
│   │   │       ├── ExcelUtils.java
│   │   │       └── ScreenshotUtils.java
│   │   └── resources/
│   │       └── log4j2.xml
│   └── test/
│       ├── java/
│       │   ├── base/
│       │   │   └── BaseTest.java
│       │   └── test/
│       │       ├── LoginWorkflowTest.java
│       │       ├── CoachWorkflowTest.java
│       │       ├── TrainerWorkflowTest.java
│       │       └── AdminWorkflowTest.java
│       └── resources/
│           ├── config.properties
│           ├── config.properties.example
│           ├── testdata.xlsx
│           └── testng.xml
```

---

## 2. Framework Core Components (`src/main/java/`)

### 2.1 `base/BasePage.java`
The fundamental foundation for all 10 Page Objects. Contains wrapped Selenium operations, explicit synchronization, and diagnostic capture.

| Method Signature | Return Type | Description |
|---|---|---|
| `BasePage(WebDriver driver)` | Constructor | Initializes protected `driver`, `wait` (10s Duration), and `JavascriptExecutor`. |
| `waitForVisibility(By locator)` | `WebElement` | Waits until the element located by `locator` is visible in the DOM. |
| `waitForClickable(By locator)` | `WebElement` | Waits until the element is visible and enabled for user interaction. |
| `clickElement(By locator)` | `void` | Attempts native click; catches intercepted exceptions and triggers JS click fallback. |
| `typeText(By locator, String text)` | `void` | Clears existing value and enters string text with wait condition. |
| `clearField(By locator)` | `void` | Clears text input using `.clear()` and backspace keystrokes. |
| `getText(By locator)` | `String` | Extracts inner text or trimmed text content from element. |
| `isDisplayed(By locator)` | `boolean` | Returns `true` if element exists and is visible; returns `false` on timeout without throwing. |
| `enterDate(By locator, String dateStr)` | `void` | Normalizes date to `yyyy-MM-dd` and injects via JS with synthetic event dispatch. |
| `selectByVisibleText(By locator, String text)` | `void` | Selects option from HTML `<select>` element by visible label. |
| `selectByValue(By locator, String value)` | `void` | Selects option from HTML `<select>` element by option value attribute. |
| `scrollToElement(By locator)` | `void` | Scrolls element into viewport center using `scrollIntoView({block: 'center'})`. |
| `waitForUrlContains(String partialUrl)` | `boolean` | Synchronizes on single-page application client-side route changes. |
| `captureDiagnostics(String label)` | `void` | Captures current URL, DOM page title, browser logs, and triggers a debug screenshot. |

### 2.2 `driver/DriverFactory.java`
Thread-safe WebDriver management engine utilizing Java's `ThreadLocal`.

| Method Signature | Return Type | Description |
|---|---|---|
| `getDriver()` | `WebDriver` | Retrieves the active `WebDriver` instance belonging to the calling thread. |
| `setDriver(WebDriver driver)` | `void` | Binds a newly created `WebDriver` instance to the current thread context. |
| `initDriver(String browser)` | `WebDriver` | Configures and instantiates `ChromeDriver`, `FirefoxDriver`, or `EdgeDriver` with optimal flags. |
| `quitDriver()` | `void` | Closes browser process via `driver.quit()` and calls `driverThreadLocal.remove()` to prevent leaks. |

### 2.3 `listeners/TestListener.java`
Implements TestNG's `ITestListener` to intercept test lifecycle events and manage reporting.

| Method Signature | Overrides | Description |
|---|---|---|
| `onStart(ITestContext context)` | Yes | Initializes ExtentSparkReporter and ExtentReports configuration. |
| `onTestStart(ITestResult result)` | Yes | Creates a new `ExtentTest` node in the report tagged with author/category. |
| `onTestSuccess(ITestResult result)` | Yes | Marks test status as `PASS` in ExtentReports and logs completion. |
| `onTestFailure(ITestResult result)` | Yes | Captures failure screenshot via `ScreenshotUtils` and attaches base64/relative link to Extent report. |
| `onTestSkipped(ITestResult result)` | Yes | Logs skipped status and records skip reason/exception message. |
| `onFinish(ITestContext context)` | Yes | Flushes all buffered test events to disk via `extent.flush()`. |

### 2.4 `utils/ConfigReader.java`
Properties loader for environment and runtime configuration.

| Method Signature | Return Type | Description |
|---|---|---|
| `getProperty(String key)` | `String` | Returns property value from `config.properties`, prioritizing CLI overrides (`-Dkey`). |
| `getIntProperty(String key)` | `int` | Parses and returns an integer property (e.g., timeout seconds). |
| `getBooleanProperty(String key)` | `boolean` | Parses and returns boolean flags (e.g., `headless`). |

### 2.5 `utils/ExcelUtils.java`
Data-driven testing engine powered by Apache POI.

| Method Signature | Return Type | Description |
|---|---|---|
| `getSheetData(String sheetName)` | `Object[][]` | Reads an Excel worksheet into an array of `Map<String, String>` for TestNG data providers. |
| `getCellValueAsString(Cell cell)` | `String` | Type-safe cell extraction supporting String, Numeric, Date, Boolean, and Formula cells. |
| `normalizeDate(String rawDate)` | `String` | Converts various date formats (e.g., `dd/MM/yyyy`, `MM-dd-yyyy`) into ISO `yyyy-MM-dd`. |
| `getRowCount(String sheetName)` | `int` | Returns the physical row count for the specified worksheet. |

### 2.6 `utils/ScreenshotUtils.java`
Failure diagnostics and screenshot capture utility.

| Method Signature | Return Type | Description |
|---|---|---|
| `takeScreenshot(WebDriver driver, String name)` | `String` | Captures screen to timestamped PNG in `target/screenshots/` and returns absolute path. |
| `takeBase64Screenshot(WebDriver driver)` | `String` | Returns base64 encoded string of screen state for direct embedding into HTML reports. |

---

## 3. Page Object Classes (`src/main/java/pages/`)

All Page Objects extend `BasePage` and encapsulate locators as `private By` variables with public operational methods.

### 3.1 `LoginPage.java`
- **Locators:**
  - `usernameInput`: `By.id("username")` / `By.cssSelector("input[name='username']")`
  - `passwordInput`: `By.id("password")` / `By.cssSelector("input[name='password']")`
  - `loginButton`: `By.cssSelector("button[type='submit']")`
  - `errorMessage`: `By.cssSelector("div.alert-danger, .error-message")`
- **Actions:**
  - `navigateTo(String url)`: Opens login page.
  - `login(String username, String password)`: Inputs credentials and clicks submit.
  - `getErrorMessage()`: Retrieves validation failure message.

### 3.2 `CoachDashboardPage.java`
- **Locators:**
  - `welcomeMessage`: `By.cssSelector("h1.dashboard-title, .welcome-banner")`
  - `activeCohortsCard`: `By.cssSelector("div[data-metric='active-cohorts']")`
  - `pendingRequestsCard`: `By.cssSelector("div[data-metric='pending-requests']")`
  - `createCohortBtn`: `By.xpath("//button[contains(text(), 'Create Cohort')]")`
- **Actions:**
  - `isDashboardLoaded()`: Asserts dashboard container visibility.
  - `getActiveCohortsCount()`: Parses numeric counter from dashboard card.
  - `clickCreateCohort()`: Navigates to cohort creation form.

### 3.3 `CreateCohortPage.java`
- **Locators:**
  - `cohortCodeInput`: `By.id("cohortCode")`
  - `cohortNameInput`: `By.id("cohortName")`
  - `technologySelect`: `By.id("techStack")`
  - `startDateInput`: `By.id("startDate")`
  - `endDateInput`: `By.id("endDate")`
  - `saveButton`: `By.cssSelector("button[type='submit']")`
- **Actions:**
  - `fillCohortForm(Map<String, String> data)`: Populates all form fields using `enterDate` for date controls.
  - `submitForm()`: Clicks save and waits for modal dismissal or redirection.

### 3.4 `EditCohortPage.java`
- **Locators:**
  - `cohortNameInput`: `By.id("cohortName")`
  - `statusSelect`: `By.id("status")`
  - `updateButton`: `By.cssSelector("button.btn-update")`
- **Actions:**
  - `updateCohortDetails(String name, String status)`: Modifies existing cohort values.
  - `confirmUpdate()`: Submits update and verifies success toast.

### 3.5 `RequestAllocationPage.java`
- **Locators:**
  - `cohortDropdown`: `By.id("selectCohort")`
  - `requiredSkillsInput`: `By.id("requiredSkills")`
  - `slotsRequiredInput`: `By.id("slotsCount")`
  - `submitRequestBtn`: `By.cssSelector("button.btn-request")`
- **Actions:**
  - `submitAllocationRequest(String cohort, String skills, String slots)`: Requests trainer allocation.

### 3.6 `CoachCohortsPage.java`
- **Locators:**
  - `cohortsTable`: `By.cssSelector("table.cohorts-table")`
  - `searchInput`: `By.cssSelector("input[placeholder*='Search']")`
  - `statusFilterSelect`: `By.id("statusFilter")`
- **Actions:**
  - `searchCohort(String query)`: Filters table rows by cohort code or name.
  - `getCohortStatus(String code)`: Returns status badge text for matching cohort row.

### 3.7 `TrainerDashboardPage.java`
- **Locators:**
  - `assignedCohortsCounter`: `By.cssSelector("div.card-assigned-cohorts .count")`
  - `upcomingSessionsCard`: `By.cssSelector("div.card-upcoming-sessions")`
  - `calendarViewBtn`: `By.id("btnCalendarView")`
- **Actions:**
  - `getAssignedCohortsCount()`: Returns integer count of cohorts assigned to current trainer.
  - `navigateToAssignedCohorts()`: Clicks navigation link to `/trainer/cohorts`.

### 3.8 `TrainerCohortsPage.java`
- **Locators:**
  - `cohortsGrid`: `By.cssSelector("div.trainer-cohorts-grid")`
  - `cohortDetailsModal`: `By.cssSelector("div.modal-cohort-details")`
  - `acknowledgementBtn`: `By.cssSelector("button.btn-acknowledge")`
- **Actions:**
  - `viewCohortDetails(String cohortCode)`: Opens details modal for a cohort.
  - `acknowledgeAssignment()`: Clicks assignment acceptance button.

### 3.9 `AdminDashboardPage.java`
- **Locators:**
  - `totalTrainersCard`: `By.cssSelector("div[data-metric='total-trainers']")`
  - `totalCohortsCard`: `By.cssSelector("div[data-metric='total-cohorts']")`
  - `unallocatedRequestsCard`: `By.cssSelector("div[data-metric='unallocated-requests']")`
  - `runAllocationBtn`: `By.id("btnTriggerAutoAllocation")`
- **Actions:**
  - `verifyMetricCards()`: Asserts that all 3 KPI cards are present with non-negative numbers.
  - `triggerAutoAllocation()`: Triggers system-wide automated matching engine.

### 3.10 `AdminCohortsPage.java`
- **Locators:**
  - `cohortSearchField`: `By.cssSelector("input[placeholder*='Search']")`
  - `serviceLineSelect`: `By.id("serviceLineFilter")`
  - `allocateTrainerModal`: `By.cssSelector("div.modal-allocate-trainer")`
  - `manualAssignBtn`: `By.cssSelector("button.btn-manual-assign")`
- **Actions:**
  - `filterByServiceLine(String serviceLine)`: Selects service line from dropdown.
  - `manuallyAssignTrainer(String cohortCode, String trainerName)`: Overrides automatic allocation manually.

---

## 4. Test Infrastructure Classes (`src/test/java/`)

### 4.1 `base/BaseTest.java`
The base class for all TestNG test classes. Manages test lifecycle, configuration loading, and driver teardown.

```java
public class BaseTest {
    protected static Logger log = LogManager.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        log.info("Starting Test Suite Execution...");
        ConfigReader.initProperties();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        String browser = ConfigReader.getProperty("browser");
        log.info("Starting test: {} on browser: {}", method.getName(), browser);
        WebDriver driver = DriverFactory.initDriver(browser);
        driver.manage().window().maximize();
        driver.get(ConfigReader.getProperty("app.url"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("Test failed: {}", result.getName());
            ScreenshotUtils.takeScreenshot(DriverFactory.getDriver(), result.getName());
        }
        DriverFactory.quitDriver();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        log.info("Test Suite Execution Finished.");
    }
}
```

### 4.2 Test Workflow Classes

| Test Class | Tests Covered | Key Test Methods | Data Provider Sheet |
|---|---|---|---|
| `LoginWorkflowTest` | L-01, L-02 | `testL01_ValidLogin()`, `testL02_InvalidLogin()` | `login_data` |
| `CoachWorkflowTest` | C-01 to C-10 | `testC01_CoachLogin()`, `testC03_CreateCohort()`, `testC07_RequestAllocation()` | `coach_cohorts`, `coach_allocations` |
| `TrainerWorkflowTest` | T-01 to T-09 | `testT01_TrainerLogin()`, `testT03_ViewSchedule()`, `testT06_UpdateAvailability()` | `trainer_data`, `trainer_schedule` |
| `AdminWorkflowTest` | A-01 to A-10 | `testA01_AdminLogin()`, `testA03_GlobalCohortSearch()`, `testA05_RunAutoAllocation()` | `admin_data`, `admin_rules` |

---

## 5. Configuration & Suite Files (`src/test/resources/`)

### 5.1 `testng.xml`
Defines suite name, test listener bindings, thread count, and test classes to execute.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="TrainMate Automation Regression Suite" verbose="1">
    <listeners>
        <listener class-name="listeners.TestListener"/>
    </listeners>
    <test name="TrainMate End-to-End Workflow Tests">
        <classes>
            <class name="test.LoginWorkflowTest"/>
            <class name="test.CoachWorkflowTest"/>
            <class name="test.TrainerWorkflowTest"/>
            <class name="test.AdminWorkflowTest"/>
        </classes>
    </test>
</suite>
```

### 5.2 `config.properties`
Centralized application and environment properties.

```properties
app.url=https://train-mate-blond.vercel.app/
browser=chrome
headless=false
timeout.explicit=10
timeout.implicit=0
timeout.pageLoad=30
excel.path=src/test/resources/testdata.xlsx
report.path=test-output/ExtentReport.html
screenshot.dir=target/screenshots/
```

### 5.3 `pom.xml` Dependencies Overview
- `org.seleniumhq.selenium:selenium-java` (4.21.0)
- `org.testng:testng` (7.10.2)
- `org.apache.poi:poi` & `poi-ooxml` (5.2.5)
- `com.aventstack:extentreports` (5.1.1)
- `org.apache.logging.log4j:log4j-api` & `log4j-core` (2.23.1)
- `org.apache.maven.plugins:maven-surefire-plugin` (3.2.5)
