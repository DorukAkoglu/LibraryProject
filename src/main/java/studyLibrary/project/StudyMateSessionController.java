package studyLibrary.project;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    @FXML private StackPane rootPane; 
    private Student matchedStudent;
    private Student previousStudent;

    Student student = (Student) MainController.getCurrentUser();

    public void initialize(){
        dbManager.connect();
        handleFindMatch();
    }

    private void displayStudyMateInfo(Student student) {
        if(student != null){
            if (student.getProfilePhoto() != null && !student.getProfilePhoto().isEmpty()) {
                imageView.setImage(new javafx.scene.image.Image(student.getProfilePhoto()));
            } else {
            imageView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/defaultProfilePicture.png")));            }
            nameLabel.setText("Name: " + student.getName());
            ageLabel.setText("Age: " + String.valueOf(student.getAge()));
            departmentLabel.setText("Department: " + student.getDepartment());
            courseLabel.setText(student.getSelectedCourse());
        }
    }
    private void displayStudyMateMatchedInfo(Student student) {
        if(student != null){
            if (student.getProfilePhoto() != null && !student.getProfilePhoto().isEmpty()) {
            matchedImageView.setImage(new javafx.scene.image.Image(student.getProfilePhoto()));
        } else {
            matchedImageView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/defaultProfilePicture.png")));
        }
            matchedNameLabel.setText("Name: " + student.getName());
            matchedAgeLabel.setText("Age: " + String.valueOf(student.getAge()));
            matchedDepartmentLabel.setText("Department: " + student.getDepartment());
            matchedCourseLabel.setText(student.getSelectedCourse());
        }
    }
    private void handleFindMatch(){
        List<Student> potentialMates = dbManager.getStudentsByCourse();
        if (potentialMates == null || potentialMates.isEmpty()) {
            matchedNameLabel.setText("No more suggestions available.");
            matchedAgeLabel.setText("");
            matchedDepartmentLabel.setText("");
            matchedCourseLabel.setText("");
            return;
        }
        Collections.shuffle(potentialMates);
        matchedStudent = potentialMates.get(0);
        if(matchedStudent != null && matchedStudent.equals(previousStudent) && potentialMates.size() > 1){
            matchedStudent = potentialMates.get(1);
        }
        displayStudyMateInfo(student);
        displayStudyMateMatchedInfo(matchedStudent);
        previousStudent = matchedStudent;
    }

    public void endSession(ActionEvent event) throws IOException {
        changeScreen(event, "/studyMateMenu(improved).fxml");
    }

    public void goNext(ActionEvent event) throws IOException {
        handleFindMatch();
    }
    public void sendRequest(ActionEvent event){
        if(matchedStudent == null){
            return;
        }
        StudyRequest studyRequest = new StudyRequest(student, matchedStudent, student.getSelectedCourse());
        dbManager.saveStudyRequest(studyRequest);
        NotificationManager.getInstance().notifyStudyRequestReceived(
            matchedStudent, student);
        matchedStudent.addStudyRequest(studyRequest);
        showRequestMessage("Request successfully sent to " + matchedStudent.getName() + ".");
    }

    private void showRequestMessage(String message) {
        Label information = new Label(message);
        information.getStyleClass().add("information-label");
        information.setOpacity(0);
        rootPane.getChildren().add(information);

        information.setLayoutX((rootPane.getWidth() - information.getBoundsInLocal().getWidth()) / 2);
        information.setLayoutY(rootPane.getHeight() / 2);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), information);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), information);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeIn.setOnFinished(e -> pause.play());
        pause.setOnFinished(e -> fadeOut.play());
        
        fadeOut.setOnFinished(e -> {
            rootPane.getChildren().remove(information); 
            handleFindMatch(); 
        });
        fadeIn.play();
    }

    private void changeScreen(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
}
