package no.jenjon13.eeexam.selenium.test;

import no.jenjon13.eeexam.ejb.EventEJB;
import no.jenjon13.eeexam.selenium.conf.Config;
import no.jenjon13.eeexam.selenium.conf.JBossUtil;
import no.jenjon13.eeexam.selenium.pageobject.CreateEventPageObject;
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

import javax.inject.Inject;
import java.io.File;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class WebPageIT {
    private static WebDriver driver;
    private HomePageObject homePageObject;
    private LoginPageObject loginPageObject;
    private NewUserPageObject newUserPageObject;
    private CreateEventPageObject createEventPageObject;

    @Inject
    private EventEJB eventEJB;

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
    public void setUp() throws Exception {
        assumeTrue(JBossUtil.isJBossUpAndRunning());

        homePageObject = new HomePageObject(driver);
        loginPageObject = new LoginPageObject(driver);
        newUserPageObject = new NewUserPageObject(driver);
        createEventPageObject = new CreateEventPageObject(driver);

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

        enterTextIntoField("WRONG USERNAME", "loginForm:txtUsername");
        enterTextIntoField("WRONG PASSWORD", "loginForm:txtPassword");

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

    @Test
    public void testLogin() throws Exception {
        String usernameAndPassword = createUser();

        homePageObject.clickLogoutButton();
        loginPageObject.navigateToLoginPage();

        enterTextIntoField(usernameAndPassword, "loginForm:txtUsername");
        enterTextIntoField(usernameAndPassword, "loginForm:txtPassword");

        By byBtnInitiateLogin = By.id("loginForm:btnInitiateLogin");
        WebElement loginBtnWebElement = driver.findElement(byBtnInitiateLogin);
        loginBtnWebElement.click();
        homePageObject.waitForPageToLoad();

        assertTrue(homePageObject.isAtHomePage());

        By byWelcomeMessage = By.id("welcomeMessage");
        String welcomeMessageText = driver.findElement(byWelcomeMessage).getText();
        assertTrue(welcomeMessageText.contains("Hi " + usernameAndPassword));

        homePageObject.clickLogoutButton();
    }

    private String createUser() {
        loginPageObject.navigateToLoginPage();
        loginPageObject.clickLoginFormCreateNewUser();
        return newUserPageObject.fillOutUserFormAndSubmit(true);
    }

    @Test
    public void testCreateOneEvent() throws Exception {
        createUser();
        final int amountOfDisplayedEvents = createEventPageObject.getAmountOfDisplayedEvents();
        createEvent("Norway");
        final int newAmountOfDisplayedEvents = createEventPageObject.getAmountOfDisplayedEvents();

        assertEquals(amountOfDisplayedEvents + 1, newAmountOfDisplayedEvents);
        assertTrue(0 < eventEJB.deleteAllEvents());
        homePageObject.clickLogoutButton();
    }

    private void createEvent(String country) {
        homePageObject.clickCreateEventButton();
        assertTrue(createEventPageObject.isOnEventPage());
        createEventPageObject.fillOutEventDataAndSubmit(country);
    }

    @Test
    public void testCreateEventInDifferentCountries() throws Exception {
        createUser();
        final int amountOfDisplayedEvents = createEventPageObject.getAmountOfDisplayedEvents();
        createEvent("Norway");
        createEvent("Sweden");

        final By byOnlyCurrentCountryCheckbox = By.id("dataForm:onlyCurrentCountry");
        final WebElement checkboxElement = driver.findElement(byOnlyCurrentCountryCheckbox);
        final boolean onlyShowCurrentCountryChecked = checkboxElement.isSelected();

        if (!onlyShowCurrentCountryChecked) {
            checkboxElement.click();
        }

        final int newAmountOfEvents = createEventPageObject.getAmountOfDisplayedEvents();
        assertEquals(amountOfDisplayedEvents + 1, newAmountOfEvents);

        checkboxElement.click();
        final int updatedAmountOfEvents = createEventPageObject.getAmountOfDisplayedEvents();
        assertEquals(amountOfDisplayedEvents + 2, updatedAmountOfEvents);

//        eventEJB.deleteAllEvents(); TODO
        homePageObject.clickLogoutButton();
    }

    private void enterTextIntoField(String textToEnter, String id) {
        By byTxtPassword = By.id(id);
        WebElement passwordWebElement = driver.findElement(byTxtPassword);
        passwordWebElement.clear();
        passwordWebElement.sendKeys(textToEnter);
    }
}
