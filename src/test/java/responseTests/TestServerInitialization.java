package responseTests;

import com.vik.server.ApplicationServer;
import com.vik.service.InitType;
import org.junit.Assert;
import org.junit.Test;

public class TestServerInitialization {
    @Test
    public void serverShouldStart() {
        if (!ApplicationServer.initialized) {
            new ApplicationServer().init(InitType.TEST);
        }
        Assert.assertTrue(ApplicationServer.initialized);
    }
}
