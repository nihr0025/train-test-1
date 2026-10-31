# Document 13 — Individual Module Workflows & Team Work Distribution

**Project Name:** TrainMate — Cohort & Trainer Automatic Allocation System  
**Document Purpose:** Detailed technical breakdown of all 5 team member workstreams, assigned test-case IDs, Java methods, workflows, and interview speaking points.

---

## 1. Master Team Work Distribution Overview

| Team Member | Module Workstream | Assigned Test Cases | Primary Test Class | Key Page Objects | Supporting Shared Classes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Nihar** | **Complete Trainer Workflow & Framework Architect** | `T-01` through `T-09` *(plus `L-01`, `L-02`)* | `TrainerWorkflowTest.java`, `LoginTest.java` | `TrainerDashboardPage`, `TrainerCohortsPage`, `TrainerMailboxPage`, `LoginPage` | Architect of: `BasePage`, `DriverFactory`, `ExcelUtils`, `TestListener`, `ScreenshotUtils`, `ConfigReader` |
| **Avinash** | **Coach Workflow (Part 1): Access & Cohort Creation** | `C-01` through `C-06` *(including `C-03b`)* | `CoachWorkflowTest.java` | `CoachDashboardPage`, `CohortPage`, `LoginPage` | `BaseTest`, `BasePage`, `ExcelUtils`, `ConfigReader`, `TestListener` |
| **Ramya** | **Coach Workflow (Part 2): Cohort Mgmt & Batch Upload**| `C-07` through `C-10` *(including `C-07b`)* | `CoachWorkflowTest.java` | `CohortPage`, `BatchUploadPage`, `CoachDashboardPage` | `BaseTest`, `BasePage`, `ExcelUtils`, `ScreenshotUtils`, `TestListener` |
| **Chavi** | **Admin Workflow (Part 1): System Oversight & Cohorts** | `A-01` through `A-04` *(including `A-03b`, `A-03c`)*| `AdminWorkflowTest.java` | `AdminDashboardPage`, `AdminCohortsPage`, `LoginPage` | `BaseTest`, `BasePage`, `ExcelUtils`, `ConfigReader`, `TestListener` |
| **Smitha** | **Admin Workflow (Part 2): Trainer Pool & Reassignment**| `A-05` through `A-10` *(including `A-05b`, `A-07b`)*| `AdminWorkflowTest.java` | `TrainerManagementPage`, `AdminCohortsPage`, `AdminDashboardPage` | `BaseTest`, `BasePage`, `ExcelUtils`, `ConfigReader`, `TestListener` |

---

## 2. Nihar — Complete Trainer Workflow & Framework Architecture

### 2.1 Role & Scope
Nihar leads the **Trainer Workflow** (`T-01` to `T-09`) and serves as the **Framework Architect**, designing the POM base layer, the `DriverFactory`, `ExcelUtils`, and `TestListener`.

### 2.2 Assigned Test Scenarios
- **`T-01`:** Trainer login and role access verification (asserts Coach creation and Admin tabs are hidden).
- **`T-02`:** Route guard check (asserts direct navigation to `/admin/trainers` and `/coach/cohorts` is blocked).
- **`T-03`:** Trainer empty-state dashboard rendering check.
- **`T-04`:** Metrics validation (asserts Active and Completed cohort counters are non-negative).
- **`T-05`:** View assigned cohort details modal (code, skills, dates, location).
- **`T-06`:** Search assigned cohort schedule via Excel data (`Cohort_or_Skill_SearchByTrainer`).
- **`T-07`:** Filter cohorts by status via Excel data (`CohortStatusFilter_By_Trainer`).
- **`T-08`:** Mailbox assignment notification check (`/trainer/mailbox`).
- **`T-09`:** Real-time updates on reassignment (interacts with 'Mark All as Read').

### 2.3 What Nihar Should Speak in an Interview
> *"In TrainMate, I automated the complete Trainer workflow covering test cases T-01 through T-09 and designed our core framework infrastructure. My tests verify that instructors can log in, view assigned classes, search schedules using Excel test data, and receive mailbox alerts, while strictly enforcing route security so trainers cannot access Admin or Coach pages. On the architecture side, I built our ThreadLocal `DriverFactory`, our explicit wait engine in `BasePage`, the Apache POI Excel reader, and ExtentReports listeners."*

---

## 3. Avinash — Coach Workflow (Part 1)

### 3.1 Role & Scope
Avinash is the lead for **Coach Workflow Part 1** (`C-01` to `C-06`), focusing on Coach authentication, form validation, positive cohort creation, and verifying the 100-point automatic allocation engine.

### 3.2 Assigned Test Scenarios
- **`C-01`:** Coach sign-in and dashboard access verification.
- **`C-02`:** Access control validation (rejecting bad credentials and blocking unauthorized admin paths).
- **`C-03`:** Form boundary checks (blank mandatory submissions and Start Date > End Date).
- **`C-03b`:** Data-driven negative form testing (executing 9 invalid rows from `Cohort_Creation`).
- **`C-04`:** Positive cohort creation with dynamic unique codes (e.g. `ADM26CS001_84920`) and table verification.
- **`C-05`:** Automated trainer allocation verification (asserts assigned trainer name and the **100-point score breakdown widget**).
- **`C-06`:** Allocation rule enforcement on no-match (asserts unassigned status when no trainer matches).

### 3.3 What Avinash Should Speak in an Interview
> *"I was responsible for Coach Workflow Part 1, automating test cases C-01 through C-06. My module handles cohort creation and trainer allocation. I automated tests that verify coach login, route protection, and form validation—such as blocking submissions where a start date is scheduled after an end date. I also automated positive cohort creation using Excel data and verified that TrainMate's 100-point matching algorithm allocates the correct trainer and renders the score breakdown modal."*

---

## 4. Ramya — Coach Workflow (Part 2)

### 4.1 Role & Scope
Ramya leads **Coach Workflow Part 2** (`C-07` to `C-10`), covering cohort lifecycle searching, status filtering, editing/reallocation, and bulk Excel workbook onboarding.

### 4.2 Assigned Test Scenarios
- **`C-07`:** Search cohort list via Excel data (`Cohort_Search_By_Coach` across codes, skills, and trainers).
- **`C-07b`:** Coach status filter check across all 6 statuses (`CohortStatusfilter_By_Coach`).
- **`C-08`:** Cohort editing and reallocation (tests positive updates and negative invalid date saves across 9 Excel rows).
- **`C-09`:** Batch upload navigation and route validation (`/coach/upload`).
- **`C-10`:** Batch upload valid cohort workbook (transmits `testdata.xlsx` directly to `<input type='file'>`).

### 4.3 What Ramya Should Speak in an Interview
> *"I was responsible for Coach Workflow Part 2, automating test cases C-07 through C-10. My focus was on cohort management and bulk operations. I automated data-driven search and status filtering tests using TestNG DataProviders reading from Excel. In test C-08, I validated cohort editing and algorithmic reallocation, ensuring valid edits save while invalid date sequences are blocked. Finally, I automated the Batch Upload workflow, testing Excel file ingestion directly through hidden file inputs without brittle OS popups."*

---

## 5. Chavi — Admin Workflow (Part 1)

### 5.1 Role & Scope
Chavi leads **Admin Workflow Part 1** (`A-01` to `A-04`), covering executive system oversight, mathematical data integrity audits, global cohort searching, and allocation report exporting.

### 5.2 Assigned Test Scenarios
- **`A-01`:** Admin sign in and executive dashboard oversight.
- **`A-02`:** Dashboard data integrity check (asserts Total, Assigned, and Unassigned numbers are non-negative).
- **`A-03`:** Global cohort search with multi-strategy selector (`Cohort_Search_By_Admin`).
- **`A-03b`:** Global cohort Service Line filtering (`CohortSLfilter_By_Admin` across ADM, QEA).
- **`A-03c`:** Global cohort lifecycle status filtering (`CohortStatusfilter_By_Admin`).
- **`A-04`:** Export allocation report (triggers report download button without errors).

### 5.3 What Chavi Should Speak in an Interview
> *"I led Admin Workflow Part 1, automating test cases A-01 through A-04. My module covers enterprise system oversight and global cohort management. I automated tests for admin login and dashboard metrics, performed mathematical data integrity audits to verify that summary statistics are valid numbers, and built data-driven tests for global cohort searching and filtering by service line and status using Apache POI. I also verified the allocation report export workflow."*

---

## 6. Smitha — Admin Workflow (Part 2)

### 6.1 Role & Scope
Smitha leads **Admin Workflow Part 2** (`A-05` to `A-10`), covering enterprise Trainer Pool Management, trainer onboarding validations, workload modification, manual reassignment, and status synchronization.

### 6.2 Assigned Test Scenarios
- **`A-05`:** Trainer registration negative validation using Excel data (`TrainerRegistration_By_Admin`).
- **`A-05b`:** Trainer boundary validation (direct blank submission and `Available Till < Available From` check).
- **`A-06`:** Register new trainer positive test with dynamic unique email (`auto{timestamp}@cognizant.com`).
- **`A-07`:** Trainer pool search via Excel (`Trainer_Search_By_Admin`).
- **`A-07b`:** Trainer availability status filtering (`TrainerFilterByStatus_By_Admin`).
- **`A-08`:** Edit trainer workload ceiling and skills modal.
- **`A-09`:** Admin manual reassignment modal access and clean dismissal.
- **`A-10`:** System-wide status synchronization (updates cohort status to `COMPLETED` and verifies persistence).

### 6.3 What Smitha Should Speak in an Interview
> *"In TrainMate, I was responsible for Admin Workflow Part 2, automating test cases A-05 through A-10. My focus was on Trainer Pool Management and system-wide allocation control. I automated trainer registration tests—including negative validations for missing fields and inverted availability dates, as well as positive registration with dynamic unique emails and custom JavaScript date injection. I also automated searching and status filtering in the trainer pool, editing workload ceilings, testing manual reassignment workflows, and verifying status synchronization across the platform."*
