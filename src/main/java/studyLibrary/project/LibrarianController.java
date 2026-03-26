package studyLibrary.project;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;


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

    refreshBookList();
}

private void refreshBookList() {
    bookListView.getItems().clear();
    bookListView.getItems().addAll(system.getBooks());
}

@FXML
public void handleAddBookAction() {
    int bookID = Integer.parseInt(bookIDField.getText());
    String title = titleField.getText();
    String author = authorField.getText();
    String category = categoryField.getText();
    int copies = Integer.parseInt(copiesField.getText());

    Book newBook = new Book(bookID, title, author, category, copies);
    currentLibrarian.addBook(system, newBook);

    refreshBookList();
}
@FXML
public void handleRemoveBookAction() {
    Book selectedBook = bookListView.getSelectionModel().getSelectedItem();

    if (selectedBook != null) {
        currentLibrarian.removeBook(system, selectedBook);
        refreshBookList();
    }
}
@FXML
public void handleSearchAction(){
    String keyword = searchField.getText();
    bookListView.getItems().clear();
    bookListView.getItems().addAll(currentLibrarian.searchBook(system.getBooks(), keyword));
}
@FXML
public void handleUpdateBookAction(){
Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
    if(selectedBook != null){
        int bookID = Integer.parseInt(bookIDField.getText());
        String title = titleField.getText();
        String author = authorField.getText();
        String category = categoryField.getText();
        int copies = Integer.parseInt(copiesField.getText());

        currentLibrarian.setbookID(bookID, selectedBook);
        currentLibrarian.setTittle(title, selectedBook);
        currentLibrarian.setAuthor(author, selectedBook);
        currentLibrarian.setCategory(category, selectedBook);
        selectedBook.setNumCopies(copies);

        system.updateBook(selectedBook);
        refreshBookList();
}
}
@FXML
public void handleApproveReservationAction() {
    Book selectedBook = bookListView.getSelectionModel().getSelectedItem();

    if (selectedBook != null) {
        boolean approved = currentLibrarian.approveRes(selectedBook);

        if (approved) {
            System.out.println("Reservation approved.");
        } else {
            System.out.println("This book is not available.");
        }
    }
}




}