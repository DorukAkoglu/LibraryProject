module studyLibrary.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.base;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;

    opens studyLibrary.project to javafx.fxml, javafx.graphics;
    exports studyLibrary.project;
}