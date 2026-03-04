package controller;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;
import service.TelecomRepository;
import service.UserSession;

public class ClienteController {

    private final TelecomRepository repository = new TelecomRepository();

    @FXML private Label welcomeLabel;
    @FXML private TextField numeroField;
    @FXML private TextField durataField;
    @FXML private TextField numeroSmsField;
    @FXML private TextField testoSmsField;
    @FXML private TextField datiField;
    @FXML private TableView<Promozione> promozioniTable;
    @FXML private TableColumn<Promozione, String> nomePromozioneColumn;
    @FXML private TableColumn<Promozione, String> descrizioneColumn;
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

        try {
            caricaPromozioni();
            aggiornaSituazioneAttuale();
        } catch (RuntimeException exception) {
            impostaSituazioneFallback();
            System.err.println("Errore inizializzazione area cliente: " + exception.getMessage());
        }
    }

    private void caricaPromozioni() {
        promozioni.setAll(repository.findAllPromozioni());
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
            if (added) {
                showAlert(Alert.AlertType.INFORMATION, "Promozione",
                        "Hai aderito alla promozione: " + selezionata.getNome());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Promozione",
                        "La promozione è già attiva: " + selezionata.getNome());
            }
            aggiornaSituazioneAttuale();
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aderire alla promozione selezionata.");
        }
    }

    @FXML
    public void handlePagamentoContanti(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Pagamento", 
                 "Pagamento in contanti registrato!");
    }

    @FXML
    public void handlePagamentoCarta(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Pagamento", 
                 "Pagamento con carta di credito registrato!");
    }

    @FXML
    public void handlePagamentoBancomat(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Pagamento", 
                 "Pagamento con bancomat registrato!");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            // Torna alla schermata di login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            UserSession.getInstance().clear();

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

}
