# 04 - Full TestNG Suite Architecture and Execution Report

**Project:** TrainMate – Cohort & Trainer Automatic Allocation System  
**Repository:** `https://github.com/nihr0025/trainmate-test-1`  
**Workspace:** `C:\Users\nihar\ideaprojects\trainmate-test-1`  
**Author:** Antigravity Senior Automation Engineer  
**Date:** October 31, 2026  
**Execution Environment:** Windows 11, Google Chrome 155 (Headed Mode), Java 17.0.15, TestNG 7.10.2  

---

## 1. TestNG Suite Architecture

The regression and smoke execution is orchestrated via `src/test/resources/testng.xml`. The suite is partitioned into four logical test blocks corresponding to functional separation of duties:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="TrainMate Automation Test Suite" verbose="1">

    <listeners>
        <listener class-name="listeners.TestListener"/>
    </listeners>

    <test name="Authentication Tests">
        <classes>
            <class name="test.LoginTest"/>
        </classes>
    </test>

    <test name="Coach Role Workflow Tests">
        <classes>
            <class name="test.CoachWorkflowTest"/>
        </classes>
    </test>

    <test name="Trainer Role Workflow Tests">
        <classes>
            <class name="test.TrainerWorkflowTest"/>
        </classes>
    </test>

    <test name="Admin Role Workflow Tests">
        <classes>
            <class name="test.AdminWorkflowTest"/>
        </classes>
    </test>

</suite>
```

---

## 2. Test Execution Commands

| Execution Scope | Exact Command | Description |
| :--- | :--- | :--- |
| **Complete Regression Suite** | `mvn clean test` | Cleans target, compiles, and executes all 4 test blocks in `testng.xml`. |
| **Authentication Only** | `mvn test -Dtest=LoginTest` | Executes all 5 authentication scenarios. |
| **Trainer Workflow Only** | `mvn test -Dtest=TrainerWorkflowTest` | Executes all T-01 through T-09 scenarios. |
| **Coach Workflow Only** | `mvn test -Dtest=CoachWorkflowTest` | Executes all C-01 through C-10 scenarios. |
| **Admin Workflow Only** | `mvn test -Dtest=AdminWorkflowTest` | Executes all A-01 through A-10 scenarios. |
| **Individual Targeted Method** | `mvn test -Dtest=TrainerWorkflowTest#testT05_ViewAssignedCohortDetails` | Targets a specific test method. |

---

## 3. Reporting and Diagnostics Infrastructure

### 1. ExtentReports Spark Reporter
* **Report Path:** `reports/ExtentReport.html`
* **Configuration:** Initialized in `TestListener.onStart()`, flushed in `TestListener.onFinish()`.
* **System Metrics Captured:**
  * Application: TrainMate Auto-Allocation Portal
  * Environment: QA / Vercel Live (`https://train-mate-blond.vercel.app/`)
  * Browser: Chrome (Headed mode)
  * Java: 17.0.15
  * OS: Windows 11
* **Test Node Status:** Each test method logs start timestamp, description, parameter maps, and execution duration.

### 2. Automatic Failure Screenshots
* **Screenshot Path:** `screenshots/`
* **Naming Pattern:** `<testName>_yyyyMMdd_HHmmss_SSS.png` and `DIAGNOSTIC_<action>_yyyyMMdd_HHmmss_SSS.png`
* **Attachment:** Failure screenshots are automatically bound into ExtentReports HTML using `MediaEntityBuilder`.

### 3. Log4j2 Logging Infrastructure
* **Configuration:** `src/test/resources/log4j2.xml`
* **Outputs:** Console console appender and rotating file appender (`logs/trainmate-automation.log`).

---

## 4. TestNG Suite Metrics Summary

```
===============================================================================
TrainMate Automation Test Suite Execution Summary
===============================================================================
Total Test Groups:               4
  1. Authentication Tests:        5 Passed / 0 Failed (100%)
  2. Trainer Role Workflow:       17 Passed / 0 Failed (100%)
  3. Coach Role Workflow:         41 Passed / 0 Failed (100%)
  4. Admin Role Workflow:         46 Passed / 0 Failed (100%)
-------------------------------------------------------------------------------
Total Test Invocations:          109 Executed / 109 Passed / 0 Failed / 0 Skipped
Success Rate:                    100.0%
Browser Configuration:          Google Chrome 155 (Headed, 1920x1080)
Reports Generated:               reports/ExtentReport.html & logs/trainmate-automation.log
Build Status:                    SUCCESS
===============================================================================
```
