# Singleton note

## Er Singleton et design pattern?

- Ja, Singleton er et design pattern
- Det er et creational pattern, fordi det handler om oprettelse og adgang til objekter
- Formålet er at sikre, at der kun findes én instans, som resten af systemet deler

## Hvorfor kan det give mening for en logger?

- Logging er en cross-cutting concern, som mange dele af systemet bruger
- En fælles logger giver ét sted at styre output og konfiguration
- Det kan gøre adfærden mere konsistent på tværs af systemet

## Vigtig nuance

- Singleton i sig selv løser ikke race conditions
- Singleton sikrer kun, at der er én delt instans
- Trådsikkerhed afhænger stadig af implementationen, fx synkronisering eller thread-safe skrivning

## Ulemper

- Det kan være sværere at mocke eller erstatte i tests
- Det skaber global state
- Det kan give skjulte afhængigheder, fordi klasser kalder singletons direkte i stedet for at få dependencies indsprøjtet
- Det kan øge kobling mellem klasser og gøre refaktorering sværere

## Kort eksamensformulering

- Singleton kan være et fornuftigt valg til en logger, fordi jeg gerne vil have én fælles logging-service
- Men jeg vil også nævne ulempen, at det gør testning sværere og ikke i sig selv løser concurrency-problemer
