package com.ise.myntra.tests;

import com.ise.myntra.base.BaseTest;
import com.ise.myntra.pages.HomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("live")
class SearchTest extends BaseTest {
    @ParameterizedTest(name = "search \"{0}\" returns products")
    @ValueSource(strings = {"tshirt", "nike shoes", "kurta"})
    @DisplayName("TC-S: keyword search shows a non-empty product grid")
    void searchReturnsProducts(String term) {
        HomePage home = new HomePage(driver).open().search(term);
        assertThat(home.resultCount()).as("products for '%s'", term).isGreaterThan(0);
        assertThat(home.currentUrl()).contains(term.split(" ")[0]);
    }
}
