package studyLibrary.project;

import java.time.LocalDateTime;

public class Notification {
    
    public enum NotificationType {
        BOOK_BORROWED,
        BOOK_RESERVED,
        BOOK_DUE_SOON,        // 3 days
        BOOK_DUE_TOMORROW,    // 1 day
        BOOK_OVERDUE,         // past due date
        STUDY_REQUEST_RECEIVED,
        STUDY_REQUEST_ACCEPTED,
        STUDY_REQUEST_REJECTED,
        STUDY_MATCH_FOUND,
        TABLE_RESERVATION_CONFIRMED,
        TABLE_RESERVATION_CANCELLED
    }

    private String id;
    private int userID;
    private String title;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime timestamp;

    public Notification(int userID, String title, String message, NotificationType type) {
        this.id = java.util.UUID.randomUUID().toString();
        this.userID = userID;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = false;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { 
        return this.id; 
    }
    public void setId(String id) { 
        this.id = id; 
    }
    public int getUserID() { 
        return this.userID; 
    }
    public String getTitle() { 
        return this.title; 
    }
    public String getMessage() { 
        return this.message; 
    }
    public NotificationType getType() { 
        return this.type; 
    }
    public boolean isRead() { 
        return this.isRead; 
    }
    public void setRead(boolean read) { 
        this.isRead = read; 
    }
    public LocalDateTime getTimestamp() { 
        return this.timestamp; 
    }
    public void setTimestamp(LocalDateTime timestamp) { 
        this.timestamp = timestamp; 
    }
}