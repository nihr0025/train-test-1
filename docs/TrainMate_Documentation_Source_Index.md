# TrainMate Automation Framework — Source and Evidence Index

**Project Name:** TrainMate — Cohort & Trainer Automatic Allocation System  
**Document Version:** 1.0.0 — Final  
**Date:** October 2026  

---

## 1. Primary Source Documents & Artifacts

| Source File Reference | Location / Identifier | Description & Verified Contents |
| :--- | :--- | :--- |
| **`media_1792221253273_d3f004c0.pdf`** | Artifact directory (`.user_uploaded/media_1792221253273_d3f004c0.pdf`) | **Original Pod Work Allocation and Workflow Test Cases Document:**<br>• Page 1: IntelliJ IDEA project structure screenshot showing package layout.<br>• Page 2: Trainer Workflows (T-01 to T-08) test steps and expected results.<br>• Page 3: T-09 and Section 4: *Admin Workflows - Part 1 (System Oversight & Cohorts)*, Author: **Chavi Maru** (Emp ID: 2521741), TC A-01 to A-04.<br>• Page 4: Admin Workflows Part 2 (A-05 to A-10).<br>• Page 5: Document Title *Workflow-Testcases*, Pod: *Innovex* / *Selenials*, Credentials, and Section 1: *Coach Workflows - Part 1 (Access & Cohort Creation)*, Author: **Avinash Anand** (Emp ID: 2521743), TC C-01 to C-06.<br>• Page 6: Section 2: *Coach Workflows - Part 2 (Cohort Management & Batch Upload)*, Author: **Ramya Thirandi** (Emp ID: 2521745), TC C-07 to C-10. |
| **`media_1792221253297_8caaf7a2.pdf`** | Artifact directory (`.user_uploaded/media_1792221253297_8caaf7a2.pdf`) | **Master Excel Test Data Workbook (`testdata.xlsx`) Specification:**<br>• Page 1: `CohortStatusFilter_By_Trainer`<br>• Page 2: `CohortSLfilter_By_Admin`<br>• Page 3: `CohortStatusfilter_By_Admin`<br>• Page 4: `Cohort_Search_By_Admin`<br>• Page 5: `Cohort_or_Skill_SearchByTrainer`<br>• Page 6: `CohortStatusfilter_By_Coach`<br>• Page 7: `Cohort_Editing_By_Coach`<br>• Page 8: `Cohort_Search_By_Coach`<br>• Page 9: `Trainer_Search_By_Admin`<br>• Page 10: `Cohort_Creation`<br>• Page 11: `TrainerFilterByStatus_By_Admin`<br>• Page 12: `TrainerRegistration_By_Admin`<br>• Page 13: `LoginData` |
| **Git Commit Log** | Workspace `.git/logs/HEAD` | **Repository Ownership & Commit History:**<br>• Commit hash `5231585fdca6d11f82003fa35226f8c6ad6e5a1f`<br>• Author: `Nihar Ranjan Sahu <niharsahu00252024@gmail.com>`<br>• Commit message: `commit (initial): Add TrainMate Selenium automation framework` |
| **Active Codebase** | `C:\Users\nihar\ideaprojects\trainmate-test-1` | Complete Java 17 + Selenium 4.21 + TestNG 7.10 + Maven workspace. |
| **Mirror Codebase** | `C:\Users\nihar\ideaprojects\trainmate-test` | Synchronized backup workspace. |

---

## 2. Test Data Worksheets & Column Mappings

All worksheets reside in `src/test/resources/testdata.xlsx`:

| Worksheet Name | Referenced Test Cases | Relevant Columns | Source PDF Page |
| :--- | :--- | :--- | :--- |
| **`LoginData`** | `L-01` | `username`, `password`, `Expected output` | PDF 2, Page 13 |
| **`Cohort_Creation`** | `C-03`, `C-03b`, `C-04` | `Cohort Code`, `Trainees Count`, `Required Skills`, `Service Line`, `Stream`, `Start Date`, `End Date`, `Industry Vertical`, `Training Location`, `Expected Output` | PDF 2, Page 10 |
| **`Cohort_Editing_By_Coach`** | `C-08` | `Cohort Code`, `Trainees Count`, `Required Skills`, `Service Line`, `Stream`, `Start Date`, `End Date`, `Training Location`, `Expected Output` | PDF 2, Page 7 |
| **`Cohort_Search_By_Coach`** | `C-07` | `CohortCode_Or_Skill_Or_Trainer`, `Expected Output` | PDF 2, Page 8 |
| **`CohortStatusfilter_By_Coach`** | `C-07b` | `Status`, `Expected Output` | PDF 2, Page 6 |
| **`Cohort_or_Skill_SearchByTrainer`** | `T-06` | `CohortCode_or_Skill`, `Expected Output` | PDF 2, Page 5 |
| **`CohortStatusFilter_By_Trainer`** | `T-07` | `Status`, `Expected Output` | PDF 2, Page 1 |
| **`Cohort_Search_By_Admin`** | `A-03` | `Search by skill/name/service line`, `Expected Output` | PDF 2, Page 4 |
| **`CohortStatusfilter_By_Admin`** | `A-03c` | `Status`, `Expected Output` | PDF 2, Page 3 |
| **`CohortSLfilter_By_Admin`** | `A-03b` | `Service Line`, `Expected Output` | PDF 2, Page 2 |
| **`Trainer_Search_By_Admin`** | `A-07` | `Search by skill/name/service line`, `Expected Output` | PDF 2, Page 9 |
| **`TrainerFilterByStatus_By_Admin`** | `A-07b` | `Status`, `Expected Output` | PDF 2, Page 11 |
| **`TrainerRegistration_By_Admin`** | `A-05`, `A-05b`, `A-06` | `Full Name`, `Email`, `Skill Set`, `Experience`, `Available from`, `Available Till`, `Max Workload`, `Expected output` | PDF 2, Page 12 |

---

## 3. Java Source Code Evidence Index

### 3.1 Test Classes (`src/test/java/test/`)
- **`LoginTest.java`**:
  - Path: `src/test/java/test/LoginTest.java`
  - Methods: `testLoginWithExcelData` (L-01), `testCoachValidLogin` (L-02)
- **`CoachWorkflowTest.java`**:
  - Path: `src/test/java/test/CoachWorkflowTest.java`
  - Methods: `testC01_SignInAndAccessDashboard` (C-01), `testC02_AccessControlValidation` (C-02), `testC03_CohortFormValidationMandatoryAndDates` (C-03), `testC03_CohortFormValidationExcelDriven` (C-03b), `testC04_CreateCohortWithValidDetails` (C-04), `testC05_AutomatedTrainerAllocationCheck` (C-05), `testC06_AllocationRuleEnforcementOnNoMatch` (C-06), `testC07_SearchAndFilterCohortList` (C-07), `testC07b_CohortStatusFilter` (C-07b), `testC08_CohortEditingAndReallocation` (C-08), `testC09_BatchUploadValidation` (C-09), `testC10_BatchUploadValidCohortWorkbook` (C-10)
- **`TrainerWorkflowTest.java`**:
  - Path: `src/test/java/test/TrainerWorkflowTest.java`
  - Methods: `testT01_TrainerLoginAndRoleAccess` (T-01), `testT02_TrainerUnauthorizedRouteCheck` (T-02), `testT03_TrainerEmptyStateDashboard` (T-03), `testT04_TrainerMetricsAfterAssignment` (T-04), `testT05_ViewAssignedCohortDetails` (T-05), `testT06_SearchAssignedCohortSchedule` (T-06), `testT07_FilterCohortsByStatus` (T-07), `testT08_MailboxAssignmentNotification` (T-08), `testT09_RealTimeUpdatesOnReassignment` (T-09)
- **`AdminWorkflowTest.java`**:
  - Path: `src/test/java/test/AdminWorkflowTest.java`
  - Methods: `testA01_AdminSignInAndSystemOversight` (A-01), `testA02_DashboardDataIntegrityCheck` (A-02), `testA03_GlobalCohortSearchAndFilter` (A-03), `testA03b_GlobalCohortServiceLineFilter` (A-03b), `testA03c_GlobalCohortStatusFilter` (A-03c), `testA04_ExportAllocationReport` (A-04), `testA05_TrainerRegistrationValidation` (A-05), `testA05b_TrainerRegistrationDirectValidation` (A-05b), `testA06_RegisterNewTrainer` (A-06), `testA07_TrainerPoolFilterAndSearch` (A-07), `testA07b_TrainerPoolFilterByStatus` (A-07b), `testA08_EditTrainerWorkloadAndSkills` (A-08), `testA09_AdminManualReassignmentAndWorkloadSync` (A-09), `testA10_SystemWideStatusUpdateSync` (A-10)

### 3.2 Page Object Model Classes (`src/main/java/pages/`)
- `LoginPage.java`: Universal login handling and asynchronous authentication wait.
- `CoachDashboardPage.java`: Coach dashboard stats, modal launch, and cohort form inputs.
- `CohortPage.java`: Coach My Cohorts, search, status filtering, edit modal, and 100-point score inspection.
- `BatchUploadPage.java`: Coach batch Excel upload tab, file input, and bulk allocation.
- `TrainerDashboardPage.java`: Trainer dashboard metrics and unauthorized tab checks.
- `TrainerCohortsPage.java`: Trainer schedule, status filter, and cohort details modal.
- `TrainerMailboxPage.java`: Trainer allocation notifications and mark all as read.
- `AdminDashboardPage.java`: Admin dashboard metric cards and navigation links.
- `AdminCohortsPage.java`: Global cohort repository, search, service line/status filter, and reassignment modal.
- `TrainerManagementPage.java`: Admin trainer pool, search, status filter, registration modal, and workload edit.

### 3.3 Core Framework & Utility Classes
- `base/BasePage.java`: Core Selenium interaction engine, explicit wait handling, `enterDate()` HTML5 date engine, and `captureDiagnostics()` failure reporting.
- `base/BaseTest.java`: TestNG `@BeforeMethod` and `@AfterMethod` lifecycle.
- `listeners/TestListener.java`: ExtentReports 5.1 HTML generation and automatic failure screenshot binding.
- `utils/DriverFactory.java`: ThreadLocal browser lifecycle management.
- `utils/ConfigReader.java`: Central configuration reader with CLI overrides.
- `utils/ExcelUtils.java`: Apache POI Excel dynamic parser, date column detector, serial converter, and validator.
- `utils/ExcelDataInitializer.java`: Self-healing Excel workbook template generator.
- `utils/ScreenshotUtils.java`: Timestamped PNG failure screenshot capture.
- `utils/LoggerUtils.java`: Log4j2 credential-safe logger.

---

## 4. Configuration and Build Files

- `pom.xml`: Maven build, Java 17 compiler compliance, and dependency management.
- `src/test/resources/testng.xml`: TestNG suite definition containing all 4 test suites.
- `src/test/resources/config.properties`: Central framework properties (`baseUrl`, `browser=chrome`, `headless=false`, timeouts, test accounts).
- `src/test/resources/log4j2.xml`: Log4j2 appenders routing to console and `logs/trainmate-test.log`.
- `README.md`: Framework setup and execution guide.

---

## 5. Verification Methodology

Every finding and claim documented across the individual reports, framework architecture, and traceability matrix was validated against:
1. Direct textual analysis of the uploaded PDF specifications (`media_1792221253273_d3f004c0.pdf` and `media_1792221253297_8caaf7a2.pdf`).
2. Exact source code verification of methods, annotations, and locators in `src/main/java` and `src/test/java`.
3. Strict inspection of Git metadata in `.git/logs/HEAD`.
4. Verification that no fictitious test methods, authors, or pass rates were introduced.
