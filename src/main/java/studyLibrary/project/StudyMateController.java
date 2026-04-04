package studyLibrary.project;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

/** 
public class StudyMateController {

    private LibrarySystem librarySystem;

    public StudyMateController(LibrarySystem librarySystem) {
        this.librarySystem = librarySystem;
    }
    public void handleStudyRequest(StudyRequest studyRequest) {
        if(studyRequest.getStatus() == RequestStatus.ACCEPTED){
            librarySystem.acceptRequest(studyRequest);
        }
        else if(studyRequest.getStatus() == RequestStatus.REJECTED){
            librarySystem.removeRequest(studyRequest);
        }
    } 
}
*/  
public class StudyMateController {

    DatabaseManager dbManager = new DatabaseManager();


    @FXML private Label nameLabel;
    @FXML private Label ageLabel;
    @FXML private Label departmentLabel;
    @FXML private ComboBox<String> courseComboBox;
    @FXML private Label errorMessage;
    @FXML private Label courseLabel;
    @FXML private ImageView imageView;

    public void initialize(){
        dbManager.connect();
        Student student = (Student) MainController.getCurrentUser();
        displayStudyMateInfo(student);
        courseComboBox.getItems().addAll("CS", "MATH", "MBG");
        if (student != null && student.getSelectedCourse() != null) {
            courseComboBox.setValue(student.getSelectedCourse());
    }
        errorMessage.setVisible(false);
    }

    private void displayStudyMateInfo(Student student) {
        if(student != null){
            nameLabel.setText("Name: " + student.getName());
            ageLabel.setText("Age: " + String.valueOf(student.getAge()));
            departmentLabel.setText("Department: " + student.getDepartment());
            courseLabel.setText("Course: "+ student.getSelectedCourse());
        }
    }
    public void setCourse(ActionEvent event){
        courseLabel.setText("Course: " + courseComboBox.getValue());
        Student student = (Student) MainController.getCurrentUser();
        student.setSelectedCourse(courseComboBox.getValue());
        dbManager.updateUser(student);
    }

    public void displayRequests(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/studyRequests.fxml"));
        Parent root = loader.load();
        root.setStyle("-fx-background-color: #f8f9fa;");
        App.PRIMARY_STAGE = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
        if(!App.PRIMARY_STAGE.getScene().getStylesheets().contains(getClass().getResource("/style.css").toExternalForm())){
            App.PRIMARY_STAGE.getScene().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }
    }
    public void switchToStudentDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
    public void handleFindMatchButton(ActionEvent event) throws IOException{
        if(courseComboBox.getValue() == null){
            errorMessage.setText("No course selected.");
            errorMessage.setVisible(true);
        }
        else{
            List<Student> potentialMates = dbManager.getStudentsByCourse();
            if(potentialMates == null || potentialMates.isEmpty()){
                errorMessage.setText("No Available Study Mates Within Your Preferences.");
                errorMessage.setVisible(true);
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event){
                        FadeTransition fade = new FadeTransition(Duration.seconds(3), errorMessage);
                        fade.setFromValue(1.0);
                        fade.setToValue(0.0);
                        fade.setOnFinished(new EventHandler<ActionEvent>() {
                            public void handle(ActionEvent event){
                                errorMessage.setVisible(false);
                                errorMessage.setOpacity(1);
                            }
                        });
                        fade.play();
                    }
                });
                pause.play();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mateMatchResult.fxml"));
            Parent root = loader.load();
            App.PRIMARY_STAGE = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            App.PRIMARY_STAGE.getScene().setRoot(root);
        }
    }      
}
