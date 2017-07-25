package com.layout.components;



import com.galenframework.testng.GalenTestNgTestBase;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class BaseComponent extends GalenTestNgTestBase {

    private static final String ENV_URL = GenericProperty.getEnvUrl();   
    
    private WebDriver createDriverForChrome(String devicename) throws Exception{
    	WebDriver driver = null;	
    		try{
    			DesiredCapabilities executionCapabilities = new DesiredCapabilities();
    			executionCapabilities.setJavascriptEnabled(true);
    			executionCapabilities.setPlatform(Platform.ANY);
    			if(!GenericProperty.REMOTE_IP.isEmpty()){
    							String remoteUrl = GenericProperty.REMOTE_IP + "/wd/hub";
    							URL uri = new URL(remoteUrl);
    							executionCapabilities.setCapability("webdriver.remote.quietExceptions", true);
								executionCapabilities = DesiredCapabilities.chrome();
								Map<String,Object> chromeoptions=new HashMap<String,Object>();
								Map<String, String> mobileEmulation = new HashMap<String, String>();
								if(devicename.toLowerCase().equals(GenericProperty.MOBILE_KEYWORD)){
						    		mobileEmulation.put("deviceName", "Google Nexus 5");}
								else if(devicename.toLowerCase().equals(GenericProperty.TABLET_KEYWORD)){
					        		mobileEmulation.put("deviceName", "iPad");}
								chromeoptions.put("mobileEmulation", mobileEmulation);
    							executionCapabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized","--ignore-certificate-errors"));
    							executionCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeoptions);
   								driver = new RemoteWebDriver(uri, executionCapabilities);
    						}
    						else{
    								//LocalHost Execution.
    								
    								if(!GenericProperty.OS_STRING.contains("win"))
    								    	GenericProperty.CHROME_EXECUTABLE = GenericProperty.CHROME_EXECUTABLE_SH;
    								executionCapabilities = DesiredCapabilities.chrome();
    								Map<String,Object> chromeoptions=new HashMap<String,Object>();;
    								Map<String, String> mobileEmulation = new HashMap<String, String>();
    								if(devicename.toLowerCase().equals(GenericProperty.MOBILE_KEYWORD)){
    						    		mobileEmulation.put("deviceName", "Nexus 5");}
    								else if(devicename.toLowerCase().equals(GenericProperty.TABLET_KEYWORD)){
    					        		mobileEmulation.put("deviceName", "iPad");}
    								chromeoptions.put("mobileEmulation", mobileEmulation);
        							executionCapabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized","--ignore-certificate-errors"));
        							executionCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeoptions);
    								ChromeDriverService service = new ChromeDriverService.Builder()
    								.usingAnyFreePort()
    								.usingDriverExecutable(new File(GenericProperty.getBaseDirectoryLocation() + File.separator + GenericProperty.CHROME_EXECUTABLE))
    								.build();
    								service.start();
    								driver = new ChromeDriver(service, executionCapabilities);   								
    								}
    		}
    		catch(MalformedURLException me){
    			throw me;
    		}
    		catch(Exception e){
    			throw e;
    		}
    		return driver;
    		
    	}

    @Override
    public WebDriver createDriver(Object[] args){
    	WebDriver driver = null;
    	try{
    	
        if (args.length > 0) {
            if (args[0] != null && args[0] instanceof GenericProperty.TestDevice) {
            	GenericProperty.TestDevice device = (GenericProperty.TestDevice)args[0];
                
                driver = this.createDriverForChrome(device.getName());
                
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
