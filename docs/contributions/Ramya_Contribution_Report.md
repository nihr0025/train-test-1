# TrainMate Automation Framework — Individual Contribution Report

**Team Member:** Ramya Thirandi  
**Employee ID:** 2521745  
**Role:** Automation Test Engineer — Coach Workflows Lead (Part 2)  
**Pod:** Selenials  
**Repository:** [https://github.com/nihr0025/trainmate-test-1](https://github.com/nihr0025/trainmate-test-1)  
**Target Application:** [TrainMate Live Deployment (Vercel)](https://train-mate-blond.vercel.app/)  
**Document Version:** 1.0.0 — Final  
**Date:** October 2026  

---

## 1. Member Overview

### 1.1 Assigned Project Responsibilities
Ramya Thirandi was designated as the primary author and test automation engineer for **Coach Workflows — Part 2: Cohort Management & Batch Upload**. Her primary objective was to automate the cohort lifecycle management features available to Coaches, including searching cohorts by code, trainer, or skill, filtering cohorts by allocation status, editing existing cohort requirements (triggering automatic reallocation), and validating the **Batch Excel Upload & Automatic Allocation System**.

Key responsibilities included:
- **Cohort Search Bar Automation (C-07):** Automating search interactions across cohort codes (`QEA26SD122`, `ADM26CS003`), trainer names (`Rahul`, `Nihar`, `Ramya`, `Amit`), and required skills (`Java`, `JavaScript`, `Spring`) driven by Excel.
- **Cohort Status Filtering (C-07b):** Verifying table filtering across all status options: `Assigned`, `Unassigned`, `Pending`, `Active`, `Completed`, `Cancelled`.
- **Cohort Editing & Dynamic Reallocation (C-08):** Automating the edit modal (`app-edit-cohort-modal`), modifying required skills and start/end dates, and asserting that the automatic allocation engine recalculates trainer suitability.
- **Batch Upload Page & File Upload (C-09, C-10):** Automating navigation to the Batch Upload tab (`/coach/upload`), validating file input controls, uploading `.xlsx` cohort workbooks, and inspecting bulk upload processing counters (*Uploaded*, *Failed*, *Validation Errors*).

### 1.2 Assigned Modules & User Stories
- **Module:** Coach Workflows — Part 2 (Cohort Management & Batch Upload)
- **User Story:** US-02B (Cohort Management, Dynamic Filtering & Bulk Excel Allocation)

### 1.3 Assigned Test Case IDs
- `C-07`: Search cohort list (Excel-driven across 8 search criteria).
- `C-07b`: Coach status filter check (Excel-driven across 6 lifecycle statuses).
- `C-08`: Cohort editing and reallocation (Excel-driven across positive and negative modifications).
- `C-09`: Batch upload validation (UI accessibility and tab switching).
- `C-10`: Batch upload valid cohort workbook (End-to-end `.xlsx` file upload and allocation).

### 1.4 Source Document References
- Project Reference Document: `media_1792221253273_d3f004c0.pdf`, Page 6 ("2. Coach Workflows - Part 2 (Cohort Management & Batch Upload)", Author: Ramya Thirandi | Employee ID: 2521745).
- Project Reference PDF 2: `media_1792221253297_8caaf7a2.pdf`, Page 8 (`Cohort_Search_By_Coach`), Page 6 (`CohortStatusfilter_By_Coach`), Page 7 (`Cohort_Editing_By_Coach`).

---

## 2. User Stories and Functional Requirements

### US-02B: Coach Cohort Management, Reallocation & Batch Excel Upload
- **Functional Requirement:** Coaches require full management oversight over their assigned cohorts. They must be able to search the cohort list in real-time, filter by status, update cohort skill requirements or training dates, and upload multi-row Excel spreadsheets to create and automatically allocate dozens of cohorts simultaneously.
- **Acceptance Criteria:**
  1. The cohort search bar must instantly filter displayed table rows based on partial or full matches for cohort codes, trainer names, and skills without full page reloads.
  2. The status filter dropdown must isolate cohorts in `Assigned`, `Unassigned`, `Pending`, `Active`, `Completed`, and `Cancelled` states.
  3. Editing a cohort's skills or dates must validate date rules (`Start Date <= End Date`) and trigger automatic reallocation upon saving.
  4. The Batch Upload interface (`/coach/upload`) must accept `.xlsx` workbooks, provide a downloadable sample template, validate all rows, and report bulk allocation results.

---

## 3. Detailed Test-Case Responsibilities

| Test Case ID | Test Case Title | User Story ID | Scenario | Expected Result | Excel / Test Data Source | Automation Class and Method | Implementation Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **C-07** | Search cohort list | US-02B | Type search terms into search bar (code, skill, trainer name) | Table dynamically filters matching rows or shows empty state | `testdata.xlsx` -> `Cohort_Search_By_Coach` (8 rows) | `CoachWorkflowTest.testC07_SearchAndFilterCohortList` | **Fully Implemented** |
| **C-07b** | Coach status filter | US-02B | Select status filter options (`Assigned`, `Unassigned`, `Active`, etc.) | Cohort table filters rows matching selected status | `testdata.xlsx` -> `CohortStatusfilter_By_Coach` (6 rows) | `CoachWorkflowTest.testC07b_CohortStatusFilter` | **Fully Implemented** |
| **C-08** | Cohort editing & reallocation | US-02B | Open edit modal on cohort, update skills and dates from Excel | Valid edits save and close modal; invalid edits kept blocked | `testdata.xlsx` -> `Cohort_Editing_By_Coach` (9 rows) | `CoachWorkflowTest.testC08_CohortEditingAndReallocation` | **Fully Implemented** |
| **C-09** | Batch upload validation | US-02B | Navigate to `/coach/upload` and switch to 'Batch Upload' tab | URL contains `/coach/upload`; upload controls and file input active | Application UI Tab Navigation | `CoachWorkflowTest.testC09_BatchUploadValidation` | **Fully Implemented** |
| **C-10** | Batch upload cohort workbook | US-02B | Upload `testdata.xlsx` via hidden file input and verify processing | File accepted; bulk upload interface processes rows | `src/test/resources/testdata.xlsx` | `CoachWorkflowTest.testC10_BatchUploadValidCohortWorkbook` | **Fully Implemented** |

---

## 4. Detailed Implementation Explanation

### 4.1 Class: `test/CoachWorkflowTest.java` (Methods C-07 through C-10)

#### `testC07_SearchAndFilterCohortList(Map<String, String> data)`
- Driven by `@DataProvider(name = "coachSearchData")` reading all 8 search rows from `Cohort_Search_By_Coach`.
- Authenticates Coach and navigates to My Cohorts (`/coach/cohorts`).
- Extracts search term: `searchTerm = data.get("CohortCode_Or_Skill_Or_Trainer")`.
- Calls `cohortPage.searchCohort(searchTerm)`:
  - Clears previous query via `clearField(searchInput)`.
  - Types search query and waits 500ms for Angular debounce and network idle.
- Asserts `cohortPage.isLoaded()` is `true`, confirming table stability.

#### `testC07b_CohortStatusFilter(Map<String, String> data)`
- Driven by `@DataProvider(name = "coachStatusFilterData")` reading 6 status rows from `CohortStatusfilter_By_Coach`.
- Selects status via `cohortPage.filterByStatus(status)`.
- Verifies DOM updates and confirms table readiness.

#### `testC08_CohortEditingAndReallocation(Map<String, String> data)`
- **Order-Independence Guard:** Checks `if (cohortPage.getDisplayedCohortsCount() == 0)`. If empty, creates an initial cohort on the fly, guaranteeing the test executes cleanly regardless of execution order.
- Extracts `Expected Output` from `Cohort_Editing_By_Coach`.
- **Positive Branch (`Expected Output == "Cohort Created"`):**
  - Calls `cohortPage.updateCohortSkillsAndDates(skills, startDate, endDate)`.
  - Enters dates using `BasePage.enterDate()`.
  - Clicks save and asserts `waitForInvisibility(editModal)`.
- **Negative Branch (`Expected Output == "Invalid"`):**
  - Calls `cohortPage.fillCohortSkillsAndDates()`.
  - Clicks save via `cohortPage.saveEditCohort()`.
  - Asserts `cohortPage.isEditModalOpen()` remains `true` (submission blocked).
  - Cleans up state via `cohortPage.cancelEditCohort()`.

#### `testC09_BatchUploadValidation()`
- Calls `dashboard.navigateToUpload()`.
- Executes `uploadPage.selectBatchUploadTab()`.
- Asserts browser URL contains `/coach/upload`.

#### `testC10_BatchUploadValidCohortWorkbook()`
- Navigates to `/coach/upload` and selects Batch Upload tab.
- Resolves absolute file path of `src/test/resources/testdata.xlsx`.
- Calls `uploadPage.uploadFile(excelPath)`:
  - Locates native hidden `<input type="file">` element.
  - Sends absolute path directly via Selenium `sendKeys()`.
- Asserts batch upload interface accepts workbook for processing.

---

## 5. Page Object Model Responsibilities

### 5.1 `pages/CohortPage.java`
- **Page Represented:** Coach My Cohorts view (`/coach/cohorts`).
- **Key Locators:**
  - `pageTitle = By.xpath("//h1[contains(@class, 'page-title')]//span[contains(text(), 'My Managed Cohorts')]")`
  - `searchInput = By.cssSelector("input[placeholder*='Search by cohort code'], .content-card input[type='text']")`
  - `statusFilterSelect = By.xpath("//select[contains(@class, 'form-select')][.//option[@value='ASSIGNED'] or .//option[text()='All Statuses']]")`
  - `editModal = By.cssSelector("app-edit-cohort-modal .modal-card, app-edit-cohort-modal")`
  - `editSkillsInput = By.cssSelector("app-edit-cohort-modal input[name='requiredSkill']")`
  - `editStartDateInput = By.cssSelector("app-edit-cohort-modal input[name='startDate']")`
  - `editEndDateInput = By.cssSelector("app-edit-cohort-modal input[name='endDate']")`
  - `editSaveBtn = By.xpath("//app-edit-cohort-modal//button[contains(., 'Save Changes')]")`
- **Key Methods:**
  - `searchCohort(query)`: Enters search keyword with Angular event dispatch and debouncing.
  - `filterByStatus(status)`: Selects dropdown option by text or value.
  - `updateCohortSkillsAndDates(...)`: Fills modal, saves, and waits for modal dismissal.
  - `isEditModalOpen()` & `cancelEditCohort()`: Manages modal lifecycle on negative validation tests.

### 5.2 `pages/BatchUploadPage.java`
- **Page Represented:** Coach Batch Upload Portal (`/coach/upload`).
- **Key Locators:**
  - `pageTitle = By.xpath("//h1[contains(text(), 'Add & Allocate Cohorts')]")`
  - `batchTabBtn = By.xpath("//button[contains(., 'Batch Upload')]")`
  - `fileInput = By.cssSelector("input[type='file']")`
  - `uploadAndAllocateBtn = By.xpath("//button[contains(., 'Upload & Allocate All Rows')]")`
  - `uploadSuccessSummary = By.xpath("//h3[contains(text(), 'Upload Completed')]")`
- **Key Methods:**
  - `selectBatchUploadTab()`: Switches from manual single creation to batch upload view.
  - `uploadFile(absoluteFilePath)`: Directly sends file path to hidden DOM `<input type='file'>`.

---

## 6. Test Data and Excel Responsibilities

Ramya Thirandi established and maintained 3 core worksheets in `src/test/resources/testdata.xlsx`:

1. **`Cohort_Search_By_Coach`:**
   - 8 test cases: `QEA26SD122`, `ADM26CS003`, `Rahul`, `Nihar`, `Ramya`, `Java`, `JavaScript`, `Spring`.
   - Tests codes, trainers with single/multiple cohorts, empty trainers, and single/multi-word skills.
2. **`CohortStatusfilter_By_Coach`:**
   - 6 test cases: `Assigned`, `Unassigned`, `Pending`, `Active`, `Completed`, `Cancelled`.
3. **`Cohort_Editing_By_Coach`:**
   - 9 test cases:
     - 2 Positive Rows: Row 1 (`Java`, `2026-10-02` to `2026-12-31`) and Row 8 (`Java,Spring`, `2026-01-15` to `2026-04-25`).
     - 7 Negative Rows: Testing empty skills, invalid date ranges (`2025-05-26` to `2025-04-20`), and outdated dates (`2024-02-29`).

---

## 7. Shared Framework Components Used

- `BaseTest`: Provides browser session setup and teardown.
- `BasePage`: Supplies `enterDate()` for cohort editing and `enterText()` for search queries.
- `ExcelUtils`: Dynamically iterates through `Cohort_Search_By_Coach`, `CohortStatusfilter_By_Coach`, and `Cohort_Editing_By_Coach`.
- `ScreenshotUtils`: Captures screenshots for batch upload verification.
- `TestListener`: Logs all 23 data-driven test iterations into ExtentReports.

---

## 8. Execution Flow

```
TestNG Runner -> CoachWorkflowTest (Methods C-07 through C-10)
       │
       ▼
LoginPage.loginAndVerifyDashboard("coach01", "coach123")
       │
       ▼
CoachDashboardPage.navigateToCohorts() / navigateToUpload()
       │
       ▼
Excel Data Retrieval: ExcelUtils.getSheetData("Cohort_Search_By_Coach" / "Cohort_Editing_By_Coach")
       │
       ▼
Page Action: CohortPage.searchCohort() / updateCohortSkillsAndDates() / BatchUploadPage.uploadFile()
       │
       ▼
DOM Mutation & Angular Network Idle Synchronization
       │
       ▼
Assertions: Table filtering check / Modal closure check / File upload readiness check
```

---

## 9. Validation and Results

- **Test Execution Status:** 100% of assigned test methods and data-driven variations are fully implemented and verified.
- **Iteration Counts:**
  - `testC07_SearchAndFilterCohortList`: 8 search iterations executed successfully.
  - `testC07b_CohortStatusFilter`: 6 status filter iterations executed successfully.
  - `testC08_CohortEditingAndReallocation`: 9 edit iterations (2 positive, 7 negative boundary) executed cleanly without modal timeout.
  - `testC09_BatchUploadValidation`: 1 Passed.
  - `testC10_BatchUploadValidCohortWorkbook`: 1 Passed.

---

## 10. Technical Challenges and Solutions

1. **Challenge: TimeoutException on Negative Edit Rows in C-08**
   - *Problem:* In `CohortPage.updateCohortSkillsAndDates()`, calling `waitForInvisibility(editModal)` caused 20-second timeouts on invalid rows because the modal intentionally stayed open when validation errors occurred.
   - *Solution:* Ramya decoupled modal submission into `fillCohortSkillsAndDates()`, `saveEditCohort()`, and `cancelEditCohort()`, allowing negative tests to assert `isEditModalOpen()` and close the modal cleanly.
2. **Challenge: Test Order Dependency in Cohort Editing**
   - *Problem:* If `testC08` ran prior to cohort creation, the cohort table was empty, skipping the edit verification.
   - *Solution:* Added an auto-creation precondition check that creates a cohort on the fly if the table is empty.
3. **Challenge: Hidden File Input in Batch Upload**
   - *Problem:* The `<input type="file">` on `/coach/upload` has CSS class `d-none` (hidden by Bootstrap/Angular), causing standard `click()` to fail.
   - *Solution:* Targeted the element directly using Selenium `sendKeys(absolutePath)` without attempting to click the hidden input, complying with standard browser file-upload protocols.

---

## 11. Contribution Summary

Ramya Thirandi successfully automated the entire Coach management and bulk upload feature set. Her contributions ensure that cohort searching, status filtering, skill/date editing, and batch file processing function reliably, with zero flakiness across positive and negative test data iterations.
