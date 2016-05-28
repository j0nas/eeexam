package no.jenjon13.eeexam.selenium.pageobject;

import no.jenjon13.eeexam.selenium.conf.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
}
