# Document 08 — Test Cases and User Stories (Traceability & Coverage)

**Project Name:** TrainMate — Cohort & Trainer Automatic Allocation System  
**Document Purpose:** Complete Requirements Traceability Matrix (RTM) mapping all 5 User Stories, 37 Test Scenarios, preconditions, steps, assertions, and Java methods.

---

## 1. Master Requirements Traceability Matrix (RTM)

The matrix below provides complete bidirectional traceability across all 37 test cases defined in the project:

| US ID | Role | TC ID | Test Scenario Name | Test Data Source | Test Class & Method | Page Objects Used | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **US-01** | Auth | **L-01** | Excel Data-Driven Login | `testdata.xlsx` (`LoginData`) | `LoginTest.testLoginWithExcelData` | `LoginPage`, `CoachDashboardPage` | Automated |
| **US-01** | Auth | **L-02** | Valid Coach Authentication | `config.properties` | `LoginTest.testCoachValidLogin` | `LoginPage`, `CoachDashboardPage` | Automated |
| **US-02A** | Coach | **C-01** | Coach Sign In & Access Dashboard | `config.properties` | `CoachWorkflowTest.testC01_SignInAndAccessDashboard` | `LoginPage`, `CoachDashboardPage` | Automated |
| **US-02A** | Coach | **C-02** | Access Control & Route Guard | Route `/admin/dashboard` | `CoachWorkflowTest.testC02_AccessControlValidation` | `LoginPage` | Automated |
| **US-02A** | Coach | **C-03** | Form Validation (Direct Blank & Dates) | `Cohort_Creation` (Row 4) | `CoachWorkflowTest.testC03_CohortFormValidationMandatoryAndDates` | `CoachDashboardPage` | Automated |
| **US-02A** | Coach | **C-03b**| Form Validation (Excel-Driven) | `Cohort_Creation` (9 rows) | `CoachWorkflowTest.testC03_CohortFormValidationExcelDriven` | `CoachDashboardPage` | Automated |
| **US-02A** | Coach | **C-04** | Create Cohort with Valid Details | `Cohort_Creation` (Valid) | `CoachWorkflowTest.testC04_CreateCohortWithValidDetails` | `CoachDashboardPage`, `CohortPage` | Automated |
| **US-02A** | Coach | **C-05** | Automated Trainer Allocation Check | Live Algorithm Modal | `CoachWorkflowTest.testC05_AutomatedTrainerAllocationCheck` | `CohortPage` | Automated |
| **US-02A** | Coach | **C-06** | Allocation Rule on No-Match | Status `UNASSIGNED` | `CoachWorkflowTest.testC06_AllocationRuleEnforcementOnNoMatch` | `CohortPage` | Automated |
| **US-02B** | Coach | **C-07** | Search Cohort List | `Cohort_Search_By_Coach` | `CoachWorkflowTest.testC07_SearchAndFilterCohortList` | `CohortPage` | Automated |
| **US-02B** | Coach | **C-07b**| Coach Status Filter Check | `CohortStatusfilter_By_Coach` | `CoachWorkflowTest.testC07b_CohortStatusFilter` | `CohortPage` | Automated |
| **US-02B** | Coach | **C-08** | Cohort Editing and Reallocation | `Cohort_Editing_By_Coach` | `CoachWorkflowTest.testC08_CohortEditingAndReallocation` | `CohortPage` | Automated |
| **US-02B** | Coach | **C-09** | Batch Upload Validation | Route `/coach/upload` | `CoachWorkflowTest.testC09_BatchUploadValidation` | `CoachDashboardPage`, `BatchUploadPage` | Automated |
| **US-02B** | Coach | **C-10** | Batch Upload Valid Workbook | `src/test/resources/testdata.xlsx` | `CoachWorkflowTest.testC10_BatchUploadValidCohortWorkbook` | `CoachDashboardPage`, `BatchUploadPage` | Automated |
| **US-03** | Trainer | **T-01** | Trainer Login & Role Access | `config.properties` | `TrainerWorkflowTest.testT01_TrainerLoginAndRoleAccess` | `LoginPage`, `TrainerDashboardPage` | Automated |
| **US-03** | Trainer | **T-02** | Trainer Unauthorized Route Check | Routes `/admin`, `/coach` | `TrainerWorkflowTest.testT02_TrainerUnauthorizedRouteCheck` | `TrainerDashboardPage` | Automated |
| **US-03** | Trainer | **T-03** | Trainer Empty-State Dashboard | UI Zero State | `TrainerWorkflowTest.testT03_TrainerEmptyStateDashboard` | `TrainerDashboardPage` | Automated |
| **US-03** | Trainer | **T-04** | Trainer Metrics After Assignment | UI Stat Cards | `TrainerWorkflowTest.testT04_TrainerMetricsAfterAssignment` | `TrainerDashboardPage` | Automated |
| **US-03** | Trainer | **T-05** | View Assigned Cohort Details | Modal Dialog | `TrainerWorkflowTest.testT05_ViewAssignedCohortDetails` | `TrainerCohortsPage` | Automated |
| **US-03** | Trainer | **T-06** | Search Assigned Cohort Schedule | `Cohort_or_Skill_SearchByTrainer`| `TrainerWorkflowTest.testT06_SearchAssignedCohortSchedule` | `TrainerCohortsPage` | Automated |
| **US-03** | Trainer | **T-07** | Filter Cohorts by Status | `CohortStatusFilter_By_Trainer` | `TrainerWorkflowTest.testT07_FilterCohortsByStatus` | `TrainerCohortsPage` | Automated |
| **US-03** | Trainer | **T-08** | Mailbox Assignment Notification | Route `/trainer/mailbox` | `TrainerWorkflowTest.testT08_MailboxAssignmentNotification` | `TrainerDashboardPage`, `TrainerMailboxPage` | Automated |
| **US-03** | Trainer | **T-09** | Real-Time Updates on Reassignment| UI Action Button | `TrainerWorkflowTest.testT09_RealTimeUpdatesOnReassignment` | `TrainerMailboxPage` | Automated |
| **US-04A** | Admin | **A-01** | Admin Sign In & System Oversight | `config.properties` | `AdminWorkflowTest.testA01_AdminSignInAndSystemOversight` | `LoginPage`, `AdminDashboardPage` | Automated |
| **US-04A** | Admin | **A-02** | Dashboard Data Integrity Check | UI Stat Cards | `AdminWorkflowTest.testA02_DashboardDataIntegrityCheck` | `AdminDashboardPage` | Automated |
| **US-04A** | Admin | **A-03** | Global Cohort Search & Filter | `Cohort_Search_By_Admin` | `AdminWorkflowTest.testA03_GlobalCohortSearchAndFilter` | `AdminCohortsPage` | Automated |
| **US-04A** | Admin | **A-03b**| Global Cohort Service Line Filter | `CohortSLfilter_By_Admin` | `AdminWorkflowTest.testA03b_GlobalCohortServiceLineFilter` | `AdminCohortsPage` | Automated |
| **US-04A** | Admin | **A-03c**| Global Cohort Status Filter | `CohortStatusfilter_By_Admin` | `AdminWorkflowTest.testA03c_GlobalCohortStatusFilter` | `AdminCohortsPage` | Automated |
| **US-04A** | Admin | **A-04** | Export Allocation Report | UI Export Button | `AdminWorkflowTest.testA04_ExportAllocationReport` | `AdminDashboardPage` | Automated |
| **US-04B** | Admin | **A-05** | Trainer Registration Validation | `TrainerRegistration_By_Admin` | `AdminWorkflowTest.testA05_TrainerRegistrationValidation` | `TrainerManagementPage` | Automated |
| **US-04B** | Admin | **A-05b**| Trainer Boundary & Date Validation| Row 7 (`Mani`) | `AdminWorkflowTest.testA05b_TrainerRegistrationDirectValidation`| `TrainerManagementPage` | Automated |
| **US-04B** | Admin | **A-06** | Register New Trainer | Row 8 (`Akash`) | `AdminWorkflowTest.testA06_RegisterNewTrainer` | `TrainerManagementPage` | Automated |
| **US-04B** | Admin | **A-07** | Trainer Pool Filter and Search | `Trainer_Search_By_Admin` | `AdminWorkflowTest.testA07_TrainerPoolFilterAndSearch` | `TrainerManagementPage` | Automated |
| **US-04B** | Admin | **A-07b**| Trainer Pool Filter by Status | `TrainerFilterByStatus_By_Admin`| `AdminWorkflowTest.testA07b_TrainerPoolFilterByStatus` | `TrainerManagementPage` | Automated |
| **US-04B** | Admin | **A-08** | Edit Trainer Workload and Skills | UI Edit Modal | `AdminWorkflowTest.testA08_EditTrainerWorkloadAndSkills` | `TrainerManagementPage` | Automated |
| **US-04B** | Admin | **A-09** | Admin Manual Reassignment Sync | UI Reassign Modal | `AdminWorkflowTest.testA09_AdminManualReassignmentAndWorkloadSync` | `AdminCohortsPage` | Automated |
| **US-04B** | Admin | **A-10** | System-Wide Status Update Sync | Status `COMPLETED` | `AdminWorkflowTest.testA10_SystemWideStatusUpdateSync` | `AdminCohortsPage` | Automated |

---

## 2. Detailed Breakdown of Every User Story

---

### User Story US-01: Authentication & Role Gateway
- **Goal:** Ensure secure access to the system for all user roles while rejecting unauthorized credentials.
- **Key Tests:**
  - `L-01` iterates through valid and invalid logins stored in Excel sheet `LoginData`, verifying appropriate dashboard redirects or error alerts.
  - `L-02` tests standard Coach login and verifies that the browser lands on `/coach/dashboard`.

---

### User Story US-02A: Coach Single Cohort Creation & Algorithmic Allocation
- **Goal:** Enable coaches to create single cohorts, validate mandatory input fields, enforce date sequences, and verify that TrainMate's 100-point algorithm assigns trainers properly.
- **Key Tests:**
  - `C-01` & `C-02`: Coach dashboard access and verifying that unauthenticated users cannot directly browse to `/admin/dashboard`.
  - `C-03` & `C-03b`: Form validations. Tests that empty mandatory fields display error messages and that setting `Start Date > End Date` (e.g. Row 4 in Excel: `2025-05-26` to `2025-04-20`) is blocked. `C-03b` executes 9 invalid combinations from Excel.
  - `C-04`: Positive cohort creation using dynamic timestamps (e.g., `ADM26CS001_84920`) and verifying the cohort appears in the table.
  - `C-05`: Inspects the details modal of an assigned cohort to assert the presence of the assigned trainer and the **100-point score breakdown widget** (Skill Match, Availability, Workload, Experience).
  - `C-06`: Verifies fallback handling: cohorts with no matching trainers remain safely labeled `UNASSIGNED`.

---

### User Story US-02B: Coach Cohort Management & Batch Upload
- **Goal:** Allow coaches to search, filter by lifecycle status, edit existing cohorts, and onboard batches in bulk via Excel workbooks.
- **Key Tests:**
  - `C-07` & `C-07b`: Excel-driven search (by code, skill, or trainer name) and dropdown filtering across all 6 statuses (`Assigned`, `Unassigned`, `Pending`, `Active`, `Completed`, `Cancelled`).
  - `C-08`: Tests editing cohorts from Excel. Asserts that valid date/skill changes save and trigger reallocation, whereas invalid date modifications remain blocked with the edit modal open.
  - `C-09` & `C-10`: Navigates to `/coach/upload` and transmits `testdata.xlsx` directly to the hidden `<input type='file'>` element, verifying clean file ingestion.

---

### User Story US-03: Trainer Portal, Schedules & Notifications
- **Goal:** Provide trainers with schedule visibility, role access security trimming, and notification management.
- **Key Tests:**
  - `T-01` & `T-02`: Validates trainer dashboard load and confirms that Coach creation buttons and Admin tabs are hidden. Directly attempts navigation to `/admin/trainers` and `/coach/cohorts`, asserting that Angular route guards redirect back to login.
  - `T-03` & `T-04`: Validates empty-state rendering without errors and asserts that Active and Completed cohort counters are non-negative numbers.
  - `T-05` to `T-07`: Opens cohort details modal, executes Excel-driven search by course code or skill, and filters schedule by status.
  - `T-08` & `T-09`: Verifies the Trainer Mailbox (`/trainer/mailbox`) displays assignment notification cards and tests the 'Mark All as Read' action button.

---

### User Story US-04A: Admin System Oversight, Global Cohorts & Reporting
- **Goal:** Empower operations administrators with enterprise-wide metrics, global search tools, and executive reporting.
- **Key Tests:**
  - `A-01` & `A-02`: Verifies executive dashboard load and audits data integrity (Total, Assigned, and Unassigned cohort numbers must be non-negative).
  - `A-03` to `A-03c`: Global multi-strategy cohort search, Service Line dropdown filtering (ADM, QEA), and lifecycle status filtering using Excel test data.
  - `A-04`: Asserts that clicking the 'Export Report' action button triggers allocation report generation cleanly without breaking the dashboard.

---

### User Story US-04B: Admin Trainer Pool Oversight & Reassignment
- **Goal:** Enable administrators to onboard certified trainers, enforce registration validation, edit workloads, and override automated assignments.
- **Key Tests:**
  - `A-05` & `A-05b`: Data-driven negative registration testing from Excel, plus direct boundary validation: blank submission and setting `Available Till` earlier than `Available From` (Row 7 `Mani`).
  - `A-06`: Positive trainer registration with dynamic unique email (`auto{timestamp}@cognizant.com`) and JavaScript date injection.
  - `A-07` & `A-07b`: Excel-driven trainer search (by skill, name, or service line) and availability status filtering (`Available`, `Unavailable`, `At capacity`).
  - `A-08`: Edits an existing trainer's profile, updating skillset and capping workload ceiling to 6 batches.
  - `A-09`: Opens and cleanly dismisses the manual reassignment interface.
  - `A-10`: Updates a cohort's status to `COMPLETED` and asserts system-wide persistence.
