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

import java.util.List;

import org.testng.annotations.*;

import java.awt.*;
import java.time.Duration;

public class MoviePageTests extends BaseTest{

    @Test
    public void movieTest() {
        WebElement element = driver.findElement(By.xpath("//a[p[text()='კინო']]"));
        Util.goToLink(driver, wait, element);
        //aq ubralod pirvel kinos virchev
        WebElement div = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".flex.flex-col.w-full.mb-6")
        ));
        wait.until(e -> !div.findElements(By.cssSelector("a[href]:not([href*='/movies/bar/'")).isEmpty());
        List<String> moviesLink = div.findElements(By.cssSelector("a[href]:not([href*='/movies/bar/'"))
                .stream().map(e -> e.getAttribute("href")).toList();

        // aq mere davinaxe rom button yofila kinoteatristvis amitomac ese vtoveb
        for (int i = 0; i < moviesLink.size(); i++) {
            String url = driver.getCurrentUrl();
            driver.get(moviesLink.get(i));
            wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(url)));
            // vnaxulob kinos tu aqvs ist pointi
            boolean hasCinema = driver.findElements(By.xpath("//h3[text()='" + Constants.CINEMANAME + "']")).isEmpty();
            if (!hasCinema){
                // exla vedzeb ist pointshi tu aris adgilebi, tu ar aris sxvas vedzeb
                WebElement cavea = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h3[text()='" + Constants.CINEMANAME + "']")
                ));
                jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", cavea);
                WebElement lastOption = Util.getLastTimeOption(cavea);
                Util.goToLink(driver,wait,lastOption);
                // es aris mwvane skamebi
                boolean noSuchOffers = driver.findElements(By.xpath("//div[@class='cursor-pointer ']")).isEmpty();
                //tu erti adgili mainc aris mashin shesaferisia
                if(!noSuchOffers){
                    String tempUrl = driver.getCurrentUrl();
                    driver.get(moviesLink.get(i));
                    wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(tempUrl)));
                    break;
                }
            }

            if (i == moviesLink.size() - 1) {
                // ar arsebobs kino ist pointit
                throw new RuntimeException(Constants.MOVIEERROR);
            }
        }

        String movieName = driver.findElement(By.cssSelector("h1.text-xl.font-tbcx-bold")).getText();

        WebElement cavea = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[text()='" + Constants.CINEMANAME + "']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", cavea);
        String cinemaName = cavea.getText();


        //es aris is droebis chamonatvali, mand shevdivar da bolos vigeb
        WebElement lastOption = Util.getLastTimeOption(cavea);

        // es imito miweria ro ricxvi da tarigi vipovo shemdeg shesamowmeblad
        WebElement lastOptionDesc = lastOption.findElement(By.cssSelector(".items-end"));
        String monthAndDate = lastOptionDesc.findElement(By.cssSelector(".leading-5")).getText();
        if (monthAndDate.startsWith("0")) {
            monthAndDate = monthAndDate.substring(1);
        }
        String hourTime = lastOptionDesc.findElement(By.cssSelector(".leading-6")).getText();

        Util.goToLink(driver, wait, lastOption);

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
        Assert.assertEquals(tempMovieName, movieName);
        Assert.assertTrue(tempDate.contains(monthAndDate) && tempDate.contains(hourTime));
        Assert.assertEquals(tempCinemaName, cinemaName);

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

        Util.goToLink(driver, wait, divElement);
        WebElement link = driver.findElement(By.linkText("შექმენი"));
        Util.goToLink(driver, wait, link);
        //es dzaan didi iyo da utilshi gavitane, constantebs vxmarob iq
        Util.register(jsExecutor, wait);

        // aq erti error iqneba mxolod da vamowmeb rom emailis error aris
        WebElement error = driver.findElement(By.xpath("//p[contains(@class, 'error')]"));
        Assert.assertEquals(error.getText(), Constants.EMAILERRORMESSAGE);
    }

}

