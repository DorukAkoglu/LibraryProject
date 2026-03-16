package studyLibrary.project;

public class StudyRequest {
    private String sender;
    private String receiver;
    private String status; // "pending", "accepted", "rejected"
    public StudyRequest(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = "pending";
    }
    
}
