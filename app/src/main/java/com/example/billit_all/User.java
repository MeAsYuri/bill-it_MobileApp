package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;

public class User {
    public String name, email, phone, password;
//    public Image image;

    public User(){

    }

    public User(String name, String email, String phone, String password){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
//        this.image = image;
    }
}