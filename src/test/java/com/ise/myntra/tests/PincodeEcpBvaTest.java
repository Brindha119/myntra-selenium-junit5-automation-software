package com.ise.myntra.tests;

import com.ise.myntra.base.BaseTest;
import com.ise.myntra.pages.ProductPage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.assertj.core.api.Assertions.assertThat;

/** ECP + BVA on the 6-digit PIN code checker. Data: testdata/pincode_ecp_bva.csv */
@Tag("live")
class PincodeEcpBvaTest extends BaseTest {

    @ParameterizedTest(name = "{0}: pincode [{1}] -> {2}")
    @CsvFileSource(resources = "/testdata/pincode_ecp_bva.csv", numLinesToSkip = 1)
    void pincodeValidation(String id, String pincode, String expected) {
        boolean rejected = new ProductPage(driver).open().checkPincode(pincode).isPincodeRejected();
        assertThat(rejected).as("%s rejected?", pincode).isEqualTo("REJECT".equals(expected));
    }
}
