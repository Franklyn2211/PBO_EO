package model;

import javafx.beans.property.*;
import java.sql.Date;

public class Event {
    private IntegerProperty number;
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty description;
    private StringProperty category;
    private ObjectProperty<Date> date;
    private StringProperty location;
    private IntegerProperty clientId;
    private IntegerProperty createdBy;
    private IntegerProperty updatedBy;

    public Event() {
        this.number = new SimpleIntegerProperty();
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.category = new SimpleStringProperty("");
        this.date = new SimpleObjectProperty<>();
        this.location = new SimpleStringProperty("");
        this.clientId = new SimpleIntegerProperty();
        this.createdBy = new SimpleIntegerProperty();
        this.updatedBy = new SimpleIntegerProperty();
    }

    // Constructor with parameters
    public Event(String name, String description, String category, Date date, 
                String location, int clientId) {
        this.number = new SimpleIntegerProperty();
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.category = new SimpleStringProperty(category);
        this.date = new SimpleObjectProperty<>(date);
        this.location = new SimpleStringProperty(location);
        this.clientId = new SimpleIntegerProperty(clientId);
        this.createdBy = new SimpleIntegerProperty();
        this.updatedBy = new SimpleIntegerProperty();
    }

    // Property getters
    public IntegerProperty numberProperty() {
        return number;
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public ObjectProperty<Date> dateProperty() {
        return date;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public IntegerProperty clientIdProperty() {
        return clientId;
    }

    public IntegerProperty createdByProperty() {
        return createdBy;
    }

    public IntegerProperty updatedByProperty() {
        return updatedBy;
    }

    // Regular getters and setters
    public int getNumber() {
        return number.get();
    }

    public void setNumber(int number) {
        this.number.set(number);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public Date getDate() {
        return date.get();
    }

    public void setDate(Date date) {
        this.date.set(date);
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public int getClientId() {
        return clientId.get();
    }

    public void setClientId(int clientId) {
        this.clientId.set(clientId);
    }

    public int getCreatedBy() {
        return createdBy.get();
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy.set(createdBy);
    }

    public int getUpdatedBy() {
        return updatedBy.get();
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy.set(updatedBy);
    }
}