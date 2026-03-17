package studyLibrary.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends User {
    
    private int age;
    private List<String> courses;
    private List<Book> borrowedBooks;
    private int grade;
    private String availabilityStatus;
    private List<StudyRequest> incomingRequests;

    public Student(int userID, String name, String email, String password, int age, int grade){
        super(userID, name, email, password);
        this.age = age;
        this.grade = grade;
        this.courses = new ArrayList<>();
        this.borrowedBooks = new ArrayList<>();
        this.incomingRequests = new ArrayList<>();
        this.availabilityStatus = "Available";
    }

    public boolean borrowBook(Book b){
        if (b != null && b.isAvailability()){
            this.borrowedBooks.add(b);
            b.setAvailability(false);
            return true;
        }
        return false;
    }

    public void returnBook(Book b){
        if (this.borrowedBooks.contains(b)){
            this.borrowedBooks.remove(b);
            b.setAvailability(true);
        }
    }

    public Review writeReview(Book b, String reviewText, int rating){
        Review newReview = new Review(this, b, reviewText, rating);
        b.addReview(newReview);
        return newReview;
    }

    public List<Book> displayBorrowedBooks(){
        return new ArrayList<>(this.borrowedBooks);
    }
    public boolean startChat(Student student){
        if (student != null && !student.getAvailabilityStatus().equals("Offline")){
            this.availabilityStatus = "In Chat";
            student.setAvailabilityStatus("In Chat");
            return true;
        }
        return false;
    }

    public StudyRequest sendStudyRequest(Student receiver, String course){
        if (receiver == null || receiver.equals(this)){
            return null;
        }
        StudyRequest request = new StudyRequest(this, receiver, course);
        receiver.addIncomingRequest(request);
        return request;
    }
    public Review addReview(String s, int rating, Book b){
        if (b != null && s != null && rating >= 1 && rating <= 5){
            Review review = new Review(this, b, s, rating);
            b.addReviewToBook(review);
            return review;
        }
        return null;
    }
    public boolean endStudySession(){
        if (this.availabilityStatus.equals("In Chat") || this.availabilityStatus.equals("Busy")) {
            this.availabilityStatus = "Available";
            return true;
        }
        return false;
    }
    public void addIncomingRequest(StudyRequest request){
        this.incomingRequests.add(request);
    }
    public Message sendMessage(Student student, String content){
        if (student != null && content != null) {
            return new Message(this, student, content);
        }
        return null;
    }
    public StudyMatch findMatch(){
        return new StudyMatch(this, null, "Pending Search");
    }

    public String getAvailabilityStatus(){
        return this.availabilityStatus; 
    }
    public void setAvailabilityStatus(String status){
        this.availabilityStatus = status; 
    }
    public int getAge(){
        return age; 
    }
    public void setAge(int age){
        this.age = age; 
    }
    public int getGrade(){
        return grade; 
    }
    public void setGrade(int grade){
        this.grade = grade; 
    }
    public List<String> getCourses(){
        return new ArrayList<>(courses); 
    }
    public void addCourse(String course){
        this.courses.add(course); 
    }
    public void removeCourse(String course){
        this.courses.remove(course); 
    }










    @Override
    public boolean equals(Object other) { //Buna studymate kısmında ihtiyacım olduğu için 
    // senden önce yapmam gerekti, classlarımız bağlı olduğu için arada küçük dokunuşlar yapmak zorunlu oluyor. Okuyunca silersin
        if(this == other) return true;
        if(!(other instanceof Student)) return false;
        Student otherStudent = (Student) other;
        if(this.userID != otherStudent.userID || !(this.email.equals(otherStudent.email))){
            return false;
        }
        return true;
    }
    @Override
    public int hashCode() { // Equals metodunu UserID ve Email üzerinden karşılaştırır, her kullanıcının kendine has ID ve emaili olduğunu düşünürsek
        return Objects.hash(userID, email); 
    }
}
