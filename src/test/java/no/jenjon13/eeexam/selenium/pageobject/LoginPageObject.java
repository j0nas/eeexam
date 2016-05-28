package no.jenjon13.eeexam.selenium.pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPageObject extends BasePageObject {
    public LoginPageObject(WebDriver driver) {
        super(driver);
    }

    public boolean isOnLoginPage() {
        By byPageTitle = By.id("pagetitle");
        WebElement element = driver.findElement(byPageTitle);
        String elementText = element.getText();
        return elementText.equals("Login");
    }

    public void navigateToLoginPage() {
        By byBtnLoginId = By.id("btnLogin");
        WebElement webElement = driver.findElement(byBtnLoginId);
        webElement.click();
        waitForPageToLoad();
    }

    public void clickLoginFormCreateNewUser() {
        By byBtnCreateNewUser = By.id("loginForm:btnCreateNewUser");
        WebElement createNewUserWebElement = driver.findElement(byBtnCreateNewUser);
        createNewUserWebElement.click();
        waitForPageToLoad();
    }
}
