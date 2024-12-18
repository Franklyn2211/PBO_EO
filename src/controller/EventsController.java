package controller;

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

public class EventsController {

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
            // Koneksi ke database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database_name", "your_username", "your_password");

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
}
