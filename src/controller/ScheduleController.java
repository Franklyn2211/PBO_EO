package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Schedule;
import util.DatabaseConnection;

public class ScheduleController implements Initializable {

    @FXML
    private Button goToDashboard;
    @FXML
    private Button goToClient;
    @FXML
    private Button goToEvent;
    @FXML
    private Button goToSchedule;
    @FXML
    private Button btnAddScheduleTop;
    @FXML
    private Button btnUpdateSchedule;
    @FXML
    private Button btnDeleteSchedule;
    @FXML
    private AnchorPane addSchedulePopup;
    @FXML
    private Button btnSaveSchedule;
    @FXML
    private Button btnCancelSchedule;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private ComboBox<String> eventNameComboBox;
    @FXML
    private DatePicker decorationDatePicker;
    @FXML
    private TextField decorationTimeField;
    @FXML
    private TableView<Schedule> scheduleTable;
    @FXML
    private TableColumn<Schedule, String> eventNameColumn;
    @FXML
    private TableColumn<Schedule, String> statusColumn;
    @FXML
    private TableColumn<Schedule, String> decorationDateColumn;
    @FXML
    private TableColumn<Schedule, String> decorationTimeColumn;
    @FXML
    private Label popupTitle;  // For dynamic popup title (e.g., "Update Schedule")

    private ObservableList<Schedule> scheduleData = FXCollections.observableArrayList();
    private ObservableList<String> availableEvents = FXCollections.observableArrayList();
    private Connection connection;
    private Schedule selectedSchedule; // Store the selected schedule for updates

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = DatabaseConnection.getDBConnection();
        } catch (SQLException e) {
            showAlert("Database Connection Error", e.getMessage());
            return;
        }

        // Configure table columns
        eventNameColumn.setCellValueFactory(cellData -> cellData.getValue().eventNameProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Convert LocalDate to String for decorationDateColumn
        decorationDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDecorationDate().toString()));

        decorationTimeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDecorationTime()));

        scheduleTable.setItems(scheduleData);

        // Initialize combo boxes
        statusComboBox.setItems(FXCollections.observableArrayList("Selesai", "Belum Selesai"));
        loadAvailableEvents();

        setupButtons();
        loadSchedulesFromDatabase();

        // Set TableView to allow single selection
        scheduleTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Add a mouse click listener to the schedule table
        scheduleTable.setOnMouseClicked(event -> {
            selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
            if (selectedSchedule != null) {
                // Populate the fields with the selected schedule's data
                eventNameComboBox.setValue(selectedSchedule.getEventName());
                statusComboBox.setValue(selectedSchedule.getStatus());
                decorationDatePicker.setValue(selectedSchedule.getDecorationDate());
                decorationTimeField.setText(selectedSchedule.getDecorationTime());

                // Set popup title and button visibility
                popupTitle.setText("Update Schedule");
                btnSaveSchedule.setVisible(false); // Hide Save button
                btnUpdateSchedule.setVisible(true); // Show Update button
                btnDeleteSchedule.setVisible(true); // Show Delete button
                btnCancelSchedule.setVisible(true); // Show Cancel button

                // Show the schedule popup
                addSchedulePopup.setVisible(true);
            } else {
                // If no schedule is selected, hide the update and delete buttons
                btnSaveSchedule.setVisible(true);
                btnUpdateSchedule.setVisible(false);
                btnDeleteSchedule.setVisible(false);
            }
        });
    }

    private void setupButtons() {
        btnAddScheduleTop.setOnAction(event -> {
            clearFields();
            addSchedulePopup.setVisible(true);
            popupTitle.setText("Add Schedule");
            btnSaveSchedule.setVisible(true);  // Show 'Save' button for adding new schedule
            btnUpdateSchedule.setVisible(false);  // Hide 'Update' button
            btnDeleteSchedule.setVisible(false);  // Hide 'Delete' button
            btnCancelSchedule.setVisible(true);  // Show 'Cancel' button
        });

        btnSaveSchedule.setOnAction(event -> saveSchedule());
        btnCancelSchedule.setOnAction(event -> {
            addSchedulePopup.setVisible(false);  // Close popup when cancel is clicked
            clearFields();
        });

        btnUpdateSchedule.setOnAction(event -> updateSchedule());

        btnDeleteSchedule.setOnAction(event -> deleteSchedule());
    }

    private void updateSchedule() {
        if (selectedSchedule == null) return;

        String eventName = eventNameComboBox.getValue();
        String status = statusComboBox.getValue();
        LocalDate decorationDate = decorationDatePicker.getValue();
        String decorationTime = decorationTimeField.getText();

        if (eventName == null || status == null || decorationDate == null || decorationTime.isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            // Get event_id based on event_name selected
            PreparedStatement stmt = connection.prepareStatement("SELECT id FROM events WHERE name = ?");
            stmt.setString(1, eventName);
            ResultSet resultSet = stmt.executeQuery();
            int eventId = 0;

            if (resultSet.next()) {
                eventId = resultSet.getInt("id");
            }

            // Update the schedule
            String query = "UPDATE schedules SET event_id=?, status=?, decoration_date=?, decoration_time=? WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, eventId);
            statement.setString(2, status);
            statement.setDate(3, Date.valueOf(decorationDate));
            statement.setString(4, decorationTime);
            statement.setInt(5, selectedSchedule.getId());
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                // Update ObservableList
                selectedSchedule.setEventName(eventName);
                selectedSchedule.setStatus(status);
                selectedSchedule.setDecorationDate(decorationDate);
                selectedSchedule.setDecorationTime(decorationTime);

                scheduleData.set(scheduleData.indexOf(selectedSchedule), selectedSchedule);
                clearFields();
                addSchedulePopup.setVisible(false);
            }

        } catch (SQLException e) {
            showAlert("Error Update", e.getMessage());
        }
    }

    private void deleteSchedule() {
        if (selectedSchedule == null) return;

        try {
            String query = "DELETE FROM schedules WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedSchedule.getId());

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                // Remove the schedule from ObservableList
                scheduleData.remove(selectedSchedule);
                clearFields();
                addSchedulePopup.setVisible(false);
            }
        } catch (SQLException e) {
            showAlert("Error Delete", e.getMessage());
        }
    }

    private void clearFields() {
        eventNameComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        decorationDatePicker.setValue(null);
        decorationTimeField.clear();
    }

    private void loadAvailableEvents() {
        try {
            availableEvents.clear();
            String query = "SELECT id, name FROM events WHERE id NOT IN (SELECT event_id FROM schedules)";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String eventName = resultSet.getString("name");
                availableEvents.add(eventName);
            }

            eventNameComboBox.setItems(availableEvents);

        } catch (SQLException e) {
            showAlert("Database Query Error", e.getMessage());
        }
    }

    private void loadSchedulesFromDatabase() {
        try {
            scheduleData.clear();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM schedules");

            while (resultSet.next()) {
                int eventId = resultSet.getInt("event_id");
                PreparedStatement eventStmt = connection.prepareStatement("SELECT name FROM events WHERE id = ?");
                eventStmt.setInt(1, eventId);
                ResultSet eventResultSet = eventStmt.executeQuery();
                String eventName = "";
                if (eventResultSet.next()) {
                    eventName = eventResultSet.getString("name");
                }

                scheduleData.add(new Schedule(
                        resultSet.getInt("id"),
                        eventName,
                        resultSet.getString("status"),
                        resultSet.getDate("decoration_date").toLocalDate(),
                        resultSet.getString("decoration_time")
                ));
            }
        } catch (SQLException e) {
            showAlert("Database Query Error", e.getMessage());
        }
    }

    private void saveSchedule() {
        String eventName = eventNameComboBox.getValue();
        String status = statusComboBox.getValue();
        LocalDate decorationDate = decorationDatePicker.getValue();
        String decorationTime = decorationTimeField.getText();

        if (eventName == null || status == null || decorationDate == null || decorationTime.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT id FROM events WHERE name = ?");
            stmt.setString(1, eventName);
            ResultSet resultSet = stmt.executeQuery();
            int eventId = 0;

            if (resultSet.next()) {
                eventId = resultSet.getInt("id");
            }

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO schedules (event_id, status, decoration_date, decoration_time) VALUES (?, ?, ?, ?)"
            );
            statement.setInt(1, eventId);
            statement.setString(2, status);
            statement.setDate(3, Date.valueOf(decorationDate));
            statement.setString(4, decorationTime);
            statement.executeUpdate();

            loadSchedulesFromDatabase();
            addSchedulePopup.setVisible(false);

        } catch (SQLException e) {
            showAlert("Database Insert Error", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods
    public void goToDashboard() {
        navigateToView("/view/MainView.fxml");
    }

    public void goToClient() {
        navigateToView("/view/Client.fxml");
    }

    public void goToEvent() {
        navigateToView("/view/Event.fxml");
    }

    public void goToSchedule() {
        navigateToView("/view/Schedule.fxml");
    }

    private void navigateToView(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) goToSchedule.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Navigation Error", e.getMessage());
        }
    }
}
