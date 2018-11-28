import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.ActionChainExecutor;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

public class WebDriverTest {

    @Test
    public void testFind(){
        WebDriver driver = new ChromeDriver();
        driver.get("http://maoyan.com/board/4");
        List<WebElement> elements = ((ChromeDriver) driver).findElementsByCssSelector(".board-index.board-index-2");
        System.out.println(elements.get(0).getText());
        Actions actions = new Actions(driver);

        driver.close();
    }
}
