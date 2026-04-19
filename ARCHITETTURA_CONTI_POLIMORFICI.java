/**
 * ====================================================================
 * ARCHITETTURA POLIMORFICA DEI CONTI - SUMMARY COMPLETO
 * ====================================================================
 *
 * Software Architect: Design Pattern GoF - Polimorfismo
 * Progetto: ParthEll Telecom (JavaFX)
 * Integrazione con: Strategy (Pagamenti), Command (UI), State (Pagamenti)
 *
 * ====================================================================
 * 1. GERARCHIA DEI CONTI (Package: model.conto)
 * ====================================================================
 *
 * INTERFACCIA: Conto (contratto polimorfico)
 * ─────────────────────────────────────────────────────────────────
 * 
 * + richiedePagamentoImmediato(double importo): boolean
 *   → Determina se il pagamento deve avvenire ORA
 *   → Valore di default: dipende dal tipo di conto
 *
 * + addebita(double importo): void
 *   → Scala l'importo dal saldo interno
 *   → ContoFisso: lancia UnsupportedOperationException
 *
 * + getSaldo(): double
 *   → Restituisce il saldo disponibile
 *   → ContoFisso: sempre 0.0
 *   → ContoRicaricabile: saldo interno
 *
 * + getTipo(): String
 *   → "Fisso" oppure "Ricaricabile" per UI e logging
 *
 * ────────────────────────────────────────────────────────────────
 * IMPLEMENTAZIONE 1: ContoRicaricabile (Prepagato)
 * ────────────────────────────────────────────────────────────────
 *
 * Scenario: Cliente con carta ricaricabile da 50€
 *
 * Campo: private double saldo
 *
 * Logica:
 * ───────
 * • richiedePagamentoImmediato(importo):
 *   - return importo > saldo;
 *   - Se importo ≤ saldo: NO pagamento immediato
 *   - Se importo > saldo: SÌ, pagamento richiesto
 *
 * • addebita(importo):
 *   - Verifica se importo ≤ saldo
 *   - Se sì: saldo -= importo (successo)
 *   - Se no: lancia IllegalStateException
 *
 * • Metodo bonus: ricarica(importo)
 *   - Incrementa il saldo (operazione di top-up)
 *
 * Esempio di flusso:
 * ──────────────────
 * Saldo = 50€, Promozione costa 30€
 * → richiedePagamentoImmediato(30) → false
 * → Addebito diretto: saldo = 20€
 * → Alert: "Promozione acquistata! Saldo residuo: 20€"
 *
 * Saldo = 20€, Promozione costa 30€
 * → richiedePagamentoImmediato(30) → true
 * → Apri dialog pagamento (Cash, Card, Bancomat)
 *
 * ────────────────────────────────────────────────────────────────
 * IMPLEMENTAZIONE 2: ContoFisso (Pay-as-you-go)
 * ────────────────────────────────────────────────────────────────
 *
 * Scenario: Cliente con piano postpagato (fatturazione mensile)
 *
 * Campo: nessuno (non ha saldo interno)
 *
 * Logica:
 * ───────
 * • richiedePagamentoImmediato(importo):
 *   - return true; // SEMPRE
 *   - Ogni acquisto richiede pagamento immediato
 *
 * • addebita(importo):
 *   - throw UnsupportedOperationException
 *   - Il pagamento deve avvenire tramite Strategy, non qui
 *
 * • getSaldo():
 *   - return 0.0; // Nessun saldo prepagato
 *
 * Esempio di flusso:
 * ──────────────────
 * Promozione costa 15€
 * → richiedePagamentoImmediato(15) → true (sempre)
 * → Apri dialog pagamento obbligatorio
 * → Utente sceglie: [Contanti] [Carta] [Bancomat]
 * → Invoca Command corrispondente e Strategy di pagamento
 *
 * ====================================================================
 * 2. INTEGRAZIONE IN ABBONATO
 * ====================================================================
 *
 * CLASSE AGGIORNATA: model.Abbonato
 *
 * Campo aggiunto:
 * ───────────────
 * private Conto conto;
 *
 * Costruttori:
 * ────────────
 * • Abbonato()
 *   → conto = new ContoFisso() [default]
 *
 * • Abbonato(String nome, String cognome, ...)
 *   → conto = new ContoFisso() [default]
 *
 * • Abbonato(String nome, ..., Conto conto)
 *   → conto = conto != null ? conto : new ContoFisso()
 *
 * Builder Pattern:
 * ────────────────
 * Abbonato abbonato = Abbonato.builder()
 *     .nome("Anna")
 *     .cognome("Rossi")
 *     .email("anna@gmail.com")
 *     .pianoTariffario("plus")
 *     .conto(new ContoRicaricabile(50.0)) // Prepagato 50€
 *     .build();
 *
 * Accessori:
 * ──────────
 * • getConto(): Conto
 * • setConto(Conto conto): void
 *
 * ====================================================================
 * 3. LOGICA POLIMORFICA NEL CONTROLLER
 * ====================================================================
 *
 * METODO: ClienteController.handleAcquistoPromozione(Promozione promo)
 *
 * Pseudo-codice:
 * ───────────────
 *
 *     1. Recupera l'abbonato loggato
 *     2. Accedi al Conto (polimorfismo!)
 *     3. Chiama richiedePagamentoImmediato(promo.getCosto())
 *
 *     if (!conto.richiedePagamentoImmediato(costo)) {
 *         // RAMO A: Addebito diretto dal saldo
 *         try {
 *             conto.addebita(costo);
 *             updateDatabase(saldo);
 *             showAlert("Acquistato! Saldo: " + conto.getSaldo() + "€");
 *         } catch (IllegalStateException e) {
 *             showAlert("Saldo insufficiente: " + e.getMessage());
 *         }
 *     } else {
 *         // RAMO B: Pagamento immediato richiesto
 *         showPaymentDialog(promo);
 *         // Invoca Command (Cash/Card/Bancomat)
 *         // Che esegue la Strategy di pagamento
 *     }
 *
 * Flow dettagliato:
 * ──────────────────
 *
 * SCENARIO 1: ContoRicaricabile, saldo sufficiente
 * ─────────────────────────────────────────────────
 * • Abbonato: ContoRicaricabile con saldo 50€
 * • Azione: Acquista promozione (30€)
 * • Flusso:
 *   1. conto.richiedePagamentoImmediato(30) → false (30 ≤ 50)
 *   2. conto.addebita(30) → OK, saldo diventa 20€
 *   3. Alert: "Acquistato! Saldo: 20€"
 *   4. Update UI: caricaPromozioni(), updatePagamentiMese()
 *
 * SCENARIO 2: ContoRicaricabile, saldo insufficiente
 * ──────────────────────────────────────────────────
 * • Abbonato: ContoRicaricabile con saldo 15€
 * • Azione: Acquista promozione (30€)
 * • Flusso:
 *   1. conto.richiedePagamentoImmediato(30) → true (30 > 15)
 *   2. showPaymentDialog(promo)
 *   3. Utente seleziona: [Carta]
 *   4. Esegui: new CardPaymentCommand(this, 30).execute()
 *   5. Template Method: beforeExecute() → doExecute() → afterExecute()
 *   6. Strategy: CardPaymentStrategy.pay(30)
 *   7. Alert: "Pagamento confermato con Carta!"
 *
 * SCENARIO 3: ContoFisso (pay-as-you-go)
 * ───────────────────────────────────────
 * • Abbonato: ContoFisso
 * • Azione: Acquista promozione (15€)
 * • Flusso:
 *   1. conto.richiedePagamentoImmediato(15) → true (sempre)
 *   2. showPaymentDialog(promo)
 *   3. Utente seleziona: [Contanti]
 *   4. Esegui: new CashPaymentCommand(this, 15).execute()
 *   5. Strategy: CashPaymentStrategy.pay(15)
 *   6. Alert: "Pagamento in contanti registrato!"
 *
 * ====================================================================
 * 4. PATTERN INTEGRATION (Come lavora insieme)
 * ====================================================================
 *
 * Pattern coinvolti:
 * ──────────────────
 *
 * A) POLIMORFISMO (Conto)
 *    ├─ Interfaccia: Conto
 *    ├─ Implementazioni: ContoRicaricabile, ContoFisso
 *    └─ Vantaggi: Decisioni di flusso basate sul tipo concreto,
 *       senza accoppiamento nel controller
 *
 * B) STRATEGY (Pagamenti)
 *    ├─ Interfaccia: PaymentStrategy
 *    ├─ Implementazioni: CashPaymentStrategy, CardPaymentStrategy, BancomatPaymentStrategy
 *    ├─ Contesto: PaymentContext
 *    └─ Vantaggi: Algoritmo di pagamento selezionabile a runtime
 *
 * C) COMMAND (UI)
 *    ├─ Interfaccia: PaymentCommand
 *    ├─ Base Template: AbstractPaymentCommand
 *    ├─ Implementazioni: CashPaymentCommand, CardPaymentCommand, BancomatPaymentCommand
 *    └─ Vantaggi: Incapsula la richiesta di pagamento in un oggetto
 *
 * D) STATE (Pagamenti)
 *    ├─ Interfaccia: PagamentoState
 *    ├─ Implementazioni: DaPagareState, PagamentoConfermatoState
 *    └─ Vantaggi: Rappresenta il ciclo di vita di un pagamento
 *
 * Sequenza di esecuzione:
 * ───────────────────────
 *
 *     ClienteController
 *          ↓
 *     [Polimorfismo] Chiama conto.richiedePagamentoImmediato()
 *          ↓
 *     ┌────────────────────────────────────┐
 *     │ false (saldo sufficiente)           │ true (pagamento richiesto)
 *     │ ContoRicaricabile OK                │ ContoFisso o saldo insufficiente
 *     ↓                                     ↓
 *  conto.addebita()              showPaymentDialog()
 *  Alert di successo                   ↓
 *  Update saldo DB          [Command Pattern] Utente sceglie metodo
 *                                   ↓
 *                           CashPaymentCommand.execute()
 *                                   ↓
 *                           [Template Method] AbstractPaymentCommand
 *                              beforeExecute() → doExecute() → afterExecute()
 *                                   ↓
 *                           [Strategy] CashPaymentStrategy.pay()
 *                                   ↓
 *                           Alert di successo
 *                           Update DB
 *
 * ====================================================================
 * 5. PRINCIPI SOLID APPLICATI
 * ====================================================================
 *
 * S - Single Responsibility
 * ──────────────────────────
 * • Conto: decide se pagamento immediato + gestisce saldo
 * • PaymentStrategy: esegue il pagamento (un algoritmo per tipo)
 * • PaymentCommand: incapsula una richiesta di pagamento
 * • ClienteController: coordina il flusso, non decide algoritmo
 *
 * O - Open/Closed
 * ───────────────
 * • Nuovi tipi di Conto? Aggiungi ContoRicaricabileAvanzato
 *   senza modificare controller
 * • Nuove strategie di pagamento? Aggiungi NuovaPaymentStrategy
 *   senza toccare il resto
 *
 * L - Liskov Substitution
 * ──────────────────────
 * • ContoRicaricabile e ContoFisso sono intercambiabili
 *   in qualsiasi posto usi Conto
 * • Il controller non sa quale tipo è, solo che implementa Conto
 *
 * I - Interface Segregation
 * ──────────────────────────
 * • Conto ha solo metodi essenziali (no getter/setter externi inutili)
 * • PaymentStrategy ha solo il metodo pay()
 * • PaymentCommand ha solo execute()
 *
 * D - Dependency Inversion
 * ────────────────────────
 * • ClienteController dipende da Conto (astrazione)
 * • NON da ContoRicaricabile o ContoFisso (concrezioni)
 * • Inversione del controllo: il controller non crea i conti,
 *   li riceve dalla "dependency injection" (tramite Abbonato builder)
 *
 * ====================================================================
 * 6. FILE CREATI
 * ====================================================================
 *
 * src/main/java/model/conto/
 * ├─ Conto.java (interfaccia)
 * ├─ ContoRicaricabile.java (implementazione prepagata)
 * └─ ContoFisso.java (implementazione pay-as-you-go)
 *
 * Modificati:
 * ├─ src/main/java/model/Abbonato.java
 * │  (aggiunto campo Conto, costruttori, getter/setter, Builder)
 * │
 * └─ src/main/java/controller/ClienteController.java
 *    (integrazione del flusso cliente con logica polimorfica dei conti)
 *
 * ====================================================================
 * 7. COME USARE IN PRATICA
 * ====================================================================
 *
 * Creazione abbonati:
 * ───────────────────
 *
 * // Cliente con conto ricaricabile
 * Abbonato abbonato1 = Abbonato.builder()
 *     .nome("Anna")
 *     .email("anna@gmail.com")
 *     .pianoTariffario("plus")
 *     .conto(new ContoRicaricabile(50.0))
 *     .build();
 *
 * // Cliente con conto fisso (default)
 * Abbonato abbonato2 = Abbonato.builder()
 *     .nome("Sara")
 *     .email("sara@gmail.com")
 *     .pianoTariffario("base")
 *     // .conto() non specificato → ContoFisso di default
 *     .build();
 *
 * Nel controller:
 * ───────────────
 *
 * // Accesso polimorfico al conto
 * Conto conto = abbonato.getConto();
 * System.out.println(conto.getTipo());  // "Ricaricabile" o "Fisso"
 * System.out.println(conto.getSaldo()); // 50.0 o 0.0
 *
 * // Decisione di flusso basata sul polimorfismo
 * if (!conto.richiedePagamentoImmediato(15.0)) {
 *     // ContoRicaricabile con saldo ≥ 15€
 *     conto.addebita(15.0);
 * } else {
 *     // ContoFisso o saldo insufficiente
 *     // → Apri dialog pagamento
 * }
 *
 * ====================================================================
 * 8. EXTENSIBILITÀ FUTURA
 * ====================================================================
 *
 * Nuovi tipi di Conto:
 * ────────────────────
 * • ContoAziendale: logiche speciali per clienti business
 * • ContoStudenti: sconto speciale senza saldo
 * • ContoPromo: con bonus automatico al raggiungimento soglie
 *
 * Basta implementare Conto e il controller rimane inalterato!
 *
 * ====================================================================
 */
