package com.ise.myntra.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Screenshots {
    private Screenshots() {}
    public static void save(WebDriver driver, String name) {
        try {
            Path dir = Path.of("target", "screenshots");
            Files.createDirectories(dir);
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(dir.resolve(name.replaceAll("[^a-zA-Z0-9._-]", "_") + ".png"), png);
        } catch (Exception ignored) { }
    }
}
