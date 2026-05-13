package service;

import javafx.scene.control.Alert;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: tracciano paternità e manutenzione della classe.
 * @param: definisce i vincoli e il significato degli input.
 * @throws: esplicita le eccezioni gestibili o propagabili dal chiamante.
 */

/**
 * Centralizza la creazione e la visualizzazione degli alert JavaFX.
 * Riduce la duplicazione del codice UI e mantiene uniforme la configurazione dei messaggi.
 * Adotta un piccolo service helper per separare la logica di presentazione dal resto dell'applicazione.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class AlertManager {

    /**
     * Mostra un alert JavaFX con titolo e messaggio configurati dal chiamante.
     *
     * @param alertType tipo di alert non nullo da visualizzare.
     * @param title titolo non nullo della finestra.
     * @param message testo non nullo da mostrare all'utente.
     * @throws NullPointerException se alertType è nullo o se JavaFX riceve un parametro nullo.
     */
    public void show(Alert.AlertType alertType, String title, String message) {
        // Creo un alert standardizzato e imposto solo i campi utili alla UI.
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Mostro la finestra e aspetto la chiusura dell'utente.
        alert.showAndWait();
    }
}
