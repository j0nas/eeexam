package no.jenjon13.eeexam.selenium.pageobject;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePageObject {
    protected final WebDriver driver;

    public BasePageObject(WebDriver driver) {
        this.driver = driver;
    }

    // TODO check for result of this method
    public Boolean waitForPageToLoad() {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, 10);

        return wait.until((ExpectedCondition<Boolean>) input -> {
            final String script = "return /loaded|complete/.test(document.readyState);";
            final String res = jsExecutor.executeScript(script).toString();
            return Boolean.parseBoolean(res);
        });
    }

}
