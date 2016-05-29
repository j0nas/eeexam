package no.jenjon13.eeexam.selenium.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RemoteMappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import no.jenjon13.eeexam.ejb.EventEJB;
import no.jenjon13.eeexam.entities.Event;
import no.jenjon13.eeexam.selenium.conf.Config;
import no.jenjon13.eeexam.selenium.conf.JBossUtil;
import no.jenjon13.eeexam.selenium.pageobject.CreateEventPageObject;
import no.jenjon13.eeexam.selenium.pageobject.HomePageObject;
import no.jenjon13.eeexam.selenium.pageobject.LoginPageObject;
import no.jenjon13.eeexam.selenium.pageobject.NewUserPageObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class RestIT {
    private static WebDriver driver;
    private static WireMockServer wireMockServer;

    private static HomePageObject homePageObject;
    private static LoginPageObject loginPageObject;
    private static NewUserPageObject newUserPageObject;
    private static CreateEventPageObject createEventPageObject;

    private static List<Event> createdEvents = new ArrayList<>();

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
        final String[] countries = new String[]{"Belgium", "Spain", "Spain"};
        for (String country : countries) {
            createUser();
            createEvent(country);
            homePageObject.clickLogoutButton();
        }
    }

    private static void createEvent(String country) {
        homePageObject.clickCreateEventButton();
        final Event event = createEventPageObject.fillOutEventDataAndSubmitAndReturnEvent(country);
        createdEvents.add(event);
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

    @Test
    public void testGetAllXml() throws Exception {
        URI uri = UriBuilder.fromUri("http://localhost/pg6100_exam/rs/events/all").port(8080).build();
        Client client = ClientBuilder.newClient();
        Response response = client.target(uri).request(MediaType.APPLICATION_XML).get();
//        final List<Event> events = response.readEntity(String.class);

        final String events = response.readEntity(String.class);
        assertTrue(events.contains("<event><country>Belgium</country>"));
        assertTrue(events.contains("<event><country>Spain</country>"));

//        for (Event event : createdEvents) {
//            assertTrue(events.contains(event));
//        }
    }

    @Test
    public void testGetCountryAXml() throws Exception {
        final Response response = queryForEventsInCountry("Spain");
        final String events = response.readEntity(String.class);
        assertTrue(events.contains("<event><country>Spain</country>"));
        assertFalse(events.contains("<event><country>Belgium</country>"));
    }

    @Test
    public void testGetCountryBXml() throws Exception {
        Response response = queryForEventsInCountry("Belgium");
        final String events = response.readEntity(String.class);
        assertTrue(events.contains("<event><country>Belgium</country>"));
        assertFalse(events.contains("<event><country>Spain</country>"));
    }

    @Test
    public void testGetAllJson() throws Exception {
        final URI uri = UriBuilder.fromUri("http://localhost/pg6100_exam/rs/events/all").port(8080).build();
        final Client client = ClientBuilder.newClient();
        final Response response = client.target(uri).request(MediaType.APPLICATION_JSON).get();

        final String events = response.readEntity(String.class);
        assertTrue(events.contains("Belgium"));
        assertTrue(events.contains("Spain"));
    }

    private Response queryForEventsInCountry(String country) {
        URI uri = UriBuilder.fromUri("http://localhost/pg6100_exam/rs/events/all?country=" + country).port(8080).build();
        Client client = ClientBuilder.newClient();
        return client.target(uri).request(MediaType.APPLICATION_XML).get();
    }
}
