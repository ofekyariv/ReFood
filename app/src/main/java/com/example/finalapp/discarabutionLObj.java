package com.example.finalapp;

import java.util.ArrayList;

public class discarabutionLObj  {

    private String id;
    private String date;
    private String place;

    public discarabutionLObj(String id, String date, String place)
    {
        this.id = id;
        this.date = date;
        this.place = place;
    }

    public discarabutionLObj(DistributionListActivity discrabutionListActivity, ArrayList<discarabutionLObj> lFood) {
    }

    public  discarabutionLObj()
    {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

}
