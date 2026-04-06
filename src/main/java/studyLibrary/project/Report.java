package studyLibrary.project;

import java.time.LocalDateTime;
import java.util.Date;

public class Report {
    
    private String content;
    private Date timestamp;
    private String sender; // email'le alıyoz (getUserByEmail() kullanırız)
    private String receiver; // email'le alıyoz (getUserByEmail() kullanırız)

    public Report(String content, String sender, String receiver){
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = new Date();
    }
    public String getContent(){
        return this.content;
    }
    public String getSender(){
        return this.sender;
    }
    public String getReceiver(){
        return this.receiver;
    }
    public Date getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(Date timestamp){
        this.timestamp = timestamp;
    }
}
