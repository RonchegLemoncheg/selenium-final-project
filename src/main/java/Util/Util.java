package Util;

import ge.tbcitacademy.data.Constants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.NaN;

public class Util {

    public static Double getPrice(WebElement temp) {
        try {
            WebElement h4 = temp.findElement(By.cssSelector("h4.text-primary_black-100-value.text-2md.leading-5.font-tbcx-bold"));
            String text = h4.getText();
            String numericPart = text.replaceAll("[^\\d.]", "");
            return Double.parseDouble(numericPart);
        } catch (Exception ex) {
            System.out.println("Error");
            return NaN;
        }

    }

    public static WebElement getPageBar(WebDriverWait wait) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.flex.justify-start.items-start.items-center.justify-center.gap-1")));
        } catch (Exception e) {
            return null;
        }
    }

    public static void goToLink(WebDriver driver, WebDriverWait wait, WebElement element) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.fixed.min-w-screen.min-h-screen.duration-300.ease-out")));
        String url = driver.getCurrentUrl();
        wait.until(ExpectedConditions.visibilityOf(element));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        element.click();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(url)));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.fixed.min-w-screen.min-h-screen.duration-300.ease-out")));
    }

    public static Double getMostExpensiveOffer(WebDriverWait wait) {
        WebElement temp1 = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("p.font-tbcx-regular.text-md.text-primary_black-100-value.flex.items-end.cursor-pointer")
        ));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.fixed.min-w-screen.min-h-screen.duration-300.ease-out")));
        temp1.click();

        WebElement temp2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.absolute.top-10.right-0.w-56.bg-white.py-2.px-2.flex.flex-col.rounded-xl.shadow-sm")
        ));
        WebElement biggest = temp2.findElement(By.xpath("//p//p[text()='ფასით კლებადი']"));

        biggest.click();

        WebElement mainDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.grid.laptop\\:grid-cols-3.grid-flow-row.gap-x-4.gap-y-8.grid-cols-2")
        ));

        wait.until(e -> !mainDiv.findElements(By.cssSelector("a.group.flex.flex-col.gap-3.cursor-pointer.max-tablet\\:gap-2")).isEmpty());

        WebElement biggestOffer = mainDiv.findElement(By.cssSelector("a.group.flex.flex-col.gap-3.cursor-pointer.max-tablet\\:gap-2"));

        return getPrice(biggestOffer);
    }


    public static List<Double> getEveryOffer(WebDriver driver, WebDriverWait wait, JavascriptExecutor jsExecutor, Boolean goToDasveneba) {
        if(goToDasveneba) {
            WebElement element = driver.findElement(By.xpath("//a[p[text()='დასვენება']]"));
            Util.goToLink(driver,wait,element);
        }


        List<Double> offersList = new ArrayList<>();
        int lastPage = 1;

        WebElement pageBar = Util.getPageBar(wait);
        if (pageBar != null) {
            WebElement lastDiv = pageBar.findElement(By.cssSelector("div:nth-last-child(2)"));
            lastPage = Integer.parseInt(lastDiv.getText());
        }


        for (int i = 1; i <= lastPage; i++) {

            WebElement mainDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.grid.laptop\\:grid-cols-3.grid-flow-row.gap-x-4.gap-y-8.grid-cols-2")
            ));

            wait.until(e -> !mainDiv.findElements(By.cssSelector("a.group.flex.flex-col.gap-3.cursor-pointer.max-tablet\\:gap-2")).isEmpty());

            List<WebElement> tempList = mainDiv.findElements(By.cssSelector("a.group.flex.flex-col.gap-3.cursor-pointer.max-tablet\\:gap-2"));

            List<Double> tempListInt = tempList.stream().map(Util::getPrice).toList();

            offersList.addAll(tempListInt);

            if (i != lastPage) {
                WebElement pageBarTemp = getPageBar(wait);
                jsExecutor.executeScript("arguments[0].scrollIntoView(true);", pageBarTemp);
                WebElement temp = pageBarTemp.findElement(By.xpath("//div[text()='" + (i + 1) + "']"));
                goToLink(driver, wait, temp);
            }

        }

        return offersList;

    }

    public static Double getLeastExpensiveOffer(WebDriverWait wait) {
        WebElement temp1 = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("p.font-tbcx-regular.text-md.text-primary_black-100-value.flex.items-end.cursor-pointer")
        ));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.fixed.min-w-screen.min-h-screen.duration-300")));
        temp1.click();

        WebElement temp2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.absolute.top-10.right-0.w-56.bg-white.py-2.px-2.flex.flex-col.rounded-xl.shadow-sm")
        ));
        WebElement smallest = temp2.findElement(By.xpath("//p//p[text()='ფასით ზრდადი']"));

        smallest.click();

        WebElement mainDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.grid.laptop\\:grid-cols-3.grid-flow-row.gap-x-4.gap-y-8.grid-cols-2")
        ));

        wait.until(e -> !mainDiv.findElements(By.cssSelector("a.group.flex.flex-col.gap-3.cursor-pointer.max-tablet\\:gap-2")).isEmpty());

        WebElement smallestOffer = mainDiv.findElement(By.cssSelector("a.group.flex.flex-col.gap-3.cursor-pointer.max-tablet\\:gap-2"));

        return getPrice(smallestOffer);
    }

    public static Color parseRgba(String rgba) {
        // es stackoverflowze vnaxe rgba rogor shevadaro hexs
        // ratomgac amas roca firefoxze vushveb rgba-s magivrad rgb-s abrunebs amitomac miweria es
        if(rgba.contains("a"))rgba = rgba.replace("rgba(", "").replace(")", "");
        else {
            rgba = rgba.replace("rgb(", "").replace(")", "");
            rgba+=", 1";
        }
        String[] parts = rgba.split(",\\s*");
        int r = Integer.parseInt(parts[0]);
        int g = Integer.parseInt(parts[1]);
        int b = Integer.parseInt(parts[2]);
        float alpha = Float.parseFloat(parts[3]);
        return new Color(r, g, b, Math.round(alpha * 255));
    }



}
