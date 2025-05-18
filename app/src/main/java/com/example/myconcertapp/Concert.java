package com.example.myconcertapp;

public class Concert {
    private String id;      // 🔧 Ez kell a Firestore dokumentum azonosítóhoz
    private String name;
    private String date;
    private String info;
    private String price;
    private String createdBy;

    public Concert() {
        // Szükséges a Firestore számára
    }

    public Concert(String name, String date, String info, String price) {
        this.name = name;
        this.date = date;
        this.info = info;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getInfo() {
        return info;
    }

    public String getPrice() {
        return price;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
