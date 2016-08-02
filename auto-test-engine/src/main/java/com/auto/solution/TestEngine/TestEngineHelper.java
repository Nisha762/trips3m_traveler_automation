package com.auto.solution.TestEngine;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.Utility;

public class TestEngineHelper {
	
	void setConfigurationInputsToSharedObject(){
		
		try{
		Property.OSString = System.getProperty("os.name") + " " + System.getProperty("os.version");;
		
		Property.PROJECT_NAME = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("project.name"));
		
		Property.BrowserName = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("drivercapability.browserName"));
		
		Property.SyncTimeOut = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("MaximumTimeout"));
		
		Property.IsRemoteExecution = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("execution.remote"));
		
		Property.IsSauceLabExecution = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("execution.saucelab") == null ? "false" : Utility.getValueForKeyFromGlobalVarMap("execution.saucelab") );
		
		Property.ApplicationURL = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("application.url"));
		
		Property.RemoteURL = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("execution.remote.url"));
		
		Property.PROJECT_TESTMANAGEMENT_TOOL = Utility.getValueForKeyFromGlobalVarMap("project.testmanagement").trim().toLowerCase();
		
		Property.EXECUTION_TEST_DRIVER = Utility.getValueForKeyFromGlobalVarMap("execution.test.driver").trim().toLowerCase();
		
		Property.DEBUG_MODE = Utility.getValueForKeyFromGlobalVarMap("execution.debugmode").trim().toLowerCase();
		
		Property.TEST_EXECUTION_GROUP  = Utility.getValueForKeyFromGlobalVarMap("execution.group").trim().toLowerCase();
		}
		catch(Exception e){
		
		}
	}
}
