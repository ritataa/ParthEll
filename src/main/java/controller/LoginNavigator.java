package controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import patterns.facade.AuthFacade;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternità e manutenzione della classe.
 * @param: Definisce i vincoli di input richiesti dal metodo per un uso corretto.
 * @throws: Esplicita le eccezioni gestibili dal chiamante o previste dal contratto.
 */

/**
 * Gestisce il ritorno alla schermata di login dopo il logout.
 * Centralizza il cambio scena per evitare duplicazione nei controller che devono uscire dall'area protetta.
 * Mantiene separata la navigazione dalla logica di autenticazione, così il controller resta più leggibile.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class LoginNavigator {

    /**
     * Effettua il logout e sostituisce la scena corrente con la schermata di login.
     * Se il caricamento FXML fallisce, l'IOException viene propagata al chiamante.
     *
     * @param source nodo JavaFX già presente nella scena corrente; non deve essere null.
     * @param authFacade facade di autenticazione usata per chiudere la sessione corrente; non deve essere null.
     * @throws IOException se il file FXML della login non può essere caricato.
     */
    public void navigateToLogin(Node source, AuthFacade authFacade) throws IOException {
        // Carico la login prima di distruggere il contesto attuale, così il cambio scena resta atomico.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Parent root = loader.load();
        authFacade.logout();

        // Riuso la finestra già aperta per evitare di crearne una nuova.
        Stage stage = (Stage) source.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("ParthEll - Login");
        stage.show();
    }
}
