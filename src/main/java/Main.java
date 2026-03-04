import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.DatabaseManager;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseManager.getInstance().initializeDatabase();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("ParthEll - Gestore Telefonico");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Impossibile avviare l'applicazione", exception);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
