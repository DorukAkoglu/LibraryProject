package studyLibrary.project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationController {

    @FXML private ListView<Notification> notificationListView;
    @FXML private VBox emptyStateBox;
    @FXML private Label unreadCountLabel;

    @FXML private Button filterAllButton;
    @FXML private Button filterUnreadButton;
    @FXML private Button filterBooksButton;
    @FXML private Button filterStudyButton;
    @FXML private Button filterTableButton;

    private List<Notification> allNotifications;
    private int userID;

    @FXML
    public void initialize() {
        userID = MainController.getCurrentUser().getUserID();
        allNotifications = NotificationManager.getInstance().getAllNotifications(userID);
        setupCellFactory();
        loadNotifications(allNotifications);
        updateUnreadCount();
    }
    @FXML
    private void filterAll() {
        setActiveFilterButton(filterAllButton);
        loadNotifications(allNotifications);
    }

    @FXML
    private void filterUnread() {
        setActiveFilterButton(filterUnreadButton);
        List<Notification> filtered = new ArrayList<>();
        for (Notification notification : allNotifications) {
            if (!notification.isRead()) {
                filtered.add(notification);
            }
        }
        loadNotifications(filtered);
    }

    @FXML
    private void filterBooks() {
        setActiveFilterButton(filterBooksButton);
        List<Notification> filtered = new ArrayList<>();
        for (Notification notification : allNotifications) {
            Notification.NotificationType type = notification.getType();
            if (type == Notification.NotificationType.BOOK_BORROWED ||
                type == Notification.NotificationType.BOOK_RESERVED ||
                type == Notification.NotificationType.BOOK_DUE_SOON ||
                type == Notification.NotificationType.BOOK_DUE_TOMORROW ||
                type == Notification.NotificationType.BOOK_OVERDUE) {
                filtered.add(notification);
            }
        }
        loadNotifications(filtered);
    }

    @FXML
    private void filterStudy() {
        setActiveFilterButton(filterStudyButton);
        List<Notification> filtered = new ArrayList<>();
        for (Notification notification : allNotifications) {
            Notification.NotificationType type = notification.getType();
            if (type == Notification.NotificationType.STUDY_REQUEST_RECEIVED ||
                type == Notification.NotificationType.STUDY_REQUEST_ACCEPTED ||
                type == Notification.NotificationType.STUDY_REQUEST_REJECTED ||
                type == Notification.NotificationType.STUDY_MATCH_FOUND) {
                filtered.add(notification);
            }
        }
        loadNotifications(filtered);
    }

    @FXML
    private void filterTable() {
        setActiveFilterButton(filterTableButton);
        List<Notification> filtered = new ArrayList<>();
        for (Notification notification : allNotifications) {
            Notification.NotificationType type = notification.getType();
            if (type == Notification.NotificationType.TABLE_RESERVATION_CONFIRMED ||
                type == Notification.NotificationType.TABLE_RESERVATION_CANCELLED) {
                filtered.add(notification);
            }
        }
        loadNotifications(filtered);
    }
    @FXML
    private void markAllAsRead() {
        NotificationManager.getInstance().markAllAsRead(userID);
        for (Notification notification : allNotifications) {
            notification.setRead(true);
        }
        notificationListView.refresh();
        updateUnreadCount();
    }
    @FXML
    private void clearAll() {
        NotificationManager.getInstance().clearAllNotifications(userID);
        allNotifications.clear();
        notificationListView.getItems().clear();
        updateUnreadCount();
        showEmptyState(true);
    }
    @FXML
    private void backToDashboard() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/student.fxml"));
        App.PRIMARY_STAGE.getScene().setRoot(root);
    }
    private void loadNotifications(List<Notification> list) {
        notificationListView.getItems().clear();
        if (list == null || list.isEmpty()) {
            showEmptyState(true);
        } 
        else {
            showEmptyState(false);
            notificationListView.getItems().addAll(list);
        }
    }

    private void updateUnreadCount() {
        int count = 0;
        for (Notification notification : allNotifications) {
            if (!notification.isRead()) {
                count++;
            }
        }
        unreadCountLabel.setText(String.valueOf(count));
    }
    private void showEmptyState(boolean show) {
        emptyStateBox.setVisible(show);
        emptyStateBox.setManaged(show);
        notificationListView.setVisible(!show);
        notificationListView.setManaged(!show);
    }

    private void setActiveFilterButton(Button active) {
        Button[] allButtons = {filterAllButton, filterUnreadButton, filterBooksButton, filterStudyButton, filterTableButton};
        for (Button button : allButtons) {
            button.getStyleClass().remove("filter-button-active");
            if (!button.getStyleClass().contains("filter-button")) {
                button.getStyleClass().add("filter-button");
            }
        }
        active.getStyleClass().remove("filter-button");
        active.getStyleClass().add("filter-button-active");
    }

    private void setupCellFactory() {
        notificationListView.setCellFactory(list -> new ListCell<Notification>() {
            @Override
            protected void updateItem(Notification notification, boolean empty) {
                super.updateItem(notification, empty);
                if (empty || notification == null) {
                    setGraphic(null);
                    return;
                }
                Label iconLabel = new Label(iconPicture(notification.getType()));
                iconLabel.setStyle("-fx-font-size: 20px; -fx-min-width: 44px; -fx-alignment: center;");
                Label titleLabel = new Label(notification.getTitle());
                titleLabel.getStyleClass().add(notification.isRead() ? "notif-title-read" : "notif-title-unread");

                Label messageLabel = new Label(notification.getMessage());
                messageLabel.getStyleClass().add("notif-message");
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(480);

                String date = notification.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                Label timeLabel = new Label(date);
                timeLabel.getStyleClass().add("notif-time");

                VBox textBox = new VBox(3, titleLabel, messageLabel, timeLabel);
                HBox.setHgrow(textBox, Priority.ALWAYS);

                Label dotLabel = new Label(notification.isRead() ? "" : "●");
                dotLabel.setStyle("-fx-text-fill: #4a90d9; -fx-font-size: 10px; -fx-min-width: 16px; -fx-alignment: center-right;");

                HBox card = new HBox(12, iconLabel, textBox, dotLabel);
                card.setAlignment(Pos.CENTER_LEFT);
                card.getStyleClass().add(notification.isRead() ? "notif-card-read" : "notif-card-unread");
                card.setPadding(new Insets(12, 16, 12, 16));
                card.setOnMouseClicked(e -> {
                    if (!notification.isRead()) {
                        NotificationManager.getInstance().markAsRead(notification.getId());
                        notification.setRead(true);
                        notificationListView.refresh();
                        updateUnreadCount();
                    }
                });
                VBox wrapper = new VBox(card);
                wrapper.setStyle("-fx-padding: 4 8 0 8;");
                setGraphic(wrapper);
                setText(null);
            }
        });
    }

    private String iconPicture(Notification.NotificationType type) {
        switch (type) {
            case BOOK_BORROWED: return "📖";
            case BOOK_RESERVED: return "🔖";
            case BOOK_DUE_SOON: return "⏰";
            case BOOK_DUE_TOMORROW: return "⚠️";
            case BOOK_OVERDUE: return "🚨";
            case STUDY_REQUEST_RECEIVED: return "📨";
            case STUDY_REQUEST_ACCEPTED: return "🤝";
            case STUDY_REQUEST_REJECTED: return "❌";
            case STUDY_MATCH_FOUND: return "✨";
            case TABLE_RESERVATION_CONFIRMED: return "🪑";
            case TABLE_RESERVATION_CANCELLED: return "🚫";
            default: return "🔔";
        }
    }
}
