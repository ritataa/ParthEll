package controller;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

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
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.Abbonato;
import model.Pagamento;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;
import model.conto.ContoFisso;
import model.conto.ContoRicaricabile;
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

    // Dipendenze applicative: accesso dati, servizi di dominio e utilità UI.

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
    @FXML private Label tipoContoLabel;
    @FXML private Label dettagliContoLabel;
    @FXML private VBox pagamentiRicaricabileBox;
    @FXML private VBox walletFissoBox;
    @FXML private Label walletTipoContoLabel;
    @FXML private Label walletAddebitoLabel;
    @FXML private Label walletIntestatarioLabel;
    @FXML private Label walletNumeroCartaLabel;
    @FXML private Label walletScadenzaLabel;

    private final ObservableList<Promozione> promozioni = FXCollections.observableArrayList();

    public void initialize() {
        // Bootstrap iniziale vista cliente: saluto, tabelle, dettagli e primo caricamento dati.
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
        // Carica e sostituisce l'elenco locale usato dalla tabella promozioni.
        promozioni.setAll(dataService.loadPromozioni());
    }

    private void caricaStoricoPagamenti() {
        // Ricarica lo storico dal backend per l'utente in sessione.
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
    public void handlePagaDaSaldoDettaglio(ActionEvent event) {
        // Pagamento da saldo disponibile solo per conto ricaricabile e riga pagabile selezionata.
        String email = UserSession.getInstance().getCurrentEmail();
        Abbonato abbonato = repository.findAbbonatoByEmail(email);
        if (abbonato == null || abbonato.getConto() == null) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Pagamento", "Impossibile recuperare i dati conto.");
            return;
        }

        if (!(abbonato.getConto() instanceof ContoRicaricabile contoRicaricabile)) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Pagamento", "Il pagamento da saldo è disponibile solo per i conti ricaricabili.");
            return;
        }

        Pagamento selezionato = storicoPagamentiTable == null ? null : storicoPagamentiTable.getSelectionModel().getSelectedItem();
        if (selezionato == null || !selezionato.isPagabile()) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Pagamento", "Seleziona una riga 'Da pagare' nello storico.");
            return;
        }

        provaPagamentoStoricoDaSaldo(email, contoRicaricabile);
        aggiornaSituazioneAttuale();
        caricaStoricoPagamenti();
    }

    @FXML
    public void handleEffettuaChiamata(ActionEvent event) {
        // Validazione input + registrazione consumo voce, poi refresh dello stato UI.
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
        // Registra un SMS in uscita e aggiorna immediatamente dashboard e campi input.
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
        // Registra traffico dati (MB), con controlli di formato e valore positivo.
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
        // Adesione promozione: aggiorna sia la situazione attuale sia lo storico pagamenti.
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
        // Disdetta promozione: mantiene allineate vista principale e storico.
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
        // Avvia il flusso di pagamento con strategia specifica per contanti.
        gestisciRichiestaPagamento("Contanti", new PagamentoContantiRunnable());
    }

    @FXML
    public void handlePagamentoCarta(ActionEvent event) {
        // Avvia il flusso di pagamento con strategia specifica per carta.
        gestisciRichiestaPagamento("Carta", new PagamentoCartaRunnable());
    }

    @FXML
    public void handlePagamentoBancomat(ActionEvent event) {
        // Avvia il flusso di pagamento con strategia specifica per bancomat/POS.
        gestisciRichiestaPagamento("Bancomat", new PagamentoBancomatRunnable());
    }

    /**
     * Il "Filtro" Polimorfico: decide se aprire l'interfaccia grafica dei pagamenti
     * o se scalare direttamente i soldi dal conto ricaricabile.
     */
    private void gestisciRichiestaPagamento(String metodoPagamento, Runnable apriFinestraPagamentoCommand) {
        String email = UserSession.getInstance().getCurrentEmail();
        Abbonato abbonatoLoggato = repository.findAbbonatoByEmail(email); 
        
        if (abbonatoLoggato == null || abbonatoLoggato.getConto() == null) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Impossibile recuperare i dati del conto.");
            return;
        }

        if (abbonatoLoggato.getConto() instanceof ContoRicaricabile contoRicaricabile) {
            gestisciRicaricaConto(email, contoRicaricabile, metodoPagamento);
            return;
        }

        Pagamento selezionato = storicoPagamentiTable.getSelectionModel().getSelectedItem();
        if (selezionato == null || !selezionato.isPagabile()) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Pagamento", "Seleziona una riga da pagare dallo storico.");
            return;
        }

        double importoDaPagare = selezionato.getImporto();
        model.conto.Conto conto = abbonatoLoggato.getConto();

        // 1. IL POLIMORFISMO IN AZIONE: Chiediamo al conto se serve la carta di credito/contanti
        if (conto.richiedePagamentoImmediato(importoDaPagare)) {
            // 2A. Conto Fisso o Ricaricabile senza fondi: Apri l'interfaccia di pagamento
            apriFinestraPagamentoCommand.run(); 
        } else {
            // 2B. Conto Ricaricabile con fondi sufficienti: Paga invisibilmente
            conto.addebita(importoDaPagare);
            
            // Dato che ha pagato internamente col saldo, aggiorniamo subito il DB
            boolean saldato = dataService.saldaPagamento(email, selezionato.getMese(), selezionato.getAnno());
            
            if (saldato) {
                showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Successo", "Pagamento effettuato con successo scalando dal saldo disponibile (" + conto.getSaldo() + "€ residui).");
                caricaStoricoPagamenti(); // Ricarichiamo la tabella
            } else {
                showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore DB", "Impossibile aggiornare lo stato del pagamento.");
            }
        }
    }

    private void gestisciRicaricaConto(String email, ContoRicaricabile contoRicaricabile, String metodoPagamento) {
        // Flusso ricarica: input importo, eventuale validazione pagamento, update saldo e tentativo saldo storico.
        Double importoRicarica = chiediImportoRicarica(metodoPagamento);
        if (importoRicarica == null) {
            return;
        }

        // Se Carta o Bancomat, apri il dialog di pagamento per validare i dati
        if ("Carta".equals(metodoPagamento) || "Bancomat".equals(metodoPagamento)) {
            if (!mostraDialogoPagamento(metodoPagamento, importoRicarica)) {
                showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Ricarica", "Pagamento annullato. Ricarica non effettuata.");
                return;
            }
        }

        try {
            contoRicaricabile.ricarica(importoRicarica);
        } catch (IllegalArgumentException exception) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Ricarica", exception.getMessage());
            return;
        }

        boolean saldoAggiornato = repository.aggiornaSaldoConto(email, contoRicaricabile.getSaldo());
        if (!saldoAggiornato) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Ricarica", "Impossibile aggiornare il saldo nel database.");
            return;
        }

        showAlert(
            javafx.scene.control.Alert.AlertType.INFORMATION,
            "Ricarica completata",
            String.format("Ricarica %s effettuata: € %.2f. Saldo attuale: € %.2f", metodoPagamento, importoRicarica, contoRicaricabile.getSaldo())
        );

        provaPagamentoStoricoDaSaldo(email, contoRicaricabile);
        aggiornaSituazioneAttuale();
        caricaStoricoPagamenti();
    }

    /**
     * Mostra il dialog di pagamento (Carta o Bancomat) per validare i dati inseriti.
     * Ritorna true se confermato dall'utente, false se cancellato.
     */
    private boolean mostraDialogoPagamento(String metodoPagamento, double importo) {
        AtomicBoolean confermato = new AtomicBoolean(false);

        if ("Carta".equals(metodoPagamento)) {
            paymentDialogFactory.showCardDialog(
                storicoPagamentiTable.getScene().getWindow(),
                importo,
                new ConfermaPagamentoSupplier(confermato)
            );
        } else if ("Bancomat".equals(metodoPagamento)) {
            paymentDialogFactory.showBancomatDialog(
                storicoPagamentiTable.getScene().getWindow(),
                importo,
                new ConfermaPagamentoSupplier(confermato)
            );
        }

        return confermato.get();
    }

    private Double chiediImportoRicarica(String metodoPagamento) {
        // Dialog sincrono: ritorna importo valido, altrimenti null per interrompere il flusso.
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ricarica conto");
        dialog.setHeaderText("Metodo: " + metodoPagamento);
        dialog.setContentText("Quanto vuoi caricare?");

        String valore = dialog.showAndWait().orElse(null);
        if (valore == null) {
            return null;
        }

        try {
            double importo = Double.parseDouble(valore.trim().replace(',', '.'));
            if (importo <= 0) {
                showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Ricarica", "Inserisci un importo maggiore di zero.");
                return null;
            }
            return importo;
        } catch (NumberFormatException exception) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Ricarica", "Importo non valido.");
            return null;
        }
    }

    private void provaPagamentoStoricoDaSaldo(String email, ContoRicaricabile contoRicaricabile) {
        // Se dopo la ricarica il saldo basta, salda subito la riga selezionata nello storico.
        Pagamento selezionato = storicoPagamentiTable.getSelectionModel().getSelectedItem();
        if (selezionato == null || !selezionato.isPagabile()) {
            return;
        }

        double importoDaPagare = selezionato.getImporto();
        if (contoRicaricabile.richiedePagamentoImmediato(importoDaPagare)) {
            showAlert(
                javafx.scene.control.Alert.AlertType.INFORMATION,
                "Pagamento non completato",
                String.format("Saldo ancora insufficiente per pagare %s %d. Importo richiesto: € %.2f", selezionato.getMese(), selezionato.getAnno(), importoDaPagare)
            );
            return;
        }

        contoRicaricabile.addebita(importoDaPagare);
        boolean saldoAggiornato = repository.aggiornaSaldoConto(email, contoRicaricabile.getSaldo());
        if (!saldoAggiornato) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore DB", "Pagamento non salvato: errore aggiornamento saldo.");
            return;
        }

        boolean saldato = dataService.saldaPagamento(email, selezionato.getMese(), selezionato.getAnno());
        if (!saldato) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore DB", "Pagamento non salvato nello storico.");
            return;
        }

        showAlert(
            javafx.scene.control.Alert.AlertType.INFORMATION,
            "Pagamento effettuato",
            String.format("Pagamento %s %d saldato dal conto ricaricabile. Saldo residuo: € %.2f", selezionato.getMese(), selezionato.getAnno(), contoRicaricabile.getSaldo())
        );
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        // Uscita controllata: ritorno al login con gestione errori di navigazione.
        try {
            loginNavigator.navigateToLogin((Node) event.getSource(), authFacade);
        } catch (IOException e) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Impossibile tornare al login!");
        }
    }

    private void aggiornaSituazioneAttuale() {
        // Sincronizza tutta la dashboard cliente (conto, consumi, residui, promozioni) con i dati correnti.
        String email = UserSession.getInstance().getCurrentEmail();
        if (email == null || email.isBlank()) {
            return;
        }

        Abbonato abbonato = repository.findAbbonatoByEmail(email);
        if (abbonato == null || abbonato.getConto() == null) {
            impostaSituazioneFallback();
            return;
        }

        if (abbonato.getConto() instanceof ContoRicaricabile contoRicaricabile) {
            if (tipoContoLabel != null) {
                tipoContoLabel.setText("Conto Ricaricabile");
            }
            if (dettagliContoLabel != null) {
                dettagliContoLabel.setText(String.format("Saldo disponibile: € %.2f", contoRicaricabile.getSaldo()));
            }
            // Mostra i pulsanti di pagamento, nascondi il wallet
            if (pagamentiRicaricabileBox != null) {
                pagamentiRicaricabileBox.setVisible(true);
            }
            if (walletFissoBox != null) {
                walletFissoBox.setVisible(false);
            }
        } else if (abbonato.getConto() instanceof ContoFisso) {
            if (tipoContoLabel != null) {
                tipoContoLabel.setText("Conto Fisso");
            }
            if (dettagliContoLabel != null) {
                dettagliContoLabel.setText("Addebito automatico a fine mese (giorno 30)");
            }
            // Nascondi i pulsanti di pagamento, mostra il wallet con i dati della carta
            if (pagamentiRicaricabileBox != null) {
                pagamentiRicaricabileBox.setVisible(false);
            }
            if (walletFissoBox != null) {
                walletFissoBox.setVisible(true);
                if (walletTipoContoLabel != null) {
                    walletTipoContoLabel.setText("Conto Fisso");
                }
                if (walletAddebitoLabel != null) {
                    walletAddebitoLabel.setText("Abilitato - Addebito automatico il 30 di ogni mese");
                }
                // Mostra i dati della carta
                if (abbonato.getIntestatarioCarta() != null) {
                    if (walletIntestatarioLabel != null) {
                        walletIntestatarioLabel.setText(abbonato.getIntestatarioCarta());
                    }
                } else {
                    if (walletIntestatarioLabel != null) {
                        walletIntestatarioLabel.setText("-");
                    }
                }
                if (abbonato.getNumeroCarta() != null) {
                    if (walletNumeroCartaLabel != null) {
                        // Mostra solo le ultime 4 cifre per sicurezza
                        String numeroCarta = abbonato.getNumeroCarta();
                        String displayCarta = numeroCarta.length() >= 4 
                            ? "****" + numeroCarta.substring(numeroCarta.length() - 4)
                            : numeroCarta;
                        walletNumeroCartaLabel.setText(displayCarta);
                    }
                } else {
                    if (walletNumeroCartaLabel != null) {
                        walletNumeroCartaLabel.setText("-");
                    }
                }
                if (abbonato.getScadenzaCarta() != null) {
                    if (walletScadenzaLabel != null) {
                        walletScadenzaLabel.setText(abbonato.getScadenzaCarta());
                    }
                } else {
                    if (walletScadenzaLabel != null) {
                        walletScadenzaLabel.setText("-");
                    }
                }
            }
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
        // Stato di sicurezza UI quando i dati non sono disponibili o si verifica un errore.
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
        if (tipoContoLabel != null) {
            tipoContoLabel.setText("Conto: -");
        }
        if (dettagliContoLabel != null) {
            dettagliContoLabel.setText("-");
        }
    }

    private void showAlert(javafx.scene.control.Alert.AlertType alertType, String title, String message) {
        alertManager.show(alertType, title, message);
    }

    private double getTotaleMensileCorrente() {
        // Totale corrente usato dai comandi pagamento (contanti/carta/bancomat).
        String email = UserSession.getInstance().getCurrentEmail();
        return dataService.calcolaTotaleMensile(email);
    }

    public void apriSchermataPagamentoContanti(double totale) {
        // Apertura dialog contanti con callback di conferma pagamento selezionato.
        paymentDialogFactory.showCashDialog(
            welcomeLabel == null || welcomeLabel.getScene() == null ? null : welcomeLabel.getScene().getWindow(),
            totale,
            this::confermaPagamentoSelezionato
        );
    }

    public void apriSchermataPagamentoCarta(double totale) {
        // Apertura dialog carta con callback di conferma pagamento selezionato.
        paymentDialogFactory.showCardDialog(
            welcomeLabel == null || welcomeLabel.getScene() == null ? null : welcomeLabel.getScene().getWindow(),
            totale,
            this::confermaPagamentoSelezionato
        );
    }

    public void apriSchermataPagamentoBancomat(double totale) {
        // Apertura dialog bancomat con callback di conferma pagamento selezionato.
        paymentDialogFactory.showBancomatDialog(
            welcomeLabel == null || welcomeLabel.getScene() == null ? null : welcomeLabel.getScene().getWindow(),
            totale,
            this::confermaPagamentoSelezionato
        );
    }

    private boolean confermaPagamentoSelezionato() {
        // Conferma lato dominio + persistenza DB della riga selezionata nello storico.
        String email = UserSession.getInstance().getCurrentEmail();
        
        Pagamento selezionato = storicoPagamentiTable.getSelectionModel().getSelectedItem();
        
        selezionato.confermaPagamentoState();

        boolean saldato = dataService.saldaPagamento(email, selezionato.getMese(), selezionato.getAnno());
        if (!saldato) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Pagamento", "Nessun pagamento aggiornato sul database.");
            return false;
        }

        caricaStoricoPagamenti();
        return true;
    }

    // Esegue il comando di pagamento contanti.
    private class PagamentoContantiRunnable implements Runnable {
        @Override
        public void run() {
            new CashPaymentCommand(ClienteController.this, getTotaleMensileCorrente()).execute();
        }
    }

    // Esegue il comando di pagamento carta.
    private class PagamentoCartaRunnable implements Runnable {
        @Override
        public void run() {
            new CardPaymentCommand(ClienteController.this, getTotaleMensileCorrente()).execute();
        }
    }

    // Esegue il comando di pagamento bancomat.
    private class PagamentoBancomatRunnable implements Runnable {
        @Override
        public void run() {
            new BancomatPaymentCommand(ClienteController.this, getTotaleMensileCorrente()).execute();
        }
    }

    // Supplier riusabile: imposta la conferma a true e notifica esito positivo.
    private static class ConfermaPagamentoSupplier implements Supplier<Boolean> {
        private final AtomicBoolean confermato;

        private ConfermaPagamentoSupplier(AtomicBoolean confermato) {
            this.confermato = confermato;
        }

        @Override
        public Boolean get() {
            confermato.set(true);
            return true;
        }
    }

}
