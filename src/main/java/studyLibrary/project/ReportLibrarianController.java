package studyLibrary.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReportLibrarianController {
    @FXML private Label nameLabel, departmentLabel;
    @FXML private ImageView profilePicture;

    
    @FXML private VBox announcementContainer, composeReportContainer;
    @FXML private Button viewAnnouncementsButton;
    @FXML private Button composeReportButton;

    //composeContainer components
    @FXML private VBox reportHistoryVBox;
    @FXML private HBox sendReportHBox;
    @FXML private TextField reportTextField;

    Librarian librarian = (Librarian) MainController.getCurrentUser();



    public void initialize(){
        announcementContainer.getStylesheets().add(getClass().getResource("/reports.css").toExternalForm());
        composeReportContainer.getStylesheets().add(getClass().getResource("/reports.css").toExternalForm());
        announcementContainer.getStylesheets().add(getClass().getResource("/mybooks.css").toExternalForm());
        composeReportContainer.getStylesheets().add(getClass().getResource("/mybooks.css").toExternalForm());
        if (librarian.getProfilePhoto() != null) {
            profilePicture.setImage(new javafx.scene.image.Image(librarian.getProfilePhoto()));
        }
        showAnnouncements();
    }
    public void showAnnouncements(){
        announcementContainer.setVisible(true);
        announcementContainer.setManaged(true);
        composeReportContainer.setVisible(false);
        composeReportContainer.setManaged(false);
        viewAnnouncementsButton.getStyleClass().add("tab-active");
        composeReportButton.getStyleClass().add("tab-inactive");
        viewAnnouncements();
    }
    public void showComposeReport(){
        announcementContainer.setVisible(false);
        announcementContainer.setManaged(false);
        composeReportContainer.setVisible(true);
        composeReportContainer.setManaged(true);
        viewAnnouncementsButton.getStyleClass().add("tab-inactive");
        composeReportButton.getStyleClass().add("tab-active");
        viewComposeScreen();
    }
    public void viewAnnouncements(){
        announcementContainer.getChildren().clear();
        List<Report> allAnnouncements = MainController.dbManager.getReportsForLibrarian(librarian.getEmail());
        for(Report report : allAnnouncements){
            VBox reportCard = new VBox();
            reportCard.getStyleClass().add("report-card");
            reportCard.setMaxWidth(Double.MAX_VALUE);
            Label title = new Label("ANNOUNCEMENT");
            title.getStyleClass().add("header-label");
            Label content = new Label(report.getContent());
            content.getStyleClass().add("content-label");
            content.setWrapText(true);
            Label date = new Label(report.getTimestamp().toString());
            date.getStyleClass().addAll("date-label");
            reportCard.getChildren().addAll(title, content, date);
            announcementContainer.getChildren().add(reportCard);
        }     
    }
    public void viewComposeScreen(){
        reportHistoryVBox.getChildren().clear();
        reportHistoryVBox.setSpacing(20);
        List<Report> myReports = MainController.dbManager.getReportsBySender(MainController.getCurrentUser().getEmail());
        for(Report report : myReports){
            VBox reportCard = createReportCard(report.getContent(), report.getTimestamp().toString());
            reportHistoryVBox.getChildren().add(reportCard);
        }

    }
    private VBox createReportCard(String content, String dateText) {
        VBox card = new VBox(5);
        card.getStyleClass().add("report-card");
        card.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label("ANNOUNCEMENT");
        title.getStyleClass().add("header-label");
        title.setStyle("-fx-font-size: 15px;");

        Label contentLabel = new Label(content);
        contentLabel.getStyleClass().add("content-label");
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 14px;");

        Label dateLabel = new Label(dateText);
        dateLabel.getStyleClass().add("date-label");

        card.getChildren().addAll(title, contentLabel, dateLabel);
        return card;
    }
    public void handleSentReport(){
        if(!reportTextField.getText().isEmpty()){
            Report newReport = new Report(reportTextField.getText(), MainController.getCurrentUser().getEmail(), "ALL");
            MainController.dbManager.saveReport(newReport);
            VBox newReportCard = createReportCard(reportTextField.getText(), "Just now");
            reportHistoryVBox.getChildren().add(newReportCard);
            reportTextField.clear();
        }
    }

    public void backToDashboard(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian.fxml"));
        Parent root = loader.load();
        App.PRIMARY_STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
}

