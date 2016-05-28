package no.jenjon13.eeexam.selenium.pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class NewUserPageObject extends BasePageObject {
    public NewUserPageObject(WebDriver driver) {
        super(driver);
    }


    public String fillOutUserFormAndSubmit(boolean matchingPassword) {
        final String testValue = "Test";
        int randomSuffix = (int) (Math.random() * 10000);
        String combinedTestValue = testValue + randomSuffix;

        String[] fields = new String[]{"username", "password1", "password2", "firstname", "lastname"};
        for (String field : fields) {
            By byTxtField = By.id("userform:" + field);
            WebElement txtWebElement = driver.findElement(byTxtField);
            txtWebElement.clear();

            boolean currentFieldIsMatchingPasswordField = field.equals("password2") && !matchingPassword;
            String stringToType = currentFieldIsMatchingPasswordField ? "DIFFERENT PASSWORD" : combinedTestValue;
            txtWebElement.sendKeys(stringToType);
        }

        By bySelectCountry = By.id("userform:country");
        WebElement selectWebElement = driver.findElement(bySelectCountry);
        Select select = new Select(selectWebElement);
        select.selectByVisibleText("Norway");

        By byBtnSubmit = By.id("userform:submit");
        WebElement submitWebElement = driver.findElement(byBtnSubmit);
        submitWebElement.click();
        waitForPageToLoad();

        return combinedTestValue;
    }

}
