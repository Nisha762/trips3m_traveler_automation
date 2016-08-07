package com.auto.solution.TestDrivers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.Common.Property.FILTERS;
import com.auto.solution.Common.Utility;
import com.auto.solution.Common.Property.ERROR_MESSAGES;


public class DesktopWebTestDriverImpl implements TestDrivers{

	private String browserName = "";
	
	private boolean isRemoteExecution = false;
	
	private String remoteURL = "";
	
	private static  WebDriver driver = null;
	
	private static WebDriverWait wait = null;
	
    private WebElement actualTestElement = null;
    
    private ArrayList<WebElement> testObjectsList = new ArrayList<WebElement>();
    
    private boolean getTestObjectList = false;
    
    private ResourceManager rManager;
    
    private TestObjectDetails testObjectInfo = null;
    
    DesktopWebTestDriverImpl(ResourceManager rManager) {
		this.rManager = rManager;
	}
    
    private File getScreenshot() throws Exception{
    	File srcFile;
    	try {
    		srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			srcFile=Utility.reduceScreenShotSize(srcFile,rManager.getTestExecutionLogFileLocation().replace("{0}", "tempFile.png"));
		} catch (Exception e) {
			throw e;
		}
		return srcFile;
	}
	
	private Object executeJavaScriptOnBrowserInstance(WebElement testElement,String javaScriptSnippet) throws Exception{
		
		JavascriptExecutor jsExecutor = null;
		Object jsResult = null;
		try{
			if(driver instanceof JavascriptExecutor){
			jsExecutor = (JavascriptExecutor)driver;
			 jsResult = jsExecutor.executeScript(javaScriptSnippet, testElement);
			}
		}
		catch(Exception e){
			throw e;
		}
		return jsResult;
	}
	
    private void switchToMostRecentWindow() {
		try {

			Set<String> windowHandles = driver.getWindowHandles();
			for (String window : windowHandles) {
				driver.switchTo().window(window);
			}

		} catch (Exception e) {
			//Nothing to do.
		}
	}
        
    /**
	 *  
	 * </br><b> Description : </b></br> This will fetch all the user inputs regarding the execution. Like Remote Execution details, Browser details, SauceLab Execution details.
	 */
	private void fetchUserInputs(){
		this.browserName = Property.BrowserName;
		if(Property.IsRemoteExecution.equalsIgnoreCase("true")){
			this.isRemoteExecution = true;
			this.remoteURL = Property.RemoteURL;
		}
	}
		
	/** 
	 * @param endPoint - String value of URL.
	 * @throws Exception
	 */
	private void openEndPointInBrowser(String endPoint) throws Exception{
		try{
			wait = new WebDriverWait(driver, Long.parseLong(Property.SyncTimeOut));
			
			driver.get(endPoint);
			
			driver.manage().window().maximize();
		}
		catch(Exception e){
			throw e;
		}
		
	}
	
	private WebElement getTestObject(String locatingStrategy,String location){
		List<WebElement> testElements = null;
		WebElement testElement = null;
		try{
			if(locatingStrategy.toLowerCase().contains("css")){
				testElements = driver.findElements(By.cssSelector(location));
			}
			else if(locatingStrategy.toLowerCase().contains("id")){
				testElements = driver.findElements(By.id(location));
			}
			else if(locatingStrategy.toLowerCase().contains("tag")){
				testElements = driver.findElements(By.tagName(location));
			}
			else if(locatingStrategy.toLowerCase().contains("class")){
				testElements = driver.findElements(By.className(location));
				}
			else if(locatingStrategy.toLowerCase().contains("name")){
				testElements = driver.findElements(By.name(location));
			}
			else if(locatingStrategy.toLowerCase().contains("xpath")){
				testElements = driver.findElements(By.xpath(location));
			}
			else if(locatingStrategy.toLowerCase().contains("text")){
				testElements = driver.findElements(By.linkText(location));
			}
			
			if(this.getTestObjectList){
				this.testObjectsList.clear();
				this.testObjectsList = (ArrayList<WebElement>) testElements;
			}
			
			String[] filtersForTestObject = testObjectInfo.getFiltersAppliedOnTestObject().split(",");
			
			
			for (WebElement testObject : testElements) {				
			
				boolean testObjectValidForFilters = true;
				
				for (String filter : filtersForTestObject) {
					if(filter.equals("")){filter = FILTERS.IS_DISPLAYED.getFilter();}
					
					if(filter.toLowerCase().contains(FILTERS.IS_ENABLED.getFilter())){
						if(!testObject.isEnabled()){testObjectValidForFilters = false; break;}
					}
					
					if(filter.toLowerCase().contains(FILTERS.IS_CLICKABLE.getFilter())){
						if(!isClickable(testObject)){testObjectValidForFilters = false; break;}
					}
				
					if(filter.toLowerCase().contains(FILTERS.IS_DISPLAYED.getFilter())){
						if(!testObject.isDisplayed()){testObjectValidForFilters = false; break;}
					}
					
					
				}
				
				if(testObjectValidForFilters){
					testElement = testObject;
					break;
				}
			}
			
		}
		catch(Exception e){
			return null;
		}
		return testElement;
	}
	
	public static boolean isClickable(WebElement testObject){
		try
		{
		   wait.until(ExpectedConditions.elementToBeClickable(testObject));
		   return true;
		}
		catch (Exception e)
		{
		  return false;
		}
	}
	
	private WebElement getFrameForTestObjectOnTheBasisOfFrameDetails(String[] locatorDetailsOfFrame){
		
		WebElement frameWebElement = null;
		try {
			String frameLocatingStrategy = locatorDetailsOfFrame[0].trim();
			String frameLOcation = locatorDetailsOfFrame[1].trim();
			frameWebElement = getTestObject(frameLocatingStrategy, frameLOcation);
		} catch (Exception e) {
			return null;
		}
		return frameWebElement;
	}
	
	private void switchToTestObjectFrame() throws NoSuchFrameException{
		try {
			
			if(testObjectInfo.getFramedetailsOfTestObject().contains(Property.Frame_Details_Seperator)){
				String[] parsedLocatorDetailsofFrameForTestObject = testObjectInfo.getFramedetailsOfTestObject().split(Property.Frame_Details_Seperator);
				WebElement frameWebElement = getFrameForTestObjectOnTheBasisOfFrameDetails(parsedLocatorDetailsofFrameForTestObject);
				driver.switchTo().frame(frameWebElement);
			}
						
		} 
		catch(NullPointerException ne){
			//nothing to do 
		}
		catch (Exception e) {
			String errMessage = ERROR_MESSAGES.ER_WHILE_SWITCHING_TO_FRAME.getErrorMessage().replace("{FRAME_DETAILS}", testObjectInfo.getFramedetailsOfTestObject());
			throw new NoSuchFrameException(errMessage);
		}
	}
	
	private WebElement getActualTestObject(){
		WebElement testElement = null;		
		
		String 	locatingStrategyForObject = testObjectInfo.getLocatingStrategyOfTestObject();
		
		String locationOfObject = testObjectInfo.getLocationOfTestObject();
		
		testElement = this.getTestObject(locatingStrategyForObject, locationOfObject);
			
		return testElement;					
	}

	private WebElement waitAndGetTestObject(Boolean isWaitRequiredToFetchTheTestObject) throws NoSuchElementException, Exception{
		try{
			switchToMostRecentWindow();
			driver.switchTo().defaultContent();
			this.switchToTestObjectFrame();
			if(Property.LIST_STRATEGY_KEYWORD.contains(Property.STRATEGY_KEYWORD.NOWAIT.toString())){
				isWaitRequiredToFetchTheTestObject = false;
			}
			
			String locatingStrategyForObject = testObjectInfo.getLocatingStrategyOfTestObject();
			
			String locationOfObject = testObjectInfo.getLocationOfTestObject();
			
			if(((locatingStrategyForObject=="")||locatingStrategyForObject==null)&&((locationOfObject=="")||locationOfObject==null))
			{
				throw new Exception(Property.ERROR_MESSAGES.ER_GETTING_TESTOBJECT.getErrorMessage());
			}
			
			if(isWaitRequiredToFetchTheTestObject){
					wait.until(new ExpectedCondition<WebElement>() {
							public WebElement apply(WebDriver d) {
								try {
										actualTestElement =  getActualTestObject();
										return actualTestElement;
									} catch (Exception e) {
									actualTestElement = null;
									return null;
									}
							}
					});
 			}
			else{
				actualTestElement = getActualTestObject();
			}
		}
		catch(TimeoutException te){
			try {
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new Exception(Property.ERROR_MESSAGES.ER_IN_SPECIFYING_RECOVERY_ACTION.getErrorMessage());
			}
		}
		catch(NoSuchFrameException ne){
			throw ne;
		}
		return actualTestElement;
	}
	
	public void waitUntilObjectIsThere(){
		
		driver.switchTo().defaultContent();
		
		this.switchToTestObjectFrame();
		
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				boolean isObjectNotPresent = true;		
				try {
						actualTestElement =  getActualTestObject();
						if(actualTestElement != null){ isObjectNotPresent = false;}
						return isObjectNotPresent;
					} catch (Exception e) {					
					}
				return isObjectNotPresent;
			}
	});
		
	}
	
	
	@Override
	public void injectTestObjectDetail(TestObjectDetails objDetails) {
		this.testObjectInfo = objDetails;
		
	}

	@Override
	public void initializeApp(String endpoint) throws MalformedURLException,Exception {
		
		try{
					//Get all the related Info.
						fetchUserInputs();
						DesiredCapabilities executionCapabilities = new DesiredCapabilities();
						executionCapabilities.setJavascriptEnabled(true);
						executionCapabilities.setPlatform(Platform.ANY);
						
										
						if(isRemoteExecution){
								String remoteUrl = this.remoteURL + "/wd/hub";
			
								URL uri = new URL(remoteUrl);
								executionCapabilities.setCapability("webdriver.remote.quietExceptions", true);
								if(this.browserName.contains(Property.CHROME_KEYWORD)){
									executionCapabilities = DesiredCapabilities.chrome();
									executionCapabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized","--ignore-certificate-errors"));
								}
								else{
									throw new Exception(Property.ERROR_MESSAGES.ER_SPECIFY_BROWSER.getErrorMessage());
								}
								driver = new RemoteWebDriver(uri, executionCapabilities);
						}
						else{
								//LocalHost Execution.
								if(this.browserName.contains(Property.CHROME_KEYWORD)){
									executionCapabilities = DesiredCapabilities.chrome();
									executionCapabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized","--ignore-certificate-errors"));
									ChromeDriverService service = new ChromeDriverService.Builder()
									.usingAnyFreePort()
									.usingDriverExecutable(new File(rManager.getChromeDriverExecutibleLocation()))
									.build();
									service.start();
									driver = new ChromeDriver(service, executionCapabilities);
								}
								else{
									throw new Exception(Property.ERROR_MESSAGES.ER_SPECIFY_BROWSER.getErrorMessage());
								}
								
								}
						
						// Open the URL to respective Browser
						this.openEndPointInBrowser(endpoint);
						Utility.addObjectToGlobalObjectCollection(Property.TEST_DRIVER_KEY, driver);
		}
		catch(MalformedURLException me){
			throw me;
		}
		catch(Exception e){
			throw e;
		}
		
	}

	
	/**
	 * Would be used for enterdata
	 */
	@Override
	public void sendKey(String text) throws NoSuchElementException, Exception {
		WebElement testElement = null;
		try{
			testElement = this.waitAndGetTestObject(true);	;			
		}
		catch(NoSuchElementException ne){
			throw ne;
		}
		try{
			try{
			testElement.clear();}
			catch(Exception e){}
			testElement.sendKeys(text);			
		}
		catch(Exception e){
			throw e;
		}
		
	}
	
	public void uploadFile(String text) throws NoSuchElementException, Exception {
		String file = null; 
		file =	rManager.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}", text);
		
		file =  file.replace("{PROJECT_NAME}", Property.PROJECT_NAME);
		
		String filepath = Utility.getAbsolutePath(file);

		WebElement testElement = null;;
		try{
			testElement = this.waitAndGetTestObject(true);;			
		}
		catch(NoSuchElementException ne){
			throw ne;
		}
		try{
			testElement.sendKeys(filepath);			
		}
		catch(Exception e){
			throw e;
		}
		
	}
	
	@Override
	public void isTextPresent(String text) throws Exception {
		WebElement testElement = null;
		
		try{
			testElement = this.waitAndGetTestObject(true);
		}
		catch(NoSuchElementException ne){
			if(testObjectInfo.getLocationOfTestObject() != ""){
				throw new NoSuchElementException(Property.ERROR_MESSAGES.ER_ELEMENT_NOT_PRESENT.getErrorMessage());
			}
			testElement = null;
		}
		
		String ObjectText = "";
		try{
		if(testElement == null){
			ObjectText = driver.getPageSource();
			}
		else{
			ObjectText = testElement.getText();
			}
			if(!Utility.matchContentsBasedOnStrategyDefinedForTestStep(text, ObjectText)){
				String msgException = Property.ERROR_MESSAGES.ER_ASSERTION.getErrorMessage().replace("{ACTUAL_DATA}",ObjectText);
				msgException = msgException.replace("{$TESTDATA}", text);
				throw new Exception(msgException);
			}		
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void click() throws Exception {
		WebElement testElement = null;
		try{
			testElement = this.waitAndGetTestObject(true);
			
			try{
			this.executeJavaScriptOnBrowserInstance(testElement, "arguments[0].focus()");
			
			testElement.click();
			}catch(Exception ex){
				if(ex.getMessage().toLowerCase().contains("element is not clickable")){
					this.executeJavaScriptOnBrowserInstance(testElement, "arguments[0].click();");

				}else{
					throw ex;
				}
//				
			}
			
		}
		catch(NoSuchElementException ne){
			throw ne;
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void shutdown() throws Exception {
		try{
			if(driver.getTitle() != "" || driver.getTitle() != null){
				driver.close();
			}
			try{
			driver.quit();}
			catch(Exception te){
				//Nothing to do.
			}
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void closeAllBrowsersWindow() throws Exception {
		try{
			for (String  window : driver.getWindowHandles()) {
				driver.switchTo().window(window);
				driver.close();
			}
			
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void isObjectThere() throws Exception {
		WebElement testElement = this.waitAndGetTestObject(true);
		try{
		if(testElement == null){
			throw new Exception(Property.ERROR_MESSAGES.ER_ELEMENT_NOT_PRESENT.getErrorMessage());
		}
		if(!testElement.isDisplayed()){
			throw new Exception(Property.ERROR_MESSAGES.ER_ELEMENT_NOT_DISPLAYED.getErrorMessage());
		}
		
		}
		catch(Exception e){
			throw e;
		}
		
	}
	
	public void isObjectNotThere() throws Exception{
				
		WebElement testElement = this.waitAndGetTestObject(false);		
		try {
			if(testElement != null){
				String errMessage = Property.ERROR_MESSAGES.TESTOBJECT_IS_THERE.getErrorMessage().replace("{IS_DISPALYED}", String.valueOf(testElement.isDisplayed()));
				throw new Exception(errMessage);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void sleep(long timesinmilliseconds){
		try{
		Thread.sleep(timesinmilliseconds);
		}
		catch(Exception e){
			// nothing to throw.
		}
		
	}

	
	@Override
	public String saveSnapshotAndHighlightTarget(boolean highlight) {
		/*
		 * For Debug Mode  OFF : take screenshots for FAILED test steps only
		 * FOr Debug MOde ON : take screenshots for ALL the test step irrespective of its status.
		 * For DebugMode  Strict Off : don't take screenshot at all. 
		 */
		File fileContainingSnapshot = null;
		File destinationFileThatWillContainSnapshot =  null;
		String screeShotFileName = "";
		try{			
			if(highlight){
				try{
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("arguments[0].style.border='4px groove Red'", actualTestElement);
					fileContainingSnapshot = this.getScreenshot();
					js.executeScript("arguments[0].style.border=''", actualTestElement);
				}
				catch(Exception e){ //nothing to do.
					}
				}				
			else{
			fileContainingSnapshot = this.getScreenshot();}				
			
			if(fileContainingSnapshot != null){
			
				String modifiedStepExecutionTimeString = Property.StepExecutionTime.replace("/", "");
				modifiedStepExecutionTimeString = modifiedStepExecutionTimeString.replace(":", "");
				modifiedStepExecutionTimeString = modifiedStepExecutionTimeString.replace(" ", "");
				destinationFileThatWillContainSnapshot = new File(rManager.getTestExecutionLogFileLocation().replace("{0}", Property.CURRENT_TEST_GROUP + "_" + Property.CURRENT_TESTSUITE + "_" + Property.CURRENT_TESTCASE + "_" + Property.CURRENT_TESTSTEP + "_" + Property.StepStatus + modifiedStepExecutionTimeString + ".jpg"));
				FileUtils.copyFile(fileContainingSnapshot,destinationFileThatWillContainSnapshot);
				screeShotFileName = destinationFileThatWillContainSnapshot.getName();
			}
		}
		catch(Exception e){
			//Nothing to do here.
		}
		return screeShotFileName;
	}	
}

