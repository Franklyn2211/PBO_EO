package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    // Regex untuk validasi password: Minimal 6 karakter, mengandung huruf dan angka
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";

    public void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validasi Input Kosong
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Please fill all fields.");
            return;
        }

        // Validasi Email harus mengandung "@gmail.com"
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            showAlert(AlertType.ERROR, "Email Error", "Email must contain '@gmail.com'.");
            return;
        }

        // Validasi Password harus memenuhi kriteria
        if (!Pattern.matches(PASSWORD_REGEX, password)) {
            showAlert(AlertType.ERROR, "Password Error",
                    "Password must be at least 6 characters long and contain both letters and numbers.");
            return;
        }

        // Cek apakah username sudah ada
        if (isUsernameExists(username)) {
            showAlert(AlertType.ERROR, "Username Error", "Username is already taken. Please choose another one.");
            return;
        }

        // Jika semua validasi lolos, simpan data ke database
        try (Connection connection = DatabaseConnection.getDBConnection()) {
            String sql = "INSERT INTO user (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password); // Anda bisa mengganti ini dengan hashing password

            stmt.executeUpdate();

            // Tampilkan notifikasi sukses
            showTemporaryNotification("Registration Successful!", 1.5, () -> goToLogin());

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to register user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Cek apakah username sudah ada di database
    private boolean isUsernameExists(String username) {
        try (Connection connection = DatabaseConnection.getDBConnection()) {
            String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to check username: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Scene scene = new Scene(loader.load());

            // Mendapatkan stage saat ini dan mengganti scene ke halaman Login
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Event Organizer - Login");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Navigation Error", "Failed to load Login page.");
            e.printStackTrace();
        }
    }

    // Metode untuk menampilkan alert biasa
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Metode untuk menampilkan notifikasi sementara dengan durasi tertentu
    private void showTemporaryNotification(String message, double durationInSeconds, Runnable onComplete) {
        Stage notificationStage = new Stage();
        notificationStage.initOwner(usernameField.getScene().getWindow());

        // Tampilan notifikasi modern
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: #4CAF50; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10; "
                     + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 10, 0.5, 0, 4);");

        Label label = new Label(message);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
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
