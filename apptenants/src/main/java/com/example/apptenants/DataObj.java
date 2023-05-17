package com.example.apptenants;

public class DataObj {
    long invoiceNo;
    long date;
    double amount;
    float previousRead;

    public DataObj() {
    }

    //meralco setters and getters
    public float getPreviousRead() {
        return previousRead;
    }

    public void setPreviousRead(float previousRead) {
        this.previousRead = previousRead;
    }

    public float getCurrentRead() {
        return currentRead;
    }

    public void setCurrentRead(float currentRead) {
        this.currentRead = currentRead;
    }

    public float getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(float priceRate) {
        this.priceRate = priceRate;
    }

    float currentRead;
    float priceRate;

    String isPaid;

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(long invoiceNo) {
        this.invoiceNo = invoiceNo;
    }


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }





}

