package com.auto.solution.TestDrivers;


import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.Property.FILTERS;
import com.auto.solution.Common.ResourceManager;

public class DesktopWebTestDriverImpl implements TestDrivers{

	private static  WebDriver driver = null;
	
	private static WebDriverWait wait = null;
	
	private WebElement actualTestElement = null;
	    
	private ArrayList<WebElement> testObjectsList = new ArrayList<WebElement>();
	    
	private boolean getTestObjectList = false;
	
	private TestObjectDetails testObjectInfo = null;
    
    DesktopWebTestDriverImpl(ResourceManager rManager) {

	}
	
	@Override
	public void initializeApp(String endpoint) {
		
	}
	
	private WebElement waitAndGetTestObject(Boolean isWaitRequiredToFetchTheTestObject) throws NoSuchElementException, Exception{
	
		
		try{
			driver.switchTo().defaultContent();
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
		catch(Exception ex){
						throw new NoSuchElementException(Property.ERROR_MESSAGES.ER_GET_TESTOBJECT.getErrorMessage());
		}
		return actualTestElement;
	}
	
	private WebElement getActualTestObject(){
		WebElement testElement = null;		
		
		String 	locatingStrategyForObject = testObjectInfo.getLocatingStrategyOfTestObject();
		
		String locationOfObject = testObjectInfo.getLocationOfTestObject();
		
		testElement = this.getTestObject(locatingStrategyForObject, locationOfObject);
			
		return testElement;					
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
					if(filter.equals("")){
						filter = FILTERS.IS_DISPLAYED.getFilter();
					}
					if(filter.toLowerCase().contains(FILTERS.IS_DISPLAYED.getFilter())){
						if(!testObject.isDisplayed()){testObjectValidForFilters = false; break;}
					}
					if(filter.toLowerCase().contains(FILTERS.IS_ENABLED.getFilter())){
						if(!testObject.isEnabled()){testObjectValidForFilters = false; break;}
					}
					if(filter.toLowerCase().contains(FILTERS.IS_CLICKABLE.getFilter())){
						if(!isClickable(testObject)){testObjectValidForFilters = false; break;}
					}
					if(filter.toLowerCase().contains(FILTERS.IS_VISIBLE.getFilter())){
						if(!isVisible(testObject)){testObjectValidForFilters = false; break;}
					}
				if(testObjectValidForFilters){
					testElement = testObject;
					break;
				}
			}
			
			}
		}
		catch(Exception e){
			return null;
		}
		return testElement;
	}
	
	private boolean isClickable(WebElement testObject){
		try{
			wait.until(ExpectedConditions.elementToBeClickable(testObject));
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	private boolean isVisible(WebElement testobject){
		try{
			wait.until(ExpectedConditions.visibilityOf(testobject));
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
}

