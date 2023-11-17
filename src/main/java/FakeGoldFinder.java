import com.sun.tools.javac.Main;
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

public class FakeGoldFinder {
    private WebDriver driver;
    private WebDriverWait wait;
    private ExpectedCondition<Boolean> allInputsEmpty;
    private Logger logger;
    FakeGoldFinder(){
        driver = new ChromeDriver();
        // Make sure the page is fully loaded
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.get("http://sdetchallenge.fetch.com/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        // Define a custom ExpectedCondition to check that all input fields are empty
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
    public int findFakeGold() {
        try {
            WebElement resetButton = driver.findElement(By.xpath("//button[text()='Reset']"));
            WebElement weighButton = driver.findElement(By.id("weigh"));
            WebElement olElement = driver.findElement(By.cssSelector("div.game-info ol"));
            // Partition the gold into three groups, each containing consecutive indices,
            // identify each group by the starting index.
            int result = performWeighing(0, 3, 6, resetButton, weighButton, olElement);
            // Find the lighter group, and further partition it into three group, each have 1 gold bar
            result = performWeighing(result * 3, result * 3 + 1, result * 3 + 2, resetButton, weighButton, olElement);

            clickFakeGold(result, olElement);
            return result;
        } catch (Exception e) {
            logger.severe("An error occurred: " + e.getMessage());
            return -1;
        }
    }
    private int performWeighing(int group1Start, int group2Start, int group3Start, WebElement resetButton, WebElement weighButton, WebElement olElement) {
        int initialListSize = olElement.findElements(By.tagName("li")).size();

        addGoldToScale(group1Start, group2Start, "left");
        addGoldToScale(group2Start, group3Start, "right");

        weighButton.click();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("div.game-info ol li"), initialListSize));

        WebElement lastLi = driver.findElement(By.cssSelector("div.game-info ol li:last-child"));
        String[] text = lastLi.getText().split(" ");

        resetButton.click();
        wait.until(allInputsEmpty);

        if (text[1].equals("=")) return 2;
        else if (text[1].equals(">")) return 1;
        else return 0;
    }
    private void addGoldToScale(int start, int end, String side) {
        for (int i = start; i < end; i++) {
            WebElement cell = driver.findElement(By.id(side + "_" + i));
            cell.sendKeys("" + i);
        }
    }

    private void clickFakeGold(int groupIndex, WebElement olElement) {
        WebElement fakeGoldButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("coin_" + groupIndex)));
        fakeGoldButton.click();

        Alert alertDialog = wait.until(ExpectedConditions.alertIsPresent());
        logger.info("Alert message: " + alertDialog.getText());
        try{TimeUnit.SECONDS.sleep(4);}
        catch (InterruptedException ex){};
        alertDialog.accept();

        List<WebElement> liList = olElement.findElements(By.tagName("li"));
        for (WebElement li : liList) {
            logger.info(li.getText());
        }
    }

    public void exit(){
        if (driver != null) {
            driver.quit();
        }
    }
    public static void main(String[] args){
        FakeGoldFinder finder = new FakeGoldFinder();
        finder.findFakeGold();
        finder.exit();
    }

}
