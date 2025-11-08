package application;

import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

public class MessageUtils {
    public static void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();

        // Dopo tot secondi chiude automaticamente la finestra
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(_ -> alert.close());
        delay.play();
    }
    
    public static Optional<ButtonType> showInizioTerapia() {
    		Alert alert = new Alert(AlertType.CONFIRMATION);
    		alert.setTitle("Inizio terapia");
    		alert.setHeaderText(null);
    		alert.setContentText("È iniziata una nuova terapia");
    		return alert.showAndWait();
    }
}