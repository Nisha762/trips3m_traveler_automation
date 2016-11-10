package com.auto.solution.TestDrivers.RecoveryHandling;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.Property.ERROR_MESSAGES;
import com.auto.solution.Common.RecoveryObjectsMapper;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.Common.Utility;


public class RecoverySupportForSeleniumDriver extends RecoverySupport {

	private WebDriver actionDriver = null;
	public RecoverySupportForSeleniumDriver(WebDriver seleniumDriver,ResourceManager rm) throws Exception{
		this.actionDriver = seleniumDriver;
		try{
		Properties recoveryInfoProperties =  this.locateAndGetRecoverySupportFile(Property.PROJECT_NAME,rm);
		this.readRecoverySupportFile(recoveryInfoProperties);
		}
		catch(Exception e){
		
		}
	}
	
	private boolean handleRecovery(String recoveryObjectName,String recoveryObjectStrategyToLocate,String recoveryObjectLocation,String recoveryActionToPerform) throws Exception {
		boolean isRecoveryPerformed =  false;
		try{
		if(recoveryObjectName.toLowerCase().contains("alert")){
			int alertTrial = Integer.parseInt(Property.RECOVERY_ALERT_TRIAL);
			boolean alertFound = true;
			while(alertTrial > 0 && alertFound!=false){
				alertTrial--;
				Alert alert = getRecoveryAlertObject(recoveryObjectStrategyToLocate, recoveryObjectLocation);
				if(alert == null){alertFound = false; continue;}
				performRecoveryActionOnTestObject(recoveryActionToPerform, alert);
				isRecoveryPerformed =  true;
			}
		}else{
			WebElement recoveryTestObject = getRecoveryTestObject(recoveryObjectStrategyToLocate, recoveryObjectLocation);
			if(recoveryTestObject == null){ 
				isRecoveryPerformed = false;
			}else{
				performRecoveryActionOnTestObject(recoveryActionToPerform, recoveryTestObject);
				isRecoveryPerformed =  true;
			}
		}
		}catch(Exception ex){
			throw ex;
		}
		return isRecoveryPerformed;
	}
	
	@Override
	public void doRecovery(RecoveryObjectsMapper objMapper) throws Exception {
		try{
		
			for(int recoveryObjectIndex = 0;recoveryObjectIndex < this.recoveryObjectNames.size();recoveryObjectIndex++){
			String recoveryObjectName = this.recoveryObjectNames.get(recoveryObjectIndex);
			String recoveryObjectStrategyToLocate = this.recoveryLocatingStrategiesForObject.get(recoveryObjectIndex);
			String recoveryObjectLocation = this.recoveryObjectLocations.get(recoveryObjectIndex);
			String recoveryActionToPerform = this.recoveryActionsToBeExcutedOnObject.get(recoveryObjectIndex);
			ArrayList<String> listOfTestObjectsToSkip = objMapper.getTestObjectNamesToSkipInRecovery();
			if(!listOfTestObjectsToSkip.contains(recoveryObjectName)){
			if(!handleRecovery(recoveryObjectName, recoveryObjectStrategyToLocate, recoveryObjectLocation, recoveryActionToPerform)){
				continue;
			}else{
				break;
			}
			}
			
		}
		}
		
		catch(Exception e){
			throw e;
		}
		
	}
	
	@Override
	public void doRecoveryForSpecialObjectsWithHigherPriority(RecoveryObjectsMapper objMapper) throws Exception {
		try{
			
			int objectSize = this.recoveryObjectNames.size();
			for(int recoveryObjectIndex = 0;recoveryObjectIndex < objectSize;recoveryObjectIndex++){
				String recoveryObjectName = this.recoveryObjectNames.get(recoveryObjectIndex);
				String recoveryObjectStrategyToLocate = this.recoveryLocatingStrategiesForObject.get(recoveryObjectIndex);
				String recoveryObjectLocation = this.recoveryObjectLocations.get(recoveryObjectIndex);
				String recoveryActionToPerform = this.recoveryActionsToBeExcutedOnObject.get(recoveryObjectIndex);
				String recoveryObjectPriority = this.recoveryObjectsPriorities.get(recoveryObjectIndex);
				ArrayList<String> listOfTestObjectsToSkip = objMapper.getTestObjectNamesToSkipInRecovery();
				if(!listOfTestObjectsToSkip.contains(recoveryObjectName)){
				if(recoveryObjectPriority.equalsIgnoreCase("high")){
					if(!handleRecovery(recoveryObjectName, recoveryObjectStrategyToLocate, recoveryObjectLocation, recoveryActionToPerform)){
					continue;
					}else{
					break;
					}
				}
			}
			}
			
		}
		catch(Exception e){
			throw e;
		}
		
		
	}
	
	private WebElement getRecoveryTestObject(String locatingStrategyForRecoveryObject,String locationOfRecoveryObject){
		List<WebElement> testElements = null;
		WebElement testElement = null;
		try{
			if(locatingStrategyForRecoveryObject.toLowerCase().contains("css")){
				testElements = this.actionDriver.findElements(By.cssSelector(locationOfRecoveryObject));
			}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("id")){
				testElements = this.actionDriver.findElements(By.id(locationOfRecoveryObject));
			}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("tag")){
				testElements = this.actionDriver.findElements(By.tagName(locationOfRecoveryObject));
			}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("class")){
				testElements = this.actionDriver.findElements(By.className(locationOfRecoveryObject));
				}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("name")){
				testElements = this.actionDriver.findElements(By.name(locationOfRecoveryObject));
			}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("xpath")){
				testElements = this.actionDriver.findElements(By.xpath(locationOfRecoveryObject));
			}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("text")){
				testElements = this.actionDriver.findElements(By.linkText(locationOfRecoveryObject));
			}
			
			for (WebElement testObject : testElements) {
				if(testObject.isDisplayed()){
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
	
	private Alert getRecoveryAlertObject(String locatingStrategyForRecoveryObject,String locationOfRecoveryObject){
		Alert alt = null;
		try{
			if(locatingStrategyForRecoveryObject.toLowerCase().contains("text")){
				alt = this.actionDriver.switchTo().alert();
				if(!alt.getText().contains(locationOfRecoveryObject)){
					alt=null;
				}
			}
		}catch(Exception ex){
			return null;
		}
		
		return alt;
	}
	
	private void performRecoveryActionOnTestObject(String actionNameToPerform,Object recoveryObject) throws Exception{
		try{
			if(actionNameToPerform.equalsIgnoreCase("click")){
				((WebElement) recoveryObject).click();
			}else if(actionNameToPerform.contains("alert-accept")){
					((Alert) recoveryObject).accept();
			}
			else{
				String errMessage = ERROR_MESSAGES.ER_IN_SPECIFYING_RECOVERY_ACTION.getErrorMessage().replace("{ACTION_NAME}", actionNameToPerform);
				throw new Exception(errMessage);
			}
		}catch(Exception ex){
			throw ex;
		}
		
	}



}
