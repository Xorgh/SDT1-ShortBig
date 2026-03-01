```mermaid
stateDiagram-v2
    [*] --> Steady
    Steady --> Growing
    Steady --> Declining
    Steady --> Steady
    Steady --> Bankrupt : currentPrice falls to 0 or less
    Growing --> Growing
    Growing --> Steady
    Growing --> Declining
    Declining --> Declining
    Declining --> Growing
    Declining --> Bankrupt : currentPrice falls to 0 or less
    Bankrupt --> Reset
    Reset --> Steady
```
