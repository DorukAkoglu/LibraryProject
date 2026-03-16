package studyLibrary.project;

public class StudyMateController {
    
    private StudyRequest studyRequest;
    private StudyMatch studyMatch;

    public StudyMateController(StudyRequest studyRequest, StudyMatch studyMatch){
        this.studyRequest = studyRequest;
        this.studyMatch = studyMatch;
    }
    public void handleStudyRequest(){
        if(studyRequest.getStatus() == RequestStatus.ACCEPTED){
            studyMatch.setCourse(studyMatch.getCourse());
        }
        else if(studyRequest.getStatus() == RequestStatus.REJECTED){
            
        }
        else{ // If pending
            
        }
    }
    
}
