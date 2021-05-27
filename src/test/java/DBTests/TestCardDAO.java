package DBTests;

import com.vik.dao.CardDAO;
import com.vik.dao.CardDAOImpl;
import com.vik.models.Account;
import com.vik.models.Card;
import com.vik.models.Owner;
import com.vik.service.DBConnection;
import com.vik.service.InitType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TestCardDAO {
    CardDAO cardDAO = new CardDAOImpl();
    @BeforeClass
    public static void dbInit() {
        if (!DBConnection.initialized)
            DBConnection.init(InitType.TEST);
    }

    @Test
    public void shouldPersistCardToDB() throws SQLException {
        Account account = new Account();
        account.setId(1);

        Card card = new Card();
        card.setCardNumber(1000000000000000L);
        card.setAccount(account);

        cardDAO.persistCard(card);
        card = cardDAO.getCardByNumber(1000000000000000L, false);

        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM cards WHERE id = ?");
            ps.setLong(1, card.getId());

            ResultSet rs = ps.executeQuery();

            rs.next();
            long out = rs.getLong("card_number");
            Assert.assertEquals(1000000000000000L, out);
        }
    }

    @Test
    public void shouldReturnMultipleCards() {
        Account account = new Account();
        account.setId(1);

        cardDAO.getCardsOnAccount(account);

        Assert.assertFalse(account.getCards().isEmpty());
    }

    @Test
    public void shouldFindCardById() throws SQLException {
        Card card = cardDAO.getCardById(1L);

        Assert.assertEquals(1111111111111111L, card.getCardNumber());
    }

    @Test
    public void shouldFindByCardNumber() {
        Card card = cardDAO.getCardByNumber(1111111111111111L, false);

        Assert.assertEquals(1L, card.getId());
    }

    @Test
    public void shouldUpdateCardNumber() {
        Card card = new Card();
        Account account = new Account();
        account.setId(1L);

        card.setAccount(account);
        card.setCardNumber(1100000000000000L);

        cardDAO.persistCard(card);
        card = cardDAO.getCardByNumber(1100000000000000L, false);

        card.setCardNumber(3L);
        cardDAO.updateCardNumber(card);
        Card out = cardDAO.getCardById(card.getId());

        Assert.assertEquals(3L, out.getCardNumber());
    }

    @Test
    public void shouldUpdateCardAccount() {
        Card card = cardDAO.getCardById(1L);
        card.getAccount().setId(2L);
        cardDAO.updateCardAccount(card);
        Card out = cardDAO.getCardById(1L);

        Assert.assertEquals(2L, out.getAccount().getId());
    }

    @Test
    public void shouldReturnBlankObject() {
        Card card = cardDAO.getCardById(null);
        Assert.assertEquals(0L, card.getId());
    }

    @Test
    public void shouldPublishNewCard() {
        Account account = new Account();
        account.setId(1L);
        Card card = cardDAO.publishNewCard(account);
        Assert.assertNotEquals(0L, card.getId());
    }

    @Test
    public void shouldGetCardsByOwner(){
        Owner owner = new Owner();
        owner.setId(1L);

        List<Card> cards = cardDAO.getAllCardsOfOwner(owner);
        Assert.assertFalse(cards.isEmpty());
    }

    @Test
    public void shouldFindCardInDB() {
        Card card = new Card();
        card.setId(1L);
        Assert.assertTrue(cardDAO.isCardInDB(card));
    }
}
