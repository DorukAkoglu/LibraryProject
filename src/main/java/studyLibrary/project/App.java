package studyLibrary.project;



import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class App extends Application
{
    public void start(Stage stage){
        Group root = new Group();
        Scene scene = new Scene(root);
        Button button = new Button("abc");
        Label label = new Label("abc");
        root.getChildren().add(label);
        root.getChildren().add(button);
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args)
    {
        launch();
    }
}
