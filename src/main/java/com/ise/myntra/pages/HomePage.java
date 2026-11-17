package com.ise.myntra.pages;

import com.ise.myntra.utils.Config;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {
    // Attribute-based locators are more stable than Myntra's generated class names. VERIFY in DevTools.
    private static final By SEARCH_BOX = By.cssSelector("input[placeholder*='Search for products']");
    private static final By RESULT_CARD = By.cssSelector("li.product-base");

    public HomePage(WebDriver d) { super(d); }

    public HomePage open() { driver.get(Config.get("base.url")); return this; }

    public HomePage search(String term) {
        type(SEARCH_BOX, term);
        visible(SEARCH_BOX).sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(RESULT_CARD));
        return this;
    }
    public int resultCount() { return driver.findElements(RESULT_CARD).size(); }
}
