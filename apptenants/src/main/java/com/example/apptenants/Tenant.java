package com.example.apptenants;

public class Tenant {
    public String houseTenant, nameTenant, emailTenant, contactTenant, passwordTenant,rental, penalty;
//    public Integer rental, penalty;
//    public Image image;

    public Tenant(){

    }

    public Tenant(String houseTenant, String nameTenant, String emailTenant, String contactTenant, String passwordTenant, String rental, String penalty){
        this.houseTenant = houseTenant;
        this.nameTenant = nameTenant;
        this.contactTenant = contactTenant;
        this.emailTenant = emailTenant;
        this.passwordTenant = passwordTenant;
        this.rental = rental;
        this.penalty = penalty;
//        this.image = image;
    }
}