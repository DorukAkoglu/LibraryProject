package studyLibrary.project;

public class Admin extends User{

    public Admin(int userID, String name, String email, String password) {
        super(userID, name, email, password);
    }
    public void addUser(LibrarySystem system, User u){
        system.addUserDB(u);
    }
    public void removeUser(LibrarySystem system, User u){
        system.deleteUserDB(u);
    }
    public void generateReport(User u){
        //TODO
    }
}



