package com.ise.myntra.base;

import com.ise.myntra.utils.Demo;
import com.ise.myntra.utils.DriverFactory;
import com.ise.myntra.utils.Screenshots;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.WebDriver;

public abstract class BaseTest {
    protected WebDriver driver;

    /** Runs BEFORE @AfterEach, so the browser is still alive when we capture the failure screenshot. */
    @RegisterExtension
    final TestExecutionExceptionHandler screenshotOnFailure = (ctx, t) -> {
        Screenshots.save(driver, ctx.getDisplayName());
        throw t;
    };

    /** Runs right after the test body (browser still open): show PASS/FAIL on the Myntra page. */
    @RegisterExtension
    final AfterTestExecutionCallback resultBanner = ctx -> {
        boolean ok = ctx.getExecutionException().isEmpty();
        String name = ctx.getDisplayName();
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name);
        Demo.banner(driver, (ok ? "\u2714 PASSED: " : "\u2718 FAILED: ") + name, ok);
        Demo.pause(Long.getLong("demo.pause", 3000L));   // time to look at the page (demo mode only)
    };

    @BeforeEach void setUp()    { driver = DriverFactory.create(); }
    @AfterEach  void tearDown() { if (driver != null) driver.quit(); }
}
