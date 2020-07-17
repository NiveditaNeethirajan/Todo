package com.example.todoapplication.model;

public class TodoData {

    private String item;
    private int id;

    public TodoData(String item, int id){
        this.item = item;
        this.id = id;
    }

    public TodoData(){

    }
    public String getItem() {
        return item;
    }

    public int getId() {
        return id;
    }
}
