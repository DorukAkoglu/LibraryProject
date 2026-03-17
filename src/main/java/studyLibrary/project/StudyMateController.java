package studyLibrary.project;

import javafx.event.ActionEvent;

/** 
public class StudyMateController {

    private LibrarySystem librarySystem;

    public StudyMateController(LibrarySystem librarySystem) {
        this.librarySystem = librarySystem;
    }
    public void handleStudyRequest(StudyRequest studyRequest) {
        if(studyRequest.getStatus() == RequestStatus.ACCEPTED){
            librarySystem.acceptRequest(studyRequest);
        }
        else if(studyRequest.getStatus() == RequestStatus.REJECTED){
            librarySystem.removeRequest(studyRequest);
        }
    } 
}
*/  
public class StudyMateController {

    private LibrarySystem librarySystem;

    public void displayRequestMessage(ActionEvent event) {
        System.out.println("Test message");
    }
}
