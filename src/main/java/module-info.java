module studyLibrary.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens studyLibrary.project to javafx.fxml, javafx.graphics;
    exports studyLibrary.project;
}