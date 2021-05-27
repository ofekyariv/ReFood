package com.example.finalapp;

public class User {

    private String email;
    private String pass;
    private String gender;
    private String phone;
    private String BirthDay;
    private String name;
    private String pic;
    private static String type;

    public User() {
    }

    public User(String email, String pass, String gender, String phone, String birthDay, String name, String pic) {
        this.email = email;
        this.pass = pass;
        this.gender = gender;
        this.phone = phone;
        this.BirthDay = birthDay;
        this.name=name;
        this.pic = pic;
        type="customer";

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthDay() {
        return BirthDay;
    }

    public void setBirthDay(String birthDay) {
        BirthDay = birthDay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
