package com.ise.myntra.demo;

import com.ise.myntra.pages.LoginPage;
import com.ise.myntra.pages.ProductPage;
import com.ise.myntra.utils.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MANUAL DEMO MODE. The program only opens Myntra and quietly records what you type.
 * Nothing is shown on the page while you present. When you press ENTER in the Console,
 * the results table (input, ECP/BVA class, expected, what Myntra did, PASS/FAIL) opens in a new tab.
 *
 * Eclipse: right-click LiveDemo -> Run As -> Java Application.   Terminal: mvn compile exec:java
 * Works on the login page (mobile field) and on any product page (PIN-code box); just navigate there yourself.
 */
public class LiveDemo {
    enum V { IDLE, PENDING, PASS, FAIL }
    record R(String id, String partition, String expected, V verdict) {}
    record Row(String field, String input, boolean errorSeen, R r) {}

    /** Follows one input field; records a value once it has stayed unchanged for 1.5 s (or an error showed up). */
    static class Tracker {
        final String field; String cur = ""; long since; boolean errSeen, committed;
        final Map<String, Boolean> seen = new LinkedHashMap<>();
        Tracker(String field) { this.field = field; }
        void tick(String v, boolean err, long now) {
            if (v == null) v = "";
            if (!v.equals(cur)) { cur = v; since = now; errSeen = false; committed = false; }
            if (v.isEmpty()) return;
            if (err) errSeen = true;
            if (committed || err || now - since >= 1500) { committed = true; seen.merge(v, errSeen, Boolean::logicalOr); }
        }
    }

    public static void main(String[] args) throws Exception {
        WebDriver driver = DriverFactory.create();
        driver.get("https://www.myntra.com");
        LoginPage login = new LoginPage(driver);       // used only for non-waiting reads
        ProductPage product = new ProductPage(driver);
        Tracker mobile = new Tracker("Mobile number"), pin = new Tracker("PIN code");

        System.out.println("Myntra is open. Go to the login page / a product page and enter inputs by hand.");
        System.out.println(">>> Press ENTER here when you want to SHOW THE RESULTS. <<<");
        Scanner in = new Scanner(System.in);
        AtomicBoolean done = new AtomicBoolean(false);
        Thread t = new Thread(() -> { try { in.nextLine(); } catch (Exception ignored) { } done.set(true); });
        t.setDaemon(true); t.start();

        int failures = 0; boolean browserAlive = true;
        while (!done.get()) {
            try {
                long now = System.currentTimeMillis();
                mobile.tick(login.peekValue(), login.errorNow(), now);
                pin.tick(product.peekValue(), product.errorNow(), now);
                failures = 0;
            } catch (Exception e) { if (++failures >= 4) { browserAlive = false; break; } }
            Thread.sleep(300);
        }

        List<Row> rows = new ArrayList<>();
        mobile.seen.forEach((v, e) -> rows.add(new Row(mobile.field, v, e, evalMobile(v, e))));
        pin.seen.forEach((v, e) -> rows.add(new Row(pin.field, v, e, evalPin(v, e))));

        printConsole(rows);
        Path out = Path.of("target", "manual-test-results.html");
        Files.createDirectories(out.getParent());
        Files.writeString(out, html(rows));
        if (browserAlive) {
            try { driver.switchTo().newWindow(WindowType.TAB).get(out.toAbsolutePath().toUri().toString()); }
            catch (Exception e) { openDesktop(out); }
            System.out.println("Results are open in a new browser tab. Press ENTER again to close the browser.");
            try { in.nextLine(); } catch (Exception ignored) { }
            try { driver.quit(); } catch (Exception ignored) { }
        } else openDesktop(out);
    }

    static void openDesktop(Path p) {
        try { Desktop.getDesktop().browse(p.toAbsolutePath().toUri()); } catch (Exception e) { System.out.println("Open manually: " + p.toAbsolutePath()); }
    }

    // ---------- ECP/BVA classification + verdict ----------
    static R evalMobile(String v, boolean err) {
        if (!v.matches("\\d+"))
            return new R("ECP-M4", "Invalid: non-numeric characters", "Digits only OR error", err ? V.PASS : V.PENDING);
        if (v.length() == 10 && "6789".indexOf(v.charAt(0)) >= 0)
            return new R("ECP-M1", "Valid: 10 digits, starts 6-9", "Accepted, no error", err ? V.FAIL : V.PASS);
        if (v.length() == 10)
            return new R("ECP-M2", "Invalid: 10 digits, starts 0-5", "Error shown", err ? V.PASS : V.PENDING);
        if (v.length() == 9)
            return new R("BVA-M1", "Boundary: Min-1 (9 digits)", "Error shown", err ? V.PASS : V.PENDING);
        return new R("ECP-M3", "Invalid: fewer than 10 digits", "Error shown", err ? V.PASS : V.PENDING);
    }

    static R evalPin(String v, boolean err) {
        if (!v.matches("\\d+"))
            return new R("ECP-P3", "Invalid: non-numeric", "Rejected", err ? V.PASS : V.PENDING);
        if (v.length() == 5)
            return new R("BVA-P1", "Boundary: Min-1 (5 digits)", "Rejected", err ? V.PASS : V.PENDING);
        if (v.length() == 6 && v.charAt(0) == '0')
            return new R("ECP-P2", "Invalid: starts with 0", "Rejected", err ? V.PASS : V.PENDING);
        if (v.length() == 6)
            return new R("ECP-P1", "Valid: 6 digits, first 1-9", "Accepted, no error", err ? V.FAIL : V.PASS);
        return new R("-", "Length " + v.length() + " (not in test design)", "-", V.IDLE);
    }

    static String verdictText(R r) {
        return switch (r.verdict()) { case PASS -> "PASS"; case FAIL -> "FAIL"; case PENDING -> "NOT TRIGGERED";
                                      default -> "INFO"; };
    }

    // ---------- results output ----------
    static void printConsole(List<Row> rows) {
        System.out.println("\n================ MANUAL TEST RESULTS ================");
        System.out.printf("%-14s %-14s %-8s %-30s %-20s %-14s%n", "Field", "Input", "Case", "Class", "Myntra behaviour", "Result");
        for (Row w : rows)
            System.out.printf("%-14s %-14s %-8s %-30s %-20s %-14s%n", w.field(), w.input(), w.r().id(), w.r().partition(),
                    w.errorSeen() ? "Error shown" : "No error shown", verdictText(w.r()));
        long pass = rows.stream().filter(w -> w.r().verdict() == V.PASS).count();
        long fail = rows.stream().filter(w -> w.r().verdict() == V.FAIL).count();
        System.out.println("PASS: " + pass + "   FAIL: " + fail + "   TOTAL: " + rows.size() + "\n");
    }

    static String esc(String s) { return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"); }

    static String html(List<Row> rows) {
        long pass = rows.stream().filter(w -> w.r().verdict() == V.PASS).count();
        long fail = rows.stream().filter(w -> w.r().verdict() == V.FAIL).count();
        long nt = rows.stream().filter(w -> w.r().verdict() == V.PENDING).count();
        StringBuilder b = new StringBuilder("<!doctype html><meta charset='utf-8'><title>Manual Test Results</title>"
            + "<style>body{font-family:Arial;margin:30px;color:#1f2a44}table{border-collapse:collapse;width:100%}"
            + "th{background:#1f2a44;color:#fff;padding:10px;text-align:left}td{padding:10px;border-bottom:1px solid #ccd}"
            + ".PASS{background:#c8e6c9;font-weight:bold}.FAIL{background:#ffcdd2;font-weight:bold}"
            + ".NOT{background:#ffe0b2;font-weight:bold}.INFO{background:#eceff1}.s{font-size:18px;margin:12px 0}</style>"
            + "<h1>Myntra: Manual Test Results (ECP / BVA)</h1><div class='s'>PASS <b>" + pass + "</b> &nbsp; FAIL <b>" + fail
            + "</b> &nbsp; NOT TRIGGERED <b>" + nt + "</b> &nbsp; TOTAL <b>" + rows.size() + "</b></div>"
            + "<table><tr><th>#</th><th>Field</th><th>Input typed</th><th>Case</th><th>Class</th><th>Expected</th>"
            + "<th>Myntra behaviour</th><th>Result</th></tr>");
        int i = 1;
        for (Row w : rows) {
            String cls = switch (w.r().verdict()) { case PASS -> "PASS"; case FAIL -> "FAIL"; case PENDING -> "NOT"; default -> "INFO"; };
            b.append("<tr><td>").append(i++).append("</td><td>").append(esc(w.field())).append("</td><td><b>")
             .append(esc(w.input())).append("</b></td><td>").append(w.r().id()).append("</td><td>").append(esc(w.r().partition()))
             .append("</td><td>").append(esc(w.r().expected())).append("</td><td>")
             .append(w.errorSeen() ? "Error shown" : "No error shown").append("</td><td class='").append(cls).append("'>")
             .append(verdictText(w.r())).append("</td></tr>");
        }
        return b.append("</table><p><i>NOT TRIGGERED = invalid input where no error appeared (the button was not clicked, "
            + "or Myntra accepted it).</i></p>").toString();
    }
}
