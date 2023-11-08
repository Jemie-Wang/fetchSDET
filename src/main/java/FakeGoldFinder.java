import com.sun.tools.javac.Main;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

public class FakeGoldFinder {
    static WebDriver driver;
    static WebDriverWait wait;
    static WebElement resetButton;
    static WebElement weighButton;
    static WebElement olElement;
    static ExpectedCondition<Boolean> allInputsEmpty;
    static Logger logger;
    FakeGoldFinder(){
        driver = new ChromeDriver();
        driver.get("http://sdetchallenge.fetch.com/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        resetButton = driver.findElement(By.xpath("//button[text()='Reset']"));
        weighButton = driver.findElement(By.id("weigh"));
        olElement = driver.findElement(By.cssSelector("div.game-info ol"));
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
    public static int findFakeGold(){
        // Partition the gold into three groups, each containing consecutive indices,
        // identify each group by the starting index.
        int group1Start = 0;
        int group2Start = 3;
        int group3Start = 6;
        // Find the lighter group
        int res = weigh(group1Start, group2Start, group3Start);
        // If group1 is lighter, the fake gold is with group1, continue to partition group1
        if(res == group1Start){
            group1Start = 0;
            group2Start = 1;
            group3Start = 2;
        }
        // If group2 is lighter, the fake gold is with group2, continue to partition group2
        else if(res == group2Start){
            group1Start = 3;
            group2Start = 4;
            group3Start = 5;
        }
        // If group3 is lighter, then the fake gold is in group1，continue to partition group3
        else if(res == group3Start){
            group1Start = 6;
            group2Start = 7;
            group3Start = 8;
        }

        // reset the scale for next round
        resetButton.click();
        // Wait until the scale is clear
        wait.until(allInputsEmpty);
        // The lighter on in the group is the fake gold
        res = weigh(group1Start, group2Start, group3Start);
        logger.info("Find the fake gold, id: " + res);
        logger.info("The weighing results are:");
        List<WebElement> liList = olElement.findElements(By.tagName("li"));
        for(WebElement li : liList){
            logger.info(li.getText());
        }
        return res;
    }

    /**
     * Given three group, weigh the first two to identify which is the lighter group among the three
     * */
    private static int weigh(int group1Start, int group2Start, int group3Start){
        // Each group is represented by the starting index and contains consecutive indices.
        // Add the gold to the scale
        for (int i = group1Start; i < group2Start; i++) {
            WebElement cellL = driver.findElement(By.id("left_" + i));
            cellL.sendKeys("" + i);
        }

        for (int i = group2Start; i < group3Start; i++) {
            WebElement cellR = driver.findElement(By.id("right_" + i));
            cellR.sendKeys("" + i);
        }

        int initialListSize = olElement.findElements(By.tagName("li")).size();
        // Weigh the two
        weighButton.click();

        // Only read when the new result is updated
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("div.game-info ol li"), initialListSize));

        WebElement lastLi = driver.findElement(By.cssSelector("div.game-info ol li:last-child"));
        String[] text = lastLi.getText().split(" ");

        // If the current two group on scale is of equal weight, the group3 should contain the fake gold
        if (text[1].equals("=")) return group3Start;
        else if (text[1].equals(">")) return group2Start;
        else if (text[1].equals("<")) return group1Start;
        return -1; // if none of the conditions are met, return an invalid index
    }

    public static void exist(){
        if (driver != null) {
            driver.quit();
        }
    }
    public static void main(String[] args){
        FakeGoldFinder finder = new FakeGoldFinder();
        finder.findFakeGold();
        finder.exist();
    }

}
