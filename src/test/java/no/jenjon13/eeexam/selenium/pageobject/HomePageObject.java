package no.jenjon13.eeexam.selenium.pageobject;

import no.jenjon13.eeexam.selenium.conf.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HomePageObject extends BasePageObject {
    public HomePageObject(WebDriver driver) {
        super(driver);
    }

    public void toIndexPage() {
        final String localIndexUrl = String.format("%s:%s%s%s", Config.HOST, Config.PORT, Config.CONTEXT, Config.INDEX);
        driver.get(localIndexUrl);
        waitForPageToLoad();
    }

    public boolean isAtHomePage() {
        By by = By.id("pagetitle");
        WebElement webElement = driver.findElement(by);
        String webElementText = webElement.getText();
        return webElementText.equals("Event List Home Page");
    }

    public void clickLogoutButton() {
        By byBtnLogout = By.id("logoutForm:btnLogout");
        WebElement logoutBtnWebElement = driver.findElement(byBtnLogout);
        logoutBtnWebElement.click();
        waitForPageToLoad();
    }

    public void clickCreateEventButton() {
        final By btnCreateNewEvent = By.id("btnCreateNewEvent");
        final WebElement btnCreateNewEventWebElement = driver.findElement(btnCreateNewEvent);
        btnCreateNewEventWebElement.click();
        waitForPageToLoad();
    }

    public int getAmountOfDisplayedEvents() {
        final By byDataTable = By.id("dataForm:eventdata_data");
        final List<WebElement> dataTable = driver.findElements(byDataTable);
        if (dataTable.size() == 0) {
            return 0;
        }

        final WebElement rows = dataTable.get(0);
        final By byClassName = By.className("ui-widget-content");
        final List<WebElement> elements = rows.findElements(byClassName);
        return elements.size();
    }

}
