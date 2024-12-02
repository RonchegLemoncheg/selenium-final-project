import Util.Util;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import com.beust.ah.A;
import ge.tbcitacademy.data.Constants;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.time.Duration;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HolidayPageTests {

    private WebDriver driver;
    private JavascriptExecutor jsExecutor;
    private WebDriverWait wait;


    @BeforeClass

    @Parameters("browserType")
    public void setup(@Optional("chrome") String browserType) {
        switch (browserType.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Error");
        }
        driver.manage().window().maximize();
        driver.get(Constants.SWOOPLINK);
        jsExecutor = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }


    @AfterClass
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void descendingOrderTest() {
        WebElement element = driver.findElement(By.xpath("//a[p[text()='დასვენება']]"));
        Util.goToLink(driver, wait, element);

        double mostExpensiveOfferPrice = Util.getMostExpensiveOffer(wait);

        List<Double> offersList = Util.getEveryOffer(driver, wait, jsExecutor, true);
        offersList.sort(Comparator.reverseOrder());

        Assert.assertEquals(offersList.get(0), mostExpensiveOfferPrice);
    }

    @Test
    public void ascendingOrderTest() {
        WebElement element = driver.findElement(By.xpath("//a[p[text()='დასვენება']]"));
        Util.goToLink(driver, wait, element);

        double leastExpensiveOfferPrice = Util.getLeastExpensiveOffer(wait);

        List<Double> offersList = Util.getEveryOffer(driver, wait, jsExecutor, true);
        offersList.sort(Comparator.naturalOrder());

        Assert.assertEquals(offersList.get(0), leastExpensiveOfferPrice);
    }

    @Test
    public void filterTest() {
        WebElement element = driver.findElement(By.xpath("//a[p[text()='დასვენება']]"));
        Util.goToLink(driver, wait, element);
        WebElement element1 = driver.findElement(By.xpath("//h5[text()='მთის კურორტები']"));
        Util.goToLink(driver, wait, element1);
        WebElement temp = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='radio-გადახდის ტიპი-1']")
        ));
        WebElement spanElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[text()='გადახდის ტიპი']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", temp);
        temp.click();
        jsExecutor.executeScript("window.scrollTo(0, 0);");
        WebElement mainDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.grid.laptop\\:grid-cols-3.grid-flow-row.gap-x-4.gap-y-8.grid-cols-2")
        ));
        //tu es ipova eseigi martlac tavzea radganac preceding aris
        WebElement precedingDiv = mainDiv.findElement(By.xpath("preceding::div//p[text()='სრული გადახდა']"));
        Assert.assertNotNull(precedingDiv);

        List<Double> offersList = Util.getEveryOffer(driver, wait, jsExecutor, false);
        offersList.sort(Comparator.naturalOrder());
        Double leastExpensiveOfferPrice = Util.getLeastExpensiveOffer(wait);
        Assert.assertEquals(offersList.get(0), leastExpensiveOfferPrice);

    }

    @Test
    public void priceRangeTest() {
        WebElement element = driver.findElement(By.xpath("//a[p[text()='დასვენება']]"));
        Util.goToLink(driver, wait, element);

        WebElement temp = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".text-primary_green-100-value.bg-primary_green-10-value.h-12.min-w-max")
        ));

        jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", temp);

        WebElement dan = driver.findElement(By.xpath("//p[text()='დან']/following-sibling::input"));

        WebElement mde = driver.findElement(By.xpath("//p[text()='მდე']/following-sibling::input"));

        dan.sendKeys("400");
        mde.sendKeys("500");

        Util.goToLink(driver,wait,temp);

        List<Double> offersList = Util.getEveryOffer(driver, wait, jsExecutor, false);
        List<Double> streamedList = offersList.stream().filter(offer -> offer >= 400 && offer <= 500).toList();
        Assert.assertEquals(offersList.size(), streamedList.size());
    }


}
