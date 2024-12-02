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

import java.time.Duration;
import java.util.List;

public class LandingPageTests {

    private WebDriver driver;
    private JavascriptExecutor jsExecutor;
    private Actions actions;
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
        actions = new Actions(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }


    @AfterClass
    public void tearDown() {
        driver.quit();
    }

   @Test
    public void activeCategoryTest(){
        WebElement button = driver.findElement(By.xpath("//p[text()='კატეგორიები' and @color='dark']"));
        button.click();
        WebElement sporti = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h4[text()='სპორტი']/parent::div"))
        );

        actions.moveToElement(sporti).perform();
        WebElement kartingebi = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h4[text()='კარტინგი']"))
        );
        String kartingi = kartingebi.getText();
        Util.goToLink(driver,wait,kartingebi);
        Assert.assertEquals(driver.getCurrentUrl(),Constants.KARTINGEBILINK);
        WebElement mainNav = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("nav.py-2")
        ));
        // es aris category chainshi arsebuli yvela teqsti (chven shemtxvevashi მთავარი სპორტი კარტინგი)
        List<String> text = mainNav.findElements(By.xpath(".//a//p"))
                .stream()
                .map(WebElement::getText)
                .toList();
        Assert.assertTrue(text.contains(kartingi));

    }


    @Test
    public void logoTest(){
        // aq DZALIAN ISHVIATAD errors migdebs magram daaxloebit 99.99% it var darwmunebuli rom kodis brali ar aris
        // meubneba sxva elementi efarebao da eg elementi arasebobs
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[p[text()='დასვენება']]")));
        Util.goToLink(driver, wait, element);
        WebElement swoopLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@alt='swoop']/parent::a")));
        Util.goToLink(driver, wait, swoopLink);
        Assert.assertEquals(driver.getCurrentUrl(),Constants.SWOOPLINK);
    }
}