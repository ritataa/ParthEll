package controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import patterns.facade.AuthFacade;

/**
 * Gestisce la navigazione verso la schermata di login.
 */
public class LoginNavigator {

    /**
     * Effettua logout e sostituisce la scena corrente con la login.
     */
    public void navigateToLogin(Node source, AuthFacade authFacade) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Parent root = loader.load();
        authFacade.logout();

        Stage stage = (Stage) source.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("ParthEll - Login");
        stage.show();
    }
}
