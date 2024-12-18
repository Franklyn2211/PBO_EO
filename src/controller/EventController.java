package controller;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Event;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EventController {

    @FXML
    private Button goToClient;
    @FXML
    private Button goToDashboard;
    @FXML
    private Button goToEvent;
    @FXML
    private Button goToSchedule;
    @FXML
    private TableView<Event> clientTable;
    @FXML
    private TableColumn<Event, String> nameColumn;
    @FXML
    private TableColumn<Event, String> descriptionColumn;
    @FXML
    private TableColumn<Event, String> categoryColumn;
    @FXML
    private TableColumn<Event, String> dateColumn;
    @FXML
    private TableColumn<Event, String> locationColumn;
    @FXML
    private TableColumn<Event, String> clientNameColumn;

    private ObservableList<Event> eventList = FXCollections.observableArrayList();
    private Connection connection;

    @FXML
    public void initialize() {
        
        // Hubungkan kolom tabel dengan atribut model Event
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));

        // Muat data dari database
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try {
            // Query untuk mendapatkan data event beserta nama client
            String query = """
                SELECT e.id, e.name, e.description, e.category, e.date, e.location, c.name AS client_name
                FROM EVENTS e
                JOIN clients c ON e.client_id = c.id;
            """;

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Tambahkan data ke ObservableList
            while (resultSet.next()) {
                Event event = new Event(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("category"),
                        resultSet.getDate("date"),
                        resultSet.getString("location"),
                        resultSet.getString("client_name")
                );
                eventList.add(event);
            }

            // Tampilkan data di TableView
            clientTable.setItems(eventList);

            // Tutup koneksi
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void goToClient() {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Client.fxml"));
            Parent clientView = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) goToClient.getScene().getWindow();
            
            // Create a new scene with the client view
            Scene scene = new Scene(clientView);
            
            // Set the new scene
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
            Parent clientView = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) goToDashboard.getScene().getWindow();
            
            // Create a new scene with the client view
            Scene scene = new Scene(clientView);
            
            // Set the new scene
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
            Parent clientView = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) goToEvent.getScene().getWindow();
            
            // Create a new scene with the client view
            Scene scene = new Scene(clientView);
            
            // Set the new scene
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
            Parent clientView = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) goToSchedule.getScene().getWindow();
            
            // Create a new scene with the client view
            Scene scene = new Scene(clientView);
            
            // Set the new scene
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading Schedule view: " + e.getMessage());
            e.printStackTrace();
        }   
    }
}
