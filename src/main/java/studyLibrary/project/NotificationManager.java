package studyLibrary.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationManager {

    private static NotificationManager instance;
    private DatabaseManager db;

    private NotificationManager() {
        this.db = new DatabaseManager();
        this.db.connect();
    }

    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    public void notifyBookBorrowed(User user, Book book) {
        String message = "\"" + book.getTitle() + "\" you have borrowed. "
                   + "Return date: " + LocalDate.now().plusDays(14);
        Notification notification = new Notification( user.getUserID(),"Book Borrowed",
            message, Notification.NotificationType.BOOK_BORROWED);
        db.saveNotification(notification);
    }

    public void notifyBookReserved(User user, Book book) {
        String message = "\"" + book.getTitle() + "\" you have reserved. "
                   + "You will be notified when the book is available.";
        Notification notification = new Notification(user.getUserID(),"Book Reserved",
            message, Notification.NotificationType.BOOK_RESERVED);
        db.saveNotification(notification);
    }

    public void notifyDueSoon(User user, Book book, int daysLeft) {
        
        Notification.NotificationType type = daysLeft <= 1
            ? Notification.NotificationType.BOOK_DUE_TOMORROW
            : Notification.NotificationType.BOOK_DUE_SOON;

        String message = "\"" + book.getTitle() + "\" book's return date "
                   + daysLeft + " days from now!";
        Notification notification = new Notification(user.getUserID(),"Due Date Approaching"
        ,message,type);
        db.saveNotification(notification);
    }

    public void notifyStudyRequestAccepted(User user, Student accepter) {
        String message = accepter.getName() + " study request has been accepted! "
                   + "You can now communicate.";
        Notification notification = new Notification(user.getUserID(), "Study Request Accepted",
            message,Notification.NotificationType.STUDY_REQUEST_ACCEPTED);
        db.saveNotification(notification);
    }

    public void notifyStudyRequestReceived(User user, Student sender) {
        String message = sender.getName() + " sent you a study request. Check your requests!";
        Notification notification = new Notification(user.getUserID(),"New Study Request",
            message,Notification.NotificationType.STUDY_REQUEST_RECEIVED);
        db.saveNotification(notification);
    }

    public void notifyTableConfirmed(User user, int tableNo) {
        String message = tableNo + " numbered table reservation has been confirmed for you.";
        Notification n = new Notification(user.getUserID(),"Table Reservation Confirmed",
            message,Notification.NotificationType.TABLE_RESERVATION_CONFIRMED);
        db.saveNotification(n);
    }

    public List<Notification> getUnreadNotifications(int userID) {
        return db.getNotificationsForUser(userID)
                 .stream()
                 .filter(n -> !n.isRead())
                 .collect(Collectors.toList());
    }

    public List<Notification> getAllNotifications(int userID) {
        return db.getNotificationsForUser(userID);
    }

    public void markAllAsRead(int userID) {
        db.markAllNotificationsRead(userID);
    }

    public void markAsRead(String notificationId) {
        db.markNotificationRead(notificationId);
    }

    public void checkDueDates(User user, List<Book> borrowedBooks) {
        LocalDate today = LocalDate.now();
        for (Book book : borrowedBooks) {
            if (book.getDueTime() == null) continue;
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, book.getDueTime());
            if (daysLeft == 3 || daysLeft == 1 || daysLeft < 0) {
                notifyDueSoon(user, book, (int) daysLeft);
            }
        }
    }
    public void clearAllNotifications(int userID) {
        db.deleteAllNotifications(userID);
    }
}
