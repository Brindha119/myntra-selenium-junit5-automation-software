package com.ise.myntra.model;

/**
 * White-box SUT: a simplified MODEL of shopping-bag pricing (our own code, NOT Myntra's source).
 * Business thresholds are illustrative constants.
 *
 * Decision (predicate) nodes: P1 mrp<=0, P2 qty<1, P3 qty>MAX_QTY, P4 insider,
 * P5 couponOff>0, P6 amount<FREE_SHIPPING_MIN  ->  V(G) = 6 + 1 = 7
 */
public final class BagPriceCalculator {
    public static final int MAX_QTY = 10;
    public static final double INSIDER_DISCOUNT = 0.05;
    public static final double FREE_SHIPPING_MIN = 999.0;
    public static final double SHIPPING_FEE = 99.0;

    private BagPriceCalculator() {}

    public static double finalAmount(double mrp, int qty, boolean insider, double couponOff) {
        if (mrp <= 0 || qty < 1 || qty > MAX_QTY) {                 // N1, N2, N3
            throw new IllegalArgumentException("Invalid price or quantity"); // N4
        }
        double amount = mrp * qty;                                   // N5
        if (insider) {                                               // N6
            amount = amount * (1 - INSIDER_DISCOUNT);                // N7
        }
        if (couponOff > 0) {                                         // N8
            amount = amount - Math.min(couponOff, amount);           // N9
        }
        if (amount < FREE_SHIPPING_MIN) {                            // N10
            amount = amount + SHIPPING_FEE;                          // N11
        }
        return Math.round(amount * 100.0) / 100.0;                   // N12 -> exit N13
    }
}
