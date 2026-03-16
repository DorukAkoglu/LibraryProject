package studyLibrary.project;

public class StudyMatch {
    private Student student1;
    private Student student2;
    private String course;

    public StudyMatch(Student student1, Student student2) {
        this.student1 = student1;
        this.student2 = student2;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public String getCourse() {
        return course;
    }
    public Student getStudent1() {
        return student1;
    }
    public Student getStudent2() {
        return student2;
    }
}
