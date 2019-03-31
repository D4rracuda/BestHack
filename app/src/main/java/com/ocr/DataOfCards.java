package com.ocr;

public class DataOfCards {
    private String number;
    private String type;
    private String type2;
    private String date;
    private String name;

    DataOfCards(String num, String type, String type2, String date, String name){
        this.name = name;
        this.date = date;
        this.number = num;
        this.type = type;
        this.type2 = type2;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getType2() {
        return type2;
    }
}
