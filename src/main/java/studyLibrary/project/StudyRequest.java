package studyLibrary.project;

import java.time.LocalDateTime;

public class StudyRequest {
    private Student sender;
    private Student receiver;
    private RequestStatus status; // PENDING, ACCEPTED, REJECTED (ENUM)
    private LocalDateTime timestamp;
    private String course;

    public StudyRequest(Student sender, Student receiver) {
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
    public Student getSender() {
        return sender;
    }
    public Student getReceiver() {
        return receiver;
    }
    public RequestStatus getStatus() {
        return status;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public String getCourse() {
        return this.course;
    }
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    } 
}
