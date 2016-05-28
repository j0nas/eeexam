package no.jenjon13.eeexam.selenium.test;

import no.jenjon13.eeexam.selenium.conf.Config;
import no.jenjon13.eeexam.selenium.conf.JBossUtil;
import no.jenjon13.eeexam.selenium.pageobject.HomePageObject;
import no.jenjon13.eeexam.selenium.pageobject.LoginPageObject;
import no.jenjon13.eeexam.selenium.pageobject.NewUserPageObject;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class WebPageIT {
    private static WebDriver driver;
    private HomePageObject homePageObject;
    private LoginPageObject loginPageObject;
    private NewUserPageObject newUserPageObject;

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
        driver.quit();
    }

    @Before
    public void startFromInitialPage() {
        assumeTrue(JBossUtil.isJBossUpAndRunning());

        homePageObject = new HomePageObject(driver);
        loginPageObject = new LoginPageObject(driver);
        newUserPageObject = new NewUserPageObject(driver);

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
        loginPageObject.navigateToLoginPage();
        assertTrue(loginPageObject.isOnLoginPage());
    }

    @Test
    public void testLoginWrongUser() {
        loginPageObject.navigateToLoginPage();
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
        assertTrue(loginPageObject.isOnLoginPage());
    }


    @Test
    public void testCreateUserFailDueToPasswordMismatch() throws Exception {
        loginPageObject.navigateToLoginPage();
        loginPageObject.clickLoginFormCreateNewUser();

        String currentUrl = driver.getCurrentUrl();
        newUserPageObject.fillOutUserFormAndSubmit(false);
        String newUrl = driver.getCurrentUrl();
        Assert.assertTrue(newUrl.equals(currentUrl));
    }

    @Test
    public void testCreateValidUser() throws Exception {
        loginPageObject.navigateToLoginPage();
        String currentUrl = driver.getCurrentUrl();
        loginPageObject.clickLoginFormCreateNewUser();
        newUserPageObject.fillOutUserFormAndSubmit(true);

        String newUrl = driver.getCurrentUrl();
        assertFalse(currentUrl.equals(newUrl));

        By byWelcomeMessage = By.id("welcomeMessage");
        String welcomeMessageText = driver.findElement(byWelcomeMessage).getText();
        assertTrue(welcomeMessageText.contains("Hi "));

        homePageObject.clickLogoutButton();

        By byNotLoggedInMessage = By.id("notLoggedInMessage");
        WebElement notLoggedInWebElement = driver.findElement(byNotLoggedInMessage);
        String notLoggedInText = notLoggedInWebElement.getText();
        assertTrue(notLoggedInText.contains("Not logged in"));
    }

    @Ignore
    public void testLogin() throws Exception {
        loginPageObject.navigateToLoginPage();
        loginPageObject.clickLoginFormCreateNewUser();
    }
}
