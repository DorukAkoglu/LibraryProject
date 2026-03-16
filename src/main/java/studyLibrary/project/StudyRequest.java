package studyLibrary.project;

import java.time.LocalDateTime;

public class StudyRequest {
    private String sender;
    private String receiver;
    private RequestStatus status; // PENDING, ACCEPTED, REJECTED (ENUM)
    private LocalDateTime timestamp;
    public StudyRequest(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = RequestStatus.PENDING;
        timestamp = LocalDateTime.now();
    }
    public void acceptRequest() {
        this.status = RequestStatus.ACCEPTED;
    }
    public void rejectRequest() {
        this.status = RequestStatus.REJECTED;
    }
    public String getSender() {
        return sender;
    }
    public String getReceiver() {
        return receiver;
    }
    public RequestStatus getStatus() {
        return status;
    }
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    } 
}
