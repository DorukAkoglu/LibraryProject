package studyLibrary.project;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
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

    DatabaseManager dbManager = new DatabaseManager();


    @FXML private Label nameLabel;
    @FXML private Label ageLabel;
    @FXML private Label departmentLabel;
    @FXML private ComboBox<String> courseComboBox;
    @FXML private Label courseLabel;
    @FXML private ImageView imageView;

    public void initialize(){
        dbManager.connect();
        Student student = (Student) MainController.getCurrentUser();
        displayStudyMateInfo(student);
        courseComboBox.getItems().addAll("CS", "MATH", "MGB");
    }

    private void displayStudyMateInfo(Student student) {
        if(student != null){
            nameLabel.setText("Name: " + student.getName());
            ageLabel.setText("Age: " + String.valueOf(student.getAge()));
            departmentLabel.setText("Department: " + student.getDepartment());
            courseLabel.setText(student.getSelectedCourse());
        }
    }
    public void setCourse(ActionEvent event){
        courseLabel.setText("Course: " + courseComboBox.getValue());
        Student student = (Student) MainController.getCurrentUser();
        student.setSelectedCourse(courseComboBox.getValue());
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
            showAlert("Course not selected", "Choose a course to use this feature.");
        }
        else{
            List<Student> potentialMates = dbManager.getStudentsByCourse();
            if(potentialMates == null || potentialMates.isEmpty()){
                showAlert("No Mates Found", "There are no available study mates around you.");
                return;
            }
            Collections.shuffle(potentialMates);
            Student matchedStudent = potentialMates.get(0);
            displayStudyMateInfo(matchedStudent);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING); 
        alert.setTitle(title);
        alert.setHeaderText(null); 
        alert.setContentText(message);
        alert.showAndWait(); 
    }
        
}
