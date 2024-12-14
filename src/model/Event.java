package model;

import java.time.LocalDate;
import java.lang.*;
public class Event {

    private String name;
    private String description;
    private String category;
    private LocalDate date;
    private String location; // Tambahkan lokasi

    public Event(String name, String description, String category, LocalDate date, String location) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.date = date;
        this.location = location;
    }

    public Event(String name, String type, String date, String location) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Event{name='" + name + "', description='" + description + "', category='" + category + "', date=" + date + ", location='" + location + "'}";
    }
}
