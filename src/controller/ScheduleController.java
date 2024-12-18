package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ScheduleController {
    
    @FXML
    private Button goToDashboard;
    
    @FXML
    private Button goToClient;
    
    @FXML
    private Button goToEvent;
    
    @FXML
    private Button goToSchedule;

    @FXML
    private TableView<?> scheduleTable;

    @FXML
    private AnchorPane addSchedulePopup;

    @FXML
    private void initialize() {
        // Initialize TableView, and any other setup
    }

    @FXML
    private void showAddSchedulePopup() {
        addSchedulePopup.setVisible(true);
    }

    @FXML
    private void hideAddSchedulePopup() {
        addSchedulePopup.setVisible(false);
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
