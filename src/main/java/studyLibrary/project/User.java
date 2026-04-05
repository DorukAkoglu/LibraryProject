package studyLibrary.project;

public class User {

    protected int userID;
    protected String name;
    protected String email;
    protected String password;
    protected String profilePhoto;

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

    public void setProfilePicture(String url) {
        this.profilePhoto = url;
    }
       
    public String getEmail() {
        return email;
    }

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhoto() {
        String url = this.profilePhoto;
        if (url != null && url.contains("/upload/")) {
            return url.replace("/upload/", "/upload/w_200,h_200,c_fill,q_auto/");
        }
        return url;
    }
}
