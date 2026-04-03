package studyLibrary.project;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;


public class LibrarianController {
    
    @FXML private TextField searchField;
    @FXML private TextField bookIDField;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField categoryField;
    @FXML private TextField copiesField;
    @FXML private ListView<Book> bookListView;
    private LibrarySystem system;
    private Librarian currentLibrarian;
    @FXML
public void initialize() {
    system = LibrarySystem.getInstance();

    if (MainController.getCurrentUser() instanceof Librarian librarian) {
        currentLibrarian = librarian;
    }

    bookListView.setCellFactory(list -> new ListCell<>() {
        @Override
        protected void updateItem(Book book, boolean empty) {
            super.updateItem(book, empty);
            if (empty || book == null) {
                setText(null);
            } else {
                String availability = book.isAvailable() ? "Available" : "Unavailable";
                setText("#" + book.getBookID() + "  " + book.getTitle()
                        + "\n" + book.getAuthor() + " | " + book.getCategory()
                        + " | Copies: " + book.getNumCopies()
                        + " | " + availability);
            }
        }
    });

    bookListView.getSelectionModel().selectedItemProperty().addListener((obs, oldBook, selectedBook) -> {
        if (selectedBook != null) {
            fillForm(selectedBook);
        }
    });

    refreshBookList();
}

private void refreshBookList() {
    bookListView.getItems().clear();
    bookListView.getItems().addAll(system.getBooks());
}

@FXML
public void handleAddBookAction() {
    if (!ensureLibrarian()) {
        return;
    }
    try {
        int bookID = Integer.parseInt(bookIDField.getText().trim());
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String category = categoryField.getText().trim();
        int copies = Integer.parseInt(copiesField.getText().trim());

        Book newBook = new Book(bookID, title, author, category, copies);
        currentLibrarian.addBook(system, newBook);

        refreshBookList();
        clearForm();
    } catch (NumberFormatException e) {
        showMessage("Book ID and copies must be numbers.");
    }
}
@FXML
public void handleRemoveBookAction() {
    if (!ensureLibrarian()) {
        return;
    }
    Book selectedBook = bookListView.getSelectionModel().getSelectedItem();

    if (selectedBook != null) {
        currentLibrarian.removeBook(system, selectedBook);
        refreshBookList();
        clearForm();
    }
}
@FXML
public void handleSearchAction(){
    if (!ensureLibrarian()) {
        return;
    }
    String keyword = searchField.getText();
    if (keyword == null || keyword.isBlank()) {
        refreshBookList();
        return;
    }
    bookListView.getItems().clear();
    bookListView.getItems().addAll(currentLibrarian.searchBook(system.getBooks(), keyword));
}
@FXML
public void handleUpdateBookAction(){
    if (!ensureLibrarian()) {
        return;
    }
Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
    if(selectedBook != null){
        try {
            int bookID = Integer.parseInt(bookIDField.getText().trim());
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String category = categoryField.getText().trim();
            int copies = Integer.parseInt(copiesField.getText().trim());

            currentLibrarian.setbookID(bookID, selectedBook);
            currentLibrarian.setTittle(title, selectedBook);
            currentLibrarian.setAuthor(author, selectedBook);
            currentLibrarian.setCategory(category, selectedBook);
            selectedBook.setNumCopies(copies);

            system.updateBook(selectedBook);
            refreshBookList();
        } catch (NumberFormatException e) {
            showMessage("Book ID and copies must be numbers.");
        }
    } else {
        showMessage("Select a book before updating.");
    }
}
@FXML
public void handleApproveReservationAction() {
    if (!ensureLibrarian()) {
        return;
    }
    Book selectedBook = bookListView.getSelectionModel().getSelectedItem();

    if (selectedBook != null) {
        boolean approved = currentLibrarian.approveRes(selectedBook);

        if (approved) {
            showMessage("Reservation approved.");
        } else {
            showMessage("This book is not available.");
        }
    } else {
        showMessage("Select a book before approving a reservation.");
    }
}

private void fillForm(Book selectedBook) {
    bookIDField.setText(String.valueOf(selectedBook.getBookID()));
    titleField.setText(selectedBook.getTitle());
    authorField.setText(selectedBook.getAuthor());
    categoryField.setText(selectedBook.getCategory());
    copiesField.setText(String.valueOf(selectedBook.getNumCopies()));
}

private void clearForm() {
    bookIDField.clear();
    titleField.clear();
    authorField.clear();
    categoryField.clear();
    copiesField.clear();
}

private void showMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setHeaderText(null);
    alert.setTitle("Librarian Panel");
    alert.setContentText(message);
    alert.show();
}

private boolean ensureLibrarian() {
    if (currentLibrarian == null) {
        showMessage("No librarian session is active.");
        return false;
    }
    return true;
}




}
