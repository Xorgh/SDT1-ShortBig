# SOLID-note til State

## SRP og State

- `LiveStock` holder runtime-data og delegerer adfærd videre
- De konkrete states står for hver deres type prisadfærd
- `TransitionManager` samler logikken for state-skift

## OCP og State

- Ny adfærd kan tilføjes med en ny state-klasse
- Det er bedre end at udvide én stor `if`- eller `switch`-blok i `LiveStock`
- Mønstret gør derfor udvidelse lettere uden at samle alt ansvar ét sted

## LSP og ISP

- Alle konkrete states bruges gennem `LiveStockState`
- `LiveStock` kalder de samme metoder uanset konkret state
- Interfacet er lille og fokuseret, så states kun skal implementere det nødvendige
- De principper passer fint her, men de er ikke hovedpointen i eksemplet

## Kort designrefleksion

- `DIP` er kun delvist opfyldt i denne løsning
- `LiveStock` afhænger af abstraktionen `LiveStockState`, hvilket er godt
- Men `LiveStock` opretter stadig en konkret `TransitionManager`

## Kort eksamensformulering

- State passer især godt med `SRP` og `OCP`
- `SRP`, fordi adfærd og overgange er delt ud på mere afgrænsede klasser
- `OCP`, fordi ny state-adfærd kan tilføjes uden at bygge mere central betinget logik
- `LSP` og `ISP` støtter også designet, mens `DIP` kun er delvist opfyldt
