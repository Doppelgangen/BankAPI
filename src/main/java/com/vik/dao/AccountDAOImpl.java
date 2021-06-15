package com.vik.dao;

import com.vik.common.Logger;
import com.vik.common.LoggerImpl;
import com.vik.models.Account;
import com.vik.models.Owner;
import com.vik.service.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of operations with an entity Account
 */
public class AccountDAOImpl implements AccountDAO {
    private Logger logger = new LoggerImpl();

    /**
     * Persists an account to DB,
     * a number of account should be unique,
     * If the account is not filled or account's number is not unique returns false
     *
     * @param account account to save
     */
    @Override
    public boolean persistAccount(Account account) {
        if (account == null || account.getOwner().getId() == 0 || account.getAccNumber().equals("")) {
            logger.write("Fill account to save it");
            return false;
        }

        boolean isSaving = true;
        Account test = getAccountByAccNumber(account.getAccNumber(), isSaving);
        if (test.getId() != 0) {
            logger.write("Account number is not unique");
            return false;
        }
        if (new OwnerDAOImpl().isOwnerInDB(account.getOwner())) {
            try (DBConnection dbc = new DBConnection()) {
                Connection connection = dbc.getConnection();
                PreparedStatement ps = connection.prepareStatement("INSERT INTO accounts (balance, acc_number, owner_id)" +
                        "VALUES ( ?, ?, ? )");
                ps.setBigDecimal(1, account.getBalance());
                ps.setString(2, account.getAccNumber());
                ps.setLong(3, account.getOwner().getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                logger.write("Error persist account");
            }
        } else return false;
        return true;
    }

    /**
     * Set accounts for provided owner to its Set owner.accounts,
     * if an owner == null || owner.id == 0 do nothing
     *
     * @param owner of accounts
     */
    @Override
    public void getAccountsOnOwner(Owner owner) {
        if (owner == null || owner.getId() == 0) {
            logger.write("Fill owner to get accounts");
            return;
        }

        List<Account> accountsSet = new ArrayList<>();
        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE owner_id = ?");
            ps.setLong(1, owner.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Account account = new Account();
                account.setId(rs.getLong("id"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setAccNumber(rs.getString("acc_number"));

                new CardDAOImpl().getCardsOnAccount(account);
                fillAccount(account);
                accountsSet.add(account);
            }
            if (accountsSet.isEmpty()) {
                logger.write("No accounts found");
                return;
            }
            owner.setAccounts(accountsSet);
        } catch (SQLException e) {
            logger.write("Error getting accounts by owner");
        }
    }

    /**
     * Returns an account by a provided id,
     * If an id == null || id == 0 returns blank account.
     *
     * @param id of needed account
     * @return account
     */
    @Override
    public Account getAccountById(Long id) {
        Account account = new Account();
        if (id == null || id == 0) {
            logger.write("Fill id to get account");
            return account;
        }

        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT acc_number, balance, o.id, o.name\n" +
                    "FROM accounts\n" +
                    "INNER JOIN owners o on o.id = accounts.owner_id\n" +
                    "WHERE accounts.id = ?;");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                logger.write("No accounts with such id");
                return account;
            }
            account.getOwner().setId(rs.getLong("owners.id"));
            account.getOwner().setName(rs.getString("owners.name"));

            account.setId(id);
            account.setBalance(rs.getBigDecimal("balance"));
            account.setAccNumber(rs.getString("acc_number"));
            new CardDAOImpl().getCardsOnAccount(account);

        } catch (SQLException e) {
            logger.write("Error getting account by id");
        }
        fillAccount(account);
        return account;
    }

    /**
     * Returns an account by provided account's number,
     * if accNumber == null || accNumber == 0 returns blank account
     *
     * @param accNumber account number of needed account
     * @param isSaving  flag for saving account
     * @return account
     */
    @Override
    public Account getAccountByAccNumber(String accNumber, boolean isSaving) {
        Account account = new Account();
        if (accNumber == null || accNumber.equals("")) {
            logger.write("Fill account number to get account");
            return account;
        }

        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT accounts.id, acc_number, balance, o.id, o.name " +
                    "FROM accounts\n" +
                    "INNER JOIN owners o on o.id = accounts.owner_id \n" +
                    "WHERE acc_number = ?");
            ps.setString(1, accNumber);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                if (!isSaving)
                    logger.write("No accounts with such account number found");
                return account;
            }
            account.getOwner().setId(rs.getLong("owners.id"));
            account.getOwner().setName(rs.getString("owners.name"));

            account.setId(rs.getLong("accounts.id"));
            account.setBalance(rs.getBigDecimal("balance"));
            account.setAccNumber(rs.getString("acc_number"));
            new CardDAOImpl().getCardsOnAccount(account);

        } catch (SQLException e) {
            logger.write("Error getting account by account number");
        }
        fillAccount(account);
        return account;
    }

    /**
     * Updates fields account's number, balance and owner_id, provided by an account to DB,
     * If account == null || owner.id == 0 || account.id == 0 || account.accNumber.equals("") do nothing
     *
     * @param account to update
     */
    @Override
    public void updateAccount(Account account) {
        if (account == null || account.getOwner().getId() == 0 || account.getId() == 0 || account.getAccNumber().equals("")) {
            logger.write("Fill account details and owner to update");
            return;
        }

        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE accounts \n" +
                    "SET acc_number = ?, balance = ?, owner_id = ? \n" +
                    "WHERE id = ?");
            ps.setString(1, account.getAccNumber());
            ps.setBigDecimal(2, account.getBalance());
            ps.setLong(3, account.getOwner().getId());
            ps.setLong(4, account.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.write("Error updating account");
        }
    }

    /**
     * Adding provided amount to an account's balance by an account's number
     *
     * @param accNumber account number
     * @param amount    amount ro add to account
     */
    @Override
    public void addToBalance(String accNumber, BigDecimal amount) {
        if (accNumber == null || accNumber.equals("")) {
            logger.write("Fill account number to add to balance");
            return;
        }
        if (amount == null || amount.compareTo(new BigDecimal(0)) <= 0) {
            logger.write("Adding amount should be positive");
            return;
        }

        Account account = getAccountByAccNumber(accNumber, false);
        if (account.getId() == 0) {
            logger.write("Failed to add to balance");
            return;
        }
        BigDecimal sum = account.getBalance().add(amount);
        account.setBalance(sum);

        updateAccount(account);
    }

    /**
     * Creates new account for provided owner
     *
     * @param owner
     * @return
     */
    @Override
    public Account createNewAccountByOwner(Owner owner) {
        Account account = new Account();
        if (owner == null || owner.getId() == 0) {
            logger.write("Fill owner to create account");
            return account;
        }
        account.setOwner(owner);

        boolean cycle = true;
        while (cycle) {
            String number = "408";
            long start = (long) (1000_0000L * Math.random());
            long end = (long) (1000_0000_000L * Math.random());
            number = number.concat(String.valueOf(start));
            number = number.concat(String.valueOf(end));
            Account temp = getAccountByAccNumber(number, true);
            if (temp.getId() == 0) {
                account.setAccNumber(number);
                cycle = false;
            }
        }
        persistAccount(account);
        account = getAccountByAccNumber(account.getAccNumber(), false);
        fillAccount(account);
        return account;
    }

    /**
     * Checks if an account is in DB by its id
     *
     * @param account to search
     * @return true if account in DB, else return false
     */
    @Override
    public boolean isAccountInDB(Account account) {
        if (account == null || account.getId() == 0L)
            return false;
        account = getAccountById(account.getId());
        return account.getId() != 0L;
    }

    /**
     * Transfer amount from one account to other
     * @param source for withdrawal
     * @param target for deposit
     * @param amount to transfer
     * @return
     */
    @Override
    public boolean transfer(Account source, Account target, BigDecimal amount) {
        if (!isAccountInDB(source) || !isAccountInDB(target))
            return false;

        if (source.getBalance().compareTo(amount) <= 0) {
            return false;
        }
        BigDecimal sourceTemp, targetTemp;
        sourceTemp = source.getBalance().subtract(amount);
        targetTemp = target.getBalance().add(amount);


        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE accounts SET balance = CASE id " +
                            "WHEN ? THEN ? " +
                            "WHEN ? THEN ?" +
                            "ELSE balance " +
                            "END " +
                            "WHERE id IN (?, ?)");
            ps.setLong(1, source.getId());
            ps.setBigDecimal(2, sourceTemp);
            ps.setLong(3, target.getId());
            ps.setBigDecimal(4, targetTemp);
            ps.setLong(5, source.getId());
            ps.setLong(6, target.getId());
            ps.executeUpdate();

            source.setBalance(sourceTemp);
            target.setBalance(targetTemp);
        } catch (SQLException e){
            logger.write("Error during transfer");
            return false;
        }
        return true;
    }

    /**
     * Fills account's owner's data if it is empty
     *
     * @param account
     */
    private void fillAccount(Account account) {
        if (account.getOwner().getAccounts().isEmpty())
            account.getOwner().getAccounts().add(account);
    }
}
