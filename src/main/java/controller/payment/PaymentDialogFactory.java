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

/**
 * Factory dei dialog di pagamento (contanti, carta, bancomat).
 */
public class PaymentDialogFactory {

    // Servizi condivisi per formattazione UI, validazione input e alert utente.
    private final UIFormatsService uiFormatsService;
    private final FormInputValidator validator;
    private final AlertManager alertManager;

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
     * Mostra il dialog di pagamento contanti.
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
     * Mostra il dialog di pagamento con carta e valida i dati inseriti.
     */
    public void showCardDialog(Window owner, double totale, Supplier<Boolean> confermaPagamentoSelezionato) {
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
     * Mostra il dialog di pagamento bancomat/POS.
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

        @Override
        public void handle(ActionEvent event) {
            String intestatario = intestatarioField.getText() == null ? "" : intestatarioField.getText().trim();
            String numeroCarta = numeroCartaField.getText() == null ? "" : numeroCartaField.getText().trim();
            String scadenza = scadenzaField.getText() == null ? "" : scadenzaField.getText().trim();
            String cvv = cvvField.getText() == null ? "" : cvvField.getText().trim();

            if (!validator.isValidCardData(intestatario, numeroCarta, scadenza, cvv)) {
                alertManager.show(Alert.AlertType.WARNING, "Pagamento", "Controlla i dati carta: numero 16 cifre, scadenza MM/AA, CVV 3 cifre.");
                return;
            }

            if (!confermaPagamentoSelezionato.get()) {
                return;
            }

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

        @Override
        public void handle(ActionEvent event) {
            if (pinField.isDisabled()) {
                alertManager.show(Alert.AlertType.WARNING, "POS", "Prima simula la lettura della carta.");
                return;
            }
            String pin = pinField.getText() == null ? "" : pinField.getText().trim();
            if (!pin.matches("\\d{4,6}")) {
                alertManager.show(Alert.AlertType.WARNING, "POS", "PIN non valido.");
                return;
            }
            if (!confermaPagamentoSelezionato.get()) {
                return;
            }
            alertManager.show(Alert.AlertType.INFORMATION, "POS", "Transazione autorizzata. Totale addebitato: " + uiFormatsService.formatEuro(totale));
            dialog.close();
        }
    }
}
