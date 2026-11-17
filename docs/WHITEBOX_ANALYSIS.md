# White-box analysis of `BagPriceCalculator.finalAmount` (Student B)

Nodes: N1 `mrp<=0` · N2 `qty<1` · N3 `qty>10` · N4 throw · N5 `amount=mrp*qty` · N6 `insider?` · N7 insider discount ·
N8 `coupon>0?` · N9 apply coupon · N10 `amount<min?` · N11 add shipping · N12 return · N13 exit

Edges (18): N1→N4, N1→N2, N2→N4, N2→N3, N3→N4, N3→N5, N5→N6, N6→N7, N6→N8, N7→N8, N8→N9, N8→N10, N9→N10, N10→N11, N10→N12, N11→N12, N4→N13, N12→N13

## Cyclomatic complexity
1. V(G) = E − N + 2P = 18 − 13 + 2 = **7**
2. V(G) = predicate nodes + 1 = 6 + 1 = **7**
3. V(G) = enclosed regions (6) + outer region (1) = **7**

## Independent basis paths
| Path | Route | Test input (mrp, qty, insider, coupon) | Expected |
|---|---|---|---|
| 1 | N1(T)→N4→N13 | (0,1,F,0) | IllegalArgumentException |
| 2 | N1(F)→N2(T)→N4→N13 | (500,0,F,0) | IllegalArgumentException |
| 3 | N1(F)→N2(F)→N3(T)→N4→N13 | (500,11,F,0) | IllegalArgumentException |
| 4 (baseline) | N1F,N2F,N3F,N5,N6F,N8F,N10F,N12,N13 | (1000,1,F,0) | 1000.00 |
| 5 | …N6T→N7… | (2000,1,T,0) | 1900.00 |
| 6 | …N8T→N9… | (2000,1,F,200) | 1800.00 |
| 7 | …N10T→N11… | (500,1,F,0) | 599.00 |
