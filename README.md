# Traccia - Gestore telefonico 2

* Codice gruppo: rklikgyqnah

Si vuole simulare un sistema relativo ad un gestore telefonico. Un gestore telefonico dispone di n utenti abbonati che effettuano chiamate telefoniche, inviano sms o effettuano
trasferimento dati. Ogni abbonato è identificato da un nome, cognome, residenza, numero di telefono, piano tariffario e un conto (fisso o ricaricabile).
Il gestore telefonico prevede un piano tariffario base a cui possono essere aggiunte delle nuove opzioni. Gli abbonati possono anche usufruire di promozioni speciali (i.e., internet
illimitato ad un costo fisso mensile, . . .).

Scrivere un programma per la simulazione del gestore telefonico. Il sistema deve prevedere l’accesso in modalità _amministratore_ e in modalità _abbonato_.

L’amministratore può effettuare le seguenti operazioni:
* registrare un nuovo abbonato
* inserire una nuova promozione
* per ogni utente mostrare le percentuali di utilizzo di ogni servizio

L’abbonato può effettuare le seguenti operazioni:
* effettuare chiamate, inviare sms ed effettuare connessioni dati. Le operazioni effettuate prevedono un addebito sul conto
* aderire ad una promozione o effettuare una ricarica. Il pagamento può avvenire secondo le modalità: contanti, carta di credito o bancomat.

Simulare il sistema considerando anche i tempi e le tariffe per la comunicazione tra utenti.

## Note di sviluppo
La prova d’esame richiede la progettazione e lo sviluppo della traccia proposta. Lo studente può scegliere di sviluppare il progetto nelle due modalità: Applicazione Web o programma
standalone con supporto grafico.

Ogni progetto può essere svolto al massimo da due studenti.

Il progetto deve essere sviluppato secondo le seguenti linee:
* usare almeno due pattern per persona tra i design pattern visti a lezione;
* attenersi ai principi della programmazione SOLID;
* usare il linguaggio Java;
* inserire sufficienti commenti (anche per Javadoc) e annotazioni;
* gestione delle eccezioni;
* usare i file o database;

E' possibile costruire l'applicazione standalone con supporto grafico tramite l'utilizzo di strumenti per la realizzazione di interfacce grafiche presenti in molti IDE (GUI Designer in IntelliJ e
WindowsBuilder in Eclipse) oppure utilizzare tools compatibili con JavaFx come Scene Builder (compatibile con gli IDE).

## Funzionalita implementate

Modalita amministratore:
* visualizzazione elenco abbonati e filtro per numero
* inserimento promozioni
* visualizzazione utilizzo servizi (chiamate, sms, dati, promozioni)

Modalita abbonato:
* chiamate, sms, traffico dati
* adesione promozioni
* pagamento con contanti, carta, bancomat
* storico pagamenti mensili con stato (conto fisso con addebito a fine mese)

## Design pattern usati

Pattern GoF:

* Singleton: [patterns.singleton.AuthenticationService](src/main/java/patterns/singleton/AuthenticationService.java), [patterns.singleton.UserSession](src/main/java/patterns/singleton/UserSession.java), [patterns.singleton.DatabaseManager](src/main/java/patterns/singleton/DatabaseManager.java), [patterns.singleton.DatabaseConnectionManager](src/main/java/patterns/singleton/DatabaseConnectionManager.java)
* Simple Factory: [patterns.factory.AbbonatoFactory](src/main/java/patterns/factory/AbbonatoFactory.java)
* Builder: [model.Abbonato.Builder](src/main/java/model/Abbonato.java)
* Strategy: [patterns.strategy.PaymentStrategy](src/main/java/patterns/strategy/PaymentStrategy.java) con [CashPaymentStrategy](src/main/java/patterns/strategy/CashPaymentStrategy.java), [CardPaymentStrategy](src/main/java/patterns/strategy/CardPaymentStrategy.java), [BancomatPaymentStrategy](src/main/java/patterns/strategy/BancomatPaymentStrategy.java) e contesto [PaymentContext](src/main/java/patterns/strategy/PaymentContext.java)
* Command (UI): [patterns.command.ui.PaymentCommand](src/main/java/patterns/command/ui/PaymentCommand.java) con i command concreti per i pagamenti in [src/main/java/patterns/command/ui](src/main/java/patterns/command/ui)
* Command (DB): [patterns.command.db.DatabaseCommand](src/main/java/patterns/command/db/DatabaseCommand.java) con i command concreti in [src/main/java/patterns/command/db](src/main/java/patterns/command/db)
* Template Method: [patterns.command.ui.AbstractPaymentCommand](src/main/java/patterns/command/ui/AbstractPaymentCommand.java) con algoritmo `execute()` e passi demandati ai command concreti
* State: [patterns.state.Pagamento](src/main/java/patterns/state/Pagamento.java) con stati in [src/main/java/patterns/state/impl](src/main/java/patterns/state/impl)
* Facade: [patterns.facade.AuthFacade](src/main/java/patterns/facade/AuthFacade.java), [patterns.facade.DatabaseFacade](src/main/java/patterns/facade/DatabaseFacade.java)
* Proxy: [patterns.proxy.TelecomRepositoryProxy](src/main/java/patterns/proxy/TelecomRepositoryProxy.java) come intermediario verso [service.TelecomRepository](src/main/java/service/TelecomRepository.java), con controlli base, normalizzazione dei parametri e logging leggero

### Tabella Design Pattern

| Pattern | Ruolo | Evidenza nel codice | Motivazione |
| --- | --- | --- | --- |
| Singleton | Garantisce una sola istanza condivisa per servizi centrali | [patterns.singleton.AuthenticationService](src/main/java/patterns/singleton/AuthenticationService.java), [patterns.singleton.UserSession](src/main/java/patterns/singleton/UserSession.java), [patterns.singleton.DatabaseManager](src/main/java/patterns/singleton/DatabaseManager.java), [patterns.singleton.DatabaseConnectionManager](src/main/java/patterns/singleton/DatabaseConnectionManager.java) | Evita duplicazioni di stato e semplifica l'accesso globale a autenticazione, sessione e DB |
| Simple Factory | Centralizza la creazione di oggetti Abbonato | [patterns.factory.AbbonatoFactory](src/main/java/patterns/factory/AbbonatoFactory.java) | Riduce l accoppiamento tra client e dettagli di istanziazione degli oggetti Abbonato centralizzando la creazione in un unico punto. |
| Builder | Costruisce Abbonato in modo leggibile e sicuro | [model.Abbonato.Builder](src/main/java/model/Abbonato.java) | Utile con oggetti con molti campi, migliora chiarezza e manutenzione |
| Strategy | Seleziona algoritmo di pagamento a runtime | [patterns.strategy.PaymentStrategy](src/main/java/patterns/strategy/PaymentStrategy.java), [patterns/strategy](src/main/java/patterns/strategy), [patterns.strategy.PaymentContext](src/main/java/patterns/strategy/PaymentContext.java) | Permette di aggiungere nuovi metodi di pagamento senza modificare il codice client |
| Command (UI) | Incapsula richieste di pagamento in oggetti comando | [patterns.command.ui.PaymentCommand](src/main/java/patterns/command/ui/PaymentCommand.java), [patterns/command/ui](src/main/java/patterns/command/ui) | Disaccoppia invocazione azione (UI) da esecuzione concreta |
| Command (DB) | Incapsula operazioni JDBC in comandi eseguibili su connessione | [patterns.command.db.DatabaseCommand](src/main/java/patterns/command/db/DatabaseCommand.java), [patterns/command/db](src/main/java/patterns/command/db) | Riduce duplicazione nelle operazioni DB e separa la logica SQL dall'orchestrazione |
| Template Method | Definisce uno scheletro comune di esecuzione per i comandi | [patterns.command.ui.AbstractPaymentCommand](src/main/java/patterns/command/ui/AbstractPaymentCommand.java) | Uniforma il flusso dei comandi e delega i passi variabili alle sottoclassi |
| State | Rappresenta il comportamento in base allo stato del pagamento | [patterns.state.Pagamento](src/main/java/patterns/state/Pagamento.java), [patterns/state/impl](src/main/java/patterns/state/impl) | Elimina controlli sparsi su stringhe stato e rende esplicite le transizioni |
| Facade | Espone punti unici verso sottosistemi applicativi | [patterns.facade.AuthFacade](src/main/java/patterns/facade/AuthFacade.java), [patterns.facade.DatabaseFacade](src/main/java/patterns/facade/DatabaseFacade.java) | Semplifica i controller e l'avvio applicativo riducendo la dipendenza da più servizi interni |
| Proxy | Interpone un livello tra client e repository reale | [patterns.proxy.TelecomRepositoryProxy](src/main/java/patterns/proxy/TelecomRepositoryProxy.java) | Consente di introdurre controlli, normalizzazione e logging senza cambiare i client |

### Polimorfismo

Il comportamento dei conti e gestito tramite polimorfismo su interfaccia `Conto`.

* [model/conto](src/main/java/model/conto)
* [model.conto.Conto](src/main/java/model/conto/Conto.java)
* [model.conto.ContoRicaricabile](src/main/java/model/conto/ContoRicaricabile.java)
* [model.conto.ContoFisso](src/main/java/model/conto/ContoFisso.java)

Pattern architetturali/supporto presenti nel progetto:
* Repository: [service.TelecomRepository](src/main/java/service/TelecomRepository.java)
* Polimorfismo: gestione astratta dei conti tramite interfaccia [model.conto.Conto](src/main/java/model/conto/Conto.java)
* MVC: file FXML in [src/main/resources/view](src/main/resources/view) collegati ai controller in [src/main/java/controller](src/main/java/controller)
* Observer / binding JavaFX: uso di `ObservableList` e `TableView` in [src/main/java/controller/ClienteController.java](src/main/java/controller/ClienteController.java) e [src/main/java/controller/AdminController.java](src/main/java/controller/AdminController.java)

### Scelte di modellazione (non GoF)

* Enum `TipoPiano`: [model.TipoPiano](src/main/java/model/TipoPiano.java)
	* Motivazione: rappresenta in modo type-safe i piani tariffari, evita stringhe "magiche" e riduce errori a runtime.

### Documentazione Architetturale

Per comprendere la logica polimorfica dei conti e l'integrazione con gli altri pattern, consultare:
* [ARCHITETTURA_CONTI_POLIMORFICI.java](ARCHITETTURA_CONTI_POLIMORFICI.java) - Spiegazione completa (360 righe) dell'architettura
* [src/main/java/controller/ClienteController.java](src/main/java/controller/ClienteController.java) - Implementazione reale del flusso cliente con integrazione dei pattern

### Migliorie SOLID applicate (SRP)

Per ridurre le responsabilita di [src/main/java/controller/ClienteController.java](src/main/java/controller/ClienteController.java) sono stati estratti componenti dedicati:

* Gestione alert UI in [src/main/java/service/AlertManager.java](src/main/java/service/AlertManager.java)
* Validazione input in [src/main/java/service/FormInputValidator.java](src/main/java/service/FormInputValidator.java)
* Formattazione valori UI in [src/main/java/service/UIFormatsService.java](src/main/java/service/UIFormatsService.java)
* Configurazione tabella storico pagamenti in [src/main/java/controller/payment/StoricoPagamentiTableConfigurator.java](src/main/java/controller/payment/StoricoPagamentiTableConfigurator.java)
* Gestione dialog pagamenti in [src/main/java/controller/payment/PaymentDialogFactory.java](src/main/java/controller/payment/PaymentDialogFactory.java)
* Gestione dettagli storico in [src/main/java/controller/StorageDetailsViewController.java](src/main/java/controller/StorageDetailsViewController.java)
* Navigazione logout/login in [src/main/java/controller/LoginNavigator.java](src/main/java/controller/LoginNavigator.java)
* Logica dati e business cliente in [src/main/java/service/ClienteDataService.java](src/main/java/service/ClienteDataService.java), [src/main/java/service/UsageRegistrationService.java](src/main/java/service/UsageRegistrationService.java), [src/main/java/service/PromotionService.java](src/main/java/service/PromotionService.java)

Questo refactor mantiene invariato il comportamento funzionale ma migliora separazione delle responsabilita, manutenibilita e testabilita.

## Persistenza dati (JDBC)

L'applicazione usa JDBC con database relazionale locale SQLite.

- Driver: `org.xerial:sqlite-jdbc`
- File database: `parth.db` (creato automaticamente alla prima esecuzione)
- Tabelle principali: `abbonato`, `piano_tariffario`, `promozione`, `abbonato_promozione`, `utilizzo`, `pagamenti`
- Inizializzazione schema e seed dati: [src/main/java/patterns/singleton/DatabaseManager.java](src/main/java/patterns/singleton/DatabaseManager.java)
- Accesso ai dati applicativi: [src/main/java/service/TelecomRepository.java](src/main/java/service/TelecomRepository.java)
- Modello storico pagamenti: [src/main/java/patterns/state/Pagamento.java](src/main/java/patterns/state/Pagamento.java)

## Interfacce principali

- Login: [src/main/resources/view/login.fxml](src/main/resources/view/login.fxml)
- Admin: [src/main/resources/view/admin.fxml](src/main/resources/view/admin.fxml)
- Cliente (include tab storico pagamenti): [src/main/resources/view/cliente.fxml](src/main/resources/view/cliente.fxml)
- Registrazione: [src/main/resources/view/register.fxml](src/main/resources/view/register.fxml)


## Avvio

Da terminale, nella root del progetto:

```bash
mvn -q -DskipTests compile && mvn -q -DskipTests javafx:run
```
