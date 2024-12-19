package model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Schedule {
    private IntegerProperty id;
    private StringProperty eventName;
    private StringProperty status;
    private ObjectProperty<LocalDate> decorationDate;
    private StringProperty decorationTime;

    // Constructor for inserting a new Schedule (without ID)
    public Schedule(String eventName, String status, LocalDate decorationDate, String decorationTime) {
        this.eventName = new SimpleStringProperty(eventName);
        this.status = new SimpleStringProperty(status);
        this.decorationDate = new SimpleObjectProperty<>(decorationDate);
        this.decorationTime = new SimpleStringProperty(decorationTime);
    }

    // Constructor for updating or displaying existing Schedule (with ID)
    public Schedule(int id, String eventName, String status, LocalDate decorationDate, String decorationTime) {
        this.id = new SimpleIntegerProperty(id);
        this.eventName = new SimpleStringProperty(eventName);
        this.status = new SimpleStringProperty(status);
        this.decorationDate = new SimpleObjectProperty<>(decorationDate);
        this.decorationTime = new SimpleStringProperty(decorationTime);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getEventName() {
        return eventName.get();
    }

    public StringProperty eventNameProperty() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName.set(eventName);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public LocalDate getDecorationDate() {
        return decorationDate.get();
    }

    public ObjectProperty<LocalDate> decorationDateProperty() {
        return decorationDate;
    }

    public void setDecorationDate(LocalDate decorationDate) {
        this.decorationDate.set(decorationDate);
    }

    public String getDecorationTime() {
        return decorationTime.get();
    }

    public StringProperty decorationTimeProperty() {
        return decorationTime;
    }

    public void setDecorationTime(String decorationTime) {
        this.decorationTime.set(decorationTime);
    }
}
