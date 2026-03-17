package studyLibrary.project;

import java.time.LocalDateTime;

public class StudyRequest {
    private Student sender;
    private Student receiver;
    private RequestStatus status; // PENDING, ACCEPTED, REJECTED (ENUM)
    private LocalDateTime timestamp;
    private String course;

    public StudyRequest(Student sender, Student receiver, String course){
        this.sender = sender;
        this.receiver = receiver;
        this.status = RequestStatus.PENDING;
        this.course = course;
        timestamp = LocalDateTime.now();
    }
    public boolean acceptRequest() {
        if (this.status == RequestStatus.PENDING){
            this.status = RequestStatus.ACCEPTED;
            this.sender.setAvailabilityStatus("In chat");
            this.receiver.setAvailabilityStatus("In Chat");
            return true;
        }
        return false;
    }
    public boolean rejectRequest() {
        if (this.status == RequestStatus.PENDING){
            this.status = RequestStatus.REJECTED;
            return true;
        }
        return false;
    }
    public Student getSender() {
        return this.sender;
    }
    public Student getReceiver() {
        return this.receiver;
    }
    public RequestStatus getStatus() {
        return this.status;
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
