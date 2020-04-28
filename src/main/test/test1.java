import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class test1 {
    ServerMain serverMain;

    @Before
    public void init() {
        serverMain = new ServerMain();
    }

    @Test
    public void testAdd1() {
        Assert.assertEquals("192.168.1.56", serverMain.getLocalIpAddress());
    }
}
