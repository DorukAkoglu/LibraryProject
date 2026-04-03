package studyLibrary.project;

import java.util.ArrayList;
import java.util.Map;


import javafx.animation.Animation;
import javafx.animation.FadeTransition;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import javafx.util.Duration;

public class MessageController {
    @FXML private VBox chatBox;
    @FXML private ScrollPane chatPane;
    @FXML private TextField messageField;
    @FXML private ListView<Student> friendList;
    @FXML private Label friendName;
    
    private LibrarySystem system;
    private Student selectedFriend;
    private Timeline autoRefresh;
    private int displayedMessageCount = 0;
    
    @FXML
    public void initialize() {
        this.system = LibrarySystem.getInstance();
        loadFriendList();
        friendList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student>() {
            @Override
            public void changed(ObservableValue<? extends Student> observable, Student oldStudent, Student newStudent) {
                if (newStudent != null) {
                    selectedFriend = newStudent;
                    friendName.setText(newStudent.getName());
                    chatBox.getChildren().clear(); 
                    displayedMessageCount = 0;
                    refreshChatArea(); 
                }
            }
        });
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedFriend != null) {
                    system.refreshChats();
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
        friendList.setCellFactory(new Callback<ListView<Student>, ListCell<Student>>() {
            @Override
            public ListCell<Student> call(ListView<Student> param) {
                return new ListCell<Student>() {
                    @Override
                    protected void updateItem(Student student, boolean empty) {
                        super.updateItem(student, empty);
                        if (empty || student == null) {
                            setText(null);
                            setGraphic(null);
                        } 
                        else {
                            ImageView imageView = new ImageView();
                            if (student.getProfilePhoto() != null && !student.getProfilePhoto().isEmpty()) {
                                imageView.setImage(new Image(student.getProfilePhoto()));
                            } 
                            else {
                                imageView.setImage(new Image(getClass().getResourceAsStream("/images/defaultProfilePicture.png")));
                            }
                            imageView.setFitHeight(40);
                            imageView.setFitWidth(40);

                            Circle clip = new Circle(20, 20, 20);
                            imageView.setClip(clip);

                            Label nameLabel = new Label(student.getName());
                            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                            
                            HBox hBox = new HBox(15, imageView, nameLabel);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });
        chatPane.getStyleClass().add("scroll-pane");
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
        Student me = (Student) MainController.getCurrentUser();
        Map<String, ArrayList<Message>> chatsByPerson = system.getChatsByPerson(me.getEmail());
        ArrayList<Message> allMessages = chatsByPerson.get(selectedFriend.getEmail());
        if (allMessages != null) {
            if (allMessages.size() > displayedMessageCount) {
                for (int i = displayedMessageCount; i < allMessages.size(); i++) {
                    Message msg = allMessages.get(i);
                    displayMessage(msg, msg.getSender().getUserID() == me.getUserID());
                }
                displayedMessageCount = allMessages.size();
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
        Label contentLabel = new Label(message.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(250);
        if (isMine) {
            contentLabel.getStyleClass().add("my-message-bubble");
        } 
        else {
            contentLabel.getStyleClass().add("other-message-bubble");
        }
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        Label timeLabel = new Label(message.getTimestamp().format(formatter));
        timeLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.5); -fx-font-size: 10px;");
        VBox vbox = new VBox(2, contentLabel, timeLabel);
        if(isMine) {
            vbox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            vbox.setAlignment(Pos.CENTER_LEFT);
        }
        HBox hBox = new HBox(vbox);
        if (isMine) {
            hBox.setAlignment(Pos.CENTER_RIGHT); // for my messages, align to the right
        } 
        else {
            hBox.setAlignment(Pos.CENTER_LEFT);  // for friend's messages, align to the left
        }
        hBox.setOpacity(0);
        chatBox.getChildren().add(hBox);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), hBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
