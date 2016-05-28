package no.jenjon13.eeexam.selenium.pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class CreateEventPageObject extends BasePageObject {
    public CreateEventPageObject(WebDriver driver) {
        super(driver);
    }


    public void fillOutEventDataAndSubmit() {
        final String[] fields = new String[] {"title", "location", "description"};
        String testString = "TEST " + ((int) (Math.random() * 10000));

        for (String field : fields) {
            final By byField = By.id("eventform:" + field);
            final WebElement fieldElement = driver.findElement(byField);
            fieldElement.clear();
            fieldElement.sendKeys(testString);
        }

        By bySelectCountry = By.id("eventform:country");
        WebElement selectWebElement = driver.findElement(bySelectCountry);
        Select select = new Select(selectWebElement);
        select.selectByVisibleText("Norway");

        By byBtnSubmit = By.id("eventform:submit");
        WebElement submitWebElement = driver.findElement(byBtnSubmit);
        submitWebElement.click();
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
