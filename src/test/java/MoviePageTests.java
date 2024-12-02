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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.awt.*;
import java.time.Duration;

public class MoviePageTests {

    private WebDriver driver;
    private JavascriptExecutor jsExecutor;
    private WebDriverWait wait;
    private Actions actions;


    @BeforeClass

    @Parameters("browserType")
    public void setup(@Optional("firefox") String browserType) {
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
        actions = new Actions(driver);
    }


    @AfterClass
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void movieTest(){
        WebElement element = driver.findElement(By.xpath("//a[p[text()='კინო']]"));
        Util.goToLink(driver,wait,element);
        //aq ubralod pirvel kinos virchev
        WebElement div = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".flex.flex-col.w-full.mb-6")
        ));
        wait.until(e -> !div.findElements(By.xpath("./div//a")).isEmpty());
        WebElement firstMovie = div.findElement(By.xpath("./div//a"));

        //aqve vigeb kinos saxels mere rom ar vedzebo
        WebElement firstMovieDesc = firstMovie.findElement(By.xpath(".//div[contains(@class, 'flex-col') and contains(@class, 'gap-1')]"));
        String movieName = firstMovieDesc.findElement(By.tagName("h3")).getText();
        // linkze suratit gadavdivar radgan firefoxs arshevboda ise
        WebElement image = driver.findElement(By.xpath("//img[@alt='" + movieName + "']"));
        Util.goToLink(driver,wait,image);

        // aq pirobashi iyo cavea gamoyofili da agar gamaq constantebshi
        WebElement cavea = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[text()='კავეა ისთ ფოინთი']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", cavea);
        String cinemaName = cavea.getText();

        //es aris is droebis chamonatvali, mand shevdivar da bolos vigeb
        WebElement mainDiv = cavea.findElement(By.xpath("./ancestor::div[contains(@class, 'flex-col') and contains(@class, 'gap-6')]"));
        WebElement options = mainDiv.findElement(By.xpath(".//div[contains(@class, 'grid') and contains(@class, 'grid-cols-2')]"));
        WebElement lastOption = options.findElement(By.xpath(".//div[contains(@class, 'cursor-pointer')][last()]"));

        // es imito miweria ro ricxvi da tarigi vipovo shemdeg shesamowmeblad
        WebElement lastOptionDesc = lastOption.findElement(By.cssSelector(".items-end"));
        String monthAndDate = lastOptionDesc.findElement(By.cssSelector(".leading-5")).getText();
        if (monthAndDate.startsWith("0")) {
            monthAndDate = monthAndDate.substring(1);
        }
        String hourTime = lastOptionDesc.findElement(By.cssSelector(".leading-6")).getText();

        Util.goToLink(driver,wait,lastOption);

        WebElement mainDivTemp = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.max-tablet\\:hidden")
        ));
        String tempMovieName = mainDivTemp.findElement(By.tagName("h2")).getText();

        // amat indeqsebis gareshe ver izamdi, gind dinamiurad meqna mainc damchirdeboda listis indeqsebi
        // radganac igive aqvt yvelaferi
        // shemedzlo listshi chamayera da satitaod shememowmebina magram igivea ideashi eg
        String tempCinemaName = mainDivTemp.findElement(By.xpath(".//p[1]")).getText();
        String tempDate = mainDivTemp.findElement(By.xpath(".//p[2]")).getText();
        // aq vamowmeb rom cheshmariti informaciaa mocemuli
        Assert.assertEquals(tempMovieName,movieName);
        Assert.assertTrue(tempDate.contains(monthAndDate) && tempDate.contains(hourTime));
        Assert.assertEquals(tempCinemaName,cinemaName);

        // es pirveli aris tavisufalis feri (wre ro iyo)
        WebElement green = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[text()='თავისუფალი']/preceding-sibling::div")
        ));
        String legendColor = green.getCssValue("background-color");
        WebElement divElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='cursor-pointer ']")
        ));

        //es aris ukve adgilis feri
        WebElement pathElement = divElement.findElement(By.tagName("path"));
        String freeSeatColor = pathElement.getAttribute("fill");

        // calke funqcia shevqmeni amat shesadareblad
        Color colorFromRgba = Util.parseRgba(legendColor);
        Color colorFromHex = Color.decode(freeSeatColor);

        Assert.assertNotEquals(colorFromHex, colorFromRgba);

        Util.goToLink(driver,wait,divElement);
        WebElement link = driver.findElement(By.linkText("შექმენი"));
        Util.goToLink(driver,wait,link);
        //es dzaan didi iyo da utilshi gavitane, constantebs vxmarob iq
        Util.register(jsExecutor,wait);

        // aq erti error iqneba mxolod da vamowmeb rom emailis error aris
        WebElement error = driver.findElement(By.xpath("//p[contains(@class, 'error')]"));
        Assert.assertEquals(error.getText(),"ჩაწერე ელფოსტა");
    }

}

