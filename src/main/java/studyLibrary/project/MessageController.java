package studyLibrary.project;

import java.io.IOException;
import java.util.ArrayList;


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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
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
    @FXML private Button sendButton;
    private LibrarySystem system;
    private Student selectedFriend;
    private Timeline autoRefresh;
    private int displayedMessageCount = 0;
    private Message editingMessage = null;
    
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
                    javafx.application.Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        chatPane.setVvalue(1.0);
                        }
                    }); 
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
        chatBox.heightProperty().addListener(new ChangeListener<Number>() {
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
        system.refreshMatches();
        ObservableList<Student> friends = FXCollections.observableArrayList();
        Student me = (Student) MainController.getCurrentUser();
        for (StudyMatch match : system.getMatches()) {
            if (match.getStudent1().getEmail().equals(me.getEmail())) {
                friends.add(match.getStudent2());
            } 
            else if (match.getStudent2().getEmail().equals(me.getEmail())) {
                friends.add(match.getStudent1());
            }
        }
        friendList.setItems(friends);
    }
    private void refreshChatArea() {
        Student me = (Student) MainController.getCurrentUser();
        ArrayList<Message> allMessages = system.getChatsBetween(me.getEmail(), selectedFriend.getEmail());
        if (allMessages != null) {
            if (allMessages.size() > displayedMessageCount) {
                for (int i = displayedMessageCount; i < allMessages.size(); i++) {
                    Message message = allMessages.get(i);
                    displayMessage(message, message.getSender().getUserID() == me.getUserID());
                }
                displayedMessageCount = allMessages.size();
            }
        }
        else if (allMessages.size() == displayedMessageCount && displayedMessageCount > 0) {
            Message lastMessage = allMessages.get(allMessages.size() - 1);
            if (!chatBox.getChildren().isEmpty()) {
                HBox lastHBox = (HBox) chatBox.getChildren().get(chatBox.getChildren().size() - 1);
                VBox lastVBox = (VBox) lastHBox.getChildren().get(0);
                Label lastLabel = (Label) lastVBox.getChildren().get(0);
                if (lastMessage.isEdited() && !lastLabel.getText().endsWith("(edited)")
                    || lastMessage.isDeleted() && !lastLabel.getText().equals("(This message was deleted)")) {
                    chatBox.getChildren().clear();
                    displayedMessageCount = 0;
                    for (Message message : allMessages) {
                        displayMessage(message, message.getSender().getUserID() == me.getUserID());
                    }
                    displayedMessageCount = allMessages.size();
                }
            }
        }
    }
    @FXML
    private void sendMessage() {
        String text = messageField.getText().trim();
        if (selectedFriend == null || text.isEmpty()) return;
        if (editingMessage != null) {
            system.removeChat(editingMessage); 
            editingMessage.editMessage(text); 
            system.addChat(editingMessage); 
            editingMessage = null; 
            sendButton.setText("Send");
            chatBox.getChildren().clear();
            displayedMessageCount = 0;
        } 
        else {
            Student me = (Student) MainController.getCurrentUser();
            Message message = new Message(me, selectedFriend, text);
            system.addChat(message);
        }
        messageField.clear();
        refreshChatArea(); 
    }
    private void displayMessage(Message message, boolean isMine) {
        String messageContent = message.getContent();
        if (message.isEdited()) {
            messageContent += " (edited)";
        }
        if(message.isDeleted()) {
            messageContent = "(This message was deleted)";
        }
        Label contentLabel = new Label(messageContent);
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(250);
        contentLabel.setMinHeight(VBox.USE_PREF_SIZE);
        if(message.isDeleted()) {
            contentLabel.setStyle("-fx-text-fill: #808080; -fx-font-style: italic;");
        }
        if (isMine && !message.isDeleted()) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("Edit Message");
            MenuItem deleteItem = new MenuItem("Delete Message");
            deleteItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (editingMessage != null) {
                        return;
                    }
                    system.removeChat(message);
                    message.deleteMessage();    
                    system.addChat(message);       
                    chatBox.getChildren().clear();
                    displayedMessageCount = 0;
                    refreshChatArea(); 
                    editingMessage = null;
                    sendButton.setText("Send");
                    messageField.clear();
                }
            });
            editItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (editingMessage != null) {
                        return;
                    }
                    editingMessage = message;
                    messageField.setText(message.getContent());
                    sendButton.setText("Update");
                    messageField.requestFocus();
                }
            });
            contextMenu.getItems().add(editItem);
            contextMenu.getItems().add(deleteItem);
            contentLabel.setContextMenu(contextMenu);
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
    @FXML
    private void backToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/student.fxml"));
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
}
