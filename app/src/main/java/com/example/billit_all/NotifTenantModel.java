package com.example.billit_all;

public class NotifTenantModel {

    public String id, type, typeOfId, date, isSeen;
//    public Integer rental, penalty;
//    public Image image;


    public NotifTenantModel(String id, String type, String typeOfId, String date, String isSeen){

        this.id = id;
        this.type = type;
        this.typeOfId = typeOfId;
        this.date = date;
        this.isSeen = isSeen;

//        this.image = image;
    }


    public String getId() {return id;}

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {return type;}

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeOfId() {
        return typeOfId;
    }

    public void setTypeOfId(String TypeOfId) {
        this.typeOfId = typeOfId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setisSeen(String isSeen) {
        this.isSeen = isSeen;
    }
}