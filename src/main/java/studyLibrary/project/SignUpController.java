package studyLibrary.project;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class SignUpController {
    @FXML private VBox mainContainer;
    @FXML private TextField txtEmail, txtFullName, txtID, txtAge, txtDept;
    @FXML private PasswordField txtPassword;
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            if (isFormEmpty()) {
                displayInformation("Please fill in all fields!");
                return;
            }
            String name = txtFullName.getText();
            String email = txtEmail.getText();
            String pass = txtPassword.getText();
            String dept = txtDept.getText();
            int id = Integer.parseInt(txtID.getText());
            int age = Integer.parseInt(txtAge.getText());
            Student newStudent = new Student(id, name, email, pass, age, 1, dept);
            LibrarySystem.getInstance().addUserDB(newStudent);
            displayInformation("Registration Successful!");
            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    try {
                        backToLogin(event);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            delay.play();
        } catch (NumberFormatException e) {
            displayInformation("ID and Age must be numbers!");
        } catch (Exception e) {
            displayInformation("Error: " + e.getMessage());
        }
    }
    private void displayInformation(String message) {
        Label information = new Label(message);
        information.getStyleClass().add("information-label");
        information.setOpacity(0);
        mainContainer.getChildren().add(0, information); 
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), information);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), information);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2));
        fadeIn.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                fadeOut.play();
            }
        });
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mainContainer.getChildren().remove(information);
            }
        });
        fadeIn.play();
    }

    private boolean isFormEmpty() {
        return txtEmail.getText().isEmpty() || txtFullName.getText().isEmpty() || 
               txtPassword.getText().isEmpty() || txtID.getText().isEmpty();
    }

    @FXML
    private void backToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}