//package com.example.billit_all.Bill_history;
//
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//
//import java.util.Date;
//
//public class Receipt {
//
//
//
//    public String PaidString;
//    public String date;
//    public Integer total_consumption;
//
//    public Receipt(String paidString, String date, Integer total_consumption, Integer total_bill, Double price_rate, Integer unit_no, Integer consumption, Double amount, Integer water_reading, Integer elec_reading) {
//        PaidString = paidString;
//        this.date = date;
//        this.total_consumption = total_consumption;
//        this.total_bill = total_bill;
//        this.price_rate = price_rate;
//        this.unit_no = unit_no;
//        this.consumption = consumption;
//        this.amount = amount;
//        this.water_reading = water_reading;
//        this.elec_reading = elec_reading;
//    }
//
//    public String getPaidString() {
//        return PaidString;
//    }
//
//    public void setPaidString(String paidString) {
//        PaidString = paidString;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public Integer getTotal_consumption() {
//        return total_consumption;
//    }
//
//    public void setTotal_consumption(Integer total_consumption) {
//        this.total_consumption = total_consumption;
//    }
//
//    public Integer getTotal_bill() {
//        return total_bill;
//    }
//
//    public void setTotal_bill(Integer total_bill) {
//        this.total_bill = total_bill;
//    }
//
//    public Double getPrice_rate() {
//        return price_rate;
//    }
//
//    public void setPrice_rate(Double price_rate) {
//        this.price_rate = price_rate;
//    }
//
//    public Integer getUnit_no() {
//        return unit_no;
//    }
//
//    public void setUnit_no(Integer unit_no) {
//        this.unit_no = unit_no;
//    }
//
//    public Integer getConsumption() {
//        return consumption;
//    }
//
//    public void setConsumption(Integer consumption) {
//        this.consumption = consumption;
//    }
//
//    public Double getAmount() {
//        return amount;
//    }
//
//    public void setAmount(Double amount) {
//        this.amount = amount;
//    }
//
//    public Integer getWater_reading() {
//        return water_reading;
//    }
//
//    public void setWater_reading(Integer water_reading) {
//        this.water_reading = water_reading;
//    }
//
//    public Integer getElec_reading() {
//        return elec_reading;
//    }
//
//    public void setElec_reading(Integer elec_reading) {
//        this.elec_reading = elec_reading;
//    }
//
//    public Integer total_bill;
//    public Double price_rate;
//    public Integer unit_no;
//    public Integer consumption;
//    public Double amount;
//    public Integer water_reading;
//    public Integer elec_reading;
//
//
//
//
//
//}

package com.example.billit_all.Bill_history;

import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Date;

public class Receipt {



    public String PaidString;
    public String date;

    public String getPaidString() {
        return PaidString;
    }

    public void setPaidString(String paidString) {
        PaidString = paidString;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTotal_consumption() {
        return total_consumption;
    }

    public void setTotal_consumption(Integer total_consumption) {
        this.total_consumption = total_consumption;
    }

    public Integer getTotal_bill() {
        return total_bill;
    }

    public void setTotal_bill(Integer total_bill) {
        this.total_bill = total_bill;
    }

    public Double getPrice_rate() {
        return price_rate;
    }

    public void setPrice_rate(Double price_rate) {
        this.price_rate = price_rate;
    }

    public Integer getUnit_no() {
        return unit_no;
    }

    public void setUnit_no(Integer unit_no) {
        this.unit_no = unit_no;
    }

    public Integer getConsumption() {
        return consumption;
    }

    public void setConsumption(Integer consumption) {
        this.consumption = consumption;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getWater_reading() {
        return water_reading;
    }

    public void setWater_reading(Integer water_reading) {
        this.water_reading = water_reading;
    }

    public Integer getElec_reading() {
        return elec_reading;
    }

    public void setElec_reading(Integer elec_reading) {
        this.elec_reading = elec_reading;
    }

    public Integer getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(Integer unit_id) {
        this.unit_id = unit_id;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public Integer total_consumption;

    public Integer total_bill;
    public Double price_rate;

    public Integer unit_no;
    public Integer consumption;
    public Double amount;
    public Integer water_reading;
    public Integer elec_reading;
    public Integer unit_id;
    public String qrCode;


    public Receipt(String paidString, String date, Integer total_consumption, Integer total_bill, Double price_rate, Integer unit_no, Integer consumption, Double amount, Integer water_reading, Integer elec_reading, Integer unit_id, String qrCode, String due_date, String tenant_id) {
        PaidString = paidString;
        this.date = date;
        this.total_consumption = total_consumption;
        this.total_bill = total_bill;
        this.price_rate = price_rate;
        this.unit_no = unit_no;
        this.consumption = consumption;
        this.amount = amount;
        this.water_reading = water_reading;
        this.elec_reading = elec_reading;
        this.unit_id = unit_id;
        this.qrCode = qrCode;
        this.due_date = due_date;
        this.tenant_id = tenant_id;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String due_date;


    public String tenant_id;
}

