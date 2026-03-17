package studyLibrary.project;

public class MainController {
    private boolean isLoggedIn;
    private User currentUser;
    private LibrarySystem system;
    private AdminController adminController;
    private LibrarianController librarianController;
    private StudentController studentController;

    public MainController() {
        currentUser = null;
        isLoggedIn = false;
        system = new LibrarySystem();
    }   
}
