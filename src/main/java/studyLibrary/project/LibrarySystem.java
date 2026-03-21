package studyLibrary.project;
import java.util.ArrayList;
import java.util.List;

public class LibrarySystem {
    private static LibrarySystem instance;
    private DatabaseManager db;
    private ArrayList<StudyRequest> requests;
    private ArrayList<StudyMatch> matches;
    private ArrayList<User> users;
    private ArrayList <Book> books;
    private ArrayList <Book> borrowedBooks;
    private ArrayList <Student> activeStudents;
    private ArrayList <Message> chats;


    public LibrarySystem() {
        this.db = new DatabaseManager();
        this.db.connect();
        this.requests = this.db.getStudyRequests();
        this.matches = this.db.getStudyMatches();
        this.books = this.db.getBooks();
        this.activeStudents = this.db.getActiveStudents();
        this.users = this.db.getUsers();
        this.borrowedBooks = this.db.getBorrowedBooks();
        this.chats = this.db.getChats();
        
    }
    public static LibrarySystem getInstance() {
        if (instance == null) {
            instance = new LibrarySystem();
        }
        return instance;
    }
    
    public void acceptRequest(StudyRequest request){
        StudyMatch studyMatch = new StudyMatch(request.getSender(), request.getReceiver(), request.getCourse());
        studyMatch.setCourse(request.getCourse());
        this.activeStudents.remove(request.getSender());
        this.activeStudents.remove(request.getReceiver());
        this.db.updateUser(request.getReceiver());
        this.db.updateUser(request.getSender());
        this.matches.add(studyMatch);
        this.requests.remove(request);
        this.db.saveStudyMatch(studyMatch);
        this.db.removeStudyRequest(request);    
    }

    public void removeRequest(StudyRequest request){
        requests.remove(request);
        db.removeStudyRequest(request);
    }

    public void addBookDB(Book b) {
        if (b != null && !books.contains(b)) {
            books.add(b);
            db.saveBook(b);
        }

    }
    public void deleteBookDB(Book b) {
        if (books.contains(b)) {
            books.remove(b);
            db.removeBook(b);
        }
    }

    public void addUserDB(User u) {
        if(!users.contains(u)){
            this.users.add(u);
            db.saveUser(u);
        }
    }

    public boolean deleteUserDB(User u) {
        if (users.contains(u)) {
            this.users.remove(u);
            db.removeUser(u);
            return true;
        }
        return false;
    }
    
     public void addChat(Message m) {
        this.chats.add(m);
        db.saveChat(m);
    }

     public void removeChat(Message m) {
        this.chats.remove(m);
        db.removeChat(m);
    }

    public ArrayList<StudyRequest> getRequests() {
        return requests;
    }

    public ArrayList<StudyMatch> getMatches() {
        return matches;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList <Book> getBooks() {
        return books;
    }

    public ArrayList <Book> getBorrowedBooks() {
        return borrowedBooks;
    }
    
    public ArrayList <Student> getActiveStudents() {
        return  activeStudents;
    }

    public List<Message> getChats() {
        return chats;
    }

    public User authorizeUser(int userID, String password) {
        User user = db.getUserByID(userID);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void addActiveStudent(Student s) {
        if (!activeStudents.contains(s)) {
            activeStudents.add(s);
            db.updateUser(s);
        }
    }

    public void removeActiveStudent(Student s) {
        if (activeStudents.contains(s)) {
            activeStudents.remove(s);
            db.updateUser(s);
        }
    }

    public void closeSystem() {
        db.closeConnection();
    }

    public void addStudyRequest(StudyRequest r) {
        if (!requests.contains(r)) {
            requests.add(r);
            db.saveStudyRequest(r);
        }
    }

    public void updateBook(Book b) {
        db.updateBook(b);
    }
    public void updateUserDB(User u) {
        if (u != null) {
            db.updateUser(u);
        }
    }
}
