package com.example.billit_all.Bill_history;

import androidx.annotation.Nullable;

import java.util.Date;

public class Tenant {


    public Tenant() {

    }
    @Nullable
    public String elec_due_date, water_due_date;
    public Integer water_amount;
    public String water_paid;
    public Integer elec_amount;
    public String elec_paid;
    public String rent_paid;
    public Integer water_consump;
    public Integer elec_consump;

    public Tenant(@Nullable String elec_due_date, @Nullable String water_due_date, Integer water_amount, String water_paid, Integer elec_amount, String elec_paid, String rent_paid, Integer water_consump, Integer elec_consump, Integer water_unitConsump, Integer elec_unitConsump, Double water_unitAmount, Double elec_unitAmount) {
        this.elec_due_date = elec_due_date;
        this.water_due_date = water_due_date;
        this.water_amount = water_amount;
        this.water_paid = water_paid;
        this.elec_amount = elec_amount;
        this.elec_paid = elec_paid;
        this.rent_paid = rent_paid;
        this.water_consump = water_consump;
        this.elec_consump = elec_consump;
        this.water_unitConsump = water_unitConsump;
        this.elec_unitConsump = elec_unitConsump;
        this.water_unitAmount = water_unitAmount;
        this.elec_unitAmount = elec_unitAmount;
    }

    @Nullable
    public String getElec_due_date() {
        return elec_due_date;
    }

    public void setElec_due_date(@Nullable String elec_due_date) {
        this.elec_due_date = elec_due_date;
    }

    @Nullable
    public String getWater_due_date() {
        return water_due_date;
    }

    public void setWater_due_date(@Nullable String water_due_date) {
        this.water_due_date = water_due_date;
    }

    public Integer getWater_amount() {
        return water_amount;
    }

    public void setWater_amount(Integer water_amount) {
        this.water_amount = water_amount;
    }

    public String getWater_paid() {
        return water_paid;
    }

    public void setWater_paid(String water_paid) {
        this.water_paid = water_paid;
    }

    public Integer getElec_amount() {
        return elec_amount;
    }

    public void setElec_amount(Integer elec_amount) {
        this.elec_amount = elec_amount;
    }

    public String getElec_paid() {
        return elec_paid;
    }

    public void setElec_paid(String elec_paid) {
        this.elec_paid = elec_paid;
    }

    public String getRent_paid() {
        return rent_paid;
    }

    public void setRent_paid(String rent_paid) {
        this.rent_paid = rent_paid;
    }

    public Integer getWater_consump() {
        return water_consump;
    }

    public void setWater_consump(Integer water_consump) {
        this.water_consump = water_consump;
    }

    public Integer getElec_consump() {
        return elec_consump;
    }

    public void setElec_consump(Integer elec_consump) {
        this.elec_consump = elec_consump;
    }

    public Integer getWater_unitConsump() {
        return water_unitConsump;
    }

    public void setWater_unitConsump(Integer water_unitConsump) {
        this.water_unitConsump = water_unitConsump;
    }

    public Integer getElec_unitConsump() {
        return elec_unitConsump;
    }

    public void setElec_unitConsump(Integer elec_unitConsump) {
        this.elec_unitConsump = elec_unitConsump;
    }

    public Double getWater_unitAmount() {
        return water_unitAmount;
    }

    public void setWater_unitAmount(Double water_unitAmount) {
        this.water_unitAmount = water_unitAmount;
    }

    public Double getElec_unitAmount() {
        return elec_unitAmount;
    }

    public void setElec_unitAmount(Double elec_unitAmount) {
        this.elec_unitAmount = elec_unitAmount;
    }

    public Integer water_unitConsump;
    public Integer elec_unitConsump;
    public Double water_unitAmount;
    public Double elec_unitAmount;








}