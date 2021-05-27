package com.example.finalapp;

public class Food {

    private String foodName;
    private String Description;
    private String num;
    private String expiration_date;
    private String Foodpic;
    private String id;
    private String address;

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getaddress() {
        return address;
    }

    public void setaddress(String address ) {
        this.address = address;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date) {
        this.expiration_date = expiration_date;
    }

    public String getFoodpic() {
        return Foodpic;
    }

    public void setFoodpic(String foodpic) {
        Foodpic = foodpic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Food(String foodName, String description, String num, String expiration_date, String foodpic, String id, String address) {
        this.foodName = foodName;
        Description = description;
        this.num = num;
        this.expiration_date = expiration_date;
        Foodpic = foodpic;
        this.id = id;
        this.address=address;
    }

    public Food() {
    }



}
