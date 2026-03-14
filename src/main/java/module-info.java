module studyLibrary.project {
    requires javafx.controls;
    requires transitive javafx.graphics;
    opens studyLibrary.project to javafx.graphics, javafx.fxml, javafx.controls;
    exports studyLibrary.project;
}