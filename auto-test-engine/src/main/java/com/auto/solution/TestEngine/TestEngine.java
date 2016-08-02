package com.auto.solution.TestEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.time.StopWatch;
import org.dbunit.dataset.ITable;

import com.auto.solution.Common.*;
import com.auto.solution.Common.Property.ERROR_MESSAGES;
import com.auto.solution.TestInterpretor.CompilerFactory;
import com.auto.solution.TestInterpretor.ICompiler;
import com.auto.solution.TestManager.*;


public class TestEngine {
	
	private TestEngineHelper engineHelper;
	
	private Utility utils;
		
	private Boolean IsAnyTestStepFailedDuringExecution = false;
	
	ITable TestORSet = null;
	
	private TestManagerFactory testManagerFactoryInstance = null;
	
	private ITestManager testManager = null;
	
	public String StepAction;
    
    public ResourceManager rManager;
    
    public static ArrayList<String> TestCaseIDsForExecution = new ArrayList<String>();
    
    boolean IsWriteStep = false;

    private ArrayList<ArrayList<String>> testStepsDetailsForATestCase = new ArrayList<ArrayList<String>>();
    
    private HashMap<String, ArrayList<ArrayList<String>>> testCasesWithTestStepDetails = new HashMap<String, ArrayList<ArrayList<String>>>();
    
    public TestEngine(TestEngineHelper engineHelper) {
		this.engineHelper = engineHelper;
	}
    
    private void ExecuteTestCase(String TestCaseIDToExecute,String testScenarioForTestCase, ArrayList<Integer> testIterationValueForTestCase,boolean isTestCaseInvockedAsSubTestCase) throws Exception{
    	
    	try{
		 
    		ITestManager testExecutionManager = testManagerFactoryInstance.getTestManager(false);
    		
    		testExecutionManager.locateRepositories(testScenarioForTestCase);
						
    		testExecutionManager.connectRepositories();
							
			List<String> listOfTestStepInTestCase = testExecutionManager.getTestStepsForTestCase(TestCaseIDToExecute);			
			
			CompilerFactory factoryInstanceToGetCompilerForTestStep = new CompilerFactory(Property.STRATEGY_TO_USE_COMPILER,rManager);
			
			ICompiler testStepCompiler = factoryInstanceToGetCompilerForTestStep.getCompiler();
			
			
			
			for (String currentTestStep : listOfTestStepInTestCase) {
				
				Property.TEST_STEP_ID = 
				
				Property.StepDescription = currentTestStep;;
				
				Property.Remarks = "";
				
				Property.StepStatus = "";
				
				testStepCompiler.setStepDefenitionToCompile(currentTestStep);
				
				String testObjectToBeUsedForTestStep = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testStepCompiler.getObjectDefenition());
				
				String testStepAction = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testStepCompiler.getStepAction());
				
				String testData = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testStepCompiler.getTestData());
				
				String  subTestCaseInvocked = testStepCompiler.getSubTestCaseInvockedInTestStep();				
				
				if(subTestCaseInvocked != ""){
					
					String testSuiteForSubTestCase = "";
					
					String subTestCaseID = "";
					
					ArrayList<Integer> listOfIterationsForSubTestCaseInvocked = new ArrayList<Integer>();
					
					try{
					//TODO : Add logic of adding the test scenario of the parent test case if not mentioned.
						
						String[] parsedTestCaseDetailsInCurrentTestStep = subTestCaseInvocked.split(":");
						
						testSuiteForSubTestCase = parsedTestCaseDetailsInCurrentTestStep[0].trim();
						
						subTestCaseID = parsedTestCaseDetailsInCurrentTestStep[1].trim();
												
					}
					catch(ArrayIndexOutOfBoundsException ae){
						throw new Exception(ERROR_MESSAGES.ER_SPECIFYING_TESTCASE_TO_INVOKE.getErrorMessage());
					}
					
					testStepAction = Property.INTERNAL_TESTSTEP_KEYWORD;
					
					// If reusable test case need to be repeated.
					if(listOfIterationsForSubTestCaseInvocked.size() > 1){
						
						for(int iteration : listOfIterationsForSubTestCaseInvocked){
							
							Utility.setKeyValueToGlobalVarMap("iteration", String.valueOf(iteration));
							ArrayList<Integer> iterationListForThisRepeatition = new ArrayList<Integer>();
							iterationListForThisRepeatition.add(iteration);							
							ExecuteTestCase(subTestCaseID, testSuiteForSubTestCase,iterationListForThisRepeatition,true);
						}
					}					
					else{
							ExecuteTestCase(subTestCaseID, testSuiteForSubTestCase,listOfIterationsForSubTestCaseInvocked,true);
					}
					
				}
				
				if(Property.StepStatus == Property.FAIL) { 
					IsAnyTestStepFailedDuringExecution = true; 
					}				
				//Prepare step execution detail list.
				
				ArrayList<String> lstTestStepExecutionDetails = new ArrayList<String>();
				
				lstTestStepExecutionDetails.add(Property.StepStatus);
				
				lstTestStepExecutionDetails.add(testObjectToBeUsedForTestStep);
				
				lstTestStepExecutionDetails.add(testData);
				
				lstTestStepExecutionDetails.add(testStepAction);
				
				lstTestStepExecutionDetails.add(Property.Remarks);
				
				lstTestStepExecutionDetails.add(Property.StepDescription);
		
				testStepsDetailsForATestCase.add(lstTestStepExecutionDetails);				
				
				if(Property.LIST_STRATEGY_KEYWORD.contains(Property.STRATEGY_KEYWORD.CRITICAL.toString()) && Property.StepStatus.equals(Property.FAIL)){
					return;
				}
			}
		}
		catch(NullPointerException e){
		    throw e;
			//throw new NullPointerException("Execution has unexpectedly broken.");
		}
		catch(Exception e){
			throw e;
		}
	}

    public void prepareTestEngineForExecution(String projectBasePath){
		try{
			
			rManager = new ResourceManager(projectBasePath);
			
			utils = new Utility(rManager);
			
			utils.loadPropertiesDefinedForExecution();
			
			Utility.populateGlobalVarMapWithPropertiesDefinedInPropertiesFile();
			
			Properties systemProperties = System.getProperties();
			
			Utility.storeSystemPropertiesToGlobalVarMap(systemProperties);
			
			Utility.showAllTestEnginePropertiesOnConsole();
			
			IsWriteStep = true;	
			
			engineHelper.setConfigurationInputsToSharedObject();
		}
		catch (Exception e) {
			//loggerForTestExecution.logMessageConsole(e.getMessage());
		}
	}
	
	public void initiateExecution() throws Exception {
		
		try{
			
		String testManagerToolDefinedByUser = Property.PROJECT_TESTMANAGEMENT_TOOL;
		
		testManagerFactoryInstance = new TestManagerFactory(testManagerToolDefinedByUser,rManager);
		
		testManager = testManagerFactoryInstance.getTestManager(true);
		
		HashMap<String, Set<String>> filteredTestExecutionHierarchy = testManager.getTestSuiteAndTestCaseHierarchyForExecution();
		
		List<String> testScenarioListInExecutionGroup = new ArrayList<String>();			
		
		 
		testScenarioListInExecutionGroup.addAll(filteredTestExecutionHierarchy.keySet());
				 
		if(testScenarioListInExecutionGroup.size() == 0){
			  
				 ERROR_MESSAGES.NO_TEST_SCENARIOS_In_TEST_GROUP.getErrorMessage().replace("{TEST_GROUP}", Property.TEST_EXECUTION_GROUP);
			  
		}
		  Property.ExecutionStartTime = Utility.getCurrentDateAndTimeInStringFormat();
		  
		  for (String testScenarioToExecute : testScenarioListInExecutionGroup) {
					
			Property.CURRENT_TESTSUITE = testScenarioToExecute;
			
			
			
			Set<String> listOfTestCasesInTestSuite = filteredTestExecutionHierarchy.get(testScenarioToExecute);
			
			if(listOfTestCasesInTestSuite == null){
				
			
				IsAnyTestStepFailedDuringExecution = true;
				
				continue;				
			}			
		
			StopWatch watchTestSuite = Utility.getAndStartStopWatch();
			
			for (String testCase : listOfTestCasesInTestSuite) {
				
				String CurrentTestCaseID = testCase;
				
				Property.CURRENT_TESTCASE = CurrentTestCaseID;
				
				//loggerForTestExecution.logMessageConsole("Execution start for " + CurrentTestCaseID);
				
				testStepsDetailsForATestCase = new ArrayList<ArrayList<String>>();
				
				StopWatch stopwatch = Utility.getAndStartStopWatch();
				
				ExecuteTestCase(CurrentTestCaseID, testScenarioToExecute,null,false);
				
				String timeTakenByTestCaseInSeconds = Utility.haltAndGetTotalTimeRecordedInStopWatch(stopwatch);
				
				Property.mapOfTestCasesAndTimeTakenByThem.put(CurrentTestCaseID, timeTakenByTestCaseInSeconds);
				
				testCasesWithTestStepDetails.put(CurrentTestCaseID, testStepsDetailsForATestCase);
				
				
				
				//loggerForTestExecution.logMessageConsole("Execution ends for " + CurrentTestCaseID);
			}	
			String timeTakenByTestSute = Utility.haltAndGetTotalTimeRecordedInStopWatch(watchTestSuite);
			
			Property.mapOfTestSuitesAndTimeTakenByThem.put(testScenarioToExecute, timeTakenByTestSute);
		}
		
		Property.ExecutionEndTime = Utility.getCurrentDateAndTimeInStringFormat();
		
		}
		catch(Exception e){
			IsAnyTestStepFailedDuringExecution = true;
			throw e;			
		}
	}	
	
	public boolean isAnyTestStepFailedDuringTestExecution(){
		return this.IsAnyTestStepFailedDuringExecution;
	}
}
