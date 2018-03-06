package com.auto.solution.TestDrivers;

import java.net.ProxySelector;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.saxon.functions.Replace;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;
import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.support.http.SoapUIMultiThreadedHttpConnectionManager;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestRunner.Status;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestSuite;

public class InvokeAPI extends Thread{
	private String project_name = "";
	
	ResourceManager resource_manager = null;
	
	private String soapui_testSuite_name = "";
	
	private String soapui_testCase_name = "";
	
	private TestCase soapuiTest = null;
	
	private HashMap<String,String> mapOfSoapUIProperties = new HashMap<String, String>();
	
	private List<String> listOfApisTestCaseStatus = new ArrayList<String>();
	
	InvokeAPI(ResourceManager rManager) {
		this.resource_manager = rManager;
	}
	
	public void run(){
		
		List<String> testCaseStatusWithReason = new ArrayList<String>();
		try{		
		
		TestCase soapui_testcase = this.accessSoapUIProject();
		
		PropertiesMap mapOfProperties = new PropertiesMap();
			
		if(this.mapOfSoapUIProperties != null)
			{
			for (String property_key : this.mapOfSoapUIProperties.keySet()) {
				property_key = URLDecoder.decode(property_key, "UTF-8");
				String property_value = this.mapOfSoapUIProperties.get(property_key);
				soapui_testcase.setPropertyValue(property_key, property_value);
			}
		}
			
		TestCaseRunner testRunner = soapui_testcase.run(mapOfProperties, false);
		
		Status status = testRunner.getStatus();
		
		String reason = testRunner.getReason();
		String testStep_msg="";
		int msg_index=0;
		List<TestStepResult> testResults= testRunner.getResults();
		for (TestStepResult testStepResult : testResults) {
			String[] msg = testStepResult.getMessages();
			if(msg.length>0 && msg!=null)
			{
			testStep_msg=testStep_msg.concat(msg[msg_index++]);
			reason=reason.concat(testStep_msg);
		}
		}
		
		testCaseStatusWithReason.add(status.toString());
		
		testCaseStatusWithReason.add(reason);
		
			
		ProxySelector.setDefault(resource_manager.getdefaultproxy());
			
		this.listOfApisTestCaseStatus =  testCaseStatusWithReason;
		 
		soapuiTest = soapui_testcase;
		}
		catch(Exception e){
			testCaseStatusWithReason.add("FAILED");
			testCaseStatusWithReason.add(e.getMessage());
		}
	}
	
	public HashMap<String, String> getTestCaseProperty(List<String> properties){
		mapOfSoapUIProperties = new HashMap<String, String>();
		for (String prop : properties) {
			mapOfSoapUIProperties.put(prop,soapuiTest.getPropertyValue(prop));
		}
		return mapOfSoapUIProperties;
	}
	
	public List<String> getListOfApisTestCaseStatus(){
		return this.listOfApisTestCaseStatus;
	}
	
	private TestCase accessSoapUIProject() throws Exception{
		try{
			System.setProperty("soapui.log4j.config", resource_manager.getSoapUiLogConfig());
			String soapuiProject = resource_manager.getLocationForExternalFilesInResources().replace("{PROJECT_NAME}", Property.PROJECT_NAME).replace("{EXTERNAL_FILE_NAME}", this.project_name + ".xml");
			
			WsdlProject soapui_project = new WsdlProject(soapuiProject);
			
			List<TestSuite> testSuitesInProject = soapui_project.getTestSuiteList();
			
			TestSuite first_testsuite = testSuitesInProject.get(0);
			
			soapui_testCase_name = first_testsuite.getName();
			
			List<TestCase> testCasesInTestSuite = first_testsuite.getTestCaseList(); 
			
			TestCase first_testcase = testCasesInTestSuite.get(0);
			
			soapui_testCase_name = first_testcase.getName();
			
			return first_testcase;
		}
		catch(Exception e){
			String errMessage = Property.ERROR_MESSAGES.ERR_ACCESSING_SOAP_PROJECT.getErrorMessage() + ". " + e.getMessage();
			throw new Exception(errMessage);
		}
	}
	
	public void setAPIProjectReference(String projectname,HashMap<String,String> propertiesMap){
		this.project_name = projectname;
		this.mapOfSoapUIProperties = propertiesMap;
	}	

}
