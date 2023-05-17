package com.example.billit_all.Bill_history;

public class Bill3 {
    public String rent_id;
    public String rentPaidString;
    public String rent_date;
    public Integer rent_amount;

    public Integer rent_unit_id;
    public Double rent_penalty;

    public String getRent_id() {
        return rent_id;
    }

    public void setRent_id(String rent_id) {
        this.rent_id = rent_id;
    }

    public String getRentPaidString() {
        return rentPaidString;
    }

    public void setRentPaidString(String rentPaidString) {
        this.rentPaidString = rentPaidString;
    }

    public String getRent_date() {
        return rent_date;
    }

    public void setRent_date(String rent_date) {
        this.rent_date = rent_date;
    }

    public Integer getRent_amount() {
        return rent_amount;
    }

    public void setRent_amount(Integer rent_amount) {
        this.rent_amount = rent_amount;
    }

    public Integer getRent_unit_id() {
        return rent_unit_id;
    }

    public void setRent_unit_id(Integer rent_unit_id) {
        this.rent_unit_id = rent_unit_id;
    }

    public Double getRent_penalty() {
        return rent_penalty;
    }

    public void setRent_penalty(Double rent_penalty) {
        this.rent_penalty = rent_penalty;
    }

    public Double getRent_balance() {
        return rent_balance;
    }

    public void setRent_balance(Double rent_balance) {
        this.rent_balance = rent_balance;
    }

    public String getElec_due_date() {
        return elec_due_date;
    }

    public void setElec_due_date(String elec_due_date) {
        this.elec_due_date = elec_due_date;
    }

    public Bill3(String rent_id, String rentPaidString, String rent_date, Integer rent_amount, Integer rent_unit_id, Double rent_penalty, Double rent_balance, String elec_due_date) {
        this.rent_id = rent_id;
        this.rentPaidString = rentPaidString;
        this.rent_date = rent_date;
        this.rent_amount = rent_amount;
        this.rent_unit_id = rent_unit_id;
        this.rent_penalty = rent_penalty;
        this.rent_balance = rent_balance;
        this.elec_due_date = elec_due_date;
    }

    public Double rent_balance;
    public String elec_due_date;

}
