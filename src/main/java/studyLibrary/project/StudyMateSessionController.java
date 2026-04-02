package studyLibrary.project;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class StudyMateSessionController {
    DatabaseManager dbManager = new DatabaseManager();


    @FXML private Label nameLabel;
    @FXML private Label ageLabel;
    @FXML private Label departmentLabel;
    @FXML private Label courseLabel;
    @FXML private ImageView imageView;

    @FXML private Label matchedNameLabel;
    @FXML private Label matchedAgeLabel;
    @FXML private Label matchedDepartmentLabel;
    @FXML private Label matchedCourseLabel;
    @FXML private ImageView matchedImageView;
    private Student matchedStudent;

    Student student = (Student) MainController.getCurrentUser();

    public void initialize(){
        dbManager.connect();
        handleFindMatch();
    }

    private void displayStudyMateInfo(Student student) {
        if(student != null){
            nameLabel.setText("Name: " + student.getName());
            ageLabel.setText("Age: " + String.valueOf(student.getAge()));
            departmentLabel.setText("Department: " + student.getDepartment());
            courseLabel.setText(student.getSelectedCourse());
        }
    }
    private void displayStudyMateMatchedInfo(Student student) {
        if(student != null){
            matchedNameLabel.setText("Name: " + student.getName());
            matchedAgeLabel.setText("Age: " + String.valueOf(student.getAge()));
            matchedDepartmentLabel.setText("Department: " + student.getDepartment());
            matchedCourseLabel.setText(student.getSelectedCourse());
        }
    }
    private void handleFindMatch(){
        List<Student> potentialMates = dbManager.getStudentsByCourse();
        Collections.shuffle(potentialMates);
        matchedStudent = potentialMates.get(0);
        displayStudyMateInfo(student);
        displayStudyMateMatchedInfo(matchedStudent);
    }

    public void endSession(ActionEvent event) throws IOException {
        changeScreen(event, "/studyMateMenu(improved).fxml");
    }

    public void goNext(ActionEvent event) throws IOException {
        handleFindMatch();
    }
    public void sendRequest(ActionEvent event){
        StudyRequest studyRequest = new StudyRequest(student, matchedStudent, student.getSelectedCourse());
        dbManager.saveStudyRequest(studyRequest);
        matchedStudent.addStudyRequest(studyRequest);
        
    }

    private void changeScreen(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
}
