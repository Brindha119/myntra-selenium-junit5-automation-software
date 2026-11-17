package com.ise.myntra.tests;

import com.ise.myntra.model.BagPriceCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** White-box tests: 7 basis paths (V(G)=7) + BVA on quantity. Runs offline, no browser needed. */
@Tag("whitebox")
class BagPriceCalculatorTest {

    // ---- Basis paths ----
    @Test @DisplayName("Path1: mrp <= 0 -> exception")
    void path1() { assertThatThrownBy(() -> BagPriceCalculator.finalAmount(0, 1, false, 0)).isInstanceOf(IllegalArgumentException.class); }

    @Test @DisplayName("Path2: qty < 1 -> exception")
    void path2() { assertThatThrownBy(() -> BagPriceCalculator.finalAmount(500, 0, false, 0)).isInstanceOf(IllegalArgumentException.class); }

    @Test @DisplayName("Path3: qty > 10 -> exception")
    void path3() { assertThatThrownBy(() -> BagPriceCalculator.finalAmount(500, 11, false, 0)).isInstanceOf(IllegalArgumentException.class); }

    @Test @DisplayName("Path4 (baseline): no insider, no coupon, free shipping")
    void path4() { assertThat(BagPriceCalculator.finalAmount(1000, 1, false, 0)).isEqualTo(1000.00); }

    @Test @DisplayName("Path5: insider discount applied")
    void path5() { assertThat(BagPriceCalculator.finalAmount(2000, 1, true, 0)).isEqualTo(1900.00); }

    @Test @DisplayName("Path6: coupon applied")
    void path6() { assertThat(BagPriceCalculator.finalAmount(2000, 1, false, 200)).isEqualTo(1800.00); }

    @Test @DisplayName("Path7: below free-shipping minimum -> shipping fee added")
    void path7() { assertThat(BagPriceCalculator.finalAmount(500, 1, false, 0)).isEqualTo(599.00); }

    // ---- BVA on quantity (Min=1, Max=10), mrp = 1000 ----
    @ParameterizedTest(name = "BVA qty={0} -> {1}")
    @CsvSource({"1,1000.0", "2,2000.0", "5,5000.0", "9,9000.0", "10,10000.0"})
    void quantityValidBoundaries(int qty, double expected) {
        assertThat(BagPriceCalculator.finalAmount(1000, qty, false, 0)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "BVA qty={0} -> exception")
    @CsvSource({"0", "11"})
    void quantityInvalidBoundaries(int qty) {
        assertThatThrownBy(() -> BagPriceCalculator.finalAmount(1000, qty, false, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
