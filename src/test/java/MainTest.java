import com.sun.tools.javac.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.logging.Logger;


public class MainTest {
    FakeGoldFinder finder;
    Logger logger;

    @Before
    public void setUp() {
        finder = new FakeGoldFinder();
        logger = Logger.getLogger(Main.class.getName());
    }

    @After
    public void tearDown() {
        // Clean up and close the browser after tests
        finder.exist();
    }
    /**
     * Test if the correct alert is produced when the fake gold is found.
     * */
    @Test
    public void testFindCorrectTarget(){
        // Get the fake gold
        int fakeGold = finder.findFakeGold();
        WebElement fakeGoldButton = finder.wait.until(ExpectedConditions.elementToBeClickable(By.id("coin_" + fakeGold)));
        fakeGoldButton.click();

        // Read the alert message
        Alert alertDialog = finder.wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alertDialog.getText();
        logger.info("Get alert message: " + alertText);
        Assert.assertTrue("Find the fake bar", "Yay! You find it!".equals(alertText));
        alertDialog.accept();
    }
}


