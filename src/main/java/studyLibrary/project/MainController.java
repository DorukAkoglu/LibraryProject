package studyLibrary.project;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {
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
    public void successfulLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }

    public boolean login (String email, String password) {
        User user = system.authorizeUser(email, password);
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
}   
