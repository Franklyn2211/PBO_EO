/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.*;

/**
 *
 * @author Franklyn
 */
public class DatabaseConnection {
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/event_organizer_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
   public static Connection getDBConnection() throws SQLException {
    Connection connection = null;

    try {
        Class.forName(DB_DRIVER);
        System.out.println("Driver ditemukan");
    } catch (ClassNotFoundException exception) {
        System.out.println("Driver tidak ditemukan " + exception.getMessage());
        return null;
    }

    try {
        connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        System.out.println("Koneksi Berhasil");
    } catch (SQLException exception) {
        System.out.println("Koneksi Gagal: " + exception.getMessage());
    }

    return connection;
}

    public static Connection getConnection() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
