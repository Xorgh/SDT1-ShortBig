# Dansk Cram Sheet

Kilde: [eksamensspørgsmål.md](./eksamensspørgsmål.md)

## Tre-lagsarkitektur

- Type: **Arkitekturmønster**
- Løser: opdeler systemet i UI, forretningslogik og dataadgang, så koden bliver lettere at forstå, teste og vedligeholde.
- SOLID: støtter især **SRP** ved at dele ansvar op i lag.

## Transaction Script

- Type: **Enterprise / applikationslogik-mønster**
- Løser: samler én forretningshandling i én metode/service, så simple use cases bliver nemme at implementere.
- SOLID: kan passe fint med **SRP**, hvis hvert script kun håndterer én use case.

## Observer

- Type: **Adfærdsmønster**
- Løser: objekter kan automatisk blive informeret om ændringer uden tæt kobling.
- SOLID: støtter især **OCP** og **DIP** gennem løs kobling via abstraktioner.

## Adapter

- Type: **Strukturmønster**
- Løser: får klasser med inkompatible interfaces til at arbejde sammen.
- SOLID: støtter især **OCP** ved at udvide uden at ændre eksisterende kode.

## DAO

- Type: **Dataadgangs-/persistensmønster**
- Løser: skjuler databasekode bag et interface, så forretningslogik ikke afhænger direkte af databasen.
- SOLID: støtter især **DIP** og **SRP** ved at adskille dataadgang fra forretningslogik.

## Unit of Work

- Type: **Enterprise / persistensmønster**
- Løser: samler flere ændringer og gemmer dem som én koordineret transaktion.
- SOLID: støtter især **SRP** ved at samle transaktionsansvar ét sted.

## MVVM

- Type: **Arkitektur- / præsentationsmønster**
- Løser: adskiller View, ViewModel og Model, så UI-kode bliver renere og lettere at teste.
- SOLID: støtter især **SRP** og **DIP** gennem tydelig ansvarsdeling og binding til abstraktioner.

## State

- Type: **Adfærdsmønster**
- Løser: flytter adfærd ud i state-objekter, så man undgår store `if/switch`-blokke.
- SOLID: støtter især **OCP** ved at nye states kan tilføjes uden store ændringer.

## Strategy

- Type: **Adfærdsmønster**
- Løser: gør algoritmer udskiftelige uden at ændre klientkoden.
- SOLID: støtter især **OCP** og **DIP** gennem udskiftelige strategier bag interfaces.

## V-modellen / Testing

- Type: **Testmodel, ikke designmønster**
- Løser: kobler udviklingsfaser sammen med testfaser, så kvalitetssikring planlægges systematisk.
- SOLID: ikke direkte et SOLID-mønster, men god ansvarsdeling gør systemet lettere at teste.
