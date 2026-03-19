package studyLibrary.project;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DatabaseManager {
    private MongoClient mongoClient;
    private MongoDatabase database;
    
    public DatabaseManager() {
        mongoClient = null;
        database = null;
    }
    public User getUserByEmail(String email) {
        MongoCollection <Document> collection = database.getCollection("users");
        Document doc = collection.find(new Document("email",email)).first();

        if (doc == null) return null;

        return new User(doc.getInteger("userID"), doc.getString("name"), doc.getString("email"), doc.getString("password"));

    }
    public void connect() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("LibraryDB");

    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
    public Book getBookByBookID(int bookID) {
        MongoCollection<Document> collection = database.getCollection("books");
        Document doc = collection.find(new Document("bookID", bookID)).first();

        if (doc == null) {
            return null;
        }

        return new Book(doc.getInteger("bookID"), doc.getString("title"), doc.getString("author"), doc.getString("category"),doc.getInteger("numofCopies"));    
    }
    public void saveBook(Book b) {
        ArrayList <Document> reviewDocs = new ArrayList<>();
        for (Review r : b.getReviews()) {
            reviewDocs.add(new Document("content", r.getContent()) .append("ratings", r.getRating()));
        }

        MongoCollection<Document> collection = database.getCollection("books");
        Document doc = new Document("bookID", b.getBookID())
                        .append("title", b.getTitle())
                        .append("author", b.getAuthor())
                        .append("averagerating", b.getAverageRating())
                        .append("reviews", reviewDocs)
                        .append("numofCopies", b.getNumCopies())
                        .append("category", b.getCategory())
                        .append("duetime", b.getDueTime())
                        .append("available", b.isAvailable());
        collection.insertOne(doc);
    }

    public void saveUser(User u) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document doc = new Document("userID",   u.getUserID())
                .append("name",     u.getName())
                .append("email",    u.getEmail())
                .append("password", u.getPassword());
        collection.insertOne(doc);
    }

    public void removeBook(Book b) {
        //to do
    }

    
}