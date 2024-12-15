package model;

import javafx.beans.property.SimpleStringProperty;

public class Event {
    private final SimpleStringProperty date;
    private final SimpleStringProperty location;
    private final SimpleStringProperty status;

    public Event(String name, String type, String date, String location, String status) {
        this.date = new SimpleStringProperty(date);
        this.location = new SimpleStringProperty(location);
        this.status = new SimpleStringProperty(status);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public String getLocation() {
        return location.get();
    }

    public SimpleStringProperty locationProperty() {
        return location;
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }
}
