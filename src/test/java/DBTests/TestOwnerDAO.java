package DBTests;

import com.vik.dao.OwnerDAO;
import com.vik.dao.OwnerDAOImpl;
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
import java.util.ArrayList;
import java.util.List;

public class TestOwnerDAO {
    OwnerDAO ownerDAO =new OwnerDAOImpl();

    @BeforeClass
    public static void dbInit() {
        if (!DBConnection.initialized)
            DBConnection.init(InitType.TEST);
    }

    @Test
    public void shouldPersistOwner() throws SQLException {
        Owner owner = new Owner();
        owner.setName("Jenkins");

        ownerDAO.persistOwner(owner);
        owner = ownerDAO.getOwnerByName("Jenkins");
        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM owners WHERE name = ?");
            ps.setString(1, owner.getName());

            ResultSet rs = ps.executeQuery();
            rs.next();

            Owner output = new Owner();
            output.setName(rs.getString("name"));
            output.setId(rs.getLong("id"));
            Assert.assertTrue(owner.equals(output));
        }
    }

    @Test
    public void shouldGetOwnerByName() throws SQLException {
        Owner owner = new Owner();
        owner = ownerDAO.getOwnerByName("Timmy");
        Assert.assertEquals(1L, owner.getId());
    }

    @Test
    public void shouldGetOwnerById() throws SQLException {
        Owner owner = new Owner();
        owner = ownerDAO.getOwnerById(1L);

        Assert.assertEquals("Timmy", owner.getName());
    }

    @Test
    public void shouldUpdateOwner() throws SQLException{
        Owner owner = new Owner();
        owner.setName("Iggy");
        ownerDAO.persistOwner(owner);
        owner = ownerDAO.getOwnerByName("Iggy");

        owner.setName("Zane");
        ownerDAO.updateOwnerName(owner);

        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM owners WHERE id = ?");
            ps.setLong(1, owner.getId());
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            Assert.assertEquals("Zane", resultSet.getString("name"));
        }
    }

    @Test
    public void shouldFindOwners() {
        List<Owner> out = new ArrayList<>();
        out = ownerDAO.getAllOwners();
        Assert.assertFalse(out.isEmpty());
    }

    @Test
    public void shouldFindOwnerInDB(){
        Owner owner = new Owner();
        owner.setId(1L);
        Assert.assertTrue(ownerDAO.isOwnerInDB(owner));
    }
}
