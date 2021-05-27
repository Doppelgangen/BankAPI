package DBTests;

import com.vik.service.DBConnection;
import com.vik.service.InitType;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDBConnection {
    @Test
    public void shouldConnectToDB() {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:default", "sa", "")) {
            Assert.assertNotNull(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void databaseShouldInitialize() {
        if (!DBConnection.initialized)
            DBConnection.init(InitType.TEST);
        Assert.assertTrue(DBConnection.initialized);
    }

}
