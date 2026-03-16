package studyLibrary.project;

import java.util.List;

public class LibrarySystem {
    
    private List<StudyRequest> requests;
    private List<StudyMatch> matches;
    private List<Student> students;

    public LibrarySystem(List<StudyRequest> requests, List<StudyMatch> matches, List<Student> students) {
        this.requests = requests;
        this.matches = matches;
        this.students = students;
    }
    //StudyMate kısmı için yapmam gerekti
    public void acceptRequest(StudyRequest request){
        StudyMatch studyMatch = new StudyMatch(request.getSender(), request.getReceiver());
        studyMatch.setCourse(request.getCourse());
        this.matches.add(studyMatch);
        this.requests.remove(request);
    }

    public void removeRequest(StudyRequest request){
        requests.remove(request);
    }
}
