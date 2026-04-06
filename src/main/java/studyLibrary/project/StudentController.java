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
    @FXML private Button notificationButton;
    @FXML private Label notificationBadge;

    @FXML
    public void initialize() {
       if (MainController.getCurrentUser().getProfilePhoto() != null && !MainController.getCurrentUser().getProfilePhoto().isEmpty()) {
            profileImage.setImage(new Image(MainController.getCurrentUser().getProfilePhoto(), true));
            profileImage.setImage(new Image(MainController.getCurrentUser().getProfilePhoto(), true));
        } else {
            profileImage.setImage(new Image(getClass().getResourceAsStream("/images/defaultProfilePicture.png")));
            profileImage.setImage(new Image(getClass().getResourceAsStream("/images/defaultProfilePicture.png")));
        }
        profileImage.setFitHeight(50);
        profileImage.setFitWidth(50);
        
        Circle clip = new Circle(25, 25, 25);
        profileImage.setClip(clip);
        profileImage.getStyleClass().add("profile-pic");
        Student student = (Student) MainController.getCurrentUser();
        userNameLabel.setText(student.getName());
        departmentLabel.setText(student.getDepartment());
        requestLabel.setText("You have " + student.getStudyRequest().size() +" study requests.");
        refreshNotificationBadge();
        NotificationManager.getInstance().checkDueDates(student,
            LibrarySystem.getInstance().getBorrowedBooksByUser(student));
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
    private void refreshNotificationBadge() {
        Student student = (Student) MainController.getCurrentUser();
        int unreadCount = NotificationManager.getInstance()
                            .getUnreadNotifications(student.getUserID()).size();
        if (unreadCount > 0) {
            notificationBadge.setText(String.valueOf(unreadCount));
            notificationBadge.setVisible(true);
        } 
        else {
            notificationBadge.setVisible(false);
        }
    }

    @FXML
    private void openNotifications(ActionEvent event) throws IOException {
        var url = getClass().getResource("/notification.fxml");
    System.out.println("FXML URL: " + url);  // null çıkıyorsa dosya yok
    if (url == null) {
        System.out.println("notifications.fxml bulunamadı!");
        return;
    }
    FXMLLoader loader = new FXMLLoader(url);
    Parent root = loader.load();
    App.PRIMARY_STAGE = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
    App.PRIMARY_STAGE.getScene().setRoot(root);
    }
}











