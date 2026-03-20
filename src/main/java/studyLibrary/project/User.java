package studyLibrary.project;

public class User {

    protected int userID;
    protected String name;
    protected String email;
    protected String password;

    public User(int userID, String name, String email, String password){
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void updateUserID(int newUserID){
        this.userID = newUserID;
    }
    public void updateName(String newName){
        this.name = newName;
    }
    public void updateEmail(String newEmail){
        this.email = newEmail;
    }
    public void updatePassword(String newPassword){
        this.password = newPassword;
    }
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getUserID() {
        return userID;
    }

    
    
}
