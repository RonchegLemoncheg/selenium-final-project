package Util;

import ge.tbcitacademy.data.Constants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.NaN;

public class Util {

    public static Double getPrice(WebElement temp) {
        try {
            WebElement h4 = temp.findElement(By.cssSelector("h4.text-primary_black-100-value.text-2md"));
            String text = h4.getText();
            String numericPart = text.replaceAll("[^\\d.]", "");
            return Double.parseDouble(numericPart);
        } catch (Exception ex) {
            System.out.println("Error");
            return NaN;
        }

    }

    public static WebElement getPageBar(WebDriver driver) {
        // aq axal waits imitom vqmni rom tu pirdapir klasis waits gadavcem
        // mashin bevrad didixani mouwevs lodini tu mxolod erti gverdia
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.items-start.justify-center.gap-1")));
        } catch (Exception e) {
            return null;
        }
    }

    public static void goToLink(WebDriver driver, WebDriverWait wait, WebElement element) {
        // am funqcias xandaxan imitom viyeneb rom ubralod ragacas davakliko
        // ucnauri wait-is xazi firefoxis gamo miweria, amitomac viyeneb am funqcias ragacas rom davachiro
        // tavidan rom ar momiwios am xazis dawera yvela daclickebaze
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.fixed.min-w-screen.min-h-screen.duration-300.ease-out")));
        String url = driver.getCurrentUrl();
        wait.until(ExpectedConditions.visibilityOf(element));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        element.click();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(url)));
        // es xazic firefoxis gamo miweria
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.fixed.min-w-screen.min-h-screen.duration-300.ease-out")));
    }

    public static Double getMostExpensiveOffer(WebDriverWait wait) {
        WebElement filter = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("p.text-primary_black-100-value.cursor-pointer")
        ));
        //es xazic firefoxis gamo miweria
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.fixed.min-w-screen.min-h-screen.duration-300.ease-out")));
        filter.click();

        WebElement mainFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.absolute.top-10.right-0.w-56.shadow-sm")
        ));
        WebElement mostExpensive = mainFilter.findElement(By.xpath("//p//p[text()='ფასით კლებადი']"));

        mostExpensive.click();

        //aqamde ubralod filters vacher rom yvelaze dzviri elementi avigo

        WebElement mainDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.grid-flow-row.gap-x-4.gap-y-8")
        ));
        wait.until(e -> !mainDiv.findElements(By.cssSelector("a.flex-col.gap-3.cursor-pointer")).isEmpty());
        WebElement biggestOffer = mainDiv.findElement(By.cssSelector("a.flex-col.gap-3.cursor-pointer"));
        //aq ubralod pirvel elements virchev

        return getPrice(biggestOffer);
    }


    public static List<Double> getEveryOffer(WebDriver driver, WebDriverWait wait, JavascriptExecutor jsExecutor, Boolean goToDasveneba) {
        if (goToDasveneba) {
            // amas im shemtxvevashi viyeneb tu aranairi filtri aris da pirdapir msurs
            // dasvenebidan yvela elementis wamogeba
            // shemedzlo sanam am funqcias gamovidzaxebdi maqamde gadavsuliyavi dasvenebaze magram ase mirchevnia
            WebElement element = driver.findElement(By.xpath("//a[p[text()='დასვენება']]"));
            Util.goToLink(driver, wait, element);
        }


        List<Double> offersList = new ArrayList<>();
        int lastPage = 1; //default mnishvneloba, minimum erti gverdi aris mainc offerebit

        WebElement pageBar = Util.getPageBar(driver);
        if (pageBar != null) {
            //tu ertze meti gverdia, mashin bolodan meore aris bolo gverdi
            // bolo aris div aris > (shemdegze gadasvla) da magis wina bolo gverdi
            WebElement lastDiv = pageBar.findElement(By.cssSelector("div:nth-last-child(2)"));
            lastPage = Integer.parseInt(lastDiv.getText());
        }


        for (int i = 1; i <= lastPage; i++) {
            // es aris is div sadac yvela offer aris ganlagebuli
            WebElement mainDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.grid-flow-row.gap-x-4.gap-y-8")
            ));
            wait.until(e -> !mainDiv.findElements(By.cssSelector("a.flex-col.gap-3.cursor-pointer")).isEmpty());
            List<WebElement> tempList = mainDiv.findElements(By.cssSelector("a.flex-col.gap-3.cursor-pointer"));
            List<Double> tempListInt = tempList.stream().map(Util::getPrice).toList();
            offersList.addAll(tempListInt);
            // aqamde ubralod yvela offer vnaxe, magat fasebze davmape da listshi davamate
            // romelsac bolos vabruneb
            if (i != lastPage) {
                WebElement pageBarTemp = getPageBar(driver);
                jsExecutor.executeScript("arguments[0].scrollIntoView(true);", pageBarTemp);
                assert pageBarTemp != null;
                WebElement temp = pageBarTemp.findElement(By.xpath("//div[text()='" + (i + 1) + "']"));
                goToLink(driver, wait, temp);
                // aq ubralod shemdeg gverdze gadavdivar
            }
        }

        return offersList;

    }

    public static Double getLeastExpensiveOffer(WebDriverWait wait) {
        WebElement filter = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("p.text-primary_black-100-value.cursor-pointer")
        ));
        //es xazic firefoxis gamo miweria
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.fixed.min-w-screen.min-h-screen.duration-300.ease-out")));
        filter.click();

        WebElement mainFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.absolute.top-10.right-0.w-56.shadow-sm")
        ));
        WebElement smallest = mainFilter.findElement(By.xpath("//p//p[text()='ფასით ზრდადი']"));

        smallest.click();

        WebElement mainDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.grid-flow-row.gap-x-4.gap-y-8")
        ));
        wait.until(e -> !mainDiv.findElements(By.cssSelector("a.flex-col.gap-3.cursor-pointer")).isEmpty());
        WebElement smallestOffer = mainDiv.findElement(By.cssSelector("a.flex-col.gap-3.cursor-pointer"));

        return getPrice(smallestOffer);
    }

    public static Color parseRgba(String rgba) {
        // es stackoverflowze vnaxe rgba rogor shevadaro hexs
        // ratomgac amas roca firefoxze vushveb rgba-s magivrad rgb-s abrunebs amitomac miweria es
        if (rgba.contains("a")) rgba = rgba.replace("rgba(", "").replace(")", "");
        else {
            rgba = rgba.replace("rgb(", "").replace(")", "");
            rgba += ", 1";
        }
        String[] parts = rgba.split(",\\s*");
        int r = Integer.parseInt(parts[0]);
        int g = Integer.parseInt(parts[1]);
        int b = Integer.parseInt(parts[2]);
        float alpha = Float.parseFloat(parts[3]);
        return new Color(r, g, b, Math.round(alpha * 255));
    }

    public static void register(JavascriptExecutor jsExecutor, WebDriverWait wait){
        // rac me amaze viwvale VER WARMOIDGENT
        // erti dge mxolod amas davutme, rom firefoxzec emushava thread.sleepis gareshe
        WebElement password1 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='password']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", password1);
        password1.sendKeys(Constants.PASSWORD);


        WebElement password2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='PasswordRetype']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", password2);
        password2.sendKeys(Constants.PASSWORD);


        WebElement gender = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[span[text()='" + Constants.GENDER + "']]")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", gender);
        gender.click();


        WebElement name = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='firstname']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", name);
        name.sendKeys(Constants.NAME);


        WebElement lastName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='lastname']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", lastName);
        lastName.sendKeys(Constants.LASTNAME);

        // aman gamiwyala guli, danarcheni mushaobda idialurad
        WebElement birthYearDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("select2-selection__rendered")));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", birthYearDropdown);
        wait.until(ExpectedConditions.elementToBeClickable(birthYearDropdown));
        birthYearDropdown.click();
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("select2-search__field")));
        searchField.sendKeys(Constants.YEAR);
        WebElement listItem = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='"+Constants.YEAR+"']")));
        listItem.click();


        WebElement phone = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='phone']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", phone);
        phone.sendKeys(Constants.PHONENUMBER);


        WebElement code = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='sms_code']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", code);
        code.sendKeys(Constants.SMSCODE);


        WebElement terms1 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='agree_terms']/following-sibling::span")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", terms1);
        terms1.click();


        WebElement terms2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='AgreeTBCTerms']/following-sibling::span")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", terms2);
        terms2.click();


        WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[@type='submit']")
        ));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", button);
        button.click();
    }


}
