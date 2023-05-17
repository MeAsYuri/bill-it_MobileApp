package com.example.billit_all;

import androidx.annotation.Nullable;

public class Tenant2 {


    public Tenant2() {

    }

    @Nullable
    public String passwordTenant;
    public String date;
    public Double water_amount;
    public String water_paid;
    public Double elec_amount;
    public String elec_paid;

    public Tenant2(String date, Double water_amount, String water_paid, Double elec_amount, String elec_paid, Double rent_amount, String rent_paid) {
        this.date = date;
        this.water_amount = water_amount;
        this.water_paid = water_paid;
        this.elec_amount = elec_amount;
        this.elec_paid = elec_paid;
        this.rent_amount = rent_amount;
        this.rent_paid = rent_paid;
    }

    public Double rent_amount;
    public String rent_paid;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getWater_amount() {
        return water_amount;
    }

    public void setWater_amount(Double water_amount) {
        this.water_amount = water_amount;
    }

    public String getWater_paid() {
        return water_paid;
    }

    public void setWater_paid(String water_paid) {
        this.water_paid = water_paid;
    }

    public Double getElec_amount() {
        return elec_amount;
    }

    public void setElec_amount(Double elec_amount) {
        this.elec_amount = elec_amount;
    }

    public String getElec_paid() {
        return elec_paid;
    }

    public void setElec_paid(String elec_paid) {
        this.elec_paid = elec_paid;
    }

    public Double getRent_amount() {
        return rent_amount;
    }

    public void setRent_amount(Double rent_amount) {
        this.rent_amount = rent_amount;
    }

    public String getRent_paid() {
        return rent_paid;
    }

    public void setRent_paid(String rent_paid) {
        this.rent_paid = rent_paid;
    }



}