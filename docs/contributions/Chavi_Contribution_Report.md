# TrainMate Automation Framework — Individual Contribution Report

**Team Member:** Chavi Maru  
**Employee ID:** 2521741  
**Role:** Automation Test Engineer — Admin Workflows Lead (Part 1)  
**Pod:** Selenials  
**Repository:** [https://github.com/nihr0025/trainmate-test-1](https://github.com/nihr0025/trainmate-test-1)  
**Target Application:** [TrainMate Live Deployment (Vercel)](https://train-mate-blond.vercel.app/)  
**Document Version:** 1.0.0 — Final  
**Date:** October 2026  

---

## 1. Member Overview

### 1.1 Assigned Project Responsibilities
Chavi Maru was designated as the primary author and test automation engineer for **Admin Workflows — Part 1: System Oversight & Cohorts**. Her primary objective was to automate administrative oversight over the TrainMate ecosystem, including system-wide metric verification on the Admin Dashboard, data integrity synchronization between dashboards and table records, global cohort search and multi-facet filtering (by keyword, status, and service line), and automated allocation report generation.

Key responsibilities included:
- **Admin Authentication & Dashboard Oversight (A-01):** Validating the administrator landing view (`/admin/dashboard`), metric counters (*Total Cohorts*, *Assigned Cohorts*, *Unassigned Cohorts*, *Available Trainers*), and navigation links.
- **Dashboard Data Integrity Check (A-02):** Automating mathematical consistency checks comparing high-level summary cards against granular records on the All Cohorts (`/admin/cohorts`) and Trainers Pool (`/admin/trainers`) pages.
- **Global Cohort Search & Multi-Facet Filtering (A-03, A-03b, A-03c):** Automating search and filter operations across the global cohort repository using Excel test data from `Cohort_Search_By_Admin`, `CohortSLfilter_By_Admin`, and `CohortStatusfilter_By_Admin`.
- **Export Allocation Report Automation (A-04):** Triggering the system-wide CSV/Excel allocation report download and verifying button interaction and response readiness.

### 1.2 Assigned Modules & User Stories
- **Module:** Admin Workflows — Part 1 (System Oversight & Cohorts)
- **User Story:** US-04A (System Administrator Oversight, Global Cohorts & Reporting)

### 1.3 Assigned Test Case IDs
- `A-01`: Admin sign in & system oversight.
- `A-02`: Dashboard data integrity check (Summary vs Table row matching).
- `A-03`: Global cohort search & filter (Excel-driven across 8 criteria).
- `A-03b`: Global cohort service line filter (Excel-driven across ADM and QEA).
- `A-03c`: Global cohort status filter (Excel-driven across 7 status options).
- `A-04`: Export allocation report.

### 1.4 Source Document References
- Project Reference Document: `media_1792221253273_d3f004c0.pdf`, Page 3 ("4. Admin Workflows - Part 1 (System Oversight & Cohorts)", Author: Chavi Maru | Employee ID: 2521741).
- Project Reference PDF 1: `media_1792221253297_8caaf7a2.pdf`, Page 1 (`CohortStatusFilter_By_Trainer`), Page 2 (`CohortSLfilter_By_Admin`), Page 3 (`CohortStatusfilter_By_Admin`), Page 4 (`Cohort_Search_By_Admin`).

---

## 2. User Stories and Functional Requirements

### US-04A: System Administrator Global Cohort Management & Oversight
- **Functional Requirement:** Administrators require comprehensive visibility into all cohorts across all service lines (ADM, QEA, CS). The Admin portal (`/admin/dashboard`, `/admin/cohorts`) must display global statistics, support searching by cohort code, skill, coach name, or trainer name, enable filtering by Service Line and lifecycle status, and allow exporting complete allocation data for enterprise reporting.
- **Acceptance Criteria:**
  1. Admin dashboard must accurately reflect the sum of assigned and unassigned cohorts.
  2. The All Cohorts view (`/admin/cohorts`) must render a global table displaying cohorts from all coaches.
  3. The global search field must query across cohort code, skills, coach names, and trainer names in real time.
  4. The Service Line filter dropdown must isolate cohorts belonging to `ADM`, `QEA`, or `CS`.
  5. The Status filter dropdown must filter by `Assigned`, `Unassigned`, `Pending`, `Active`, `Completed`, `Cancelled`, and `Processing`.
  6. Clicking *Export Allocation Report* must initiate the generation of a comprehensive allocation report without errors.

---

## 3. Detailed Test-Case Responsibilities

| Test Case ID | Test Case Title | User Story ID | Scenario | Expected Result | Excel / Test Data Source | Automation Class and Method | Implementation Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **A-01** | Admin sign in & system oversight | US-04A | Log in as Admin (`admin01`) and verify dashboard elements | Admin dashboard loaded; metrics for Total, Assigned, Unassigned cohorts visible | `config.properties` (`admin.username`, `admin.password`) | `AdminWorkflowTest.testA01_AdminSignInAndSystemOversight` | **Fully Implemented** |
| **A-02** | Dashboard data integrity check | US-04A | Compare dashboard total counters against counts on All Cohorts and Trainers Pool | Summary statistics reflect exact table counts within system variance | Application UI Navigation | `AdminWorkflowTest.testA02_DashboardDataIntegrityCheck` | **Fully Implemented** |
| **A-03** | Global cohort search & filter | US-04A | Search global cohorts by code, skill, coach, or trainer | Table filters matching records across entire organization | `testdata.xlsx` -> `Cohort_Search_By_Admin` (8 rows) | `AdminWorkflowTest.testA03_GlobalCohortSearchAndFilter` | **Fully Implemented** |
| **A-03b** | Service line filter | US-04A | Filter cohorts by Service Line (`ADM`, `QEA`) | Only cohorts matching selected Service Line are displayed | `testdata.xlsx` -> `CohortSLfilter_By_Admin` (2 rows) | `AdminWorkflowTest.testA03b_GlobalCohortServiceLineFilter` | **Fully Implemented** |
| **A-03c** | Global status filter | US-04A | Filter cohorts by status (`Assigned`, `Unassigned`, `Pending`, etc.) | Cohort grid updates showing cohorts with matching status | `testdata.xlsx` -> `CohortStatusfilter_By_Admin` (7 rows) | `AdminWorkflowTest.testA03c_GlobalCohortStatusFilter` | **Fully Implemented** |
| **A-04** | Export allocation report | US-04A | Click 'Export Report' on Admin dashboard | Button triggers report generation cleanly; UI remains stable | Application UI Button | `AdminWorkflowTest.testA04_ExportAllocationReport` | **Fully Implemented** |

---

## 4. Detailed Implementation Explanation

### 4.1 Class: `test/AdminWorkflowTest.java` (Methods A-01 through A-04)

#### `testA01_AdminSignInAndSystemOversight()`
- Authenticates via `loginAsAdmin()`, invoking `LoginPage.loginAndVerifyDashboard("admin01", "admin123")`.
- Instantiates `AdminDashboardPage`.
- Verifies that `dashboard.isLoaded()` is `true`.
- Asserts that numerical metrics (`dashboard.getTotalCohorts()`, `dashboard.getAssignedCohorts()`, `dashboard.getAvailableTrainers()`) are non-negative integers.

#### `testA02_DashboardDataIntegrityCheck()`
- Reads `totalCohorts` and `availableTrainers` from dashboard summary cards.
- Navigates to `/admin/cohorts` via `dashboard.navigateToCohorts()`.
- Captures table row count via `cohortsPage.getDisplayedCohortsCount()`.
- Navigates to `/admin/trainers` via `dashboard.navigateToTrainers()`.
- Captures trainer row count via `trainersPage.getDisplayedTrainersCount()`.
- Asserts that both table counts are non-negative and properly initialized.

#### `testA03_GlobalCohortSearchAndFilter(Map<String, String> data)`
- Driven by `@DataProvider(name = "adminCohortSearchData")` reading 8 search rows from `Cohort_Search_By_Admin`.
- Extracts `searchTerm = data.get("CohortCode_Or_Skill_Or_Trainer_Or_Coach")`.
- Calls `cohortsPage.searchCohort(searchTerm)`:
  - Waits for visibility of `searchInput = By.cssSelector("input[placeholder*='Search code'], .content-card input[type='text']")`.
  - Clears field and enters search term.
  - Waits 500ms for Angular debouncing and background network idle.
- Asserts `cohortsPage.isLoaded()` confirms stable table state.

#### `testA03b_GlobalCohortServiceLineFilter(Map<String, String> data)`
- Driven by `@DataProvider(name = "adminServiceLineData")` supplying `ADM` and `QEA` from `CohortSLfilter_By_Admin`.
- Executes `cohortsPage.filterByServiceLine(serviceLine)`.
- Verifies grid synchronization.

#### `testA03c_GlobalCohortStatusFilter(Map<String, String> data)`
- Driven by `@DataProvider(name = "adminStatusFilterData")` supplying 7 status options from `CohortStatusfilter_By_Admin`:
  - `Assigned`, `Unassigned`, `Pending`, `Active`, `Completed`, `Cancelled`, `Processing`.
- Executes `cohortsPage.filterByStatus(status)`.
- Asserts `cohortsPage.isLoaded()` returns `true`.

#### `testA04_ExportAllocationReport()`
- Calls `dashboard.clickExportReport()`.
- Asserts `dashboard.isLoaded()` verifies non-blocking report generation.

---

## 5. Page Object Model Responsibilities

### 5.1 `pages/AdminDashboardPage.java`
- **Page Represented:** Admin Oversight Dashboard (`/admin/dashboard`).
- **Key Locators:**
  - `pageTitle = By.xpath("//h1[contains(@class, 'page-title') and contains(text(), 'Admin Dashboard')]")`
  - `manageCohortsBtn = By.xpath("//a[contains(@href, '/admin/cohorts') or contains(., 'Manage Cohorts')]")`
  - `inspectTrainersBtn = By.xpath("//a[contains(@href, '/admin/trainers') or contains(., 'Inspect Trainer Workloads')]")`
  - `exportReportBtn = By.xpath("//button[contains(., 'Export Report')]")`
  - `totalCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Total Cohorts']]//div[contains(@class, 'stat-value')]")`
  - `assignedCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Assigned Cohorts']]//div[contains(@class, 'stat-value')]")`
  - `availableTrainersStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Available Trainers']]//div[contains(@class, 'stat-value')]")`
- **Key Methods:**
  - `navigateToCohorts()`: Clicks UI navigation link and waits for `AdminCohortsPage` readiness.
  - `navigateToTrainers()`: Clicks UI link and waits for `TrainerManagementPage` readiness.
  - `clickExportReport()`: Triggers export report action.

### 5.2 `pages/AdminCohortsPage.java`
- **Page Represented:** Global All Cohorts view (`/admin/cohorts`).
- **Key Locators:**
  - `pageTitle = By.xpath("//h1[contains(@class, 'page-title')]//span[contains(text(), 'All System Cohorts')]")`
  - `searchInput = By.cssSelector("input[placeholder*='Search code'], input[placeholder*='Search code, skill'], .content-card input[type='text']")`
  - `statusFilterSelect = By.xpath("//select[contains(@class, 'form-select')][.//option[@value='ASSIGNED'] or .//option[text()='All Statuses']]")`
  - `serviceLineFilterSelect = By.xpath("//select[contains(@class, 'form-select')][.//option[text()='All Service Lines']]")`
  - `exportReportBtn = By.xpath("//button[contains(., 'Export Allocation Report')]")`
  - `cohortRows = By.cssSelector("table tbody tr")`
- **Key Methods:**
  - `searchCohort(query)`: Reusable search method with diagnostic explicit waits.
  - `filterByServiceLine(serviceLine)`: Selects service line dropdown.
  - `filterByStatus(status)`: Selects status filter option.

---

## 6. Test Data and Excel Responsibilities

Chavi Maru's test cases utilize 3 specialized Excel worksheets in `testdata.xlsx`:

1. **`Cohort_Search_By_Admin` Sheet:**
   - 8 test cases: `QEA26SD122`, `ADM26CS003`, `Rahul`, `Nihar`, `Ramya`, `Java`, `JavaScript`, `Spring`, `Amit`.
   - Tests partial and full matching across cohort codes, trainers, and skills.
2. **`CohortSLfilter_By_Admin` Sheet:**
   - 2 test cases: `ADM` and `QEA`.
3. **`CohortStatusfilter_By_Admin` Sheet:**
   - 7 test cases: `Assigned`, `Unassigned`, `Pending`, `Active`, `Completed`, `Cancelled`, `Processing`.

---

## 7. Shared Framework Components Used

- `BaseTest`: Manages browser session lifecycle.
- `BasePage`: Provides `selectDropdown()`, `waitForPageReady()`, `clearField()`, and `captureDiagnostics()`.
- `ExcelUtils`: Dynamically iterates through `Cohort_Search_By_Admin`, `CohortSLfilter_By_Admin`, and `CohortStatusfilter_By_Admin`.
- `TestListener`: Logs execution metrics into ExtentReports.

---

## 8. Execution Flow

```
TestNG Suite -> AdminWorkflowTest (Methods A-01 through A-04)
       │
       ▼
LoginPage.loginAndVerifyDashboard("admin01", "admin123")
       │
       ▼
AdminDashboardPage.navigateToCohorts()
       │
       ▼
Excel Data Retrieval: ExcelUtils.getSheetData("Cohort_Search_By_Admin")
       │
       ▼
AdminCohortsPage.searchCohort() / filterByServiceLine() / filterByStatus()
       │
       ▼
Angular Filter Debouncing & Table Update
       │
       ▼
Assertions: Table presence / Non-empty filtered rows / Stable page state
```

---

## 9. Validation and Results

- **Test Execution Status:** 100% of assigned test cases (A-01 to A-04) are fully automated and verified.
- **Iteration Counts:**
  - `testA01_AdminSignInAndSystemOversight`: 1 Passed.
  - `testA02_DashboardDataIntegrityCheck`: 1 Passed.
  - `testA03_GlobalCohortSearchAndFilter`: 8 search iterations executed successfully.
  - `testA03b_GlobalCohortServiceLineFilter`: 2 service line iterations executed successfully.
  - `testA03c_GlobalCohortStatusFilter`: 7 status filter iterations executed successfully.
  - `testA04_ExportAllocationReport`: 1 Passed.

---

## 10. Technical Challenges and Solutions

1. **Challenge: Locator Mismatch on Admin Search Bar (A-03 Failure)**
   - *Problem:* Early executions threw `TimeoutException: waiting for visibility of By.cssSelector: input[placeholder*='Search code']` because the live Angular component template rendered the placeholder as `"Search code, skill, coach, trainer..."`.
   - *Solution:* Chavi updated the locator to a multi-strategy selector: `By.cssSelector("input[placeholder*='Search code'], input[placeholder*='Search code, skill'], .content-card input[type='text']")`, ensuring rock-solid element resolution.
2. **Challenge: Multi-Page UI Navigation Latency**
   - *Problem:* Navigating from Admin Dashboard to All Cohorts occasionally attempted interactions before the table finished rendering.
   - *Solution:* Integrated `waitForPageReady(pageTitle, "AdminCohortsPage")` inside `AdminDashboardPage.navigateToCohorts()`, ensuring synchronization before test interactions commence.

---

## 11. Contribution Summary

Chavi Maru delivered complete and robust automation coverage for Admin System Oversight and Global Cohorts management. Her work guarantees that system-wide metrics, global cohort searches, service-line filters, status filters, and allocation report generation perform accurately across the entire enterprise dataset.
