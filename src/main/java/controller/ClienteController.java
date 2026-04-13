package controller;

import java.io.IOException;

import controller.command.BancomatPaymentCommand;
import controller.command.CardPaymentCommand;
import controller.command.CashPaymentCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Pagamento;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;
import service.AuthFacade;
import service.TelecomRepository;
import service.TelecomRepositoryProxy;
import service.UserSession;

public class ClienteController {

    private final TelecomRepository repository = new TelecomRepositoryProxy();
    private final AuthFacade authFacade = new AuthFacade();

    @FXML private Label welcomeLabel;
    @FXML private TextField numeroField;
    @FXML private TextField durataField;
    @FXML private TextField numeroSmsField;
    @FXML private TextField testoSmsField;
    @FXML private TextField datiField;
    @FXML private TableView<Promozione> promozioniTable;
    @FXML private TableColumn<Promozione, String> nomePromozioneColumn;
    @FXML private TableColumn<Promozione, String> descrizioneColumn;
    @FXML private TableView<Pagamento> storicoPagamentiTable;
    @FXML private TableColumn<Pagamento, String> mesePagamentoColumn;
    @FXML private TableColumn<Pagamento, Integer> annoPagamentoColumn;
    @FXML private TableColumn<Pagamento, Double> importoPagamentoColumn;
    @FXML private TableColumn<Pagamento, String> promoPagamentoColumn;
    @FXML private TableColumn<Pagamento, String> statoPagamentoColumn;
    @FXML private VBox storicoClassicoPane;
    @FXML private VBox storicoDettaglioPane;
    @FXML private Label dettaglioMeseLabel;
    @FXML private Label dettaglioAnnoLabel;
    @FXML private Label dettaglioImportoLabel;
    @FXML private Label dettaglioStatoLabel;
    @FXML private TextArea dettaglioPromoArea;
    @FXML private Label numeroAttualeLabel;
    @FXML private Label pianoAttivoLabel;
    @FXML private Label chiamateUsateLabel;
    @FXML private Label smsUsatiLabel;
    @FXML private Label datiUsatiLabel;
    @FXML private Label minutiResiduiLabel;
    @FXML private Label gigaResiduiLabel;
    @FXML private Label promozioniAttiveLabel;

    private final ObservableList<Promozione> promozioni = FXCollections.observableArrayList();

    public void initialize() {
        String email = UserSession.getInstance().getCurrentEmail();
        if (welcomeLabel != null) {
            welcomeLabel.setText("Ciao!");
            try {
                String nome = repository.findNomeByEmail(email);
                if (nome != null && !nome.isBlank()) {
                    welcomeLabel.setText("Ciao " + nome + "!");
                }
            } catch (RuntimeException exception) {
                System.err.println("Errore lettura nome cliente: " + exception.getMessage());
            }
        }

        if (nomePromozioneColumn != null) {
            nomePromozioneColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        }
        if (descrizioneColumn != null) {
            descrizioneColumn.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        }
        if (promozioniTable != null) {
            promozioniTable.setItems(promozioni);
        }

        if (mesePagamentoColumn != null) {
            mesePagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("mese"));
        }
        if (annoPagamentoColumn != null) {
            annoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("anno"));
        }
        if (importoPagamentoColumn != null) {
            importoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("importo"));
            importoPagamentoColumn.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                        return;
                    }
                    setText(String.format("%.2f EUR", item));
                }
            });
        }
        if (promoPagamentoColumn != null) {
            promoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("promo"));
            promoPagamentoColumn.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setStyle("");
                        return;
                    }

                    String testo = item == null || item.isBlank() ? "Nessuna promo" : item.replace(", ", "\n");
                    setText(testo);
                    setWrapText(true);
                    setStyle("-fx-alignment: CENTER-LEFT;");
                }
            });
        }
        if (statoPagamentoColumn != null) {
            statoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("stato"));
            statoPagamentoColumn.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                        return;
                    }
                    setText(item);
                    if ("Da pagare".equalsIgnoreCase(item.trim())) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            });
        }
        if (storicoPagamentiTable != null) {
            storicoPagamentiTable.setFixedCellSize(-1);
            storicoPagamentiTable.setRowFactory(tv -> new javafx.scene.control.TableRow<>() {
                @Override
                protected void updateItem(Pagamento item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                        setPrefHeight(Region.USE_COMPUTED_SIZE);
                        return;
                    }
                    setPrefHeight(Region.USE_COMPUTED_SIZE);
                    if ("Da pagare".equalsIgnoreCase(item.getStato())) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("");
                    }
                }
            });
        }

        try {
            if (email != null && !email.isBlank()) {
                repository.aggiornaPagamentoMeseCorrente(email);
            }
            caricaPromozioni();
            aggiornaSituazioneAttuale();
            caricaStoricoPagamenti();
        } catch (RuntimeException exception) {
            impostaSituazioneFallback();
            System.err.println("Errore inizializzazione area cliente: " + exception.getMessage());
        }
    }

    private void caricaPromozioni() {
        promozioni.setAll(repository.findAllPromozioni());
    }

    private void caricaStoricoPagamenti() {
        if (storicoPagamentiTable == null) {
            return;
        }
        String email = UserSession.getInstance().getCurrentEmail();
        if (email == null || email.isBlank()) {
            storicoPagamentiTable.setItems(FXCollections.observableArrayList());
            return;
        }
        storicoPagamentiTable.setItems(repository.getStoricoPagamenti(email));
    }

    @FXML
    public void handleMostraDettagliStorico(ActionEvent event) {
        Pagamento selezionato = storicoPagamentiTable == null ? null : storicoPagamentiTable.getSelectionModel().getSelectedItem();
        if (selezionato == null) {
            showAlert(Alert.AlertType.WARNING, "Storico Pagamenti", "Seleziona una riga per vedere i dettagli.");
            return;
        }

        if (storicoClassicoPane != null) {
            storicoClassicoPane.setVisible(false);
            storicoClassicoPane.setManaged(false);
        }
        if (storicoDettaglioPane != null) {
            storicoDettaglioPane.setVisible(true);
            storicoDettaglioPane.setManaged(true);
        }

        if (dettaglioMeseLabel != null) {
            dettaglioMeseLabel.setText(selezionato.getMese());
        }
        if (dettaglioAnnoLabel != null) {
            dettaglioAnnoLabel.setText(String.valueOf(selezionato.getAnno()));
        }
        if (dettaglioImportoLabel != null) {
            dettaglioImportoLabel.setText(String.format("%.2f EUR", selezionato.getImporto()));
        }
        if (dettaglioStatoLabel != null) {
            dettaglioStatoLabel.setText(selezionato.getStato());
        }
        if (dettaglioPromoArea != null) {
            String promo = selezionato.getPromo();
            dettaglioPromoArea.setText(promo == null || promo.isBlank() ? "Nessuna promo" : promo);
        }
    }

    @FXML
    public void handleIndietroStorico(ActionEvent event) {
        if (storicoDettaglioPane != null) {
            storicoDettaglioPane.setVisible(false);
            storicoDettaglioPane.setManaged(false);
        }
        if (storicoClassicoPane != null) {
            storicoClassicoPane.setVisible(true);
            storicoClassicoPane.setManaged(true);
        }
    }

    @FXML
    public void handleEffettuaChiamata(ActionEvent event) {
        String numero = numeroField.getText();
        String durata = durataField.getText();

        if (numero.isEmpty() || durata.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Inserisci numero e durata!");
            return;
        }

        try {
            int min = Integer.parseInt(durata);
            if (min <= 0) {
                showAlert(Alert.AlertType.ERROR, "Errore", "La durata deve essere maggiore di zero!");
                return;
            }
            repository.registraChiamata(UserSession.getInstance().getCurrentEmail(), min);
            showAlert(Alert.AlertType.INFORMATION, "Chiamata", 
                     "Chiamata di " + min + " minuti al numero " + numero + " effettuata con successo!");
            
            // Pulisci i campi
            numeroField.clear();
            durataField.clear();
            aggiornaSituazioneAttuale();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Inserisci una durata valida!");
        }
    }

    @FXML
    public void handleInviaSms(ActionEvent event) {
        String numero = numeroSmsField.getText();
        String testo = testoSmsField.getText();

        if (numero.isEmpty() || testo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Inserisci numero e testo!");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "SMS", 
                 "SMS inviato al numero " + numero + " con successo!");
        repository.registraSms(UserSession.getInstance().getCurrentEmail());
        
        // Pulisci i campi
        numeroSmsField.clear();
        testoSmsField.clear();
        aggiornaSituazioneAttuale();
    }

    @FXML
    public void handleUsaDati(ActionEvent event) {
        String dati = datiField.getText();

        if (dati.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Inserisci la quantità di dati!");
            return;
        }

        try {
            int mb = Integer.parseInt(dati);
            if (mb <= 0) {
                showAlert(Alert.AlertType.ERROR, "Errore", "La quantità deve essere maggiore di zero!");
                return;
            }
            repository.registraDati(UserSession.getInstance().getCurrentEmail(), mb);
            showAlert(Alert.AlertType.INFORMATION, "Dati", 
                     "Utilizzati " + mb + " GB con successo!");
            
            datiField.clear();
            aggiornaSituazioneAttuale();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Inserisci una quantità valida!");
        }
    }

    @FXML
    public void handleAderisciPromozione(ActionEvent event) {
        Promozione selezionata = promozioniTable.getSelectionModel().getSelectedItem();
        
        if (selezionata == null) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Seleziona una promozione!");
            return;
        }

        String email = UserSession.getInstance().getCurrentEmail();
        try {
            boolean added = repository.aderisciPromozione(email, selezionata.getNome());
            repository.aggiornaPagamentoMeseCorrente(email);
            if (added) {
                showAlert(Alert.AlertType.INFORMATION, "Promozione",
                        "Hai aderito alla promozione: " + selezionata.getNome());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Promozione",
                        "La promozione è già attiva: " + selezionata.getNome());
            }
            aggiornaSituazioneAttuale();
            caricaStoricoPagamenti();
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aderire alla promozione selezionata.");
        }
    }

    @FXML
    public void handleDisdiciPromozione(ActionEvent event) {
        Promozione selezionata = promozioniTable.getSelectionModel().getSelectedItem();

        if (selezionata == null) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Seleziona una promozione da disdire!");
            return;
        }

        String email = UserSession.getInstance().getCurrentEmail();
        try {
            boolean removed = repository.disdiciPromozione(email, selezionata.getNome());
            repository.aggiornaPagamentoMeseCorrente(email);
            if (removed) {
                showAlert(Alert.AlertType.INFORMATION, "Promozione",
                        "Hai disdetto la promozione: " + selezionata.getNome());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Promozione",
                        "La promozione selezionata non risulta attiva.");
            }
            aggiornaSituazioneAttuale();
            caricaStoricoPagamenti();
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile disdire la promozione selezionata.");
        }
    }

    @FXML
    public void handlePagamentoContanti(ActionEvent event) {
        new CashPaymentCommand(this, getTotaleMensileCorrente()).execute();
    }

    @FXML
    public void handlePagamentoCarta(ActionEvent event) {
        new CardPaymentCommand(this, getTotaleMensileCorrente()).execute();
    }

    @FXML
    public void handlePagamentoBancomat(ActionEvent event) {
        new BancomatPaymentCommand(this, getTotaleMensileCorrente()).execute();
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            // Torna alla schermata di login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            authFacade.logout();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("ParthEll - Login");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile tornare al login!");
        }
    }

    private void aggiornaSituazioneAttuale() {
        String email = UserSession.getInstance().getCurrentEmail();
        if (email == null || email.isBlank()) {
            return;
        }

        Utilizzo utilizzo;
        PianoTariffario pianoTariffario;
        try {
            utilizzo = repository.findUtilizzoByEmail(email);
            pianoTariffario = repository.findPianoTariffarioByEmail(email);
        } catch (RuntimeException exception) {
            impostaSituazioneFallback();
            throw exception;
        }

        if (pianoAttivoLabel != null) {
            pianoAttivoLabel.setText(pianoTariffario == null ? "-" : pianoTariffario.getNome());
        }

        if (numeroAttualeLabel != null) {
            numeroAttualeLabel.setText(utilizzo.getNumero() == null || utilizzo.getNumero().isBlank() ? "-" : utilizzo.getNumero());
        }
        if (chiamateUsateLabel != null) {
            chiamateUsateLabel.setText(String.valueOf(utilizzo.getChiamate()));
        }
        if (smsUsatiLabel != null) {
            smsUsatiLabel.setText(String.valueOf(utilizzo.getSms()));
        }
        if (datiUsatiLabel != null) {
            datiUsatiLabel.setText(utilizzo.getDati() + " GB");
        }

        if (minutiResiduiLabel != null) {
            if (pianoTariffario == null) {
                minutiResiduiLabel.setText("-");
            } else if (pianoTariffario.isIllimitatoMinuti()) {
                minutiResiduiLabel.setText("Illimitati");
            } else {
                int residui = Math.max(0, pianoTariffario.getMinutiMensili() - utilizzo.getChiamate());
                minutiResiduiLabel.setText(residui + " min");
            }
        }

        if (gigaResiduiLabel != null) {
            if (pianoTariffario == null) {
                gigaResiduiLabel.setText("-");
            } else if (pianoTariffario.isIllimitatoGiga()) {
                gigaResiduiLabel.setText("Illimitati");
            } else {
                int residui = Math.max(0, pianoTariffario.getGigaMensili() - utilizzo.getDati());
                gigaResiduiLabel.setText(residui + " GB");
            }
        }

        if (promozioniAttiveLabel != null) {
            String promo = utilizzo.getPromo();
            promozioniAttiveLabel.setText((promo == null || promo.isBlank()) ? "Nessuna" : promo);
        }
    }

    private void impostaSituazioneFallback() {
        if (numeroAttualeLabel != null) {
            numeroAttualeLabel.setText("-");
        }
        if (pianoAttivoLabel != null) {
            pianoAttivoLabel.setText("-");
        }
        if (chiamateUsateLabel != null) {
            chiamateUsateLabel.setText("0");
        }
        if (smsUsatiLabel != null) {
            smsUsatiLabel.setText("0");
        }
        if (datiUsatiLabel != null) {
            datiUsatiLabel.setText("0 GB");
        }
        if (minutiResiduiLabel != null) {
            minutiResiduiLabel.setText("-");
        }
        if (gigaResiduiLabel != null) {
            gigaResiduiLabel.setText("-");
        }
        if (promozioniAttiveLabel != null) {
            promozioniAttiveLabel.setText("Nessuna");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private double getTotaleMensileCorrente() {
        String email = UserSession.getInstance().getCurrentEmail();
        if (email == null || email.isBlank()) {
            return 0.0;
        }
        repository.aggiornaPagamentoMeseCorrente(email);
        return repository.calcolaTotaleMensileByEmail(email);
    }

    private Stage createDialog(String titolo) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(titolo);
        if (welcomeLabel != null && welcomeLabel.getScene() != null) {
            dialog.initOwner(welcomeLabel.getScene().getWindow());
        }
        return dialog;
    }

    public void apriSchermataPagamentoContanti(double totale) {
        Stage dialog = createDialog("Pagamento in Contanti");

        Label totaleLabel = new Label("Totale: " + String.format("%.2f", totale) + " EUR");
        TextField contantiField = new TextField();
        contantiField.setPromptText("Importo ricevuto");
        Label restoLabel = new Label("Resto: 0.00 EUR");

        Button calcolaRestoButton = new Button("Calcola Resto");
        calcolaRestoButton.setOnAction(e -> {
            try {
                double ricevuto = Double.parseDouble(contantiField.getText().trim());
                double resto = ricevuto - totale;
                if (resto < 0) {
                    restoLabel.setText("Importo insufficiente: mancano " + String.format("%.2f", Math.abs(resto)) + " EUR");
                    return;
                }
                restoLabel.setText("Resto: " + String.format("%.2f", resto) + " EUR");
            } catch (NumberFormatException ex) {
                restoLabel.setText("Inserisci un importo valido.");
            }
        });

        Button confermaButton = new Button("Conferma Pagamento");
        confermaButton.setOnAction(e -> {
            try {
                double ricevuto = Double.parseDouble(contantiField.getText().trim());
                if (ricevuto < totale) {
                    showAlert(Alert.AlertType.WARNING, "Pagamento", "Importo contanti insufficiente.");
                    return;
                }
                if (!confermaPagamentoSelezionato()) {
                    return;
                }
                showAlert(Alert.AlertType.INFORMATION, "Pagamento", "Pagamento in contanti confermato.");
                dialog.close();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Errore", "Inserisci un importo valido.");
            }
        });

        Button annullaButton = new Button("Annulla");
        annullaButton.setOnAction(e -> dialog.close());

        HBox pulsanti = new HBox(10, calcolaRestoButton, confermaButton, annullaButton);
        VBox root = new VBox(12, totaleLabel, contantiField, restoLabel, pulsanti);
        root.setStyle("-fx-padding: 16;");

        dialog.setScene(new Scene(root, 460, 200));
        dialog.showAndWait();
    }

    public void apriSchermataPagamentoCarta(double totale) {
        Stage dialog = createDialog("Pagamento con Carta");

        Label totaleLabel = new Label("Totale: " + String.format("%.2f", totale) + " EUR");
        TextField intestatarioField = new TextField();
        intestatarioField.setPromptText("Nome Intestatario");
        TextField numeroCartaField = new TextField();
        numeroCartaField.setPromptText("Numero Carta (16 cifre)");
        TextField scadenzaField = new TextField();
        scadenzaField.setPromptText("Scadenza (MM/AA)");
        TextField cvvField = new TextField();
        cvvField.setPromptText("CVV (3 cifre)");

        Button pagaOraButton = new Button("Paga Ora");
        pagaOraButton.setOnAction(e -> {
            String intestatario = intestatarioField.getText() == null ? "" : intestatarioField.getText().trim();
            String numeroCarta = numeroCartaField.getText() == null ? "" : numeroCartaField.getText().trim();
            String scadenza = scadenzaField.getText() == null ? "" : scadenzaField.getText().trim();
            String cvv = cvvField.getText() == null ? "" : cvvField.getText().trim();

            if (intestatario.isBlank() || !numeroCarta.matches("\\d{16}") || !scadenza.matches("(0[1-9]|1[0-2])/\\d{2}") || !cvv.matches("\\d{3}")) {
                showAlert(Alert.AlertType.WARNING, "Pagamento", "Controlla i dati carta: numero 16 cifre, scadenza MM/AA, CVV 3 cifre.");
                return;
            }

            if (!confermaPagamentoSelezionato()) {
                return;
            }

            showAlert(Alert.AlertType.INFORMATION, "Pagamento", "Transazione con carta completata. Totale addebitato: " + String.format("%.2f", totale) + " EUR");
            dialog.close();
        });

        Button indietroButton = new Button("Indietro");
        indietroButton.setOnAction(e -> dialog.close());

        HBox pulsanti = new HBox(10, pagaOraButton, indietroButton);
        VBox root = new VBox(10, totaleLabel, intestatarioField, numeroCartaField, scadenzaField, cvvField, pulsanti);
        root.setStyle("-fx-padding: 16;");

        dialog.setScene(new Scene(root, 440, 300));
        dialog.showAndWait();
    }

    public void apriSchermataPagamentoBancomat(double totale) {
        Stage dialog = createDialog("Pagamento Bancomat (POS)");

        Label totaleLabel = new Label("Importo da pagare: " + String.format("%.2f", totale) + " EUR");
        Button simulaLetturaButton = new Button("Simula Lettura Carta");
        Label pinLabel = new Label("Inserisci PIN");
        PasswordField pinField = new PasswordField();
        pinField.setDisable(true);

        simulaLetturaButton.setOnAction(e -> pinField.setDisable(false));

        Button autorizzaButton = new Button("Autorizza Transazione");
        autorizzaButton.setOnAction(e -> {
            if (pinField.isDisabled()) {
                showAlert(Alert.AlertType.WARNING, "POS", "Prima simula la lettura della carta.");
                return;
            }
            String pin = pinField.getText() == null ? "" : pinField.getText().trim();
            if (!pin.matches("\\d{4,6}")) {
                showAlert(Alert.AlertType.WARNING, "POS", "PIN non valido.");
                return;
            }
            if (!confermaPagamentoSelezionato()) {
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "POS", "Transazione autorizzata. Totale addebitato: " + String.format("%.2f", totale) + " EUR");
            dialog.close();
        });

        Button annullaButton = new Button("Annulla");
        annullaButton.setOnAction(e -> dialog.close());

        HBox pinBox = new HBox(8, pinLabel, pinField);
        HBox pulsanti = new HBox(10, autorizzaButton, annullaButton);
        VBox root = new VBox(12, totaleLabel, simulaLetturaButton, pinBox, pulsanti);
        root.setStyle("-fx-padding: 16;");

        dialog.setScene(new Scene(root, 430, 220));
        dialog.showAndWait();
    }

    private boolean confermaPagamentoSelezionato() {
        String email = UserSession.getInstance().getCurrentEmail();
        if (email == null || email.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Pagamento", "Sessione utente non valida.");
            return false;
        }

        if (storicoPagamentiTable == null) {
            showAlert(Alert.AlertType.ERROR, "Pagamento", "Tabella storico non disponibile.");
            return false;
        }

        Pagamento selezionato = storicoPagamentiTable.getSelectionModel().getSelectedItem();
        if (selezionato == null) {
            showAlert(Alert.AlertType.WARNING, "Pagamento", "Seleziona una riga dello storico da saldare.");
            return false;
        }

        if (!selezionato.isPagabile()) {
            showAlert(Alert.AlertType.INFORMATION, "Pagamento", "La riga selezionata risulta gia confermata.");
            return false;
        }

        selezionato.confermaPagamentoState();

        boolean saldato = repository.saldaPagamento(email, selezionato.getMese(), selezionato.getAnno());
        if (!saldato) {
            showAlert(Alert.AlertType.ERROR, "Pagamento", "Nessun pagamento aggiornato sul database.");
            return false;
        }

        caricaStoricoPagamenti();
        return true;
    }

}
