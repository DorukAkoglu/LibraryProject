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
        this.dueTime = null;
    }

    public int getAverageRating(){
        if(reviews.size()==0){
            return 0;
        }
        int total=0;
        for(int i=0; i<reviews.size();i++){
            Review r = reviews.get(i);
            total+=r.getRating();
        }
        return total / reviews.size();
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

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public boolean deleteReview(Review rev){
        if (this.reviews.contains(rev)){
            this.reviews.remove(rev);
            return true; 
        }
        return false;
    }

    public LocalDate getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalDate dueTime) {
        this.dueTime = dueTime;
    }

    public int getNumCopies() {
        return numCopies;
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
    //Librarianların kitapları düzenleyebilmesi için Setter methodlar
    public void setbookID(int newBookID){
        this.bookID = newBookID;
    }
    public void setTittle(String newTittle){
        this.title = newTittle;
    }
    public void setAuthor(String newAuthor){
        this.author = newAuthor;
    }
    public void setCategory(String newCategory){
        this.category = newCategory;
    }
}
