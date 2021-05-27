package com.example.finalapp;

import java.util.ArrayList;

public class StaticValues {

    private static User user;

    private static String uid="";

    private static ArrayList<User> list = new ArrayList<User>();

    public static User getUser()
    {
        return user;
    }

    public static void setUser(User user) {
        StaticValues.user = user;
    }

    public static String getUid() {
        return uid;
    }

    public static void setUid(String uid) {
        StaticValues.uid = uid;
    }

    public static ArrayList<User> getList() {
        return list;
    }

    public static void setList(ArrayList<User> list) {
        StaticValues.list = list;
    }
}


