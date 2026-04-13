package controller;

import java.io.IOException;

import controller.command.BancomatPaymentCommand;
import controller.command.CardPaymentCommand;
import controller.command.CashPaymentCommand;
import controller.payment.PaymentDialogFactory;
import controller.payment.StoricoPagamentiTableConfigurator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.Pagamento;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;
import service.AlertManager;
import service.AuthFacade;
import service.ClienteDataService;
import service.FormInputValidator;
import service.OperationResult;
import service.PromotionService;
import service.TelecomRepository;
import service.TelecomRepositoryProxy;
import service.UIFormatsService;
import service.UsageRegistrationService;
import service.UserSession;

public class ClienteController {

    private final TelecomRepository repository = new TelecomRepositoryProxy();
    private final AuthFacade authFacade = new AuthFacade();
    private final AlertManager alertManager = new AlertManager();
    private final FormInputValidator validator = new FormInputValidator();
    private final UIFormatsService uiFormatsService = new UIFormatsService();
    private final ClienteDataService dataService = new ClienteDataService(repository);
    private final UsageRegistrationService usageService = new UsageRegistrationService(repository);
    private final PromotionService promotionService = new PromotionService(repository);
    private final LoginNavigator loginNavigator = new LoginNavigator();
    private final StoricoPagamentiTableConfigurator storicoTableConfigurator =
        new StoricoPagamentiTableConfigurator(uiFormatsService);
    private final PaymentDialogFactory paymentDialogFactory =
        new PaymentDialogFactory(uiFormatsService, validator, alertManager);
    private StorageDetailsViewController storageDetailsViewController;

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
                String nome = dataService.findNomeCliente(email);
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

        storicoTableConfigurator.configure(
            storicoPagamentiTable,
            mesePagamentoColumn,
            annoPagamentoColumn,
            importoPagamentoColumn,
            promoPagamentoColumn,
            statoPagamentoColumn
        );

        storageDetailsViewController = new StorageDetailsViewController(
            storicoClassicoPane,
            storicoDettaglioPane,
            dettaglioMeseLabel,
            dettaglioAnnoLabel,
            dettaglioImportoLabel,
            dettaglioStatoLabel,
            dettaglioPromoArea,
            uiFormatsService
        );

        try {
            dataService.aggiornaPagamentoMeseCorrente(email);
            caricaPromozioni();
            aggiornaSituazioneAttuale();
            caricaStoricoPagamenti();
        } catch (RuntimeException exception) {
            impostaSituazioneFallback();
            System.err.println("Errore inizializzazione area cliente: " + exception.getMessage());
        }
    }

    private void caricaPromozioni() {
        promozioni.setAll(dataService.loadPromozioni());
    }

    private void caricaStoricoPagamenti() {
        if (storicoPagamentiTable == null) {
            return;
        }
        String email = UserSession.getInstance().getCurrentEmail();
        storicoPagamentiTable.setItems(dataService.loadStoricoPagamenti(email));
    }

    @FXML
    public void handleMostraDettagliStorico(ActionEvent event) {
        Pagamento selezionato = storicoPagamentiTable == null ? null : storicoPagamentiTable.getSelectionModel().getSelectedItem();
        if (storageDetailsViewController == null || !storageDetailsViewController.showDetails(selezionato)) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Storico Pagamenti", "Seleziona una riga per vedere i dettagli.");
        }
    }

    @FXML
    public void handleIndietroStorico(ActionEvent event) {
        if (storageDetailsViewController != null) {
            storageDetailsViewController.hideDetails();
        }
    }

    @FXML
    public void handleEffettuaChiamata(ActionEvent event) {
        String numero = numeroField.getText();
        String durata = durataField.getText();

        if (!validator.areFilled(numero, durata)) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Attenzione", "Inserisci numero e durata!");
            return;
        }

        try {
            if (!validator.isPositiveInteger(durata)) {
                showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "La durata deve essere maggiore di zero!");
                return;
            }
            int min = validator.parseInteger(durata);
            OperationResult result = usageService.registraChiamata(UserSession.getInstance().getCurrentEmail(), min, numero);
            showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Chiamata", result.message());
            
            // Pulisci i campi
            numeroField.clear();
            durataField.clear();
            aggiornaSituazioneAttuale();
        } catch (NumberFormatException e) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Inserisci una durata valida!");
        }
    }

    @FXML
    public void handleInviaSms(ActionEvent event) {
        String numero = numeroSmsField.getText();
        String testo = testoSmsField.getText();

        if (!validator.areFilled(numero, testo)) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Attenzione", "Inserisci numero e testo!");
            return;
        }

        OperationResult result = usageService.registraSms(UserSession.getInstance().getCurrentEmail(), numero);
        showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "SMS", result.message());
        
        // Pulisci i campi
        numeroSmsField.clear();
        testoSmsField.clear();
        aggiornaSituazioneAttuale();
    }

    @FXML
    public void handleUsaDati(ActionEvent event) {
        String dati = datiField.getText();

        if (validator.isBlank(dati)) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Attenzione", "Inserisci la quantità di dati!");
            return;
        }

        try {
            if (!validator.isPositiveInteger(dati)) {
                showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "La quantità deve essere maggiore di zero!");
                return;
            }
            int mb = validator.parseInteger(dati);
            OperationResult result = usageService.registraDati(UserSession.getInstance().getCurrentEmail(), mb);
            showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Dati", result.message());
            
            datiField.clear();
            aggiornaSituazioneAttuale();
        } catch (NumberFormatException e) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Inserisci una quantità valida!");
        }
    }

    @FXML
    public void handleAderisciPromozione(ActionEvent event) {
        Promozione selezionata = promozioniTable.getSelectionModel().getSelectedItem();
        
        if (selezionata == null) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Attenzione", "Seleziona una promozione!");
            return;
        }

        String email = UserSession.getInstance().getCurrentEmail();
        try {
            OperationResult result = promotionService.aderisci(email, selezionata.getNome());
            showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Promozione", result.message());
            aggiornaSituazioneAttuale();
            caricaStoricoPagamenti();
        } catch (RuntimeException exception) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Impossibile aderire alla promozione selezionata.");
        }
    }

    @FXML
    public void handleDisdiciPromozione(ActionEvent event) {
        Promozione selezionata = promozioniTable.getSelectionModel().getSelectedItem();

        if (selezionata == null) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Attenzione", "Seleziona una promozione da disdire!");
            return;
        }

        String email = UserSession.getInstance().getCurrentEmail();
        try {
            OperationResult result = promotionService.disdici(email, selezionata.getNome());
            showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Promozione", result.message());
            aggiornaSituazioneAttuale();
            caricaStoricoPagamenti();
        } catch (RuntimeException exception) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Impossibile disdire la promozione selezionata.");
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
            loginNavigator.navigateToLogin((Node) event.getSource(), authFacade);
        } catch (IOException e) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Impossibile tornare al login!");
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
            ClienteDataService.ClienteSnapshot snapshot = dataService.loadSnapshot(email);
            utilizzo = snapshot.utilizzo();
            pianoTariffario = snapshot.pianoTariffario();
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

    private void showAlert(javafx.scene.control.Alert.AlertType alertType, String title, String message) {
        alertManager.show(alertType, title, message);
    }

    private double getTotaleMensileCorrente() {
        String email = UserSession.getInstance().getCurrentEmail();
        return dataService.calcolaTotaleMensile(email);
    }

    public void apriSchermataPagamentoContanti(double totale) {
        paymentDialogFactory.showCashDialog(
            welcomeLabel == null || welcomeLabel.getScene() == null ? null : welcomeLabel.getScene().getWindow(),
            totale,
            this::confermaPagamentoSelezionato
        );
    }

    public void apriSchermataPagamentoCarta(double totale) {
        paymentDialogFactory.showCardDialog(
            welcomeLabel == null || welcomeLabel.getScene() == null ? null : welcomeLabel.getScene().getWindow(),
            totale,
            this::confermaPagamentoSelezionato
        );
    }

    public void apriSchermataPagamentoBancomat(double totale) {
        paymentDialogFactory.showBancomatDialog(
            welcomeLabel == null || welcomeLabel.getScene() == null ? null : welcomeLabel.getScene().getWindow(),
            totale,
            this::confermaPagamentoSelezionato
        );
    }

    private boolean confermaPagamentoSelezionato() {
        String email = UserSession.getInstance().getCurrentEmail();
        if (email == null || email.isBlank()) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Pagamento", "Sessione utente non valida.");
            return false;
        }

        if (storicoPagamentiTable == null) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Pagamento", "Tabella storico non disponibile.");
            return false;
        }

        Pagamento selezionato = storicoPagamentiTable.getSelectionModel().getSelectedItem();
        if (selezionato == null) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Pagamento", "Seleziona una riga dello storico da saldare.");
            return false;
        }

        if (!selezionato.isPagabile()) {
            showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Pagamento", "La riga selezionata risulta gia confermata.");
            return false;
        }

        selezionato.confermaPagamentoState();

        boolean saldato = dataService.saldaPagamento(email, selezionato.getMese(), selezionato.getAnno());
        if (!saldato) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Pagamento", "Nessun pagamento aggiornato sul database.");
            return false;
        }

        caricaStoricoPagamenti();
        return true;
    }

}
