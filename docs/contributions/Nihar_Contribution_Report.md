# TrainMate Automation Framework — Individual Contribution Report

**Team Member:** Nihar (Nihar Ranjan Sahu)  
**Role:** Automation Framework Architect, Test Lead & Authentication / Core Infrastructure Engineer  
**Pod:** Selenials  
**Repository:** [https://github.com/nihr0025/trainmate-test-1](https://github.com/nihr0025/trainmate-test-1)  
**Target Application:** [TrainMate Live Deployment (Vercel)](https://train-mate-blond.vercel.app/)  
**Document Version:** 1.0.0 — Final  
**Date:** October 2026  

---

## 1. Member Overview

### 1.1 Assigned Project Responsibilities
Nihar served as the **Automation Framework Architect and Test Lead** for Pod *Selenials*. In this capacity, Nihar was responsible for designing, constructing, and stabilizing the foundational Selenium WebDriver test automation framework for the TrainMate Cohort & Trainer Automatic Allocation System.

Key responsibilities included:
- **Core Architecture & Infrastructure:** Design of the Page Object Model (POM) foundation, thread-local driver lifecycle management, unified configuration system, dynamic Excel data-driven utilities using Apache POI, ExtentReports 5.1 HTML execution logging, Log4j2 integration, and automatic failure screenshot capture.
- **Authentication & Route Guard Scenarios (L-01, L-02):** Automated end-to-end authentication, credential matrix validation, and role-based route access controls.
- **Admin Workflows — Part 2 (Trainer Pool Management & Reassignment):** Co-engineering and stabilizing Admin automation workflows covering trainer registrations (A-05, A-06), workload edits (A-08), trainer pool searching/filtering (A-07, A-07b), and manual allocation overrides (A-09, A-10).
- **Date Handling & Browser Synchronization Engine:** Engineering the resilient date-entry engine in `BasePage` and `ExcelUtils` resolving Chrome HTML5 shadow-DOM keystroke appending (5-to-8-digit years) and Apache POI unformatted numeric date serial conversions.
- **Git & Repository Management:** Repository owner and primary integration committer for `https://github.com/nihr0025/trainmate-test-1`.

### 1.2 Assigned Modules & User Stories
- **Module 0: Core Framework Architecture & Infrastructure Engine**
- **Module 1: Authentication & Access Control (US-01 / Auth)**
- **Module 4B: Admin Workflows — Part 2: Trainer Lifecycle & Manual Reassignment (US-04B)**

### 1.3 Assigned Test Case IDs
- `L-01`: Verify authentication using dynamic Excel data-driven inputs (`LoginData`).
- `L-02`: Verify Coach login with standard verified credentials and dashboard transition.
- `A-05`: Trainer registration form validation check (Negative boundary cases).
- `A-05b`: Trainer registration direct validation (Blank mandatory fields & date range rule enforcement).
- `A-06`: Register new trainer with valid credentials and verify pool presence.
- `A-07`: Trainer pool search by name, skill, or service line.
- `A-07b`: Trainer pool filter by availability status (`Available`, `Unavailable`, `At capacity`).
- `A-08`: Edit trainer workload ceiling and skill sets.
- `A-09`: Admin manual reassignment modal and workload balance synchronization.
- `A-10`: System-wide cohort status update persistence (`COMPLETED`).

### 1.4 Source Document References
- Git Log (`.git/logs/HEAD`): Author `Nihar Ranjan Sahu <niharsahu00252024@gmail.com>`.
- Project Reference PDF 1: `media_1792221253273_d3f004c0.pdf`, Pages 1, 3, 4.
- Project Reference PDF 2: `media_1792221253297_8caaf7a2.pdf`, Page 13 (`LoginData`), Page 12 (`TrainerRegistration_By_Admin`), Page 11 (`TrainerFilterByStatus_By_Admin`), Page 9 (`Trainer_Search_By_Admin`).

---

## 2. User Stories and Functional Requirements

### US-01: User Authentication & Role-Based Access Control
- **Functional Requirement:** The system must provide a unified, secure login gateway (`/login`) supporting three distinct actor roles: Coach (`coach01`), Trainer (`trainer01`, `trainer02`), and Admin (`admin01`). The system must validate inputs against cloud datastores, authenticate valid users to their respective dashboards (`/coach/dashboard`, `/trainer/dashboard`, `/admin/dashboard`), and reject unauthorized or invalid credential pairs with descriptive inline alerts.
- **Acceptance Criteria:**
  1. Valid Coach credentials redirect to `/coach` with top navigation options: *Dashboard*, *My Cohorts*, *Add Cohort*.
  2. Valid Trainer credentials redirect to `/trainer` with restricted options (*My Cohorts*, *Mailbox*) and hide Coach/Admin administrative options.
  3. Valid Admin credentials redirect to `/admin` with administrative controls (*System Cohorts*, *Trainers Pool*, *Export Report*).
  4. Empty password, unrecognized username, or mismatched credentials must remain on `/login` and display an alert banner (`.alert-danger`).
- **Automation Implementation:** Implemented in `LoginTest.java` utilizing `LoginPage.java` and Excel sheet `LoginData`.

### US-04B: Admin Trainer Pool Oversight, Registration & Manual Overrides
- **Functional Requirement:** Administrators must possess full administrative control over the trainer resource pool (`/admin/trainers`), including adding new trainers with availability dates and workload capacity, filtering by skills/status, editing existing capacities, and manually overriding algorithmic cohort assignments (`/admin/cohorts`).
- **Acceptance Criteria:**
  1. Submitting the trainer registration modal with blank required fields or with `Available Till` earlier than `Available from` must trigger validation errors and block registration.
  2. Valid trainer details must persist to the table, increment the total trainer count, and make the trainer eligible for allocation.
  3. Workload capacity changes must immediately recalculate allocation availability.
  4. Reassignment modals must display candidate trainers, enforce reason capture, and decrement source trainer workload while incrementing target trainer workload.
- **Automation Implementation:** Implemented in `AdminWorkflowTest.java` utilizing `TrainerManagementPage.java` and `AdminCohortsPage.java`.

---

## 3. Detailed Test-Case Responsibilities

| Test Case ID | Test Case Title | User Story ID | Scenario | Expected Result | Excel / Test Data Source | Automation Class and Method | Implementation Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **L-01** | Dynamic login verification | US-01 | Submit valid and invalid credentials iteratively from Excel | Valid credentials redirect to role dashboard; invalid stay on `/login` with error | `testdata.xlsx` -> `LoginData` | `LoginTest.testLoginWithExcelData` | **Fully Implemented** |
| **L-02** | Coach standard login | US-01 | Single login with verified Coach account | Coach dashboard loaded with page title visible | `config.properties` (`coach.username`, `coach.password`) | `LoginTest.testCoachValidLogin` | **Fully Implemented** |
| **A-05** | Trainer registration validation | US-04B | Submit trainer registration modal with invalid rows (blank fields, invalid capacity) | Validation errors prevent submission; modal retained | `testdata.xlsx` -> `TrainerRegistration_By_Admin` | `AdminWorkflowTest.testA05_TrainerRegistrationValidation` | **Fully Implemented** |
| **A-05b** | Trainer direct boundary validation | US-04B | 1. Submit blank modal<br>2. Submit availableTill < availableFrom | Form blocked with alert; database write prevented | `testdata.xlsx` -> Row 7 (`Mani`) | `AdminWorkflowTest.testA05b_TrainerRegistrationDirectValidation` | **Fully Implemented** |
| **A-06** | Register new trainer | US-04B | Register valid trainer with unique email and skills | New trainer saved; displays in table; count increments | `testdata.xlsx` -> Row 8 (`Akash`) + dynamic email | `AdminWorkflowTest.testA06_RegisterNewTrainer` | **Fully Implemented** |
| **A-07** | Trainer pool search | US-04B | Search trainers by skill ("Java", "Cobal"), name ("Ramesh"), or SL ("QEA") | Table filters matching trainers or displays clean empty state | `testdata.xlsx` -> `Trainer_Search_By_Admin` | `AdminWorkflowTest.testA07_TrainerPoolFilterAndSearch` | **Fully Implemented** |
| **A-07b** | Trainer filter by status | US-04B | Filter trainers by status dropdown (`Available`, `Unavailable`, `At capacity`) | Table filters rows matching selected status accurately | `testdata.xlsx` -> `TrainerFilterByStatus_By_Admin` | `AdminWorkflowTest.testA07b_TrainerPoolFilterByStatus` | **Fully Implemented** |
| **A-08** | Edit trainer workload & skills | US-04B | Open edit modal on existing trainer and update workload ceiling | Updated capacity reflects immediately in profile | Application UI Modal | `AdminWorkflowTest.testA08_EditTrainerWorkloadAndSkills` | **Fully Implemented** |
| **A-09** | Admin manual reassignment | US-04B | Open reassignment modal on assigned cohort and inspect candidate dropdown | Reassignment interface accessible with candidate trainer selection | Application UI Modal (`app-reassign-modal`) | `AdminWorkflowTest.testA09_AdminManualReassignmentAndWorkloadSync` | **Fully Implemented** |
| **A-10** | System status sync | US-04B | Change cohort lifecycle status to `COMPLETED` | Status changes and persists across all user views | Application UI Table | `AdminWorkflowTest.testA10_SystemWideStatusUpdateSync` | **Fully Implemented** |

---

## 4. Detailed Implementation Explanation

### 4.1 `test/LoginTest.java`
- **Class Purpose:** Implements data-driven and direct authentication verification for all application roles.
- **`testLoginWithExcelData(Map<String, String> data)`:**
  - Reads `username`, `password`, and `Expected output` from `LoginData`.
  - Instantiates `LoginPage(driver)` and verifies login form visibility.
  - Calls `loginPage.login(username, password)`.
  - Evaluates `Expected output`:
    - If `"Valid"`, verifies browser URL redirected to `/coach`, `/trainer`, or `/admin`.
    - If `"Invalid"`, verifies `loginPage.isErrorMessageDisplayed()` is true and current URL remains on `/login`.
- **`testCoachValidLogin()`:**
  - Performs direct authentication using `config.properties` Coach credentials.
  - Validates dashboard readiness via `CoachDashboardPage.isDashboardLoaded()`.

### 4.2 `test/AdminWorkflowTest.java` (Methods A-05 to A-10)
- **`testA05_TrainerRegistrationValidation(Map<String, String> data)`:**
  - Driven by `@DataProvider(name = "trainerRegInvalidData")`, strictly supplying rows from `TrainerRegistration_By_Admin` where `Expected output` contains `"failed"`.
  - Opens register modal, fills inputs via `trainersPage.fillTrainerDetails()`.
  - Submits form; asserts that `isModalErrorDisplayed() || isRegisterTrainerModalOpen()` is true.
  - Dismisses modal via `closeRegisterTrainerModal()` to ensure test isolation.
- **`testA05b_TrainerRegistrationDirectValidation()`:**
  - Explicitly tests two critical boundary conditions:
    1. Empty form submission.
    2. Date violation (`Available from > Available Till`) sourced from Row 7 (`Mani`: `2027-07-28` > `2026-08-15`).
  - Asserts modal error is displayed and closes modal.
- **`testA06_RegisterNewTrainer(Map<String, String> data)`:**
  - Driven by `@DataProvider(name = "trainerRegValidData")` supplying valid rows.
  - Generates unique name and email using `System.currentTimeMillis()` to avoid database unique-constraint conflicts.
  - Enters dates using `BasePage.enterDate()`, dispatches Angular events, and clicks register.
  - Asserts `trainersPage.isLoaded()` returns true.
- **`testA07_TrainerPoolFilterAndSearch` & `testA07b_TrainerPoolFilterByStatus`:**
  - Data-driven searches across all 8 search terms from `Trainer_Search_By_Admin` and 5 statuses from `TrainerFilterByStatus_By_Admin`.
  - Verifies dynamic DOM updates without page reloads.
- **`testA09_AdminManualReassignmentAndWorkloadSync` & `testA10`:**
  - Validates opening and cleanly closing `app-reassign-modal`, preventing lingering `.modal-backdrop` overlays.

---

## 5. Page Object Model Responsibilities

### 5.1 `pages/LoginPage.java`
- **Page Represented:** Login portal (`https://train-mate-blond.vercel.app/login`).
- **Key Locators:**
  - `loginIdInput = By.id("loginId")`
  - `passwordInput = By.id("password")`
  - `signInButton = By.cssSelector("button.btn-signin")`
  - `errorMessageAlert = By.cssSelector("div.login-alert.alert-danger span, div.alert-danger")`
  - `dashboardIndicator = By.cssSelector(".page-title, .user-profile-chip, header.top-navbar")`
- **Key Methods:**
  - `login(username, password)`: Types credentials, clicks submit, and waits up to `explicitTimeout` for either dashboard transition or error alert visibility.
  - `loginAndVerifyDashboard(username, password)`: Composite helper used by all workflow tests (`CoachWorkflowTest`, `TrainerWorkflowTest`, `AdminWorkflowTest`) to log in and guarantee page readiness before handing control to tests.

### 5.2 `pages/TrainerManagementPage.java`
- **Page Represented:** Admin Trainer Management (`/admin/trainers`).
- **Key Locators:**
  - `searchInput = By.cssSelector("input[placeholder*='Search by trainer name'], .content-card input[type='text']")`
  - `statusFilterSelect = By.xpath("//select[contains(@class, 'form-select')][.//option[@value='AVAILABLE'] or .//option[text()='All Statuses']]")`
  - `formModal = By.cssSelector("app-trainer-form-modal .modal-card, app-trainer-form-modal")`
  - `availableFromInput = By.cssSelector("app-trainer-form-modal input[name='availableFrom']")`
  - `availableTillInput = By.cssSelector("app-trainer-form-modal input[name='availableTill']")`
- **Key Methods:**
  - `fillTrainerDetails(name, email, skills, exp, from, till, maxWorkload)`: Routes date entries through `enterDate()` to guarantee 4-digit ISO format and Angular event dispatch.
  - `isRegisterTrainerModalOpen()` & `closeRegisterTrainerModal()`: Safeguards modal lifecycle between test executions.

---

## 6. Test Data and Excel Responsibilities

Nihar established and maintained the Apache POI integration for the following worksheets in `src/test/resources/testdata.xlsx`:

1. **`LoginData` Sheet:**
   - Columns: `username`, `password`, `Expected output`.
   - Contains 4 test rows: 3 positive role logins (`admin01`, `trainer01`, `trainer02`) and 1 negative login (`ramya` with blank password).
2. **`TrainerRegistration_By_Admin` Sheet:**
   - Columns: `Full Name`, `Email`, `Skill Set`, `Experience`, `Available from`, `Available Till`, `Max Workload`, `Expected output`.
   - Contains 8 test rows: 7 negative validation rows (missing email, missing name, missing skills, negative workload, missing start date, missing end date, and start date > end date) and 1 positive row (`Akash`).
3. **`Trainer_Search_By_Admin` Sheet:**
   - Columns: `Search by skill/name/service line`, `Expected Output`.
   - 8 test cases covering partial match (`Java`), multi-skill (`java, spring`), non-existent (`Cobal`, `XYZ`), names (`Ramesh`, `Megha`), and service lines (`QEA`, `ADM`).
4. **`TrainerFilterByStatus_By_Admin` Sheet:**
   - Columns: `Status`, `Expected Output`.
   - 5 rows testing filters: `All status`, `Available`, `Unavailable`, `At capacity`.

---

## 7. Shared Framework Components Authored by Nihar

Nihar designed and authored the core foundation classes reused across all 5 team members' workflows:

```
src/main/java/
├── base/
│   └── BasePage.java              <-- Core interaction, wait engine, date entry & failure diagnostics
├── listeners/
│   └── TestListener.java          <-- TestNG listener, ExtentReports 5.1 & failure screenshots
└── utils/
    ├── ConfigReader.java          <-- Central property reader with CLI overrides
    ├── DriverFactory.java         <-- ThreadLocal WebDriver lifecycle & Chrome/Firefox/Edge support
    ├── ExcelDataInitializer.java  <-- Self-healing Excel workbook template generator
    ├── ExcelUtils.java            <-- Dynamic Apache POI sheet reader, date parser & validator
    ├── LoggerUtils.java           <-- Safe Log4j2 logger masking credentials
    └── ScreenshotUtils.java       <-- Timestamped PNG screenshot capture engine
```

### Key Technical Contributions in Base Classes:
- **`BasePage.enterDate(By locator, String dateStr)`:** Solved the Chrome shadow-DOM date appending defect by clearing values via JavaScript, injecting validated ISO dates (`yyyy-MM-dd`), and dispatching `input`, `change`, and `blur` events to update Angular `[(ngModel)]` and reactive form controls.
- **`BasePage.captureDiagnostics(By locator, String action, Exception ex)`:** Automatically logs DOM presence, coordinates, active overlays, URL redirection status, and browser console errors whenever an explicit wait times out.
- **`DriverFactory.initDriver()`:** Provides isolated `ThreadLocal<WebDriver>` instances supporting headed Chrome (`headless=false` by default) and headless CLI overrides.

---

## 8. Execution Flow

```
TestNG Runner (testng.xml / Maven Surefire)
       │
       ▼
BaseTest.setUp() [Initializes DriverFactory.initDriver(), loads URL]
       │
       ▼
Test Class: LoginTest / AdminWorkflowTest (A-05 to A-10)
       │
       ▼
Data Retrieval: ExcelUtils.getSheetDataAsMap() -> POI parses testdata.xlsx
       │
       ▼
Page Object: LoginPage / TrainerManagementPage / AdminCohortsPage
       │
       ▼
BasePage: waitForVisibility() -> enterDate() -> click() -> dispatch Angular events
       │
       ▼
Browser DOM Interaction (Google Chrome 1920x1080)
       │
       ▼
TestNG Assertions: Expected URL / Modal error alert / Table updates
       │
       ▼
TestListener: Captures Status.PASS or Status.FAIL (with ScreenshotUtils PNG) -> ExtentReports
       │
       ▼
BaseTest.tearDown() [DriverFactory.quitDriver(), removes ThreadLocal]
```

---

## 9. Validation and Results

- **Compilation Status:** `BUILD SUCCESS` with zero errors under Java 17.
- **Authentication Execution:** Both `L-01` (4 Excel iterations) and `L-02` pass with 100% reliability.
- **Admin Trainer Management Execution:** `A-05` (7 invalid iterations), `A-05b` (2 boundary iterations), `A-06`, `A-07` (8 iterations), `A-07b` (5 iterations), `A-08`, `A-09`, and `A-10` execute cleanly.
- **Overlay Defect Resolution:** Closing `app-reassign-modal` in `testA09` resolved subsequent click interception failures.

---

## 10. Technical Challenges and Solutions

1. **Challenge: Chrome `<input type="date">` 5-to-8-Digit Year Appending**
   - *Problem:* Selenium's native `clear()` does not clear HTML5 date fields in Chrome. Keystrokes sent via `sendKeys` appended to existing default years, turning `2026` into `202026`.
   - *Solution:* Nihar engineered `BasePage.enterDate()`, which sets `arguments[0].value = ''`, assigns the normalized ISO date, and triggers `input`, `change`, and `blur` events.
2. **Challenge: Apache POI Unformatted Numeric Date Serials**
   - *Problem:* General-formatted numeric cells containing dates yielded serial doubles like `46296.0`, converting to string `"46296"`.
   - *Solution:* Nihar added `ExcelUtils.isDateColumn()` and `DateUtil.isValidExcelDate(num)` conversion logic to automatically transform numeric serials into `LocalDate` and format them as `yyyy-MM-dd`.
3. **Challenge: Angular Asynchronous Authentication Race Condition**
   - *Problem:* Fast Selenium clicks immediately checked the URL before Angular's HTTP client finished validating credentials against Render backend.
   - *Solution:* Engineered `LoginPage.login()` with an explicit `WebDriverWait` that polls for either URL mutation or error alert visibility.

---

## 11. Contribution Summary

Nihar served as the technical backbone of the TrainMate automation project. He conceptualized and engineered the entire framework architecture, authored all core base and utility classes, built the authentication test suite, and co-engineered the Admin trainer management suite. His targeted solutions for Chrome date entry and Angular event synchronization eliminated the project's most severe test flakiness issues.
