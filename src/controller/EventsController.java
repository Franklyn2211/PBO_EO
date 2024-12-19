package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.Event;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import javafx.scene.layout.HBox;

public class EventsController {

    // Table view columns for events
    @FXML private TableView<Event> clientTable;
    @FXML private TableColumn<Event, Integer> numberColumn;
    @FXML private TableColumn<Event, String> nameColumn;
    @FXML private TableColumn<Event, String> descriptionColumn;
    @FXML private TableColumn<Event, String> categoryColumn;
    @FXML private TableColumn<Event, Date> dateColumn;
    @FXML private TableColumn<Event, String> locationColumn;
    @FXML private TableColumn<Event, String> clientNameColumn;

    // Add Event Form fields
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<Category> categoryComboBox;  // Use enum type for category
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> clientComboBox;

    // Update Event Form fields
    @FXML private TextField updateNameField;
    @FXML private TextField updateDescriptionField;
    @FXML private ComboBox<Category> updateCategoryComboBox;  // Use enum type for category
    @FXML private DatePicker updateEventDatePicker;
    @FXML private TextField updateLocationField;
    @FXML private ComboBox<String> updateClientComboBox;

    @FXML private AnchorPane addEventPopup;
    @FXML private AnchorPane updateEventPopup;

    private ObservableList<Event> eventList;

    // Table action column (edit/delete)
    @FXML private TableColumn<Event, Void> actionColumn;

    @FXML
    public void initialize() {
        // Set up the columns for the table
        numberColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(clientTable.getItems().indexOf(cellData.getValue()) + 1).asObject());
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDate()));
        locationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLocation()));
        clientNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(getClientNameById(cellData.getValue().getClientId())));

        // Set up the action column with buttons (edit/delete)
        actionColumn.setCellFactory(param -> new TableCell<Event, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                // Set edit button action
                editButton.setOnAction(event -> {
                    Event eventData = getTableView().getItems().get(getIndex());
                    showUpdateEventPopup(eventData);  // Show the update popup with the event data
                });

                // Set delete button action
                deleteButton.setOnAction(event -> {
                    Event eventData = getTableView().getItems().get(getIndex());
                    deleteEvent(eventData);  // Delete the selected event
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10, editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });

        // Load clients into client dropdown
        loadClientNames();

        // Load events from the database
        loadEvents();

        // Load enum values for the Category ComboBox
        categoryComboBox.setItems(FXCollections.observableArrayList(Category.values()));
        updateCategoryComboBox.setItems(FXCollections.observableArrayList(Category.values()));
    }

    // Method to delete event from database
    private void deleteEvent(Event event) {
        try (Connection connection = DatabaseConnection.getDBConnection()) {
            String query = "DELETE FROM EVENTS WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, event.getId());
            statement.executeUpdate();
            loadEvents();  // Reload events after deletion
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to show update popup and pre-fill the form
    private void showUpdateEventPopup(Event event) {
        updateEventPopup.setVisible(true);
        // Populate fields with event data for editing
        updateNameField.setText(event.getName());
        updateDescriptionField.setText(event.getDescription());
        updateCategoryComboBox.setValue(Category.valueOf(event.getCategory())); // Set enum value
        updateEventDatePicker.setValue(event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        updateLocationField.setText(event.getLocation());
        // Populate the client combo box
        loadClientNamesForUpdate(event.getClientId());
    }

    // Method to load events from the database
    private void loadEvents() {
        eventList = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getDBConnection()) {
            String query = "SELECT * FROM EVENTS";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Event event = new Event();
                event.setId(resultSet.getInt("id"));
                event.setName(resultSet.getString("name"));
                event.setDescription(resultSet.getString("description"));
                event.setCategory(resultSet.getString("category"));
                event.setDate(resultSet.getDate("date"));
                event.setLocation(resultSet.getString("location"));
                event.setClientId(resultSet.getInt("client_id"));
                eventList.add(event);
            }
            clientTable.setItems(eventList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load client names into the combo box
    private void loadClientNames() {
        ObservableList<String> clientNames = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getDBConnection()) {
            String query = "SELECT name FROM clients";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                clientNames.add(resultSet.getString("name"));
            }
            clientComboBox.setItems(clientNames);  // Set client names into the combo box
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load client names for the update form
    private void loadClientNamesForUpdate(int clientId) {
        ObservableList<String> clientNames = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getDBConnection()) {
            String query = "SELECT name FROM clients";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                clientNames.add(resultSet.getString("name"));
            }
            updateClientComboBox.setItems(clientNames);  // Set client names for update combo box
            // Set the current client for the update
            updateClientComboBox.setValue(getClientNameById(clientId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to get client name by ID
    private String getClientNameById(int clientId) {
        try (Connection connection = DatabaseConnection.getDBConnection()) {
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

    // Action method to show Add Event popup
    @FXML
    public void showAddEventPopup(ActionEvent event) {
        addEventPopup.setVisible(true);
    }

    // Action method to save event to database
    @FXML
    public void saveEvent(ActionEvent event) {
        String name = nameField.getText();
        String description = descriptionField.getText();
        Category category = categoryComboBox.getValue();  // Get the selected category (enum)
        LocalDate eventDateLocal = eventDatePicker.getValue();
        java.sql.Date eventDate = java.sql.Date.valueOf(eventDateLocal);
        String location = locationField.getText();
        String clientName = clientComboBox.getValue();

        int clientId = getClientIdByName(clientName);

        try (Connection connection = DatabaseConnection.getDBConnection()) {
            String query = "INSERT INTO EVENTS (name, description, category, date, location, client_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, category.name());  // Store the enum name
            statement.setDate(4, eventDate);
            statement.setString(5, location);
            statement.setInt(6, clientId);
            statement.executeUpdate();
            loadEvents();  // Reload events after saving
            addEventPopup.setVisible(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Action method to cancel Add Event
    @FXML
    public void cancelEvent(ActionEvent event) {
        addEventPopup.setVisible(false);
    }

    // Action method to show Update Event popup
    @FXML
    public void showUpdateEventPopup(ActionEvent event) {
        updateEventPopup.setVisible(true);
    }

    // Action method to update event in database
    @FXML
    public void updateEvent(ActionEvent event) {
        // Implement update logic here
    }

    // Action method to cancel Update Event
    @FXML
    public void cancelUpdate(ActionEvent event) {
        updateEventPopup.setVisible(false);
    }

    // Helper method to get client ID by name
    private int getClientIdByName(String clientName) {
        try (Connection connection = DatabaseConnection.getDBConnection()) {
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

    // Enum for Category
    public enum Category {
        KECIL, SEDANG, BESAR;

        @Override
        public String toString() {
            // Capitalize first letter
            return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
        }
    }
}
