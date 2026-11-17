# Black-box test design (Student B)

## ECP
| Input field | Valid partition | Invalid partitions | Selected values | Expected |
|---|---|---|---|---|
| Mobile no. | 10 digits, first digit 6–9 | I1: first digit 0–5; I2: fewer than 10 digits; I3: non-numeric | 9876543210 / 5876543210 / 98765 / 98ab567890 | accepted / error / error / digits only or error |
| PIN code | 6 digits, first digit 1–9 | I1: first digit 0; I2: non-numeric; I3: wrong length | 560001 / 060001 / 56a001 / 56001 | accepted / rejected / rejected / rejected |
| Bag quantity (model) | 1–10 | I1: < 1; I2: > 10 | 5 / 0 / 11 | amount / exception / exception |

## BVA
| Field | Range | Min-1 | Min | Min+1 | Nominal | Max-1 | Max | Max+1 |
|---|---|---|---|---|---|---|---|---|
| Quantity | 1–10 | 0 ✗ | 1 ✓ | 2 ✓ | 5 ✓ | 9 ✓ | 10 ✓ | 11 ✗ |
| PIN length | 6–6 | 5 ✗ | 6 ✓ | 7 ✗ | – | – | – | – |
| Mobile length | 10–10 | 9 ✗ | 10 ✓ | 11 → truncated to 10 (field maxlength) | – | – | – | – |

> Record the *observed* behaviour for every row during manual exploration before automating; edit the CSV if Myntra behaves differently.
