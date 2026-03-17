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

    public boolean login (String email, String password) {
        User user = system.authorizeUser(email, password);
        if (user != null) {
            currentUser = user;
            isLoggedIn = true;
            if (user instanceof Admin) {
                adminController = new AdminController();
            } else if (user instanceof Librarian) {
                librarianController = new LibrarianController();
            } else if (user instanceof Student) {
                studentController = new StudentController();
            }
            return true;
        }
        return false;
    }
}
