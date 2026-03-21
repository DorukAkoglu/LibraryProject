package studyLibrary.project;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainController {
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    private boolean isLoggedIn;
    private static User currentUser;
    private LibrarySystem system;
    //private AdminController adminController;
    //private LibrarianController librarianController;
    private StudentController studentController;

    public MainController() {
        currentUser = null;
        isLoggedIn = false;
        system = new LibrarySystem();
    }   
    public void handleLogin(ActionEvent event) throws IOException {
        String email = idField.getText();
        String password = passwordField.getText();
        if(checkLogin(email, password)){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/student.fxml"));
            Parent root = loader.load();
            App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
            App.PRIMARY_STAGE.getScene().setRoot(root);
        }
        else{
            idField.setStyle("-fx-border-color: red;");
            passwordField.setStyle("-fx-border-color: red;");
        }
    }

    public boolean checkLogin (String userID, String password) {
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
    @FXML
    private void signUp(ActionEvent event) throws IOException {
        // Kullanıcıyı kayıt ekranına uçuruyoruz
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/register.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
}   
