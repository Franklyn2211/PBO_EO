package model;

import java.sql.Date;

public class Event {
    private int id;
    private String name;
    private String description;
    private String category;
    private Date date;
    private String location;
    private String clientName;

    // Constructor
    public Event(int id, String name, String description, String category, Date date, String location, String clientName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.date = date;
        this.location = location;
        this.clientName = clientName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
