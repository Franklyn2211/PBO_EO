package model;

import javafx.beans.property.*;

public class Client {
    private IntegerProperty number;
    private StringProperty name;
    private StringProperty contact;
    private StringProperty address;

    public Client(String name, String contact, String address) {
        this.number = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty(name);
        this.contact = new SimpleStringProperty(contact);
        this.address = new SimpleStringProperty(address);
    }

    // Getter untuk Property
    public IntegerProperty numberProperty() {
        return number;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty contactProperty() {
        return contact;
    }

    public StringProperty addressProperty() {
        return address;
    }

    // Getter dan Setter biasa
    public int getNumber() {
        return number.get();
    }

    public void setNumber(int number) {
        this.number.set(number);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getContact() {
        return contact.get();
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }
}