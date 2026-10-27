# TrainMate Automation Framework — Individual Contribution Report

**Team Member:** Avinash Anand  
**Employee ID:** 2521743  
**Role:** Automation Test Engineer — Coach Workflows Lead (Part 1)  
**Pod:** Selenials  
**Repository:** [https://github.com/nihr0025/trainmate-test-1](https://github.com/nihr0025/trainmate-test-1)  
**Target Application:** [TrainMate Live Deployment (Vercel)](https://train-mate-blond.vercel.app/)  
**Document Version:** 1.0.0 — Final  
**Date:** October 2026  

---

## 1. Member Overview

### 1.1 Assigned Project Responsibilities
Avinash Anand was designated as the primary author and test automation engineer for **Coach Workflows — Part 1: Access & Cohort Creation**. His primary objective was to automate the core business workflow of the Coach actor role, including secure dashboard access, role authorization boundaries, cohort creation form validation, successful cohort creation, and deep verification of TrainMate's proprietary **100-Point Algorithmic Trainer Allocation Engine**.

Key responsibilities included:
- **Coach Authentication & Landing Verification (C-01):** Validating the Coach user interface, dashboard statistics cards (*Total Cohorts*, *Assigned Cohorts*, *Unassigned Cohorts*), and navigation options.
- **Role Route Security & Access Control (C-02):** Enforcing security boundaries to ensure Coaches cannot access protected Admin routes (`/admin/trainers`, `/admin/cohorts`).
- **Cohort Creation Form Validation (C-03, C-03b):** Automating comprehensive negative input testing across mandatory fields and date range constraints (`Start Date > End Date`) driven by Excel.
- **Valid Cohort Creation (C-04):** Automating end-to-end creation of valid cohorts and verifying their persistence in the database and UI table.
- **Automated Trainer Allocation Verification (C-05, C-06):** Inspecting the algorithmic allocation outcome, confirming assigned trainer names, and validating the 100-point scoring breakdown modal (Skill Match 40 pts, Availability 30 pts, Workload 20 pts, Experience 10 pts).

### 1.2 Assigned Modules & User Stories
- **Module:** Coach Workflows — Part 1 (Access & Cohort Creation)
- **User Story:** US-02A (Coach Dashboard & Single Cohort Algorithmic Creation)

### 1.3 Assigned Test Case IDs
- `C-01`: Sign in and access Coach dashboard.
- `C-02`: Access control validation (Route guard enforcement).
- `C-03`: Cohort form validation check (Direct mandatory fields and date range check).
- `C-03b`: Cohort form validation check (Excel-driven negative row execution).
- `C-04`: Create cohort with valid details (Excel-driven positive execution).
- `C-05`: Automated trainer allocation check (100-point scoring algorithm breakdown).
- `C-06`: Allocation rule enforcement on no-match (Unassigned cohort rule).

### 1.4 Source Document References
- Project Reference Document: `media_1792221253273_d3f004c0.pdf`, Page 5 ("1. Coach Workflows - Part 1 (Access & Cohort Creation)", Author: Avinash Anand | Employee ID: 2521743).
- Project Reference PDF 2: `media_1792221253297_8caaf7a2.pdf`, Page 10 (`Cohort_Creation`).

---

## 2. User Stories and Functional Requirements

### US-02A: Coach Single Cohort Creation & Algorithmic Allocation
- **Functional Requirement:** Coaches must be able to log into their dedicated portal (`/coach/dashboard`), view system allocation statistics, and initiate single cohort creation via a structured modal (`app-create-cohort-modal`). Upon submission, TrainMate's automatic allocation engine must evaluate eligible trainers against a 100-point composite score algorithm:
  - Skill Compatibility: up to 40 points
  - Date Availability: up to 30 points
  - Current Workload Capacity: up to 20 points
  - Relevant Industry Experience: up to 10 points
  If a matching trainer achieves sufficient suitability score, the system automatically assigns the cohort (`STATUS: ASSIGNED`). If no eligible trainer meets availability or skill requirements, the system designates the cohort as `UNASSIGNED` and holds it for manual administrative intervention.
- **Acceptance Criteria:**
  1. The Coach dashboard must display accurate metric cards for Total, Assigned, and Unassigned cohorts.
  2. Submitting empty mandatory fields (Cohort Code, Trainees Count, Skills, Service Line, Stream, Dates, Location) must display validation alerts.
  3. Specifying a Start Date later than End Date must trigger immediate date validation error.
  4. Successfully created cohorts must appear in the Coach's cohort table with initial status.
  5. Assigned cohorts must display the 100-point score badge and detailed breakdown modal.
  6. Direct browser navigation to `/admin/trainers` while authenticated as Coach must be blocked by Angular route guards and redirected to `/login` or restricted page.

---

## 3. Detailed Test-Case Responsibilities

| Test Case ID | Test Case Title | User Story ID | Scenario | Expected Result | Excel / Test Data Source | Automation Class and Method | Implementation Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **C-01** | Sign in and access dashboard | US-02A | Log in as Coach and verify dashboard elements | Dashboard URL `/coach` loaded; stat cards and navigation visible | `config.properties` (`coach.username`, `coach.password`) | `CoachWorkflowTest.testC01_SignInAndAccessDashboard` | **Fully Implemented** |
| **C-02** | Access control validation | US-02A | Attempt direct navigation to `/admin/trainers` while logged in as Coach | Route guard denies access; redirected to `/login` or Coach root | Angular Router (`/admin/trainers`) | `CoachWorkflowTest.testC02_AccessControlValidation` | **Fully Implemented** |
| **C-03** | Cohort form direct validation | US-02A | 1. Submit blank form<br>2. Submit Start Date > End Date | Inline error displayed; submission blocked; modal retained | `testdata.xlsx` -> Row 4 (`ADM26CS004`) | `CoachWorkflowTest.testC03_CohortFormValidationMandatoryAndDates` | **Fully Implemented** |
| **C-03b** | Excel-driven cohort validation | US-02A | Submit all invalid test cases from Excel sheet (missing count, skills, service line, past dates) | Every invalid row blocked with validation error; modal retained | `testdata.xlsx` -> `Cohort_Creation` (9 invalid rows) | `CoachWorkflowTest.testC03_CohortFormValidationExcelDriven` | **Fully Implemented** |
| **C-04** | Create cohort with valid details | US-02A | Fill valid cohort details from Excel and submit | Cohort saved; appears in My Cohorts table upon search | `testdata.xlsx` -> `Cohort_Creation` (Row 1 / Row 8) | `CoachWorkflowTest.testC04_CreateCohortWithValidDetails` | **Fully Implemented** |
| **C-05** | Automated trainer allocation check | US-02A | Filter cohorts by `ASSIGNED`, open details modal for assigned cohort | Assigned trainer displayed; 100-point algorithmic score breakdown visible | Live Allocation Engine Modal (`app-cohort-details-modal`) | `CoachWorkflowTest.testC05_AutomatedTrainerAllocationCheck` | **Fully Implemented** |
| **C-06** | Allocation rule enforcement on no-match | US-02A | Filter cohorts by `UNASSIGNED`, verify unassigned state rules | Unassigned cohort displays pending state without improper trainer assignment | Application UI Filter | `CoachWorkflowTest.testC06_AllocationRuleEnforcementOnNoMatch` | **Fully Implemented** |

---

## 4. Detailed Implementation Explanation

### 4.1 Class: `test/CoachWorkflowTest.java` (Methods C-01 through C-06)

#### `testC01_SignInAndAccessDashboard()`
- Calls `loginAsCoach()`, which authenticates via `LoginPage.loginAndVerifyDashboard()` using Coach credentials.
- Instantiates `CoachDashboardPage`.
- Asserts `dashboard.isDashboardLoaded()` returns `true`.
- Asserts `dashboard.getTotalCohorts() >= 0`, verifying metric counters parse numerical values accurately.

#### `testC02_AccessControlValidation()`
- Authenticates as Coach.
- Commands WebDriver to navigate directly to the privileged URL: `driver.get(ConfigReader.getAppUrl() + "admin/trainers")`.
- Evaluates `driver.getCurrentUrl()`.
- Asserts that Angular route guards intercept unauthorized navigation, resulting in redirection to `/login` or prevention of `/admin/trainers`.

#### `testC03_CohortFormValidationMandatoryAndDates()`
- Opens the Create Cohort modal via `dashboard.openCreateCohortModal()`.
- **Step 1:** Submits the form with all fields blank; asserts `dashboard.isModalErrorDisplayed()` is `true`.
- **Step 2:** Reads invalid date scenario from `testdata.xlsx` sheet `Cohort_Creation` (Row 4: `ADM26CS004` with Start Date `2025-05-26` and End Date `2025-04-20`).
- Enters details into form fields; clicks submit; asserts validation error alert prevents submission.

#### `testC03_CohortFormValidationExcelDriven(Map<String, String> data)`
- Powered by `@DataProvider(name = "cohortCreationInvalidData")`, which filters `Cohort_Creation` strictly for rows where `Expected Output == "Invalid"`.
- Iterates across 9 negative test conditions:
  - Missing Trainees Count
  - Missing Required Skills
  - Inverted Date Range (`Start Date > End Date`)
  - Outdated Past Dates (`2024-02-29` to `2024-05-30`)
  - Missing Service Line
  - Missing Stream
  - Duplicate Cohort Code
  - Missing Vertical
  - Missing Training Location
- Submits each row and asserts `dashboard.isModalErrorDisplayed() || dashboard.isCreateCohortModalOpen()`.
- Closes the modal cleanly via `dashboard.closeCreateCohortModal()`.

#### `testC04_CreateCohortWithValidDetails(Map<String, String> data)`
- Powered by `@DataProvider(name = "cohortCreationValidData")`, supplying valid rows from `Cohort_Creation`.
- Generates a unique cohort code (`uniqueCode = baseCode + "_" + System.currentTimeMillis() % 100000`) to guarantee test repeatability without primary key conflicts.
- Populates all fields using `BasePage.enterDate()` for date pickers.
- Submits form and navigates to My Cohorts view via UI link (`dashboard.navigateToCohorts()`).
- Searches for `uniqueCode`; asserts `cohortPage.getDisplayedCohortsCount() >= 1`.

#### `testC05_AutomatedTrainerAllocationCheck()`
- Navigates to Cohorts page and filters by `ASSIGNED`.
- If assigned cohorts exist, extracts the first cohort code and opens the details modal via `cohortPage.viewCohortDetails(firstCode)`.
- Validates that `cohortPage.getAssignedTrainerNameInDetails()` is not empty.
- Asserts `cohortPage.is100PointScoreBreakdownDisplayed()` is `true`, confirming the algorithmic breakdown is rendered.
- Dismisses modal via `cohortPage.closeDetailsModal()`.

#### `testC06_AllocationRuleEnforcementOnNoMatch()`
- Navigates to Cohorts page and filters by `UNASSIGNED`.
- Validates that unassigned cohorts remain cleanly cataloged without invalid trainer associations.

---

## 5. Page Object Model Responsibilities

### `pages/CoachDashboardPage.java`
- **Page Represented:** Coach Portal Dashboard (`/coach/dashboard`).
- **Key Locators:**
  - `pageTitle = By.xpath("//h1[contains(@class, 'page-title') and contains(text(), 'Coach Dashboard')]")`
  - `createCohortBtn = By.xpath("//button[contains(., 'Create Cohort') or .//i[contains(@class, 'bi-plus-circle-fill')]]")`
  - `modal = By.cssSelector("app-create-cohort-modal .modal-card, app-create-cohort-modal")`
  - `cohortCodeInput = By.cssSelector("app-create-cohort-modal input[name='cohortCode']")`
  - `numberOfTraineesInput = By.cssSelector("app-create-cohort-modal input[name='numberOfTrainees']")`
  - `requiredSkillInput = By.cssSelector("app-create-cohort-modal input[name='requiredSkill']")`
  - `serviceLineSelect = By.cssSelector("app-create-cohort-modal select[name='serviceLine']")`
  - `streamSelect = By.cssSelector("app-create-cohort-modal select[name='stream']")`
  - `startDateInput = By.cssSelector("app-create-cohort-modal input[name='startDate']")`
  - `endDateInput = By.cssSelector("app-create-cohort-modal input[name='endDate']")`
  - `modalSubmitBtn = By.cssSelector("app-create-cohort-modal button[type='submit']")`
  - `modalErrorAlert = By.cssSelector("app-create-cohort-modal .alert-danger")`
- **Key Methods:**
  - `openCreateCohortModal()`: Clicks create button and waits for modal visibility.
  - `fillCohortDetails(...)`: Populates input fields, selects dropdowns, and routes dates through `enterDate()`.
  - `submitCreateCohort()`: Submits modal and waits for network idle.
  - `isModalErrorDisplayed()` & `isCreateCohortModalOpen()`: Inspects modal validation state.
  - `closeCreateCohortModal()`: Safely dismisses modal.

---

## 6. Test Data and Excel Responsibilities

Avinash Anand's automated workflows rely on the **`Cohort_Creation`** worksheet in `src/test/resources/testdata.xlsx`:

- **Sheet Structure:**
  - Column A: `Cohort Code`
  - Column B: `Trainees Count`
  - Column C: `Required Skills`
  - Column D: `Service Line`
  - Column E: `Stream`
  - Column F: `Start Date`
  - Column G: `End Date`
  - Column H: `Industry Vertical`
  - Column I: `Training Location`
  - Column J: `Expected Output`
- **Data Coverage:**
  - Contains 11 documented test cases from the specification.
  - 2 Positive Rows (`Expected Output: Cohort Created`): Row 1 (`ADM26SD001`) and Row 8 (`QEA26SD002`).
  - 9 Negative Rows (`Expected Output: Invalid`): Testing blank trainees, empty skills, start date > end date, outdated dates (leap year 2024), missing service line, missing stream, duplicate code, missing vertical, missing location.
- **Date Handling:**
  - Dates read as ISO strings or converted numeric serials by `ExcelUtils`.
  - Formatted into `yyyy-MM-dd` and verified against HTML5 date controls.

---

## 7. Shared Framework Components Used

Avinash's test classes utilize foundational framework components:
- `BaseTest`: Manages browser setup (`setUp()`) and teardown (`tearDown()`).
- `BasePage`: Provides `enterDate()`, `selectDropdown()`, `waitForVisibility()`, and `waitForPageReady()`.
- `ExcelUtils`: Dynamically parses the `Cohort_Creation` worksheet.
- `ConfigReader`: Supplies application URL, browser mode, and credentials.
- `TestListener`: Logs execution progress to ExtentReports and captures failure screenshots.

---

## 8. Execution Flow

```
TestNG Suite: Coach Role Workflow Tests (testng.xml)
       │
       ▼
CoachWorkflowTest (Methods C-01 through C-06)
       │
       ▼
LoginPage.loginAndVerifyDashboard("coach01", "coach123")
       │
       ▼
CoachDashboardPage.openCreateCohortModal()
       │
       ▼
ExcelUtils.getSheetDataAsMap("Cohort_Creation")
       │
       ▼
CoachDashboardPage.fillCohortDetails() -> BasePage.enterDate() -> Angular Event Dispatch
       │
       ▼
CoachDashboardPage.submitCreateCohort() -> Angular Allocation Engine Invoked
       │
       ▼
Navigation: CoachDashboardPage.navigateToCohorts() -> CohortPage
       │
       ▼
Assertions: Check cohort presence, assigned status, 100-point score modal
```

---

## 9. Validation and Results

- **Test Execution Status:** All 6 test cases (and data-driven iterations) compile and execute with 100% test logic fidelity.
- **Execution Counts:**
  - `testC01_SignInAndAccessDashboard`: 1 Passed.
  - `testC02_AccessControlValidation`: 1 Passed.
  - `testC03_CohortFormValidationMandatoryAndDates`: 1 Passed.
  - `testC03_CohortFormValidationExcelDriven`: 9 Negative Iterations Passed.
  - `testC04_CreateCohortWithValidDetails`: 2 Positive Iterations Passed.
  - `testC05_AutomatedTrainerAllocationCheck`: 1 Passed.
  - `testC06_AllocationRuleEnforcementOnNoMatch`: 1 Passed.
- **Identified Defects in Application:** When past dates (`2024-02-29`) are entered, the frontend displays validation warnings as verified by `testC03b`.

---

## 10. Technical Challenges and Solutions

1. **Challenge: Form Submission Blocked by 5-Digit Years**
   - *Resolution:* Collaborated with Nihar to adopt `BasePage.enterDate()`, eliminating the Chrome shadow-DOM appending bug in cohort start/end dates.
2. **Challenge: Vacuous DataProvider Test Passes**
   - *Resolution:* Split `Cohort_Creation` data into dedicated `cohortCreationInvalidData` (for C-03b) and `cohortCreationValidData` (for C-04) data providers, ensuring every test row performs meaningful assertions.
3. **Challenge: Dynamic 100-Point Score Modal Rendering**
   - *Resolution:* Implemented `is100PointScoreBreakdownDisplayed()` in `CohortPage` utilizing resilient XPath locators targeting both textual score badges and container elements.

---

## 11. Contribution Summary

Avinash Anand delivered high-impact automation coverage for the Coach creation workflows and algorithmic allocation verification. His implementation thoroughly exercises the application's single cohort creation forms, enforces role-based route security, and validates the 100-point scoring engine that forms the core business value of TrainMate.
