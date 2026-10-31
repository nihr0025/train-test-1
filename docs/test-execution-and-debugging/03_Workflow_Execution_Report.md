# 03 - Role-Based Workflow Execution Report

**Project:** TrainMate – Cohort & Trainer Automatic Allocation System  
**Repository:** `https://github.com/nihr0025/trainmate-test-1`  
**Workspace:** `C:\Users\nihar\ideaprojects\trainmate-test-1`  
**Author:** Antigravity Senior Automation Engineer  
**Date:** October 31, 2026  
**Execution Environment:** Windows 11, Google Chrome 155 (Headed Mode), Java 17.0.15, TestNG 7.10.2  

---

## 1. Overview of Workflow Architecture

The TrainMate automation test suite tests three core organizational roles—**Coach**, **Trainer**, and **Admin**—plus foundational **Authentication**.

All tests adhere strictly to the true end-user navigation flow:
$$\text{Browser Init} \longrightarrow \text{Login} \longrightarrow \text{Verify Dashboard} \longrightarrow \text{Navigate via UI} \longrightarrow \text{Interact/Assert} \longrightarrow \text{Cleanup}$$

Direct URL jumping to sub-features is eliminated in favor of real UI link and button interactions via the resilient `clickNav()` framework helper.

```
       +-------------------------------------------------------+
       |             TrainMate Allocation Portal               |
       +-------------------------------------------------------+
                                   |
         +-------------------------+-------------------------+
         |                         |                         |
         v                         v                         v
+-----------------+       +-----------------+       +-----------------+
|   Coach Role    |       |  Trainer Role   |       |   Admin Role    |
| (test.Coach...) |       | (test.Trainer..)|       | (test.Admin...) |
+-----------------+       +-----------------+       +-----------------+
  - C-01: Sign In           - T-01: Role Acc.         - A-01: Oversight
  - C-02: Auth Guard        - T-02: Auth Guard        - A-02: Integrity
  - C-03: Validation        - T-03: Empty State       - A-03: Search/Filter
  - C-04: Create Cohort     - T-04: Metrics           - A-04: Export Report
  - C-05: Auto Allocation   - T-05: View Details      - A-05: Registration
  - C-06: Unassigned Rule   - T-06: Schedule Search   - A-06: Valid Reg
  - C-07: Search/Filter     - T-07: Status Filter     - A-07: Pool Search
  - C-08: Edit/Reallocate   - T-08: Mailbox Alert     - A-08: Workload Edit
  - C-09: Upload Tab        - T-09: Real-Time Sync    - A-09: Reassignment
  - C-10: Batch Upload                                - A-10: Status Sync
```

---

## 2. Authentication Workflow Execution (`LoginTest.java`)

### Test Cases & Methods:
1. `testCoachValidLogin`: Coach credential authentication from `config.properties` (`coach01`).
2. `testLoginWithExcelData` (Data-driven from sheet `LoginData`):
   - Row 1: `admin01` (Admin role -> `/admin/dashboard`)
   - Row 2: `trainer01` (Trainer role -> `/trainer/dashboard`)
   - Row 3: `trainer02` (Trainer role -> `/trainer/dashboard`)
   - Row 4: `ramya` (Coach role -> `/coach/dashboard`)

### Execution Details:
* **Command:** `mvn test -Dtest=LoginTest`
* **Flow:** Navigate to base URL -> Enter username -> Enter password -> Click Sign In -> Wait for network idle -> Assert destination URL and role-specific dashboard readiness.
* **Results:** **5 / 5 PASSED** (0 Failures, 0 Errors, Duration: ~57.9 s).

---

## 3. Trainer Role Workflow Execution (`TrainerWorkflowTest.java`)

### Test Cases & Coverage:
* **T-01: Trainer Login & Role Access:** Verifies trainer lands on `/trainer/dashboard`, and confirms Coach creation and Admin links are hidden in the DOM.
* **T-02: Trainer Unauthorized Route Check:** Attempts direct navigation to `/admin/trainers` and `/coach/cohorts`; asserts redirection to `/login` or route protection.
* **T-03: Trainer Empty State Dashboard:** Validates clean dashboard loading without JavaScript console errors.
* **T-04: Trainer Metrics Verification:** Verifies numeric metric counters (`Active Cohorts`, `Completed Cohorts`, `Mails & Alerts`).
* **T-05: View Assigned Cohort Details:** Navigates via UI to `/trainer/cohorts`, inspects cohort details modal (code, skills, duration, location).
* **T-06: Search Assigned Cohort Schedule:** Data-driven search from `Cohort_or_Skill_SearchByTrainer` sheet.
* **T-07: Filter Cohorts by Status:** Data-driven dropdown filter from `CohortStatusFilter_By_Trainer` sheet (`All Statuses`, `Assigned`, `Active`, `Completed`).
* **T-08: Mailbox Assignment Notification:** Navigates via UI to `/trainer/mail`, validates inbox message cards.
* **T-09: Real-Time Updates on Reassignment:** Validates notification alert acknowledgment and "Mark All as Read" workflow.

### Remediations Applied:
* Upgraded `navigateToCohorts()` and `navigateToMailbox()` to use `clickNav()` with JavaScript dispatching.
* Disambiguated `TrainerCohortsPage` and `TrainerMailboxPage` H1 headers.
* **Verified Outcomes:** T-01 to T-09 executed with 100% pass rate.

---

## 4. Coach Role Workflow Execution (`CoachWorkflowTest.java`)

### Test Cases & Coverage:
* **C-01: Sign In & Access Dashboard:** Authenticates coach and validates `/coach/dashboard` page readiness.
* **C-02: Access Control Validation:** Validates invalid credential rejection and unauthorized access blocks to `/admin/dashboard`.
* **C-03: Form Validation (Mandatory & Date Range):** Validates empty form submission and invalid date ranges (e.g., Start Date > End Date).
* **C-03 (Excel-Driven):** Data-driven validation from `Cohort_Creation` sheet (negative test rows).
* **C-04: Create Cohort with Valid Details:** Data-driven positive rows from `Cohort_Creation`. Generates unique cohort codes (`baseCode + "_" + timestamp`), submits form, and verifies presence in `CohortPage` table.
* **C-05: Automated Trainer Allocation Check:** Filters cohorts by `ASSIGNED`, opens cohort details modal, verifies assigned trainer name and 100-point algorithmic score breakdown.
* **C-06: Allocation Rule Enforcement on No-Match:** Filters cohorts by `UNASSIGNED`, verifies unassigned status alert.
* **C-07 & C-07b: Cohort Search and Status Filter:** Data-driven search and filtering from `Cohort_Search_By_Coach` and `CohortStatusfilter_By_Coach`.
* **C-08: Cohort Editing and Reallocation:** Data-driven editing from `Cohort_Editing_By_Coach`. Updates skills and dates, asserting reallocation.
* **C-09 & C-10: Batch Upload Interfaces:** Navigates to `/coach/upload`, selects batch tab, and uploads `testdata.xlsx`.

---

## 5. Admin Role Workflow Execution (`AdminWorkflowTest.java`)

### Test Cases & Coverage:
* **A-01: System Oversight:** Verifies global cohort distribution metrics and trainer pool statistics on `/admin/dashboard`.
* **A-02: Dashboard Data Integrity:** Asserts that system metrics (`Total Cohorts`, `Assigned`, `Unassigned`) are valid integers.
* **A-03, A-03b, A-03c: Global Search and Filtering:** Data-driven searches and filters from `Cohort_Search_By_Admin`, `CohortSLfilter_By_Admin`, and `CohortStatusfilter_By_Admin`.
* **A-04: Export Allocation Report:** Triggers CSV report download and asserts completion.
* **A-05 & A-05b: Trainer Registration Validation:** Direct blank validation and Excel-driven invalid inputs from `TrainerRegistration_By_Admin`.
* **A-06: Register New Trainer:** Data-driven positive registration generating unique trainer names and emails.
* **A-07 & A-07b: Trainer Pool Search and Availability Filter:** Searches and filters from `Trainer_Search_By_Admin` and `TrainerFilterByStatus_By_Admin`.
* **A-08: Edit Trainer Workload and Skills:** Modifies trainer skills and workload limits in `TrainerManagementPage`.
* **A-09: Manual Reassignment Workflow:** Opens manual reassignment modal for assigned cohorts and verifies trainer selection dropdown.
* **A-10: Status Update Synchronization:** Updates cohort status to `COMPLETED` and asserts persistence.
