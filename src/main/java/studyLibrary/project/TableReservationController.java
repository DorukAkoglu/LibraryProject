package studyLibrary.project;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TableReservationController {
    @FXML private FlowPane tableContainer;
    Table previousReservedTable;
    Student student = (Student) MainController.getCurrentUser();
    
    public void initialize(){
        tableContainer.setHgap(15);
        tableContainer.setVgap(15);
        tableContainer.setPrefWrapLength(400);
        tableContainer.getChildren().clear();
        List<Table> allTables = MainController.dbManager.getTables();
        for(Table table : allTables){
            Node tableGraphic = createTable(table);
            tableContainer.getChildren().add(tableGraphic);
        }
    }
    public Node createTable(Table table){
        StackPane card = new StackPane();
        card.setPrefSize(150, 150);
        Rectangle rectangle = new Rectangle(120, 100);
        rectangle.setArcWidth(20);
        rectangle.setArcHeight(20);
        if(table.getAvailability().equals("Available")){
            rectangle.setFill(Color.web("#00ab41", 0.2));
            rectangle.setStroke(Color.web("#00ff88"));
        }
        else if(table.getAvailability().equals("Reserved")){
            rectangle.setFill(Color.web("#D78C3D", 0.2));
            rectangle.setStroke(Color.web("#D78C3D"));
        }
        else{
            rectangle.setFill(Color.web("#c30010", 0.2));
            rectangle.setStroke(Color.web("#c30010"));
        }
        rectangle.setStrokeWidth(2);
        Text text = new Text(Integer.toString(table.getTableNo()));
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        card.getChildren().addAll(rectangle, text);

        if(table.getReservedBy() == student.getUserID()){
            Text youText = new Text("You");
            youText.setFill(Color.WHITE);
            youText.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
            youText.setTranslateY(-75);
            card.getChildren().add(youText);
        }

        card.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                if(table.getAvailability().equals("Available")){
                    if(student.getReservedTableNo() != 0){
                        List<Table> tables = MainController.dbManager.getTables();
                        for(Table t : tables){ 
                            if(t.getTableNo() == student.getReservedTableNo()) {
                                t.setAvailability("Available");
                                t.setReservedBy(0);
                                MainController.dbManager.updateTable(t);
                                break;
                            }
                        }
                        rectangle.setFill(Color.web("#00ab41", 0.2));
                        rectangle.setStroke(Color.web("#00ff88"));
                    }
                    table.setAvailability("Reserved");
                    student.setreservedTable(table);
                    student.setReservedTableNo(table.getTableNo());
                    table.setReservedBy(student.getUserID());
                    rectangle.setFill(Color.web("#D78C3D", 0.2));
                    rectangle.setStroke(Color.web("#D78C3D"));
                    MainController.dbManager.updateTable(table);
                    MainController.dbManager.updateStudentReservedTable(student, table.getTableNo());
                    initialize();
                }
                else if(table.getAvailability().equals("Reserved") && table.getReservedBy() == student.getUserID()){
                    table.setAvailability("Available");
                    table.setReservedBy(0);
                    student.setReservedTableNo(0);
                    student.setreservedTable(null);
                    rectangle.setFill(Color.web("#00ab41", 0.2));
                    rectangle.setStroke(Color.web("#00ff88"));
                    MainController.dbManager.updateTable(table);
                    MainController.dbManager.updateStudentReservedTable(student, 0);
                    initialize();
                }
            }
        });
        card.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                card.setOpacity(0.8);
            }
        });
        card.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                card.setOpacity(1);
            }
        });

        return card;
    }
    public void returnToDashboard(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
    
}
