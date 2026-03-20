package studyLibrary.project;



import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class App extends Application
{
    public static Stage PRIMARY_STAGE;

    public void start(Stage stage) throws Exception{
        PRIMARY_STAGE = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 600);
        String css2 = getClass().getResource("/student.css").toExternalForm();
        scene.getStylesheets().add(css2);
        PRIMARY_STAGE.setScene(scene);
        PRIMARY_STAGE.setTitle("StudyLibrary");
        PRIMARY_STAGE.setMinHeight(600);
        PRIMARY_STAGE.setMinWidth(900);
        PRIMARY_STAGE.setResizable(true);
        PRIMARY_STAGE.getIcons().add(new Image(getClass().getResourceAsStream("/images/programIcon.png")));
        PRIMARY_STAGE.show();
        
    }
    
    public static void main(String[] args)
    {
        launch();
    }
}
