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

## Design pattern usati

Il progetto include più di sei pattern riconoscibili nel codice:

* Singleton: [service.AuthenticationService](src/main/java/service/AuthenticationService.java), [service.UserSession](src/main/java/service/UserSession.java), [service.DatabaseManager](src/main/java/service/DatabaseManager.java)
* Factory Method: [model.AbbonatoFactory](src/main/java/model/AbbonatoFactory.java)
* Builder: [model.Abbonato.Builder](src/main/java/model/Abbonato.java)
* Strategy: [service.payment.PaymentStrategy](src/main/java/service/payment/PaymentStrategy.java) con [CashPaymentStrategy](src/main/java/service/payment/CashPaymentStrategy.java), [CardPaymentStrategy](src/main/java/service/payment/CardPaymentStrategy.java), [BancomatPaymentStrategy](src/main/java/service/payment/BancomatPaymentStrategy.java)
* Command: [controller.command.PaymentCommand](src/main/java/controller/command/PaymentCommand.java) con i command concreti per i pagamenti in [src/main/java/controller/command](src/main/java/controller/command)
* Facade: [service.AuthFacade](src/main/java/service/AuthFacade.java)
* Repository: [service.TelecomRepository](src/main/java/service/TelecomRepository.java)
* MVC: file FXML in [src/main/resources/view](src/main/resources/view) collegati ai controller in [src/main/java/controller](src/main/java/controller)
* Observer / binding JavaFX: uso di `ObservableList` e `TableView` in [controller.ClienteController](src/main/java/controller/ClienteController.java) e [controller.AdminController](src/main/java/controller/AdminController.java)

## Persistenza dati (JDBC)

L'applicazione usa JDBC con database relazionale locale SQLite.

- Driver: `org.xerial:sqlite-jdbc`
- File database: `parth.db` (creato automaticamente alla prima esecuzione)
- Inizializzazione schema e seed dati: `service.DatabaseManager`
- Accesso ai dati applicativi: `service.TelecomRepository`


## Avvio

per runnarlo, nel terminale scrivere: mvn -q -DskipTests compile && mvn -q -DskipTests javafx:run
