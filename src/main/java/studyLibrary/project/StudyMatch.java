package studyLibrary.project;

public class StudyMatch {
    private Student student1;
    private Student student2;
    private String course;
    private boolean isActive;

    public StudyMatch(Student student1, Student student2, String course) {
        this.student1 = student1;
        this.student2 = student2;
        this.course = course;
        this.student1.setAvailabilityStatus("Busy");
        this.student2.setAvailabilityStatus("Busy");
        this.isActive = true;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public String getCourse() {
        return this.course;
    }
    public Student getStudent1() {
        return this.student1;
    }
    public Student getStudent2() {
        return this.student2;
    }
    public boolean endSession(){
        if (!this.isActive){
            return false;
        }
        this.isActive = false;
        this.student1.setAvailabilityStatus("Available");
        this.student2.setAvailabilityStatus("Available");
        return true;
    }
    public boolean isActive(){
        return this.isActive;
    }
    public String displayMatchDetails(Student currentStudent) {
        if(this.student1.equals(currentStudent)){
            return "You are matched with " + this.student2.getName() + "\n Course: " + this.course;
        }
        else{
            return "You are matched with " + this.student1.getName() + "\n Course: " + this.course;
        }
    }
}
