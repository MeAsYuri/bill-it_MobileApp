package com.example.billit_all.Bill_history;

//public class Receipt2 {
//
//
//    public String PaidString;
//    public String date;
//
//    public Receipt2(String paidString, String date, Integer unit_no, Integer receipt_rentFee) {
//        PaidString = paidString;
//        this.date = date;
//        this.unit_no = unit_no;
//        this.receipt_rentFee = receipt_rentFee;
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
//    public Integer getUnit_no() {
//        return unit_no;
//    }
//
//    public void setUnit_no(Integer unit_no) {
//        this.unit_no = unit_no;
//    }
//
//    public Integer getReceipt_rentFee() {
//        return receipt_rentFee;
//    }
//
//    public void setReceipt_rentFee(Integer receipt_rentFee) {
//        this.receipt_rentFee = receipt_rentFee;
//    }
//
//    public Integer unit_no;
//    public Integer receipt_rentFee;
//
//
//}


public class Receipt2 {


    public String PaidString;
    public String date;
    public Integer unit_no;
    public Integer receipt_rentFee;

    public Receipt2(String paidString, String date, Integer unit_no, Integer receipt_rentFee, Integer unit_id, String qrCode, String tenant_id) {
        PaidString = paidString;
        this.date = date;
        this.unit_no = unit_no;
        this.receipt_rentFee = receipt_rentFee;
        this.unit_id = unit_id;
        this.qrCode = qrCode;
        this.tenant_id = tenant_id;
    }

    public Integer unit_id;


    public String qrCode;
    public String tenant_id;

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }

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

    public Integer getUnit_no() {
        return unit_no;
    }

    public void setUnit_no(Integer unit_no) {
        this.unit_no = unit_no;
    }

    public Integer getReceipt_rentFee() {
        return receipt_rentFee;
    }

    public void setReceipt_rentFee(Integer receipt_rentFee) {
        this.receipt_rentFee = receipt_rentFee;
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


}