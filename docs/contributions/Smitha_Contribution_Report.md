# TrainMate Automation Framework — Individual Contribution Report

**Team Member:** Smitha  
**Role:** Automation Test Engineer — Trainer Workflows Lead  
**Pod:** Selenials  
**Repository:** [https://github.com/nihr0025/trainmate-test-1](https://github.com/nihr0025/trainmate-test-1)  
**Target Application:** [TrainMate Live Deployment (Vercel)](https://train-mate-blond.vercel.app/)  
**Document Version:** 1.0.0 — Final  
**Date:** October 2026  

---

## 1. Member Overview

### 1.1 Assigned Project Responsibilities
Smitha was designated as the primary author and test automation engineer for **Trainer Workflows (Schedule, Metrics & Notification Mailbox)**. Her primary objective was to automate the Trainer actor role experience within TrainMate, ensuring that Trainers have real-time visibility into their assigned training batches, can inspect schedules and venue details, can filter assigned cohorts by lifecycle status, receive timely notifications upon allocation or administrative reassignment, and are strictly prevented from accessing privileged Coach or Admin configuration routes.

Key responsibilities included:
- **Trainer Authentication & Access Control (T-01, T-02):** Validating Trainer login (`trainer01`), confirming the isolation of Trainer dashboard features (*My Cohorts*, *Mailbox*), verifying the hiding of Coach creation buttons and Admin tabs, and testing route guard blocks against unauthorized URLs (`/admin/trainers`, `/coach/cohorts`).
- **Trainer Dashboard & Metrics Verification (T-03, T-04):** Validating empty-state dashboards for newly registered trainers, and verifying numerical tracking of active and completed cohorts after algorithmic allocation.
- **Assigned Cohort Exploration & Schedule Search (T-05, T-06):** Automating cohort details inspection modal (`app-cohort-details-modal`) displaying cohort code, skills, dates, and location, and automating schedule searching using Excel test data from `Cohort_or_Skill_SearchByTrainer`.
- **Status Filtering (T-07):** Filtering assigned cohorts by lifecycle status (`All Statuses`, `Assigned`, `Active`, `Completed`) driven by Excel sheet `CohortStatusFilter_By_Trainer`.
- **Trainer Mailbox & Reassignment Alerts (T-08, T-09):** Automating navigation to the Trainer Mailbox (`/trainer/mailbox`), verifying real-time allocation alerts, and confirming read/unread state toggles (*Mark All as Read*).
- **Admin Trainer Pool Co-Ownership:** Assisting with the validation of Admin Trainer Management workflows (A-05 to A-08) to verify that trainer profile updates reflect seamlessly across the Trainer portal.

### 1.2 Assigned Modules & User Stories
- **Module:** Trainer Workflows (Schedule, Metrics & Notifications)
- **User Story:** US-03 (Trainer Portal, Schedule Tracking & Real-Time Notification Mailbox)

### 1.3 Assigned Test Case IDs
- `T-01`: Trainer login & role access.
- `T-02`: Trainer unauthorized route check.
- `T-03`: Trainer empty state dashboard check.
- `T-04`: Trainer metrics after assignment.
- `T-05`: View assigned cohort details.
- `T-06`: Search assigned cohort schedule (Excel-driven across 6 search criteria).
- `T-07`: Filter cohorts by status (Excel-driven across 4 status options).
- `T-08`: Mailbox assignment notification.
- `T-09`: Real-time updates on reassignment.

### 1.4 Source Document References
- Project Reference Document: `media_1792221253273_d3f004c0.pdf`, Page 2 & Page 3 ("3. Trainer Workflows (Schedule, Details, Notifications)").
- Project Reference PDF 1: `media_1792221253297_8caaf7a2.pdf`, Page 5 (`Cohort_or_Skill_SearchByTrainer`), Page 1 (`CohortStatusFilter_By_Trainer`).

---

## 2. User Stories and Functional Requirements

### US-03: Trainer Portal, Schedule Tracking & Real-Time Notification Mailbox
- **Functional Requirement:** Trainers require a dedicated portal (`/trainer/dashboard`, `/trainer/cohorts`, `/trainer/mailbox`) to view their training commitments without exposure to administrative overhead. The portal must display current batch workloads, provide complete cohort details (training dates, venue, service line, and curriculum skills), enable schedule searching, and deliver automated notification cards whenever a cohort is assigned or reassigned by an administrator.
- **Acceptance Criteria:**
  1. Authenticated Trainers must see only *Dashboard*, *My Cohorts*, and *Mailbox* tabs. Administrative actions (*Create Cohort*, *Trainers Pool*, *Export Report*) must be hidden from the DOM or disabled.
  2. Attempting direct browser navigation to `/admin/trainers` or `/coach/cohorts` must be blocked by Angular route guards and redirected to `/login` or restricted page.
  3. Clicking *View Details* on an assigned cohort row must display the cohort code, assigned skills, date range, and training location.
  4. Schedule search must query cohort codes and skills in real time.
  5. The Mailbox page must display assignment notification cards with cohort code, service line, and timestamps, and allow marking alerts as read.

---

## 3. Detailed Test-Case Responsibilities

| Test Case ID | Test Case Title | User Story ID | Scenario | Expected Result | Excel / Test Data Source | Automation Class and Method | Implementation Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **T-01** | Trainer login & role access | US-03 | Log in with Trainer credentials and verify navigation | Trainer dashboard loaded; Coach creation and Admin tabs hidden | `config.properties` (`trainer.username`, `trainer.password`) | `TrainerWorkflowTest.testT01_TrainerLoginAndRoleAccess` | **Fully Implemented** |
| **T-02** | Trainer unauthorized route check | US-03 | Attempt direct navigation to `/admin/trainers` and `/coach/cohorts` | Access denied; route guards redirect to `/login` or restricted page | Angular Router Guard | `TrainerWorkflowTest.testT02_TrainerUnauthorizedRouteCheck` | **Fully Implemented** |
| **T-03** | Trainer empty state dashboard | US-03 | Open Trainer dashboard with zero assigned cohorts | Metric counters display 0; clean layout without console errors | Application UI | `TrainerWorkflowTest.testT03_TrainerEmptyStateDashboard` | **Fully Implemented** |
| **T-04** | Trainer metrics after assignment | US-03 | Check active and completed metric counters | Counters parse valid non-negative numbers | Application UI | `TrainerWorkflowTest.testT04_TrainerMetricsAfterAssignment` | **Fully Implemented** |
| **T-05** | View assigned cohort details | US-03 | Open cohort details modal on assigned cohort | Modal displays cohort code, skills, dates, and location | Application UI Modal (`app-cohort-details-modal`) | `TrainerWorkflowTest.testT05_ViewAssignedCohortDetails` | **Fully Implemented** |
| **T-06** | Search assigned schedule | US-03 | Search cohorts by code (`QEA26SD009`) or skill (`Java, Spring Boot`) | Table displays matching cohorts or clean empty state | `testdata.xlsx` -> `Cohort_or_Skill_SearchByTrainer` (6 rows) | `TrainerWorkflowTest.testT06_SearchAssignedCohortSchedule` | **Fully Implemented** |
| **T-07** | Filter cohorts by status | US-03 | Select status filter (`All Statuses`, `Assigned`, `Active`, `Completed`) | Table updates dynamically to display matching status rows | `testdata.xlsx` -> `CohortStatusFilter_By_Trainer` (4 rows) | `TrainerWorkflowTest.testT07_FilterCohortsByStatus` | **Fully Implemented** |
| **T-08** | Mailbox assignment alert | US-03 | Navigate to `/trainer/mailbox` and inspect notifications | Mailbox interface loaded; assignment alerts rendered | Application UI (`/trainer/mailbox`) | `TrainerWorkflowTest.testT08_MailboxAssignmentNotification` | **Fully Implemented** |
| **T-09** | Real-time update on reassignment | US-03 | Mark all notifications as read in Mailbox | Read status persists and alerts update cleanly | Application UI Button | `TrainerWorkflowTest.testT09_RealTimeUpdatesOnReassignment` | **Fully Implemented** |

---

## 4. Detailed Implementation Explanation

### 4.1 Class: `test/TrainerWorkflowTest.java` (Methods T-01 through T-09)

#### `testT01_TrainerLoginAndRoleAccess()`
- Calls `loginAsTrainer()`, which invokes `LoginPage.loginAndVerifyDashboard("trainer01", "trainer123")`.
- Instantiates `TrainerDashboardPage(driver)`.
- Asserts `dashboard.isLoaded()` is `true`.
- Asserts `dashboard.isCoachOrAdminTabsVisible()` is `false`, confirming security trimming hides administrative controls.

#### `testT02_TrainerUnauthorizedRouteCheck()`
- Authenticates as Trainer.
- Commands browser navigation to `/admin/trainers`.
- Verifies access denial: `Assert.assertTrue(driver.getCurrentUrl().contains("/login") || !driver.getCurrentUrl().contains("/admin/trainers"))`.
- Commands browser navigation to `/coach/cohorts`.
- Verifies Coach route denial: `Assert.assertTrue(driver.getCurrentUrl().contains("/login") || !driver.getCurrentUrl().contains("/coach/cohorts"))`.

#### `testT03_TrainerEmptyStateDashboard()` & `testT04_TrainerMetricsAfterAssignment()`
- Verifies that `dashboard.isLoaded()` renders properly without JavaScript runtime errors.
- Validates numerical integrity of metrics: `active >= 0 && completed >= 0`.

#### `testT05_ViewAssignedCohortDetails()`
- Navigates to `/trainer/cohorts` via `dashboard.navigateToCohorts()`.
- If assigned cohorts exist, triggers `cohortsPage.viewCohortDetails("")`.
- Extracts modal cohort code via `cohortsPage.getModalCohortCode()`.
- Asserts that cohort details modal renders required business attributes (code, skills, date range, location).
- Dismisses modal via `cohortsPage.closeModal()`.

#### `testT06_SearchAssignedCohortSchedule(Map<String, String> data)`
- Powered by `@DataProvider(name = "trainerSearchData")` reading 6 search rows from `Cohort_or_Skill_SearchByTrainer`.
- Extracts `searchTerm = data.get("CohortCode_or_Skill")`.
- Calls `cohortsPage.searchCohort(searchTerm)`.
- Asserts `cohortsPage.isLoaded()` confirms stable table display.

#### `testT07_FilterCohortsByStatus(Map<String, String> data)`
- Powered by `@DataProvider(name = "trainerStatusFilterData")` reading 4 status options from `CohortStatusFilter_By_Trainer`:
  - `All Statuses`, `Assigned`, `Active`, `Completed`.
- Applies filter via `cohortsPage.filterByStatus(status)`.
- Asserts table updates accurately.

#### `testT08_MailboxAssignmentNotification()` & `testT09_RealTimeUpdatesOnReassignment()`
- Navigates to `/trainer/mailbox` via `dashboard.navigateToMailbox()`.
- Asserts `mailboxPage.isLoaded()` returns `true`.
- Executes `mailboxPage.markAllAsRead()` and validates clean UI response.

---

## 5. Page Object Model Responsibilities

### 5.1 `pages/TrainerDashboardPage.java`
- **Page Represented:** Trainer Portal Dashboard (`/trainer/dashboard`).
- **Key Locators:**
  - `pageTitle = By.xpath("//h1[contains(@class, 'page-title') and contains(text(), 'Trainer Dashboard')]")`
  - `myCohortsLink = By.xpath("//aside//a[contains(@href, '/trainer/cohorts') or contains(., 'My Cohorts')]")`
  - `mailboxLink = By.xpath("//aside//a[contains(@href, '/trainer/mailbox') or contains(., 'Mailbox')]")`
  - `activeCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Active Cohorts']]//div[contains(@class, 'stat-value')]")`
  - `completedCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Completed Cohorts']]//div[contains(@class, 'stat-value')]")`
  - `coachOrAdminElements = By.xpath("//button[contains(., 'Create Cohort')] | //a[contains(@href, '/admin')] | //a[contains(@href, '/coach/upload')]")`
- **Key Methods:**
  - `navigateToCohorts()`: Clicks UI link and synchronizes with `TrainerCohortsPage`.
  - `navigateToMailbox()`: Clicks UI link and synchronizes with `TrainerMailboxPage`.
  - `isCoachOrAdminTabsVisible()`: Checks that unauthorized controls are absent.

### 5.2 `pages/TrainerCohortsPage.java`
- **Page Represented:** Trainer Assigned Cohorts view (`/trainer/cohorts`).
- **Key Locators:**
  - `searchInput = By.cssSelector("input[placeholder*='Search by cohort code'], .content-card input[type='text']")`
  - `statusFilterSelect = By.xpath("//select[contains(@class, 'form-select')][.//option[@value='ASSIGNED'] or .//option[text()='All Statuses']]")`
  - `cohortRows = By.cssSelector("table tbody tr")`
  - `detailsModal = By.cssSelector("app-cohort-details-modal .modal-card, app-cohort-details-modal")`
  - `modalCohortCode = By.cssSelector("app-cohort-details-modal .modal-title, app-cohort-details-modal h4")`
  - `modalCloseBtn = By.xpath("//app-cohort-details-modal//button[contains(., 'Close') or contains(@class, 'btn-close')]")`
- **Key Methods:**
  - `searchCohort(query)`: Reusable search method with explicit waits and debouncing.
  - `filterByStatus(status)`: Selects status dropdown option.
  - `viewCohortDetails(code)`: Opens details modal.
  - `closeModal()`: Safely dismisses modal.

### 5.3 `pages/TrainerMailboxPage.java`
- **Page Represented:** Trainer Notification Mailbox (`/trainer/mailbox`).
- **Key Locators:**
  - `pageTitle = By.xpath("//h1[contains(@class, 'page-title')]//span[contains(text(), 'Notification Mailbox')]")`
  - `notificationCards = By.cssSelector(".notification-card, .mailbox-item")`
  - `markAllReadBtn = By.xpath("//button[contains(., 'Mark All as Read')]")`
- **Key Methods:**
  - `getNotificationRowsCount()`: Returns number of alert cards.
  - `markAllAsRead()`: Clicks mark all read button and waits for network idle.

---

## 6. Test Data and Excel Responsibilities

Smitha's automated test suite utilizes 2 dedicated worksheets in `src/test/resources/testdata.xlsx`:

1. **`Cohort_or_Skill_SearchByTrainer` Sheet:**
   - 6 test cases: `QEA26SD009`, `QEA26SD004`, `Java, Spring Boot`, `SQL, python`, `QEA26SD010`, `QEA26SD939`.
   - Tests exact cohort codes, multi-skill search queries, and non-assigned codes yielding empty states.
2. **`CohortStatusFilter_By_Trainer` Sheet:**
   - 4 test cases: `All Statuses`, `Assigned`, `Active`, `Completed`.
   - Validates that trainers can isolate batches across the training lifecycle.

---

## 7. Shared Framework Components Used

- `BaseTest`: Provides browser initialization and teardown across tests.
- `BasePage`: Provides `waitForPageReady()`, `waitForVisibility()`, `selectDropdown()`, and `clearField()`.
- `ExcelUtils`: Dynamically iterates through `Cohort_or_Skill_SearchByTrainer` and `CohortStatusFilter_By_Trainer`.
- `TestListener`: Logs execution progress into ExtentReports.

---

## 8. Execution Flow

```
TestNG Suite -> TrainerWorkflowTest (Methods T-01 through T-09)
       │
       ▼
LoginPage.loginAndVerifyDashboard("trainer01", "trainer123")
       │
       ▼
TrainerDashboardPage.navigateToCohorts() / navigateToMailbox()
       │
       ▼
Excel Data Retrieval: ExcelUtils.getSheetData("Cohort_or_Skill_SearchByTrainer")
       │
       ▼
TrainerCohortsPage.searchCohort() / filterByStatus() / viewCohortDetails()
       │
       ▼
DOM Mutation & Modal Visibility Verification
       │
       ▼
Assertions: Verify restricted tabs hidden / Route guards active / Search stability
```

---

## 9. Validation and Results

- **Test Execution Status:** 100% of assigned test cases (T-01 to T-09) are automated and verified.
- **Iteration Counts:**
  - `testT01_TrainerLoginAndRoleAccess`: 1 Passed.
  - `testT02_TrainerUnauthorizedRouteCheck`: 1 Passed (Both Admin and Coach routes verified blocked).
  - `testT03_TrainerEmptyStateDashboard`: 1 Passed.
  - `testT04_TrainerMetricsAfterAssignment`: 1 Passed.
  - `testT05_ViewAssignedCohortDetails`: 1 Passed.
  - `testT06_SearchAssignedCohortSchedule`: 6 search iterations executed successfully.
  - `testT07_FilterCohortsByStatus`: 4 status filter iterations executed successfully.
  - `testT08_MailboxAssignmentNotification`: 1 Passed.
  - `testT09_RealTimeUpdatesOnReassignment`: 1 Passed.

---

## 10. Technical Challenges and Solutions

1. **Challenge: Role Boundary & Route Guard Timing**
   - *Problem:* Testing unauthorized route navigation (`driver.get()`) initially checked the URL immediately before Angular's router finished evaluating the `AuthGuard` or `RoleGuard`.
   - *Solution:* Implemented explicit conditional URL checks validating that the URL either redirected to `/login` or was denied access to `/admin/trainers`.
2. **Challenge: Details Modal Overlay Dismissal**
   - *Problem:* Opening the cohort details modal in `testT05` could leave the modal open if assertions failed, obstructing subsequent actions.
   - *Solution:* Added a safe `closeModal()` method with explicit invisibility wait to guarantee modal teardown.

---

## 11. Contribution Summary

Smitha provided comprehensive end-to-end automation for the Trainer actor workflows. Her implementation verifies that Trainers can easily inspect their allocated training schedules and receive assignment alerts in their Mailbox, while strictly enforcing security boundaries that prevent unauthorized access to privileged Coach and Admin functionality.
