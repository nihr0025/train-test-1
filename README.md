# TrainMate Automation Testing Framework

Comprehensive end-to-end Test Automation Framework built with **Java 17**, **Selenium WebDriver 4.21**, **TestNG 7.10**, **Page Object Model (POM)**, **Apache POI 5.2.5**, and **ExtentReports 5.1.1** for the **TrainMate - Cohort & Trainer Automatic Allocation System**.

---

## Table of Contents
1. [Application Under Test](#1-application-under-test)
2. [Prerequisites & Compatible Software](#2-prerequisites--compatible-software)
3. [IntelliJ IDEA Project Setup Guide](#3-intellij-idea-project-setup-guide)
4. [Framework Configuration & Browser Execution Mode](#4-framework-configuration--browser-execution-mode)
5. [Excel Test Data (`testdata.xlsx`) Setup](#5-excel-test-data-testdataxlsx-setup)
6. [Compilation & Execution Commands](#6-compilation--execution-commands)
7. [Running Tests from IntelliJ IDEA UI](#7-running-tests-from-intellij-idea-ui)
8. [Test Reports, Screenshots, and Logs](#8-test-reports-screenshots-and-logs)
9. [Detailed Test Coverage Matrix](#9-detailed-test-coverage-matrix)
10: [Troubleshooting Guide](#10-troubleshooting-guide)
11. [Pushing to GitHub](#11-pushing-to-github)

---

## 1. Application Under Test

- **Web Application URL:** https://train-mate-blond.vercel.app/
- **Backend API:** https://trainmate-o6tv.onrender.com/api
- **Test User Accounts:**
  - **Coach:** `coach01` / `coach123`
  - **Trainer:** `trainer01` / `trainer123`
  - **Admin:** `admin01` / `admin123`

---

## 2. Prerequisites & Compatible Software

Before running the project, verify that the following software is installed on your Windows machine:

| Software | Recommended Version | Verification Command |
| :--- | :--- | :--- |
| **Java Development Kit (JDK)** | JDK 17 (or 21) | `java -version` |
| **Apache Maven** | 3.8.x or 3.9.x | `mvn -version` |
| **Google Chrome** | Latest Stable | In Chrome: *Help -> About Google Chrome* |
| **Git for Windows** | 2.40+ | `git --version` |
| **IntelliJ IDEA** | Community or Ultimate (2023.x+) | *Help -> About* |

---

## 3. IntelliJ IDEA Project Setup Guide

Follow these exact steps to open and configure the project in IntelliJ IDEA:

### Step 1: Open the Project
1. Open IntelliJ IDEA.
2. Click **File -> Open...** (or click **Open** on the Welcome screen).
3. Navigate to `C:\Users\nihar\ideaprojects\trainmate-test` (or `trainmate-test-1`) and click **OK**.
4. When prompted, select **Trust Project**.

### Step 2: Configure Project SDK (Java 17)
1. Go to **File -> Project Structure...** (or press `Ctrl + Alt + Shift + S`).
2. Under **Project Settings**, select **Project**.
3. Set **SDK** to **17** (e.g., `openjdk-17` or `corretto-17`). If not listed, click **Add SDK -> Download JDK... -> Version: 17**.
4. Set **Language Level** to **17 - Sealed types, always-strict floating-point, pattern matching**.
5. Click **Apply** and **OK**.

### Step 3: Reload Maven Project & Download Dependencies
1. Open the **Maven tool window** on the right sidebar (or go to **View -> Tool Windows -> Maven**).
2. Click the **Reload All Maven Projects** icon (the two circular arrows in the top-left of the Maven pane).
3. Wait for IntelliJ to download dependencies and index the project.

---

## 4. Framework Configuration & Browser Execution Mode

Configuration is managed centrally in `src/test/resources/config.properties`.

### Visible (Headed) vs Headless Execution
By default, the framework is configured to launch a **visible Chrome browser window** on your screen:

```properties
# src/test/resources/config.properties
baseUrl=https://train-mate-blond.vercel.app/
app.url=https://train-mate-blond.vercel.app/
browser=chrome
headless=false

# Timeouts in seconds
explicitWaitSeconds=20
pageLoadTimeoutSeconds=60
scriptTimeoutSeconds=30
timeout.implicit=10
timeout.explicit=20
timeout.pageload=60
```

- **Visible Browser (Default):** `headless=false` — Opens Google Chrome, maximizes the window, and displays every test action live.
- **Headless Mode:** Set `headless=true` in `config.properties`, or pass the Maven command-line override:
  ```bash
  mvn test -Dheadless=true
  ```

---

## 5. Excel Test Data (`testdata.xlsx`) Setup

The data-driven tests read test data from `src/test/resources/testdata.xlsx` across all 13 specialized sheets:
1. `LoginData`
2. `Cohort_Creation`
3. `Cohort_Editing_By_Coach`
4. `Cohort_Search_By_Coach`
5. `CohortStatusfilter_By_Coach`
6. `Cohort_or_Skill_SearchByTrainer`
7. `CohortStatusFilter_By_Trainer`
8. `Cohort_Search_By_Admin`
9. `CohortStatusfilter_By_Admin`
10. `CohortSLfilter_By_Admin`
11. `Trainer_Search_By_Admin`
12. `TrainerFilterByStatus_By_Admin`
13. `TrainerRegistration_By_Admin`

### Self-Healing & Automatic Generation
The framework includes an automatic generator. If `testdata.xlsx` is missing, you can generate it in one command:
```bash
mvn compile exec:java
```
*(Or simply run tests; `ExcelUtils` automatically creates `testdata.xlsx` on first read).*

---

## 6. Compilation & Execution Commands

Run these commands in the **IntelliJ Terminal** (`Alt + F12`) or Command Prompt:

### 1. Compile the Project & Verify Dependencies
```bash
mvn clean test-compile
```
*Expected Result:* `BUILD SUCCESS` with zero compilation errors.

### 2. Execute the Complete Test Suite
```bash
mvn clean test
```
*Executes the entire suite configured in `testng.xml`.*

### 3. Execute Individual Test Classes
- **Authentication Tests:**
  ```bash
  mvn test -Dtest=LoginTest
  ```
- **Coach Workflow Tests (C-01 to C-10):**
  ```bash
  mvn test -Dtest=CoachWorkflowTest
  ```
- **Trainer Workflow Tests (T-01 to T-09):**
  ```bash
  mvn test -Dtest=TrainerWorkflowTest
  ```
- **Admin Workflow Tests (A-01 to A-10):**
  ```bash
  mvn test -Dtest=AdminWorkflowTest
  ```

---

## 7. Running Tests from IntelliJ IDEA UI

You can also run tests visually inside the IDE:

1. **Run Full Suite via TestNG XML:**
   - In the Project view, navigate to `testng.xml` (or `src/test/resources/testng.xml`).
   - Right-click `testng.xml` and select **Run '...\testng.xml'**.

2. **Run Individual Test Class:**
   - Navigate to `src/test/java/test/`.
   - Right-click any test file (e.g., `CoachWorkflowTest.java`) and click **Run 'CoachWorkflowTest'**.

3. **Run a Single Test Method:**
   - Open a test class (e.g., `LoginTest.java`).
   - Click the green **Play** arrow next to any `@Test` method and select **Run**.

---

## 8. Test Reports, Screenshots, and Logs

The framework records comprehensive diagnostics for every execution:

| Artifact | File Location | Description |
| :--- | :--- | :--- |
| **ExtentReports HTML** | `reports/ExtentReport.html` | Interactive HTML report with test durations, pass/fail status, and embedded failure screenshots |
| **Maven Surefire XML** | `target/surefire-reports/TEST-*.xml` | Standard JUnit/Surefire XML results for CI/CD |
| **Maven Surefire Text** | `target/surefire-reports/*.txt` | Plain-text execution output |
| **Failure Screenshots** | `screenshots/<TestName>_<Timestamp>.png` | Full-browser screenshots captured automatically on test failure |
| **Execution Logs** | `logs/trainmate-test.log` | Safe Log4j2 execution logs (passwords and tokens masked) |

---

## 9. Detailed Test Coverage Matrix

| Test ID | Module | Scenario Name | Test Method | Test Data Source |
| :--- | :--- | :--- | :--- | :--- |
| **L-01** | Authentication | Dynamic login verification | `LoginTest.testLoginWithExcelData` | Excel: `LoginData` |
| **L-02** | Authentication | Coach valid login | `LoginTest.testCoachValidLogin` | `config.properties` |
| **C-01** | Coach | Sign in and access dashboard | `CoachWorkflowTest.testC01_SignInAndAccessDashboard` | `config.properties` |
| **C-02** | Coach | Access control validation | `CoachWorkflowTest.testC02_AccessControlValidation` | Application Route Guard |
| **C-03** | Coach | Cohort form validation (Mandatory/Dates) | `CoachWorkflowTest.testC03_CohortFormValidationMandatoryAndDates` | Application UI Validation |
| **C-03b** | Coach | Cohort form validation (Excel-driven) | `CoachWorkflowTest.testC03_CohortFormValidationExcelDriven` | Excel: `Cohort_Creation` |
| **C-04** | Coach | Create cohort with valid details | `CoachWorkflowTest.testC04_CreateCohortWithValidDetails` | Dynamic Unique Code |
| **C-05** | Coach | Automated trainer allocation check | `CoachWorkflowTest.testC05_AutomatedTrainerAllocationCheck` | Live 100-Point Engine Modal |
| **C-06** | Coach | Allocation rule enforcement on no-match | `CoachWorkflowTest.testC06_AllocationRuleEnforcementOnNoMatch` | Live 100-Point Engine Modal |
| **C-07** | Coach | Search cohort list | `CoachWorkflowTest.testC07_SearchAndFilterCohortList` | Excel: `Cohort_Search_By_Coach` |
| **C-07b** | Coach | Filter cohorts by status | `CoachWorkflowTest.testC07b_CohortStatusFilter` | Excel: `CohortStatusfilter_By_Coach` |
| **C-08** | Coach | Cohort editing and reallocation | `CoachWorkflowTest.testC08_CohortEditingAndReallocation` | Excel: `Cohort_Editing_By_Coach` |
| **C-09** | Coach | Batch upload validation | `CoachWorkflowTest.testC09_BatchUploadValidation` | Application UI |
| **C-10** | Coach | Batch upload valid cohort workbook | `CoachWorkflowTest.testC10_BatchUploadValidCohortWorkbook` | `src/test/resources/testdata.xlsx` |
| **T-01** | Trainer | Trainer login & role access | `TrainerWorkflowTest.testT01_TrainerLoginAndRoleAccess` | `config.properties` |
| **T-02** | Trainer | Trainer unauthorized route check | `TrainerWorkflowTest.testT02_TrainerUnauthorizedRouteCheck` | Application Route Guard |
| **T-03** | Trainer | Trainer empty state dashboard check | `TrainerWorkflowTest.testT03_TrainerEmptyStateDashboard` | Application UI |
| **T-04** | Trainer | Trainer metrics after assignment | `TrainerWorkflowTest.testT04_TrainerMetricsAfterAssignment` | Application UI |
| **T-05** | Trainer | View assigned cohort details | `TrainerWorkflowTest.testT05_ViewAssignedCohortDetails` | Application UI Modal |
| **T-06** | Trainer | Search assigned cohort schedule | `TrainerWorkflowTest.testT06_SearchAssignedCohortSchedule` | Excel: `Cohort_or_Skill_SearchByTrainer` |
| **T-07** | Trainer | Filter cohorts by status | `TrainerWorkflowTest.testT07_FilterCohortsByStatus` | Excel: `CohortStatusFilter_By_Trainer` |
| **T-08** | Trainer | Mailbox assignment notification | `TrainerWorkflowTest.testT08_MailboxAssignmentNotification` | Application UI |
| **T-09** | Trainer | Real-time updates on reassignment | `TrainerWorkflowTest.testT09_RealTimeUpdatesOnReassignment` | Application UI |
| **A-01** | Admin | Admin sign in & system oversight | `AdminWorkflowTest.testA01_AdminSignInAndSystemOversight` | `config.properties` |
| **A-02** | Admin | Dashboard data integrity check | `AdminWorkflowTest.testA02_DashboardDataIntegrityCheck` | Application UI |
| **A-03** | Admin | Global cohort search & filter | `AdminWorkflowTest.testA03_GlobalCohortSearchAndFilter` | Excel: `Cohort_Search_By_Admin` |
| **A-03b** | Admin | Global cohort service line filter | `AdminWorkflowTest.testA03b_GlobalCohortServiceLineFilter` | Excel: `CohortSLfilter_By_Admin` |
| **A-03c** | Admin | Global cohort status filter | `AdminWorkflowTest.testA03c_GlobalCohortStatusFilter` | Excel: `CohortStatusfilter_By_Admin` |
| **A-04** | Admin | Export allocation report | `AdminWorkflowTest.testA04_ExportAllocationReport` | Application UI |
| **A-05** | Admin | Trainer registration validation (Excel-driven) | `AdminWorkflowTest.testA05_TrainerRegistrationValidation` | Excel: `TrainerRegistration_By_Admin` |
| **A-05b** | Admin | Trainer registration validation (Blank & Dates) | `AdminWorkflowTest.testA05b_TrainerRegistrationDirectValidation` | Application UI Modal |
| **A-06** | Admin | Register new trainer | `AdminWorkflowTest.testA06_RegisterNewTrainer` | Unique Data Generation |
| **A-07** | Admin | Trainer pool filter and search | `AdminWorkflowTest.testA07_TrainerPoolFilterAndSearch` | Excel: `Trainer_Search_By_Admin` |
| **A-07b** | Admin | Trainer pool filter by status | `AdminWorkflowTest.testA07b_TrainerPoolFilterByStatus` | Excel: `TrainerFilterByStatus_By_Admin` |
| **A-08** | Admin | Edit trainer workload and skills | `AdminWorkflowTest.testA08_EditTrainerWorkloadAndSkills` | Application UI |
| **A-09** | Admin | Admin manual reassignment & sync | `AdminWorkflowTest.testA09_AdminManualReassignmentAndWorkloadSync` | Application UI Modal |
| **A-10** | Admin | System-wide status update sync | `AdminWorkflowTest.testA10_SystemWideStatusUpdateSync` | Application UI |

---

## 10. Troubleshooting Guide

### 1. "mvn is not recognized as an internal or external command"
- **Cause:** Maven `bin` directory is not in your Windows `PATH`.
- **Solution:** 
  - Add `C:\Program Files\apache-maven-<version>\bin` to your System Environment Variable `Path`.
  - Or use the Maven tool window built directly into IntelliJ IDEA.

### 2. "The cloud server is still initializing from cold start"
- **Cause:** The backend API (`https://trainmate-o6tv.onrender.com/api`) runs on a free-tier Render container that spins down after inactivity.
- **Solution:** Wait 30–45 seconds on the first login attempt; the app will wake up automatically. Timeouts in `config.properties` are configured to handle this.

### 3. Excel file `testdata.xlsx` missing
- **Cause:** Fresh clone without build artifacts.
- **Solution:** Run `mvn compile exec:java`. The generator recreates all 13 sheets with the exact test data matching the project reference documents.

---

## 11. Pushing to GitHub

To push the project to your GitHub repository (`https://github.com/nihr0025/trainmate-test`):

```bash
git init
git branch -M main
git remote add origin https://github.com/nihr0025/trainmate-test.git
git add pom.xml testng.xml README.md .gitignore src/
git commit -m "Complete TrainMate Selenium automation framework"
git push -u origin main
```
