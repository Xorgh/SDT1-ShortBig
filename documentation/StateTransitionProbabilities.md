
# General Timing

## Configuration

| Config                                      | Value                     |
|--------------------------------------------|---------------------------|
| Tick frequency                             | 1000 ms (1 tick / second) |
| Max consecutive ticks before force transition | 10 ticks                  |
| Adjustment increment per tick              | +5% per tick              |
| Bankrupt timeout                           | 20 ticks                  |
| Reset price                                | $100.00                   |

---

# State Transition Probabilities

Each tick spent in the same state adds **+5% adjustment**, increasing the chance to leave the current state.  
At **tick 10**, the transition is **forced**.

---

## STEADY →

**Base:** 80% stay, 10% Growing, 10% Declining  
**Adjustment:** Split equally between Growing and Declining

| Tick | Stay   | → Growing | → Declining |
|-----:|--------|-----------|-------------|
| 1    | 75%    | 12.5%     | 12.5%       |
| 2    | 70%    | 15%       | 15%         |
| 3    | 65%    | 17.5%     | 17.5%       |
| 4    | 60%    | 20%       | 20%         |
| 5    | 55%    | 22.5%     | 22.5%       |
| 6    | 50%    | 25%       | 25%         |
| 7    | 45%    | 27.5%     | 27.5%       |
| 8    | 40%    | 30%       | 30%         |
| 9    | 35%    | 32.5%     | 32.5%       |
| 10   | FORCED | 50%       | 50%         |

---

## GROWING →

**Base:** 70% stay, 20% Steady, 10% Declining  
**Adjustment:** Full adjustment goes to Steady

| Tick | Stay   | → Steady | → Declining |
|-----:|--------|----------|-------------|
| 1    | 65%    | 25%      | 10%         |
| 2    | 60%    | 30%      | 10%         |
| 3    | 55%    | 35%      | 10%         |
| 4    | 50%    | 40%      | 10%         |
| 5    | 45%    | 45%      | 10%         |
| 6    | 40%    | 50%      | 10%         |
| 7    | 35%    | 55%      | 10%         |
| 8    | 30%    | 60%      | 10%         |
| 9    | 25%    | 65%      | 10%         |
| 10   | FORCED | 100%     | —           |

---

## DECLINING →

**Base:** 70% stay, 20% Steady, 10% Growing  
**Adjustment:** Full adjustment goes to Steady

| Tick | Stay   | → Steady | → Growing |
|-----:|--------|----------|-----------|
| 1    | 65%    | 25%      | 10%       |
| 2    | 60%    | 30%      | 10%       |
| 3    | 55%    | 35%      | 10%       |
| 4    | 50%    | 40%      | 10%       |
| 5    | 45%    | 45%      | 10%       |
| 6    | 40%    | 50%      | 10%       |
| 7    | 35%    | 55%      | 10%       |
| 8    | 30%    | 60%      | 10%       |
| 9    | 25%    | 65%      | 10%       |
| 10   | FORCED | 100%     | —         |

---

## BANKRUPT → RESET

- Price is locked at **$0** for **20 ticks**
- After 20 ticks → automatically transitions to **RESET**
- Also triggered immediately if any state's price calculation reaches **≤ $0**

---

## RESET → STEADY

- Instantly resets price to **$100.00**
- Transitions to **STEADY** on the very next tick (**no waiting**)

---

# Price Change Per Tick Per State

| State     | Scenario          | Chance | Change        |
|-----------|-------------------|--------|---------------|
| Steady    | Random drift      | 100%   | ±0–1%         |
| Growing   | Slight decline    | 5%     | −0–0.5%       |
| Growing   | Moderate growth   | 90%    | +0.1–1.5%     |
| Growing   | Strong growth     | 5%     | +1.5–5%       |
| Declining | Slight recovery   | 5%     | +0–0.5%       |
| Declining | Moderate decline  | 90%    | −0.1–1.5%     |
| Declining | Strong decline    | 5%     | −1.5–5%       |
| Bankrupt  | Locked            | 100%   | $0            |
| Reset     | Restored          | 100%   | Back to $100  |
