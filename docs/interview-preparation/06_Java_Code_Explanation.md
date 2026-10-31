# Document 06 — Java Code Explanation & Core Concepts

**Project Name:** TrainMate — Cohort & Trainer Automatic Allocation System  
**Document Purpose:** In-depth explanation of actual Java implementations across all framework layers, method-by-method breakdowns, and core OOP principles applied in the code.

---

## 1. Key Java Concepts Applied in This Project

Before looking at individual classes, let's understand the core Java concepts powering our framework:

| Java Concept | How It Is Used in Our Framework | Concrete File Example |
| :--- | :--- | :--- |
| **Encapsulation** | Web element locators are marked `private final By` inside Page Objects; public methods expose business actions. | `LoginPage.java`, `CohortPage.java` |
| **Inheritance** | All page objects extend `BasePage`; all test classes extend `BaseTest`. Eliminates boilerplate code. | `BasePage -> CoachDashboardPage`, `BaseTest -> CoachWorkflowTest` |
| **Polymorphism (Method Overriding)** | `TestListener` overrides methods from TestNG's `ITestListener` (`onTestStart`, `onTestFailure`). | `listeners.TestListener.java` |
| **Abstraction** | `BasePage` and `BaseTest` are abstract classes defining foundational behavior without being instantiated directly. | `base.BasePage`, `base.BaseTest` |
| **Generics & Collections** | `List<WebElement>`, `Map<String, String>`, and `List<Map<String, String>>` manage dynamic web rows and Excel records. | `ExcelUtils.java`, `AdminWorkflowTest.java` |
| **Java Streams & Lambdas** | Used to filter Excel rows by criteria (e.g. `allRows.stream().filter(...).toList()`) without messy nested loops. | `AdminWorkflowTest.java` |
| **ThreadLocal Concurrency** | `ThreadLocal<WebDriver>` ensures each test thread retains its own isolated browser session. | `utils.DriverFactory.java` |
| **Exception Handling** | `try-catch-finally` blocks catch `TimeoutException`, `StaleElementReferenceException`, and `NoSuchElementException`. | `base.BasePage.java`, `utils.ExcelUtils.java` |

---

## 2. In-Depth Breakdown: Core Framework Classes

---

### 2.1 `base.BasePage.java`

#### Class Purpose
`BasePage` is the foundational parent class for all page objects. It initializes the `WebDriver` and `WebDriverWait` instances and houses all synchronized browser interactions (clicks, text input, date entry, dropdowns, and failure diagnostics).

#### Key Fields
```java
protected WebDriver driver;           // The active browser instance passed from DriverFactory
protected WebDriverWait wait;         // Explicit wait engine with configurable timeout
protected int explicitTimeout;        // Timeout in seconds (default 20s from config.properties)
```

#### Detailed Method Breakdown

---

#### Method 1: `waitForVisibility(By locator, int timeoutSeconds)`
- **Purpose:** Pauses execution until the element specified by the locator is visible in the browser DOM.
- **Input:** `By locator` (element locator), `int timeoutSeconds` (maximum wait time).
- **Output:** `WebElement` (the visible element).
- **Step-by-Step Explanation:**
  1. Instantiates a `WebDriverWait` with the provided duration.
  2. Calls `customWait.until(ExpectedConditions.visibilityOfElementLocated(locator))`.
  3. If found within the timeout, returns the `WebElement`.
  4. If the timer expires, catches `TimeoutException`, calls `captureDiagnostics()`, and re-throws a detailed `TimeoutException` with DOM diagnostics.
- **Example Usage in Code:**
  ```java
  WebElement searchBox = waitForVisibility(By.cssSelector("input[placeholder*='Search']"));
  ```
- **Interview Explanation:**
  > *"Instead of using raw `driver.findElement()` which can fail instantly if an element is animating, our `waitForVisibility()` waits up to 20 seconds. If it times out, it captures an exhaustive diagnostic report including current URL, DOM presence, and active overlays."*

---

#### Method 2: `enterDate(By locator, String dateStr)`
- **Purpose:** Injects a normalized date into HTML5 date fields without causing 5-to-8 digit year appending bugs.
- **Input:** `By locator` (date input element), `String dateStr` (raw date text, e.g., `2026-11-15`).
- **Output:** `void`.
- **Step-by-Step Explanation:**
  1. Waits for element visibility and checks `element.isEnabled()`.
  2. If the string is null/blank, clears the value via JavaScript.
  3. Calls `normalizeDateString(dateStr)` to ensure format `yyyy-MM-dd` and a 4-digit year between 1900 and 2100.
  4. Executes JavaScript to set `element.value = formattedDate`.
  5. Dispatches synthetic JavaScript `input`, `change`, and `blur` events with `bubbles: true` so Angular's reactive form detects the new value.
  6. Checks `element.getAttribute("value")` to verify that the value matches; if not, falls back to keyboard simulation (`Keys.CONTROL + a`, `BACK_SPACE`, `sendKeys`).
  7. Asserts DOM reflection, throwing an `AssertionError` if the date was not accepted.
- **Example Usage in Code:**
  ```java
  // In CoachDashboardPage.java: Setting cohort start and end dates
  enterDate(startDateInput, "2026-11-01");
  enterDate(endDateInput, "2026-12-15");
  ```
- **Interview Explanation:**
  > *"Standard Selenium `sendKeys()` on HTML5 date pickers often duplicates year digits in Chrome. Our `enterDate()` method uses JavaScript value injection followed by event dispatching, guaranteeing that Angular updates its model without date corruption."*

---

#### Method 3: `click(By locator)`
- **Purpose:** Clicks an element with automatic stale element retry and intercepted-click JavaScript fallback.
- **Input:** `By locator`.
- **Output:** `void`.
- **Step-by-Step Explanation:**
  1. Calls `waitForClickable(locator).click()`.
  2. If a `StaleElementReferenceException` occurs (DOM re-rendered), catches it, logs a warning, and retries the click once.
  3. If an `ElementClickInterceptedException` occurs (a loading spinner or overlay blocked the click), scrolls the element into the center of the viewport via `scrollIntoView()` and triggers a direct JavaScript click `clickViaJs()`.
- **Interview Explanation:**
  > *"In modern single-page applications, UI elements often get covered by temporary transparent overlays or re-render during network calls. Our `click()` method handles stale element retries and falls back to JavaScript clicks automatically."*

---

### 2.2 `utils.DriverFactory.java`

#### Class Purpose
`DriverFactory` manages the creation, configuration, and teardown of `WebDriver` instances using `ThreadLocal` storage.

#### Key Implementation
```java
public class DriverFactory {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver initDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        boolean headless = ConfigReader.isHeadless();

        WebDriver driver;
        switch (browser) {
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.addArguments("--headless=new");
                driver = new EdgeDriver(edgeOptions);
                break;
            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--window-size=1920,1080");
                driver = new ChromeDriver(chromeOptions);
                break;
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driverThreadLocal.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}
```

#### Line-by-Line Highlights
- **`ThreadLocal<WebDriver> driverThreadLocal`**: Ensures thread safety across parallel runs.
- **`chromeOptions.addArguments("--window-size=1920,1080")`**: Guarantees standard desktop resolution so responsive UI elements don't collapse into mobile hamburger menus.
- **`driverThreadLocal.remove()`**: Crucial in `quitDriver()` to prevent memory leaks in thread pools.

---

### 2.3 `utils.ExcelUtils.java`

#### Class Purpose
`ExcelUtils` provides dynamic parsing of Microsoft Excel spreadsheets (`.xlsx`) using Apache POI. It converts sheet rows into data-provider friendly collections and handles Excel dates.

#### Detailed Method: `getSheetData(String filePath, String sheetName)`
```java
public static Object[][] getSheetData(String filePath, String sheetName) {
    List<Map<String, String>> list = getSheetDataAsMap(filePath, sheetName);
    Object[][] data = new Object[list.size()][1];
    for (int i = 0; i < list.size(); i++) {
        data[i][0] = list.get(i);
    }
    return data;
}
```
- **How it works:**
  1. Opens the workbook via `WorkbookFactory.create(new FileInputStream(filePath))`.
  2. Extracts column names from Row 0 to use as Map keys.
  3. Iterates from Row 1 to `sheet.getLastRowNum()`.
  4. For each cell, evaluates `cell.getCellType()`. For string cells, trims text; for numeric date cells, formats them using `DATE_FORMAT`.
  5. Wraps each row in a `Map<String, String>` and returns an `Object[][]` array where each element contains one row Map.
- **Interview Explanation:**
  > *"Returning `Object[][]` containing `Map<String, String>` allows our TestNG test methods to access data by column header names (e.g. `data.get("Cohort Code")`) rather than brittle zero-based array indexes."*

---

## 3. In-Depth Breakdown: Test Execution Classes

---

### 3.1 `test.CoachWorkflowTest.java`

#### Detailed Method: `testC04_CreateCohortWithValidDetails(Map<String, String> data)`
```java
@Test(description = "C-04: Create cohort with valid details (Excel-driven)", dataProvider = "cohortCreationValidData")
public void testC04_CreateCohortWithValidDetails(Map<String, String> data) {
    CoachDashboardPage dashboard = loginAsCoach();
    dashboard.openCreateCohortModal();

    String baseCode = data.get("Cohort Code");
    String uniqueCode = (baseCode != null && !baseCode.isEmpty() ? baseCode : "AUTO") + "_" + (System.currentTimeMillis() % 100000);
    dashboard.fillCohortDetails(
            uniqueCode,
            data.get("Trainees Count"),
            data.get("Required Skills"),
            data.get("Service Line"),
            data.get("Stream"),
            data.get("Start Date"),
            data.get("End Date"),
            data.get("Industry Vertical"),
            data.get("Training Location")
    );
    dashboard.submitCreateCohort();

    CohortPage cohortPage = dashboard.navigateToCohorts();
    cohortPage.searchCohort(uniqueCode);
    Assert.assertTrue(cohortPage.getDisplayedCohortsCount() >= 1,
            "Newly created cohort " + uniqueCode + " should appear in My Cohorts table");
}
```

#### Step-by-Step Code Walkthrough
1. **`loginAsCoach()`**: Encapsulates navigation to `/login`, entering credentials from `config.properties`, clicking submit, and returning the initialized `CoachDashboardPage`.
2. **`openCreateCohortModal()`**: Clicks the 'Create Cohort' button, waiting for the modal dialog to be visible.
3. **`System.currentTimeMillis() % 100000`**: Generates a dynamic 5-digit number appended to the base code (e.g., `ADM26CS001_84921`). This prevents primary key duplication errors during repetitive test runs.
4. **`fillCohortDetails(...)`**: Calls the page object method to enter all cohort fields.
5. **`submitCreateCohort()`**: Clicks the modal submit button and waits for modal invisibility.
6. **`cohortPage.searchCohort(uniqueCode)`**: Types our new code into the search box.
7. **`Assert.assertTrue(...)`**: Verifies that at least 1 matching row appears in the table.
