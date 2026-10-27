# TrainMate Automation Framework — Team Work Allocation Reference

**Project Name:** TrainMate — Cohort & Trainer Automatic Allocation System  
**Application URL:** [https://train-mate-blond.vercel.app/](https://train-mate-blond.vercel.app/)  
**Testing Pod:** Selenials  
**Document Purpose:** Quick-reference mapping of team member responsibilities, test cases, and code components for interview preparation.  

---

## 1. Master Work Distribution Table

| Team Member | Module / Role | Assigned User Story | Assigned Test Case IDs | Test Classes Automated | Key Page Objects | Supporting Framework Classes Used |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Nihar (Nihar Ranjan Sahu)** | **Trainer Workflow Lead & Framework Architect** | **US-03** (Trainer Portal) & **US-01** (Auth Gateway) | `T-01`, `T-02`, `T-03`, `T-04`, `T-05`, `T-06`, `T-07`, `T-08`, `T-09` *(plus `L-01`, `L-02`)* | `TrainerWorkflowTest.java`, `LoginTest.java` | `TrainerDashboardPage.java`, `TrainerCohortsPage.java`, `TrainerMailboxPage.java`, `LoginPage.java` | Built core infrastructure: `BasePage.java`, `DriverFactory.java`, `ExcelUtils.java`, `TestListener.java`, `ScreenshotUtils.java`, `ConfigReader.java` |
| **Avinash Anand** *(Emp ID: 2521743)* | **Coach Workflows Lead (Part 1)** | **US-02A** (Coach Access, Cohort Creation & Algorithmic Allocation) | `C-01`, `C-02`, `C-03`, `C-03b`, `C-04`, `C-05`, `C-06` | `CoachWorkflowTest.java` | `CoachDashboardPage.java`, `CohortPage.java`, `LoginPage.java` | `BaseTest`, `BasePage` (especially `enterDate`), `ExcelUtils`, `ConfigReader`, `TestListener` |
| **Ramya Thirandi** *(Emp ID: 2521745)* | **Coach Workflows Lead (Part 2)** | **US-02B** (Cohort Management, Filtering & Batch Upload) | `C-07`, `C-07b`, `C-08`, `C-09`, `C-10` | `CoachWorkflowTest.java` | `CohortPage.java`, `BatchUploadPage.java`, `CoachDashboardPage.java` | `BaseTest`, `BasePage`, `ExcelUtils`, `ScreenshotUtils`, `TestListener` |
| **Chavi Maru** *(Emp ID: 2521741)* | **Admin Workflows Lead (Part 1)** | **US-04A** (System Oversight, Cohorts & Reporting) | `A-01`, `A-02`, `A-03`, `A-03b`, `A-03c`, `A-04` | `AdminWorkflowTest.java` | `AdminDashboardPage.java`, `AdminCohortsPage.java`, `LoginPage.java` | `BaseTest`, `BasePage`, `ExcelUtils`, `ConfigReader`, `TestListener` |
| **Smitha** | **Admin Workflows Lead (Part 2)** | **US-04B** (Trainer Pool Management & System Sync) | `A-05`, `A-05b`, `A-06`, `A-07`, `A-07b`, `A-08`, `A-09`, `A-10` | `AdminWorkflowTest.java` | `TrainerManagementPage.java`, `AdminCohortsPage.java`, `AdminDashboardPage.java` | `BaseTest`, `BasePage` (especially `enterDate`), `ExcelUtils`, `ConfigReader`, `TestListener` |

---

## 2. Test Case Description and Verification Index

### Trainer Workflows (Assigned to Nihar)
- **`T-01`**: Trainer login and role access (Verifies dashboard load and hidden admin tabs).
- **`T-02`**: Trainer unauthorized route check (Asserts route guards block `/admin/trainers` and `/coach/cohorts`).
- **`T-03`**: Trainer empty state dashboard check (Verifies clean rendering without batches).
- **`T-04`**: Trainer metrics after assignment (Validates numerical counters for active/completed cohorts).
- **`T-05`**: View assigned cohort details (Opens details modal for code, skills, dates, location).
- **`T-06`**: Search assigned cohort schedule (Excel data-driven search by cohort code or skill).
- **`T-07`**: Filter cohorts by status (Excel data-driven filtering by lifecycle status).
- **`T-08`**: Mailbox assignment notification (Verifies incoming cohort assignment alerts).
- **`T-09`**: Real-time updates on reassignment (Tests 'Mark All as Read' action).

### Coach Workflows — Part 1 (Assigned to Avinash Anand)
- **`C-01`**: Sign in and access dashboard (Verifies coach login and metrics).
- **`C-02`**: Access control validation (Verifies bad login rejection and blocked direct admin URLs).
- **`C-03`**: Cohort form validation check (Direct verification of blank mandatory fields and start date > end date).
- **`C-03b`**: Cohort form validation check (Excel-driven testing across 9 negative rows).
- **`C-04`**: Create cohort with valid details (Positive cohort creation with unique dynamic code).
- **`C-05`**: Automated trainer allocation check (Verifies 100-point algorithmic score breakdown).
- **`C-06`**: Allocation rule enforcement on no-match (Verifies unassigned state when no match is found).

### Coach Workflows — Part 2 (Assigned to Ramya Thirandi)
- **`C-07`**: Search cohort list (Excel data-driven search across 8 rows of codes, skills, and trainers).
- **`C-07b`**: Coach status filter check (Excel data-driven status filtering across 6 statuses).
- **`C-08`**: Cohort editing and reallocation (Excel data-driven testing for valid updates and invalid boundary saves).
- **`C-09`**: Batch upload validation (UI navigation to `/coach/upload`).
- **`C-10`**: Batch upload valid cohort workbook (Direct file upload of `testdata.xlsx` via `<input type='file'>`).

### Admin Workflows — Part 1 (Assigned to Chavi Maru)
- **`A-01`**: Admin sign-in and system oversight (Verifies executive dashboard load and metric cards).
- **`A-02`**: Dashboard data integrity check (Asserts Total, Assigned, and Unassigned counts are non-negative numbers).
- **`A-03`**: Global cohort search and filter (Excel data-driven search with resilient CSS selector).
- **`A-03b`**: Global cohort service line filter (Excel data-driven filtering by Service Line like ADM, QEA).
- **`A-03c`**: Global cohort status filter (Excel data-driven filtering across 7 lifecycle statuses).
- **`A-04`**: Export allocation report (Verifies 'Export Report' action button).

### Admin Workflows — Part 2 (Assigned to Smitha)
- **`A-05`**: Trainer registration validation (Excel data-driven testing across negative rows).
- **`A-05b`**: Trainer boundary & date validation (Direct checks for blank submissions and inverted availability dates).
- **`A-06`**: Register new trainer (Positive registration with dynamic unique email).
- **`A-07`**: Trainer pool filter and search (Excel data-driven search across skills, names, and service lines).
- **`A-07b`**: Trainer pool filter by status (Excel data-driven filtering by availability status).
- **`A-08`**: Edit trainer workload and skills (Updates skills and max workload ceiling to 6).
- **`A-09`**: Admin manual reassignment and workload synchronization (Opens and closes reassignment modal).
- **`A-10`**: System-wide status update synchronization (Changes status to `COMPLETED` and verifies persistence).

---

## 3. Discrepancy & Documentation Source Clarification

When reviewing the original project documents and history:

1. **Authorship in Initial Project Requirement PDF (`media_1792221253273_d3f004c0.pdf`):**
   - **Page 5:** explicitly headers **Section 1: Coach Workflows - Part 1**, naming **Author: Avinash Anand** (Emp ID: 2521743).
   - **Page 6:** explicitly headers **Section 2: Coach Workflows - Part 2**, naming **Author: Ramya Thirandi** (Emp ID: 2521745).
   - **Page 3:** explicitly headers **Section 4: Admin Workflows - Part 1 (System Oversight & Cohorts)**, naming **Author: Chavi Maru** (Emp ID: 2521741).
   - **Pages 2 and 3:** contain **Section 3: Trainer Workflows (T-01 to T-09)**. In preliminary project notes, Smitha was sometimes associated with Trainer work, and Nihar was designated overall architect.
   - **Page 4:** contains **Admin Workflows Part 2 (A-05 to A-10)** without repeating an author header card.
2. **Current Master Allocation Alignment:**
   - Under this definitive master allocation, **Nihar** owns the complete **Trainer Workflow** (`T-01` to `T-09`) in addition to Framework Architecture.
   - **Smitha** owns **Admin Workflow Part 2** (`A-05` to `A-10`, Trainer Pool Management & Synchronization).
   - **Avinash Anand** owns **Coach Workflow Part 1** (`C-01` to `C-06`).
   - **Ramya Thirandi** owns **Coach Workflow Part 2** (`C-07` to `C-10`).
   - **Chavi Maru** owns **Admin Workflow Part 1** (`A-01` to `A-04`).
   - This clear separation guarantees that every module has a dedicated owner and prevents confusion between Trainer and Admin modules.
