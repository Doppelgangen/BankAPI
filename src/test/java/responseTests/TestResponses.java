package responseTests;

import com.vik.server.ApplicationServer;
import com.vik.service.InitType;
import org.junit.BeforeClass;

public class TestResponses {
    @BeforeClass
    public static void init(){
        if (!ApplicationServer.initialized)
            new ApplicationServer().init(InitType.TEST);
    }
}
