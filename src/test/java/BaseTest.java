import ge.tbcitacademy.data.Constants;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    protected JavascriptExecutor jsExecutor;
    protected WebDriverWait wait;
    protected Actions actions;


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
                throw new IllegalArgumentException(Constants.BROWSERERROR);
        }
        driver.manage().window().maximize();
        jsExecutor = (JavascriptExecutor) driver;
        actions = new Actions(driver);
        driver.get(Constants.SWOOPLINK);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }


    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
