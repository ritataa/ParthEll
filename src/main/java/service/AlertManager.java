package service;

import javafx.scene.control.Alert;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: tracciano paternità e manutenzione della classe.
 * @param: definisce i vincoli e il significato degli input.
 * @throws: esplicita le eccezioni gestibili o propagabili dal chiamante.
 */

/**
    Centralizza la creazione e la visualizzazione degli alert JavaFX.
    Implementa il Service Pattern (Helper Service) per ridurre duplicazione UI e mantenere uniforme la configurazione dei messaggi d'errore/info.
    Scopo: Evita di dover riscrivere lo stesso codice per i pop-up in ogni schermata.
    Ruolo nell'MVC: Aiuta i Controller. Il Controller decide cosa dire (es. "Errore Password"), 
    ma è questo Service che si occupa di come disegnarlo graficamente in modo uniforme.

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
