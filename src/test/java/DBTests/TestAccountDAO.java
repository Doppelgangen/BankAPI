package DBTests;

import com.vik.dao.AccountDAOImpl;
import com.vik.models.Account;
import com.vik.models.Owner;
import com.vik.service.DBConnection;
import com.vik.service.InitType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestAccountDAO {
    AccountDAOImpl accountDAO = new AccountDAOImpl();
    @BeforeClass
    public static void dbInit(){
        if (!DBConnection.initialized)
            DBConnection.init(InitType.TEST);
    }

    @Test
    public void shouldPersistAccount() throws SQLException {
        Account account = new Account();
        account.getOwner().setId(1L);
        account.setAccNumber("3189");
        account.setBalance(new BigDecimal(999));
        accountDAO.persistAccount(account);

        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE acc_number = ?");
            ps.setString(1, account.getAccNumber());
            ResultSet rs = ps.executeQuery();

            rs.next();
            Assert.assertEquals(account.getBalance(), rs.getBigDecimal("balance"));
        }
    }

    @Test
    public void shouldGetAccountByID() {
        Account account = new Account();
        account.setId(1L);
        account.getOwner().setId(1L);
        account = new AccountDAOImpl().getAccountById(account.getId());
        Assert.assertEquals("40884729571209875298", account.getAccNumber());
    }

    @Test
    public void shouldGetAccountByAccountNumber(){
        Account account = new Account();
        account = new AccountDAOImpl().getAccountByAccNumber("40884729571209875298", false);
        Assert.assertEquals(1L, account.getId());
    }

    @Test
    public void shouldGetAccountsOnOwner(){
        Owner owner = new Owner();
        owner.setId(1L);
        new AccountDAOImpl().getAccountsOnOwner(owner);
        Assert.assertFalse(owner.getAccounts().isEmpty());
    }

    @Test
    public void shouldAddToBalance(){
        Account account = accountDAO.getAccountById(1L);
        BigDecimal value = new BigDecimal("200.00");
        accountDAO.addToBalance(account.getAccNumber(), value);
        Account out = accountDAO.getAccountById(1L);
        Assert.assertEquals(account.getBalance().add(value), out.getBalance());
    }

    @Test
    public void shouldCreateNewAccount(){
        Owner owner = new Owner();
        owner.setId(1L);
        Account account = accountDAO.createNewAccountByOwner(owner);

        Assert.assertNotEquals("", account.getAccNumber());
    }

    @Test
    public void shouldFindAccountInDB(){
        Account account = new Account();
        account.setId(1L);
        Assert.assertTrue(accountDAO.isAccountInDB(account));
    }
}
