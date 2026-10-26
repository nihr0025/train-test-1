package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CoachDashboardPage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.ExcelUtils;
import utils.LoggerUtils;

import java.util.Map;

/**
 * LoginTest covering authentication scenarios for Coach, Trainer, and Admin,
 * driven by Excel sheet 'LoginData'.
 */
public class LoginTest extends BaseTest {

    @DataProvider(name = "loginExcelData")
    public Object[][] getLoginData() {
        String path = ConfigReader.getProperty("testdata.excel.path", "src/test/resources/testdata.xlsx");
        return ExcelUtils.getSheetData(path, "LoginData");
    }

    @Test(description = "Verify authentication using Excel data-driven inputs", dataProvider = "loginExcelData")
    public void testLoginWithExcelData(Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");
        String expectedOutput = data.get("Expected output");

        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginPageLoaded(), "Login page should be loaded");

        loginPage.login(username, password);

        if ("Valid".equalsIgnoreCase(expectedOutput)) {
            // Should redirect to role dashboard (url contains /admin, /coach, or /trainer)
            boolean redirected = driver.getCurrentUrl().contains("/coach") ||
                    driver.getCurrentUrl().contains("/trainer") ||
                    driver.getCurrentUrl().contains("/admin");
            Assert.assertTrue(redirected, "Valid login for " + username + " should redirect to dashboard. Current URL: " + driver.getCurrentUrl());
        } else {
            // Should display error message
            Assert.assertTrue(loginPage.isErrorMessageDisplayed() || driver.getCurrentUrl().contains("/login"),
                    "Invalid login for " + username + " should stay on login or display error.");
        }
    }

    @Test(description = "Verify Coach login with standard credentials")
    public void testCoachValidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.getProperty("coach.username"), ConfigReader.getProperty("coach.password"));

        CoachDashboardPage dashboardPage = new CoachDashboardPage(driver);
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Coach dashboard should be loaded upon successful login");
    }
}
