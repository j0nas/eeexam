package no.jenjon13.eeexam.selenium.test;

import no.jenjon13.eeexam.selenium.conf.Config;
import no.jenjon13.eeexam.selenium.conf.JBossUtil;
import no.jenjon13.eeexam.selenium.pageobject.HomePageObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class WebPageIT {
    private static WebDriver driver;
    private HomePageObject homePageObject;

    @BeforeClass
    public static void init() throws InterruptedException {
        final File pathToBinary = new File(Config.FIREFOX_BINARY_PATH);
        final FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
        final FirefoxProfile firefoxProfile = new FirefoxProfile();

        DesiredCapabilities desiredCapabilities = DesiredCapabilities.htmlUnit();
        desiredCapabilities.setBrowserName("firefox");
        desiredCapabilities.setJavascriptEnabled(true);
        driver = new FirefoxDriver(ffBinary, firefoxProfile, desiredCapabilities);

        int i = 0;
        while (i++ < 30 && !JBossUtil.isJBossUpAndRunning()) {
            Thread.sleep(1_000);
        }
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
    }

    @Before
    public void startFromInitialPage() {
        assumeTrue(JBossUtil.isJBossUpAndRunning());

        homePageObject = new HomePageObject(driver);
        homePageObject.toIndexPage();
        assertTrue(homePageObject.isAtHomePage());
    }

    @Test
    public void testHomePage() {
        final By elementIdentifier = By.id("pagetitle");
        WebElement text = driver.findElement(elementIdentifier);
        String value = text.getText();
        assertEquals("Event List Home Page", value);
    }

    @Test
    public void testLoginLink() {
        navigateToLoginPage();
        assertTrue(isOnLoginPage());
    }

    private boolean isOnLoginPage() {
        By byPageTitle = By.id("pagetitle");
        WebElement element = driver.findElement(byPageTitle);
        String elementText = element.getText();
        return elementText.equals("Login");
    }

    private void navigateToLoginPage() {
        By byBtnLoginId = By.id("btnLogin");
        WebElement webElement = driver.findElement(byBtnLoginId);
        webElement.click();
        homePageObject.waitForPageToLoad();
    }

    @Test
    public void testLoginWrongUser() {
        navigateToLoginPage();
        By byTxtUsername = By.id("loginForm:txtUsername");
        WebElement usernameWebElement = driver.findElement(byTxtUsername);
        usernameWebElement.sendKeys("WRONG USERNAME");

        By byTxtPassword = By.id("loginForm:txtPassword");
        WebElement passwordWebElement = driver.findElement(byTxtPassword);
        passwordWebElement.sendKeys("WRONG PASSWORD");

        By byBtnInitiateLogin = By.id("loginForm:btnInitiateLogin");
        WebElement loginBtnWebElement = driver.findElement(byBtnInitiateLogin);
        loginBtnWebElement.click();

        homePageObject.waitForPageToLoad();
        assertTrue(isOnLoginPage());
    }
}
