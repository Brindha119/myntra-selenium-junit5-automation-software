package com.ise.myntra.pages;

import com.ise.myntra.utils.Config;
import org.openqa.selenium.*;

/** Mobile-number field only. NEVER submit a valid number: that would send a real OTP SMS. */
public class LoginPage extends BasePage {
    private static final By MOBILE   = By.cssSelector("input[type='tel'], input[class*='mobileNumber']");
    private static final By CONTINUE = By.xpath("//*[normalize-space(text())='CONTINUE' or normalize-space(text())='Continue']");
    private static final By ERROR    = By.cssSelector("[class*='error' i]");

    public LoginPage(WebDriver d) { super(d); }

    public LoginPage open() { driver.get(Config.get("login.url")); visible(MOBILE); return this; }
    public LoginPage enterMobile(String value) { type(MOBILE, value); return this; }
    public String storedValue() { return visible(MOBILE).getAttribute("value"); }
    public LoginPage clickContinue() { click(CONTINUE); return this; }
    public boolean isErrorDisplayed() { return appears(ERROR, 3); }

    // ---- non-waiting reads used by the live demo ----
    public String peekValue() {
        var l = driver.findElements(MOBILE);
        return l.isEmpty() ? null : l.get(0).getAttribute("value");
    }
    public boolean errorNow() {
        return driver.findElements(ERROR).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception x) { return false; }
        });
    }
}
