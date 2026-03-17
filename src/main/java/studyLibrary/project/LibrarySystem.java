package studyLibrary.project;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.CacheHint;

public class LibrarySystem {
    private DatabaseManager db;
    private List<StudyRequest> requests;
    private List<StudyMatch> matches;
    private List<User> users;
    private List <Book> books;
    private List <Book> borrowedBooks;
    private List <Student> activeStudents;
    private List <Message> chats;


    public LibrarySystem() {
        requests = new ArrayList<StudyRequest>();
        matches = new ArrayList<StudyMatch>();
        books = new ArrayList<Book>();
        activeStudents = new ArrayList<Student>();
        users = new ArrayList<User>();
        borrowedBooks = new ArrayList<Book>();
        db = new DatabaseManager();
        chats = new ArrayList<Message>();
        
    }
    
    public void acceptRequest(StudyRequest request){
        StudyMatch studyMatch = new StudyMatch(request.getSender(), request.getReceiver());
        studyMatch.setCourse(request.getCourse());
        this.activeStudents.remove(request.getSender());
        this.activeStudents.remove(request.getReceiver());
        this.matches.add(studyMatch);
        this.requests.remove(request);
    }

    public void removeRequest(StudyRequest request){
        requests.remove(request);
    }
}
