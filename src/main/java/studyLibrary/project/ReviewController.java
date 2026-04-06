package studyLibrary.project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ReviewController {
    @FXML private ImageView bookCoverImage;
    @FXML private Label bookTitleLabel;
    @FXML private Label bookAuthorLabel;
    @FXML private ListView<Review> reviewsListView; 
    @FXML private TextField commentTextField;
    @FXML private ComboBox<Integer> ratingComboBox;
    @FXML private Label bookStarsLabel;

    private Book currentBook;
    private User currentUser;
    private DatabaseManager dbManager = new DatabaseManager();
    private String targetReviewIdToReply = null;

    public void initializeBookData(Book book) {
        this.currentBook = book;
        this.currentUser = MainController.getCurrentUser();
        int arating = (int) Math.round((double) currentBook.getAverageRating());
        System.out.println("Rating: " + arating);
        if (book != null) {
            bookTitleLabel.setText(book.getTitle());
            bookAuthorLabel.setText("Author: " + book.getAuthor());
            
            int rating = (int) Math.round((double) book.getAverageRating());
            if (bookStarsLabel != null) {
                bookStarsLabel.setText(getStarsPattern(rating));
            }
            if (ratingComboBox != null) {
                ratingComboBox.getItems().setAll(1, 2, 3, 4, 5);
                ratingComboBox.getSelectionModel().selectFirst();
            }
        }
        setupCellFactory();
        loadReviews();
    }
    private void setupCellFactory() {
        reviewsListView.setCellFactory(param -> new ListCell<Review>() {
            @Override
            protected void updateItem(Review review, boolean empty) {
                super.updateItem(review, empty);
                if (empty || review == null) {
                    setGraphic(null);
                } else {
                    VBox root = new VBox(8);

                    Label header = new Label("★ " + review.getRating() + "/5 by " + review.getUser().getName());
                    header.setStyle("-fx-font-weight: bold;");

                    Label contentLabel = new Label(review.getContent());
                    contentLabel.setWrapText(true);

                    root.getChildren().addAll(header, contentLabel);

                    if (review.getComments() != null) {
                        for (Comment c : review.getComments()) {
                            Label commentLabel = new Label("    → " + c.getContent());
                            commentLabel.setStyle("-fx-text-fill: gray;");
                            root.getChildren().add(commentLabel);
                        }
                    }

                    HBox replyBox = new HBox(5);
                    TextField replyField = new TextField();
                    replyField.setPromptText("Add comment...");
                    replyField.setPrefWidth(250);

                    Button replyBtn = new Button("Reply");
                    replyBtn.setOnAction(e -> {
                        if (replyField.getText().trim().isEmpty()) return;
                        review.addComment(new Comment(currentUser, replyField.getText().trim()));
                        LibrarySystem.getInstance().updateBook(currentBook);
                        loadReviews();
                    });

                    replyBox.getChildren().addAll(replyField, replyBtn);
                    root.getChildren().add(replyBox);
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
    if (currentBook.getReviews() != null) {
        reviewsListView.getItems().addAll(currentBook.getReviews());
    }
}

    @FXML
    private void submitReviewAction() {
        String text = commentTextField.getText();
        if (text == null || text.trim().isEmpty()) return;

        int rating = ratingComboBox.getValue() != null ? ratingComboBox.getValue() : 5;

        Review existingReview = null;
        if (currentBook.getReviews() != null) {
            for (Review r : currentBook.getReviews()) {
                if (r.getUser().getEmail().equals(currentUser.getEmail())) {
                    existingReview = r;
                    break;
                }
            }
        }

        if (existingReview != null) {
            existingReview.setContent(text.trim());
            existingReview.setRating(rating);
        } else {
            Review newReview = new Review(currentUser, currentBook, text.trim(), rating);
            currentBook.addReview(newReview);
        }

        LibrarySystem.getInstance().updateBook(currentBook);
        commentTextField.clear();
        loadReviews();
    }

    @FXML
    private void goBackAction(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/book.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            
        } catch (java.io.IOException e) {
            System.out.println("Error: No FXML file found to return to!");
            e.printStackTrace();
        }
    }

    public void setBook(Book selectedBook) {
        this.currentBook = selectedBook; 
        
        if (bookTitleLabel != null) {
            bookTitleLabel.setText(selectedBook.getTitle());
        }
        
        if (bookAuthorLabel != null) {
            bookAuthorLabel.setText("Author: " + selectedBook.getAuthor());
        }
    }

   
}
