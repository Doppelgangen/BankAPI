package com.vik.dao;

import com.vik.common.LoggerImpl;
import com.vik.models.Account;
import com.vik.models.Card;
import com.vik.models.Owner;
import com.vik.service.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of operations with entity Card via DB
 */
public class CardDAOImpl implements CardDAO {
    LoggerImpl logger = new LoggerImpl();

    /**
     * Persists a card to DB,
     * If the card is null or card number not filled returns false
     * (card!=null && card.getCardNumber!=0)
     *
     * @param card to save to db
     */
    @Override
    public boolean persistCard(Card card) {

        if (card == null || card.getCardNumber() == 0) {
            logger.write("Fill card");
            return false;
        }

        Card test = getCardByNumber(card.getCardNumber(), true);
        if (test.getId() != 0) {
            logger.write("Card number is not unique");
            return false;
        }

        if (new AccountDAOImpl().isAccountInDB(card.getAccount())) {
            try (DBConnection dbc = new DBConnection()) {
                Connection connection = dbc.getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO cards (card_number, account_id) " +
                                "VALUES (?, ?)");

                ps.setLong(1, card.getCardNumber());
                ps.setLong(2, card.getAccount().getId());
                ps.executeUpdate();

            } catch (Exception e) {
                logger.write("Error writing card to DB");
            }
        } else return false;
        return true;
    }

    /**
     * Fills set of cards of a received account,
     * If the account is empty or null then do nothing
     * (account!=null && account.getId()!=0)
     *
     * @param account to get cards for
     */
    @Override
    public void getCardsOnAccount(Account account) {
        if (account == null || account.getId() == 0) {
            logger.write("Fill account to get its cards");
            return;
        }

        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();

            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM cards WHERE account_id = ?");
            ps.setLong(1, account.getId());

            ResultSet resultSet = ps.executeQuery();

            List<Card> cardsOnAccount = new ArrayList<>();

            while (resultSet.next()) {
                Card card = new Card();
                card.setId(resultSet.getLong("id"));
                card.setCardNumber(resultSet.getLong("card_number"));
                card.setAccount(account);
                fillCard(card);
                cardsOnAccount.add(card);
            }
            if (cardsOnAccount.isEmpty()) {
                return;
            }
            account.setCards(cardsOnAccount);

        } catch (SQLException e) {
            logger.write("Error get card by account");
        }
    }

    /**
     * Finds a card by an id,
     * If the id is null or not filled returns blank card
     * (id!=null && id != 0)
     *
     * @param id : Card id
     * @return card
     */
    @Override
    public Card getCardById(Long id) {
        Card card = new Card();

        if (id == null || id.equals(0L)) {
            logger.write("Card Id should be valid");
            return card;
        }

        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();

            PreparedStatement ps = connection.prepareStatement(
                    "SELECT CARDS.ID, CARD_NUMBER, A.ID, BALANCE, ACC_NUMBER, O.ID, NAME\n" +
                            "FROM CARDS\n" +
                            "INNER JOIN ACCOUNTS A on A.ID = CARDS.ACCOUNT_ID\n" +
                            "INNER JOIN OWNERS O on O.ID = A.OWNER_ID\n" +
                            "WHERE CARDS.ID = ?");

            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                pasteCard(resultSet, card);
            } else {
                logger.write("No card with such ID");
            }
        } catch (SQLException e) {
            logger.write("Error getting card by ID");
            return new Card();
        }
        fillCard(card);
        return card;
    }

    /**
     * Updates the card's number by a provided card,
     * (card.id != 0 && card.number != 0)
     *
     * @param card to update card number
     */
    @Override
    public void updateCardNumber(Card card) {
        if (card == null || card.getAccount() == null || card.getId() == 0 || card.getCardNumber() == 0) {
            logger.write("Wrong card Id");
            return;
        }
        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE cards SET card_number = ?" +
                    "WHERE id = ?");
            ps.setLong(1, card.getCardNumber());
            ps.setLong(2, card.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.write("Error update card number");
        }
    }

    /**
     * Updates a card's account_id by a provided card,
     * card.id != 0
     *
     * @param card to update card account
     */
    @Override
    public void updateCardAccount(Card card) {
        if (card == null || card.getAccount() == null || card.getId() == 0 || card.getAccount().getId() == 0) {
            logger.write("Wrong card account id");
        }
        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE cards SET account_id = ?" +
                    "WHERE id = ?");
            ps.setLong(1, card.getAccount().getId());
            ps.setLong(2, card.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.write("Error update card account id");
        }
    }

    /**
     * Get a card by card's number
     *
     * @param cardNumber of needed card
     * @param isSaving   needed for ignore messages to log in case of search for duplicates
     * @return card
     */
    @Override
    public Card getCardByNumber(Long cardNumber, boolean isSaving) {
        Card card = new Card();
        if (cardNumber == null || cardNumber == 0) {
            logger.write("Wrong card number");
            return card;
        }
        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT CARDS.ID, CARD_NUMBER, A.ID, BALANCE, ACC_NUMBER, O.ID, NAME\n" +
                            "FROM CARDS\n" +
                            "INNER JOIN ACCOUNTS A on A.ID = CARDS.ACCOUNT_ID\n" +
                            "INNER JOIN OWNERS O on O.ID = A.OWNER_ID\n" +
                            "WHERE CARD_NUMBER = ?");

            ps.setLong(1, cardNumber);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                pasteCard(resultSet, card);
            } else {
                if (!isSaving)
                    logger.write("No card with such number");
                return card;
            }
        } catch (SQLException e) {
            logger.write("Error get card by card number");
        }
        fillCard(card);
        return card;
    }

    /**
     * Publishes a new card by a provided account,
     * if the account is empty do nothing and return blank card
     *
     * @param account for new card
     * @return new card
     */
    @Override
    public Card publishNewCard(Account account) {
        Card card = new Card();

        if (account == null || account.getId() == 0) {
            logger.write("Fill account to publish new card");
            return card;
        }

        card.setAccount(account);
        boolean cycle = true;
        while (cycle) {
            long number = (long) (1000_0000_0000_0000_0L * Math.random());
            Card temp = getCardByNumber(number, true);
            if (temp.getId() == 0) {
                card.setCardNumber(number);
                cycle = false;
            }
        }
        persistCard(card);
        card = getCardByNumber(card.getCardNumber(), false);
        fillCard(card);
        return card;
    }

    @Override
    public List<Card> getAllCardsOfOwner(Owner owner) {
        List<Card> cards = new ArrayList<>();
        if (owner == null || owner.getId() == 0) {
            logger.write("Fill owner to get all his cards");
            return cards;
        }

        try (DBConnection dbc = new DBConnection()){
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT C.ID, C.CARD_NUMBER, A.ID, OWNERS.ID " +
                    "FROM OWNERS " +
                    "INNER JOIN ACCOUNTS A on A.OWNER_ID = OWNERS.ID " +
                    "INNER JOIN CARDS C on C.ACCOUNT_ID = A.ID " +
                    "WHERE OWNERS.ID = ?");
            ps.setLong(1, owner.getId());

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()){
                Card card = new Card();
                card.setId(resultSet.getLong("cards.id"));
                card.setCardNumber(resultSet.getLong("card_number"));
                card.getAccount().setId(resultSet.getLong("owners.id"));
                fillCard(card);
                cards.add(card);
            }
        } catch (SQLException e){
            logger.write("Error getting cards by owner");
            return cards;
        }
        return cards;
    }

    /**
     * Checks if a card is in DB by its id
     * @param card to search
     * @return true if card is in DB
     */
    @Override
    public boolean isCardInDB(Card card) {
        if (card == null || card.getId() == 0L)
            return false;
        card = getCardById(card.getId());
        return card.getId() != 0L;
    }

    /**
     * Method is used both in getById and getCardByNumber
     *
     * @param resultSet provided by preparedStatement
     * @param card      provided by method
     * @throws SQLException yes
     */
    private void pasteCard(ResultSet resultSet, Card card) throws SQLException {
        card.getAccount().getOwner().setName(resultSet.getString("OWNERS.NAME"));
        card.getAccount().getOwner().setId(resultSet.getLong("OWNERS.ID"));

        card.getAccount().setId(resultSet.getLong("ACCOUNTS.ID"));
        card.getAccount().setBalance(resultSet.getBigDecimal("ACCOUNTS.BALANCE"));
        card.getAccount().setAccNumber(resultSet.getString("ACCOUNTS.ACC_NUMBER"));

        card.setId(resultSet.getLong("CARDS.ID"));
        card.setCardNumber(resultSet.getLong("CARD_NUMBER"));
    }

    /**
     * Fills card data up to it's owner if it is empty
     * @param card to fill
     */
    private void fillCard(Card card){
        if (card.getAccount().getOwner().getAccounts().isEmpty())
        card.getAccount().getOwner().getAccounts().add(card.getAccount());

        if (card.getAccount().getCards().isEmpty())
        card.getAccount().getCards().add(card);
    }
}
