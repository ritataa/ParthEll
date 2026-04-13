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
* storico pagamenti mensili con stato

## Design pattern usati

Pattern GoF della lista richiesta:

* Singleton: [service.AuthenticationService](src/main/java/service/AuthenticationService.java), [service.UserSession](src/main/java/service/UserSession.java), [service.DatabaseManager](src/main/java/service/DatabaseManager.java)
* Factory Method: [model.AbbonatoFactory](src/main/java/model/AbbonatoFactory.java)
* Builder: [model.Abbonato.Builder](src/main/java/model/Abbonato.java)
* Strategy: [service.payment.PaymentStrategy](src/main/java/service/payment/PaymentStrategy.java) con [CashPaymentStrategy](src/main/java/service/payment/CashPaymentStrategy.java), [CardPaymentStrategy](src/main/java/service/payment/CardPaymentStrategy.java), [BancomatPaymentStrategy](src/main/java/service/payment/BancomatPaymentStrategy.java)
* Command: [controller.command.PaymentCommand](src/main/java/controller/command/PaymentCommand.java) con i command concreti per i pagamenti in [src/main/java/controller/command](src/main/java/controller/command)
* Template Method: [controller.command.AbstractPaymentCommand](src/main/java/controller/command/AbstractPaymentCommand.java) con algoritmo `execute()` e passi demandati ai command concreti
* State: [model.Pagamento](src/main/java/model/Pagamento.java) con stati in [src/main/java/model/state](src/main/java/model/state)
* Facade: [service.AuthFacade](src/main/java/service/AuthFacade.java)
* Proxy: [service.TelecomRepositoryProxy](src/main/java/service/TelecomRepositoryProxy.java) come intermediario verso [service.TelecomRepository](src/main/java/service/TelecomRepository.java)

### Tabella per discussione orale

| Pattern | Ruolo | Evidenza nel codice | Motivazione |
| --- | --- | --- | --- |
| Singleton | Garantisce una sola istanza condivisa per servizi centrali | [service.AuthenticationService](src/main/java/service/AuthenticationService.java), [service.UserSession](src/main/java/service/UserSession.java), [service.DatabaseManager](src/main/java/service/DatabaseManager.java) | Evita duplicazioni di stato e semplifica l'accesso globale a autenticazione, sessione e DB |
| Factory Method | Centralizza la creazione di oggetti Abbonato | [model.AbbonatoFactory](src/main/java/model/AbbonatoFactory.java) | Riduce accoppiamento tra chiamanti e costruttori concreti |
| Builder | Costruisce Abbonato in modo leggibile e sicuro | [model.Abbonato.Builder](src/main/java/model/Abbonato.java) | Utile con oggetti con molti campi, migliora chiarezza e manutenzione |
| Strategy | Seleziona algoritmo di pagamento a runtime | [service.payment.PaymentStrategy](src/main/java/service/payment/PaymentStrategy.java), [service/payment](src/main/java/service/payment) | Permette di aggiungere nuovi metodi di pagamento senza modificare il codice client |
| Command | Incapsula richieste di pagamento in oggetti comando | [controller.command.PaymentCommand](src/main/java/controller/command/PaymentCommand.java), [controller/command](src/main/java/controller/command) | Disaccoppia invocazione azione (UI) da esecuzione concreta |
| Template Method | Definisce uno scheletro comune di esecuzione per i comandi | [controller.command.AbstractPaymentCommand](src/main/java/controller/command/AbstractPaymentCommand.java) | Uniforma il flusso dei comandi e delega i passi variabili alle sottoclassi |
| State | Rappresenta il comportamento in base allo stato del pagamento | [model.Pagamento](src/main/java/model/Pagamento.java), [model/state](src/main/java/model/state) | Elimina controlli sparsi su stringhe stato e rende esplicite le transizioni |
| Facade | Espone un punto unico per login/logout/sessione | [service.AuthFacade](src/main/java/service/AuthFacade.java) | Semplifica i controller e riduce la dipendenza da più servizi interni |
| Proxy | Interpone un livello tra client e repository reale | [service.TelecomRepositoryProxy](src/main/java/service/TelecomRepositoryProxy.java) | Consente di introdurre controlli/logging senza cambiare i client |

Pattern architetturali/supporto presenti nel progetto:
* Repository: [service.TelecomRepository](src/main/java/service/TelecomRepository.java)
* MVC: file FXML in [src/main/resources/view](src/main/resources/view) collegati ai controller in [src/main/java/controller](src/main/java/controller)
* Observer / binding JavaFX: uso di `ObservableList` e `TableView` in [src/main/java/controller/ClienteController.java](src/main/java/controller/ClienteController.java) e [src/main/java/controller/AdminController.java](src/main/java/controller/AdminController.java)

## Persistenza dati (JDBC)

L'applicazione usa JDBC con database relazionale locale SQLite.

- Driver: `org.xerial:sqlite-jdbc`
- File database: `parth.db` (creato automaticamente alla prima esecuzione)
- Tabelle principali: `abbonato`, `piano_tariffario`, `promozione`, `abbonato_promozione`, `utilizzo`, `pagamenti`
- Inizializzazione schema e seed dati: [src/main/java/service/DatabaseManager.java](src/main/java/service/DatabaseManager.java)
- Accesso ai dati applicativi: [src/main/java/service/TelecomRepository.java](src/main/java/service/TelecomRepository.java)
- Modello storico pagamenti: [src/main/java/model/Pagamento.java](src/main/java/model/Pagamento.java)

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
