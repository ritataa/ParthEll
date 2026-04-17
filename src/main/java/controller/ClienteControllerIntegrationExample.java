package controller;

/**
 * FRAMMENTO DI CODICE PER INTEGRAZIONE NEL ClienteController
 * 
 * Questo frammento mostra come usare il pattern Polimorfismo con il Conto
 * per gestire l'acquisto di una promozione.
 * 
 * La logica è centrata sul contratto Conto:
 * - Se il conto richiede pagamento immediato: apri il dialog di pagamento (via Strategy)
 * - Se il conto permette addebito diretto: scalare dal saldo e confermare
 * 
 * Questo esempio può essere aggiunto al ClienteController.
 */
public class ClienteControllerIntegrationExample {

    /*
    // ========== METODO DA AGGIUNGERE AL ClienteController ==========

    /**
     * Gestisce l'acquisto di una promozione con logica polimorfica basata sul Conto.
     * 
     * Flusso:
     * 1. Carica il conto dell'abbonato loggato
     * 2. Chiede al conto se richiedePagamentoImmediato(costo_promo)
     * 3. Se false: addebita dal saldo, mostra Alert di successo
     * 4. Se true: apre il dialog di pagamento (Command + Strategy)
     * 
     * @param promo la promozione selezionata dall'utente
     */
    /*
    @FXML
    public void handleAcquistoPromozione(Promozione promo) {
        String emailCorrente = UserSession.getInstance().getCurrentEmail();

        // Step 1: Recupera l'abbonato (contiene il Conto polimorfco)
        model.Abbonato abbonato = repository.findAbbonatoByEmail(emailCorrente);
        if (abbonato == null) {
            alertManager.show(
                javafx.scene.control.Alert.AlertType.ERROR,
                "Errore",
                "Impossibile trovare i dati dell'abbonato"
            );
            return;
        }

        // Step 2: Accedi al Conto (interfaccia polimorfica)
        model.conto.Conto conto = abbonato.getConto();
        double costPromozione = promo.getCosto();

        // Step 3: Usa il Polimorfismo per decidere il flusso
        if (!conto.richiedePagamentoImmediato(costPromozione)) {
            // Caso A: ContoRicaricabile con saldo sufficiente
            // Addebita direttamente dal saldo
            try {
                conto.addebita(costPromozione);
                
                // Persisti l'aggiornamento del saldo nel database
                repository.updateContoSaldo(emailCorrente, conto.getSaldo());
                
                // Adesione alla promozione
                promotionService.addPromozione(emailCorrente, promo.getNome());
                
                alertManager.show(
                    javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Successo",
                    String.format(
                        "Promozione '%s' acquistata!\nAddebitato %.2f€ dal tuo conto ricaricabile.\nSaldo residuo: %.2f€",
                        promo.getNome(),
                        costPromozione,
                        conto.getSaldo()
                    )
                );
                
                // Aggiorna la UI
                caricaPromozioni();
                updatePagamentiMese();
                
            } catch (IllegalStateException e) {
                alertManager.show(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Errore",
                    "Saldo insufficiente: " + e.getMessage()
                );
            }
        } else {
            // Caso B: ContoFisso oppure ContoRicaricabile con saldo insufficiente
            // Apri il dialog di pagamento (Strategy + Command)
            
            // Usa il PaymentDialogFactory per mostrare le opzioni di pagamento
            // (Cash, Card, Bancomat)
            showPaymentDialog(promo);
        }
    }

    /**
     * Helper: mostra il dialog di pagamento per l'acquisto della promozione.
     * Incapsula la logica di Command + Strategy.
     * 
     * @param promo la promozione da pagare
     */
    /*
    private void showPaymentDialog(Promozione promo) {
        double totale = promo.getCosto();
        
        // Crea un dialog che permette di scegliere il metodo di pagamento
        // e automaticamente esegue il Command associato
        
        // Esempio: mostra un menu con opzioni [Contanti] [Carta] [Bancomat]
        // Quando l'utente sceglie, istanzia il Command corrispondente
        
        // Pseudo-codice:
        // if (utente sceglie "Contanti") {
        //     PaymentCommand cmd = new CashPaymentCommand(this, totale);
        //     cmd.execute(); // Template Method: beforeExecute -> doExecute -> afterExecute
        // } else if (utente sceglie "Carta") {
        //     PaymentCommand cmd = new CardPaymentCommand(this, totale);
        //     cmd.execute();
        // } else if (utente sceglie "Bancomat") {
        //     PaymentCommand cmd = new BancomatPaymentCommand(this, totale);
        //     cmd.execute();
        // }
        
        // Mostra il dialog factory
        paymentDialogFactory.showPaymentOptions(
            welcomeLabel.getScene().getWindow(),
            totale,
            promo,
            () -> {
                // Callback: dopo il pagamento confermato
                promotionService.addPromozione(
                    UserSession.getInstance().getCurrentEmail(),
                    promo.getNome()
                );
                alertManager.show(
                    javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Pagamento Confermato",
                    "Promozione acquistata con successo!"
                );
                caricaPromozioni();
                updatePagamentiMese();
            }
        );
    }

    // ========== FINE FRAMMENTO ==========
    */


    /**
     * SPIEGAZIONE DELL'ARCHITETTURA POLIMORFICA:
     *
     * 1. INTERFACCIA CONTO (Astrazione):
     *    - Contratto: richiedePagamentoImmediato(), addebita(), getSaldo()
     *    - Nasconde i dettagli di implementazione specifica
     *
     * 2. IMPLEMENTAZIONI CONCRETE:
     *
     *    a) ContoRicaricabile:
     *       - Ha un saldo interno (prepagato, es. carta ricaricabile €50)
     *       - richiedePagamentoImmediato() = importo > saldo
     *       - addebita() = sottrae dal saldo
     *       - Flusso: se importo ≤ saldo -> pagamento automatico
     *
     *    b) ContoFisso:
     *       - Non ha saldo (pay-as-you-go, es. fatturazione mensile)
     *       - richiedePagamentoImmediato() = sempre true
     *       - addebita() = non supportato (eccezione)
     *       - Flusso: SEMPRE apertura dialog pagamento
     *
     * 3. POLIMORFISMO NEL CONTROLLER:
     *
     *    Conto conto = abbonato.getConto(); // Può essere ContoRicaricabile o ContoFisso
     *
     *    if (!conto.richiedePagamentoImmediato(importo)) {
     *        // Comportamento per ContoRicaricabile con saldo sufficiente
     *        conto.addebita(importo);
     *    } else {
     *        // Comportamento per ContoFisso o saldo insufficiente
     *        // Apri pagamento
     *    }
     *
     *    Il codice del controller NON sa quale tipo concreto è,
     *    dipende solo dalla INTERFACCIA (contratto).
     *
     * 4. PATTERN INTEGRATION:
     *    - Strategy: PaymentStrategy (Cash, Card, Bancomat)
     *    - Command: PaymentCommand (CashPaymentCommand, CardPaymentCommand, BancomatPaymentCommand)
     *    - Polimorfismo: Conto (ContoRicaricabile, ContoFisso)
     *    - Tutti lavorano insieme per una logica coerente e flessibile
     *
     * 5. PRINCIPI SOLID:
     *    - Single Responsibility: ogni classe fa una cosa sola
     *    - Open/Closed: facile aggiungere nuovi tipi di Conto senza modificare il controller
     *    - Liskov Substitution: ContoFisso e ContoRicaricabile sono intercambiabili
     *    - Interface Segregation: Conto ha solo i metodi essenziali
     *    - Dependency Inversion: il controller dipende da Conto (astrazione), non da implementazioni
     */
}
