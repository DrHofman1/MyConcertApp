package com.example.myconcertapp;

public class Concert {
    private String name;
    private String date;
    private String info;
    private String price;

    public Concert(String name, String date, String info, String price) {
        this.name = name;
        this.date = date;
        this.info = info;
        this.price = price;
    }

    public String getName() { return name; }
    public String getDate() { return date; }
    public String getInfo() { return info; }
    public String getPrice() { return price; }
}

