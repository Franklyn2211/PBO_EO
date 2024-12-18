package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class ScheduleController {

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
}
