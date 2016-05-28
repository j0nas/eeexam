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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class RestIT {
    private static WebDriver driver;
    private static WireMockServer wireMockServer;

    private static HomePageObject homePageObject;
    private static LoginPageObject loginPageObject;
    private static NewUserPageObject newUserPageObject;
    private static CreateEventPageObject createEventPageObject;

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

        final InputStream countriesJson = RestIT.class.getResourceAsStream("/countries.json");
        final String body = new Scanner(countriesJson, "UTF-8").useDelimiter("\\A").next();

        final UrlMatchingStrategy matching = urlMatching("/rest/v1/all");
        final ResponseDefinitionBuilder responseBuilder = aResponse()
                .withHeader("Content-Type", "application/json;charset=utf-8")
                .withBody(body);
        final RemoteMappingBuilder mappingBuilder = get(matching).willReturn(responseBuilder);
        wireMockServer.stubFor(mappingBuilder);

        assumeTrue(JBossUtil.isJBossUpAndRunning());

        instantiatePageObjects();
        homePageObject.toIndexPage();
        assertTrue(homePageObject.isAtHomePage());

        createTestEventsAndUsers();
    }

    private static void instantiatePageObjects() {
        homePageObject = new HomePageObject(driver);
        loginPageObject = new LoginPageObject(driver);
        newUserPageObject = new NewUserPageObject(driver);
        createEventPageObject = new CreateEventPageObject(driver);
    }

    private static void createTestEventsAndUsers() {
        final String[] countries = new String[] { "Belgium", "Spain", "Spain"};
        for (String country : countries) {
            createUser();
            createEvent(country);
            homePageObject.clickLogoutButton();
        }
    }

    private static void createEvent(String country) {
        homePageObject.clickCreateEventButton();
        createEventPageObject.fillOutEventDataAndSubmit(country);
    }

    private static void createUser() {
        loginPageObject.navigateToLoginPage();
        loginPageObject.clickLoginFormCreateNewUser();
        newUserPageObject.fillOutUserFormAndSubmit(true);
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }

    @Before
    public void setUp() throws Exception {
        toggled = false;
        assumeTrue(JBossUtil.isJBossUpAndRunning());


        homePageObject.toIndexPage();
        assertTrue(homePageObject.isAtHomePage());
    }

    @Test
    public void testGetAllXml() throws Exception {
        System.out.println("Sleeping");
        Thread.sleep(50000);
    }
}
