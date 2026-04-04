package studyLibrary.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

public class MyBooksController {

    @FXML private ListView<Book> bookListView;
    @FXML private Button btnBorrowed, btnReserved, btnHistory;
    
    @FXML private HBox borrowedActionBox;
    @FXML private HBox reservedActionBox;

    private User currentUser;
    private LibrarySystem system;
    
    private String currentTab = "Borrowed"; 

    @FXML
    public void initialize() {
        this.system = LibrarySystem.getInstance();
        this.currentUser = MainController.getCurrentUser();
        
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
            showAlert("Warning", "Please select a borrowed book first!");
            return;
        }

        // Veritabanında süreyi uzat
        boolean success = system.extendBook(currentUser, selectedBook);
        if (success) {
            showAlert("Success", "Due date extended by 7 days.");
            loadBorrowedBooks(null); // Listeyi yenile ki yeni tarih ekranda görünsün
        } else {
            showAlert("Error", "Could not extend the due date.");
        }
    }

    @FXML
    public void handleReturn() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Warning", "Please select a borrowed book first!");
            return;
        }

        // Veritabanında kitabı iade et Geçmişe taşır, stoğu artırır
        boolean success = system.returnBook(currentUser, selectedBook);
        if (success) {
            showAlert("Success", "Book returned successfully! It has been moved to your History.");
            loadBorrowedBooks(null); // Listeden kaybolması için ekranı yenile
        } else {
            showAlert("Error", "Could not return the book.");
        }
    }

    @FXML
    public void handleBorrowFromReserve() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Warning", "Please select a reserved book first!");
            return;
        }

        // Rezerve edilen kitabı ödünç alma Veritabanında reserved'den siler, borrowed'a ekler
        boolean success = system.borrowBook(currentUser, selectedBook);
        if (success) {
            showAlert("Success", "You have successfully borrowed your reserved book!");
            loadReservedBooks(null); // Listeden kaybolması için ekranı yenile
        } else {
            showAlert("Error", "This book is still out of stock. You cannot borrow it yet.");
        }
    }

    @FXML
    public void handleCancelReserve() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Warning", "Please select a reserved book first!");
            return;
        }

        // Veritabanında rezervasyonu iptal et
        boolean success = system.cancelReserve(currentUser, selectedBook);
        if (success) {
            showAlert("Success", "Reservation cancelled successfully.");
            loadReservedBooks(null); // Ekranı yenile
        } else {
            showAlert("Error", "Could not cancel the reservation.");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("student.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
