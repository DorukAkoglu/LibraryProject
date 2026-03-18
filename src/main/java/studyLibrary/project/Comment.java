package studyLibrary.project;

public class Comment {
    private User user;
    private String content;

    public Comment(User user, String content){
        this.user = user;
        this.content = content;
    }

    public boolean editComment(String com){
        this.content = com;
        return true;
    }

    public boolean deleteComment(){
        //TODO
        return false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
