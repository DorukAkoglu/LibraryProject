package studyLibrary.project;

public class MainController {
    private boolean isLoggedIn;
    private static User currentUser;
    private LibrarySystem system;
    //private AdminController adminController;
    //private LibrarianController librarianController;
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
            return true;
        }
        return false;
    }
    public void routeUser(User u) {
        if (u instanceof Student) {
            studentController = new StudentController();
        } else if (u instanceof Librarian) {
            //librarianController = new LibrarianController();
        } else if (u instanceof Admin) {
            //adminController = new AdminController();
        }
    }
    public static User getCurrentUser() {
        return currentUser;
    }
}   
