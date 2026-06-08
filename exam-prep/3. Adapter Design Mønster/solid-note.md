# SOLID-note til Adapter

## OCP og Adapter

- Open/Closed Principle er princippet
- Adapter er et mønster, der kan bruges til at understøtte princippet
- Pointen er, at jeg kan tilføje en ny eller inkompatibel klasse uden at ændre klientkoden

## Kort eksamensformulering

- OCP er målet, og Adapter er et middel
- Adapteren gør, at eksisterende kode kan blive ved med at bruge det samme target-interface, mens jeg tilføjer et oversættelseslag

## LSP og Adapter

- En adapter er ikke automatisk "god", bare fordi den passer strukturelt ind
- Hvis adapteren implementerer et interface, skal den også kunne leve op til hele den kontrakt interfacet lover
- Ellers kan der opstå et brud på Liskov Substitution Principle

## Konkret eksempel fra mit projekt

- `FileLogOutputAdapter` implementerer `LogOutput`
- `LogOutput` accepterer alle `LogLevel`-værdier, inklusive `DEBUG`
- Men `FileLogOutputAdapter` ignorerer `DEBUG`
- Derfor er den ikke fuldt substituerbar med andre `LogOutput`-implementationer, fx `ConsoleLogOutput`, som faktisk logger `DEBUG`

## Designrefleksion om ekstern kode

- Problemet er ikke nødvendigvis, at jeg ikke kendte den eksterne begrænsning, da jeg først designede `LogOutput`
- Problemet opstår ved integrationen, når jeg vælger at lade en adapter implementere et interface, som den eksterne komponent ikke fuldt ud kan leve op til
- Det gør det til et arkitekturspørgsmål: passer den eksterne dependency reelt til min abstraktion?
- Adapteren kan løse interface mismatch, men ikke automatisk semantic mismatch
- Derfor kan man sige, at udfordringen ikke kun er inde i adapterens kode, men også i beslutningen om at adapt’e netop den eksterne løsning til `LogOutput`

## Kort eksamensformulering

- Adapter understøtter ofte OCP, fordi jeg kan udvide systemet uden at ændre klienten
- Men adapteren skal stadig overholde target-kontrakten, ellers kan den bryde LSP
