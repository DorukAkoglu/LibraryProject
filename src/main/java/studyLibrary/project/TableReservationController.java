package studyLibrary.project;

import java.io.IOException;
import java.util.List;
import java.util.Stack;



import org.bson.AbstractBsonWriter.Context;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TableReservationController {
    @FXML private Label nameLabel;
    @FXML private Label departmentLabel;
    @FXML private FlowPane tableContainer;
    Table previousReservedTable;
    
    public void initialize(){
        if(MainController.getCurrentUser() instanceof Student){
            Student student = (Student) MainController.getCurrentUser();
            nameLabel.setText(student.getName());
            departmentLabel.setText(student.getDepartment());
        }
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
        if(MainController.getCurrentUser() instanceof Student){
            Student student = (Student) MainController.getCurrentUser();
            Student upToDateStudent = (Student) MainController.dbManager.getUserByID(student.getUserID());
            if(table.getReservedBy() == upToDateStudent.getUserID() && table.getAvailability().equals("Reserved")){
                Text youText = new Text("You");
                youText.setFill(Color.WHITE);
                youText.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
                youText.setTranslateY(-75);
                card.getChildren().add(youText);
            }
            if(table.getOccupiedBy() == student.getUserID() && table.getAvailability().equals("Occupied")){
                Text youText = new Text("You");
                youText.setFill(Color.WHITE);
                youText.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
                youText.setTranslateY(-75);
                card.getChildren().add(youText);
            }
            card.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                Student upToDateStudent = (Student) MainController.dbManager.getUserByID(student.getUserID());
                if(upToDateStudent.getIsOccupiedTable()){
                    return;
                }
                if(table.getAvailability().equals("Available")){
                    if(upToDateStudent.getReservedTableNo() != 0){
                        List<Table> tables = MainController.dbManager.getTables();
                        for(Table t : tables){ 
                            if(t.getTableNo() == upToDateStudent.getReservedTableNo()) {
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
                    upToDateStudent.setreservedTable(table);
                    upToDateStudent.setReservedTableNo(table.getTableNo());
                    table.setReservedBy(upToDateStudent.getUserID());
                    rectangle.setFill(Color.web("#D78C3D", 0.2));
                    rectangle.setStroke(Color.web("#D78C3D"));
                    MainController.dbManager.updateTable(table);
                    MainController.dbManager.updateStudentReservedTable(upToDateStudent, table.getTableNo());
                    initialize();
                }
                else if(table.getAvailability().equals("Reserved") && table.getReservedBy() == upToDateStudent.getUserID()){
                    table.setAvailability("Available");
                    table.setReservedBy(0);
                    upToDateStudent.setReservedTableNo(0);
                    upToDateStudent.setreservedTable(null);
                    rectangle.setFill(Color.web("#00ab41", 0.2));
                    rectangle.setStroke(Color.web("#00ff88"));
                    MainController.dbManager.updateTable(table);
                    MainController.dbManager.updateStudentReservedTable(upToDateStudent, 0);
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
        else{ //Librarian ise
            if(table.getAvailability().equals("Reserved")){
                ContextMenu contextMenu = new ContextMenu();
                MenuItem confirmReservation = new MenuItem("Confirm Reservation");
                MenuItem cancelReservation = new MenuItem("Cancel Reservation");
                Student student = (Student) MainController.dbManager.getUserByID(table.getReservedBy());
                confirmReservation.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event){
                        table.setAvailability("Occupied");
                        table.setOccupiedBy(table.getReservedBy());
                        table.setReservedBy(0);
                        MainController.dbManager.updateStudentOccupiedStatus(student, true);
                        MainController.dbManager.updateStudentReservedTable(student, 0);
                        MainController.dbManager.updateTable(table);
                        initialize();
                    }
                });
                cancelReservation.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event){
                        table.setAvailability("Available");
                        table.setOccupiedBy(0);
                        table.setReservedBy(0);
                        MainController.dbManager.updateStudentReservedTable(student, 0);
                        MainController.dbManager.updateTable(table);
                        initialize();
                    }
                });
                contextMenu.getItems().addAll(confirmReservation, cancelReservation);
                card.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                    public void handle(ContextMenuEvent event){
                        contextMenu.show(card, event.getScreenX(), event.getScreenY());
                    }
                });
                Text idText = new Text(String.valueOf(student.getUserID()));
                idText.setFill(Color.WHITE);
                idText.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
                idText.setTranslateY(-75);
                card.getChildren().add(idText);
                return card;
            }
            else if(table.getAvailability().equals("Occupied")){
                Student student = (Student) MainController.dbManager.getUserByID(table.getOccupiedBy());
                ContextMenu contextMenu = new ContextMenu();
                MenuItem endOccupation = new MenuItem("Finish Desk Session");
                endOccupation.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event){
                        table.setAvailability("Available");
                        table.setOccupiedBy(0);
                        table.setReservedBy(0);
                        MainController.dbManager.updateTable(table);
                        MainController.dbManager.updateStudentOccupiedStatus(student, false);
                        initialize();
                    }
                });
                contextMenu.getItems().add(endOccupation);
                card.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                    public void handle(ContextMenuEvent event){
                        contextMenu.show(card, event.getScreenX(), event.getScreenY());
                    }
                });
                Text idText = new Text(String.valueOf(student.getUserID()));
                idText.setFill(Color.WHITE);
                idText.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
                idText.setTranslateY(-75);
                card.getChildren().add(idText);
                return card;
            }
            return card;   
            
        }
    }   
    public void returnToDashboard(ActionEvent event) throws IOException{
        if(MainController.getCurrentUser() instanceof Student){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/student.fxml"));
            Parent root = loader.load();
            App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
            App.PRIMARY_STAGE.getScene().setRoot(root);
        }
        else if(MainController.getCurrentUser() instanceof Librarian){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian.fxml"));
            Parent root = loader.load();
            App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
            App.PRIMARY_STAGE.getScene().setRoot(root);
        }
    }
    
}
