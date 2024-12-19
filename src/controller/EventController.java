package controller;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.Event;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.sql.Date;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EventController implements Initializable {
    
    @FXML private Button goToDashboard;
    @FXML private Button goToClient;
    @FXML private Button goToEvent;
    @FXML private Button goToSchedule;
    @FXML private Button btnAddEventTop;

    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, Integer> numberColumn;
    @FXML private TableColumn<Event, String> nameColumn;
    @FXML private TableColumn<Event, String> descriptionColumn;
    @FXML private TableColumn<Event, String> categoryColumn;
    @FXML private TableColumn<Event, Date> dateColumn;
    @FXML private TableColumn<Event, String> locationColumn;
    @FXML private TableColumn<Event, String> clientNameColumn;

    @FXML private AnchorPane addEventPopup;
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> clientComboBox;
    
    @FXML private Button btnSaveEvent;
    @FXML private Button btnUpdateEvent;
    @FXML private Button btnCancelEvent;
    @FXML private Button btnDeleteEvent;

    private ObservableList<Event> eventData = FXCollections.observableArrayList();
    private Connection connection;
    private Event selectedEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = DatabaseConnection.getDBConnection();
        } catch (SQLException e) {
            showAlert("Database Connection Error", e.getMessage());
            return;
        }

        // Configure table columns
        numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        clientNameColumn.setCellValueFactory(cellData -> {
            String clientName = getClientNameById(cellData.getValue().getClientId());
            return new javafx.beans.property.SimpleStringProperty(clientName);
        });

        eventTable.setItems(eventData);

        // Add listener for row selection
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEvent = newSelection;
                populateEventFields(newSelection);
                btnSaveEvent.setVisible(false);
                btnUpdateEvent.setVisible(true);
                btnDeleteEvent.setVisible(true);
                addEventPopup.setVisible(true);
            }
        });

        // Load categories into combo box
        categoryComboBox.setItems(FXCollections.observableArrayList(Category.values()));
        
        // Load clients into combo box
        loadClientNames();

        setupButtons();
        loadEventsFromDatabase();
    }

    private void setupButtons() {
        btnAddEventTop.setOnAction(event -> {
            clearFields();
            btnSaveEvent.setVisible(true);
            btnUpdateEvent.setVisible(false);
            btnDeleteEvent.setVisible(false);
            addEventPopup.setVisible(true);
        });

        btnSaveEvent.setOnAction(event -> saveEvent());
        btnUpdateEvent.setOnAction(event -> updateEvent());
        btnDeleteEvent.setOnAction(event -> deleteEvent());
        btnCancelEvent.setOnAction(event -> {
            addEventPopup.setVisible(false);
            clearFields();
        });
    }

    private void saveEvent() {
        if (!validateEventFields()) return;

        try {
            String query = "INSERT INTO EVENTS (name, description, category, date, location, client_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            setEventStatementParameters(statement);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    Event newEvent = createEventFromFields();
                    newEvent.setId(id);
                    eventData.add(newEvent);
                }
                clearFields();
                addEventPopup.setVisible(false);
            }
        } catch (SQLException e) {
            showAlert("Error Saving", e.getMessage());
        }
    }

    private void updateEvent() {
        if (selectedEvent == null || !validateEventFields()) return;

        try {
            String query = "UPDATE EVENTS SET name=?, description=?, category=?, date=?, location=?, client_id=? WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            setEventStatementParameters(statement);
            statement.setInt(7, selectedEvent.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                int index = eventData.indexOf(selectedEvent);
                Event updatedEvent = createEventFromFields();
                updatedEvent.setId(selectedEvent.getId());
                eventData.set(index, updatedEvent);
                clearFields();
                addEventPopup.setVisible(false);
            }
        } catch (SQLException e) {
            showAlert("Error Updating", e.getMessage());
        }
    }

    private void deleteEvent() {
        if (selectedEvent == null) return;

        try {
            String query = "DELETE FROM EVENTS WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedEvent.getId());

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                eventData.remove(selectedEvent);
                clearFields();
                addEventPopup.setVisible(false);
            }
        } catch (SQLException e) {
            showAlert("Error Deleting", e.getMessage());
        }
    }

    private void loadEventsFromDatabase() {
        eventData.clear();
        try {
            String query = "SELECT * FROM EVENTS ORDER BY id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            int rowNum = 1;
            while (resultSet.next()) {
                Event event = new Event();
                event.setId(resultSet.getInt("id"));
                event.setNumber(rowNum++);
                event.setName(resultSet.getString("name"));
                event.setDescription(resultSet.getString("description"));
                event.setCategory(resultSet.getString("category"));
                event.setDate(resultSet.getDate("date"));
                event.setLocation(resultSet.getString("location"));
                event.setClientId(resultSet.getInt("client_id"));
                eventData.add(event);
            }
        } catch (SQLException e) {
            showAlert("Error Loading Data", e.getMessage());
        }
    }

    private void loadClientNames() {
        ObservableList<String> clientNames = FXCollections.observableArrayList();
        try {
            String query = "SELECT name FROM clients";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                clientNames.add(resultSet.getString("name"));
            }
            clientComboBox.setItems(clientNames);
        } catch (SQLException e) {
            showAlert("Error Loading Clients", e.getMessage());
        }
    }

    private void populateEventFields(Event event) {
        nameField.setText(event.getName());
        descriptionField.setText(event.getDescription());
        categoryComboBox.setValue(Category.valueOf(event.getCategory()));
        if (event.getDate() != null) {
        eventDatePicker.setValue(((java.sql.Date) event.getDate()).toLocalDate());
    } else {
        eventDatePicker.setValue(null);
    }
        locationField.setText(event.getLocation());
        clientComboBox.setValue(getClientNameById(event.getClientId()));
    }

    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        categoryComboBox.setValue(null);
        eventDatePicker.setValue(null);
        locationField.clear();
        clientComboBox.setValue(null);
        selectedEvent = null;
    }

    private boolean validateEventFields() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        Category category = categoryComboBox.getValue();
        LocalDate date = eventDatePicker.getValue();
        String location = locationField.getText().trim();
        String clientName = clientComboBox.getValue();

        if (name.isEmpty() || description.isEmpty() || category == null || 
            date == null || location.isEmpty() || clientName == null) {
            showAlert("Validation", "Please fill in all fields!");
            return false;
        }
        return true;
    }

    private void setEventStatementParameters(PreparedStatement statement) throws SQLException {
        statement.setString(1, nameField.getText().trim());
        statement.setString(2, descriptionField.getText().trim());
        statement.setString(3, categoryComboBox.getValue().name());
        statement.setDate(4, java.sql.Date.valueOf(eventDatePicker.getValue()));
        statement.setString(5, locationField.getText().trim());
        statement.setInt(6, getClientIdByName(clientComboBox.getValue()));
    }

    private Event createEventFromFields() {
        Event event = new Event();
        event.setName(nameField.getText().trim());
        event.setDescription(descriptionField.getText().trim());
        event.setCategory(categoryComboBox.getValue().name());
        event.setDate(java.sql.Date.valueOf(eventDatePicker.getValue()));
        event.setLocation(locationField.getText().trim());
        event.setClientId(getClientIdByName(clientComboBox.getValue()));
        return event;
    }

    private String getClientNameById(int clientId) {
        try {
            String query = "SELECT name FROM clients WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, clientId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private int getClientIdByName(String clientName) {
        try {
            String query = "SELECT id FROM clients WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, clientName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            showAlert("Connection Error", e.getMessage());
        }
    }

    // Navigation methods
    public void goToClient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Client.fxml"));
            Parent clientView = loader.load();
            Stage stage = (Stage) goToClient.getScene().getWindow();
            Scene scene = new Scene(clientView);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading Client view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Parent view = loader.load();
            Stage stage = (Stage) goToDashboard.getScene().getWindow();
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading Dashboard view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void goToEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Event.fxml"));
            Parent view = loader.load();
            Stage stage = (Stage) goToEvent.getScene().getWindow();
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading Event view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void goToSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Schedule.fxml"));
            Parent view = loader.load();
            Stage stage = (Stage) goToSchedule.getScene().getWindow();
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading Schedule view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public enum Category {
        Kecil, Sedang, Besar;

        @Override
        public String toString() {
            return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
        }
    }
}