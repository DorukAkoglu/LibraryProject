package studyLibrary.project;
import java.io.IOException;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class DatabaseManager {
    private MongoClient mongoClient;
    private CloudinaryManager cloudinaryManager;
    private MongoDatabase database;
    
    public DatabaseManager() {
        mongoClient = null;
        database = null;
        try {
            this.cloudinaryManager = new CloudinaryManager();
        } catch (IOException e) {
            System.err.println("Cloudinary network error: " + e.getMessage());
        }
    }
    public User getUserByEmail(String email) {
        Document doc = database.getCollection("users")
                            .find(new Document("email", email)).first();
    if (doc == null) return null;

        String role = doc.getString("role");
        if ("student".equals(role)) {
            Student s = new Student(doc.getInteger("userID"), doc.getString("name"),
                            doc.getString("email"),   doc.getString("password"),
                            doc.getInteger("age"),    doc.getInteger("grade"),
                            doc.getString("department"));
            s.setProfilePicture(doc.getString("profilePhoto"));
            return s;
        } else if ("librarian".equals(role)) {
            Librarian lb = new Librarian(doc.getInteger("userID"), doc.getString("name"),
                                doc.getString("email"),   doc.getString("password"));
            lb.setProfilePicture(doc.getString("profilePhoto"));
            return lb;
        }
        Admin a =new Admin(doc.getInteger("userID"), doc.getString("name"),
                        doc.getString("email"),   doc.getString("password"));
        a.setProfilePicture(doc.getString("profilePhoto"));
        return a;
    }
     public User getUserByID(int userID) {
        Document doc = database.getCollection("users")
                            .find(new Document("userID", userID)).first();
        if (doc == null) return null;

        String role = doc.getString("role");
        if ("student".equals(role)) {
            Student s = new Student(doc.getInteger("userID"), doc.getString("name"),
                            doc.getString("email"),   doc.getString("password"),
                            doc.getInteger("age"),    doc.getInteger("grade"),
                            doc.getString("department"));
            s.setProfilePicture(doc.getString("profilePhoto"));
            return s;
        } else if ("librarian".equals(role)) {
            Librarian lb = new Librarian(doc.getInteger("userID"), doc.getString("name"),
                                doc.getString("email"),   doc.getString("password"));
            lb.setProfilePicture(doc.getString("profilePhoto"));
            return lb;
        }
        Admin a =new Admin(doc.getInteger("userID"), doc.getString("name"),
                        doc.getString("email"),   doc.getString("password"));
        a.setProfilePicture(doc.getString("profilePhoto"));
        return a;
    }

    public void connect() {
        mongoClient = MongoClients.create("mongodb+srv://mehmetsamedtek_db_user:bilkent123@cluster0.nxfstjh.mongodb.net/?appName=Cluster0");
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
            reviewDocs.add(new Document("userEmail", r.getUser().getEmail()).append("bookID", r.getBook().getBookID()).append("content", r.getContent()) .append("ratings", r.getRating()));
        }

        MongoCollection<Document> collection = database.getCollection("books");
        Document doc = new Document("bookID", b.getBookID())
                        .append("title", b.getTitle())
                        .append("author", b.getAuthor())
                        .append("averagerating", b.getAverageRating())
                        .append("reviews", reviewDocs)
                        .append("numofCopies", b.getNumCopies())
                        .append("category", b.getCategory())
                        .append("duetime", b.getDueTime().toString())
                        .append("available", b.isAvailable());
        collection.insertOne(doc);
    }

    public void saveUser(User u) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document doc = new Document("userID",   u.getUserID())
                .append("name",     u.getName())
                .append("email",    u.getEmail())
                .append("password", u.getPassword());   

        if (u instanceof Student s) {
            doc.append("role", "student")
            .append("age", s.getAge())     
           .append("grade", s.getGrade())
           .append("department", s.getDepartment())
            .append("availabilityStatus", s.getAvailabilityStatus())
            .append("profilePhoto", s.getProfilePhoto());
        } else if (u instanceof Librarian l) {
            doc.append("role", "librarian");
        } else {
            doc.append("role", "admin");
        }
        collection.insertOne(doc);
}

    public void removeBook(Book b) {
        database.getCollection("books").deleteOne(new Document("bookID", b.getBookID()));
    }

    public void removeUser(User u) {
        database.getCollection("users").deleteOne(new Document("userID", u.getUserID()));
    }

    public void saveChat(Message m) {
        MongoCollection<Document> collection = database.getCollection("chats");
        Document doc = new Document("senderEmail", m.getSender().getEmail())
                .append("receiverEmail", m.getReceiver().getEmail())
                .append("content", m.getContent())
                .append("timestamp", m.getTimestamp().toString());
        collection.insertOne(doc);
    }

    public void removeChat(Message m) {
        database.getCollection("chats")
            .deleteOne(new Document("senderEmail",   m.getSender().getEmail())
            .append("timestamp", m.getTimestamp().toString()));
    }

    public void saveStudyRequest(StudyRequest r) {
        MongoCollection<Document> collection = database.getCollection("studyRequests");
        Document doc = new Document("senderEmail", r.getSender().getEmail())
                .append("receiverEmail", r.getReceiver().getEmail())
                .append("course", r.getCourse())
                .append("status", r.getStatus().toString());
        collection.insertOne(doc);
    }
    public void updateUser(User u) {
        Document update = new Document("$set", new Document("name", u.getName())
                .append("email",    u.getEmail())
                .append("password", u.getPassword())).append("profilePhoto", u.getProfilePhoto());

        if (u instanceof Student) {
            update.get("$set", Document.class)
                .append("availabilityStatus", ((Student) u).getAvailabilityStatus());
        }
        database.getCollection("users")
                .updateOne(new Document("userID", u.getUserID()), update);
    }

    public void removeStudyRequest(StudyRequest r) {
        database.getCollection("studyRequests")
            .deleteOne(new Document("senderEmail",   r.getSender().getEmail())
            .append("receiverEmail", r.getReceiver().getEmail())
            .append("course",     r.getCourse()));
    }

    public void saveStudyMatch(StudyMatch m) {
        MongoCollection<Document> collection = database.getCollection("studyMatches");
        Document doc = new Document("student1Email", m.getStudent1().getEmail())
                .append("student2Email", m.getStudent2().getEmail())
                .append("course", m.getCourse());
        collection.insertOne(doc);
    }

    public void removeStudyMatch(StudyMatch m) {
        database.getCollection("studyMatches")
            .deleteOne(new Document("student1Email", m.getStudent1().getEmail())
            .append("student2Email", m.getStudent2().getEmail())
            .append("course",     m.getCourse()));
    }

    public void updateBook(Book b) {
        ArrayList <Document> reviewDocs = new ArrayList<>();
        for (Review r : b.getReviews()) {
            reviewDocs.add(new Document("userEmail", r.getUser().getEmail()).append("bookID", r.getBook().getBookID()).append("content", r.getContent()) .append("ratings", r.getRating()));
        }
        database.getCollection("books").updateOne(new Document("bookID", b.getBookID()),new Document("$set", new Document("reviews", reviewDocs).append("available", b.isAvailable()).append("averageRating", b.getAverageRating()).append("numofCopies", b.getNumCopies())));
        
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        for (Document doc : database.getCollection("users").find()) {
            String role = doc.getString("role");
                if ("student".equals(role)) {
                    Student s = new Student(doc.getInteger("userID"), doc.getString("name"),
                                    doc.getString("email"),   doc.getString("password"),
                                    doc.getInteger("age"),    doc.getInteger("grade"),
                                    doc.getString("department"));

                    s.setProfilePicture(doc.getString("profilePhoto"));
                    users.add(s);
                } else if ("librarian".equals(role)) {
                    Librarian lb = new Librarian(doc.getInteger("userID"), doc.getString("name"),
                                        doc.getString("email"),   doc.getString("password"));

                    lb.setProfilePicture(doc.getString("profilePhoto"));
                    users.add(lb);
                }
                Admin a =new Admin(doc.getInteger("userID"), doc.getString("name"),
                                doc.getString("email"),   doc.getString("password"));
                                
                a.setProfilePicture(doc.getString("profilePhoto"));
                users.add(a);
            }
        return users;
    }
    public ArrayList<Book> getBooks() {
        ArrayList<Book> books = new ArrayList<>();
        for (Document doc : database.getCollection("books").find()) {
            Book b = new Book(
                doc.getInteger("bookID"),
                doc.getString("title"),
                doc.getString("author"),
                doc.getString("category"),
                doc.getInteger("numofCopies"));
                b.setAvailability(doc.getBoolean("available"));
                ArrayList <Document> reviewDocs = (ArrayList<Document>) doc.getList("reviews", Document.class);
                if (reviewDocs != null) {
                    for (Document reviewDoc : reviewDocs) {
                        b.addReview(new Review(getUserByEmail(reviewDoc.getString("userEmail")), b,reviewDoc.getString("content"), reviewDoc.getInteger("ratings")));
                    }
                }
            books.add(b);
        }
        return books;    
    }

    public ArrayList<Student> getActiveStudents() {
        ArrayList<Student> students = new ArrayList<>();
        for (User u : getUsers()) {
            if (u instanceof Student && ((Student)u).getAvailabilityStatus().equals("Available")) {
                students.add((Student) u); 
            }
        }
        return students;    
    }

    public ArrayList<StudyRequest> getStudyRequests() {
        ArrayList<StudyRequest> requests = new ArrayList<>();
        for (Document doc: database.getCollection("studyRequests").find()) {
            User sender = getUserByEmail(doc.getString("senderEmail"));
            User receiver = getUserByEmail(doc.getString("receiverEmail"));
            if (sender instanceof Student && receiver instanceof Student) {
                requests.add(new StudyRequest((Student)sender, (Student)receiver, doc.getString("course")));                
            }
        }
        return requests;
    }

    public ArrayList<StudyMatch> getStudyMatches() {
        ArrayList<StudyMatch> matches = new ArrayList<>();
        for (Document doc: database.getCollection("studyMatches").find()) {
            User student1 = getUserByEmail(doc.getString("student1Email"));
            User student2 = getUserByEmail(doc.getString("student2Email"));
            if (student1 instanceof Student && student2 instanceof Student) {
                matches.add(new StudyMatch((Student)student1, (Student)student2, doc.getString("course")));
            }
        }
        return matches;
    }

    public ArrayList<Message> getChats() {
        ArrayList<Message> chats = new ArrayList<>();
        for (Document doc: database.getCollection("chats").find()) {
            User sender = getUserByEmail(doc.getString("senderEmail"));
            User receiver = getUserByEmail(doc.getString("receiverEmail"));
            if (sender != null && receiver != null) {
                Message m = new Message((Student)sender, (Student)receiver, doc.getString("content"));
                m.setTimestamp(doc.getString("timestamp"));
                chats.add(m);
            }
        }
        return chats;
    }
    public ArrayList<Book> getBorrowedBooks() {
        ArrayList<Book> borrowedBooks = new ArrayList<>();
        for (Book b: getBooks()) {
            if(!b.isAvailable()) {
                borrowedBooks.add(b);
            }
        }
        return borrowedBooks;    
    }
}