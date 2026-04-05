package studyLibrary.project;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

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
        ArrayList<Book> allBooks = system.getBooks();
        ArrayList<String> uniqueCategories = new ArrayList<>();
        for (Book b : allBooks) {
            String cat = b.getCategory();
            if (cat != null && !cat.trim().isEmpty() && !uniqueCategories.contains(cat)) {
                uniqueCategories.add(cat);
            }
        }
        categoryFilter.getItems().add("All");
        categoryFilter.getItems().addAll(uniqueCategories);
        categoryFilter.getSelectionModel().selectFirst();

        setupBookCellFactory();

    }

    private void setupBookCellFactory() {
        bookListView.setCellFactory(param -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                
                if (empty || book == null) {
                    setGraphic(null);
                } else {
                    // Ana Kutu
                    HBox root = new HBox(20);
                    root.getStyleClass().add("cell-root-box");

                    // Sol Taraf: Kapak Fotoğrafı
                    ImageView bookCover = new ImageView();
                    try {
                        Image img = new Image(getClass().getResourceAsStream("images/download.jpg"));
                        bookCover.setImage(img);
                        bookCover.setFitHeight(120);
                        bookCover.setFitWidth(85);
                        bookCover.setPreserveRatio(true);
                    } catch (Exception e) {}

                    // Sağ Taraf: Detaylar Kutusu
                    VBox detailsBox = new VBox(8);
                    
                    Label titleLabel = new Label("Title: " + book.getTitle());
                    titleLabel.getStyleClass().add("cell-title");
                    
                    Label authorLabel = new Label("Author: " + book.getAuthor());
                    authorLabel.getStyleClass().add("cell-author");

                    // Yıldızlar
                    int rating = 4; // Kendi sisteminde varsa book.getRating() yap
                    StringBuilder starText = new StringBuilder();
                    for(int i=0; i<5; i++) { starText.append((i < rating) ? "★" : "☆"); }
                    Label starsLabel = new Label(starText.toString());
                    starsLabel.getStyleClass().add("cell-stars");

                    Label availLabel = new Label();
                    if (book.isAvailable()) {
                        availLabel.setText("Availability: Available");
                    } else {
                        availLabel.setText("Availability: Out of Stock");
                    }
                    availLabel.getStyleClass().add("cell-availability");

                    // Butonlar
                    HBox buttonsBox = new HBox(10);
                    Button borrowBtn = new Button("Borrow");
                    borrowBtn.getStyleClass().add("cell-action-button");

                    Button reserveBtn = new Button("Reserve");
                    reserveBtn.getStyleClass().add("cell-action-button");

                    Button detailsBtn = new Button("Show Details");
                    detailsBtn.getStyleClass().add("cell-details-button");
                    
                    // Tıklama Olayları
                    detailsBtn.setOnAction(e -> {
                         System.out.println(book.getTitle() + " detay sayfasına gidiliyor...");
                    });
                    
                    borrowBtn.setOnAction(e -> {
                         System.out.println(book.getTitle() + " ödünç alınmak istendi.");
                    });

                    buttonsBox.getChildren().addAll(borrowBtn, reserveBtn, detailsBtn);
                    detailsBox.getChildren().addAll(titleLabel, authorLabel, starsLabel, availLabel, buttonsBox);
                    root.getChildren().addAll(bookCover, detailsBox);

                    setGraphic(root);
                }
            }
        });
    }

    @FXML
    private void goBackToDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }

    @FXML
    public void handleSearchAction(){
       String searchWord = "";
        if (searchField.getText() != null) {
        searchWord = searchField.getText().toLowerCase();
        }
        String category = categoryFilter.getValue();
        boolean onlyAvailable = availabilityFilter.isSelected();

        ArrayList<Book> allBooks = system.getBooks();
        List<Book> results = new ArrayList<>();

        for (Book b : allBooks){
            boolean titleMatches = searchWord.isEmpty() || b.getTitle().toLowerCase().contains(searchWord);
            boolean categoryMatches = category == null || category.equals("All") || b.getCategory().equals(category);
            boolean availabilityMatches = !onlyAvailable || b.isAvailable();
            if (titleMatches && categoryMatches && availabilityMatches){
                results.add(b);
            } 
        }
        bookListView.getItems().clear();
        bookListView.getItems().addAll(results);

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
            
            if (book.getNumCopies() == 0) {
                book.setAvailability(false);
            }
            
            if (!system.getBorrowedBooks().contains(book)) {
                system.getBorrowedBooks().add(book);
            }
            system.updateBook(book);
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
