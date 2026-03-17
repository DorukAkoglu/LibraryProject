package studyLibrary.project;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


public class DatabaseManager {
    private MongoClient mongoClient;
    private MongoDatabase database;
    
    public DatabaseManager() {
        
    }
    public User getUserByEmail(String email) {
        // to do
        return null; // Placeholder return
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

    public void saveBook(Book b) {
        // to do
    }

    
}