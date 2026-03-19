package studyLibrary.project;



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
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/studyMate.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("StudyLibrary");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/programIcon.png")));
        stage.show();
        
    }
    
    public static void main(String[] args)
    {
        launch();
    }
}
