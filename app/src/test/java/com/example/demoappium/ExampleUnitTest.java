package com.example.demoappium;


import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import androidx.test.InstrumentationRegistry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.By.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

import static org.junit.Assert.assertEquals;


public class ExampleUnitTest {

    private static AndroidDriver driver;
    private static String mFolderName;
    private static String mCurrentFragment;

    @BeforeClass
    public static void setUp() throws MalformedURLException {

        mFolderName = getFolderName();

        // Created object of DesiredCapabilities class.
        DesiredCapabilities capabilities = new DesiredCapabilities();

        // Set android deviceName desired capability. Set your device name.
        capabilities.setCapability("deviceName", "ce061606caa9a40f04");

        // Set android VERSION desired capability. Set your mobile device's OS version.
        capabilities.setCapability(CapabilityType.VERSION, "6.0.1");

        // Set android platformName desired capability. It's Android in our case here.
        capabilities.setCapability("platformName", "Android");

        // Set android appPackage desired capability. It is
        // com.android.calculator2 for calculator application.
        // Set your application's appPackage if you are using any other app.
        capabilities.setCapability("appPackage", "com.example.demoappium");

        // Set android appActivity desired capability. It is
        // com.android.calculator2.Calculator for calculator application.
        // Set your application's appPackage if you are using any other app.
        capabilities.setCapability("appActivity", "com.example.demoappium.MainActivity");

        // Created object of RemoteWebDriver will all set capabilities.
        // Set appium server address and port number in URL string.
        // It will launch calculator app in android device.
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);



        final String folderPath = "screenshots/" + mFolderName + "/";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        didChangePage();
    }

    private static String getCurrentFragment() {
        return driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc='activity']/android.view.ViewGroup[1]/android.widget.FrameLayout[1]/*")).getAttribute("content-desc");
    }

    private static String getFolderName() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        final String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);

        return year + month + day + hour + minute + second;
    }

    public void captureScreenshot() {
        final String fileName = getCurrentFragment();

        try {
            final String folderPath = "screenshots/" + mFolderName + "/";

            if (!new File(folderPath + fileName + ".png").exists()) {
                FileOutputStream out = new FileOutputStream(folderPath + fileName + ".png");
                out.write(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
                out.close();
            }
        } catch (Exception e) {
            System.out.println("Deu erro... sad");
            e.printStackTrace();
        }
    }

    private void goToStep2() {
        captureScreenshot();
        driver.findElementById("button_first").click();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        assertEquals(driver.findElement(By.id("textview_second")).getText(), "Segundo");
    }

    private static void didChangePage() {
        if (mCurrentFragment != null) {
            
        }

        mCurrentFragment = getCurrentFragment();
    }

    @Test
    public void testFirstFlow() {

        goToStep2();

        captureScreenshot();
        driver.findElementById("button_second").click();
        assertEquals(driver.findElement(By.id("textview_third")).getText(), "Terceiro");

        captureScreenshot();
        driver.findElementById("button_third").click();
        assertEquals(driver.findElement(By.id("textview_first")).getText(), "Primeiro");

//        WebDriverWait wait = new WebDriverWait(driver, 30);
//        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("DetailedFilterTitle"))).get(0).click();
    }

//    @Test
    public void testErrorFlow() {

        goToStep2();

        captureScreenshot();
        driver.findElementById("button_error").click();
        assertEquals(driver.findElement(By.id("textview_error")).getText(), "ERRO");

        captureScreenshot();
    }

    @AfterClass
    public static void End() {
        driver.quit();

        boolean errorOcurred = false;

        final String folderPath = "screenshots/" + mFolderName + "/";

        File file = new File(folderPath + "flow.dot");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write("digraph MeuDiagrama {");
            fr.write(System.lineSeparator());
            fr.write("A->B");
            fr.write(System.lineSeparator());
            fr.write("B->C");
            fr.write(System.lineSeparator());
            fr.write("C->A");
            fr.write(System.lineSeparator());
            fr.write("B->E");
            fr.write(System.lineSeparator());
            fr.write("}");

        } catch (IOException e) {
            System.out.println("Erro ao gerar arquivo");
            e.printStackTrace();

            errorOcurred = true;
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String absolutePath = file.getAbsolutePath();
        final String path = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
        if (!errorOcurred) {
            try {
                Runtime.getRuntime().exec("dot -Tpng -O " + path + "\\flow.dot");
            } catch (IOException e) {
                System.out.println("Erro ao gerar grafo:" +  path);
                e.printStackTrace();
            }
        }
    }
}
