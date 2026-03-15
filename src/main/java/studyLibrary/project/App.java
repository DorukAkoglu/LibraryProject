package studyLibrary.project;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application
{
    public void start(Stage stage){
        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args)
    {
        launch();
    }
}
