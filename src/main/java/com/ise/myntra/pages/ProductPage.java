package com.ise.myntra.pages;

import com.ise.myntra.utils.Config;
import org.openqa.selenium.*;

public class ProductPage extends BasePage {
    private static final String LOWER = "translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
    private static final By PINCODE_INPUT = By.cssSelector("input[placeholder*='incode' i]");
    private static final By CHECK_BTN     = By.xpath("//*[normalize-space(text())='Check' or normalize-space(text())='CHECK']");
    private static final By PIN_ERROR     = By.xpath("//*[contains(" + LOWER + ",'valid pincode')]");

    public ProductPage(WebDriver d) { super(d); }

    public ProductPage open() { driver.get(Config.get("product.url")); visible(PINCODE_INPUT); return this; }

    public ProductPage checkPincode(String pin) {
        type(PINCODE_INPUT, pin);
        click(CHECK_BTN);
        return this;
    }
    public boolean isPincodeRejected() { return appears(PIN_ERROR, 4); }

    // ---- non-waiting reads used by the live demo ----
    public String peekValue() {
        var l = driver.findElements(PINCODE_INPUT);
        return l.isEmpty() ? null : l.get(0).getAttribute("value");
    }
    public boolean errorNow() {
        return driver.findElements(PIN_ERROR).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception x) { return false; }
        });
    }
}
