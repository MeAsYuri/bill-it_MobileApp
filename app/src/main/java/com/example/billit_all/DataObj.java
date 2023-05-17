package com.example.billit_all;

public class DataObj {
    long invoiceNo;
    long date;
    double previousRead;
    double currentRead;
    double priceRate;
    double amount;
    String isPaid;

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }






    //meralco setters and getters

    public DataObj() {
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

    public double getPreviousRead() {
        return previousRead;
    }

    public void setPreviousRead(double previousRead) {
        this.previousRead = previousRead;
    }

    public double getCurrentRead() {
        return currentRead;
    }

    public void setCurrentRead(double currentRead) {
        this.currentRead = currentRead;
    }

    public double getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(double priceRate) {
        this.priceRate = priceRate;
    }




}

