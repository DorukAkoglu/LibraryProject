package studyLibrary.project;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class AdminController {

    @FXML private TextField searchField;
    @FXML private TextField userIDField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField ageField;
    @FXML private TextField gradeField;
    @FXML private TextField departmentField;
    @FXML private ListView<User> userListView;

    private LibrarySystem system;
    private Admin currentAdmin;

    @FXML
    public void initialize() {
        system = LibrarySystem.getInstance();

        if (MainController.getCurrentUser() instanceof Admin admin) {
            currentAdmin = admin;
        }

        roleComboBox.getItems().addAll("Student", "Librarian", "Admin");
        roleComboBox.setValue("Student");
        roleComboBox.valueProperty().addListener((obs, oldValue, newValue) -> updateRoleSpecificFields());

        userListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(formatUser(user));
                }
            }
        });

        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, selectedUser) -> {
            if (selectedUser != null) {
                fillForm(selectedUser);
            }
        });

        updateRoleSpecificFields();
        refreshUserList();
    }

    @FXML
    public void handleAddUserAction() {
        if (!ensureAdmin()) {
            return;
        }

        try {
            User newUser = createUserFromForm();
            currentAdmin.addUser(system, newUser);
            refreshUserList();
            clearForm();
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

        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showMessage("Select a user before updating.");
            return;
        }

        try {
            updateExistingUser(selectedUser);
            system.updateUserDB(selectedUser);
            refreshUserList();
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

        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showMessage("Select a user before removing.");
            return;
        }

        currentAdmin.removeUser(system, selectedUser);
        refreshUserList();
        clearForm();
        showMessage("User removed successfully.");
    }

    @FXML
    public void handleSearchAction() {
        if (!ensureAdmin()) {
            return;
        }

        String keyword = searchField.getText();
        if (keyword == null || keyword.isBlank()) {
            refreshUserList();
            return;
        }

        String normalized = keyword.trim().toLowerCase();
        userListView.getItems().clear();
        for (User user : system.getUsers()) {
            String role = describeRole(user).toLowerCase();
            if (user.getName().toLowerCase().contains(normalized)
                    || user.getEmail().toLowerCase().contains(normalized)
                    || role.contains(normalized)
                    || String.valueOf(user.getUserID()).contains(normalized)) {
                userListView.getItems().add(user);
            }
        }
    }

    @FXML
    public void handleRefreshAction() {
        refreshUserList();
    }

    private void refreshUserList() {
        userListView.getItems().clear();
        userListView.getItems().addAll(system.getUsers());
    }

    private User createUserFromForm() {
        int userID = parseRequiredInt(userIDField.getText(), "User ID must be a number.");
        String name = requireText(nameField.getText(), "Name is required.");
        String email = requireText(emailField.getText(), "Email is required.");
        String password = requireText(passwordField.getText(), "Password is required.");
        String role = roleComboBox.getValue();

        if ("Student".equals(role)) {
            int age = parseRequiredInt(ageField.getText(), "Age must be a number.");
            int grade = parseRequiredInt(gradeField.getText(), "Grade must be a number.");
            String department = requireText(departmentField.getText(), "Department is required for students.");
            return new Student(userID, name, email, password, age, grade, department);
        }
        if ("Librarian".equals(role)) {
            return new Librarian(userID, name, email, password);
        }
        return new Admin(userID, name, email, password);
    }

    private void updateExistingUser(User user) {
        user.setUserID(parseRequiredInt(userIDField.getText(), "User ID must be a number."));
        user.setName(requireText(nameField.getText(), "Name is required."));
        user.setEmail(requireText(emailField.getText(), "Email is required."));
        user.updatePassword(requireText(passwordField.getText(), "Password is required."));

        if (user instanceof Student student) {
            student.setAge(parseRequiredInt(ageField.getText(), "Age must be a number."));
            student.setGrade(parseRequiredInt(gradeField.getText(), "Grade must be a number."));
            student.setDepartment(requireText(departmentField.getText(), "Department is required for students."));
        }
    }

    private void fillForm(User user) {
        userIDField.setText(String.valueOf(user.getUserID()));
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        passwordField.setText(user.getPassword());

        if (user instanceof Student student) {
            roleComboBox.setValue("Student");
            ageField.setText(String.valueOf(student.getAge()));
            gradeField.setText(String.valueOf(student.getGrade()));
            departmentField.setText(student.getDepartment());
        } else if (user instanceof Librarian) {
            roleComboBox.setValue("Librarian");
            ageField.clear();
            gradeField.clear();
            departmentField.clear();
        } else {
            roleComboBox.setValue("Admin");
            ageField.clear();
            gradeField.clear();
            departmentField.clear();
        }

        updateRoleSpecificFields();
    }

    private void clearForm() {
        userIDField.clear();
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        ageField.clear();
        gradeField.clear();
        departmentField.clear();
        roleComboBox.setValue("Student");
        updateRoleSpecificFields();
    }

    private void updateRoleSpecificFields() {
        boolean isStudent = "Student".equals(roleComboBox.getValue());
        ageField.setDisable(!isStudent);
        gradeField.setDisable(!isStudent);
        departmentField.setDisable(!isStudent);
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
