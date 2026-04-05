package studyLibrary.project;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class AdminController {

    @FXML private ImageView profileImage;
    @FXML private Label adminNameLabel;
    @FXML private Label adminRoleLabel;

    @FXML private Button overviewButton;
    @FXML private Button searchButton;
    @FXML private Button addButton;
    @FXML private Button manageButton;

    @FXML private VBox overviewSection;
    @FXML private VBox searchSection;
    @FXML private VBox addSection;
    @FXML private VBox manageSection;

    @FXML private Label totalUsersLabel;
    @FXML private Label studentCountLabel;
    @FXML private Label librarianCountLabel;
    @FXML private Label adminCountLabel;

    @FXML private TextField searchField;
    @FXML private ListView<User> searchUserListView;

    @FXML private TextField addUserIDField;
    @FXML private TextField addNameField;
    @FXML private TextField addEmailField;
    @FXML private TextField addPasswordField;
    @FXML private ComboBox<String> addRoleComboBox;
    @FXML private TextField addAgeField;
    @FXML private TextField addGradeField;
    @FXML private TextField addDepartmentField;

    @FXML private ListView<User> manageUserListView;
    @FXML private TextField editUserIDField;
    @FXML private TextField editNameField;
    @FXML private TextField editEmailField;
    @FXML private TextField editPasswordField;
    @FXML private ComboBox<String> editRoleComboBox;
    @FXML private TextField editAgeField;
    @FXML private TextField editGradeField;
    @FXML private TextField editDepartmentField;

    private LibrarySystem system;
    private Admin currentAdmin;

    @FXML
    public void initialize() {
        system = LibrarySystem.getInstance();

        if (MainController.getCurrentUser() instanceof Admin admin) {
            currentAdmin = admin;
        }

        configureProfile();
        configureRoleBoxes();
        configureLists();
        refreshAllData();
        showOverviewSection();
    }

    @FXML
    public void showOverviewSection() {
        setActiveSection(overviewSection, overviewButton);
        refreshStats();
    }

    @FXML
    public void showSearchSection() {
        setActiveSection(searchSection, searchButton);
        refreshSearchList();
    }

    @FXML
    public void showAddSection() {
        setActiveSection(addSection, addButton);
    }

    @FXML
    public void showManageSection() {
        setActiveSection(manageSection, manageButton);
        refreshManageList();
    }

    @FXML
    public void handleSearchAction() {
        if (!ensureAdmin()) {
            return;
        }

        String keyword = searchField.getText();
        if (keyword == null || keyword.isBlank()) {
            refreshSearchList();
            return;
        }

        String normalized = keyword.trim().toLowerCase();
        searchUserListView.getItems().clear();
        for (User user : system.getUsers()) {
            if (matchesKeyword(user, normalized)) {
                searchUserListView.getItems().add(user);
            }
        }
    }

    @FXML
    public void handleRefreshAction() {
        refreshAllData();
    }

    @FXML
    public void handleAddUserAction() {
        if (!ensureAdmin()) {
            return;
        }

        try {
            User newUser = createUserFromAddForm();
            currentAdmin.addUser(system, newUser);
            refreshAllData();
            clearAddForm();
            showMessage("User added successfully.");
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage());
        }
    }

    @FXML
    public void handleUpdateUserAction() {
        if (!ensureAdmin()) {
            return;
        }

        User selectedUser = manageUserListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showMessage("Select a user first.");
            return;
        }

        try {
            updateExistingUser(selectedUser);
            system.updateUserDB(selectedUser);
            refreshAllData();
            showMessage("User updated successfully.");
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage());
        }
    }

    @FXML
    public void handleRemoveUserAction() {
        if (!ensureAdmin()) {
            return;
        }

        User selectedUser = manageUserListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showMessage("Select a user first.");
            return;
        }

        currentAdmin.removeUser(system, selectedUser);
        refreshAllData();
        clearEditForm();
        showMessage("User removed successfully.");
    }

    @FXML
    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            App.PRIMARY_STAGE.getScene().setRoot(root);
        } catch (IOException e) {
            showMessage("Could not load login screen.");
        }
    }

    private void configureProfile() {
        if (currentAdmin != null) {
            adminNameLabel.setText(currentAdmin.getName());
            adminRoleLabel.setText("System Administrator");
            if (currentAdmin.getProfilePhoto() != null && !currentAdmin.getProfilePhoto().isBlank()) {
                profileImage.setImage(new Image(currentAdmin.getProfilePhoto(), true));
            }
        }
    }

    private void configureRoleBoxes() {
        addRoleComboBox.getItems().addAll("Student", "Librarian", "Admin");
        addRoleComboBox.setValue("Student");
        addRoleComboBox.valueProperty().addListener((obs, oldValue, newValue) -> updateAddFormState());

        editRoleComboBox.getItems().addAll("Student", "Librarian", "Admin");
        editRoleComboBox.setDisable(true);

        updateAddFormState();
        updateEditFormState(false);
    }

    private void configureLists() {
        searchUserListView.setCellFactory(list -> createUserCell());
        manageUserListView.setCellFactory(list -> createUserCell());

        manageUserListView.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, selectedUser) -> {
            if (selectedUser != null) {
                fillEditForm(selectedUser);
            }
        });
    }

    private ListCell<User> createUserCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(formatUser(user));
                }
            }
        };
    }

    private void refreshAllData() {
        refreshStats();
        refreshSearchList();
        refreshManageList();
    }

    private void refreshStats() {
        int total = 0;
        int students = 0;
        int librarians = 0;
        int admins = 0;

        for (User user : system.getUsers()) {
            total++;
            if (user instanceof Student) {
                students++;
            } else if (user instanceof Librarian) {
                librarians++;
            } else if (user instanceof Admin) {
                admins++;
            }
        }

        totalUsersLabel.setText(String.valueOf(total));
        studentCountLabel.setText(String.valueOf(students));
        librarianCountLabel.setText(String.valueOf(librarians));
        adminCountLabel.setText(String.valueOf(admins));
    }

    private void refreshSearchList() {
        searchUserListView.getItems().setAll(system.getUsers());
    }

    private void refreshManageList() {
        manageUserListView.getItems().setAll(system.getUsers());
    }

    private void setActiveSection(VBox activeSection, Button activeButton) {
        overviewSection.setVisible(false);
        overviewSection.setManaged(false);
        searchSection.setVisible(false);
        searchSection.setManaged(false);
        addSection.setVisible(false);
        addSection.setManaged(false);
        manageSection.setVisible(false);
        manageSection.setManaged(false);

        activeSection.setVisible(true);
        activeSection.setManaged(true);
    }

    private boolean matchesKeyword(User user, String keyword) {
        return user.getName().toLowerCase().contains(keyword)
                || user.getEmail().toLowerCase().contains(keyword)
                || describeRole(user).toLowerCase().contains(keyword)
                || String.valueOf(user.getUserID()).contains(keyword);
    }

    private User createUserFromAddForm() {
        int userID = parseRequiredInt(addUserIDField.getText(), "User ID must be a number.");
        String name = requireText(addNameField.getText(), "Name is required.");
        String email = requireText(addEmailField.getText(), "Email is required.");
        String password = requireText(addPasswordField.getText(), "Password is required.");
        String role = addRoleComboBox.getValue();

        if ("Student".equals(role)) {
            int age = parseRequiredInt(addAgeField.getText(), "Age must be a number.");
            int grade = parseRequiredInt(addGradeField.getText(), "Grade must be a number.");
            String department = requireText(addDepartmentField.getText(), "Department is required for students.");
            return new Student(userID, name, email, password, age, grade, department);
        }
        if ("Librarian".equals(role)) {
            return new Librarian(userID, name, email, password);
        }
        return new Admin(userID, name, email, password);
    }

    private void updateExistingUser(User user) {
        user.setUserID(parseRequiredInt(editUserIDField.getText(), "User ID must be a number."));
        user.setName(requireText(editNameField.getText(), "Name is required."));
        user.setEmail(requireText(editEmailField.getText(), "Email is required."));
        user.updatePassword(requireText(editPasswordField.getText(), "Password is required."));

        if (user instanceof Student student) {
            student.setAge(parseRequiredInt(editAgeField.getText(), "Age must be a number."));
            student.setGrade(parseRequiredInt(editGradeField.getText(), "Grade must be a number."));
            student.setDepartment(requireText(editDepartmentField.getText(), "Department is required for students."));
        }
    }

    private void fillEditForm(User user) {
        editUserIDField.setText(String.valueOf(user.getUserID()));
        editNameField.setText(user.getName());
        editEmailField.setText(user.getEmail());
        editPasswordField.setText(user.getPassword());
        editRoleComboBox.setValue(describeRole(user));

        if (user instanceof Student student) {
            editAgeField.setText(String.valueOf(student.getAge()));
            editGradeField.setText(String.valueOf(student.getGrade()));
            editDepartmentField.setText(student.getDepartment());
            updateEditFormState(true);
        } else {
            editAgeField.clear();
            editGradeField.clear();
            editDepartmentField.clear();
            updateEditFormState(false);
        }
    }

    private void clearAddForm() {
        addUserIDField.clear();
        addNameField.clear();
        addEmailField.clear();
        addPasswordField.clear();
        addAgeField.clear();
        addGradeField.clear();
        addDepartmentField.clear();
        addRoleComboBox.setValue("Student");
        updateAddFormState();
    }

    private void clearEditForm() {
        editUserIDField.clear();
        editNameField.clear();
        editEmailField.clear();
        editPasswordField.clear();
        editRoleComboBox.setValue(null);
        editAgeField.clear();
        editGradeField.clear();
        editDepartmentField.clear();
        updateEditFormState(false);
    }

    private void updateAddFormState() {
        boolean isStudent = "Student".equals(addRoleComboBox.getValue());
        addAgeField.setDisable(!isStudent);
        addGradeField.setDisable(!isStudent);
        addDepartmentField.setDisable(!isStudent);
    }

    private void updateEditFormState(boolean isStudent) {
        editAgeField.setDisable(!isStudent);
        editGradeField.setDisable(!isStudent);
        editDepartmentField.setDisable(!isStudent);
    }

    private String formatUser(User user) {
        String role = describeRole(user);
        String details = role;
        if (user instanceof Student student) {
            details = role + " | Grade: " + student.getGrade() + " | " + student.getDepartment();
        }
        return "#" + user.getUserID() + "  " + user.getName()
                + "\n" + user.getEmail()
                + "\n" + details;
    }

    private String describeRole(User user) {
        if (user instanceof Student) {
            return "Student";
        }
        if (user instanceof Librarian) {
            return "Librarian";
        }
        return "Admin";
    }

    private int parseRequiredInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private String requireText(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value.trim();
    }

    private boolean ensureAdmin() {
        if (currentAdmin == null) {
            showMessage("No admin session is active.");
            return false;
        }
        return true;
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Admin Panel");
        alert.setContentText(message);
        alert.show();
    }
}
