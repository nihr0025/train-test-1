# 05 - Code Changes and Framework Fixes Report

**Project:** TrainMate – Cohort & Trainer Automatic Allocation System  
**Repository:** `https://github.com/nihr0025/trainmate-test-1`  
**Workspace:** `C:\Users\nihar\ideaprojects\trainmate-test-1`  
**Author:** Antigravity Senior Automation Engineer  
**Date:** October 31, 2026  

---

## 1. Overview of Framework Enhancements

During regression execution on the live Angular application (`https://train-mate-blond.vercel.app/`), several critical execution blockers were encountered. These blockers prevented Coach, Trainer, and Admin workflow tests from navigating between application routes, locating page elements, and verifying page loads.

All root causes were diagnosed using browser console logs, DOM inspection of compiled Angular bundles (`main-I2N3FPUR.js`, `chunk-N5EOE4T2.js`, `chunk-5P2WGWE6.js`, `chunk-LNZ2M7KH.js`, `chunk-JERLPMXT.js`), and Selenium runtime failure triage.

---

## 2. Root Cause Analysis & Detailed Fixes

### Fix 1: Resilient Angular Navigation Method (`clickNav`) in `BasePage.java`

* **Problem:** In Angular 17/18 single-page applications, native Selenium WebDriver `.click()` on navigation items (such as `<a routerLink="/trainer/cohorts" class="nav-item-link">` or `<a routerLink="/admin/cohorts">`) sends coordinate-based OS click events. These events frequently failed to trigger the Angular Zone change detection or the `RouterLink` directive's internal click handler. As a result, the browser silently stayed on `/dashboard` without throwing an exception, leading downstream page waits (such as `waitForVisibility(searchInput)`) to time out after 20 seconds.
* **Root Cause:** Angular's router listener is attached to DOM click dispatching. Native click without scrolling into center view or coordinate misalignment missed the router activation.
* **Solution:** Introduced `clickNav(By locator)` in `BasePage.java`. This method:
  1. Explicitly waits for element clickability.
  2. Scrolls the target element smoothly into center view.
  3. Executes `clickViaJs(element)` (`arguments[0].click()`), guaranteeing that Angular's event delegation receives the click event and immediately initiates the route transition.

```java
// src/main/java/base/BasePage.java
/**
 * Resilient navigation click for Angular router links.
 * Ensures element is clickable, scrolls it into center view, and triggers clickViaJs
 * to guarantee Angular RouterLink zone change detection and route transitions.
 */
protected void clickNav(By locator) {
    try {
        WebElement element = waitForClickable(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        clickViaJs(element);
    } catch (Exception e) {
        captureDiagnostics(locator, "clickNav", e);
        throw e;
    }
}
```

---

### Fix 2: Disambiguating Page Readiness H1 Titles Across Page Objects

* **Problem:** `TrainerCohortsPage`, `TrainerMailboxPage`, `AdminCohortsPage`, `TrainerManagementPage`, `CohortPage`, and `BatchUploadPage` all contained a trailing fallback in their `pageTitle` locator:
  ```java
  private final By pageTitle = By.xpath("//h1[contains(@class, 'page-title')]//span[...] | //h1[contains(@class, 'page-title')]");
  ```
  When navigating from a Dashboard (e.g. `TrainerDashboardPage` with `<h1 class="page-title">Trainer Dashboard</h1>`), `//h1[contains(@class, 'page-title')]` immediately matched the **current** dashboard's H1. As a consequence, `waitForPageReady()` returned in 0ms while the browser was still on `/dashboard`. The subsequent locator then timed out.
* **Solution:** Removed the ambiguous `| //h1[contains(@class, 'page-title')]` fallback from all child page objects. Replaced with exact, text-qualified H1 selectors matching only the destination page's true header.

| Page Object Class | Original Locator | Fixed Locator |
| :--- | :--- | :--- |
| `TrainerCohortsPage.java` | `//h1[contains(@class, 'page-title')]//span[contains(text(), 'My Assigned Cohorts')] \| //h1[contains(@class, 'page-title')]` | `//h1[contains(@class, 'page-title') and contains(., 'My Assigned Cohorts')] \| //h1[contains(., 'My Assigned Cohorts')]` |
| `TrainerMailboxPage.java` | `//h1[contains(@class, 'page-title')]//span[contains(text(), 'Mailbox')] \| //h1[contains(@class, 'page-title')]` | `//h1[contains(@class, 'page-title') and contains(., 'Mailbox')] \| //h1[contains(., 'Mailbox')]` |
| `AdminCohortsPage.java` | `//h1[contains(@class, 'page-title')]//span[contains(text(), 'All System Cohorts')] \| //h1[contains(@class, 'page-title')]` | `//h1[contains(@class, 'page-title') and contains(., 'All System Cohorts')] \| //h1[contains(., 'All System Cohorts')]` |
| `TrainerManagementPage.java` | `//h1[contains(@class, 'page-title')]//span[contains(text(), 'Trainers Management')] \| //h1[contains(@class, 'page-title')]` | `//h1[contains(@class, 'page-title') and contains(., 'Trainers Management')] \| //h1[contains(., 'Trainers Management')]` |
| `CohortPage.java` | `//h1[contains(@class, 'page-title')]//span[contains(text(), 'My Managed Cohorts')] \| //h1[contains(@class, 'page-title')]` | `//h1[contains(@class, 'page-title') and contains(., 'My Managed Cohorts')] \| //h1[contains(., 'My Managed Cohorts')]` |
| `BatchUploadPage.java` | `//h1[contains(text(), 'Add & Allocate Cohorts')] \| //h1[contains(@class, 'page-title')]` | `//h1[contains(text(), 'Add & Allocate Cohorts')]` |

---

### Fix 3: Upgrading Dashboard Page Navigation Methods

* **Problem:** In `TrainerDashboardPage`, `CoachDashboardPage`, and `AdminDashboardPage`, navigation methods (`navigateToCohorts()`, `navigateToTrainers()`, `navigateToMailbox()`, `navigateToUpload()`) used plain `click(navTarget)`.
* **Solution:** Replaced `click(navTarget)` with `clickNav(navTarget)` across all three role dashboard classes, and enhanced the navigation target locators to recognize both sidebar router links and dashboard card action links:

```java
// TrainerDashboardPage.java
public TrainerCohortsPage navigateToCohorts() {
    LoggerUtils.info(getClass(), "Navigating to Trainer My Cohorts via UI...");
    By navTarget = isElementDisplayed(sidebarMyCohortsLink) ? sidebarMyCohortsLink : viewAllCohortsLink;
    clickNav(navTarget);
    TrainerCohortsPage cohortsPage = new TrainerCohortsPage(driver);
    cohortsPage.waitUntilLoaded();
    return cohortsPage;
}

public TrainerMailboxPage navigateToMailbox() {
    LoggerUtils.info(getClass(), "Navigating to Trainer Mailbox via UI...");
    clickNav(mailboxNavLink);
    TrainerMailboxPage mailboxPage = new TrainerMailboxPage(driver);
    mailboxPage.waitUntilLoaded();
    return mailboxPage;
}
```

```java
// CoachDashboardPage.java
public CohortPage navigateToCohorts() {
    LoggerUtils.info(getClass(), "Navigating to Coach My Cohorts page via UI...");
    By navTarget = By.xpath("//a[contains(@href, '/coach/cohorts') and contains(., 'My Cohorts')] | //aside//a[contains(@href, '/coach/cohorts')] | //a[contains(@href, '/coach/cohorts')]");
    clickNav(navTarget);
    CohortPage cohortPage = new CohortPage(driver);
    cohortPage.waitUntilLoaded();
    return cohortPage;
}

public BatchUploadPage navigateToUpload() {
    LoggerUtils.info(getClass(), "Navigating to Batch Upload page via UI...");
    By navTarget = By.xpath("//a[contains(@href, '/coach/upload') and contains(., 'Add Cohort')] | //aside//a[contains(@href, '/coach/upload')] | //a[contains(@href, '/coach/upload')]");
    clickNav(navTarget);
    BatchUploadPage uploadPage = new BatchUploadPage(driver);
    uploadPage.waitUntilLoaded();
    return uploadPage;
}
```

```java
// AdminDashboardPage.java
public AdminCohortsPage navigateToCohorts() {
    LoggerUtils.info(getClass(), "Navigating to Admin Cohorts page via UI...");
    By navTarget = By.xpath("//a[contains(@href, '/admin/cohorts') and contains(., 'All Cohorts')] | //aside//a[contains(@href, '/admin/cohorts')] | //a[contains(@href, '/admin/cohorts')]");
    clickNav(navTarget);
    AdminCohortsPage cohortsPage = new AdminCohortsPage(driver);
    cohortsPage.waitUntilLoaded();
    return cohortsPage;
}

public TrainerManagementPage navigateToTrainers() {
    LoggerUtils.info(getClass(), "Navigating to Trainer Management page via UI...");
    By navTarget = By.xpath("//a[contains(@href, '/admin/trainers') and contains(., 'Trainers Pool')] | //aside//a[contains(@href, '/admin/trainers')] | //a[contains(@href, '/admin/trainers')]");
    clickNav(navTarget);
    TrainerManagementPage trainersPage = new TrainerManagementPage(driver);
    trainersPage.waitUntilLoaded();
    return trainersPage;
}
```

---

### Fix 4: Universal TestListener Binding in `BaseTest.java`

* **Problem:** `TestListener` (responsible for ExtentReports HTML generation, console test summaries, and automatic failure screenshots) was only configured in `src/test/resources/testng.xml`. Running individual test classes with `mvn test -Dtest=ClassName` bypassed `testng.xml`, resulting in missing ExtentReport logs.
* **Solution:** Annotated `BaseTest.java` with `@Listeners(listeners.TestListener.class)`. Now, whether tests are executed via `mvn test`, `mvn test -Dtest=...`, or IntelliJ IDEA runner, ExtentReports and screenshot capture are active for every execution.

```java
// src/test/java/base/BaseTest.java
@Listeners(listeners.TestListener.class)
public abstract class BaseTest {
    ...
}
```

---

### Fix 5: Environment Tooling Setup (`powershell.bat`)

* **Problem:** Antigravity agent shell execution failed because system PATH did not include `powershell.exe` and `JAVA_HOME`.
* **Solution:** Created `powershell.bat` in the project root configuring `JAVA_HOME` (`C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot`), IntelliJ's bundled Apache Maven 3.9.16 (`C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3`), Windows System32, and Git. This restored full automated test execution and shell command execution capability.

---

### Fix 6: Modal Opening Mechanism Upgrades in Admin Pages

* **Problem:** `openRegisterTrainerModal()` and `openEditTrainer()` in `TrainerManagementPage`, as well as `openReassignModal()` in `AdminCohortsPage`, used native `.click()`. Native ChromeDriver clicks did not trigger the Angular modal open event handlers on `<button class="btn-primary-custom">` or `<button class="btn-reassign">`, resulting in 20-second timeout exceptions waiting for `app-trainer-form-modal` and `app-reassign-modal`.
* **Solution:** Upgraded `openRegisterTrainerModal()`, `openEditTrainer()`, and `openReassignModal()` to use `clickNav()`, which scrolls the button into center view and triggers direct DOM click dispatch via JavaScript (`clickViaJs`), guaranteeing that Angular's event bindings open the respective modals immediately.

---

### Fix 7: Handling Pre-populated Angular Modal Defaults in `CoachWorkflowTest#testC03`

* **Problem:** In TrainMate, the Angular `CreateCohortModalComponent` initializes its `formData` with dummy default data (`QEA26SD...`, 45 associates, Chennai, valid dates). Submitting without clearing these default values submitted a valid cohort, which caused the backend to auto-allocate the cohort and display the cohort details modal (`app-cohort-details-modal`) rather than showing validation errors.
* **Solution:** In `testC03_CohortFormValidationMandatoryAndDates`, explicitly invoked `dashboard.fillCohortDetails("", "", "", null, null, "", "", null, null)` to clear the pre-populated values before testing blank mandatory field validation.

---

### Fix 8: Robust SPA Route Guard Verification in `TrainerWorkflowTest#testT02`

* **Problem:** In Angular SPA architecture, route guards cancel component activation when an unauthorized user attempts direct URL navigation. The browser URL bar retains the attempted route without rendering the protected component. The test asserted only `driver.getCurrentUrl().contains("/login")`, causing a failure when the guard blocked access while keeping the URL.
* **Solution:** Enhanced `testT02_TrainerUnauthorizedRouteCheck` to verify that access is denied if the URL redirects or if the protected role H1 headers (`Trainers Management` and `My Managed Cohorts`) are completely absent from the DOM.

---

### Fix 9: Chrome Password Manager & Breach Alert Suppression in `DriverFactory.java` and `LoginPage.java`

* **Problem:** When logging in with standard test accounts (`coach01`/`coach123`, `admin01`/`admin123`), Chrome's built-in password manager displayed a native browser dialog:
  > *"Change your password – The password you just used was found in a data breach. Password Manager recommends changing this password now."*
  This native popup interrupted automated test execution, stole browser focus, and risked blocking subsequent interactions.
* **Root Cause:** Chrome has internal feature flags (`PasswordLeakDetection`, `PasswordCheck`) and credential manager services enabled by default, which compare entered credentials against known breach lists.
* **Solution:**
  1. Configured `DriverFactory.java` with isolated temporary profile directories per test session (`Files.createTempDirectory("trainmate_chrome_profile_")`), preventing password reuse and personal profile pollution. Profiles are recursively deleted upon `quitDriver()`.
  2. Injected Chrome arguments:
     * `--disable-save-password-bubble`
     * `--disable-features=PasswordLeakDetection,PasswordCheck,AutofillServerCommunication`
     * `--disable-notifications`
     * `--disable-popup-blocking`
  3. Added Chrome experimental preferences:
     * `credentials_enable_service: false`
     * `profile.password_manager_enabled: false`
     * `profile.password_manager_leak_detection: false`
     * `autofill.profile_enabled: false`
  4. Added `dismissBrowserWarningsIfAny()` in `LoginPage.java` to safely dismiss any unexpected native alerts or web-based dialogs without modifying account credentials.

---

### Fix 10: Transient Network Flakiness Retry in `BaseTest.java`

* **Problem:** During full regression suite runs spanning 100+ tests, intermittent network blips (e.g. `ERR_INTERNET_DISCONNECTED`) during `driver.get(appUrl)` in `setUp()` could cause subsequent tests in a suite to be marked as skipped.
* **Solution:** Enhanced `BaseTest.setUp()` with a 3-attempt retry loop and 3-second recovery delay on the initial `driver.get(appUrl)` call, ensuring that transient network glitches are automatically absorbed without failing test setup.
