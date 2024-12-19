package controller;

import java.io.IOException;
import util.DatabaseConnection;
import model.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientController implements Initializable {

    @FXML
    private Button goToDashboard;
    
    @FXML
    private Button goToClient;
    
    @FXML
    private Button goToEvent;
    
    @FXML
    private Button goToSchedule;
    
    @FXML
    private Button btnAddClientTop;

    @FXML
    private AnchorPane addClientPopup;

    @FXML
    private Button btnSaveClient;

    @FXML
    private Button btnUpdateClient;

    @FXML
    private Button btnCancelClient;

    @FXML
    private Button btnDeleteClient;

    @FXML
    private TextField nameField;

    @FXML
    private TextField contactField;

    @FXML
    private TextField addressField;

    @FXML
    private TableView<Client> clientTable;

    @FXML
    private TableColumn<Client, Integer> numberColumn;

    @FXML
    private TableColumn<Client, String> nameColumn;

    @FXML
    private TableColumn<Client, String> contactColumn;

    @FXML
    private TableColumn<Client, String> addressColumn;

    private ObservableList<Client> clientData = FXCollections.observableArrayList();
    private Connection connection;
    private Client selectedClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = DatabaseConnection.getDBConnection();
        } catch (SQLException e) {
            showAlert("Koneksi Database Error", e.getMessage());
            return;
        }

        // Konfigurasi kolom tabel
        numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());

        clientTable.setItems(clientData);

        // Tambah event listener untuk seleksi baris
        clientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedClient = newSelection;
                populateClientFields(newSelection);
                btnSaveClient.setVisible(false);
                btnUpdateClient.setVisible(true);
                btnDeleteClient.setVisible(true);
                addClientPopup.setVisible(true);
            }
        });

        setupButtons();
        loadClientsFromDatabase();
    }

    private void setupButtons() {
        // Tombol Tambah Client
        btnAddClientTop.setOnAction(event -> {
            clearFields();
            btnSaveClient.setVisible(true);
            btnUpdateClient.setVisible(false);
            btnDeleteClient.setVisible(false);
            addClientPopup.setVisible(true);
        });

        // Tombol Simpan
        btnSaveClient.setOnAction(event -> saveClient());

        // Tombol Update
        btnUpdateClient.setOnAction(event -> updateClient());

        // Tombol Delete
        btnDeleteClient.setOnAction(event -> deleteClient());

        // Tombol Batal
        btnCancelClient.setOnAction(event -> {
            addClientPopup.setVisible(false);
            clearFields();
        });
    }

    private void saveClient() {
        String nama = nameField.getText().trim();
        String kontak = contactField.getText().trim();
        String alamat = addressField.getText().trim();

        if (nama.isEmpty() || kontak.isEmpty() || alamat.isEmpty()) {
            showAlert("Validasi", "Harap isi semua field!");
            return;
        }

        try {
            String query = "INSERT INTO clients (name, contact, address) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, nama);
            statement.setString(2, kontak);
            statement.setString(3, alamat);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    Client newClient = new Client(nama, kontak, alamat);
                    newClient.setNumber(id);
                    clientData.add(newClient);
                }
                clearFields();
                addClientPopup.setVisible(false);
            }
        } catch (SQLException e) {
            showAlert("Error Menyimpan", e.getMessage());
        }
    }

    private void updateClient() {
        if (selectedClient == null) return;

        String nama = nameField.getText().trim();
        String kontak = contactField.getText().trim();
        String alamat = addressField.getText().trim();

        if (nama.isEmpty() || kontak.isEmpty() || alamat.isEmpty()) {
            showAlert("Validasi", "Harap isi semua field!");
            return;
        }

        try {
            String query = "UPDATE clients SET name=?, contact=?, address=? WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nama);
            statement.setString(2, kontak);
            statement.setString(3, alamat);
            statement.setInt(4, selectedClient.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                // Update data di ObservableList
                int index = clientData.indexOf(selectedClient);
                selectedClient.setName(nama);
                selectedClient.setContact(kontak);
                selectedClient.setAddress(alamat);
                clientData.set(index, selectedClient);

                clearFields();
                addClientPopup.setVisible(false);
            }
        } catch (SQLException e) {
            showAlert("Error Update", e.getMessage());
        }
    }

    private void deleteClient() {
        if (selectedClient == null) return;

        try {
            String query = "DELETE FROM clients WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedClient.getId());

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                clientData.remove(selectedClient);
                clearFields();
                addClientPopup.setVisible(false);
            }
        } catch (SQLException e) {
            showAlert("Error Hapus", e.getMessage());
        }
    }

    private void loadClientsFromDatabase() {
        clientData.clear();
        try {
            String query = "SELECT * FROM clients ORDER BY id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            int rowNum = 1;
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nama = resultSet.getString("name");
                String kontak = resultSet.getString("contact");
                String alamat = resultSet.getString("address");

                Client client = new Client(nama, kontak, alamat);
                client.setId(id);
                client.setNumber(rowNum++);
                clientData.add(client);
            }
        } catch (SQLException e) {
            showAlert("Error Memuat Data", e.getMessage());
        }
    }

    private void populateClientFields(Client client) {
        nameField.setText(client.getName());
        contactField.setText(client.getContact());
        addressField.setText(client.getAddress());
    }

    private void clearFields() {
        nameField.clear();
        contactField.clear();
        addressField.clear();
        selectedClient = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Pastikan untuk menutup koneksi saat controller ditutup
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            showAlert("Error Koneksi", e.getMessage());
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