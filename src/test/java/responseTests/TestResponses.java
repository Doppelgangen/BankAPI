package responseTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vik.client.ClientHandler;
import com.vik.dao.*;
import com.vik.models.Account;
import com.vik.models.Owner;
import com.vik.server.ApplicationServer;
import com.vik.service.DBConnection;
import com.vik.service.InitType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestResponses {
    ClientHandler clientHandler = new ClientHandler();
    ObjectMapper objectMapper = new ObjectMapper();
    OwnerDAO ownerDAO = new OwnerDAOImpl();
    AccountDAO accountDAO = new AccountDAOImpl();
    CardDAO cardDAO = new CardDAOImpl();
    String URL = "http://localhost:8080";

    @BeforeClass
    public static void init(){
        if (!ApplicationServer.initialized)
            new ApplicationServer().init(InitType.TEST);
        if (!DBConnection.initialized)
            DBConnection.init(InitType.TEST);
    }

    @Test
    public void shouldGetAllOwners() throws JsonProcessingException {
        String newURL = URL + "/owners";
        String result = clientHandler.sendGetById(newURL, 0L);
        List<Owner> owners = new ArrayList<>();
        owners = objectMapper.readValue(result, List.class);
        Assert.assertFalse(owners.isEmpty());
    }

    @Test
    public void shouldGetOwnerById() throws JsonProcessingException {
        String newURL = URL + "/owners";
        Long id = 1L;
        String result = clientHandler.sendGetById(newURL, id);
        Owner owner = ownerDAO.getOwnerById(id);
        Owner output = objectMapper.readValue(result, Owner.class);
        Assert.assertTrue(owner.equals(output));
    }

    @Test
    public void shouldGetOwnerByName() throws JsonProcessingException {
        String newURL = URL + "/owners";
        String parameterName = "name";
        String parameter = "Kerry";
        Owner owner = new Owner();
        owner.setName(parameter);
        ownerDAO.persistOwner(owner);
        String result = clientHandler.sendGetWithParameter(newURL, parameterName, parameter);
        owner = ownerDAO.getOwnerByName(parameter);
        Owner output = objectMapper.readValue(result, Owner.class);
        Assert.assertTrue(owner.equals(output));
    }

    @Test
    public void shouldPersistNewOwner() throws JsonProcessingException {
        String newURL = URL + "/owners";
        Owner owner = new Owner();
        owner.setName("Killian");
        String result = clientHandler.sendPostOwner(newURL, 0L, owner.getName());
        Owner output = objectMapper.readValue(result, Owner.class);
        owner = ownerDAO.getOwnerByName(owner.getName());
        Assert.assertTrue(owner.equals(output));
    }

    @Test
    public void shouldGetOwnerByAccountsID() throws JsonProcessingException {
        String newURL = URL + "/accounts";
        Long id = 1L;
        String result = clientHandler.sendGetById(newURL, id);
        Owner owner = objectMapper.readValue(result, Owner.class);
        Account account = accountDAO.getAccountById(id);
        Assert.assertTrue(account.equals(owner.getAccounts().get(0)));
    }

    @Test
    public void shouldCreateNewAccountByOwner() throws JsonProcessingException {
        String newURL = URL + "/accounts";
        Owner owner = new Owner();
        Long id = 1L;
        owner.setId(id);
        owner = ownerDAO.getOwnerById(1L);
        String result = clientHandler.sendPostOwner(newURL, id, "");
        accountDAO.getAccountsOnOwner(owner);
        Account account = owner.getAccounts().get(owner.getAccounts().size()-1);
        Owner output = objectMapper.readValue(result, Owner.class);
        Assert.assertTrue(account.equals(output.getAccounts().get(0)));
    }
}
