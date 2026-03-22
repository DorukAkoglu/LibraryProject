package studyLibrary.project;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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


    @FXML private Label nameLabel;
    @FXML private Label ageLabel;
    @FXML private Label departmentLabel;
    @FXML private Label courseLabel;
    @FXML private ImageView imageView;

    public void initialize(){
        User user = MainController.getCurrentUser();
        if(user instanceof Student){
            Student student = (Student) user;
            displayStudyMateInfo(student);
        }
    }

    private void displayStudyMateInfo(Student student) {
        if(student != null){
            nameLabel.setText("Name: " + student.getName());
            ageLabel.setText("Age: " + String.valueOf(student.getAge()));
            departmentLabel.setText("Department: " + student.getDepartment());
        }
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
        
}
