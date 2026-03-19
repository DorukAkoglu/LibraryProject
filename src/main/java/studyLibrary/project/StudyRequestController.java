package studyLibrary.project;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.scene.shape.Circle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class StudyRequestController {
    
    // private LibrarySystem librarySystem = new LibrarySystem();
    @FXML
    private VBox requestsBox; 
    @FXML
    private Button returnButton;
    private List<StudyRequest> activeRequests = new ArrayList<>(); // for testing purposes, replace with actual data retrieval from librarySystem
    @FXML
    private ScrollPane myScrollPane;
    @FXML
    public void initialize(){
        Student ali = new Student(1, "Ali", "ali@example.com", "password", 20, 85, "Computer Engineering");
        Student ayse = new Student(2, "Ayşe", "ayse@example.com", "password", 21, 88, "Industrial Engineering");
        activeRequests.add(new StudyRequest(ali, ayse, "Calculus II"));
        Student a = new Student(3, "Ayşe", "ayse@example.com", "password", 21, 88, "Industrial Engineering");
        activeRequests.add(new StudyRequest(a, ayse, "Calculus II"));
        Student b = new Student(4, "Ayşe", "ayse@example.com", "password", 21, 88, "Industrial Engineering");
        activeRequests.add(new StudyRequest(b, ayse, "Calculus II"));
        Student c = new Student(5, "Ayşe", "ayse@example.com", "password", 21, 88, "Industrial Engineering");
        activeRequests.add(new StudyRequest(c, ayse, "Calculus II"));
        activeRequests.add(new StudyRequest(ayse, ali, "Java Programming"));
        
        myScrollPane.setFitToWidth(true); 
        requestsBox.setFillWidth(true);
        returnButton.setOnAction(new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent event){
            try{
                backToStudyMateMenu(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        });
        displayRequests();
    }

    private void displayRequests(){
        requestsBox.getChildren().clear();
        requestsBox.setSpacing(10);
        for (StudyRequest request : activeRequests){
            if (request.getStatus() == RequestStatus.PENDING){
                requestsBox.getChildren().add(createRequestCard(request));
            }
        }
    }
    private HBox createRequestCard(StudyRequest request){
        HBox card = new HBox(); 
        card.getStyleClass().add("request-card");
        card.setAlignment(Pos.CENTER_LEFT);
        
        ImageView profileImage = new ImageView(new Image(getClass().getResourceAsStream("/images/defaultProfilePicture.png")));
        profileImage.setFitHeight(50);
        profileImage.setFitWidth(50);
        
        Circle clip = new Circle(25, 25, 25);
        profileImage.setClip(clip);
        profileImage.getStyleClass().add("profile-pic");

        VBox informations = new VBox(2);
        HBox.setHgrow(informations, Priority.ALWAYS);
        
        Label nameLabel = new Label(request.getSender().getName());
        nameLabel.getStyleClass().add("student-name");
        
        Label courseLabel = new Label(request.getCourse());
        courseLabel.getStyleClass().add("course-name");
        
        informations.getChildren().addAll(nameLabel, courseLabel);

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button acceptButton = new Button("✓ Accept");
        acceptButton.getStyleClass().add("accept-button");
        acceptButton.setMinWidth(110);
        acceptButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                if (request.acceptRequest()){
                    removeCardWithAnimation(card, request);
                }
            }
        });

        Button rejectButton = new Button("✗ Reject");
        rejectButton.getStyleClass().add("reject-button");
        rejectButton.setMinWidth(110);
        rejectButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if (request.rejectRequest()) {
                    removeCardWithAnimation(card, request);
                }
            }
        });

        buttonBox.getChildren().addAll(acceptButton, rejectButton);
        card.getChildren().addAll(profileImage, informations, buttonBox);
        
        return card;
    }
    private void removeCardWithAnimation(HBox card, StudyRequest request){
        FadeTransition fade = new FadeTransition(Duration.millis(300), card);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                requestsBox.getChildren().remove(card);
                activeRequests.remove(request);
                checkIfEmpty();
            }
        });  
        fade.play();
    }
    private void checkIfEmpty() {
        if (requestsBox.getChildren().isEmpty()) {
            Label emptyList = new Label("There are no pending study requests.");
            emptyList.getStyleClass().add("empty-label");
            requestsBox.setAlignment(Pos.CENTER);
            requestsBox.getChildren().add(emptyList);
        }
    }

    public void backToStudyMateMenu(ActionEvent event) throws IOException{
        Stage stage;
        Scene scene;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/studyMate.fxml"));
        Parent root = loader.load();
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
