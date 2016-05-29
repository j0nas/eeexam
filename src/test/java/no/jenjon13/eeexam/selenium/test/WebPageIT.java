package no.jenjon13.eeexam.selenium.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RemoteMappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import no.jenjon13.eeexam.ejb.EventEJB;
import no.jenjon13.eeexam.selenium.conf.Config;
import no.jenjon13.eeexam.selenium.conf.JBossUtil;
import no.jenjon13.eeexam.selenium.pageobject.CreateEventPageObject;
import no.jenjon13.eeexam.selenium.pageobject.HomePageObject;
import no.jenjon13.eeexam.selenium.pageobject.LoginPageObject;
import no.jenjon13.eeexam.selenium.pageobject.NewUserPageObject;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class WebPageIT {
    private static WebDriver driver;
    private static WireMockServer wireMockServer;

    private HomePageObject homePageObject;
    private LoginPageObject loginPageObject;
    private NewUserPageObject newUserPageObject;
    private CreateEventPageObject createEventPageObject;

    @Inject
    private EventEJB eventEJB;
    private boolean toggled;


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

        final ConsoleNotifier consoleNotifier = new ConsoleNotifier(true);
        final WireMockConfiguration configuration = WireMockConfiguration
                .wireMockConfig()
                .port(8099)
                .notifier(consoleNotifier);
        wireMockServer = new WireMockServer(configuration);
        wireMockServer.start();

        final InputStream countriesJson = WebPageIT.class.getResourceAsStream("/countries.json");
        final String body = new Scanner(countriesJson, "UTF-8").useDelimiter("\\A").next();

        final UrlMatchingStrategy matching = urlMatching("/rest/v1/all");
        final ResponseDefinitionBuilder responseBuilder = aResponse()
                .withHeader("Content-Type", "application/json;charset=utf-8")
                .withBody(body);
        final RemoteMappingBuilder mappingBuilder = get(matching).willReturn(responseBuilder);
        wireMockServer.stubFor(mappingBuilder);
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
        wireMockServer.stop();
    }

    @Before
    public void setUp() throws Exception {
        toggled = false;
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

        homePageObject.toIndexPage();
    }

    @Test
    public void testCreateValidUser() throws Exception {
        homePageObject.toIndexPage();
        homePageObject.clickLogoutButton();

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
        homePageObject.toIndexPage();

        homePageObject.clickLogoutButton();
        homePageObject.waitForPageToLoad();
        homePageObject.toIndexPage();

        loginPageObject.navigateToLoginPage();
        loginPageObject.clickLoginFormCreateNewUser();
        return newUserPageObject.fillOutUserFormAndSubmit(true);
    }

    @Test
    public void testCreateOneEvent() throws Exception {
        createUser();
        final int amountOfDisplayedEvents = homePageObject.getAmountOfDisplayedEvents();
        createEvent("Norway");
        final int newAmountOfDisplayedEvents = homePageObject.getAmountOfDisplayedEvents();

        assertEquals(amountOfDisplayedEvents + 1, newAmountOfDisplayedEvents);
        homePageObject.clickLogoutButton();
    }

    private String createEvent(String country) {
        homePageObject.clickCreateEventButton();
        assertTrue(createEventPageObject.isOnEventPage());
        return createEventPageObject.fillOutEventDataAndSubmit(country);
    }

    @Test
    public void testCreateEventInDifferentCountries() throws Exception {
        createUser();
        final int amountOfDisplayedEvents = homePageObject.getAmountOfDisplayedEvents();
        createEvent("Norway");
        createEvent("Sweden");

        setOnlyCurrentCountryCheckBox(false);
        Thread.sleep(500);
        final int newAmountOfEvents = homePageObject.getAmountOfDisplayedEvents();
        assertEquals(amountOfDisplayedEvents + 2, newAmountOfEvents);

        setOnlyCurrentCountryCheckBox(true);
        Thread.sleep(500);
        final int updatedAmountOfEvents = homePageObject.getAmountOfDisplayedEvents();
        assertEquals(amountOfDisplayedEvents + 1, updatedAmountOfEvents);

        homePageObject.clickLogoutButton();
    }

    private void setOnlyCurrentCountryCheckBox(boolean checked) {
        final By byOnlyCurrentCountryCheckbox = By.id("dataForm:onlyCurrentCountry");
        final List<WebElement> checkboxElements = driver.findElements(byOnlyCurrentCountryCheckbox);
        if (checkboxElements.isEmpty()) {
            return;
        }

        if (!toggled) {
            checked = !checked;
            toggled = true;
        }

        final WebElement checkboxElement = checkboxElements.get(0);

        final boolean onlyShowCurrentCountryChecked = checkboxElement.isSelected();

        if (onlyShowCurrentCountryChecked && !checked) {
            checkboxElement.click();
        }

        if (!onlyShowCurrentCountryChecked && checked) {
            checkboxElement.click();
        }
    }

    @Test
    public void testCreateEventsFromDifferentUsers() throws Exception {
        createUser();

        Thread.sleep(500);
        setOnlyCurrentCountryCheckBox(false);
        Thread.sleep(500);
        final int initialAmountOfDisplayedEvents = homePageObject.getAmountOfDisplayedEvents();

        createEvent("Norway");
        homePageObject.clickLogoutButton();

        toggled = false;
        createUser();
        createEvent("Sweden");
        Thread.sleep(500);
        setOnlyCurrentCountryCheckBox(false);
        Thread.sleep(500);

        final int newAmountOfDisplayedEvents = homePageObject.getAmountOfDisplayedEvents();
        assertEquals(initialAmountOfDisplayedEvents + 2, newAmountOfDisplayedEvents);
        homePageObject.clickLogoutButton();
    }

    // mvn clean verify -Dit.test=WebPageIT#testCreateUserWithFakeCountry test
    @Ignore
    public void testCreateUserWithFakeCountry() throws Exception {
        createUser();

        Thread.sleep(500);
        setOnlyCurrentCountryCheckBox(false);
        Thread.sleep(500);
        final int initialAmountOfDisplayedEvents = homePageObject.getAmountOfDisplayedEvents();

        boolean exceptionCaught = false;
        try {
            createEvent("wrong");
        } catch (NoSuchElementException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        homePageObject.toIndexPage();
        Thread.sleep(500);
        assertEquals(initialAmountOfDisplayedEvents, homePageObject.getAmountOfDisplayedEvents());
        createEvent("Fake Land");

        Thread.sleep(500);
        assertEquals(initialAmountOfDisplayedEvents + 1, homePageObject.getAmountOfDisplayedEvents());

        homePageObject.clickLogoutButton();
    }

    private void enterTextIntoField(String textToEnter, String id) {
        By byTxtPassword = By.id(id);
        WebElement passwordWebElement = driver.findElement(byTxtPassword);
        passwordWebElement.clear();
        passwordWebElement.sendKeys(textToEnter);
    }
}
