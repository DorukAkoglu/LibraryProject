package studyLibrary.project;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class SignUpController {
    @FXML private VBox mainContainer;
    @FXML private TextField txtEmail, txtFullName, txtID, txtAge, txtPasswordVisible;
    @FXML private ComboBox<String> comboBoxDept;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox checkShowPassword;
    @FXML private ScrollPane scrollPane;
    @FXML 
    public void initialize() {
        comboBoxDept.getItems().addAll("Computer Science", "Electrical Engineering", 
        "Industrial Engineering", "Machine Engineering", "Mathematics", "Chemistry", "Biology");
        
        checkShowPassword.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (checkShowPassword.isSelected()) {
                    txtPasswordVisible.setText(txtPassword.getText());
                    txtPasswordVisible.setVisible(true);
                    txtPasswordVisible.setManaged(true);
                    txtPassword.setVisible(false);
                    txtPassword.setManaged(false);
                } 
                else {
                    txtPassword.setText(txtPasswordVisible.getText());
                    txtPassword.setVisible(true);
                    txtPassword.setManaged(true);
                    txtPasswordVisible.setVisible(false);
                    txtPasswordVisible.setManaged(false);
                }
            }
        });
        scrollPane.getContent().setOnScroll(new EventHandler<ScrollEvent>() {
        @Override
        public void handle(ScrollEvent event) {
            double deltaY = event.getDeltaY() * 2; 
            double width = scrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = scrollPane.getVvalue();
            scrollPane.setVvalue(vValue + -deltaY / width); 
        }
    });
    }
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            if (!isFormEmpty()) {
                displayInformation("Please fill in all fields!");
                return;
            }
            String name = txtFullName.getText();
            String email = txtEmail.getText();
            String pass = txtPassword.getText();
            String dept = comboBoxDept.getValue();

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
        boolean isValid = true;
        TextField[] fields = {txtEmail, txtFullName, txtID, txtAge};
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                field.setStyle( "-fx-border-color: red;");
                isValid = false;
            } 
            else {
                field.setStyle(""); 
            }
        }
        if (comboBoxDept.getValue() == null) {
            comboBoxDept.setStyle("-fx-border-color: red;");
            isValid = false;
        }
        TextField activePassField = checkShowPassword.isSelected() ? txtPasswordVisible : txtPassword;
        if (activePassField.getText().trim().isEmpty()) {
            activePassField.setStyle("-fx-border-color: red;");
            isValid = false;
        } 
        else {
            txtPassword.setStyle("");
            txtPasswordVisible.setStyle("");
        }
        return isValid;
    }
    @FXML
    private void backToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}