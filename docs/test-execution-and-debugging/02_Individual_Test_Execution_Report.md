# 02 - Individual Test Execution Report

**Project:** TrainMate – Cohort & Trainer Automatic Allocation System  
**Repository:** `https://github.com/nihr0025/trainmate-test-1`  
**Workspace:** `C:\Users\nihar\ideaprojects\trainmate-test-1`  
**Author:** Antigravity Senior Automation Engineer  
**Date:** October 31, 2026  
**Execution Environment:** Windows 11, Google Chrome 155 (Headed Mode), Java 17.0.15, TestNG 7.10.2  

---

## 1. Executive Summary

This report documents the verification and outcome of every automated test case across the TrainMate automation framework. Each test case maps directly to the functional requirements, user stories, and Excel test data sheets.

---

## 2. Comprehensive Test Case Execution Matrix

### A. Authentication & Access Tests (`test.LoginTest`)

| Test Case ID | Test Method Name | Role | Test Data Source | Failure / Issue Observed | Root Cause & Code Remediation | Final Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **L-01** | `testCoachValidLogin` | Coach | `config.properties` (`coach01`) | None | Valid login verified; navigated to `/coach/dashboard`. | **PASSED** |
| **L-02** | `testLoginWithExcelData` (Row 1) | Admin | `LoginData` (`admin01`) | None | Excel-driven login authenticated; verified `/admin/dashboard`. | **PASSED** |
| **L-03** | `testLoginWithExcelData` (Row 2) | Trainer | `LoginData` (`trainer01`) | None | Excel-driven login authenticated; verified `/trainer/dashboard`. | **PASSED** |
| **L-04** | `testLoginWithExcelData` (Row 3) | Trainer | `LoginData` (`trainer02`) | None | Excel-driven login authenticated; verified `/trainer/dashboard`. | **PASSED** |
| **L-05** | `testLoginWithExcelData` (Row 4) | Coach | `LoginData` (`ramya`) | None | Excel-driven login authenticated; verified `/coach/dashboard`. | **PASSED** |

---

### B. Trainer Role Tests (`test.TrainerWorkflowTest`)

| Test Case ID | Test Method Name | Role | Test Data Source | Failure / Issue Observed | Root Cause & Code Remediation | Final Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **T-01** | `testT01_TrainerLoginAndRoleAccess` | Trainer | `config.properties` | None | Asserted dashboard load & absence of Coach/Admin tabs. | **PASSED** |
| **T-02** | `testT02_TrainerUnauthorizedRouteCheck` | Trainer | Direct URL GET | None | Asserted auth guards redirect `/admin/trainers` & `/coach/cohorts`. | **PASSED** |
| **T-03** | `testT03_TrainerEmptyStateDashboard` | Trainer | Runtime UI | None | Asserted clean dashboard load without console errors. | **PASSED** |
| **T-04** | `testT04_TrainerMetricsAfterAssignment` | Trainer | Runtime UI | None | Asserted metric cards (Active, Completed, Mails) are $\ge 0$. | **PASSED** |
| **T-05** | `testT05_ViewAssignedCohortDetails` | Trainer | Runtime UI | Timeout on search input; URL stayed on `/dashboard` | Native click failed on Angular router link; pageTitle had fallback `\| //h1[contains(@class, 'page-title')]`. Fixed with `clickNav()` and specific H1 text check. | **PASSED** |
| **T-06** | `testT06_SearchAssignedCohortSchedule` | Trainer | `Cohort_or_Skill_SearchByTrainer` (6 rows) | Navigation timeout to cohorts page | Same Angular router navigation blocker. Fixed with `clickNav()`. Tested search across cohort codes and skills. | **PASSED** |
| **T-07** | `testT07_FilterCohortsByStatus` | Trainer | `CohortStatusFilter_By_Trainer` (4 rows) | Navigation timeout to cohorts page | Same Angular router navigation blocker. Fixed with `clickNav()`. Tested `All Statuses`, `Assigned`, `Active`, `Completed`. | **PASSED** |
| **T-08** | `testT08_MailboxAssignmentNotification` | Trainer | Runtime UI | URL was not `/mail` | Native click on Mailbox did not navigate. Fixed with `clickNav(mailboxNavLink)` and specific `pageTitle` in `TrainerMailboxPage`. | **PASSED** |
| **T-09** | `testT09_RealTimeUpdatesOnReassignment` | Trainer | Runtime UI | URL was not `/mail` | Same mailbox navigation blocker. Fixed with `clickNav()`. Verified "Mark All as Read" workflow. | **PASSED** |

---

### C. Coach Role Tests (`test.CoachWorkflowTest`)

| Test Case ID | Test Method Name | Role | Test Data Source | Failure / Issue Observed | Root Cause & Code Remediation | Final Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **C-01** | `testC01_SignInAndAccessDashboard` | Coach | `config.properties` | None | Authenticates coach and asserts URL contains `/coach`. | **PASSED** |
| **C-02** | `testC02_AccessControlValidation` | Coach | Invalid User / Direct URL | None | Asserts invalid credentials error & protected route guard. | **PASSED** |
| **C-03a**| `testC03_CohortFormValidationMandatoryAndDates` | Coach | `Cohort_Creation` (Row 4) | None | Validates mandatory blank error and Start Date > End Date error. | **PASSED** |
| **C-03b**| `testC03_CohortFormValidationExcelDriven` | Coach | `Cohort_Creation` (Negative rows) | None | Validates Excel-driven negative cohort submissions with normalized dates. | **PASSED** |
| **C-04** | `testC04_CreateCohortWithValidDetails` | Coach | `Cohort_Creation` (Positive rows) | Ambiguous H1 page readiness | Fixed `pageTitle` in `CohortPage` and used `clickNav()`. Appends timestamp for unique cohort code. | **PASSED** |
| **C-05** | `testC05_AutomatedTrainerAllocationCheck` | Coach | Runtime UI | Ambiguous H1 page readiness | Filters by `ASSIGNED`, views modal, asserts assigned trainer & 100-pt breakdown. | **PASSED** |
| **C-06** | `testC06_AllocationRuleEnforcementOnNoMatch`| Coach | Runtime UI | Ambiguous H1 page readiness | Filters by `UNASSIGNED`, asserts unassigned badge or notification. | **PASSED** |
| **C-07** | `testC07_SearchAndFilterCohortList` | Coach | `Cohort_Search_By_Coach` (6 rows) | Navigation blocker | Fixed navigation to `CohortPage` with `clickNav()`. Validates table search filtering. | **PASSED** |
| **C-07b**| `testC07b_CohortStatusFilter` | Coach | `CohortStatusfilter_By_Coach` (4 rows) | Navigation blocker | Fixed navigation to `CohortPage` with `clickNav()`. Validates dropdown status filtering. | **PASSED** |
| **C-08** | `testC08_CohortEditingAndReallocation` | Coach | `Cohort_Editing_By_Coach` (5 rows) | Navigation blocker | Opens edit modal, updates skills/dates, asserts save and table refresh. | **PASSED** |
| **C-09** | `testC09_BatchUploadValidation` | Coach | Runtime UI | Navigation blocker | Navigates to `/coach/upload`, selects batch upload tab, asserts URL. | **PASSED** |
| **C-10** | `testC10_BatchUploadValidCohortWorkbook` | Coach | `testdata.xlsx` | Navigation blocker | Selects batch tab, uploads workbook path, asserts acceptance. | **PASSED** |

---

### D. Admin Role Tests (`test.AdminWorkflowTest`)

| Test Case ID | Test Method Name | Role | Test Data Source | Failure / Issue Observed | Root Cause & Code Remediation | Final Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **A-01** | `testA01_AdminSignInAndSystemOversight` | Admin | `config.properties` | None | Verifies system-wide cohort and trainer pool metrics on dashboard. | **PASSED** |
| **A-02** | `testA02_DashboardDataIntegrityCheck` | Admin | Runtime UI | None | Asserts non-negative values for total, assigned, and unassigned metrics. | **PASSED** |
| **A-03** | `testA03_GlobalCohortSearchAndFilter` | Admin | `Cohort_Search_By_Admin` (6 rows) | Timeout waiting for `input[placeholder*='Search code']` | Native click didn't navigate; `pageTitle` matched dashboard H1. Fixed with `clickNav()` and specific `pageTitle`. | **PASSED** |
| **A-03b**| `testA03b_GlobalCohortServiceLineFilter` | Admin | `CohortSLfilter_By_Admin` (4 rows) | Navigation timeout | Same navigation blocker. Fixed with `clickNav()`. Dropdown filtering verified. | **PASSED** |
| **A-03c**| `testA03c_GlobalCohortStatusFilter` | Admin | `CohortStatusfilter_By_Admin` (4 rows) | Navigation timeout | Same navigation blocker. Fixed with `clickNav()`. Status filtering verified. | **PASSED** |
| **A-04** | `testA04_ExportAllocationReport` | Admin | Runtime UI | None | Triggers allocation report export cleanly. | **PASSED** |
| **A-05** | `testA05_TrainerRegistrationValidation` | Admin | `TrainerRegistration_By_Admin` (Negative rows) | Navigation blocker to `/admin/trainers` | Fixed `navigateToTrainers()` with `clickNav()` and specific `pageTitle`. Validates form blocks invalid registration. | **PASSED** |
| **A-05b**| `testA05b_TrainerRegistrationDirectValidation` | Admin | `TrainerRegistration_By_Admin` (Row 7) | Navigation blocker | Validates direct blank submission and availableTill < availableFrom dates. | **PASSED** |
| **A-06** | `testA06_RegisterNewTrainer` | Admin | `TrainerRegistration_By_Admin` (Positive rows) | Navigation blocker | Generates unique name/email with timestamp, registers trainer successfully. | **PASSED** |
| **A-07** | `testA07_TrainerPoolFilterAndSearch` | Admin | `Trainer_Search_By_Admin` (5 rows) | Navigation blocker | Filters trainer pool by skill, name, and service line. | **PASSED** |
| **A-07b**| `testA07b_TrainerPoolFilterByStatus` | Admin | `TrainerFilterByStatus_By_Admin` (3 rows) | Navigation blocker | Filters trainer pool by availability status (`All Statuses`, `Available`, etc.). | **PASSED** |
| **A-08** | `testA08_EditTrainerWorkloadAndSkills` | Admin | Runtime UI | Navigation blocker | Opens trainer edit modal, updates workload ceiling & skills, asserts save. | **PASSED** |
| **A-09** | `testA09_AdminManualReassignmentAndWorkloadSync` | Admin | Runtime UI | Navigation blocker | Opens manual reassignment modal, verifies trainer dropdown & closes. | **PASSED** |
| **A-10** | `testA10_SystemWideStatusUpdateSync` | Admin | Runtime UI | Navigation blocker | Updates cohort status to `COMPLETED` and asserts persistence. | **PASSED** |
