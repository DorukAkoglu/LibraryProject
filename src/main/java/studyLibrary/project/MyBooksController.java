package studyLibrary.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MyBooksController {

    @FXML private ListView<Book> bookListView;
    @FXML private Button btnBorrowed, btnReserved, btnHistory;
    @FXML private VBox notificationBox;
    @FXML private HBox borrowedActionBox;
    @FXML private HBox reservedActionBox;

    private User currentUser;
    private LibrarySystem system;
    
    private String currentTab = "Borrowed"; 

    @FXML
    public void initialize() {
        this.system = LibrarySystem.getInstance();
        this.currentUser = MainController.getCurrentUser();
        setupMyBooksCellFactory();
        loadBorrowedBooks(null); 
    }
   
    @FXML
    public void loadBorrowedBooks(ActionEvent event) {
        currentTab = "Borrowed";
        setActiveTabStyle(btnBorrowed);
        
        bookListView.getItems().clear();
        // Gerçek veritabanından kullanıcının ödünç aldığı kitapları çeker
        bookListView.getItems().addAll(system.getBorrowedBooksByUser(currentUser));

        borrowedActionBox.setVisible(true);
        reservedActionBox.setVisible(false);
    }

    @FXML
    public void loadReservedBooks(ActionEvent event) {
        currentTab = "Reserved";
        setActiveTabStyle(btnReserved);
        
        bookListView.getItems().clear();
        // Gerçek veritabanından kullanıcının rezerve ettiği kitapları çeker
        bookListView.getItems().addAll(system.getReservedBooksByUser(currentUser)); 

        borrowedActionBox.setVisible(false);
        reservedActionBox.setVisible(true);
    }

    @FXML
    public void loadHistoryBooks(ActionEvent event) {
        currentTab = "History";
        setActiveTabStyle(btnHistory);
        
        bookListView.getItems().clear();
        // Gerçek veritabanından kullanıcının geçmiş okuma listesini çeker
        bookListView.getItems().addAll(system.getHistoryBooksByUser(currentUser));

        borrowedActionBox.setVisible(false);
        reservedActionBox.setVisible(false);
    }

    @FXML
    public void handleExtend() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            displayTheInformation("Warning: Please select a borrowed book first!");
            return;
        }

        // Extend the due date in the database
        boolean success = system.extendBook(currentUser, selectedBook);
        if (success) {
            displayTheInformation("Success: Due date extended by 7 days.");
            loadBorrowedBooks(null); // Refresh the list to show the new date
        } else {
            displayTheInformation("Error: Could not extend the due date.");
        }
    }

    @FXML
    public void handleReturn() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            displayTheInformation("Warning: Please select a borrowed book first!");
            return;
        }

        // Return the book, move to history, increase stock
        boolean success = system.returnBook(currentUser, selectedBook);
        if (success) {
            displayTheInformation("Success: Book returned successfully! It has been moved to your History.");
            loadBorrowedBooks(null); // Refresh the screen to remove from the list
        } else {
            displayTheInformation("Error: Could not return the book.");
        }
    }

    @FXML
    public void handleBorrowFromReserve() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            displayTheInformation("Warning: Please select a reserved book first!");
            return;
        }

        // Borrow the reserved book, remove from reserved, add to borrowed
        boolean success = system.borrowBook(currentUser, selectedBook);
        if (success) {
            displayTheInformation("Success: You have successfully borrowed your reserved book!");
            loadReservedBooks(null); // Refresh the screen to remove from the list
        } else {
            displayTheInformation("Error: This book is still out of stock. You cannot borrow it yet.");
        }
    }

    @FXML
    public void handleCancelReserve() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            displayTheInformation("Warning: Please select a reserved book first!");
            return;
        }

        // Cancel the reservation in the database
        boolean success = system.cancelReserve(currentUser, selectedBook);
        if (success) {
            displayTheInformation("Success: Reservation cancelled successfully.");
            loadReservedBooks(null); // Refresh the screen
        } else {
            displayTheInformation("Error: Could not cancel the reservation.");
        }
    }
    
    private void displayTheInformation(String message) {
        Label information = new Label(message);
        information.getStyleClass().add("information-label"); 
        
        if (notificationBox != null) {
            notificationBox.getChildren().add(0, information); 
        } else {
            System.out.println("Message (Image Box Not Found): " + message);
        }

        javafx.animation.FadeTransition startFade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), information);
        startFade.setFromValue(0.0);
        startFade.setToValue(1.0);

        javafx.animation.FadeTransition finishFade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(500), information);
        finishFade.setFromValue(1.0);
        finishFade.setToValue(0.0);
        finishFade.setDelay(javafx.util.Duration.seconds(2)); // 2 saniye ekranda kalır

        finishFade.setOnFinished(e -> {
            if (notificationBox != null) notificationBox.getChildren().remove(information);
        });

        startFade.setOnFinished(e -> finishFade.play());
        startFade.play();
    }

    private void setActiveTabStyle(Button activeBtn) {
        btnBorrowed.getStyleClass().removeAll("tab-active", "tab-inactive");
        btnReserved.getStyleClass().removeAll("tab-active", "tab-inactive");
        btnHistory.getStyleClass().removeAll("tab-active", "tab-inactive");

        btnBorrowed.getStyleClass().add("tab-inactive");
        btnReserved.getStyleClass().add("tab-inactive");
        btnHistory.getStyleClass().add("tab-inactive");
        
        activeBtn.getStyleClass().remove("tab-inactive");
        activeBtn.getStyleClass().add("tab-active");
    }

    @FXML
    public void goBackToDashboard(ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/student.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupMyBooksCellFactory() {
        bookListView.setCellFactory(param -> new javafx.scene.control.ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                
                if (empty || book == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    javafx.scene.layout.HBox root = new javafx.scene.layout.HBox(15);
                    root.getStyleClass().add("mybooks-row");
                    root.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    javafx.scene.control.Label iconLabel = new javafx.scene.control.Label("📚");
                    iconLabel.getStyleClass().add("mybooks-icon");

                    javafx.scene.layout.VBox infoBox = new javafx.scene.layout.VBox(5);
                    
                    
                    javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(book.getTitle() + " - " + book.getAuthor());
                    titleLabel.getStyleClass().add("mybooks-info");
                    infoBox.getChildren().add(titleLabel);


                    if ("Borrowed".equals(currentTab) && book.getDueTime() != null) {
                        javafx.scene.control.Label dateLabel = new javafx.scene.control.Label("⏳ Due Date: " + book.getDueTime().toString());
                        dateLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-size: 14px; -fx-font-weight: bold;"); 
                        infoBox.getChildren().add(dateLabel);
                    } 
                    else if ("Reserved".equals(currentTab)) {
                        javafx.scene.control.Label statusLabel = new javafx.scene.control.Label(" Status: Waiting in queue");
                        statusLabel.setStyle("-fx-text-fill: #3498db; -fx-font-size: 14px;");
                        infoBox.getChildren().add(statusLabel);
                    }

                    root.getChildren().addAll(iconLabel, infoBox);
                    setGraphic(root);
                }
            }
        });
    }

    
}
