package com.example.billit_all;

public class PaymentInformation {

    private Long CR, PR, PRate, Total, Amount;

    public PaymentInformation() {
    }

    public PaymentInformation(Long CR, Long PR, Long PRate, Long total, Long amount) {
        this.CR = CR;
        this.PR = PR;
        this.PRate = PRate;
        Total = total;
        Amount = amount;
    }

    public Long getCR() {
        return CR;
    }

    public void setCR(Long CR) {
        this.CR = CR;
    }

    public Long getPR() {
        return PR;
    }

    public void setPR(Long PR) {
        this.PR = PR;
    }

    public Long getPRate() {
        return PRate;
    }

    public void setPRate(Long PRate) {
        this.PRate = PRate;
    }

    public Long getTotal() {
        return Total;
    }

    public void setTotal(Long total) {
        Total = total;
    }

    public Long getAmount() {
        return Amount;
    }

    public void setAmount(Long amount) {
        Amount = amount;
    }
}

