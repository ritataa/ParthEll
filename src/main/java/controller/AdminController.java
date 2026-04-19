package controller;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Abbonato;
import service.AuthFacade;
import service.TelecomRepository;
import service.TelecomRepositoryProxy;

public class AdminController {
    // Dipendenze principali: repository dati e gestione autenticazione/sessione.
    private final TelecomRepository repository = new TelecomRepositoryProxy();
    private final AuthFacade authFacade = new AuthFacade();

    @FXML private TableView<model.Utilizzo> tabStatistiche;
    @FXML private TableColumn<model.Utilizzo, String> numClienteStat;
    @FXML private TableColumn<model.Utilizzo, String> nomeClienteStat;
    @FXML private TableColumn<model.Utilizzo, String> cognomeClienteStat;
    @FXML private TableColumn<model.Utilizzo, String> emailClienteStat;
    @FXML private TableColumn<model.Utilizzo, Integer> chiamateClienteStat;
    @FXML private TableColumn<model.Utilizzo, Integer> smsClienteStat;
    @FXML private TableColumn<model.Utilizzo, Integer> datiClienteStat;
    @FXML private TableColumn<model.Utilizzo, String> promoClienteStat;
    @FXML private TextField searchFieldStatistiche;

    @FXML private TableView<model.Promozione> tabPromo;
    @FXML private TableColumn<model.Promozione, String> colNomePromo;
    @FXML private TableColumn<model.Promozione, Double> colCostoPromo;
    @FXML private TableColumn<model.Promozione, String> colDescrizionePromo;
    @FXML private TextField searchFieldPromozioni;
    @FXML private TextField nomePromozione;
    @FXML private TextField costoPromozione;
    @FXML private TextArea descrizionePromozione;

    @FXML private TableView<Abbonato> abbonatiTable;
    @FXML private TableColumn<Abbonato, String> nomeColumn;
    @FXML private TableColumn<Abbonato, String> cognomeColumn;
    @FXML private TableColumn<Abbonato, String> numColumn;
    @FXML private TableColumn<Abbonato, String> pianoColumn;
    @FXML private TextField searchFieldAbbonati;

    private javafx.collections.ObservableList<Abbonato> abbonatiCompleti = javafx.collections.FXCollections.observableArrayList();
    private javafx.collections.ObservableList<model.Promozione> promozioniComplete = javafx.collections.FXCollections.observableArrayList();
    private javafx.collections.ObservableList<model.Utilizzo> utilizziCompleti = javafx.collections.FXCollections.observableArrayList();

        // ...altri campi...

        private void caricaAbbonati() {
            // Carica elenco completo abbonati e lo associa alla tabella amministratore.
            abbonatiCompleti = javafx.collections.FXCollections.observableArrayList(repository.findAllAbbonati());
            if (abbonatiTable != null) {
                abbonatiTable.setItems(abbonatiCompleti);
            }
        }
    public void initialize() {
        // Inizializzazione vista admin: colonne, dataset iniziali e listener di filtro.
        // Inizializza le colonne della tabella
    if (nomeColumn != null) nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
    if (cognomeColumn != null) cognomeColumn.setCellValueFactory(new PropertyValueFactory<>("cognome"));
    if (numColumn != null) numColumn.setCellValueFactory(new PropertyValueFactory<>("numeroTelefono"));
    if (pianoColumn != null) pianoColumn.setCellValueFactory(new PropertyValueFactory<>("pianoTariffario"));
        caricaAbbonati();
        if (searchFieldAbbonati != null) {
            searchFieldAbbonati.textProperty().addListener(new FiltroAbbonatiListener());
        }

        // Colonne tabStatistiche
    if (numClienteStat != null) numClienteStat.setCellValueFactory(new PropertyValueFactory<>("numero"));
    if (nomeClienteStat != null) nomeClienteStat.setCellValueFactory(new PropertyValueFactory<>("nome"));
    if (cognomeClienteStat != null) cognomeClienteStat.setCellValueFactory(new PropertyValueFactory<>("cognome"));
    if (emailClienteStat != null) emailClienteStat.setCellValueFactory(new PropertyValueFactory<>("email"));
    if (chiamateClienteStat != null) chiamateClienteStat.setCellValueFactory(new PropertyValueFactory<>("chiamate"));
    if (smsClienteStat != null) smsClienteStat.setCellValueFactory(new PropertyValueFactory<>("sms"));
    if (datiClienteStat != null) datiClienteStat.setCellValueFactory(new PropertyValueFactory<>("dati"));
    if (promoClienteStat != null) promoClienteStat.setCellValueFactory(new PropertyValueFactory<>("promo"));
        caricaUtilizzo();
        if (searchFieldStatistiche != null) {
            searchFieldStatistiche.textProperty().addListener(new FiltroStatisticheListener());
        }

        // Colonne tabPromo
    if (colNomePromo != null) colNomePromo.setCellValueFactory(new PropertyValueFactory<>("nome"));
    if (colCostoPromo != null) colCostoPromo.setCellValueFactory(new PropertyValueFactory<>("prezzo"));
    if (colDescrizionePromo != null) colDescrizionePromo.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        caricaPromozioni();
        if (searchFieldPromozioni != null) {
            searchFieldPromozioni.textProperty().addListener(new FiltroPromozioniListener());
        }
        }
    private void caricaUtilizzo() {
        // Carica statistiche aggregate di utilizzo dei clienti.
        utilizziCompleti = javafx.collections.FXCollections.observableArrayList(repository.findAllUtilizzi());
        if (tabStatistiche != null) tabStatistiche.setItems(utilizziCompleti);
    }

    private void caricaPromozioni() {
        // Carica catalogo promozioni disponibile nell'area amministrativa.
        promozioniComplete = javafx.collections.FXCollections.observableArrayList(repository.findAllPromozioni());
        if (tabPromo != null) tabPromo.setItems(promozioniComplete);
        // fine metodo, NON chiudere la classe qui
    }

    private void filtraAbbonatiPerNumero(String filtroNumero) {
        // Filtro locale per numero telefonico nella tabella abbonati.
        if (abbonatiTable == null) {
            return;
        }
        if (filtroNumero == null || filtroNumero.isBlank()) {
            abbonatiTable.setItems(abbonatiCompleti);
            return;
        }
        String filtro = filtroNumero.trim();
        javafx.collections.ObservableList<Abbonato> filtrati = javafx.collections.FXCollections.observableArrayList();
        for (Abbonato abbonato : abbonatiCompleti) {
            if (abbonato.getNumeroTelefono() != null && abbonato.getNumeroTelefono().contains(filtro)) {
                filtrati.add(abbonato);
            }
        }
        abbonatiTable.setItems(filtrati);
    }

    private void filtraPromozioniPerNome(String filtroNome) {
        // Filtro case-insensitive sul nome promozione.
        if (tabPromo == null) {
            return;
        }
        if (filtroNome == null || filtroNome.isBlank()) {
            tabPromo.setItems(promozioniComplete);
            return;
        }

        String filtro = filtroNome.trim().toLowerCase();
        javafx.collections.ObservableList<model.Promozione> filtrate = javafx.collections.FXCollections.observableArrayList();
        for (model.Promozione promozione : promozioniComplete) {
            String nome = promozione.getNome();
            if (nome != null && nome.toLowerCase().contains(filtro)) {
                filtrate.add(promozione);
            }
        }
        tabPromo.setItems(filtrate);
    }

    private void filtraStatistichePerNumero(String filtroNumero) {
        // Filtro statistiche per numero telefonico cliente.
        if (tabStatistiche == null) {
            return;
        }
        if (filtroNumero == null || filtroNumero.isBlank()) {
            tabStatistiche.setItems(utilizziCompleti);
            return;
        }

        String filtro = filtroNumero.trim();
        javafx.collections.ObservableList<model.Utilizzo> filtrate = javafx.collections.FXCollections.observableArrayList();
        for (model.Utilizzo utilizzo : utilizziCompleti) {
            String numero = utilizzo.getNumero();
            if (numero != null && numero.contains(filtro)) {
                filtrate.add(utilizzo);
            }
        }
        tabStatistiche.setItems(filtrate);
    }

    @FXML
    public void handleAggiungiPromozione(ActionEvent event) {
        // Inserimento promozione con validazione campi, persistenza e refresh tabelle.
        String nome = nomePromozione == null ? "" : nomePromozione.getText();
        String costo = costoPromozione == null ? "" : costoPromozione.getText();
        String descrizione = descrizionePromozione == null ? "" : descrizionePromozione.getText();

        if (nome == null || nome.isBlank() || costo == null || costo.isBlank() || descrizione == null || descrizione.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Compila tutti i campi della promozione.");
            return;
        }

        try {
            double prezzo = Double.parseDouble(costo.trim());
            if (prezzo <= 0) {
                showAlert(Alert.AlertType.WARNING, "Attenzione", "Il costo deve essere maggiore di zero.");
                return;
            }
            repository.addPromozione(nome.trim(), prezzo, descrizione.trim());
            caricaPromozioni();
            filtraPromozioniPerNome(searchFieldPromozioni == null ? "" : searchFieldPromozioni.getText());
            nomePromozione.clear();
            costoPromozione.clear();
            descrizionePromozione.clear();
            showAlert(Alert.AlertType.INFORMATION, "Promozioni", "Promozione aggiunta correttamente.");
        } catch (NumberFormatException exception) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Inserisci un costo numerico valido.");
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aggiungere la promozione.");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            // Torna alla schermata di login e chiude la sessione corrente.
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Listener testuale per filtrare la tabella abbonati.
    private class FiltroAbbonatiListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            filtraAbbonatiPerNumero(newValue);
        }
    }

    // Listener testuale per filtrare la tabella statistiche.
    private class FiltroStatisticheListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            filtraStatistichePerNumero(newValue);
        }
    }

    // Listener testuale per filtrare la tabella promozioni.
    private class FiltroPromozioniListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            filtraPromozioniPerNome(newValue);
        }
    }
}
