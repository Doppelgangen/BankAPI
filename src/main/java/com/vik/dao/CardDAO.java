package com.vik.dao;

import com.vik.models.Account;
import com.vik.models.Card;
import com.vik.models.Owner;

import java.util.List;

public interface CardDAO {
     boolean persistCard(Card card);
     void getCardsOnAccount(Account account);
     Card getCardById(Long id);
     Card getCardByNumber(Long cardNumber, boolean isSaving);
     void updateCardNumber(Card card);
     void updateCardAccount(Card card);
     List<Card> getAllCardsOfOwner(Owner owner);
     public Card publishNewCard(Account account);
     boolean isCardInDB(Card card);
}
