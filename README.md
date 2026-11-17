# End-to-End Web Automation of Myntra using Selenium, JUnit 5 & Formal QA Techniques

**Course:** Software Testing & Quality Assurance (ISE2501) 
**Target application:** Myntra – https://www.myntra.com
**Team:**
1. Brindha Yadav R K– *Automation Architect & Framework Engineer*  
2. C S Bhagath Shetty – *Test Designer & White-Box Specialist*

## 1. What this project does
| Technique | Where |
|---|---|
| Equivalence Class Partitioning | Mobile-number field, PIN-code checker – `MobileFieldEcpBvaTest`, `PincodeEcpBvaTest` |
| Boundary Value Analysis | Same fields (length 5/6/7, 9/10/11) + bag quantity (0,1,2,5,9,10,11) |
| White-box (CFG, V(G), basis paths) | `BagPriceCalculator` + `BagPriceCalculatorTest` (see `docs/WHITEBOX_ANALYSIS.md`) |
| Page Object Model | `src/main/java/com/ise/myntra/pages` |
| Parameterized tests | `@CsvFileSource`, `@CsvSource`, `@ValueSource` |

> **Scope & ethics.** Tests only touch public, read-only pages (search, login *field* validation, PIN-code check).
> They never log in, never submit a valid mobile number (which would trigger a real OTP SMS), never place orders.
> `BagPriceCalculator` is our own *model* of bag pricing used for white-box analysis, not Myntra's source code.
> Use responsibly and respect Myntra's Terms of Use; this is an academic exercise.

## 2. Project structure
```
src/main/java/com/ise/myntra
 ├─ pages/   BasePage, HomePage, LoginPage, ProductPage      (locators + actions, NO assertions)
 ├─ model/   BagPriceCalculator                              (white-box SUT)
 └─ utils/   Config, DriverFactory, Screenshots
src/test/java/com/ise/myntra
 ├─ base/    BaseTest                                        (@BeforeEach/@AfterEach, screenshot on failure)
 └─ tests/   SearchTest, MobileFieldEcpBvaTest, PincodeEcpBvaTest, BagPriceCalculatorTest
src/test/resources
 ├─ config.properties                                         (URLs, timeout)
 └─ testdata/ mobile_ecp_bva.csv, pincode_ecp_bva.csv
docs/        TEST_DESIGN.md (ECP/BVA tables), WHITEBOX_ANALYSIS.md (CFG, V(G), paths)
```

## 3. Prerequisites
- JDK 17+ (`java -version`)
- Maven 3.9+ (`mvn -version`)
- Google Chrome (latest). Optional: Firefox / Edge. WebDriverManager downloads drivers automatically.
- Internet access (Myntra + Maven Central)
- Eclipse (*Eclipse IDE for Java Developers* 2023-09+) or IntelliJ – optional

## 4. Setup
```bash
git clone <YOUR_REPO_URL>
cd myntra-selenium-junit5-automation
```
Open Myntra manually, pick any in-stock product, copy its URL and paste it into
`src/test/resources/config.properties` → `product.url=...`

## 5. Run the tests
```bash
# Everything
mvn clean test

# Offline white-box tests only (no browser, no internet needed)
mvn clean test -Dgroups=whitebox

# Live Selenium tests only
mvn clean test -Dgroups=live

# Other browsers / headless (headless may be blocked by Myntra)
mvn test -Dgroups=live -Dbrowser=firefox
mvn test -Dgroups=live -Dheadless=true

# One class
mvn test -Dtest=PincodeEcpBvaTest
```
**Eclipse:** File → Import → *Existing Maven Projects* → select the folder → right-click project →
*Run As → JUnit Test* (or right-click a test class). Window → Show View → *JUnit* shows the green bar.

### Demo mode (watch the tests run on Myntra)
```bash
mvn clean test -Dgroups=live -Ddemo=true            # highlights elements, slows down, keeps result visible
mvn test -Dgroups=live -Ddemo=true -Ddemo.pause=6000 # stay on each result page for 6 s
```
Chrome opens Myntra, Selenium's actions are outlined in pink, and a **green "PASSED" / red "FAILED" banner** appears
on the page after each test so you can verify manually. In Eclipse: Run → Run Configurations → JUnit → *Arguments* →
VM arguments: `-Ddemo=true`.

### Manual demo (open Myntra, type by hand, show results at the end)
Opens real Myntra and **only records** while you type; nothing is shown on the page during the demo.
Navigate to the login page and/or a product page yourself and enter valid and invalid inputs. When finished,
press **ENTER in the Console**: a results table (input, ECP/BVA case, expected, Myntra's behaviour, PASS/FAIL)
opens in a new browser tab and is saved to `target/manual-test-results.html`.
```bash
mvn compile exec:java
```
Eclipse: right-click `src/main/java/com/ise/myntra/demo/LiveDemo.java` → Run As → Java Application.

## 6. Reports & evidence
| Artifact | Command | Location |
|---|---|---|
| Raw results (XML/TXT) | `mvn test` | `target/surefire-reports/` |
| HTML test report | `mvn surefire-report:report` | `target/site/surefire-report.html` |
| Code coverage (JaCoCo) | `mvn test` | `target/site/jacoco/index.html` |
| Failure screenshots | automatic | `target/screenshots/` |

## 7. Troubleshooting
- **"Access Denied" / blank page:** Myntra's bot protection blocked the session. Run non-headless, slow down, retry later or from another network. The `whitebox` suite always works offline.
- **`NoSuchElement` / timeout:** the site changed its HTML. Re-inspect the element (F12) and update the locator in the matching page class – this is the only place locators live.
- **Driver errors:** update Chrome, or run `mvn clean test` again so WebDriverManager refreshes the driver.

## 8. Demo
`docs/demo.mp4` (or `.gif`) – screen recording of a full run. *(add before submitting)*

## 9. Contribution split
| Student A | Student B |
|---|---|
| pom.xml, WebDriver lifecycle, cross-browser, Page Objects, waits, reports | ECP/BVA tables, CFG, V(G), basis paths, CSV data, parameterized tests, defect log |
