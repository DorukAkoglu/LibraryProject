package studyLibrary.project;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class StudentController {

    @FXML private Label userNameLabel, departmentLabel, requestLabel;
    @FXML private ImageView profileImage;
    @FXML private Button btnStudyMate, btnLibrary;

    @FXML
    public void initialize() {
        profileImage = new ImageView(new Image(getClass().getResourceAsStream("/images/defaultProfilePicture.png")));
        profileImage.setFitHeight(50);
        profileImage.setFitWidth(50);
        
        Circle clip = new Circle(25, 25, 25);
        profileImage.setClip(clip);
        profileImage.getStyleClass().add("profile-pic");
        Student student = (Student) MainController.getCurrentUser();
        userNameLabel.setText(student.getName());
        departmentLabel.setText(student.getDepartment());
        requestLabel.setText("You have " + student.getStudyRequest().size() +" study requests.");
    }
    @FXML
    private void backToLibrary(ActionEvent event) throws IOException {
        changeScreen(event, "/book.fxml");
    }
    @FXML
    private void backToStudyMateMenu(ActionEvent event) throws IOException {
        changeScreen(event, "/studyMateMenu(improved).fxml");
    }
    @FXML
    private void viewRequests(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/studyRequests.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
        String css2 = getClass().getResource("/style.css").toExternalForm();
        if(!App.PRIMARY_STAGE.getScene().getStylesheets().contains(css2)){
            App.PRIMARY_STAGE.getScene().getStylesheets().add(css2);
        }
    }
    @FXML
    private void updateProfile(ActionEvent event) throws IOException {
        changeScreen(event, "/profile.fxml");
    }
    @FXML
    private void startChat(ActionEvent event) throws IOException {
        changeScreen(event, "/message.fxml");
    }
    
    @FXML
    private void displayBorrowedBooks(ActionEvent event) throws IOException {
        changeScreen(event, "/MyBooks.fxml");
    }
    
    @FXML
    private void logout(ActionEvent event) throws IOException {
        changeScreen(event, "/login.fxml");
    }
    @FXML 
    private void switchToDeskReservation(ActionEvent event) throws IOException{
        changeScreen(event, "/reservation.fxml");
    }

    private void changeScreen(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
}











