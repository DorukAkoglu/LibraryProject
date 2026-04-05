package studyLibrary.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends User {
    
    private int age;
    private String name;
    private List<String> courses;
    private List<Book> borrowedBooks;
    private int grade;
    private List <Message> messages;
    private String availabilityStatus;
    private List<StudyRequest> incomingRequests;
    private String department;
    private Table reservedTable;
    private int reservedTableNo = 0;
    private boolean isOccupiedTable = false;
    private String courseSelected = "No Course selected"; 

    public Student(int userID, String name, String email, String password, int age, int grade, String department) {
        super(userID, name, email, password);
        this.age = age;
        this.grade = grade;
        this.department = department;
        this.name = name;
        this.courses = new ArrayList<>();
        this.borrowedBooks = new ArrayList<>();
        this.incomingRequests = new ArrayList<>();
        this.availabilityStatus = "Available";
    }
    
    public boolean borrowBook(Book b){
        if (b != null && b.isAvailable()){
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
    /** 
    public Review addReview(String s, int rating, Book b){
        if (b != null && s != null && rating >= 1 && rating <= 5){
            Review review = new Review(this, b, s, rating);
            b.addReviewToBook(review);
            return review;
        }
        return null;
    }
        */
    
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
    public List<StudyRequest> getStudyRequest() {
        return this.incomingRequests;
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
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getName() {
        return name;
    }
    public String getSelectedCourse(){
        return courseSelected;
    }
    public void setSelectedCourse(String course){
        courseSelected = course;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public boolean equals(Object other) { 
        if(this == other) return true;
        if(!(other instanceof Student)) return false;
        Student otherStudent = (Student) other;
        if(this.userID != otherStudent.userID || !(this.email.equals(otherStudent.email))){
            return false;
        }
        return true;
    }
    public Table getReservedTable(){
        return reservedTable;
    }
    public void setreservedTable(Table table){
        reservedTable = table;
    }
    public void addStudyRequest(StudyRequest sr) {
        incomingRequests.add(sr);
    }
    public int getReservedTableNo() {
        return reservedTableNo;
    }
    public void setReservedTableNo(int reservedTableNo) {
        this.reservedTableNo = reservedTableNo;
    }
    public boolean getIsOccupiedTable(){
        return isOccupiedTable;
    }
    public void setIsOccupiedTable(boolean isAtDesk){
        isOccupiedTable = isAtDesk;
    }
    @Override
    public int hashCode() { 
        return Objects.hash(userID, email); 
    }
}
