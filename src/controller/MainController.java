package controller;

import javafx.collections.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.Event;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {

    @FXML private AnchorPane addEventPopup;
    @FXML private TextField eventNameField;
    @FXML private TextArea eventDescriptionField;
    @FXML private ComboBox<String> eventCategoryComboBox;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField eventLocationField; // Ubah dari waktu menjadi lokasi
    @FXML private Button btnSaveEvent;
    @FXML private Button btnCancelEvent;
    @FXML private Button btnAddEvent;

    @FXML
    private TableView<Event> eventTable;

    @FXML
    private TableColumn<Event, String> nameColumn;
    
    @FXML
    private TableColumn<Event, String> dateColumn;

    @FXML
    private TableColumn<Event, String> locationColumn;

    private ObservableList<Event> eventList = FXCollections.observableArrayList();

    public MainController() {
        // Empty constructor
    }

    @FXML
    private void initialize() {
        // Bind TableColumns to Event properties
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        // Load data from database
        loadEventData();
        
        // Initialize combo box
        ObservableList<String> categories = FXCollections.observableArrayList("Besar", "Sedang", "Kecil");
        eventCategoryComboBox.setItems(categories);
        eventCategoryComboBox.setPromptText("Pilih Kategori");

        // Hide popup initially
        if (addEventPopup != null) {
            addEventPopup.setVisible(false);
            addEventPopup.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 0);");
            addEventPopup.setLayoutX(200);
            addEventPopup.setLayoutY(100);
        }

        if (btnAddEvent != null) {
            btnAddEvent.setOnAction(event -> showAddEventPopup());
        }

        if (btnSaveEvent != null) {
            btnSaveEvent.setOnAction(event -> handleSaveEvent());
        }

        if (btnCancelEvent != null) {
            btnCancelEvent.setOnAction(event -> handleCancelEvent());
        }
    }
    
    private void loadEventData() {
        eventList.clear();
        try (Connection connection = DatabaseConnection.getDBConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT name, type, date, location FROM events";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String date = resultSet.getString("date");
                String location = resultSet.getString("location");

                // Add data to ObservableList
                eventList.add(new Event(name, date, location));
            }

        } catch (SQLException e) {
            System.out.println("Error loading event data: " + e.getMessage());
        }

        // Bind data to TableView
        eventTable.setItems(eventList);
    }

    @FXML
    private void showAddEventPopup() {
        clearForm();
        addEventPopup.setVisible(true);
    }

    private void clearForm() {
        if (eventNameField != null) eventNameField.clear();
        if (eventDescriptionField != null) eventDescriptionField.clear();
        if (eventCategoryComboBox != null) eventCategoryComboBox.getSelectionModel().clearSelection();
        if (eventDatePicker != null) eventDatePicker.setValue(null);
        if (eventLocationField != null) eventLocationField.clear();
    }

    @FXML
    private void handleSaveEvent() {
        if (!validateInputs()) {
            return;
        }

        try (Connection connection = DatabaseConnection.getDBConnection()) {
            String insertQuery = "INSERT INTO events (name, type, description, category, date, location) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, eventNameField.getText());
                preparedStatement.setString(2, "Custom"); // Set default type
                preparedStatement.setString(3, eventDescriptionField.getText());
                preparedStatement.setString(4, eventCategoryComboBox.getValue());
                preparedStatement.setDate(5, java.sql.Date.valueOf(eventDatePicker.getValue()));
                preparedStatement.setString(6, eventLocationField.getText());

                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    showSuccessAlert("Event berhasil disimpan ke database!");

                    Event newEvent = new Event(
                        eventNameField.getText(),
                        eventDescriptionField.getText(),
                        eventCategoryComboBox.getValue(),
                        eventDatePicker.getValue(),
                        eventLocationField.getText() // Lokasi
                    );

                    eventList.add(newEvent);
                    eventTable.setItems(eventList);
                }
            }
        } catch (SQLException exception) {
            showErrorAlert("Terjadi kesalahan saat menyimpan ke database: " + exception.getMessage());
        }

        clearForm();
        addEventPopup.setVisible(false);
    }

    @FXML
    private void handleCancelEvent() {
        clearForm();
        addEventPopup.setVisible(false);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (isNullOrEmpty(eventNameField.getText())) errors.append("- Nama event harus diisi\n");
        if (isNullOrEmpty(eventDescriptionField.getText())) errors.append("- Deskripsi event harus diisi\n");
        if (eventCategoryComboBox.getValue() == null) errors.append("- Kategori harus dipilih\n");
        if (eventDatePicker.getValue() == null) errors.append("- Tanggal harus dipilih\n");
        if (isNullOrEmpty(eventLocationField.getText())) errors.append("- Lokasi harus diisi\n");

        if (errors.length() > 0) {
            showErrorAlert("Mohon perbaiki kesalahan berikut:\n" + errors);
            return false;
        }

        return true;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
