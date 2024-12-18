/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import model.Client; // Import class Client

/**
 * FXML Controller class
 *
 * @author ASUS
 */
public class ClientController implements Initializable {

    @FXML
    private Button btnAddClientTop; // Tombol Tambah Client di atas tabel

    @FXML
    private AnchorPane addClientPopup; // Popup Form Tambah Client

    @FXML
    private Button btnSaveClient; // Tombol Simpan pada Popup Form

    @FXML
    private Button btnCancelClient; // Tombol Batal pada Popup Form

    @FXML
    private TextField nameField; // Input Nama Client

    @FXML
    private TextField contactField; // Input Kontak Client

    @FXML
    private TextField addressField; // Input Alamat Client

    @FXML
    private TableView<Client> clientTable; // Tabel akan menyimpan data Client

    @FXML
    private TableColumn<Client, String> nameColumn; // Kolom Nama

    @FXML
    private TableColumn<Client, String> contactColumn; // Kolom Kontak

    @FXML
    private TableColumn<Client, String> addressColumn; // Kolom Alamat

    // Daftar data untuk ditampilkan dalam tabel
    private ObservableList<Client> clientData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Set nilai untuk kolom di tabel
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Hubungkan data ke tabel
        clientTable.setItems(clientData);

        // Tombol Tambah Client (menampilkan form popup)
        btnAddClientTop.setOnAction(event -> {
            addClientPopup.setVisible(true);
        });

        // Tombol Batal: Tutup form popup
        btnCancelClient.setOnAction(event -> {
            addClientPopup.setVisible(false);
        });

        // Tombol Simpan: Simpan data ke dalam tabel dan tutup form
        btnSaveClient.setOnAction(event -> {
            // Ambil data dari field input
            String nama = nameField.getText();
            String kontak = contactField.getText();
            String alamat = addressField.getText();

            // Periksa apakah ada data yang kosong
            if (!nama.isEmpty() && !kontak.isEmpty() && !alamat.isEmpty()) {
                // Tambahkan data client ke ObservableList
                Client newClient = new Client(nama, kontak, alamat);
                clientData.add(newClient);

                // Tampilkan log di console (opsional)
                System.out.println("Data Client Ditambahkan:");
                System.out.println("Nama: " + nama);
                System.out.println("Kontak: " + kontak);
                System.out.println("Alamat: " + alamat);

                // Reset field
                nameField.clear();
                contactField.clear();
                addressField.clear();

                // Tutup form popup
                addClientPopup.setVisible(false);
            } else {
                System.out.println("Harap isi semua field sebelum menyimpan!");
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}



