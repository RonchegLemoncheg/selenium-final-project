import Util.Util;
import ge.tbcitacademy.data.Constants;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

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
        // aq yvelaferi gasagebi unda iyos wesit, funqciebshi maq gatanili mteli saqme
        WebElement element = driver.findElement(By.xpath("//a[p[text()='დასვენება']]"));
        Util.goToLink(driver, wait, element);

        double mostExpensiveOfferPrice = Util.getMostExpensiveOffer(wait);

        List<Double> offersList = Util.getEveryOffer(driver, wait, jsExecutor, true);
        offersList.sort(Comparator.reverseOrder());

        Assert.assertEquals(offersList.get(0), mostExpensiveOfferPrice);
    }

    @Test
    public void ascendingOrderTest() {
        // aqac yvelaferi gasagebi unda iyos wesit, funqciebshi maq gatanili mteli saqme
        WebElement element = driver.findElement(By.xpath("//a[p[text()='დასვენება']]"));
        Util.goToLink(driver, wait, element);

        double leastExpensiveOfferPrice = Util.getLeastExpensiveOffer(wait);

        List<Double> offersList = Util.getEveryOffer(driver, wait, jsExecutor, true);
        offersList.sort(Comparator.naturalOrder());

        Assert.assertEquals(offersList.get(0), leastExpensiveOfferPrice);
    }

    @Test
    public void filterTest() {
        // aqac yvelaferi gasagebi unda iyos wesit, funqciebshi maq gatanili mteli saqme
        WebElement element = driver.findElement(By.xpath("//a[p[text()='დასვენება']]"));
        Util.goToLink(driver, wait, element);
        WebElement element1 = driver.findElement(By.xpath("//h5[text()='მთის კურორტები']"));
        Util.goToLink(driver, wait, element1);
        WebElement temp = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='radio-გადახდის ტიპი-1']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", temp);
        // es firefoxis gamo miweria
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.fixed.min-w-screen.min-h-screen.duration-300.ease-out")));
        temp.click();
        //aqamde ubralod filtrebi avirchie

        jsExecutor.executeScript("window.scrollTo(0, 0);");
        WebElement mainDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.grid-flow-row.gap-x-4.gap-y-8")
        ));
        // es aris is div sadac yvela offer aris ganlagebuli
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
                By.cssSelector(".text-primary_green-100-value.bg-primary_green-10-value")
        ));

        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", temp);

        WebElement dan = driver.findElement(By.xpath("//p[text()='დან']/following-sibling::input"));

        WebElement mde = driver.findElement(By.xpath("//p[text()='მდე']/following-sibling::input"));

        //aq ubralod konstantebidan shemaq fasi
        dan.sendKeys(Constants.PRICEMIN);
        mde.sendKeys(Constants.PRICEMAX);

        Util.goToLink(driver,wait,temp);

        //aq vamowmeb ubralod tu ipova mag fashi rame, tu veraferi ipova errors visvri
        boolean noSuchOffers = !driver.findElements(By.xpath("//h2[text()='შეთავაზება არ მოიძებნა']")).isEmpty();
        if(noSuchOffers) throw new RuntimeException(Constants.PRICEERROR);

        List<Double> offersList = Util.getEveryOffer(driver, wait, jsExecutor, false);
        List<Double> streamedList = offersList.stream().
                filter(offer -> offer >= Integer.parseInt(Constants.PRICEMIN) &&
                        offer <= Integer.parseInt(Constants.PRICEMAX)).toList();
        // vamowmeb rom yvela mnishvneloba mag shualedshia, tu es simartlea mashin sigrdze igive eqnebat listebs
        Assert.assertEquals(offersList.size(), streamedList.size());
    }


}
