/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validasi input kosong
        if (username.isEmpty() || password.isEmpty()) {
            showTemporaryNotification("Please fill all fields!", "error", 1.5, null);
            return;
        }

        try (Connection connection = DatabaseConnection.getDBConnection()) {
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // Tambahkan hashing password untuk keamanan di production
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Jika login berhasil, tampilkan notifikasi sukses dan pindah ke halaman utama
                showTemporaryNotification("Login Successful!", "success", 1.5, this::goToMainView);
            } else {
                // Jika login gagal, tampilkan notifikasi kesalahan
                showTemporaryNotification("Invalid username or password!", "error", 1.5, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Scene scene = new Scene(loader.load());

            // Dapatkan stage saat ini dan ganti scene ke halaman utama
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Event Organizer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Register.fxml"));
            Scene scene = new Scene(loader.load());

            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Event Organizer - Register");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metode untuk menampilkan notifikasi sementara dengan durasi tertentu
    private void showTemporaryNotification(String message, String type, double durationInSeconds, Runnable onComplete) {
        Stage notificationStage = new Stage();
        notificationStage.initOwner(usernameField.getScene().getWindow());

        // Tampilan notifikasi modern
        StackPane pane = new StackPane();
        String backgroundColor = type.equals("success") ? "#4CAF50" : "#F44336"; // Hijau untuk sukses, merah untuk error
        pane.setStyle("-fx-background-color: " + backgroundColor + "; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label label = new Label(message);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        pane.getChildren().add(label);

        Scene scene = new Scene(pane, 300, 100);
        notificationStage.setScene(scene);
        notificationStage.setAlwaysOnTop(true);

        notificationStage.show();

        // Timeline untuk menutup notifikasi setelah waktu tertentu
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(durationInSeconds), event -> {
            notificationStage.close();
            if (onComplete != null) {
                onComplete.run();
            }
        }));

        timeline.setCycleCount(1);
        timeline.play();
    }
}
