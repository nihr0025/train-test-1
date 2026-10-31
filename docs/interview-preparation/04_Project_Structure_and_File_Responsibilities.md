# Document 04 — Project Structure & File Responsibilities

**Project Name:** TrainMate — Cohort & Trainer Automatic Allocation System  
**Document Purpose:** Exhaustive file-by-file directory walkthrough, explaining component classifications, dependencies, callers/callees, and common interview questions.

---

## 1. Complete Active Workspace Directory Tree

Below is the verified directory layout of the primary active project root (`trainmate-test-1`):

```text
trainmate-test-1/
├── pom.xml                                  [Build Configuration & Dependency Management]
├── README.md                                [Project Setup & Overview]
├── logs/                                    [Generated Log Files]
│   └── trainmate-test.log                   [Log4j2 Append Log Output]
├── reports/                                 [Generated HTML Reports]
│   └── ExtentReport.html                    [ExtentReports 5.1 Interactive Dashboard]
├── screenshots/                             [Generated Test Failure Screenshots (.png)]
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── base/
│   │       │   └── BasePage.java            [Core Selenium Interaction & Wait Engine]
│   │       ├── pages/                       [Page Object Model Classes]
│   │       │   ├── LoginPage.java           [Authentication & Redirect Checks]
│   │       │   ├── CoachDashboardPage.java  [Coach Stats & Create Cohort Modal Form]
│   │       │   ├── CohortPage.java          [Coach Cohort Table, Search, Edit & 100-pt Score Modal]
│   │       │   ├── BatchUploadPage.java     [Bulk Excel Ingestion Component]
│   │       │   ├── TrainerDashboardPage.java[Trainer Metric Counters & Navigation Tabs]
│   │       │   ├── TrainerCohortsPage.java  [Trainer Schedule, Status Filters & Details Popup]
│   │       │   ├── TrainerMailboxPage.java  [Trainer Assignment Alerts & Mark-as-Read]
│   │       │   ├── AdminDashboardPage.java  [Admin Metric Counters & Report Export]
│   │       │   ├── AdminCohortsPage.java    [Global Cohort Grid, SL Filters & Reassignment]
│   │       │   └── TrainerManagementPage.java[Trainer Pool Registration Modal & Search]
│   │       └── utils/                       [Shared Engineering Utilities]
│   │           ├── ConfigReader.java        [Properties File Loader with CLI Overrides]
│   │           ├── DriverFactory.java       [ThreadLocal WebDriver Lifecycle Provider]
│   │           ├── ExcelUtils.java          [Apache POI Dynamic Data Reader & Date Parser]
│   │           ├── ExcelDataInitializer.java[Self-Healing Excel Template Generator]
│   │           ├── LoggerUtils.java         [Log4j2 Safe Wrapper]
│   │           └── ScreenshotUtils.java     [Timestamped Failure Screenshot Capturer]
│   └── test/
│       ├── java/
│       │   ├── base/
│       │   │   └── BaseTest.java            [TestNG @BeforeMethod & @AfterMethod Lifecycle]
│       │   ├── listeners/
│       │   │   └── TestListener.java        [ExtentReports & Screenshot Binding Hook]
│       │   └── test/                        [Test Execution Classes]
│       │       ├── LoginTest.java           [Test Cases L-01, L-02]
│       │       ├── CoachWorkflowTest.java   [Test Cases C-01 through C-10]
│       │       ├── TrainerWorkflowTest.java [Test Cases T-01 through T-09]
│       │       └── AdminWorkflowTest.java   [Test Cases A-01 through A-10]
│       └── resources/
│           ├── config.properties            [Active Environment Settings & Test Accounts]
│           ├── log4j2.xml                   [Logging Appenders & Formatting Rules]
│           ├── testdata.xlsx                [Master Excel Workbook with 13 Worksheets]
│           └── testng.xml                   [Master TestNG Suite Configuration]
└── docs/                                    [Engineering & Interview Documentation]
    ├── interview-preparation/               [Dedicated Interview Notes & Study Guides]
    └── contributions/                       [Formal Team Pod Contribution Reports]
```

*(Note on Workspace Mirrors: The active development directory is `c:\Users\nihar\ideaprojects\trainmate-test-1`. The directory `trainmate-test` is a synchronized mirror maintained as a clean backup).*

---

## 2. File-by-File Inventory and Responsibilities

---

### 2.1 Build, Configuration & Runner Files

#### `pom.xml`
- **Classification:** Maven Build File.
- **What it does:** Configures Java 17 compilation, downloads external dependencies (Selenium, TestNG, Apache POI, ExtentReports, Log4j2), and binds the Maven Surefire Plugin to execute `src/test/resources/testng.xml`.
- **Dependencies:** None (Maven parses this first).
- **Interview Question:** *"How does Maven know which test cases to run?"*  
  **Answer:** *"The `maven-surefire-plugin` in `pom.xml` points to `<suiteXmlFile>src/test/resources/testng.xml</suiteXmlFile>`. When we run `mvn test`, Surefire launches the suites declared in that XML."*

#### `src/test/resources/testng.xml`
- **Classification:** TestNG Suite Definition.
- **What it does:** Defines the master test suite (`TrainMate Automation Test Suite`) divided into 4 test blocks: Authentication, Coach Role, Trainer Role, and Admin Role. Registers `listeners.TestListener`.
- **Interview Question:** *"How do you add a global listener to all TestNG tests?"*  
  **Answer:** *"By adding the `<listeners><listener class-name="listeners.TestListener"/></listeners>` block directly under the `<suite>` root tag in `testng.xml`."*

#### `src/test/resources/config.properties`
- **Classification:** Configuration Resource.
- **What it does:** Stores key-value pairs for `baseUrl`, `browser=chrome`, `headless=false`, explicit/implicit timeouts, and standard login credentials (`coach01`, `trainer01`, `admin01`).
- **Called by:** `utils.ConfigReader`.

---

### 2.2 Base Layer & Architecture Classes

#### `src/main/java/base/BasePage.java`
- **Classification:** Core Automation Engine.
- **What it does:** Parent class for all 10 page objects. Houses protected `WebDriver` and `WebDriverWait` instances. Contains reusable, explicit-wait wrapped methods: `waitForVisibility()`, `waitForClickable()`, `click()`, `clearField()`, `enterText()`, `enterDate()`, `selectDropdown()`, and `captureDiagnostics()`.
- **Called by:** All classes in `pages.*`.
- **Calls:** `utils.ConfigReader`, `utils.ExcelUtils`, `utils.LoggerUtils`, `utils.ScreenshotUtils`.
- **Interview Question:** *"Why make BasePage an abstract class?"*  
  **Answer:** *"Because BasePage represents a generic browser window rather than a concrete application screen. It must be inherited by specific page objects like `LoginPage` or `CohortPage`."*

#### `src/test/java/base/BaseTest.java`
- **Classification:** Test Lifecycle Controller.
- **What it does:** Parent class for all 4 test classes. Contains `@BeforeMethod` (launches browser via `DriverFactory.initDriver()` and navigates to `baseUrl`) and `@AfterMethod` (quits browser via `DriverFactory.quitDriver()`).
- **Called by:** `LoginTest`, `CoachWorkflowTest`, `TrainerWorkflowTest`, `AdminWorkflowTest`.

#### `src/main/java/utils/DriverFactory.java`
- **Classification:** Driver Provider Utility.
- **What it does:** Manages a `ThreadLocal<WebDriver>` instance. Initializes Chrome, Edge, or Firefox based on `ConfigReader` settings, configuring window size and flags (`--no-sandbox`, `--disable-gpu`). Provides `getDriver()` and `quitDriver()`.
- **Interview Question:** *"What happens if you do not call `driverThreadLocal.remove()` in `quitDriver()`?"*  
  **Answer:** *"It causes memory leaks because thread-local variables remain pinned in memory even after threads are returned to a thread pool."*

---

### 2.3 Page Object Model Layer (`src/main/java/pages/`)

| Page Object Class | Application Route / Screen | Key Locators & Elements | Key Methods |
| :--- | :--- | :--- | :--- |
| **`LoginPage.java`** | `/login` | `input[name='username']`, `input[name='password']`, `button[type='submit']`, `.error-message` | `login()`, `loginAndVerifyDashboard()`, `isErrorMessageDisplayed()` |
| **`CoachDashboardPage.java`**| `/coach/dashboard` | Metric stat cards, `button:has-text('Create Cohort')`, modal input fields, submit button | `isDashboardLoaded()`, `openCreateCohortModal()`, `fillCohortDetails()`, `submitCreateCohort()`, `navigateToCohorts()`, `navigateToUpload()` |
| **`CohortPage.java`** | `/coach/cohorts` | Search input, status select, cohort rows, edit modal, 100-pt score breakdown widget | `searchCohort()`, `filterByStatus()`, `viewCohortDetails()`, `openEditCohort()`, `is100PointScoreBreakdownDisplayed()` |
| **`BatchUploadPage.java`** | `/coach/upload` | Batch upload tab, hidden `input[type='file']`, dropzone container | `selectBatchUploadTab()`, `uploadFile()`, `isUploadSuccessDisplayed()` |
| **`TrainerDashboardPage.java`**| `/trainer/dashboard` | Active cohorts card, completed cohorts card, cohorts link, mailbox link, restricted admin tabs | `isLoaded()`, `getActiveCohortsCount()`, `getCompletedCohortsCount()`, `navigateToCohorts()`, `navigateToMailbox()`, `isCoachOrAdminTabsVisible()` |
| **`TrainerCohortsPage.java`** | `/trainer/cohorts` | Search input, status dropdown, cohort table rows, details modal | `searchCohort()`, `filterByStatus()`, `viewCohortDetails()`, `getModalCohortCode()`, `closeModal()` |
| **`TrainerMailboxPage.java`** | `/trainer/mailbox` | Notification cards, `button:has-text('Mark All as Read')`, badge count | `isLoaded()`, `markAllAsRead()` |
| **`AdminDashboardPage.java`** | `/admin/dashboard` | Total cohorts, assigned cohorts, unassigned cohorts stat cards, export button, navigation tabs | `isLoaded()`, `getTotalCohorts()`, `getAssignedCohorts()`, `getUnassignedCohorts()`, `navigateToCohorts()`, `navigateToTrainers()`, `clickExportReport()` |
| **`AdminCohortsPage.java`** | `/admin/cohorts` | Multi-strategy search input, Service Line filter, status filter, cohort table, reassignment modal | `searchCohort()`, `filterByServiceLine()`, `filterByStatus()`, `openReassignModal()`, `closeReassignModal()`, `changeCohortStatus()` |
| **`TrainerManagementPage.java`**| `/admin/trainers` | Register trainer button, search input, status filter, registration modal fields (Name, Email, Skills, Dates, Workload) | `openRegisterTrainerModal()`, `fillTrainerDetails()`, `submitTrainerForm()`, `searchTrainer()`, `filterByAvailabilityStatus()`, `openEditTrainer()` |

---

### 2.4 Test Execution Classes (`src/test/java/test/`)

#### `LoginTest.java`
- **Test Scenarios:** `L-01` (Excel data-driven authentication across 4 rows in `LoginData` sheet), `L-02` (Standard valid coach login).
- **Page Objects Used:** `LoginPage`, `CoachDashboardPage`.

#### `CoachWorkflowTest.java`
- **Test Scenarios:** `C-01` to `C-06` (Coach Part 1: login, route guard, form boundary validation, valid cohort creation, 100-pt algorithm score check, unassigned fallback), `C-07` to `C-10` (Coach Part 2: search, status filter, cohort editing/reallocation, batch upload route validation, valid workbook file upload).
- **Page Objects Used:** `LoginPage`, `CoachDashboardPage`, `CohortPage`, `BatchUploadPage`.
- **Test Data Sheets:** `Cohort_Creation`, `Cohort_Search_By_Coach`, `CohortStatusfilter_By_Coach`, `Cohort_Editing_By_Coach`.

#### `TrainerWorkflowTest.java`
- **Test Scenarios:** `T-01` to `T-09` (Role access, unauthorized route checks against `/admin/trainers` and `/coach/cohorts`, empty-state dashboard, metrics calculation, details modal, schedule search, status filter, mailbox alerts, real-time updates).
- **Page Objects Used:** `LoginPage`, `TrainerDashboardPage`, `TrainerCohortsPage`, `TrainerMailboxPage`.
- **Test Data Sheets:** `Cohort_or_Skill_SearchByTrainer`, `CohortStatusFilter_By_Trainer`.

#### `AdminWorkflowTest.java`
- **Test Scenarios:** `A-01` to `A-04` (Admin Part 1: oversight, dashboard data integrity audit, global search, service line filter, status filter, export report), `A-05` to `A-10` (Admin Part 2: trainer registration negative validation, direct boundary/date checks, positive registration with dynamic email, trainer search/filter, workload editing, manual reassignment, status sync).
- **Page Objects Used:** `LoginPage`, `AdminDashboardPage`, `AdminCohortsPage`, `TrainerManagementPage`.
- **Test Data Sheets:** `Cohort_Search_By_Admin`, `CohortSLfilter_By_Admin`, `CohortStatusfilter_By_Admin`, `TrainerRegistration_By_Admin`, `Trainer_Search_By_Admin`, `TrainerFilterByStatus_By_Admin`.

---

### 2.5 Utilities & Reporting Components (`src/main/java/utils/` & `src/test/java/listeners/`)

#### `utils/ExcelUtils.java`
- **Classification:** Data Ingestion Engine.
- **What it does:** Uses Apache POI to read `.xlsx` files. Converts sheet rows into `Object[][]` of `Map<String, String>` for TestNG DataProviders. Houses advanced date parsing methods (`parseDateSafely`, `parseAndValidateDate`, `isDateColumn`) that handle Excel serial numbers and 9 distinct date string patterns.

#### `utils/ScreenshotUtils.java`
- **Classification:** Visual Capture Utility.
- **What it does:** Takes timestamped full-page PNG screenshots using `((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE)` and saves them into the `screenshots/` directory.

#### `listeners/TestListener.java`
- **Classification:** TestNG Event Listener.
- **What it does:** Implements `ITestListener`. Manages `ExtentReports` and `ExtentTest` instances. Binds failure screenshots to ExtentReports HTML dashboard upon assertion or runtime failures.

#### `utils/LoggerUtils.java`
- **Classification:** Audit Logging Wrapper.
- **What it does:** Wraps Log4j2 `Logger` to provide structured console and file logging (`info`, `warn`, `error`, `debug`) without leaking sensitive user passwords.
