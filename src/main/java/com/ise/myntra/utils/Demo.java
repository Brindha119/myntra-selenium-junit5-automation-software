package com.ise.myntra.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Visual feedback on the live Myntra page. Enable slow, highlighted demo mode with -Ddemo=true. */
public final class Demo {
    public static final boolean ON = Boolean.getBoolean("demo");
    private Demo() {}

    public static void pause(long ms) {
        if (!ON) return;
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void highlight(WebDriver d, WebElement e) {
        if (!ON) return;
        try { ((JavascriptExecutor) d).executeScript(
                "arguments[0].style.outline='3px solid #e91e63';arguments[0].style.outlineOffset='2px';", e);
        } catch (Exception ignored) { }
    }

    /** Green/red banner across the top of the Myntra page showing the test result. */
    public static void banner(WebDriver d, String msg, boolean ok) {
        try {
            ((JavascriptExecutor) d).executeScript(
                "var b=document.getElementById('qa-banner');"
              + "if(!b){b=document.createElement('div');b.id='qa-banner';document.body.appendChild(b);}"
              + "b.style.cssText='position:fixed;top:0;left:0;right:0;z-index:2147483647;padding:14px;"
              + "font:bold 18px Arial;text-align:center;color:#fff;background:'+arguments[1];"
              + "b.textContent=arguments[0];", msg, ok ? "#2e7d32" : "#c62828");
        } catch (Exception ignored) { }
    }
}
