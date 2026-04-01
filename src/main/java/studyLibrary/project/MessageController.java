package studyLibrary.project;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MessageController {
    @FXML private VBox chatBox;
    @FXML private ScrollPane chatPane;
    @FXML private TextField messageField;
    @FXML private ListView<Student> friendList;
    
    private LibrarySystem system;
    private Student selectedFriend;
    private Timeline autoRefresh;
    
    @FXML
    public void initialize() {
        this.system = LibrarySystem.getInstance();
        loadFriendList();
        friendList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student>() {
            @Override
            public void changed(ObservableValue<? extends Student> observable, Student oldStudent, Student newStudent) {
                if (newStudent != null) {
                    selectedFriend = newStudent;
                    refreshChatArea(); 
                }
            }
        });
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedFriend != null) {
                    refreshChatArea();
                }
            }
        }));
        autoRefresh.setCycleCount(Animation.INDEFINITE);
        autoRefresh.play();
        chatPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                chatPane.setVvalue(1.0);
            }
        });
    }

    private void loadFriendList() {
        ObservableList<Student> friends = FXCollections.observableArrayList();
        Student me = (Student) MainController.getCurrentUser();
        for (StudyMatch match : system.getMatches()) {
            if (match.getStudent1().getUserID() == me.getUserID()) {
                friends.add(match.getStudent2());
            } 
            else if (match.getStudent2().getUserID() == me.getUserID()) {
                friends.add(match.getStudent1());
            }
        }
        friendList.setItems(friends);
    }
    private void refreshChatArea() {
        chatBox.getChildren().clear();
        Student me = (Student) MainController.getCurrentUser();

        for (Message message : system.getChats()) {
            boolean isBetweenUs = 
                (message.getSender().getUserID() == me.getUserID() && message.getReceiver().getUserID() == selectedFriend.getUserID()) ||
                (message.getSender().getUserID() == selectedFriend.getUserID() && message.getReceiver().getUserID() == me.getUserID());

            if (isBetweenUs) {
                displayMessage(message, message.getSender().getUserID() == me.getUserID());
            }
        }
    }
    @FXML
    private void sendMessage() {
        String text = messageField.getText().trim();
        if (selectedFriend == null || text.isEmpty()) return;
        Student me = (Student) MainController.getCurrentUser();
        Message message = new Message(me, selectedFriend, text);
        system.addChat(message);
        messageField.clear();
        refreshChatArea(); 
    }
    private void displayMessage(Message message, boolean isMine) {
        Label label = new Label(message.getContent());
        label.setWrapText(true);
        label.setMaxWidth(250);
        if (isMine) {
            label.getStyleClass().add("my-message-bubble");
        } 
        else {
            label.getStyleClass().add("other-message-bubble");
        }
        HBox hBox = new HBox(label);
        if (isMine) {
            hBox.setAlignment(Pos.CENTER_RIGHT); // for my messages, align to the right
        } 
        else {
            hBox.setAlignment(Pos.CENTER_LEFT);  // for friend's messages, align to the left
        }
        chatBox.getChildren().add(hBox);
    }

}
