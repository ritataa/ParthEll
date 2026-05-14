package controller.payment;

import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import service.AlertManager;
import service.FormInputValidator;
import service.UIFormatsService;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternità e manutenzione della classe.
 * @param: Definisce i vincoli di input richiesti dal metodo per un uso corretto.
 * @return: Esplicita l'output garantito o l'assenza di risultato, così il Client sa cosa può usare.
 */



/**
    Rappresenta il punto di incontro tra l'interfaccia grafica e la logica di business. Utilizza: 
    - Factory Pattern per la creazione dei componenti UI (Stage e Scene), 
    - Command Pattern per gestire gli eventi dei pulsanti tramite handler inner-class (CalcolaRestoHandler, ConfermaContantiHandler, etc.),  
    - Strategy Pattern per delegare l'esecuzione dei pagamenti a classi specializzate (CashPaymentStrategy, CardPaymentStrategy, BancomatPaymentStrategy).
 * 
 * NOTE: Questa NON è una vera implementazione del Factory Pattern (come in patterns/factory).
 * È un "UI Factory" o "Dialog Factory" perché:
 *   - Crea Stage e Scene JavaFX, NON istanze di oggetti di business logic (Pagamento, ContoRicaricabile, ecc.)
 *   - Non ritorna interfacce astratte, ma componenti UI concrete
 *   - Il suo scopo è centralizzare la costruzione di dialog, non astrarre creazioni di entità di dominio
 * È più simile a un Builder di UI che a un vero Factory Pattern. Posizionato in "controller/payment"
 * per motivi organizzativi (UI controller), non in "patterns/factory" per questa ragione.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class PaymentDialogFactory {

    // Servizi condivisi per formattazione UI, validazione input e alert utente.
    private final UIFormatsService uiFormatsService;
    private final FormInputValidator validator;
    private final AlertManager alertManager;

    /**
     * Crea la factory con servizi condivisi per formato, validazione e alert.
     * I parametri non devono essere null perché vengono usati da tutti i dialog.
     *
     * @param uiFormatsService servizio di formattazione valuta; non deve essere null.
     * @param validator servizio di validazione input; non deve essere null.
     * @param alertManager servizio di visualizzazione alert; non deve essere null.
     */
    public PaymentDialogFactory(
        UIFormatsService uiFormatsService,
        FormInputValidator validator,
        AlertManager alertManager
    ) {
        this.uiFormatsService = uiFormatsService;
        this.validator = validator;
        this.alertManager = alertManager;
    }

    /**
     * Mostra il dialog di pagamento in contanti e collega i pulsanti alle azioni locali.
     * Il callback esterno viene invocato solo dopo una conferma valida.
     *
     * @param owner finestra proprietaria del dialog; può essere null se non disponibile.
     * @param totale importo da pagare; deve essere un valore coerente con il saldo richiesto.
     * @param confermaPagamentoSelezionato callback che conferma il pagamento sullo storico; non deve essere null.
     */
    public void showCashDialog(Window owner, double totale, Supplier<Boolean> confermaPagamentoSelezionato) {
        // Costruzione dialog contanti: calcolo resto, conferma pagamento e chiusura.
        Stage dialog = createDialog(owner, "Pagamento in Contanti");

        Label totaleLabel = new Label("Totale: " + uiFormatsService.formatEuro(totale));
        TextField contantiField = new TextField();
        contantiField.setPromptText("Importo ricevuto");
        Label restoLabel = new Label("Resto: 0.00 EUR");

        Button calcolaRestoButton = new Button("Calcola Resto");
        calcolaRestoButton.setOnAction(new CalcolaRestoHandler(contantiField, restoLabel, totale));

        Button confermaButton = new Button("Conferma Pagamento");
        confermaButton.setOnAction(new ConfermaContantiHandler(contantiField, totale, confermaPagamentoSelezionato, dialog));

        Button annullaButton = new Button("Annulla");
        annullaButton.setOnAction(new ChiudiDialogHandler(dialog));

        HBox pulsanti = new HBox(10, calcolaRestoButton, confermaButton, annullaButton);
        VBox root = new VBox(12, totaleLabel, contantiField, restoLabel, pulsanti);
        root.setStyle("-fx-padding: 16;");

        dialog.setScene(new Scene(root, 460, 200));
        dialog.showAndWait();
    }

    /**
     * Mostra il dialog di pagamento con carta e valida i dati inseriti prima della conferma.
     * Il pagamento prosegue solo se i dati della carta sono formalmente corretti.
     *
     * @param owner finestra proprietaria del dialog; può essere null se non disponibile.
     * @param totale importo da pagare; deve essere coerente con la transazione richiesta.
     * @param confermaPagamentoSelezionato callback che conferma il pagamento sullo storico; non deve essere null.
     */
    public void showCardDialog(Window owner, double totale, Supplier<Boolean> confermaPagamentoSelezionato) {
        System.out.println("[ATTO 4 - 4. PAYMENT DIALOG FACTORY] Apro il dialog Carta con importo da confermare.");
        // Dialog carta: raccolta dati, validazione e conferma transazione.
        Stage dialog = createDialog(owner, "Pagamento con Carta");

        Label totaleLabel = new Label("Totale: " + uiFormatsService.formatEuro(totale));
        TextField intestatarioField = new TextField();
        intestatarioField.setPromptText("Nome Intestatario");
        TextField numeroCartaField = new TextField();
        numeroCartaField.setPromptText("Numero Carta (16 cifre)");
        TextField scadenzaField = new TextField();
        scadenzaField.setPromptText("Scadenza (MM/AA)");
        TextField cvvField = new TextField();
        cvvField.setPromptText("CVV (3 cifre)");

        Button pagaOraButton = new Button("Paga Ora");
        pagaOraButton.setOnAction(new ConfermaCartaHandler(
            intestatarioField,
            numeroCartaField,
            scadenzaField,
            cvvField,
            totale,
            confermaPagamentoSelezionato,
            dialog
        ));

        Button indietroButton = new Button("Indietro");
        indietroButton.setOnAction(new ChiudiDialogHandler(dialog));

        HBox pulsanti = new HBox(10, pagaOraButton, indietroButton);
        VBox root = new VBox(10, totaleLabel, intestatarioField, numeroCartaField, scadenzaField, cvvField, pulsanti);
        root.setStyle("-fx-padding: 16;");

        dialog.setScene(new Scene(root, 440, 300));
        dialog.showAndWait();
    }

    /**
     * Mostra il dialog di pagamento bancomat/POS e abilita il PIN solo dopo la simulazione lettura carta.
     * La conferma esterna viene chiamata solo dopo la validazione del PIN.
     *
     * @param owner finestra proprietaria del dialog; può essere null se non disponibile.
     * @param totale importo da pagare; deve essere coerente con la transazione richiesta.
     * @param confermaPagamentoSelezionato callback che conferma il pagamento sullo storico; non deve essere null.
     */
    public void showBancomatDialog(Window owner, double totale, Supplier<Boolean> confermaPagamentoSelezionato) {
        // Dialog POS: sblocco PIN, validazione pin e autorizzazione addebito.
        Stage dialog = createDialog(owner, "Pagamento Bancomat (POS)");

        Label totaleLabel = new Label("Importo da pagare: " + uiFormatsService.formatEuro(totale));
        Button simulaLetturaButton = new Button("Simula Lettura Carta");
        Label pinLabel = new Label("Inserisci PIN");
        PasswordField pinField = new PasswordField();
        pinField.setDisable(true);

        simulaLetturaButton.setOnAction(new AbilitaPinHandler(pinField));

        Button autorizzaButton = new Button("Autorizza Transazione");
        autorizzaButton.setOnAction(new ConfermaBancomatHandler(pinField, totale, confermaPagamentoSelezionato, dialog));

        Button annullaButton = new Button("Annulla");
        annullaButton.setOnAction(new ChiudiDialogHandler(dialog));

        HBox pinBox = new HBox(8, pinLabel, pinField);
        HBox pulsanti = new HBox(10, autorizzaButton, annullaButton);
        VBox root = new VBox(12, totaleLabel, simulaLetturaButton, pinBox, pulsanti);
        root.setStyle("-fx-padding: 16;");

        dialog.setScene(new Scene(root, 430, 220));
        dialog.showAndWait();
    }

    private Stage createDialog(Window owner, String titolo) {
        // Finestra modale riusabile: centralizza titolo, ownership e blocco dell'interazione esterna.
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(titolo);
        if (owner != null) {
            dialog.initOwner(owner);
        }
        return dialog;
    }

    // Handler riusabile per chiusura dialog da pulsanti Annulla/Indietro.
    private static class ChiudiDialogHandler implements EventHandler<ActionEvent> {
        private final Stage dialog;

        private ChiudiDialogHandler(Stage dialog) {
            this.dialog = dialog;
        }

        // Sicurezza: obbliga Java a verificare che sto davvero implementando handle() dell'interfaccia EventHandler, evitando errori di battitura.
        @Override
        public void handle(ActionEvent event) {
            dialog.close();
        }
    }

    // Calcola il resto nel dialog contanti e aggiorna etichetta di feedback.
    private class CalcolaRestoHandler implements EventHandler<ActionEvent> {
        private final TextField contantiField;
        private final Label restoLabel;
        private final double totale;

        private CalcolaRestoHandler(TextField contantiField, Label restoLabel, double totale) {
            this.contantiField = contantiField;
            this.restoLabel = restoLabel;
            this.totale = totale;
        }

        // Sicurezza: obbliga Java a verificare che sto davvero implementando handle() dell'interfaccia EventHandler, evitando errori di battitura.
        @Override
        public void handle(ActionEvent event) {
            try {
                double ricevuto = Double.parseDouble(contantiField.getText().trim());
                double resto = ricevuto - totale;
                if (resto < 0) {
                    restoLabel.setText("Importo insufficiente: mancano " + String.format("%.2f", Math.abs(resto)) + " EUR");
                    return;
                }
                restoLabel.setText("Resto: " + uiFormatsService.formatEuro(resto));
            } catch (NumberFormatException ex) {
                restoLabel.setText("Inserisci un importo valido.");
            }
        }
    }

    // Conferma pagamento contanti con validazione importo ricevuto.
    private class ConfermaContantiHandler implements EventHandler<ActionEvent> {
        private final TextField contantiField;
        private final double totale;
        private final Supplier<Boolean> confermaPagamentoSelezionato;
        private final Stage dialog;

        private ConfermaContantiHandler(
            TextField contantiField,
            double totale,
            Supplier<Boolean> confermaPagamentoSelezionato,
            Stage dialog
        ) {
            this.contantiField = contantiField;
            this.totale = totale;
            this.confermaPagamentoSelezionato = confermaPagamentoSelezionato;
            this.dialog = dialog;
        }

        // Sicurezza: obbliga Java a verificare che sto davvero implementando handle() dell'interfaccia EventHandler, evitando errori di battitura.
        @Override
        public void handle(ActionEvent event) {
            try {
                double ricevuto = Double.parseDouble(contantiField.getText().trim());
                if (ricevuto < totale) {
                    alertManager.show(Alert.AlertType.WARNING, "Pagamento", "Importo contanti insufficiente.");
                    return;
                }
                if (!confermaPagamentoSelezionato.get()) {
                    return;
                }

                // --- AGGANCIO PATTERN STRATEGY (CONTANTI) ---
                patterns.strategy.PaymentContext context = new patterns.strategy.PaymentContext();
                context.setStrategy(new patterns.strategy.CashPaymentStrategy());
                
                String messaggioConferma = context.executePayment(totale);
                System.out.println(messaggioConferma); 
                // --------------------------------------------

                alertManager.show(Alert.AlertType.INFORMATION, "Pagamento", "Pagamento in contanti confermato.");
                dialog.close();
            } catch (NumberFormatException ex) {
                alertManager.show(Alert.AlertType.ERROR, "Errore", "Inserisci un importo valido.");
            }
        }
    }

    // Valida i campi carta e completa il pagamento se la conferma esterna è positiva.
    private class ConfermaCartaHandler implements EventHandler<ActionEvent> {
        private final TextField intestatarioField;
        private final TextField numeroCartaField;
        private final TextField scadenzaField;
        private final TextField cvvField;
        private final double totale;
        private final Supplier<Boolean> confermaPagamentoSelezionato;
        private final Stage dialog;

        private ConfermaCartaHandler(
            TextField intestatarioField,
            TextField numeroCartaField,
            TextField scadenzaField,
            TextField cvvField,
            double totale,
            Supplier<Boolean> confermaPagamentoSelezionato,
            Stage dialog
        ) {
            this.intestatarioField = intestatarioField;
            this.numeroCartaField = numeroCartaField;
            this.scadenzaField = scadenzaField;
            this.cvvField = cvvField;
            this.totale = totale;
            this.confermaPagamentoSelezionato = confermaPagamentoSelezionato;
            this.dialog = dialog;
        }

        // Sicurezza: obbliga Java a verificare che sto davvero implementando handle() dell'interfaccia EventHandler, evitando errori di battitura.
        @Override
        public void handle(ActionEvent event) {
            System.out.println("[ATTO 4 - 5. PAYMENT DIALOG FACTORY] L'utente ha cliccato Paga Ora. Avvio validazione dati carta.");
            String intestatario;
            if (intestatarioField.getText() == null) {
                intestatario = "";
            } else {
                intestatario = intestatarioField.getText().trim();
            }

            String numeroCarta;
            if (numeroCartaField.getText() == null) {
                numeroCarta = "";
            } else {
                numeroCarta = numeroCartaField.getText().trim();
            }

            String scadenza;
            if (scadenzaField.getText() == null) {
                scadenza = "";
            } else {
                scadenza = scadenzaField.getText().trim();
            }

            String cvv;
            if (cvvField.getText() == null) {
                cvv = "";
            } else {
                cvv = cvvField.getText().trim();
            }

            if (!validator.isValidCardData(intestatario, numeroCarta, scadenza, cvv)) {
                alertManager.show(Alert.AlertType.WARNING, "Pagamento", "Controlla i dati carta: numero 16 cifre, scadenza MM/AA, CVV 3 cifre.");
                return;
            }

            if (!confermaPagamentoSelezionato.get()) {
                return;
            }

            // --- INIZIO FIX: USIAMO IL PATTERN STRATEGY ---
            // 1. Chiamiamo il "telecomando" universale (il Context)
            patterns.strategy.PaymentContext context = new patterns.strategy.PaymentContext();
            
            // 2. Gli diciamo "Usa la strategia della Carta di Credito"
            context.setStrategy(new patterns.strategy.CardPaymentStrategy());
            
            // 3. Eseguiamo il pagamento tramite il pattern!
            String messaggioConferma = context.executePayment(totale);
            System.out.println(messaggioConferma); // Questo stamperà il log che prima non usciva!
            // --- FINE FIX ---

            alertManager.show(Alert.AlertType.INFORMATION, "Pagamento", "Transazione con carta completata. Totale addebitato: " + uiFormatsService.formatEuro(totale));
            dialog.close();
        }
    }

    // Abilita il campo PIN dopo simulazione lettura carta sul POS.
    private static class AbilitaPinHandler implements EventHandler<ActionEvent> {
        private final PasswordField pinField;

        private AbilitaPinHandler(PasswordField pinField) {
            this.pinField = pinField;
        }

        // Sicurezza: obbliga Java a verificare che sto davvero implementando handle() dell'interfaccia EventHandler, evitando errori di battitura.
        @Override
        public void handle(ActionEvent event) {
            pinField.setDisable(false);
        }
    }

    // Conferma pagamento bancomat con validazione sequenza POS e formato PIN.
    private class ConfermaBancomatHandler implements EventHandler<ActionEvent> {
        private final PasswordField pinField;
        private final double totale;
        private final Supplier<Boolean> confermaPagamentoSelezionato;
        private final Stage dialog;

        private ConfermaBancomatHandler(
            PasswordField pinField,
            double totale,
            Supplier<Boolean> confermaPagamentoSelezionato,
            Stage dialog
        ) {
            this.pinField = pinField;
            this.totale = totale;
            this.confermaPagamentoSelezionato = confermaPagamentoSelezionato;
            this.dialog = dialog;
        }

        // Sicurezza: obbliga Java a verificare che sto davvero implementando handle() dell'interfaccia EventHandler, evitando errori di battitura.
        @Override
        public void handle(ActionEvent event) {
            if (pinField.isDisabled()) {
                alertManager.show(Alert.AlertType.WARNING, "POS", "Prima simula la lettura della carta.");
                return;
            }
            String pin;
            if (pinField.getText() == null) {
                pin = "";
            } else {
                pin = pinField.getText().trim();
            }
            if (!pin.matches("\\d{4,6}")) {
                alertManager.show(Alert.AlertType.WARNING, "POS", "PIN non valido.");
                return;
            }
            if (!confermaPagamentoSelezionato.get()) {
                return;
            }

            // --- AGGANCIO PATTERN STRATEGY (BANCOMAT) ---
            patterns.strategy.PaymentContext context = new patterns.strategy.PaymentContext();
            context.setStrategy(new patterns.strategy.BancomatPaymentStrategy());
            
            String messaggioConferma = context.executePayment(totale);
            System.out.println(messaggioConferma);
            // --------------------------------------------

            alertManager.show(Alert.AlertType.INFORMATION, "POS", "Transazione autorizzata. Totale addebitato: " + uiFormatsService.formatEuro(totale));
            dialog.close();
        }
    }
}
