package com.ise.myntra.tests;

import com.ise.myntra.base.BaseTest;
import com.ise.myntra.pages.LoginPage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.assertj.core.api.Assertions.assertThat;

/** ECP + BVA on the 10-digit mobile field. Data: testdata/mobile_ecp_bva.csv */
@Tag("live")
class MobileFieldEcpBvaTest extends BaseTest {

    @ParameterizedTest(name = "{0}: [{1}] -> {3}")
    @CsvFileSource(resources = "/testdata/mobile_ecp_bva.csv", numLinesToSkip = 1)
    void mobileNumberValidation(String id, String input, int expectedLen, String outcome) {
        LoginPage login = new LoginPage(driver).open().enterMobile(input);
        String stored = login.storedValue();

        switch (outcome) {
            case "VALID" -> {                       // never click CONTINUE: it would send a real OTP
                assertThat(stored).hasSize(expectedLen);
                assertThat(login.isErrorDisplayed()).isFalse();
            }
            case "INVALID" -> {                     // invalid submit is safe: no OTP is sent
                login.clickContinue();
                assertThat(login.isErrorDisplayed()).isTrue();
            }
            case "NONNUMERIC" -> assertThat(stored.matches("\\d*") || login.clickContinue().isErrorDisplayed()).isTrue();
            default -> throw new IllegalArgumentException("Unknown outcome " + outcome);
        }
    }
}
