package studyLibrary.project;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.CacheHint;

public class LibrarySystem {
    private DatabaseManager db;
    private ArrayList<StudyRequest> requests;
    private ArrayList<StudyMatch> matches;
    private ArrayList<User> users;
    private ArrayList <Book> books;
    private ArrayList <Book> borrowedBooks;
    private ArrayList <Student> activeStudents;
    private ArrayList <Message> chats;


    public LibrarySystem() {
        requests = new ArrayList<StudyRequest>();
        matches = new ArrayList<StudyMatch>();
        books = new ArrayList<Book>();
        activeStudents = new ArrayList<Student>();
        users = new ArrayList<User>();
        borrowedBooks = new ArrayList<Book>();
        db = new DatabaseManager();
        db.connect();
        chats = new ArrayList<Message>();
        
    }
    
    public void acceptRequest(StudyRequest request){
        StudyMatch studyMatch = new StudyMatch(request.getSender(), request.getReceiver(), request.getCourse());
        studyMatch.setCourse(request.getCourse());
        this.activeStudents.remove(request.getSender());
        this.activeStudents.remove(request.getReceiver());
        this.matches.add(studyMatch);
        this.requests.remove(request);
    }

    public void removeRequest(StudyRequest request){
        requests.remove(request);
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
        }
    }

    public boolean deleteUserDB(User u) {
        if (users.contains(u)) {
            this.users.remove(u);
            return true;
        }
        return false;
    }
    
     public void addChat(Message m) {
        this.chats.add(m);
    }

     public void removeChat(Message m) {
        this.chats.remove(m);
    }

    public ArrayList<StudyRequest> getRequests() {
        return (ArrayList<StudyRequest>) requests;
    }

    public ArrayList<StudyMatch> getMatches() {
        return (ArrayList<StudyMatch>) matches;
    }

    public ArrayList<User> getUsers() {
        return (ArrayList<User>)users;
    }

    public ArrayList <Book> getBooks() {
        return books;
    }

    public ArrayList <Book> getBorrowedBooks() {
        return (ArrayList<Book>)borrowedBooks;
    }
    
    public ArrayList <Student> getActiveStudents() {
        return (ArrayList<Student>) activeStudents;
    }

    public List<Message> getChats() {
        return (ArrayList <Message>) chats;
    }

    public User authorizeUser(String email, String password) {
        User user = db.getUserByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void closeSystem() {
        db.closeConnection();
    }
}
