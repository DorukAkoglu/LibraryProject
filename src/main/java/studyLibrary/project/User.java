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
    
    public void updateProfile(){
        //TODO
    }

    //database kısmı için getterların hepsi lazımdı.
    public String getPassword() {
        return null;
    }

    public String getEmail() {
        return email;
    }

    public int getUserID() {
        return userID;
    }

    
    
}
