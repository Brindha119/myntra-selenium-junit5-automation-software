package com.ise.myntra.pages;

import com.ise.myntra.utils.Config;
import com.ise.myntra.utils.Demo;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Reusable actions + explicit waits. No assertions live in page classes. */
public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Config.getInt("timeout.seconds")));
    }
    protected WebElement visible(By by) { return wait.until(ExpectedConditions.visibilityOfElementLocated(by)); }
    protected void click(By by) {
        WebElement e = wait.until(ExpectedConditions.elementToBeClickable(by));
        Demo.highlight(driver, e);
        Demo.pause(600);
        e.click();
    }
    protected void type(By by, String text) {
        WebElement e = visible(by);
        Demo.highlight(driver, e);
        Demo.pause(600);
        e.click();
        e.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        e.sendKeys(text);
    }
    /** true if the element becomes visible within `seconds`, else false (no exception). */
    protected boolean appears(By by, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds)).until(ExpectedConditions.visibilityOfElementLocated(by));
            return true;
        } catch (TimeoutException e) { return false; }
    }
    public String currentUrl() { return driver.getCurrentUrl(); }
}
