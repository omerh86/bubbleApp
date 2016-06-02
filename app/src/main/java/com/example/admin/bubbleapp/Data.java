package com.example.admin.bubbleapp;

/**
 * Created by Admin on 5/11/2016.
 */
public class Data {

    private static Data data;
    public String room;
    public boolean breath;

    private Data() {
        data = this;

    }


    public static Data getinstance() {
        if (data == null) {
            data = new Data();
            return data;
        }
        return data;

    }
}
