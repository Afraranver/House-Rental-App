package com.example.rentalhouse.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class PostModelSerial implements Serializable {
    public String id;
    public ArrayList<String> imagesList;
    public String description;
    public String price;
    public String contact;
    public String name;
    public String location;
    public String availability;

    public PostModelSerial(String id, ArrayList<String> imagesList, String description, String price, String contact,
                           String name, String location, String availability){
        this.id = id;
        this.imagesList = imagesList;
        this.description = description;
        this.price = price;
        this.contact = contact;
        this.name = name;
        this.location = location;
        this.availability = availability;
    }

    public ArrayList<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(ArrayList<String> imagesList) {
        this.imagesList = imagesList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
