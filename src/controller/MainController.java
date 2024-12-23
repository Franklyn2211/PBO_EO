package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {

@FXML
private Button goToDashboard;

@FXML
private Button goToClient;

@FXML
private Button goToEvent;

@FXML
private Button goToSchedule;

@FXML
private Label totalClientLabel;

@FXML
private Label totalEventLabel;

    @FXML
    private PieChart eventStatusChart;

    private Connection connection;

    // Initialize method called by FXML Loader
    @FXML
    public void initialize() {
        connection = connectToDatabase();
        loadTotalCounts();
        loadEventStatusChart();
    }

    // Connect to the database
    private Connection connectToDatabase() {
        try {
            return DatabaseConnection.getDBConnection();
        } catch (SQLException e) {
            System.out.println("Database Connection Error: " + e.getMessage());
            return null;
        }
    }

    // Load total counts for User, Clients, and Events
    private void loadTotalCounts() {
        String totalClientsQuery = "SELECT COUNT(*) AS total FROM clients";
        String totalEventsQuery = "SELECT COUNT(*) AS total FROM EVENTS";

        try {
            PreparedStatement statement;
            ResultSet resultSet;

            // Total Clients
            statement = connection.prepareStatement(totalClientsQuery);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalClientLabel.setText(String.valueOf(resultSet.getInt("total")));
            }

            // Total Events
            statement = connection.prepareStatement(totalEventsQuery);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalEventLabel.setText(String.valueOf(resultSet.getInt("total")));
            }

        } catch (SQLException e) {
            System.out.println("Error Loading Totals: " + e.getMessage());
        }
    }

    // Load PieChart for event status
    private void loadEventStatusChart() {
        String eventStatusQuery = "SELECT status, COUNT(*) AS total FROM schedules GROUP BY status";
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try {
            PreparedStatement statement = connection.prepareStatement(eventStatusQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String status = resultSet.getString("status");
                int total = resultSet.getInt("total");
                pieChartData.add(new PieChart.Data(status, total));
            }

            eventStatusChart.setData(pieChartData);

        } catch (SQLException e) {
            System.out.println("Error Loading PieChart: " + e.getMessage());
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
