package com.doplery;

import java.io.Serializable;
import java.util.ArrayList;

public class Advert implements Serializable {
    private String title;
    private int price;
    private String description;
    private String userId;
    private long timeCreated;
    private ArrayList<String> files;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }

    public int getPrice() {
        return price;

    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }


    public Advert() { }

    public Advert(String title, int price, String description, String userId, long timeCreated) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.userId=userId;
        this.timeCreated = timeCreated;
    }
}
