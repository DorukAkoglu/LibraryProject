package studyLibrary.project;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;


public class ProfileController {

    @FXML private TextField txtName, txtID, txtEmail, txtDept, txtAge;
    @FXML private ImageView profileImageView, profileImage;
    @FXML private Label statusLabel, userNameLabel, departmentLabel;
    @FXML private VBox propertiesBox;
    private LibrarySystem system;

    @FXML
    public void initialize() {
        system = new LibrarySystem();
        User user = MainController.getCurrentUser();
        if (user != null) {
            txtName.setText(user.getName());
            txtID.setText(String.valueOf(user.getUserID()));
            txtEmail.setText(user.getEmail());
            
            if (user instanceof Student student) {
                txtDept.setText(student.getDepartment());
                txtAge.setText(String.valueOf(student.getAge()));
            }
        }
        Circle clip = new Circle(90, 90, 90); 
        profileImageView.setClip(clip);
        profileImage = new ImageView(new Image(getClass().getResourceAsStream("/images/defaultProfilePicture.png")));
        profileImage.setFitHeight(50);
        profileImage.setFitWidth(50);
        
        Circle clip2 = new Circle(25, 25, 25);
        profileImage.setClip(clip2);
        profileImage.getStyleClass().add("profile-pic");
        Student student = (Student) MainController.getCurrentUser();
        userNameLabel.setText(student.getName());
        departmentLabel.setText(student.getDepartment());
        txtID.setEditable(false);
    }
    @FXML
    private void uploadPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File file = fileChooser.showOpenDialog(((Node)event.getSource()).getScene().getWindow());

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            profileImageView.setImage(image);
        }
    }

    @FXML
    private void saveChanges(ActionEvent event) {
        User user = MainController.getCurrentUser();
        if (user != null) {
            try {
                user.setName(txtName.getText());
                user.setEmail(txtEmail.getText());
                if (user instanceof Student) {
                    Student student = (Student) user;
                    student.setDepartment(txtDept.getText());
                    String ageText = txtAge.getText();
                    if(!ageText.isEmpty()) {
                        student.setAge(Integer.parseInt(ageText));
                    }
                }
                LibrarySystem.getInstance().updateUserDB(user);
                displayTheInformation("Changes saved.");
            } catch (NumberFormatException e) {
                displayTheInformation("Age must be a number!");
            }
        }
    }
    private void displayTheInformation(String message) {
        Label information = new Label(message);
        information.getStyleClass().add("information-label");
        propertiesBox.getChildren().add(0, information); 
        FadeTransition startFade = new FadeTransition(Duration.millis(300), information);
        startFade.setFromValue(0.0);
        startFade.setToValue(1.0);
        FadeTransition finishFade = new FadeTransition(Duration.millis(500), information);
        finishFade.setFromValue(1.0);
        finishFade.setToValue(0.0);
        finishFade.setDelay(Duration.seconds(2)); 
        finishFade.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                propertiesBox.getChildren().remove(information);
            }
        });
        startFade.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                finishFade.play();
            }
        });
        startFade.play();
    }

    @FXML
    private void returnDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
}
