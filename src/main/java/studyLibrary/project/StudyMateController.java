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

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML private Label nameLabel;
    @FXML private Label ageLabel;
    @FXML private Label departmentLabel;
    @FXML private ImageView imageView;


    public void displayStudyMateInfo(Student student) {
        if(student != null){
            nameLabel.setText(student.getName());
            ageLabel.setText(String.valueOf(student.getAge()));
            departmentLabel.setText(student.getDepartment());
        }
    }
    

    public void displayRequests(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("studyRequests.fxml"));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void backToStudyMateMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("studyMate.fxml"));
        Parent root = loader.load();
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
