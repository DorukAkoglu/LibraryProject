package studyLibrary.project;

import java.util.List;

public abstract class User {
    protected int userId;
    protected String name;
    protected String email;
    protected String password;

    public String getName(){
        return this.name;
    }

}
