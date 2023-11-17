import com.sun.tools.javac.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ExtraTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private ExpectedCondition<Boolean> allInputsEmpty;
    Logger logger;
    @Before
    public void setUp() {
        driver = new ChromeDriver();
        // Make sure the page is fully loaded
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.get("http://sdetchallenge.fetch.com/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        allInputsEmpty = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                // Find all input elements within divs inside the div with class 'game-board'
                List<WebElement> inputs = driver.findElements(By.cssSelector("div.game-board div input"));
                // Stream through all inputs and check if any has a non-empty value
                return inputs.stream().allMatch(input -> input.getAttribute("value").isEmpty());
            }
        };
        logger = Logger.getLogger(Main.class.getName());
    }

    @After
    public void tearDown() {
        // Clean up and close the browser after tests
        if (driver != null) {
            driver.quit();
        }
    }
    /**
     * Test if the there are 8 good coin and 1 fake coin
     * */
    @Test
    public void testCoinTarget(){
        int fakeCount = 0, goodCount = 0;
        for(int i = 0; i < 9; i++){
            WebElement goldButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("coin_" + i)));
            goldButton.click();
            // Read the alert message
            Alert alertDialog = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alertDialog.getText();
            if("Yay! You find it!".equals(alertText))
                fakeCount += 1;
            else if("Oops! Try Again!".equals(alertText))
                goodCount += 1;
            alertDialog.accept();
        }

        Assert.assertEquals(1, fakeCount);
        Assert.assertEquals(8, goodCount);

    }

    /**
     * Test if only valid input(0-8) is accepted.
     * */
    @Test
    public void testInvalidInput(){
        WebElement cell = driver.findElement(By.id("left_0"));
        cell.sendKeys("x");
        Assert.assertTrue(cell.getText().equals(""));

        cell.sendKeys("10");
        Assert.assertTrue(cell.getAttribute("value").equals("1"));

        cell = driver.findElement(By.id("left_1"));
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
        WebElement weighButton = driver.findElement(By.id("weigh"));
        WebElement resetButton = driver.findElement(By.xpath("//button[text()='Reset']"));
        WebElement cellLeft0 = driver.findElement(By.id("left_" + 0));
        WebElement cellLeft8 = driver.findElement(By.id("left_" + 8));
        cellLeft0.sendKeys("1");
        cellLeft8.sendKeys("1");
        weighButton.click();
        Alert alertDialog = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alertDialog.getText();

        Assert.assertTrue("Inputs are invalid: Left side has duplicates".equals(alertText));
        alertDialog.accept();

        resetButton.click();
        // UWait until the input are cleared
        wait.until(allInputsEmpty);

        WebElement cellRight4 = driver.findElement(By.id("right_" + 4));
        cellRight4.sendKeys("8");
        WebElement cellRight2 = driver.findElement(By.id("right_" + 2));
        cellRight2.sendKeys("8");
        weighButton.click();
        alertDialog = wait.until(ExpectedConditions.alertIsPresent());
        alertText = alertDialog.getText();

        Assert.assertTrue("Inputs are invalid: Right side has duplicates".equals(alertText));
        alertDialog.accept();
    }

    /**
     * Test if the correct alert is produced when there are same index on both sides.
     * */
    @Test
    public void testSameValueOnBothSide(){
        WebElement weighButton = driver.findElement(By.id("weigh"));
        WebElement cellLeft0 = driver.findElement(By.id("left_" + 7));
        cellLeft0.sendKeys("4");
        WebElement cellRight4 = driver.findElement(By.id("right_" + 5));
        cellRight4.sendKeys("4");
        weighButton.click();
        Alert alertDialog = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alertDialog.getText();

        Assert.assertTrue("Inputs are invalid: Both sides have coin(s): 4".equals(alertText));
        alertDialog.accept();
    }
}
