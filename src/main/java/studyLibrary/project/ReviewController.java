package studyLibrary.project;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;

public class ReviewController {
    @FXML private ImageView bookCoverImage;
    @FXML private Label bookTitleLabel;
    @FXML private Label bookAuthorLabel;
    @FXML private ListView<org.bson.Document> reviewsListView; 
    @FXML private TextField commentTextField;
    @FXML private ComboBox<Integer> ratingComboBox;

    private Book currentBook;
    private User currentUser;
    private DatabaseManager dbManager = new DatabaseManager();
    private String targetReviewIdToReply = null;

    public void initializeBookData(Book book) {
        this.currentBook = book;
        this.currentUser = MainController.getCurrentUser(); 
        
        bookTitleLabel.setText(book.getTitle());
        bookAuthorLabel.setText("Author: " + book.getAuthor());
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);
        
        setupCellFactory();
        loadReviews();
    }

    private void setupCellFactory() {
        reviewsListView.setCellFactory(param -> new ListCell<org.bson.Document>() {
            @Override
            protected void updateItem(org.bson.Document review, boolean empty) {
                super.updateItem(review, empty);
                
                if (empty || review == null) {
                    setGraphic(null);
                } else {
                    VBox root = new VBox(8);
                    root.getStyleClass().add("review-card");

                    HBox header = new HBox(10);
                    
                    Label nameLabel = new Label(review.getString("senderName"));
                    nameLabel.getStyleClass().add("review-name");

                    int rating = review.getInteger("rating", 0);
                    Label starsLabel = new Label(getStarsPattern(rating));
                    starsLabel.getStyleClass().add("review-stars");

                    Label dateLabel = new Label(review.getString("date"));
                    dateLabel.getStyleClass().add("review-date");

                    header.getChildren().addAll(nameLabel, starsLabel, dateLabel);

                    Label contentLabel = new Label(review.getString("comment"));
                    contentLabel.setWrapText(true);
                    contentLabel.getStyleClass().add("review-content");

                    root.getChildren().addAll(header, contentLabel);

                    java.util.List<org.bson.Document> comments = (java.util.List<org.bson.Document>) review.get("comments");
                    if (comments != null && !comments.isEmpty()) {
                        VBox repliesBox = new VBox(5);
                        repliesBox.getStyleClass().add("reply-box"); // CSS Sınıfı eklendi

                        for (org.bson.Document comment : comments) {
                            Label replyLabel = new Label("User ID " + comment.getInteger("userID") + ": " + comment.getString("text"));
                            replyLabel.setWrapText(true);
                            repliesBox.getChildren().add(replyLabel);
                        }
                        root.getChildren().add(repliesBox);
                    }

                    Button replyBtn = new Button("Yanıtla");
                    replyBtn.getStyleClass().add("reply-button"); // CSS Sınıfı eklendi
                    
                    replyBtn.setOnAction(e -> {
                        targetReviewIdToReply = review.getString("reviewID");
                        commentTextField.setPromptText("Replying to " + review.getString("senderName") + "...");
                        ratingComboBox.setDisable(true); 
                    });

                    root.getChildren().add(replyBtn);
                    setGraphic(root);
                }
            }
        });
    }

    private String getStarsPattern(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) stars.append("★");
            else stars.append("☆");
        }
        return stars.toString();
    }

    private void loadReviews() {
        reviewsListView.getItems().clear();
        ArrayList<org.bson.Document> reviewList = dbManager.getReviewsForBook(currentBook.getBookID());
        reviewsListView.getItems().addAll(reviewList);
    }

    @FXML
    private void submitReviewAction() {
        String text = commentTextField.getText();
        if (text == null || text.trim().isEmpty()) return; 

        if (targetReviewIdToReply == null) {
            int rating = ratingComboBox.getValue() != null ? ratingComboBox.getValue() : 5; 
            dbManager.addReviewToBook(currentBook.getBookID(), currentUser.getName(), text, rating);
        } else {
            Comment myReply = new Comment(currentUser, text); 
            dbManager.addCommentToReview(currentBook.getBookID(), targetReviewIdToReply, myReply);
            
            targetReviewIdToReply = null;
            commentTextField.setPromptText("Write your review here...");
            ratingComboBox.setDisable(false); 
        }
        commentTextField.clear();
        loadReviews(); 
    }

    @FXML
    private void goBackAction(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("book.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            
        } catch (java.io.IOException e) {
            System.out.println("Error: No FXML file found to return to!");
            e.printStackTrace();
        }
    }
   
}
