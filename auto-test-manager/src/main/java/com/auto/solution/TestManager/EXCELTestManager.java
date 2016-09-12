/**
 * 
 */
package com.auto.solution.TestManager;


import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.excel.XlsDataSet;

import com.auto.solution.Common.*;
import com.auto.solution.Common.Property.ERROR_MESSAGES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.io.*;

public class EXCELTestManager extends TestManagerUtils implements ITestManager {

	private String testCaseRepository;
	
	private String testDataRepository;
	
	private String objectRepository;
	
	private String testCaseRepositorySheetName;
	
	private String testDataRepositorySheetName;
	
	private String objectRepositorySheetName;
	
	private ITable testCaseTable = null;
	
	private ITable testDataTable = null;
	
	private ITable objectRepoTable = null;
	
	private HashMap<String, Set<String>> mapOfTestSuiteAndTheirTestCases = new HashMap<String, Set<String>>();
	
	private HashMap<String, List<String>> mapOfTestGroupsAndTheirTestSuites = new HashMap<String, List<String>>();
	
	private HashMap<String, List<String>> mapOfTestGroupsAndTheirTestCases = new HashMap<String, List<String>>();
	
	private List<String> listOfTestGroupSelectedByUser = new ArrayList<String>();	
	
	private ResourceManager rm;
	
	/**
	 * Public Constructor which sets the test suite to locate. 
	 */
	public EXCELTestManager(ResourceManager rm) throws Exception{
		testCaseRepositorySheetName = Property.TestCaseSheet;
		testDataRepositorySheetName = Property.TestDataSheetName;
		objectRepositorySheetName = Property.ObjectRepositorySheetName;
		this.rm = rm;
		try{
			accessLocalTestData(rm.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}", "").replace("{PROJECT_NAME}", Property.PROJECT_NAME));
		}catch (Exception ex) {
			throw new Exception(Property.ERROR_MESSAGES.ER_CONNECTING_EXTERNALFILE.getErrorMessage());
		}
	}

	private void accessLocalTestData(String targetLocation) throws Exception{
		try{
			File[] propertyFiles = connectToStaticTestDataProperties(targetLocation);
			if(propertyFiles != null)
				setAllKeysInLocalTestDataToGlobalVarMap(propertyFiles);
		}
		catch(Exception e){
			String errMessage = Property.ERROR_MESSAGES.ERR_WHILE_PROCESSING_LOCALTESTDATA.getErrorMessage();
			throw new Exception(errMessage + e.getMessage());
		}
	}
	
	private Properties getProjectTestPlans() throws Exception{
		Properties groupPropertyFile = new Properties();
		try { 
		  groupPropertyFile.load(new FileInputStream(rm.getTestGroupPropertyFileLocationForFileSystem().replace("{PROJECT_NAME}", Property.PROJECT_NAME)));
		}catch(Exception ex){
			throw ex;
		}
		return groupPropertyFile;
	}
	
	@Override
	public void locateRepositories(String testSuiteName) {
		testCaseRepository = rm.getTestSuiteLocationInFileSystem().replace("{PROJECT_NAME}", Property.PROJECT_NAME);
		testCaseRepository = testCaseRepository + testSuiteName + ".xls";
		testDataRepository = testCaseRepository;
		objectRepository = rm.getObjectRepositoryFileLocation().replace("{PROJECT_NAME}", Property.PROJECT_NAME);		
		

	}

	@Override
	public void connectRepositories() throws Exception  {
		
		//connect to test case repository.
		try {
			testCaseTable = null;
			testDataTable = null;
			objectRepoTable = null;
			testCaseTable = connectRepository(testCaseRepository, testCaseRepositorySheetName);
			testDataTable = connectRepository(testDataRepository, testDataRepositorySheetName);
			objectRepoTable = connectRepository(objectRepository, objectRepositorySheetName);
		} 
		catch (Exception e) {
			throw e;
		}
		
	}
	
	/**
	 * 
	 * @param Keyword  -  Specify the Keyword to fetch the data in following format: <br> <b>"rownumber:keyword".</b>
	 * @return String data value.
	 */
	@Override
	public String fetchObjectRepositoryContent( String Keyword) {
		
		String datavalue = "";
		try{
		String[] contents = Keyword.split(":");
		
		int row = Integer.parseInt(contents[0]);
		
		datavalue = objectRepoTable.getValue(row, contents[1]).toString();				
		}
		catch(Exception e){
			return "";
		}
		return datavalue;
	}

	/**
	 * 
	 * @param Keyword -  Specify the Keyword to fetch the data in following format: <br> <b>"rownumber:keyword".</b>
	 *  @return String data value.
	 */
	@Override
	public String fetchTestDataRepositoryContent(String Keyword) {
		String datavalue = "";
		try{
		String[] contents = Keyword.split(":");
		
		int row = Integer.parseInt(contents[0]);
		
		String columnName = contents[1].toString();
		
		datavalue = testDataTable.getValue(row-1, columnName).toString();				
		}
		catch(Exception e){
			return "";
		}
		return datavalue;
	}

	/**
	 * 
	 * @param Keyword -  Specify the Keyword to fetch the data in following format: <br> <b>"rownumber:keyword".</b>
	 *  @return String data value.
	 */
	@Override
	public String fetchTestCaseRepositoryContent(String Keyword) {
		
		String datavalue = "";
		try{
		String[] contents = Keyword.split(":");
		
		int row = Integer.parseInt(contents[0]);
		
		datavalue = testCaseTable.getValue(row, contents[1]).toString();				
		}
		catch(Exception e){
			return "";
		}
		return datavalue;
	}	
		
 private ITable connectRepository(String repository,String worksheet) throws Exception{		 
		 
		 ITable objSheet = null;
			try {
				File inFile = new File(repository);
				XlsDataSet ds = new XlsDataSet(inFile);
				objSheet = ds.getTable(worksheet);
			    	
			} 
			catch(DataSetException de){
				throw new DataSetException(ERROR_MESSAGES.ER_CONNECTING_REPOSITORIES.getErrorMessage().replace("{REPOSITORY}", repository));
			}
			catch (Exception e) {
				throw e;
			}
		 return objSheet;
	 }

 @Override
	public HashMap<String, String> getActualObjectDefination(
			String logicalNameOfTheObject) throws Exception {
		HashMap<String,String> objDef = new HashMap<String, String>();
		try{
			if(objectRepoTable == null){
				throw new Exception("Locate Object repository to access its data!");
			}
			
			int rowCount = objectRepoTable.getRowCount();
			
			Integer iterativeRow = 0;
			if(logicalNameOfTheObject != ""){	
				
				while(iterativeRow < rowCount){			
				if(fetchObjectRepositoryContent(iterativeRow.toString() + ":" + Property.TESTOBJECT_KEYWORD_IN_ObjectRepository).equals(logicalNameOfTheObject)){
					
					String locatingStrategy = fetchObjectRepositoryContent(iterativeRow.toString() + ":" + Property.Locating_Strategy_Keyword );
					
					String locationOfObject = fetchObjectRepositoryContent(iterativeRow.toString() + ":" + Property.Locating_Value_Keyword_In_OR);
					
					String inFrame = fetchObjectRepositoryContent(iterativeRow.toString() + ":" + Property.TestObject_InFrame_Keyword);
					
					String testObjectFilter = fetchObjectRepositoryContent(iterativeRow.toString() + ":" + Property.TestObject_Filter_Keyword);
					
					objDef.put(Property.TESTOBJECT_KEYWORD_IN_ObjectRepository, logicalNameOfTheObject);
					
					objDef.put(Property.Locating_Strategy_Keyword, locatingStrategy);
					
					objDef.put(Property.Locating_Value_Keyword_In_OR, locationOfObject);
					
					objDef.put(Property.TestObject_InFrame_Keyword, inFrame);
					
					objDef.put(Property.TestObject_Filter_Keyword,testObjectFilter);
					
					break;
				}
				iterativeRow++;
			}
			}
		}
		catch(Exception e){
			throw new Exception(e.getMessage());
		}	
		return objDef;
	}

/**
 * @param testCaseID : String Test Case ID.
 * @return List of Test Step in Test Case.
 * @throws Exception 
 */
@Override
public List<String> getTestStepsForTestCase(String testCaseID) throws Exception {
	List<String> lstTestStep = new ArrayList<String>();
	try{
		
		int rowIndex = 0;
		
		boolean isTestCaseFound = false; //Flag to handle the logic to get the steps for particular test case.
		
		int rowCount = testCaseTable.getRowCount();
		
		String fetchedTestCaseId = "";
		
		//Iterate through each row.
		while(rowIndex < rowCount){
			
			String testStepDefinition = "";
			
			try{
			
				//Get the value of testCaseId. It can be a blank value || Null Value || Any other testCaseID.				
				fetchedTestCaseId = fetchTestCaseRepositoryContent(rowIndex + ":" + Property.TESTCASE_ID_KEYWORD);}
			
			catch(NullPointerException ne){
				fetchedTestCaseId = "";
			}
			
			//If i got my test case id then i found the correct test case.
			if(fetchedTestCaseId.equalsIgnoreCase(testCaseID)){
				
				isTestCaseFound = true;
			}
				if(isTestCaseFound){
					
					if(fetchedTestCaseId != "" && !fetchedTestCaseId.equals(testCaseID)){
						isTestCaseFound = false;
						break;
					}
					else{
						String testStep = fetchTestCaseRepositoryContent(rowIndex + ":" + Property.TESTSTEP_KEYWORD);
						
						String Options = fetchTestCaseRepositoryContent(rowIndex + ":" + Property.OPTION_KEYWORD);
						
						testStepDefinition = testStep + Options;
						
						lstTestStep.add(testStepDefinition);
					}
				}
					rowIndex ++;	
		}
	}
	catch(Exception e){
		throw e;
	}
	return lstTestStep;
}

@Override
public List<String> getTestGroupsForExecution() throws Exception {
	return this.listOfTestGroupSelectedByUser;
}

@Override
public HashMap<String, Set<String>> getTestSuiteAndTestCaseHierarchyForExecution()
		throws Exception {
	HashMap<String, Set<String>> testSuiteAndTestCaseDetails = new HashMap<String, Set<String>>();
	
	HashMap<String, List<String>> testGroupAndTestSuiteDetails = new HashMap<String, List<String>>();
	
	HashMap<String, List<String>> testGroupAndTestCaseDetails = new HashMap<String, List<String>>();

	try{
		String[]  groupMentionedByUser = Utility.getValueForKeyFromGlobalVarMap("execution.group").toLowerCase().split(",");
		List<String> groupMentionedByUserAsList = Arrays.asList(groupMentionedByUser);
		
		Set<Object> setOfKeysInPropertyFile = this.getProjectTestPlans().keySet();
		
		if(groupMentionedByUser.length < 1 || (groupMentionedByUser.length == 1 && groupMentionedByUser[0] == "")){			
			groupMentionedByUserAsList.clear();
			for(Object group:setOfKeysInPropertyFile){
				String testGroupName = group.toString().toLowerCase();
				groupMentionedByUserAsList.add(testGroupName);
			}
		}	
		this.listOfTestGroupSelectedByUser.addAll(groupMentionedByUserAsList);
		for (String testGroup : groupMentionedByUserAsList) {
			String [] testCasesForCurrentTestPlan = this.getProjectTestPlans().getProperty(testGroup).split(",");
			ArrayList<String> testSuiteListForCurrentGroup = new ArrayList<String>();
			List<String> testCaseListInGroup = new ArrayList<String>();
			for (String testCase : testCasesForCurrentTestPlan) {
				testCaseListInGroup.add(testCase);
				String testSuiteName = testCase.split("_")[0];
				
				if(testSuiteAndTestCaseDetails.containsKey(testSuiteName)){
					Set<String> existingTestCaseList = testSuiteAndTestCaseDetails.get(testSuiteName);	
					existingTestCaseList.add(testCase);
					testSuiteAndTestCaseDetails.put(testSuiteName, existingTestCaseList);
				}
				else{
					Set<String> testCaseList = new HashSet<String>();
					testCaseList.add(testCase);
					testSuiteAndTestCaseDetails.put(testSuiteName, testCaseList);
				}
				if(!testSuiteListForCurrentGroup.contains(testSuiteName)){
					testSuiteListForCurrentGroup.add(testSuiteName);
				}
			}
			testGroupAndTestCaseDetails.put(testGroup, testCaseListInGroup);
			testGroupAndTestSuiteDetails.put(testGroup, testSuiteListForCurrentGroup);
		}
		
	}catch(Exception e){
		throw new Exception(e.getMessage());
	}
	this.mapOfTestGroupsAndTheirTestSuites = testGroupAndTestSuiteDetails;
	
	this.mapOfTestGroupsAndTheirTestCases = testGroupAndTestCaseDetails;
	
	this.mapOfTestSuiteAndTheirTestCases = testSuiteAndTestCaseDetails;
	
	return testSuiteAndTestCaseDetails;
}

public HashMap<String, List<String>> exposeTestsHierarchy() throws Exception {
	
	HashMap<String, List<String>> testHierarchy = new HashMap<String, List<String>>();
	
	
	try{
	String testSuiteLocation = rm.getTestSuiteLocationInFileSystem().replace("{PROJECT_NAME}", Property.PROJECT_NAME);
		File testSuiteDirectory = new File(testSuiteLocation); 
	
	File[] testSuitesFile = testSuiteDirectory.listFiles();
	
	for (File file : testSuitesFile) {		
		
		String testSuiteName = file.getName().replaceFirst("[.][^.]+$", "");
		
		String testSuiteFile = file.getName();
		
		ITable testSuiteTable = connectRepository(testSuiteLocation + Property.FileSeperator + testSuiteFile, testCaseRepositorySheetName);
		
		this.testCaseTable = testSuiteTable;
		
		ArrayList<String> testCaseIds = new ArrayList<String>();
		int rowIndex = 0;
		
		int rowCount = testSuiteTable.getRowCount();
		
		while(rowIndex < rowCount){
			String testCaseID = fetchTestCaseRepositoryContent(rowIndex + ":" + Property.TESTCASE_ID_KEYWORD);
			if(testCaseID != null && testCaseID !=""){
				testCaseIds.add(testCaseID);
			}
			rowIndex++;
		}
		testHierarchy.put(testSuiteName, testCaseIds);
	}
	
	}
	catch(SecurityException se){
		throw se;
	}
	catch(Exception e){
		throw e;
	}
	return testHierarchy;
}

@Override
public HashMap<String, HashMap<String, Set<String>>> prepareAndGetCompleteTestHierarchy()
		throws Exception {
	try{
	HashMap<String, List<String>> testGroupAndTestCases = mapOfTestGroupsAndTheirTestCases;
	
	HashMap<String, List<String>> testGroupAndTestSuites = mapOfTestGroupsAndTheirTestSuites;
	
	HashMap<String, HashMap<String, Set<String>>> testGroupTestSuiteAndTestCasesDetails = new HashMap<String, HashMap<String,Set<String>>>();
	
	for (String testGroup : testGroupAndTestSuites.keySet()) {
		
		HashMap<String,Set<String>> testSuiteAndTestCases = new HashMap<String, Set<String>>();
		
		testSuiteAndTestCases = this.mapOfTestSuiteAndTheirTestCases;
		
		List<String> testSuitesInTestGroup = new ArrayList<String>();
		
		testSuitesInTestGroup = testGroupAndTestSuites.get(testGroup);
		
		HashMap<String, Set<String>> testSuiteAndTestCasesForThisGroup = new HashMap<String, Set<String>>();
		
		for (String testSuite : testSuitesInTestGroup) {
			
			HashMap<String,Set<String>> testSuiteDetails = new HashMap<String, Set<String>>(testSuiteAndTestCases);
			
			Set<String> testCasesForCurrentTestSuite = new HashSet<String>();
			
			Set<String> testCasesForCurrentGroup = new HashSet<String>(testGroupAndTestCases.get(testGroup));
			
			for(String testCase : testSuiteDetails.get(testSuite)){
			
				if(testCasesForCurrentGroup.contains(testCase)){
					testCasesForCurrentTestSuite.add(testCase);
				}
			}				
			
			testSuiteAndTestCasesForThisGroup.put(testSuite, testCasesForCurrentTestSuite);
		}
		testGroupTestSuiteAndTestCasesDetails.put(testGroup, testSuiteAndTestCasesForThisGroup);
	}		
	return testGroupTestSuiteAndTestCasesDetails;
	}
	catch(Exception e){
		throw e;
	}
}
 }
