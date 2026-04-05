package studyLibrary.project;

import java.io.IOException;

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

public class MainController {
    @FXML private TextField idField, txtID;
    @FXML private PasswordField passwordField, txtPassword;
    @FXML private VBox forgotPasswordBox;
    private boolean isLoggedIn;
    private static User currentUser;
    public static DatabaseManager dbManager = new DatabaseManager();
    private LibrarySystem system;
    //private AdminController adminController;
    //private LibrarianController librarianController;
    private StudentController studentController;

    public MainController() {
        currentUser = null;
        isLoggedIn = false;
        system = new LibrarySystem();
        dbManager.connect();
    }   
    public void handleLogin(ActionEvent event) throws IOException {
        String auserID = idField.getText();
        int userID = -1;
        if (auserID != null && !auserID.isEmpty()) {
            try {
                userID = Integer.parseInt(auserID);
            } catch (NumberFormatException e) {
                idField.setStyle("-fx-border-color: red;");
                return;
            }
        } else {
            idField.setStyle("-fx-border-color: red;");
            return;
        }
        String password = passwordField.getText();
        if(checkLogin(userID, password)){
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resolveDashboardFxml(currentUser)));
            Parent root = loader.load();
            App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
            App.PRIMARY_STAGE.getScene().setRoot(root);
        }
        else{
            idField.setStyle("-fx-border-color: red;");
            passwordField.setStyle("-fx-border-color: red;");
        }
    }

    public boolean checkLogin (int userID, String password) {
        User user = system.authorizeUser(userID, password);
        if (user != null) {
            currentUser = user;
            isLoggedIn = true;
            return true;
        }
        return false;
    }
    public void routeUser(User u) {
        if (u instanceof Student) {
            studentController = new StudentController();
        } else if (u instanceof Librarian) {
            //librarianController = new LibrarianController();
        } else if (u instanceof Admin) {
            //adminController = new AdminController();
        }
    }
    public static User getCurrentUser() {
        return currentUser;
    }

    private String resolveDashboardFxml(User user) {
        if (user instanceof Admin) {
            return "/admin.fxml";
        }
        if (user instanceof Librarian) {
            return "/librarian.fxml";
        }
        return "/student.fxml";
    }
    @FXML
    private void signUp(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/register.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
    @FXML
    private void showForgotPassword() {
        forgotPasswordBox.setVisible(true);
        forgotPasswordBox.setManaged(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), forgotPasswordBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    @FXML
    private void hideForgotPassword() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), forgotPasswordBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                forgotPasswordBox.setVisible(false);
                forgotPasswordBox.setManaged(false);
                txtID.clear();
                txtPassword.clear();
            }
        });
        fadeOut.play();
    }
    @FXML
    private void updatePassword() {
        try {
            String idText = txtID.getText().trim();
            String newPass = txtPassword.getText().trim();
            if (idText.isEmpty() || newPass.isEmpty()) {
                displayInformation("Please fill all fields!");
                return;
            }
            int userID = Integer.parseInt(idText);
            User targetUser = null;
                for (User u : system.getUsers()) {
                    if (u.getUserID() == userID) {
                        targetUser = u;
                        break;
                    }
                }
            if (targetUser != null) {
                targetUser.updatePassword(newPass);
                LibrarySystem.getInstance().updateUserDB(targetUser);
                displayInformation("Password changed successfully!");
                javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
                delay.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        hideForgotPassword(); 
                    }
                });
                delay.play();
            } 
            else {
                displayInformation("User ID not found in system!");
                txtID.setStyle("-fx-border-color: red;");
            }

        } catch (NumberFormatException e) {
            displayInformation("ID must be a number!");
        }
    }
    private void displayInformation(String message) {
        Label information = new Label(message);
        information.getStyleClass().add("information-label");
        information.setOpacity(0);
        forgotPasswordBox.getChildren().add(0, information); 
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
                forgotPasswordBox.getChildren().remove(information);
            }
        });
        fadeIn.play();
    }
}   
