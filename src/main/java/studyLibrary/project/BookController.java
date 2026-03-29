package studyLibrary.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class BookController {
    @FXML private TextField searchField;
    @FXML private ListView<Book> bookListView;
    @FXML private Button borrowButton;
    @FXML private Button returnButton;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> pagesFilter;
    @FXML private CheckBox availabilityFilter;

    private LibrarySystem system;
    private static final int BORROW_DURATION = 14;
    private User currentUser;

    @FXML
    public void initialize(){
        this.system = LibrarySystem.getInstance();
        pagesFilter.getItems().addAll("Max 100", "Max 300", "Max 500", "No Limit");

    }

    @FXML
    public void handleSearchAction(){
        String searchWord = searchField.getText();
        String category = categoryFilter.getValue();
        String maxPages = pagesFilter.getValue();
        boolean onlyAv = availabilityFilter.isSelected();

        //List<Book> results = searchBook(searchWord, category, maxPages, onlyAv);

        // Bulunan kitapları ekrandaki listeye  bas
        bookListView.getItems().clear();
        //bookListView.getItems().addAll(results);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleBorrowAction(){
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null){
            showAlert("Warning", "Please select a book from the list!", Alert.AlertType.WARNING);
            return;
        }

        boolean isSuccessful = borrowBook(currentUser, selectedBook);
        if (isSuccessful){
            showAlert("Success", "Book successfully borrowed!", Alert.AlertType.INFORMATION);
            handleSearchAction(); // Listeyi güncelle
        }else{
            showAlert("Error", "This book is currently out of stock!", Alert.AlertType.ERROR);
        }
    }

    

    public boolean borrowBook(User u, Book book){
        if (book.isAvailable()) {
            book.setNumCopies(book.getNumCopies() - 1);
            book.setDueTime(LocalDate.now().plusDays(BORROW_DURATION));
            
            if (!system.getBorrowedBooks().contains(book)) {
                system.getBorrowedBooks().add(book);
            }
            system.updateBook(book);
            return true;
        }
        return false;
    }

    public boolean returnBook(User u, Book b){
        b.setNumCopies(b.getNumCopies() + 1);
        b.setDueTime(null);
        system.getBorrowedBooks().remove(b);
        system.updateBook(b);
        return true;
    }

    public boolean extendBook(User u, Book b){
        if (b.getDueTime() != null) {
            b.setDueTime(b.getDueTime().plusDays(7));
            system.updateBook(b); 
            return true;
        }
        return false;
    }

    public boolean reserveBook(User u, Book b){
        if (!b.isAvailable()) {
            return true;
        }
        return false;
    }
}
