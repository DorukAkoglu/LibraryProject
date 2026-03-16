package studyLibrary.project;


import java.util.*;

public class LibrarySystem {
    private List <Book> books;
    private List <User> users;
    private List <Student> activeStudents;
    private List <StudyMatch> activeStudyMatches;
    private List <Book> borrowedBooks;
    private List <Message> messages;
    private DatabaseManager db;


    public LibrarySystem() {
        books = new ArrayList <Book> ();
        users = new ArrayList <User> ();
        activeStudents = new ArrayList<Student> ();
        activeStudyMatches = new ArrayList<StudyMatch>();
        borrowedBooks = new ArrayList<Book>();
        messages = new ArrayList<Message>();
        db = new DatabaseManager();
    }

}
