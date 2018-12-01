import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverParseTest {

    @Test
    public void testGetUrl(){
        ChromeDriver driver = new ChromeDriver();
        driver.get("");
    }

}
