package com.vik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Account {

    private long id = 0L;
    private BigDecimal balance = new BigDecimal("0.0");
    private String accNumber = "";
    @JsonIgnore
    private Owner owner = new Owner();
    private List<Card> cards = new ArrayList<>();

    public Account() {
    }

    public long getId() {
        return id;
    }

    public void setId(@NotNull long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(@NotNull BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(@NotNull String accNumber) {
        this.accNumber = accNumber;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(@NotNull Owner owner) {
        this.owner = owner;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(@NotNull List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return id == account.id && accNumber == account.accNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, accNumber, owner);
    }
}
