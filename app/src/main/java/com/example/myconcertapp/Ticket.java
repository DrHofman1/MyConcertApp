package com.example.myconcertapp;

public class Ticket {
    private String id;
    private String concertName;
    private String concertDate;
    private String concertPrice;

    public Ticket() {}

    public Ticket(String id, String concertName, String concertDate, String concertPrice) {
        this.id = id;
        this.concertName = concertName;
        this.concertDate = concertDate;
        this.concertPrice = concertPrice;
    }

    public String getId() { return id; }
    public String getConcertName() { return concertName; }
    public String getConcertDate() { return concertDate; }
    public String getConcertPrice() { return concertPrice; }

    public void setId(String id) { this.id = id; }
}

