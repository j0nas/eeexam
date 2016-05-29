package no.jenjon13.eeexam.selenium.pageobject;

import no.jenjon13.eeexam.entities.Event;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class CreateEventPageObject extends BasePageObject {
    public CreateEventPageObject(WebDriver driver) {
        super(driver);
    }


    public String fillOutEventDataAndSubmit(String country) {
        return fillOutEventDataAndSubmitAndReturnEvent(country).getTitle();
    }

    public Event fillOutEventDataAndSubmitAndReturnEvent(String country) {
        final String[] fields = new String[] {"title", "location", "description"};
        String testString = "TEST " + ((int) (Math.random() * 10000));

        final Event event = new Event();
        event.setTitle(testString);
        event.setLocation(testString);
        event.setDescription(testString);
        event.setCountry(country);

        for (String field : fields) {
            final By byField = By.id("eventform:" + field);
            final WebElement fieldElement = driver.findElement(byField);
            fieldElement.clear();
            fieldElement.sendKeys(testString);
        }

        By bySelectCountry = By.id("eventform:country");
        WebElement selectWebElement = driver.findElement(bySelectCountry);
        Select select = new Select(selectWebElement);
        select.selectByVisibleText(country);

        By byBtnSubmit = By.id("eventform:submit");
        WebElement submitWebElement = driver.findElement(byBtnSubmit);
        submitWebElement.click();
        waitForPageToLoad();

        return event;
    }

    public boolean isOnEventPage() {
        final By byPageTitle = By.id("pagetitle");
        final List<WebElement> titleElements = driver.findElements(byPageTitle);

        if (titleElements.isEmpty()) {
            return false;
        }

        final WebElement firstElement = titleElements.get(0);
        final String titleElementText = firstElement.getText();
        return titleElementText.contains("Create New Event");
    }
}
