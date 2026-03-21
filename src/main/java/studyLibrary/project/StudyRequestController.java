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
        activeRequests = LibrarySystem.getInstance().getRequests();
        
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
        if (activeRequests.isEmpty()) {
            checkIfEmpty();
        } 
        else {
            displayRequests();
        }
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
        
        acceptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (request.acceptRequest()) {
                    displayTheInformation("Success: Request accepted. You can now chat with " + request.getSender().getName() + ".");
                    removeCardWithAnimation(card, request);
                }
            }
        });

        rejectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (request.rejectRequest()) {
                    displayTheInformation("Request rejected.");
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
        if (activeRequests.isEmpty()) {
            requestsBox.getChildren().clear();
            Label emptyListLabel = new Label("There are no pending study requests.");
            emptyListLabel.getStyleClass().add("empty-label");
            requestsBox.setAlignment(Pos.CENTER);
            requestsBox.getChildren().add(emptyListLabel);
        }
    }

    public void backToStudyMateMenu(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/studyMate.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }

    private void displayTheInformation(String message) {
        Label information = new Label(message);
        information.getStyleClass().add("toast-label");
        requestsBox.getChildren().add(0, information); 
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
                requestsBox.getChildren().remove(information);
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
}
