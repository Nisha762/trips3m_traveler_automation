package com.layout.tests;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.layout.components.BaseComponent;
import com.layout.components.GenericProperty;

import java.io.IOException;


public class WelcomePageTest extends BaseComponent {

    @Test(dataProvider = "devices")
    public void welcomePage_shouldLookGood_onDevice(GenericProperty.TestDevice device) throws IOException {
        load("/");
        checkLayout("/specs/HomePage.spec", device.getTags());
    }

    //@Test(dataProvider = "devices")
    public void loginPage_shouldLookGood_onDevice(GenericProperty.TestDevice device) throws IOException {
        load("/");
        getDriver().findElement(By.xpath("//button[.='Login']")).click();
        
        checkLayout("/specs/loginPage.spec", device.getTags());
    }

}
