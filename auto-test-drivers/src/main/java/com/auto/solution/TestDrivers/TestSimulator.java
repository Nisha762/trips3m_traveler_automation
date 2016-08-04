package com.auto.solution.TestDrivers;


import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.Common.Utility;

public class TestSimulator {
	
 	private String[] testDataContents = null;
 	
 	private  TestDrivers testSimulator = null;
 	
 	private ResourceManager rm;
 	
 	public TestSimulator(ResourceManager rmanager){
 		this.rm = rmanager;
 	}
 	
 	public void enableTestDriver(String testDriverKey){
 		
 		testSimulator = null;
 		
 		if(testDriverKey.contains(Property.DESKTOP_WEB_TESTDRIVER_KEYWORD)){
 			testSimulator = new DesktopWebTestDriverImpl(this.rm);
 	    }else if(testDriverKey.contains(Property.MOBILE_APP_ANDRIOD_TESTDRIVER_KEYWORD)){
 	    	
 	    }
 		
 	}
 	
 	private void prepareTestData(String testData){
 		this.testDataContents = null;
 		this.testDataContents = testData.split(Property.TESTDATA_SEPERATOR);			
 	}
 	
 	
 	public void simulateTestStep(String stepAction,String testData,String testObject, String strategyModifier,boolean isReusableTestKeyword) throws Exception{
 		
 		if(isReusableTestKeyword){
 			return;
 		}
 		Property.CURRENT_TESTSTEP = stepAction;
 		
 		prepareTestData(testData);
 		
 		
 		Property.StepExecutionTime = Utility.getCurrentDateAndTimeInStringFormat();
 		
 	
 		if(Property.LIST_STRATEGY_KEYWORD.contains(Property.STRATEGY_KEYWORD.IGNORE_STEP.toString())){
 			Property.Remarks = Property.ERROR_MESSAGES.STEP_IGNORED.getErrorMessage();
 			Property.StepStatus =  Property.PASS;
 			return;
 		}
 		
 		try{
 		///////////////////////////////////
 			if(stepAction.toLowerCase().equals("initializeapp")){
 				
 				//TODO : Replace the STep description with actual test data used. 
 					String URL = "";
 					try{
 						URL = this.testDataContents[0];
 						if(URL.equals("")){
 							URL = Property.ApplicationURL;
 						}
 					}
 				catch(ArrayIndexOutOfBoundsException ae){
 					throw new Exception(Property.ERROR_MESSAGES.ER_MISSING_TESTDATA.getErrorMessage());
 				}
 					testSimulator.initializeApp(URL);
 			}	
 			 Property.StepStatus = Property.PASS;
 		}
 		catch(Exception e){
 			if(Property.LIST_STRATEGY_KEYWORD.contains(Property.STRATEGY_KEYWORD.OPTIONAL.toString())){
 				Property.StepStatus = Property.PASS;
 				Property.Remarks = Property.ERROR_MESSAGES.STEP_MARKED_OPTIONAL.getErrorMessage();
 			}
 			else{
 			Property.Remarks = e.getMessage();
 			Property.StepStatus = Property.FAIL;
 			}
 		}
 	}
}
