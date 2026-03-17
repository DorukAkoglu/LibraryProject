package studyLibrary.project;

import java.util.List;

public class Review {
    private User user;
    private Book book;
    private String content;
    private List<Comment> comments;
    private int rating;

    public boolean editReview(String s, int rating){
        return false;
    }

    public boolean deleteReview(){
        return false;
    }

    public Comment addComment(Review r){
        return null;
    }
}
