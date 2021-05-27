package com.vik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Income {
    private long id = 0L;
    private String accountNumber = "";
    private BigDecimal currentBalance = new BigDecimal("0.00");
    @JsonIgnore
    private BigDecimal income = new BigDecimal("0.00");

    public Income() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    @JsonIgnore
    public BigDecimal getIncome() {
        return income;
    }

    @JsonProperty
    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
