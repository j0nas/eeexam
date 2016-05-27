package no.jenjon13.eeexam.selenium;

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
        assertTrue(homePageObject.isAtIndexPage());
    }

    @Test
    public void testHomePage() {
        final By elementIdentifier = By.id("pagetitle");
        WebElement text = driver.findElement(elementIdentifier);
        String value = text.getText();
        assertEquals("Event List Home Page", value); //FIXME
    }
}
