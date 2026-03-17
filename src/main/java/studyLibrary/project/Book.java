package studyLibrary.project;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class Book {
    private int bookID;
    private String title;
    private String author;
    private String category;
    private boolean availability;
    private List<Review> reviews;
    private LocalDate dueTime;
    private int numCopies;

    public Book(int bookID, String title, String author, String category, int numCopies){
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.category = category;
        this.numCopies = numCopies;

        this.availability = calculateAv() ;
        this.reviews=new ArrayList<>();
    }

    public int getAverageRating(){
        return 0;
    }

    public int getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return availability;
    }

    private boolean calculateAv(){
        if (this.numCopies > 0){
            return true ;
        }else{
            return false;
        }
    }
    public void setNumCopies(int numCopies) {
        this.numCopies = numCopies;
        // Kopya sayısı değiştiği an müsaitlik durumunu yeniden uzun yoldan hesaplama
        this.availability = calculateAv();
    }
}
