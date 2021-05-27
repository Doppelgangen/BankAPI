package com.vik.dao;

import com.vik.models.Account;
import com.vik.models.Owner;

import java.math.BigDecimal;

public interface AccountDAO {
    boolean persistAccount(Account account);
    void getAccountsOnOwner(Owner owner);
    Account getAccountById(Long id);
    Account getAccountByAccNumber(String accNumber, boolean isSaving);
    void updateAccount(Account account);
    void addToBalance(String accNumber, BigDecimal amount);
    Account createNewAccountByOwner(Owner owner);
    boolean isAccountInDB(Account account);
}
