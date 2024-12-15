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
import model.Client;

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
    private TableView<Client> clientTable;

    @FXML
    private TableColumn<Client, String> nameColumn;

    @FXML
    private TableColumn<Client, String> contactColumn;

    @FXML
    private TableColumn<Client, String> eventColumn;

    @FXML
    private TableView<Event> scheduleTable;

    @FXML
    private TableColumn<Event, String> dateColumn;

    @FXML
    private TableColumn<Event, String> locationColumn;

    @FXML
    private TableColumn<Event, String> statusColumn;

    private ObservableList<Client> clientList = FXCollections.observableArrayList();
    private ObservableList<Event> eventList = FXCollections.observableArrayList();

    public MainController() {
        // Empty constructor
    }

    @FXML
    private void initialize() {
         nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        eventColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());


        // Load data into clientTable
        loadClientData();

        // Set listener for clientTable
        clientTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadEventDetails(newValue.getEventId());
            }
        });
        
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
    
    private void loadClientData() {
        clientList.clear();
        try (Connection connection = DatabaseConnection.getDBConnection();
             Statement statement = connection.createStatement()) {

            String query = """
                    SELECT clients.id AS client_id, clients.name AS client_name, clients.contact, events.id AS event_id, events.name AS event_name
                    FROM clients
                    LEFT JOIN events ON clients.event_id = events.id
                    """;
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int clientId = resultSet.getInt("client_id");
                String clientName = resultSet.getString("client_name");
                String contact = resultSet.getString("contact");
                int eventId = resultSet.getInt("event_id");
                String eventName = resultSet.getString("event_name");

                clientList.add(new Client(clientId, clientName, contact, eventId, eventName));
            }

        } catch (SQLException e) {
            System.out.println("Error loading client data: " + e.getMessage());
        }

        clientTable.setItems(clientList);
    }
    
    private void loadEventDetails(int eventId) {
    eventList.clear();
    try (Connection connection = DatabaseConnection.getDBConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("""
                SELECT events.name AS event_name, events.date, events.location
                FROM events
                LEFT JOIN schedules ON events.id = schedules.event_id
                WHERE events.id = ?
         """)) {
        preparedStatement.setInt(1, eventId);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString("event_name");
            String date = resultSet.getString("date");
            String location = resultSet.getString("location");

            eventList.add(new Event(name, null, date, location, null)); // Abaikan status
        }
    } catch (SQLException e) {
        System.out.println("Error loading event details: " + e.getMessage());
    }

    scheduleTable.setItems(eventList);
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
    if (!validateInputs()) return;

    try (Connection connection = DatabaseConnection.getDBConnection()) {
        String insertQuery = """
            INSERT INTO events (name, description, category, date, location)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, eventNameField.getText());
            preparedStatement.setString(3, eventDescriptionField.getText());
            preparedStatement.setString(4, eventCategoryComboBox.getValue());
            preparedStatement.setDate(5, java.sql.Date.valueOf(eventDatePicker.getValue()));
            preparedStatement.setString(6, eventLocationField.getText());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                showSuccessAlert("Event berhasil disimpan!");

                // Muat ulang tabel dari database
                loadClientData();
            }
        }
    } catch (SQLException e) {
        showErrorAlert("Terjadi kesalahan saat menyimpan data ke database: " + e.getMessage());
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
