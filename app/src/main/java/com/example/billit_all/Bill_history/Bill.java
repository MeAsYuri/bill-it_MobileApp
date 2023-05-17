
package com.example.billit_all.Bill_history;

import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Date;

public class Bill {

    public String elec_id;
    public String elecPaidString;
    public Double elec_amount;

    public Double elec_penalty;
    public String elec_due_date;
    public Integer elec_reading;
    public Integer elec_consumption;

    public String getElec_id() {
        return elec_id;
    }

    public void setElec_id(String elec_id) {
        this.elec_id = elec_id;
    }

    public String getElecPaidString() {
        return elecPaidString;
    }

    public void setElecPaidString(String elecPaidString) {
        this.elecPaidString = elecPaidString;
    }

    public Double getElec_amount() {
        return elec_amount;
    }

    public void setElec_amount(Double elec_amount) {
        this.elec_amount = elec_amount;
    }

    public Double getElec_penalty() {
        return elec_penalty;
    }

    public void setElec_penalty(Double elec_penalty) {
        this.elec_penalty = elec_penalty;
    }

    public String getElec_due_date() {
        return elec_due_date;
    }

    public void setElec_due_date(String elec_due_date) {
        this.elec_due_date = elec_due_date;
    }

    public Integer getElec_reading() {
        return elec_reading;
    }

    public void setElec_reading(Integer elec_reading) {
        this.elec_reading = elec_reading;
    }

    public Integer getElec_consumption() {
        return elec_consumption;
    }

    public void setElec_consumption(Integer elec_consumption) {
        this.elec_consumption = elec_consumption;
    }

    public Integer getElec_total_bill() {
        return elec_total_bill;
    }

    public void setElec_total_bill(Integer elec_total_bill) {
        this.elec_total_bill = elec_total_bill;
    }

    public Integer getElec_total_consumption() {
        return elec_total_consumption;
    }

    public void setElec_total_consumption(Integer elec_total_consumption) {
        this.elec_total_consumption = elec_total_consumption;
    }

    public Double getElec_price_rate() {
        return elec_price_rate;
    }

    public void setElec_price_rate(Double elec_price_rate) {
        this.elec_price_rate = elec_price_rate;
    }

    public String getElec_unit_no() {
        return elec_unit_no;
    }

    public void setElec_unit_no(String elec_unit_no) {
        this.elec_unit_no = elec_unit_no;
    }

    public String getElec_date() {
        return elec_date;
    }

    public void setElec_date(String elec_date) {
        this.elec_date = elec_date;
    }

    public Double getElec_balance() {
        return elec_balance;
    }

    public void setElec_balance(Double elec_balance) {
        this.elec_balance = elec_balance;
    }

    public Integer elec_total_bill;
    public Integer elec_total_consumption;
    public Double elec_price_rate;
    public String elec_unit_no;
    public String elec_date;

    public Bill(String elec_id, String elecPaidString, Double elec_amount, Double elec_penalty, String elec_due_date, Integer elec_reading, Integer elec_consumption, Integer elec_total_bill, Integer elec_total_consumption, Double elec_price_rate, String elec_unit_no, String elec_date, Double elec_balance) {
        this.elec_id = elec_id;
        this.elecPaidString = elecPaidString;
        this.elec_amount = elec_amount;
        this.elec_penalty = elec_penalty;
        this.elec_due_date = elec_due_date;
        this.elec_reading = elec_reading;
        this.elec_consumption = elec_consumption;
        this.elec_total_bill = elec_total_bill;
        this.elec_total_consumption = elec_total_consumption;
        this.elec_price_rate = elec_price_rate;
        this.elec_unit_no = elec_unit_no;
        this.elec_date = elec_date;
        this.elec_balance = elec_balance;
    }

    public Double elec_balance;


}

