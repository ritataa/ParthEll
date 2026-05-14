package controller;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

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
import javafx.stage.Window;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;
import model.conto.ContoFisso;
import model.conto.ContoRicaricabile;
import patterns.builder.Abbonato;
import patterns.command.ui_TemplateM.BancomatPaymentCommand;
import patterns.command.ui_TemplateM.CardPaymentCommand;
import patterns.command.ui_TemplateM.CashPaymentCommand;
import patterns.facade.AuthFacade;
import patterns.proxy.TelecomRepositoryProxy;
import patterns.singleton.UserSession;
import patterns.state.Pagamento;
import service.AlertManager;
import service.ClienteDataService;
import service.FormInputValidator;
import service.OperationResult;
import service.PromotionService;
import service.TelecomRepository;
import service.UIFormatsService;
import service.UsageRegistrationService;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternità e manutenzione della classe.
 * @param: Definisce i vincoli di input richiesti dal metodo per un uso corretto.
 * @return: Esplicita l'output garantito o l'assenza di risultato, così il Client sa cosa può usare.
 */

/**
 * Gestisce la dashboard cliente: consumi, promozioni, storico pagamenti e operazioni di pagamento.
 * Coordina UI, servizi applicativi e repository per mantenere coerenti vista e stato dominio.
 * Usa una logica a responsabilità separata per isolare navigazione, persistenza e dialoghi di pagamento.
 *
 * @author ParthEll Team
 * @version 1.0
 */
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

    /**
     * Inizializza la vista cliente con saluto, tabelle e primo caricamento dati.
     * Se i dati non sono disponibili, applica un fallback sicuro per non bloccare l'interfaccia.
     *
     * @return nessun valore; aggiorna solo lo stato della schermata.
     */
    public void initialize() {
        System.out.println("[ATTO 2 - 4. CLIENTE CONTROLLER] Dashboard cliente in apertura. Avvio caricamento dati iniziali e storico pagamenti.");
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

    /**
     * Mostra i dettagli della riga selezionata nello storico pagamenti.
     * Se non c'è selezione, avvisa l'utente senza alterare lo stato della vista.
     *
     * @param event evento UI generato dal pulsante; deve provenire dalla schermata cliente.
     * @return nessun valore; eventuali errori sono mostrati tramite alert.
     */
    @FXML
    public void handleMostraDettagliStorico(ActionEvent event) {
        System.out.println("[ATTO 4 - 6. CLIENTE CONTROLLER] L'utente ha aperto i dettagli di una voce nello Storico Pagamenti.");
        Pagamento selezionato;
        if (storicoPagamentiTable == null) {
            selezionato = null;
        } else {
            selezionato = storicoPagamentiTable.getSelectionModel().getSelectedItem();
        }
        if (storageDetailsViewController == null || !storageDetailsViewController.showDetails(selezionato)) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Storico Pagamenti", "Seleziona una riga per vedere i dettagli.");
        }
    }

    /**
     * Torna dalla vista dettagli allo storico compatto.
     * Serve solo a ripristinare il pannello principale della sezione pagamenti.
     *
     * @param event evento UI del comando di ritorno; non richiede dati aggiuntivi.
     * @return nessun valore; aggiorna soltanto la visibilità dei pannelli.
     */
    @FXML
    public void handleIndietroStorico(ActionEvent event) {
        if (storageDetailsViewController != null) {
            storageDetailsViewController.hideDetails();
        }
    }

    /**
     * Salda una riga pagabile dello storico usando il saldo del conto ricaricabile.
     * Il metodo fallisce in modo controllato se mancano conto o selezione valida.
     *
     * @param event evento UI del pulsante di pagamento da saldo.
     * @return nessun valore; mostra il risultato tramite alert e refresh della vista.
     */
    @FXML
    public void handlePagaDaSaldoDettaglio(ActionEvent event) {
        System.out.println("[ATTO 4 - 7. CLIENTE CONTROLLER] L'utente ha cliccato Paga dal dettaglio storico. Verifico conto e pagamento selezionato.");
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

        Pagamento selezionato;
        if (storicoPagamentiTable == null) {
            selezionato = null;
        } else {
            selezionato = storicoPagamentiTable.getSelectionModel().getSelectedItem();
        }
        if (selezionato == null || !selezionato.isPagabile()) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Pagamento", "Seleziona una riga 'Da pagare' nello storico.");
            return;
        }

        provaPagamentoStoricoDaSaldo(email, contoRicaricabile);
        aggiornaSituazioneAttuale();
        caricaStoricoPagamenti();
    }

    /**
     * Registra una chiamata e aggiorna i consumi mostrati nella dashboard.
     * Valida i campi numerici prima di invocare il servizio di registrazione.
     *
     * @param event evento UI del pulsante "effettua chiamata".
     * @return nessun valore; i problemi vengono riportati con alert.
     */
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

    /**
     * Registra un SMS in uscita e aggiorna i contatori della vista cliente.
     * Richiede numero e testo non vuoti per mantenere valido il flusso di input.
     *
     * @param event evento UI del comando di invio SMS.
     * @return nessun valore; eventuali errori sono gestiti con alert.
     */
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

    /**
     * Registra traffico dati consumato e ricalcola la situazione attuale.
     * Accetta solo valori interi positivi per evitare input incoerenti.
     *
     * @param event evento UI del comando di uso dati.
     * @return nessun valore; errori di validazione sono mostrati all'utente.
     */
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

    /**
     * Aderisce alla promozione selezionata nella tabella delle offerte.
     * Se l'adesione riesce, riallinea dashboard e storico pagamenti.
     *
     * @param event evento UI del pulsante di adesione.
     * @return nessun valore; ritorna solo feedback tramite alert.
     */
    @FXML
    public void handleAderisciPromozione(ActionEvent event) {
        System.out.println("[ATTO 3 - 1. CLIENTE CONTROLLER] L'utente ha cliccato Aderisci promozione. Invio richiesta al Promotion Service.");
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

    /**
     * Disdice la promozione selezionata e aggiorna la vista cliente.
     * Il metodo è protetto contro selezioni mancanti o errori di dominio.
     *
     * @param event evento UI del pulsante di disdetta.
     * @return nessun valore; l'esito viene comunicato con alert.
     */
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

    /**
     * Avvia il pagamento del totale mensile tramite contanti.
     * Delegando al comando, separa la scelta UI dall'esecuzione del pagamento.
     *
     * @param event evento UI del pulsante contanti.
     * @return nessun valore; l'azione continua nel comando associato.
     */
    @FXML
    public void handlePagamentoContanti(ActionEvent event) {
        // Avvia il flusso di pagamento con strategia specifica per contanti.
        gestisciRichiestaPagamento("Contanti", new PagamentoContantiRunnable());
    }

    /**
     * Avvia il pagamento del totale mensile tramite carta.
     * Usa un comando dedicato per mantenere il controller indipendente dalla strategia.
     *
     * @param event evento UI del pulsante carta.
     * @return nessun valore; il flusso effettivo è delegato al comando.
     */
    @FXML
    public void handlePagamentoCarta(ActionEvent event) {
        System.out.println("[ATTO 4 - 1. CLIENTE CONTROLLER] L'utente ha scelto Carta nel Wallet. Avvio il flusso di ricarica/pagamento.");
        // Avvia il flusso di pagamento con strategia specifica per carta.
        gestisciRichiestaPagamento("Carta", new PagamentoCartaRunnable());
    }

    /**
     * Avvia il pagamento del totale mensile tramite bancomat/POS.
     * La scelta del comando isola il controller dai dettagli del mezzo di pagamento.
     *
     * @param event evento UI del pulsante bancomat.
     * @return nessun valore; il comando gestisce l'esecuzione concreta.
     */
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
        System.out.println("[ATTO 4 - 2. CLIENTE CONTROLLER] Instrado la richiesta su ricarica immediata o pagamento storico in base al tipo conto.");
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
            // 2A. Solo conto ricaricabile con saldo insufficiente: apri l'interfaccia di pagamento
            apriFinestraPagamentoCommand.run(); 
        } else {
            // 2B. Conto fisso (addebito differito) o ricaricabile con fondi: aggiorna direttamente lo stato pagamento
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
        System.out.println("[ATTO 4 - 3. CLIENTE CONTROLLER] Flusso ricarica conto ricaricabile avviato con metodo: " + metodoPagamento + ".");
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
        System.out.println("[ATTO 4 - 4. CLIENTE CONTROLLER] Apro dialog di pagamento per " + metodoPagamento + " con importo " + importo + " EUR.");
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

    /**
     * Chiude la sessione e riporta l'utente alla schermata di login.
     * Se la navigazione fallisce, mantiene la UI corrente e mostra un errore.
     *
     * @param event evento UI del pulsante di logout.
     * @return nessun valore; eventuali problemi sono comunicati con alert.
     */
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
            String pianoText;
            if (pianoTariffario == null) {
                pianoText = "-";
            } else {
                pianoText = pianoTariffario.getNome();
            }
            pianoAttivoLabel.setText(pianoText);
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

    /**
     * Apre il dialog di pagamento in contanti per il totale richiesto.
     * Il callback conferma il pagamento selezionato se l'utente completa il flusso.
     *
     * @param totale importo da pagare; deve essere coerente con il totale mensile corrente.
     * @return nessun valore; la conferma avviene tramite callback.
     */
    public void apriSchermataPagamentoContanti(double totale) {
        // Apertura dialog contanti con callback di conferma pagamento selezionato.
        Window ownerWindow;
        if (welcomeLabel == null || welcomeLabel.getScene() == null) {
            ownerWindow = null;
        } else {
            ownerWindow = welcomeLabel.getScene().getWindow();
        }
        paymentDialogFactory.showCashDialog(
            ownerWindow,
            totale,
            this::confermaPagamentoSelezionato
        );
    }

    /**
     * Apre il dialog di pagamento con carta per il totale richiesto.
     * Se il dialog viene confermato, richiama la conferma del pagamento selezionato.
     *
     * @param totale importo da pagare; deve essere maggiore o uguale a zero.
     * @return nessun valore; l'esito dipende dal dialog e dal callback.
     */
    public void apriSchermataPagamentoCarta(double totale) {
        // Apertura dialog carta con callback di conferma pagamento selezionato.
        Window ownerWindow;
        if (welcomeLabel == null || welcomeLabel.getScene() == null) {
            ownerWindow = null;
        } else {
            ownerWindow = welcomeLabel.getScene().getWindow();
        }
        paymentDialogFactory.showCardDialog(
            ownerWindow,
            totale,
            this::confermaPagamentoSelezionato
        );
    }

    /**
     * Apre il dialog di pagamento con bancomat/POS per il totale richiesto.
     * Mantiene uniforme il flusso di conferma tra i diversi metodi di pagamento.
     *
     * @param totale importo da pagare; deve essere coerente con il totale corrente.
     * @return nessun valore; il dialog gestisce il resto del flusso.
     */
    public void apriSchermataPagamentoBancomat(double totale) {
        // Apertura dialog bancomat con callback di conferma pagamento selezionato.
        Window ownerWindow;
        if (welcomeLabel == null || welcomeLabel.getScene() == null) {
            ownerWindow = null;
        } else {
            ownerWindow = welcomeLabel.getScene().getWindow();
        }
        paymentDialogFactory.showBancomatDialog(
            ownerWindow,
            totale,
            this::confermaPagamentoSelezionato
        );
    }

    /**
     * Conferma il pagamento della riga selezionata nello storico e lo salva nel database.
     * Ritorna false se l'aggiornamento persistente fallisce dopo la conferma logica.
     *
     * @return true se il pagamento viene registrato correttamente; false se la persistenza fallisce.
     */
    private boolean confermaPagamentoSelezionato() {
        System.out.println("[ATTO 4 - 8. STATE PAGAMENTO] Confermo la fattura selezionata: applico transizione di stato e persistenza su database.");
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
        // Sicurezza: obbliga Java a verificare che sto davvero implementando run() dell'interfaccia Runnable, evitando errori di battitura.
        @Override
        public void run() {
            new CashPaymentCommand(ClienteController.this, getTotaleMensileCorrente()).execute();
        }
    }

    // Esegue il comando di pagamento carta.
    private class PagamentoCartaRunnable implements Runnable {
        // Sicurezza: obbliga Java a verificare che sto davvero implementando run() dell'interfaccia Runnable, evitando errori di battitura.
        @Override
        public void run() {
            new CardPaymentCommand(ClienteController.this, getTotaleMensileCorrente()).execute();
        }
    }

    // Esegue il comando di pagamento bancomat.
    private class PagamentoBancomatRunnable implements Runnable {
        // Sicurezza: obbliga Java a verificare che sto davvero implementando run() dell'interfaccia Runnable, evitando errori di battitura.
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

        // Sicurezza: obbliga Java a verificare che sto davvero implementando get() dell'interfaccia Supplier<Boolean>, evitando errori di battitura.
        @Override
        public Boolean get() {
            confermato.set(true);
            return true;
        }
    }

}
