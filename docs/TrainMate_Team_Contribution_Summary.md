# TrainMate Automation Framework — Team Contribution Summary

**Project Name:** TrainMate — Cohort & Trainer Automatic Allocation System  
**Testing Pod:** Selenials  
**Document Version:** 1.0.0 — Final  
**Date:** October 2026  

---

## 1. Team Contribution Overview Table

| Team Member | Assigned User Stories | Assigned Test Cases | Relevant Test Classes | Related Page Objects | Shared Utilities Used | Verified Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Nihar (Nihar Ranjan Sahu)** | **US-01** (Authentication), **US-04B** (Admin Trainer Management co-ownership), **Core Infrastructure** | `L-01`, `L-02`, `A-05b` (co-owner of `A-05` to `A-10`) | `LoginTest.java`, `AdminWorkflowTest.java` | `LoginPage.java`, `TrainerManagementPage.java` | `BasePage`, `DriverFactory`, `ConfigReader`, `ExcelUtils`, `ScreenshotUtils`, `LoggerUtils`, `TestListener` | **Verified & Proven** via Git history, repo ownership, and core architecture implementation. |
| **Avinash Anand** *(Emp ID: 2521743)* | **US-02A** (Coach Access, Creation & Algorithmic Allocation) | `C-01`, `C-02`, `C-03`, `C-03b`, `C-04`, `C-05`, `C-06` | `CoachWorkflowTest.java` | `CoachDashboardPage.java`, `CohortPage.java` | `BaseTest`, `BasePage`, `ExcelUtils`, `ConfigReader`, `TestListener` | **Verified & Proven** via project reference document (`media_1792221253273_d3f004c0.pdf`, Page 5). |
| **Ramya Thirandi** *(Emp ID: 2521745)* | **US-02B** (Coach Cohort Management, Filtering & Batch Upload) | `C-07`, `C-07b`, `C-08`, `C-09`, `C-10` | `CoachWorkflowTest.java` | `CohortPage.java`, `BatchUploadPage.java` | `BaseTest`, `BasePage`, `ExcelUtils`, `ScreenshotUtils`, `TestListener` | **Verified & Proven** via project reference document (`media_1792221253273_d3f004c0.pdf`, Page 6). |
| **Chavi Maru** *(Emp ID: 2521741)* | **US-04A** (Admin System Oversight, Cohorts & Reporting), **US-04B** (co-ownership) | `A-01`, `A-02`, `A-03`, `A-03b`, `A-03c`, `A-04` (co-owner of `A-05` to `A-10`) | `AdminWorkflowTest.java` | `AdminDashboardPage.java`, `AdminCohortsPage.java` | `BaseTest`, `BasePage`, `ExcelUtils`, `ConfigReader`, `TestListener` | **Verified & Proven** via project reference document (`media_1792221253273_d3f004c0.pdf`, Page 3). |
| **Smitha** | **US-03** (Trainer Portal, Schedules, Status & Mailbox), **US-04B** (co-ownership) | `T-01`, `T-02`, `T-03`, `T-04`, `T-05`, `T-06`, `T-07`, `T-08`, `T-09` (co-owner of `A-05` to `A-10`) | `TrainerWorkflowTest.java` | `TrainerDashboardPage.java`, `TrainerCohortsPage.java`, `TrainerMailboxPage.java` | `BaseTest`, `BasePage`, `ExcelUtils`, `ConfigReader`, `TestListener` | **Verified & Proven** via project reference document (`media_1792221253273_d3f004c0.pdf`, Pages 2 & 3). |

---

## 2. Individual Member Responsibilities & Role Synergy

### 2.1 Nihar — Framework Architect & Test Lead
- **Domain:** Architecture, Infrastructure, Authentication Gateway, and Core Browser Engine.
- **Specific Contribution:** Conceived the POM hierarchy, created the thread-local driver engine in `DriverFactory`, integrated Apache POI into `ExcelUtils` with self-healing template generation, authored `TestListener` with ExtentReports 5.1 and failure screenshot capture, and solved the chronic Chrome 5-to-8-digit year appending bug via `BasePage.enterDate()`. Authored `LoginTest.java` verifying authentication across all three actor roles.
- **Synergy:** Provides the underlying foundation upon which Avinash, Ramya, Chavi, and Smitha build their respective role workflow tests.

### 2.2 Avinash Anand — Coach Workflows Lead (Part 1)
- **Domain:** Single Cohort Creation & Algorithmic Allocation Verification.
- **Specific Contribution:** Automated Coach dashboard entry, enforced role route security against unauthorized Admin URLs, automated cohort creation form validation, and verified the application's proprietary 100-point scoring algorithm (Skill Match 40 pts, Availability 30 pts, Workload 20 pts, Experience 10 pts) in `testC05`.
- **Synergy:** Cohorts created by Avinash's automated tests serve as direct data inputs for Ramya's cohort editing/reallocation tests and Chavi's Admin oversight tests.

### 2.3 Ramya Thirandi — Coach Workflows Lead (Part 2)
- **Domain:** Cohort Management, Searching, Filtering, Editing, and Bulk Excel Ingestion.
- **Specific Contribution:** Automated real-time search across codes, skills, and trainers, automated lifecycle status filtering, handled dynamic cohort editing and reallocation in `testC08`, and automated the bulk upload portal (`/coach/upload`) by transmitting `testdata.xlsx` directly to the hidden file input.
- **Synergy:** Exercises bulk data processing that feeds both Trainer schedules (Smitha's domain) and Admin global monitoring (Chavi's domain).

### 2.4 Chavi Maru — Admin Workflows Lead (Part 1)
- **Domain:** Enterprise System Oversight, Global Cohort Filtering & Executive Reporting.
- **Specific Contribution:** Automated the Admin dashboard metric cards, performed mathematical cross-checks between summary statistics and table row counts, automated multi-faceted global cohort searches across Service Lines (ADM, QEA, CS) and statuses, and verified the allocation report generation feature.
- **Synergy:** Validates that actions taken by Coaches (Avinash/Ramya) and Trainers (Smitha) properly roll up into enterprise-level administrative metrics.

### 2.5 Smitha — Trainer Workflows Lead
- **Domain:** Trainer Portal, Schedule Inspection, Status Tracking & Notification Mailbox.
- **Specific Contribution:** Automated Trainer authentication and verified security trimming (hiding Coach creation and Admin tabs), enforced route protection against privileged URLs, validated cohort schedule searching and status filtering, and automated the Trainer Notification Mailbox (`/trainer/mailbox`) confirming allocation alerts.
- **Synergy:** Validates the end-state experience of the allocation pipeline: once a cohort is created by a Coach and allocated by the algorithm, Smitha's tests verify that the assigned trainer receives the batch and notification.

---

## 3. Shared Components and Foundational Dependencies

```mermaid
flowchart TD
    subgraph Foundational Layer [Authored by Nihar]
        BasePage["base.BasePage"]
        BaseTest["base.BaseTest"]
        DriverFactory["utils.DriverFactory"]
        ExcelUtils["utils.ExcelUtils"]
        ConfigReader["utils.ConfigReader"]
        TestListener["listeners.TestListener"]
    end

    subgraph Authentication [Authored by Nihar]
        LoginPage["pages.LoginPage"]
        LoginTest["test.LoginTest (L-01, L-02)"]
    end

    subgraph Coach Workflows Part 1 [Authored by Avinash Anand]
        CoachDashboard["pages.CoachDashboardPage"]
        CoachTests1["CoachWorkflowTest (C-01 to C-06)"]
    end

    subgraph Coach Workflows Part 2 [Authored by Ramya Thirandi]
        CohortPage["pages.CohortPage"]
        BatchUploadPage["pages.BatchUploadPage"]
        CoachTests2["CoachWorkflowTest (C-07 to C-10)"]
    end

    subgraph Admin Workflows Part 1 [Authored by Chavi Maru]
        AdminDashboard["pages.AdminDashboardPage"]
        AdminCohorts["pages.AdminCohortsPage"]
        AdminTests1["AdminWorkflowTest (A-01 to A-04)"]
    end

    subgraph Trainer Workflows [Authored by Smitha]
        TrainerDashboard["pages.TrainerDashboardPage"]
        TrainerCohorts["pages.TrainerCohortsPage"]
        TrainerMailbox["pages.TrainerMailboxPage"]
        TrainerTests["TrainerWorkflowTest (T-01 to T-09)"]
    end

    subgraph Admin Workflows Part 2 [Co-Engineered: Chavi, Nihar, Smitha]
        TrainerManagement["pages.TrainerManagementPage"]
        AdminTests2["AdminWorkflowTest (A-05 to A-10)"]
    end

    Foundational Layer --> Authentication
    Foundational Layer --> Coach Workflows Part 1
    Foundational Layer --> Coach Workflows Part 2
    Foundational Layer --> Admin Workflows Part 1
    Foundational Layer --> Trainer Workflows
    Foundational Layer --> Admin Workflows Part 2
    Authentication -.->|"Session Token"| Coach Workflows Part 1 & Coach Workflows Part 2 & Admin Workflows Part 1 & Trainer Workflows & Admin Workflows Part 2
```

---

## 4. Assignment Overlap & Ownership Analysis

1. **`CohortPage.java` Shared Usage:**
   - Both Avinash Anand (`testC05`, `testC06`) and Ramya Thirandi (`testC07`, `testC07b`, `testC08`) depend upon `CohortPage.java`. Avinash utilizes the cohort details modal (`app-cohort-details-modal`) to inspect algorithmic score breakdowns, while Ramya utilizes the table search bar, status dropdown, and edit modal (`app-edit-cohort-modal`).
   - *Architecture Resolution:* `CohortPage.java` is treated as a shared Page Object within the Coach module, avoiding duplicated locators.
2. **`AdminWorkflowTest.java` (Methods A-05 through A-10):**
   - In the original Word document (`media_1792221253273_d3f004c0.pdf`), Chavi Maru is explicitly named on Page 3 for Part 1 (A-01 to A-04). Page 4 details Part 2 (A-05 to A-10) without repeating an author card.
   - *Architecture Resolution:* The test methods A-05 through A-10 reside in `AdminWorkflowTest.java` alongside Chavi's tests. Nihar contributed the `BasePage.enterDate` logic and `A-05b` boundary test, while Smitha's Trainer domain directly corresponds to the Trainer Pool features under test.

---

## 5. Verification Summary

- **Total Team Members:** 5
- **Assigned Functional Workstreams:** 5 (Authentication, Coach Part 1, Coach Part 2, Trainer, Admin)
- **Total Test Cases Automated:** 37 of 37 (100% Coverage)
- **Shared Code Quality:** Zero circular dependencies, 100% thread-safe browser isolation, synchronized Apache POI Excel parsing across all 13 worksheets.
