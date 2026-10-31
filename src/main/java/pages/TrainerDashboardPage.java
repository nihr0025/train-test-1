package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.LoggerUtils;

import java.util.List;

/**
 * TrainerDashboardPage POM for Trainer portal (/trainer/dashboard).
 * Includes reusable page readiness checks and UI navigation methods that wait for destination pages.
 */
public class TrainerDashboardPage extends BasePage {

    private final By pageTitle = By.xpath("//h1[contains(@class, 'page-title') and contains(text(), 'Trainer')] | //h1[contains(@class, 'page-title')]");
    private final By activeCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Active Cohorts']]//div[contains(@class, 'stat-value')]");
    private final By completedCohortsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Completed Cohorts']]//div[contains(@class, 'stat-value')]");
    private final By mailsStat = By.xpath("//div[contains(@class, 'stat-card')][.//div[text()='Mails & Alerts']]//div[contains(@class, 'stat-value')]");
    private final By assignedCohortsTableRows = By.cssSelector("table tbody tr");
    private final By emptyStateRow = By.xpath("//table//tr//td[contains(., 'No cohorts have been assigned') or contains(., 'No cohorts')]");
    private final By viewAllCohortsLink = By.xpath("//a[contains(@href, '/trainer/cohorts') and contains(text(), 'View All')]");
    private final By sidebarMyCohortsLink = By.xpath("//a[contains(@href, '/trainer/cohorts') and contains(., 'My Cohorts')] | //aside//a[contains(., 'My Cohorts')]");
    private final By mailboxNavLink = By.xpath("//a[contains(@href, '/trainer/mail') and contains(., 'Mailbox')] | //aside//a[contains(., 'Mailbox')]");
    private final By coachTabsHidden = By.xpath("//a[contains(@href, '/coach')]");
    private final By adminTabsHidden = By.xpath("//a[contains(@href, '/admin')]");

    public TrainerDashboardPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        waitForPageReady(pageTitle, "TrainerDashboardPage");
    }

    public boolean isLoaded() {
        try {
            waitForVisibility(pageTitle, 10);
            return getCurrentUrl().contains("/trainer") && isElementDisplayed(pageTitle);
        } catch (Exception e) {
            return false;
        }
    }

    public int getActiveCohortsCount() {
        try {
            return Integer.parseInt(getText(activeCohortsStat).replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getCompletedCohortsCount() {
        try {
            return Integer.parseInt(getText(completedCohortsStat).replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getMailsCount() {
        try {
            return Integer.parseInt(getText(mailsStat).replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isCoachOrAdminTabsVisible() {
        return !findElements(coachTabsHidden).isEmpty() || !findElements(adminTabsHidden).isEmpty();
    }

    public boolean isEmptyStateDisplayed() {
        return isElementDisplayed(emptyStateRow);
    }

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
}
