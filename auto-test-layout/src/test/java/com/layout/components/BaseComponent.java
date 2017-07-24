package com.layout.components;



import com.galenframework.testng.GalenTestNgTestBase;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.DataProvider;
import java.util.HashMap;
import java.util.Map;



import java.io.File;

public abstract class BaseComponent extends GalenTestNgTestBase {

    private static final String ENV_URL = GenericProperty.getEnvUrl();

    private WebDriver createChromeDriver(String device) throws Exception{
    	WebDriver driver = null;
    	String directory = GenericProperty.getBaseDirectoryLocation();
    	try{
    		DesiredCapabilities executionCapabilities = DesiredCapabilities.chrome();
    		
    		if(device.toLowerCase().equals("mobile")){
    		Map<String, String> mobileEmulation = new HashMap<String, String>();
    		mobileEmulation.put("deviceName", "Nexus 5");
    		Map<String, Object> chromeOptions = new HashMap<String, Object>();
    		chromeOptions.put("mobileEmulation", mobileEmulation);
    		
    		executionCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
    		}
    		else if(device.toLowerCase().equals("tablet")){
        		Map<String, String> mobileEmulation = new HashMap<String, String>();
        		mobileEmulation.put("deviceName", "iPad");
        		Map<String, Object> chromeOptions = new HashMap<String, Object>();
        		chromeOptions.put("mobileEmulation", mobileEmulation);
        		
        		executionCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        		}
    		executionCapabilities.setCapability("start-maximized", true);
    		ChromeDriverService service = new ChromeDriverService.Builder()
		.usingAnyFreePort()
		.usingDriverExecutable(new File(directory + File.separator + "chromedriver"))
		.build();
		service.start();
		driver = new ChromeDriver(service, executionCapabilities);
		return driver;
		}
		catch(Exception e){
			throw e;
		}
    	
    }
    
    
    @Override
    public WebDriver createDriver(Object[] args){
    	WebDriver driver = null;
    	try{
    	
        if (args.length > 0) {
            if (args[0] != null && args[0] instanceof GenericProperty.TestDevice) {
            	GenericProperty.TestDevice device = (GenericProperty.TestDevice)args[0];
                
                driver = this.createChromeDriver(device.getName());
                
                if (device.getScreenSize() != null) {
                    driver.manage().window().setSize(device.getScreenSize());
                }
            }
        }
        }
        catch(Exception e){
        	System.out.println(e.getLocalizedMessage());
        }
        return driver;
    }
    @Override
    public void load(String uri) {
        getDriver().get(ENV_URL + uri);
    }

    @DataProvider(name = "devices")
    public Object[][] devices () {
    	
    	Object[][] deviceDetails = null;
    	
    	try{
    	deviceDetails =  GenericProperty.getDeviceInfo();
    	}
    	catch(Exception e){
    		System.out.println(e.getMessage());
    		return null;
    	}
    	return deviceDetails;
    }
}
