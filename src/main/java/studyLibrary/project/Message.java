package studyLibrary.project;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private Student sender;
    private Student receiver;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;
    private boolean isEdited;
    private boolean isDeleted;

    public Message(Student sender, Student receiver, String content){
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
        this.isEdited = false;
        this.isDeleted = false;
    }

    public boolean editMessage(String newContent){
        if (newContent == null || newContent.isEmpty() || this.isDeleted) {
            return false;
        }
        this.content = newContent;
        this.isEdited = true;
        return true;
    }

    public boolean deleteMessage(){
        if (isDeleted){
            return false;
        }
        this.content = "This message has been deleted.";
        this.isDeleted = true;
        return true;
    }
    public void markAsRead(){
        this.isRead = true;
    }
    
    public String displayMessage(){
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = this.timestamp.format(formatter);
        String result = "(" + formattedTime + ") " + this.sender.getName() + ": ";
        if (this.isDeleted){
            result = result + "(This message was deleted)";
        } 
        else{
            result = result + this.content;
            if (this.isEdited) {
                result = result + " (edited)";
            }
        }
        if (this.isRead){
            result = result + " ✓✓";
        }
        return result;
    }
    public Student getSender(){
        return this.sender; 
    }
    public Student getReceiver(){
        return this.receiver; 
    }
    public String getContent(){ 
        return this.content; 
    }
    public LocalDateTime getTimestamp(){ 
        return this.timestamp; 
    }
    public boolean isRead(){ 
        return this.isRead; 
    }
}