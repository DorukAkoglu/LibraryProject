package studyLibrary.project;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibrarianController {

    @FXML private ImageView profileImage;
    @FXML private Label librarianNameLabel;
    @FXML private Label librarianRoleLabel;

    @FXML private Button overviewButton;
    @FXML private Button searchButton;
    @FXML private Button addButton;
    @FXML private Button manageButton;

    @FXML private VBox overviewSection;
    @FXML private VBox searchSection;
    @FXML private VBox addSection;
    @FXML private VBox manageSection;

    @FXML private Label totalBooksLabel;
    @FXML private Label availableBooksLabel;
    @FXML private Label borrowedBooksLabel;
    @FXML private Label categoryCountLabel;

    @FXML private TextField searchField;
    @FXML private ListView<Book> searchBookListView;

    @FXML private TextField addBookIDField;
    @FXML private TextField addTitleField;
    @FXML private TextField addAuthorField;
    @FXML private TextField addCategoryField;
    @FXML private TextField addCopiesField;

    @FXML private ListView<Book> manageBookListView;
    @FXML private TextField editBookIDField;
    @FXML private TextField editTitleField;
    @FXML private TextField editAuthorField;
    @FXML private TextField editCategoryField;
    @FXML private TextField editCopiesField;

    private LibrarySystem system;
    private Librarian currentLibrarian;

    @FXML
    public void initialize() {
        system = LibrarySystem.getInstance();

        if (MainController.getCurrentUser() instanceof Librarian librarian) {
            currentLibrarian = librarian;
        }

        configureProfile();
        configureLists();
        refreshAllData();
        showOverviewSection();
    }

    @FXML
    public void showOverviewSection() {
        setActiveSection(overviewSection, overviewButton);
        refreshStats();
    }

    @FXML
    public void showSearchSection() {
        setActiveSection(searchSection, searchButton);
        refreshSearchList();
    }

    @FXML
    public void showAddSection() {
        setActiveSection(addSection, addButton);
    }

    @FXML
    public void showManageSection() {
        setActiveSection(manageSection, manageButton);
        refreshManageList();
    }

    @FXML
    public void handleSearchAction() {
        if (!ensureLibrarian()) {
            return;
        }

        String keyword = searchField.getText();
        if (keyword == null || keyword.isBlank()) {
            refreshSearchList();
            return;
        }

        searchBookListView.getItems().clear();
        searchBookListView.getItems().addAll(currentLibrarian.searchBook(system.getBooks(), keyword.trim()));
    }

    @FXML
    public void handleRefreshAction() {
        refreshAllData();
    }

    @FXML
    public void handleAddBookAction() {
        if (!ensureLibrarian()) {
            return;
        }

        try {
            Book newBook = createBookFromAddForm();
            currentLibrarian.addBook(system, newBook);
            refreshAllData();
            clearAddForm();
            showMessage("Book added successfully.");
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage());
        }
    }

    @FXML
    public void handleUpdateBookAction() {
        if (!ensureLibrarian()) {
            return;
        }

        Book selectedBook = manageBookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showMessage("Select a book first.");
            return;
        }

        try {
            updateExistingBook(selectedBook);
            system.updateBook(selectedBook);
            refreshAllData();
            showMessage("Book updated successfully.");
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage());
        }
    }

    @FXML
    public void handleRemoveBookAction() {
        if (!ensureLibrarian()) {
            return;
        }

        Book selectedBook = manageBookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showMessage("Select a book first.");
            return;
        }

        currentLibrarian.removeBook(system, selectedBook);
        refreshAllData();
        clearEditForm();
        showMessage("Book removed successfully.");
    }

    @FXML
    public void handleApproveReservationAction() {
        if (!ensureLibrarian()) {
            return;
        }

        Book selectedBook = manageBookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showMessage("Select a book first.");
            return;
        }

        if (currentLibrarian.approveRes(selectedBook)) {
            showMessage("Reservation approved.");
        } else {
            showMessage("This book is not available.");
        }
    }

    @FXML
    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            App.PRIMARY_STAGE.getScene().setRoot(root);
        } catch (IOException e) {
            showMessage("Could not load login screen.");
        }
    }

    private void configureProfile() {
        if (currentLibrarian != null) {
            librarianNameLabel.setText(currentLibrarian.getName());
            librarianRoleLabel.setText("Library Staff");
            if (currentLibrarian.getProfilePhoto() != null && !currentLibrarian.getProfilePhoto().isBlank()) {
                profileImage.setImage(new Image(currentLibrarian.getProfilePhoto(), true));
            }
        }
    }

    private void configureLists() {
        searchBookListView.setCellFactory(list -> createBookCell());
        manageBookListView.setCellFactory(list -> createBookCell());

        manageBookListView.getSelectionModel().selectedItemProperty().addListener((obs, oldBook, selectedBook) -> {
            if (selectedBook != null) {
                fillEditForm(selectedBook);
            }
        });
    }

    private ListCell<Book> createBookCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setText(null);
                } else {
                    setText(formatBook(book));
                }
            }
        };
    }

    private void refreshAllData() {
        refreshStats();
        refreshSearchList();
        refreshManageList();
    }

    private void refreshStats() {
        int total = system.getBooks().size();
        int available = 0;
        int borrowed = 0;
        Set<String> categories = new HashSet<>();

        for (Book book : system.getBooks()) {
            if (book.isAvailable()) {
                available++;
            } else {
                borrowed++;
            }

            if (book.getCategory() != null && !book.getCategory().isBlank()) {
                categories.add(book.getCategory().trim().toLowerCase());
            }
        }

        totalBooksLabel.setText(String.valueOf(total));
        availableBooksLabel.setText(String.valueOf(available));
        borrowedBooksLabel.setText(String.valueOf(borrowed));
        categoryCountLabel.setText(String.valueOf(categories.size()));
    }

    private void refreshSearchList() {
        searchBookListView.getItems().setAll(system.getBooks());
    }

    private void refreshManageList() {
        manageBookListView.getItems().setAll(system.getBooks());
    }

    private void setActiveSection(VBox activeSection, Button activeButton) {
        overviewSection.setVisible(false);
        overviewSection.setManaged(false);
        searchSection.setVisible(false);
        searchSection.setManaged(false);
        addSection.setVisible(false);
        addSection.setManaged(false);
        manageSection.setVisible(false);
        manageSection.setManaged(false);

        activeSection.setVisible(true);
        activeSection.setManaged(true);
    }

    private Book createBookFromAddForm() {
        int bookID = parseRequiredInt(addBookIDField.getText(), "Book ID must be a number.");
        String title = requireText(addTitleField.getText(), "Title is required.");
        String author = requireText(addAuthorField.getText(), "Author is required.");
        String category = requireText(addCategoryField.getText(), "Category is required.");
        int copies = parseRequiredInt(addCopiesField.getText(), "Copies must be a number.");
        return new Book(bookID, title, author, category, copies);
    }

    private void updateExistingBook(Book book) {
        int bookID = parseRequiredInt(editBookIDField.getText(), "Book ID must be a number.");
        String title = requireText(editTitleField.getText(), "Title is required.");
        String author = requireText(editAuthorField.getText(), "Author is required.");
        String category = requireText(editCategoryField.getText(), "Category is required.");
        int copies = parseRequiredInt(editCopiesField.getText(), "Copies must be a number.");

        currentLibrarian.setbookID(bookID, book);
        currentLibrarian.setTittle(title, book);
        currentLibrarian.setAuthor(author, book);
        currentLibrarian.setCategory(category, book);
        book.setNumCopies(copies);
    }

    private void fillEditForm(Book book) {
        editBookIDField.setText(String.valueOf(book.getBookID()));
        editTitleField.setText(book.getTitle());
        editAuthorField.setText(book.getAuthor());
        editCategoryField.setText(book.getCategory());
        editCopiesField.setText(String.valueOf(book.getNumCopies()));
    }

    private void clearAddForm() {
        addBookIDField.clear();
        addTitleField.clear();
        addAuthorField.clear();
        addCategoryField.clear();
        addCopiesField.clear();
    }

    private void clearEditForm() {
        editBookIDField.clear();
        editTitleField.clear();
        editAuthorField.clear();
        editCategoryField.clear();
        editCopiesField.clear();
    }

    private String formatBook(Book book) {
        String availability = book.isAvailable() ? "Available" : "Borrowed";
        return "#" + book.getBookID() + "  " + book.getTitle()
                + "\n" + book.getAuthor() + " | " + book.getCategory()
                + "\nCopies: " + book.getNumCopies() + " | " + availability;
    }

    private int parseRequiredInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private String requireText(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value.trim();
    }

    private boolean ensureLibrarian() {
        if (currentLibrarian == null) {
            showMessage("No librarian session is active.");
            return false;
        }
        return true;
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Librarian Panel");
        alert.setContentText(message);
        alert.show();
    }
    public void switchToDeskReservations(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
    public void switchToReports(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reportsForLibrarian.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
}
