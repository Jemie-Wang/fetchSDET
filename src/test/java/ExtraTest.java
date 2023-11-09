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

public class ExtraTest {
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
     * Test if the correct alert is produced when click the click result.
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
    /**
     * Test if the correct alert is produced when click the wrong result.
     * */
    @Test
    public void testFindWrongTarget(){
        // Get the fake gold
        int fakeGold = finder.findFakeGold();
        int trueGold = (fakeGold + 1) % 9;
        WebElement fakeGoldButton = finder.wait.until(ExpectedConditions.elementToBeClickable(By.id("coin_" + trueGold)));
        fakeGoldButton.click();

        // Read the alert message
        Alert alertDialog = finder.wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alertDialog.getText();

        Assert.assertTrue("Choose the wrong gold", "Oops! Try Again!".equals(alertText));
        alertDialog.accept();
    }

    /**
     * Test if only valid input(0-8) is accepted.
     * */
    @Test
    public void testInvalidInput(){
        WebElement cell = finder.driver.findElement(By.id("left_0"));
        cell.sendKeys("x");
        Assert.assertTrue(cell.getText().equals(""));

        cell.sendKeys("10");
        Assert.assertTrue(cell.getAttribute("value").equals("1"));

        cell = finder.driver.findElement(By.id("left_1"));
        cell.sendKeys("9");
        Assert.assertTrue(cell.getAttribute("value").equals(""));

        cell.sendKeys("%");
        Assert.assertTrue(cell.getAttribute("value").equals(""));

        cell.sendKeys("5");
        Assert.assertTrue(cell.getAttribute("value").equals("5"));
    }

    /**
     * Test if the correct alert is produced when there are duplicated index on one side.
     * */
    @Test
    public void testDuplicateValueOnOneSize(){
        WebElement cellLeft0 = finder.driver.findElement(By.id("left_" + 0));
        cellLeft0.sendKeys("1");
        WebElement cellLeft8 = finder.driver.findElement(By.id("left_" + 8));
        cellLeft8.sendKeys("1");
        finder.weighButton.click();
        Alert alertDialog = finder.wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alertDialog.getText();

        Assert.assertTrue("Inputs are invalid: Left side has duplicates".equals(alertText));
        alertDialog.accept();

        finder.resetButton.click();
        // UWait until the input are cleared
        finder.wait.until(finder.allInputsEmpty);

        WebElement cellRight4 = finder.driver.findElement(By.id("right_" + 4));
        cellRight4.sendKeys("8");
        WebElement cellRight2 = finder.driver.findElement(By.id("right_" + 2));
        cellRight2.sendKeys("8");
        finder.weighButton.click();
        alertDialog = finder.wait.until(ExpectedConditions.alertIsPresent());
        alertText = alertDialog.getText();

        Assert.assertTrue("Inputs are invalid: Right side has duplicates".equals(alertText));
        alertDialog.accept();
    }

    /**
     * Test if the correct alert is produced when there are same index on both sides.
     * */
    @Test
    public void testSameValueOnBothSide(){
        WebElement cellLeft0 = finder.driver.findElement(By.id("left_" + 7));
        cellLeft0.sendKeys("4");
        WebElement cellRight4 = finder.driver.findElement(By.id("right_" + 5));
        cellRight4.sendKeys("4");
        finder.weighButton.click();
        Alert alertDialog = finder.wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alertDialog.getText();

        Assert.assertTrue("Inputs are invalid: Both sides have coin(s): 4".equals(alertText));
        alertDialog.accept();
    }
}
