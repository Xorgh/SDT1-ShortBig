# Bookmark Cheat Sheet

## 2. Observer Design Mønster

### 1. Subject

- Fil: `src/business/stockmarket/StockMarket.java`
- Vis: listener-listerne og `updateAllLiveStocks()`
- Sig:
- Her viser jeg subject'et i min Observer-løsning
- `StockMarket` holder state og udsender events, når noget ændrer sig
- Jeg bruger listener-lister med `Consumer<Event>` i stedet for et klassisk observer-interface

### 2. Wiring / callback functions

- Fil: `src/presentation/core/AppContext.java`
- Vis: `registerObservers()`
- Sig:
- Her kobler jeg observers på subject'et
- Når jeg kalder `.add(...)`, registrerer jeg en callback eller method reference
- `StockMarket` kender derfor ikke de konkrete services direkte

### 3. Observer reaction: persistence update

- Fil: `src/business/services/handlers/StockListenerService.java`
- Vis: `handlePriceChange()` eller `handleStateChange()`
- Sig:
- Her kan man se en konkret observer-reaktion
- Når `StockMarket` udsender en event, bliver data opdateret i persistence-laget
- Det viser, at Observer i mit projekt ikke kun er UI, men også forretningslogik og datavedligeholdelse

### 4. Observer reaction: business rule

- Fil: `src/business/services/handlers/StockBankruptService.java`
- Vis: `handleBankruptcy()`
- Sig:
- Her reagerer en anden observer på samme type mekanisme
- Ved konkurs håndteres en forretningsregel, nemlig at ejede aktier fjernes
- Det viser, at observers kan have forskellige ansvar

### 5. Observer reaction: UI alert

- Fil: `src/business/services/handlers/StockAlertService.java`
- Vis: `handleBankruptcyAlert()` eller `handleStockResetAlert()`
- Sig:
- Her ser man, at events også kan bruges til at opdatere UI'et
- Den samme event kan altså notificere både business-logik og præsentation
- Det er en af styrkerne ved Observer: flere uafhængige reaktioner på samme ændring

### Kort rækkefølge jeg kan huske

- Subject
- Registrering
- Persistence-reaktion
- Business-reaktion
- UI-reaktion

### Hvis jeg kun når 3 bookmarks

- `StockMarket.java`
- `AppContext.java`
- `StockAlertService.java`
