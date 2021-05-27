package com.vik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.NotNull;

import java.util.Objects;

public class Card {

    private long id = 0L;
    @JsonIgnore
    private long cardNumber = 0L;
    private String cardNumberSplit = "";
    @JsonIgnore
    private Account account = new Account();

    public Card() {
    }

    public long getId() {
        return id;
    }

    public void setId(@NotNull long id) {
        this.id = id;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(@NotNull long cardNumber) {
        this.cardNumber = cardNumber;
        String[] temp = Long.toString(cardNumber).split("",16);
        if (temp.length == 16) {
            for (int i = 0; i < 16; i++) {
                cardNumberSplit += temp[i];
                if (i == 3 || i == 7 || i == 11)
                    cardNumberSplit += " ";
            }
        } else {
            cardNumberSplit = Long.toString(cardNumber);
        }
    }

    @JsonProperty("cardNumber")
    public String getCardNumberSplit() {
        return cardNumberSplit;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(@NotNull Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return id == card.id && cardNumber == card.cardNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardNumber, account);
    }
}
