package com.auto.solution.TestDrivers;

import java.util.HashMap;
import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.Common.Utility;
import com.auto.solution.Common.Property.ERROR_MESSAGES;

public class TestSimulator {

 	private HashMap<String, String> objDefInfo = new HashMap<String, String>();
 	
 	private String[] testDataContents = null;
 	
 	private  TestDrivers testSimulator = null;
 	
 	private ResourceManager rm;
 	
 	public TestSimulator(ResourceManager rmanager){
 		
 		this.rm = rmanager;
 		
 	}
 	
 	public void setTestObjectInfo(HashMap<String, String> currentTestObjectInfo){
 		this.objDefInfo = null;
 		
 		this.objDefInfo = currentTestObjectInfo;
 	}
 	
 	public void enableTestDriver(String testDriverKey){
 		
 		testSimulator = null;
 		
 		if(testDriverKey.contains(Property.DESKTOP_WEB_TESTDRIVER_KEYWORD)){
 			testSimulator = new DesktopWebTestDriverImpl(this.rm);
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
 		
 		TestObjectDetails objCurrentObjectDetails = new TestObjectDetails(this.objDefInfo);
 		
 		testSimulator.injectTestObjectDetail(objCurrentObjectDetails); // Setting up the object definition.
 		
 		Property.StepExecutionTime = Utility.getCurrentDateAndTimeInStringFormat();
 		
 		//If Ignore option is enabled then Ignore the test step.
 		if(Property.LIST_STRATEGY_KEYWORD.contains(Property.STRATEGY_KEYWORD.IGNORE_STEP.toString())){
 			Property.Remarks = Property.ERROR_MESSAGES.STEP_IGNORED.getErrorMessage();
 			Property.StepStatus =  Property.PASS;
 			return;
 		}
 		
 		try{
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
 			else if(stepAction.toLowerCase().equals("istextpresent")){
 				String textToVerify = "";
 				try{
 					textToVerify = this.testDataContents[0];		      
 					if(textToVerify.equals("")){
 						throw new Exception(Property.ERROR_MESSAGES.ER_MISSING_TESTDATA.getErrorMessage());
 					}
 				}
 			catch(ArrayIndexOutOfBoundsException ae){
 				throw new Exception(Property.ERROR_MESSAGES.ER_MISSING_TESTDATA.getErrorMessage());
 			}
 								
 				testSimulator.isTextPresent(textToVerify);
 			}
 			else if(stepAction.toLowerCase().equals("click")){
 				testSimulator.click();
 			}
 			else if(stepAction.toLowerCase().equals("sendkey") ){
 				String textToType = "";
 				try{
 					textToType = this.testDataContents[0];					      
 				}
 			catch(ArrayIndexOutOfBoundsException ae){
 				throw new Exception(Property.ERROR_MESSAGES.ER_MISSING_TESTDATA.getErrorMessage());
 			}
 				Utility.setKeyValueToGlobalVarMap(testObject, textToType);
 				
 				testSimulator.sendKey(textToType);	
 			}
 			else if(stepAction.toLowerCase().equals("shutdown")){
 				testSimulator.shutdown();
 			}
 			else if(stepAction.toLowerCase().equals("isobjectthere")){
 				testSimulator.isObjectThere();
 			}
 			else if(stepAction.equalsIgnoreCase("sleep")){
 				long timeToSleepInMilliSeconds;
 				try{
 				timeToSleepInMilliSeconds = Long.parseLong(testDataContents[0]);}
 				catch(Exception e){
 					throw new Exception(ERROR_MESSAGES.ER_SPECIFYING_TESTDATA.getErrorMessage() + "--" +   e.getMessage());
 				}
 				testSimulator.sleep(timeToSleepInMilliSeconds);
 			}
 			else if(stepAction.equalsIgnoreCase("assertoninputvalue")){
 				if(testDataContents.length != 1){
 					throw new Exception(ERROR_MESSAGES.ER_SPECIFYING_TESTDATA.getErrorMessage());
 				}
 				else{
 					boolean bFlag ;
 					bFlag = Utility.assertOnInputValue(testDataContents[0]);
 					if(!bFlag){
 						String errMessage = ERROR_MESSAGES.ERR_TESTDATA_MATCH.getErrorMessage().replace("{ACTUAL_STRING}", testDataContents[0]);					
 						throw new Exception(errMessage);
 					}
 				}	
 			}
 			else if(stepAction.equalsIgnoreCase("setvariable")){
 				if(testDataContents.length != 2){
 					throw new Exception(ERROR_MESSAGES.ER_SPECIFYING_TESTDATA.getErrorMessage());
 				}
 				String key = testDataContents[0];
 				String value = testDataContents[1];
 				Utility.setKeyValueToGlobalVarMap(key, value);
 			}
 			else{
 				throw new NoSuchMethodException(Property.ERROR_MESSAGES.ER_NO_STEP_ACTION.getErrorMessage());
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
 		this.takeSnapshots();
 	}
 	
 	private void takeSnapshots(){
 		String snapShotName = "";
 		if(Property.DEBUG_MODE.contains("off") && Property.DEBUG_MODE.contains("strict")){ 	}
 		
 		else if(!Property.DEBUG_MODE.contains("on") && Property.StepStatus == Property.FAIL ){snapShotName = testSimulator.saveSnapshotAndHighlightTarget(false);}
 		
 		else if(Property.DEBUG_MODE.contains("on")){
 			snapShotName = testSimulator.saveSnapshotAndHighlightTarget(true);
 		}
 		Property.StepSnapShot = snapShotName;
 	}
}
