package studyLibrary.project;

import java.util.ArrayList;
import java.util.List;

public class Review {
    private User user;
    private Book book;
    private String content;
    private List<Comment> comments;
    private int rating;

    public Review(User user, Book book, String content, int rating){
        this.user = user;
        this.book = book;
        this.content = content;
        this.rating = rating;
        this.comments = new ArrayList<>();
    }

    public void editReview(String s, int rating){
        this.content = s;
        this.rating = rating;
    }

    public void addComment(Comment com) {
        this.comments.add(com);
    }

    public boolean deleteComment(Comment delCom){
        if(this.comments.contains(delCom)){
            this.comments.remove(delCom);
            return true;
        }
        return false;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
