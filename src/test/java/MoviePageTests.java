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

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.time.Duration;
import java.util.HashMap;
import java.util.stream.Collectors;

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
        WebElement div = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".flex.flex-col.w-full.mb-6")
        ));

        wait.until(e -> !div.findElements(By.xpath("./div//a")).isEmpty());

        WebElement firstMovie = div.findElement(By.xpath("./div//a"));

        WebElement firstMovieDesc = firstMovie.findElement(By.xpath(".//div[contains(@class, 'flex-col') and contains(@class, 'gap-1')]"));

        String movieName = firstMovieDesc.findElement(By.tagName("h3")).getText();

        WebElement image = driver.findElement(By.xpath("//img[@alt='" + movieName + "']"));


        Util.goToLink(driver,wait,image);

        WebElement cavea = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[text()='კავეა ისთ ფოინთი']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", cavea);

        String cinemaName = cavea.getText();

        WebElement mainDiv = cavea.findElement(By.xpath("./ancestor::div[contains(@class, 'flex-col') and contains(@class, 'gap-6')]"));
        WebElement options = mainDiv.findElement(By.xpath(".//div[contains(@class, 'grid') and contains(@class, 'grid-cols-2')]"));
        WebElement lastOption = options.findElement(By.xpath(".//div[contains(@class, 'cursor-pointer')][last()]"));

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

        //amat indeqsebis gareshe ver izamdi, gind dinamiurad meqna mainc damchirdeboda listis indeqsebi
        String tempCinemaName = mainDivTemp.findElement(By.xpath(".//p[1]")).getText();
        String tempDate = mainDivTemp.findElement(By.xpath(".//p[2]")).getText();
        Assert.assertEquals(tempMovieName,movieName);
        Assert.assertTrue(tempDate.contains(monthAndDate) && tempDate.contains(hourTime));
        Assert.assertEquals(tempCinemaName,cinemaName);

        WebElement green = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[text()='თავისუფალი']/preceding-sibling::div")
        ));
        String legendColor = green.getCssValue("background-color");
        WebElement divElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='cursor-pointer ']")
        ));

        WebElement pathElement = divElement.findElement(By.tagName("path"));
        String freeSeatColor = pathElement.getAttribute("fill");


        Color colorFromRgba = Util.parseRgba(legendColor);
        Color colorFromHex = Color.decode(freeSeatColor);

        Assert.assertNotEquals(colorFromHex, colorFromRgba);

        Util.goToLink(driver,wait,divElement);

        WebElement link = driver.findElement(By.linkText("შექმენი"));

        Util.goToLink(driver,wait,link);

        WebElement password1 = driver.findElement(By.xpath("//input[@name='password']"));
        WebElement password2 = driver.findElement(By.xpath("//input[@name='PasswordRetype']"));
        WebElement gender = driver.findElement(By.xpath("//div[span[text()='მამრობითი']]"));
        WebElement name = driver.findElement(By.xpath("//input[@name='firstname']"));
        WebElement lastName = driver.findElement(By.xpath("//input[@name='lastname']"));
        WebElement phone = driver.findElement(By.xpath("//input[@name='phone']"));
        WebElement code = driver.findElement(By.xpath("//input[@name='sms_code']"));
        WebElement terms1 = driver.findElement(By.xpath("//input[@name='agree_terms']/following-sibling::span"));
        WebElement terms2 = driver.findElement(By.xpath("//input[@name='AgreeTBCTerms']/following-sibling::span"));

        WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
        password1.sendKeys("giorgiPOL123");
        password2.sendKeys("giorgiPOL123");
        gender.click();
        name.sendKeys("roncheg");
        lastName.sendKeys("lemoncheg");

        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//select[@name='birth_year']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", dropdown);
        wait.until(ExpectedConditions.elementToBeClickable(dropdown));
        Select birthYearSelect = new Select(dropdown);
        birthYearSelect.selectByValue("2005");

        phone.sendKeys("555707090");
        code.sendKeys("5656");
        terms1.click();
        terms2.click();
        button.click();
        // aq erti error iqneba mxolod da vamowmeb rom emailis error aris
        WebElement error = driver.findElement(By.xpath("//p[contains(@class, 'error')]"));
        Assert.assertEquals(error.getText(),"ჩაწერე ელფოსტა");
    }

}

