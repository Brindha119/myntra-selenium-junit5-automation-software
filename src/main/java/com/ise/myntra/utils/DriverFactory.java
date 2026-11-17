package com.ise.myntra.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/** WebDriver lifecycle. Choose browser with -Dbrowser=chrome|firefox|edge (default chrome). */
public final class DriverFactory {
    private DriverFactory() {}

    public static WebDriver create() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        WebDriver driver;
        switch (browser) {
            case "firefox" -> { WebDriverManager.firefoxdriver().setup(); driver = new FirefoxDriver(); }
            case "edge"    -> { WebDriverManager.edgedriver().setup();    driver = new EdgeDriver(); }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions o = new ChromeOptions();
                o.addArguments("--start-maximized", "--disable-notifications");
                // Headless is OFF by default: large retail sites often block headless browsers.
                if (Boolean.getBoolean("headless")) o.addArguments("--headless=new", "--window-size=1920,1080");
                driver = new ChromeDriver(o);
            }
        }
        driver.manage().window().maximize();
        return driver;
    }
}
