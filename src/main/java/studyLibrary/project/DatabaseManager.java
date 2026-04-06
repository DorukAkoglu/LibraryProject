package studyLibrary.project;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class DatabaseManager {
    private MongoClient mongoClient;
     private CloudinaryManager cloudinaryManager;
    private MongoDatabase database;
    private HashMap<String, User> userCacheByEmail = new HashMap<>();
    private HashMap<Integer, User> userCacheByID = new HashMap<>();
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
        if (userCacheByEmail.containsKey(email)) {
            return userCacheByEmail.get(email);
        }
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
            userCacheByEmail.put(email, s);
            return s;
        } else if ("librarian".equals(role)) {
            Librarian lb = new Librarian(doc.getInteger("userID"), doc.getString("name"),
                                doc.getString("email"),   doc.getString("password"));
            lb.setProfilePicture(doc.getString("profilePhoto"));
            userCacheByEmail.put(email, lb);
            return lb;
        }
        Admin a =new Admin(doc.getInteger("userID"), doc.getString("name"),
                        doc.getString("email"),   doc.getString("password"));
        a.setProfilePicture(doc.getString("profilePhoto"));
        userCacheByEmail.put(email, a);
        return a;
    }
    public User getUserByID(int userID) {
        if (userCacheByID.containsKey(userID)) {
            return userCacheByID.get(userID);
        }

        Document doc = database.getCollection("users")
                            .find(new Document("userID", userID)).first();
        if (doc == null) return null;

        String role = doc.getString("role");
        if ("student".equals(role)) {
            Student s = new Student(doc.getInteger("userID"), doc.getString("name"),
                            doc.getString("email"),   doc.getString("password"),
                            doc.getInteger("age"),    doc.getInteger("grade"),
                            doc.getString("department"));
            s.setSelectedCourse(doc.getString("selectedCourse"));
            s.setProfilePicture(doc.getString("profilePhoto"));
            int reservedTableNo = 0;
            boolean isOccupiedTable = false;
            if(doc.get("reservedTableNo") != null){
                reservedTableNo = doc.getInteger("reservedTableNo");
            }
            if(doc.get("isOccupiedTable") != null){
                isOccupiedTable = doc.getBoolean("isOccupiedTable");
            }
            s.setReservedTableNo(reservedTableNo);
            s.setIsOccupiedTable(isOccupiedTable);
            userCacheByID.put(userID,s);
            return s;
        } else if ("librarian".equals(role)) {
            Librarian lb = new Librarian(doc.getInteger("userID"), doc.getString("name"),
                                doc.getString("email"),   doc.getString("password"));
            lb.setProfilePicture(doc.getString("profilePhoto"));
            userCacheByID.put(userID,lb);
            return lb;
        }
        Admin a =new Admin(doc.getInteger("userID"), doc.getString("name"),
                        doc.getString("email"),   doc.getString("password"));
        a.setProfilePicture(doc.getString("profilePhoto"));
        userCacheByID.put(userID,a);
        return a;
    }

    public void connect() {
        mongoClient = MongoClients.create("mongodb+srv://mehmetsamedtek_db_user:bilkent123@cluster0.fdhw2wq.mongodb.net/");
        database = mongoClient.getDatabase("LibraryDB");

    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
    public Book getBookByBookID(int bookID) {
       Document doc = database.getCollection("books")
                    .find(new Document("bookID", bookID)).first();
    if (doc == null) return null;

    Book b = new Book(
        doc.getInteger("bookID"),
        doc.getString("title"),
        doc.getString("author"),
        doc.getString("category"),
        doc.getInteger("numofCopies"));

    b.setAvailability(doc.getBoolean("available"));
    
    if (doc.get("duetime") != null) 
        b.setDueTime(LocalDate.parse(doc.get("duetime").toString()));

    ArrayList<Document> reviewDocs = (ArrayList<Document>) doc.getList("reviews", Document.class);
    if (reviewDocs != null) {
        for (Document reviewDoc : reviewDocs) {
            Review r = new Review(
                getUserByEmail(reviewDoc.getString("userEmail")),
                b,
                reviewDoc.getString("content"),
                reviewDoc.getInteger("ratings"));

            ArrayList<Document> commentDocs = (ArrayList<Document>) reviewDoc.getList("comments", Document.class);
            if (commentDocs != null) {
                for (Document commentDoc : commentDocs) {
                    r.addComment(new Comment(
                        getUserByEmail(commentDoc.getString("userEmail")),
                        commentDoc.getString("comment")));
                }
            }
            b.addReview(r);
        }
    }
    return b;   
    }
    public void saveBook(Book b) {
        ArrayList <Document> reviewDocs = new ArrayList<>();
        for (Review r : b.getReviews()) {
            ArrayList<Document> commentDocs = new ArrayList<>();
            for (Comment c : r.getComments()) {
                commentDocs.add(new Document("userEmail", c.getUser().getEmail()).append("comment", c.getContent()));
            }
            reviewDocs.add(new Document("userEmail", r.getUser().getEmail()).append("bookID", r.getBook().getBookID()).append("content", r.getContent()) .append("ratings", r.getRating()).append("comments", commentDocs));

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
    public ArrayList<Message> getChatsBetween(String email1, String email2, int limit) {
        ArrayList<Message> chats = new ArrayList<>();
        Document query = new Document("$or", List.of(
            new Document("senderEmail", email1).append("receiverEmail", email2),
            new Document("senderEmail", email2).append("receiverEmail", email1)
        ));
        for (Document doc : database.getCollection("chats")
                .find(query)
                .sort(new Document("timestamp", -1))
                .limit(limit)) {
            User sender = getUserByEmail(doc.getString("senderEmail"));
            User receiver = getUserByEmail(doc.getString("receiverEmail"));
            if (sender != null && receiver != null) {
                Message m = new Message((Student) sender, (Student) receiver, doc.getString("content"));
                m.setTimestamp(doc.getString("timestamp"));
                if (doc.containsKey("isEdited")) {
                    m.setEdited(doc.getBoolean("isEdited"));
                }
                chats.add(m);
            }
        }
        java.util.Collections.reverse(chats);
        return chats;
    }

    public void saveUser(User u) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document doc = new Document("userID",   u.getUserID())
                .append("name",     u.getName())
                .append("email",    u.getEmail())
                .append("password", u.getPassword());   

        if (u instanceof Student s) {
            Student a = (Student) u;
            

            doc.append("role", "student")
            .append("age", s.getAge())     
           .append("grade", s.getGrade())
           .append("department", s.getDepartment())
            .append("availabilityStatus", s.getAvailabilityStatus())
            .append("profilePhoto", s.getProfilePhoto())
            .append("selectedCourse", s.getSelectedCourse())
            .append("reservedTableNo", 0)
            .append("isOccupiedTable", false);
            
            
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
                .append("timestamp", m.getTimestamp().toString())
                .append("isEdited", m.isEdited());
        collection.insertOne(doc);
    }

    public void removeChat(Message m) {
        database.getCollection("chats")
            .deleteOne(new Document("senderEmail",   m.getSender().getEmail())
            .append("timestamp", m.getTimestamp().toString()));
    }

    public void saveStudyRequest(StudyRequest r) {
        MongoCollection<Document> collection = database.getCollection("users");
        
        Document filter = new Document("email", r.getReceiver().getEmail());
        
        Document requestDoc = new Document("senderEmail", r.getSender().getEmail())
                                    .append("course", r.getCourse())
                                    .append("status", r.getStatus().toString())
                                    .append("timestamp", r.getTimestamp().toString());

        collection.updateOne(filter, new Document("$push", new Document("requests", requestDoc)));
    }
    public void updateStudyRequestStatus(StudyRequest r) {
        MongoCollection<Document> collection = database.getCollection("users");

        Document filter = new Document("email", r.getReceiver().getEmail())
                            .append("requests.senderEmail", r.getSender().getEmail())
                            .append("requests.course", r.getCourse());

        Document update = new Document("$set", new Document("requests.$.status", r.getStatus().toString()));

        collection.updateOne(filter, update);
    }
    public void updateUser(User u) {
        Document update = new Document("$set", new Document("name", u.getName())
                .append("email",    u.getEmail())
                .append("password", u.getPassword())
                .append("profilePhoto", u.getProfilePhoto()));

        if (u instanceof Student s) {
            update.get("$set", Document.class)
                .append("availabilityStatus", s.getAvailabilityStatus())
                .append("age",        s.getAge())
                .append("grade",      s.getGrade())
                .append("department", s.getDepartment())
                .append("selectedCourse", s.getSelectedCourse());
        }
        database.getCollection("users")
                .updateOne(new Document("userID", u.getUserID()), update);
    }

    public void removeStudyRequest(StudyRequest r) {
        MongoCollection<Document> collection = database.getCollection("users");

        Document filter = new Document("email", r.getReceiver().getEmail());

        Document pullValue = new Document("senderEmail", r.getSender().getEmail())
                                    .append("course", r.getCourse());

        Document update = new Document("$pull", new Document("requests", pullValue));
        collection.updateOne(filter, update);
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
    public User validateUser(int ID, String password){
        Document query = new Document("userID", ID).append("password", password);
        Document userDoc = database.getCollection("users").find(query).first();
        if(userDoc == null) return null;
        return getUserByID(ID);
    }

    public void updateBook(Book b) {
        ArrayList <Document> reviewDocs = new ArrayList<>();
        for (Review r : b.getReviews()) {
            ArrayList <Document> commentDocs = new ArrayList<>();
            for(Comment c : r.getComments()) {
                commentDocs.add(new Document("userEmail", c.getUser().getEmail()).append("comment", c.getContent()));
            }
            reviewDocs.add(new Document("userEmail", r.getUser().getEmail()).append("bookID", r.getBook().getBookID()).append("content", r.getContent()) .append("ratings", r.getRating()).append("comments", commentDocs));

        }
        database.getCollection("books").updateOne(new Document("bookID", b.getBookID()),new Document("$set", new Document("reviews", reviewDocs).append("available", b.isAvailable()).append("averagerating", b.getAverageRating()).append("numofCopies", b.getNumCopies())));
        
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
                    s.setSelectedCourse(doc.getString("selectedCourse"));
                    users.add(s);
                } else if ("librarian".equals(role)) {
                    Librarian lb = new Librarian(doc.getInteger("userID"), doc.getString("name"),
                                        doc.getString("email"),   doc.getString("password"));

                    lb.setProfilePicture(doc.getString("profilePhoto"));
                    users.add(lb);
                } else {
                Admin a =new Admin(doc.getInteger("userID"), doc.getString("name"),
                                doc.getString("email"),   doc.getString("password"));
                                
                a.setProfilePicture(doc.getString("profilePhoto"));
                users.add(a);
                }
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
                        Review r = new Review(getUserByEmail(reviewDoc.getString("userEmail")), b,reviewDoc.getString("content"), reviewDoc.getInteger("ratings"));
                        ArrayList <Document> commentDocs = (ArrayList<Document>)reviewDoc.getList("comments", Document.class);
                        if (commentDocs != null) {
                            for (Document commentDoc : commentDocs){
                                r.addComment(new Comment (getUserByEmail(commentDoc.getString("userEmail")), commentDoc.getString("comment")));
                            }
                        }
                        b.addReview(r);
                    }   
                }
                if (doc.get("duetime") != null) b.setDueTime(LocalDate.parse(doc.get("duetime").toString()));
            books.add(b);
        }
        Collections.sort(books);
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

    public List<Student> getStudentsByCourse(){
        Student student = (Student) MainController.getCurrentUser();
        List<Student> studyMates = new ArrayList<>();
        ArrayList<StudyMatch> allMatches = getStudyMatches();
        List<String> myFriendEmails = new ArrayList<>();
        for (StudyMatch match : allMatches) {
            if (match.getStudent1().getEmail().equals(student.getEmail())) {
                myFriendEmails.add(match.getStudent2().getEmail());
            } 
            else if (match.getStudent2().getEmail().equals(student.getEmail())) {
                myFriendEmails.add(match.getStudent1().getEmail());
            }
        }
        for (Student other : getActiveStudents()) {
            if (other.getEmail().equals(student.getEmail()) || 
                !other.getSelectedCourse().equals(student.getSelectedCourse())) {
                continue;
            }
            if (myFriendEmails.contains(other.getEmail())) {
                continue;
            }
            boolean alreadySent = false;
            ArrayList<StudyRequest> othersIncoming = getStudyRequestsForUser(other.getEmail());
            for (StudyRequest sr : othersIncoming) {
                if (sr.getSender().getEmail().equals(student.getEmail())) {
                    alreadySent = true;
                    break;
                }
            }
            if (alreadySent) {
                continue;
            }
            studyMates.add(other);
        }
        return studyMates;
    }

    public ArrayList<StudyRequest> getStudyRequestsForUser(String userEmail) {
        ArrayList<StudyRequest> requests = new ArrayList<>();
        Document userDoc = database.getCollection("users").find(new Document("email", userEmail)).first();

        if (userDoc != null && userDoc.get("requests") != null) {
            List<Document> requestDocs = (List<Document>) userDoc.get("requests");
            for (Document doc : requestDocs) {
                RequestStatus status = RequestStatus.valueOf(doc.getString("status"));
                User sender = getUserByEmail(doc.getString("senderEmail"));
                User receiver = getUserByEmail(userEmail);

                if (sender instanceof Student && receiver instanceof Student) {
                    StudyRequest sr = new StudyRequest((Student)sender, (Student)receiver, doc.getString("course"));
                    sr.setStatus(status);
                    requests.add(sr);
                }
            }
        }
        return requests;
    }
    public ArrayList<Message> getChatsForUser(String userEmail) {
        ArrayList<Message> chats = new ArrayList<>();
        
        Document query = new Document("$or", List.of(
            new Document("senderEmail", userEmail),
            new Document("receiverEmail", userEmail)
        ));
        
        for (Document doc : database.getCollection("chats").find(query).sort(new Document("timestamp", 1))) {
            User sender = getUserByEmail(doc.getString("senderEmail"));
            User receiver = getUserByEmail(doc.getString("receiverEmail"));
            if (sender != null && receiver != null) {
                Message m = new Message((Student) sender, (Student) receiver, doc.getString("content"));
                m.setTimestamp(doc.getString("timestamp"));
                if (doc.containsKey("isEdited")) {
                    m.setEdited(doc.getBoolean("isEdited"));
                }
                chats.add(m);
            }
        }
        return chats;
    }

    public Map<String, ArrayList<Message>> getChatsByPerson(String userEmail) {
        Map<String, ArrayList<Message>> chatsByPerson = new HashMap<>();
        
        for (Message m : getChatsForUser(userEmail)) {
            String otherEmail;
                if (m.getSender().getEmail().equals(userEmail)) {
                    otherEmail = m.getReceiver().getEmail();
                } else {
                    otherEmail = m.getSender().getEmail();
                }
                if (!chatsByPerson.containsKey(otherEmail)) {
                    chatsByPerson.put(otherEmail, new ArrayList<>());
                }
                chatsByPerson.get(otherEmail).add(m);        
            }
        return chatsByPerson;
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
    
    public ArrayList<Table> getTables(){
        ArrayList<Table> tables = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("Tables");
        for(Document doc : collection.find()){
            Table table = new Table();
            table.setTableNo(doc.getInteger("tableNo"));
            table.setAvailability(doc.getString("availability"));
            if (doc.containsKey("reservedBy") && doc.get("reservedBy") != null) {
                table.setReservedBy(doc.getInteger("reservedBy"));
            } 
            else {
                table.setReservedBy(0); 
            }
            if (doc.containsKey("occupiedBy") && doc.get("occupiedBy") != null) {
            table.setOccupiedBy(doc.getInteger("occupiedBy"));
            } 
            else {
                table.setOccupiedBy(0); 
            }
            tables.add(table);
        }
        return tables;
    }
    public void updateTable(Table table){
        MongoCollection<Document> collection = database.getCollection("Tables");
        Document filter = new Document("tableNo", table.getTableNo());
        Document update = new Document("$set", new Document("availability", table.getAvailability())
                                                        .append("reservedBy", table.getReservedBy())
                                                        .append("occupiedBy", table.getOccupiedBy()));
                                                        
                                                        
        collection.updateOne(filter, update);
    }
    public void updateStudentReservedTable(Student student, int tableNo){
        MongoCollection<Document> collection = database.getCollection("users");
        Document filter = new Document("userID", student.getUserID()); // Öğrenciyi email ile bul
        Document update = new Document("$set", new Document("reservedTableNo", tableNo));
        collection.updateOne(filter, update);
    }
    public void updateStudentOccupiedStatus(Student student, boolean isOccupied){
        MongoCollection<Document> collection = database.getCollection("users");
        Document query = new Document("userID", student.getUserID());
        Document update = new Document("$set", new Document("isOccupiedTable", isOccupied));
        collection.updateOne(query, update);
        student.setIsOccupiedTable(isOccupied);
    }

    public ArrayList<Book> getBorrowedBooksByUser(User u) {
        ArrayList<Book> list = new ArrayList<>();
        org.bson.Document userDoc = database.getCollection("users").find(new org.bson.Document("userID", u.getUserID())).first();
        
        if (userDoc != null && userDoc.containsKey("borrowedBooks")) {
            List<org.bson.Document> borrowed = (List<org.bson.Document>) userDoc.get("borrowedBooks");
            for (org.bson.Document doc : borrowed) {
                Book b = getBookByBookID(doc.getInteger("bookID"));
                if (b != null) {
                    b.setDueTime(LocalDate.parse(doc.getString("dueDate"))); 
                    list.add(b);
                }
            }
        }
        return list;
    }

    public ArrayList<Book> getReservedBooksByUser(User u) {
        ArrayList<Book> list = new ArrayList<>();
        org.bson.Document userDoc = database.getCollection("users").find(new org.bson.Document("userID", u.getUserID())).first();
        
        if (userDoc != null && userDoc.containsKey("reservedBooks")) {
            List<org.bson.Document> reserved = (List<org.bson.Document>) userDoc.get("reservedBooks");
            for (org.bson.Document doc : reserved) {
                Book b = getBookByBookID(doc.getInteger("bookID"));
                if (b != null) {
                    list.add(b);
                }
            }
        }
        return list;
    }

    public ArrayList<Book> getHistoryBooksByUser(User u) {
        ArrayList<Book> list = new ArrayList<>();
        org.bson.Document userDoc = database.getCollection("users").find(new org.bson.Document("userID", u.getUserID())).first();
        
        if (userDoc != null && userDoc.containsKey("history")) {
            List<org.bson.Document> history = (List<org.bson.Document>) userDoc.get("history");
            for (org.bson.Document doc : history) {
                Book b = getBookByBookID(doc.getInteger("bookID"));
                if (b != null) {
                    // Tarihi ekranda göstermek için Book nesnesindeki dueTime alanına geçici yazıyoruz
                    b.setDueTime(LocalDate.parse(doc.getString("returnDate")));
                    list.add(b);
                }
            }
        }
        return list;
    }

    public boolean borrowBookDB(User u, Book b) {
        if (!b.isAvailable() || b.getNumCopies() <= 0 || getBorrowedBooksByUser(u).contains(b)) return false;
        MongoCollection<org.bson.Document> usersCollection = database.getCollection("users");
        org.bson.Document borrowedInfo = new org.bson.Document("bookID", b.getBookID())
                                             .append("dueDate", LocalDate.now().plusDays(14).toString());
        usersCollection.updateOne(
                new org.bson.Document("userID", u.getUserID()),
                new org.bson.Document("$push", new org.bson.Document("borrowedBooks", borrowedInfo))
        );
        usersCollection.updateOne(
                new org.bson.Document("userID", u.getUserID()),
                new org.bson.Document("$pull", new org.bson.Document("reservedBooks", new org.bson.Document("bookID", b.getBookID())))
        );
        b.setNumCopies(b.getNumCopies() - 1);
        if(b.getNumCopies() <= 0) b.setAvailability(false);
        updateBook(b);
        NotificationManager.getInstance().notifyBookBorrowed(u, b);
        return true;
    }

    public boolean returnBookDB(User u, Book b) {
        MongoCollection<org.bson.Document> usersCollection = database.getCollection("users");

        usersCollection.updateOne(
                new org.bson.Document("userID", u.getUserID()),
                new org.bson.Document("$pull", new org.bson.Document("borrowedBooks", new org.bson.Document("bookID", b.getBookID())))
        );

        
        org.bson.Document historyInfo = new org.bson.Document("bookID", b.getBookID())
                                            .append("returnDate", LocalDate.now().toString());
        usersCollection.updateOne(
                new org.bson.Document("userID", u.getUserID()),
                new org.bson.Document("$push", new org.bson.Document("history", historyInfo))
        );
        b.setNumCopies(b.getNumCopies() + 1);
        b.setAvailability(true);
        b.setDueTime(null);
        updateBook(b);
        
        return true;
    }

    public boolean extendBookDB(User u, Book b) {
        if (b.getDueTime() == null) return false;
        
        LocalDate newDate = b.getDueTime().plusDays(7);
        
        database.getCollection("users").updateOne(
                new org.bson.Document("userID", u.getUserID()).append("borrowedBooks.bookID", b.getBookID()),
                new org.bson.Document("$set", new org.bson.Document("borrowedBooks.$.dueDate", newDate.toString()))
        );
        return true;
    }

    public boolean reserveBookDB(User u, Book b) {
    if (b.isAvailable()) return false;

    // Zaten reserve etmiş mi kontrol et
    org.bson.Document userDoc = database.getCollection("users")
            .find(new org.bson.Document("userID", u.getUserID())).first();
    
    if (userDoc != null && userDoc.containsKey("reservedBooks")) {
        List<org.bson.Document> reserved = (List<org.bson.Document>) userDoc.get("reservedBooks");
        for (org.bson.Document doc : reserved) {
            if (doc.getInteger("bookID") == b.getBookID()) {
                return false; // Zaten rezerve etmiş
            }
        }
    }

    org.bson.Document reserveInfo = new org.bson.Document("bookID", b.getBookID())
                                        .append("status", "Waiting");
    database.getCollection("users").updateOne(
            new org.bson.Document("userID", u.getUserID()),
            new org.bson.Document("$push", new org.bson.Document("reservedBooks", reserveInfo))
    );
    return true;
}

    public boolean cancelReserveDB(User u, Book b) {
        database.getCollection("users").updateOne(
                new org.bson.Document("userID", u.getUserID()),
                new org.bson.Document("$pull", new org.bson.Document("reservedBooks", new org.bson.Document("bookID", b.getBookID())))
        );
        return true;
    }

    public ArrayList<org.bson.Document> getReviewsForBook(int bookID) {
        ArrayList<org.bson.Document> reviewList = new ArrayList<>();
        org.bson.Document bookDoc = database.getCollection("books").find(new org.bson.Document("bookID", bookID)).first();
        
        if (bookDoc != null && bookDoc.containsKey("reviews")) {
            java.util.List<org.bson.Document> reviews = (java.util.List<org.bson.Document>) bookDoc.get("reviews");
            reviewList.addAll(reviews);
        }
        return reviewList;
    }

    public boolean addReviewToBook(int bookID, String senderName, String reviewText, int rating) {
        String uniqueReviewID = java.util.UUID.randomUUID().toString(); 

        org.bson.Document newReview = new org.bson.Document("reviewID", uniqueReviewID) 
                                            .append("senderName", senderName)
                                            .append("comment", reviewText) 
                                            .append("rating", rating)
                                            .append("date", java.time.LocalDate.now().toString())
                                            .append("comments", new java.util.ArrayList<org.bson.Document>()); 

        com.mongodb.client.result.UpdateResult result = database.getCollection("books").updateOne(
                new org.bson.Document("bookID", bookID),
                new org.bson.Document("$push", new org.bson.Document("reviews", newReview))
        );
        return result.getModifiedCount() > 0;
    }

    public boolean addCommentToReview(int bookID, String targetReviewID, Comment reply) {
        org.bson.Document commentDoc = new org.bson.Document("userID", reply.getUser().getUserID())
                                             .append("text", reply.getContent());

        com.mongodb.client.result.UpdateResult result = database.getCollection("books").updateOne(
                new org.bson.Document("bookID", bookID).append("reviews.reviewID", targetReviewID),
                new org.bson.Document("$push", new org.bson.Document("reviews.$.comments", commentDoc))
        );
        return result.getModifiedCount() > 0;
    }
    public User getUserByIDForReservation(int userID) {

        Document doc = database.getCollection("users")
                            .find(new Document("userID", userID)).first();
        if (doc == null) return null;

        String role = doc.getString("role");
        if ("student".equals(role)) {
            Student s = new Student(doc.getInteger("userID"), doc.getString("name"),
                            doc.getString("email"),   doc.getString("password"),
                            doc.getInteger("age"),    doc.getInteger("grade"),
                            doc.getString("department"));
            s.setSelectedCourse(doc.getString("selectedCourse"));
            s.setProfilePicture(doc.getString("profilePhoto"));
            int reservedTableNo = 0;
            boolean isOccupiedTable = false;
            if(doc.get("reservedTableNo") != null){
                reservedTableNo = doc.getInteger("reservedTableNo");
            }
            if(doc.get("isOccupiedTable") != null){
                isOccupiedTable = doc.getBoolean("isOccupiedTable");
            }
            s.setReservedTableNo(reservedTableNo);
            s.setIsOccupiedTable(isOccupiedTable);
            userCacheByID.put(userID,s);
            return s;
        } else if ("librarian".equals(role)) {
            Librarian lb = new Librarian(doc.getInteger("userID"), doc.getString("name"),
                                doc.getString("email"),   doc.getString("password"));
            lb.setProfilePicture(doc.getString("profilePhoto"));
            userCacheByID.put(userID,lb);
            return lb;
        }
        Admin a =new Admin(doc.getInteger("userID"), doc.getString("name"),
                        doc.getString("email"),   doc.getString("password"));
        a.setProfilePicture(doc.getString("profilePhoto"));
        return a;
    }
    public void saveNotification(Notification notification) {
        Document doc = new Document("id", notification.getId())
            .append("userID", notification.getUserID())
            .append("title", notification.getTitle())
            .append("message", notification.getMessage())
            .append("type", notification.getType().toString())
            .append("isRead", notification.isRead())
            .append("timestamp", notification.getTimestamp().toString());
        database.getCollection("notifications").insertOne(doc);
    }

    public ArrayList<Notification> getNotificationsForUser(int userID) {
        ArrayList<Notification> list = new ArrayList<>();
        for (Document doc : database.getCollection("notifications")
                .find(new Document("userID", userID))
                .sort(new Document("timestamp", -1))) {
            Notification notification = new Notification(
                doc.getInteger("userID"),
                doc.getString("title"),
                doc.getString("message"),
                Notification.NotificationType.valueOf(doc.getString("type"))
            );
            notification.setId(doc.getString("id"));
            notification.setRead(doc.getBoolean("isRead"));
            notification.setTimestamp(java.time.LocalDateTime.parse(doc.getString("timestamp")));
            list.add(notification);
        }
        return list;
    }

    public void markAllNotificationsRead(int userID) {
        database.getCollection("notifications").updateMany(
            new Document("userID", userID).append("isRead", false),
            new Document("$set", new Document("isRead", true))
        );
    }

    public void markNotificationRead(String notificationId) {
        database.getCollection("notifications").updateOne(
            new Document("id", notificationId),
            new Document("$set", new Document("isRead", true))
        );
    }
    public void deleteAllNotifications(int userID) {
        database.getCollection("notifications")
                .deleteMany(new Document("userID", userID));
    }
    public ArrayList<StudyRequest> getSentStudyRequests(String userEmail) {
        ArrayList<StudyRequest> requests = new ArrayList<>();
        for (Document userDoc : database.getCollection("users").find()) {
            if (userDoc.get("requests") == null) continue;
            List<Document> requestDocs = (List<Document>) userDoc.get("requests");
            for (Document doc : requestDocs) {
                if (doc.getString("senderEmail").equals(userEmail)) {
                    RequestStatus status = RequestStatus.valueOf(doc.getString("status"));
                    User sender = getUserByEmail(userEmail);
                    User receiver = getUserByEmail(userDoc.getString("email"));
                    
                    if (sender instanceof Student && receiver instanceof Student) {
                        StudyRequest request = new StudyRequest((Student) sender, (Student) receiver, 
                            doc.getString("course"));
                        request.setStatus(status);
                        requests.add(request);
                    }
                }
            }
        }
        return requests;
    }
}
