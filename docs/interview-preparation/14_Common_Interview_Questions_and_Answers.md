# Document 14 — Comprehensive Interview Questions & Answers

**Project Name:** TrainMate — Cohort & Trainer Automatic Allocation System  
**Document Purpose:** 50+ categorized interview questions with concise spoken answers, detailed technical explanations, and concrete examples from our project.

---

## 1. Beginner Questions

### Q1: What is Selenium WebDriver?
- **Spoken Answer:** *"Selenium WebDriver is an open-source browser automation tool that allows us to control browsers programmatically by simulating real user actions like typing, clicking, and navigating."*
- **Project Example:** In `BasePage.java`, we use `driver.findElement(locator).click()` and `element.sendKeys(text)`.

### Q2: What is TestNG?
- **Spoken Answer:** *"TestNG is a testing framework for Java that organizes, controls, and executes our automated test suites using annotations, XML suite files, data providers, and assertion libraries."*
- **Project Example:** We annotate our test methods with `@Test(description = "...")` and configure execution suites in `src/test/resources/testng.xml`.

### Q3: What is the Page Object Model (POM)?
- **Spoken Answer:** *"POM is an architectural design pattern where every web page is represented as a Java class, keeping locators and user actions separate from test assertions for high maintainability."*
- **Project Example:** `LoginPage.java` contains locators for username, password, and submit, while `LoginTest.java` contains the assertions.

### Q4: What is the difference between an Assertion and a Page Action?
- **Spoken Answer:** *"A page action interacts with the browser (e.g. typing or clicking a button). An assertion verifies the outcome against expected criteria (e.g. checking that a table contains rows). Actions belong in Page Objects; assertions belong in Test classes."*
- **Project Example:** `cohortPage.searchCohort("Java")` is an action; `Assert.assertTrue(cohortPage.isLoaded())` is an assertion.

### Q5: What is an Explicit Wait?
- **Spoken Answer:** *"An explicit wait pauses test execution until a specific condition—such as an element becoming visible or clickable—is satisfied, polling the browser at regular intervals up to a maximum timeout."*
- **Project Example:** `new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.visibilityOfElementLocated(locator))`.

---

## 2. Intermediate Questions

### Q6: Can you explain your framework architecture?
- **Spoken Answer:** *"Our framework follows a 4-tier Page Object Model architecture built with Java 17, Selenium 4, TestNG, and Maven. Layer 1 is Configuration (`ConfigReader` and `DriverFactory` with ThreadLocal WebDriver). Layer 2 is the Test & Data Layer (TestNG test classes reading from Excel via Apache POI). Layer 3 is the Page Object Layer (`BasePage` providing bounded explicit waits and event dispatches). Layer 4 is Observability (TestNG `TestListener` generating ExtentReports with failure screenshots and Log4j2 logging)."*

### Q7: How does your framework read data from Excel?
- **Spoken Answer:** *"We use Apache POI in `ExcelUtils.java`. It opens `testdata.xlsx`, reads column headers in Row 0 as Map keys, and loops through data rows. It converts Excel dates and numeric serials into standard ISO `yyyy-MM-dd` strings. It feeds data into test methods as `Map<String, String>` using TestNG's `@DataProvider`."*

### Q8: How did you solve the HTML5 date entry bug in Chrome?
- **Spoken Answer:** *"In Chrome, standard Selenium `sendKeys()` on `<input type='date'>` fields appends digits to existing years, resulting in invalid 8-digit years like `20262026`. We built `BasePage.enterDate()` which validates a 4-digit year, clears the field, sets the value via JavaScript (`arguments[0].value = formattedDate`), and dispatches synthetic Angular `input`, `change`, and `blur` events so the reactive form detects the update."*

### Q9: What is the difference between `findElement()` and `findElements()`?
- **Spoken Answer:** *"`findElement()` returns the first matching `WebElement` in the DOM or throws `NoSuchElementException` if not found. `findElements()` returns a `List<WebElement>` containing all matching elements, or returns an empty list without throwing an exception if none match."*
- **Project Example:** In `BasePage.captureDiagnostics()`, we call `driver.findElements(locator)` to check whether an element exists in the DOM without failing the triage method.

### Q10: How do you handle Angular asynchronous updates in Selenium?
- **Spoken Answer:** *"In Angular applications, UI updates happen asynchronously through change detection. In `BasePage`, after typing or clearing fields, we execute JavaScript event dispatches (`new Event('input', { bubbles: true })` and `new Event('change')`). We also use explicit waits on expected DOM states rather than fixed sleeps."*

---

## 3. Advanced & Architectural Questions

### Q11: Why did you use `ThreadLocal<WebDriver>` in `DriverFactory`?
- **Spoken Answer:** *"`ThreadLocal` allocates an independent `WebDriver` instance for each thread. When tests run in parallel, Thread 1 and Thread 2 operate in completely separate Chrome processes, preventing race conditions or browser cross-contamination."*

### Q12: How do you prevent tests from depending on each other?
- **Spoken Answer:** *"Every test method is isolated. Each test uses TestNG's `@BeforeMethod` to launch a fresh browser session and authenticate independently. For positive cohort and trainer creation tests, we append dynamic timestamps (`System.currentTimeMillis()`) to base codes so that tests never fail due to duplicate primary key constraints in the database."*

### Q13: How did you test the 100-Point Allocation Algorithm?
- **Spoken Answer:** *"In test `C-05`, after a cohort is created and allocated, our automation navigates to the Coach Cohorts page, filters by `ASSIGNED`, and opens the details modal. The test asserts that an assigned trainer is displayed and verifies that the modal renders the **100-point score breakdown widget**, which shows the score distribution across Skill Match (40 pts), Availability (30 pts), Workload (20 pts), and Experience (10 pts)."*

### Q14: How do you automate file uploads without using OS-level dialogs?
- **Spoken Answer:** *"In test `C-10` on `/coach/upload`, instead of dealing with brittle OS window dialogs via AutoIT or Robot class, we locate the underlying HTML `<input type='file'>` element and transmit the absolute file path directly using `element.sendKeys(absoluteFilePath)`. This works reliably across Windows, Mac, and headless Linux CI/CD environments."*

### Q15: How would you debug a failing test in your framework?
- **Spoken Answer:** *"I follow a 3-step triage process: First, I open `reports/ExtentReport.html` to review the embedded failure screenshot and see what was on screen. Second, I inspect `logs/trainmate-test.log` to read the `captureDiagnostics()` report, which shows whether the element existed in the DOM, if an overlay blocked it, and what browser console errors occurred. Third, I check whether the application's HTML locator changed or if Excel test data was altered."*

---

## 4. Project-Specific Questions

### Q16: What happens in TrainMate when no trainer matches a cohort?
- **Answer:** *"The system marks the cohort status as `UNASSIGNED`. In test `C-06`, our automation filters by `UNASSIGNED`, opens the cohort details, and asserts that the trainer field explicitly displays 'Unassigned', confirming the algorithm does not force an invalid assignment."*

### Q17: How did you fix the locator mismatch on the Admin Cohorts search field?
- **Answer:** *"Initially, tests threw a `TimeoutException` looking for `input[placeholder*='Search code']` because the application placeholder text was 'Search cohorts'. In `AdminCohortsPage.java`, we implemented a resilient multi-strategy CSS selector: `input[placeholder*='Search'], input[placeholder*='search'], input[type='search'], input.form-control`, ensuring tests pass across different UI builds."*
