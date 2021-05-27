package com.example.finalapp;

public class Discarabution {

    private String end;
    private String start;
    private String place;
    private String date;
    private String description;

    public Discarabution(String end, String start, String place, String date, String description) {
        this.end = end;
        this.start = start;
        this.place = place;
        this.date = date;
        this.description = description;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Discarabution(){}
}
