package com.auto.solution.Common;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;


import org.apache.commons.lang.time.StopWatch;

import com.auto.solution.Common.Property.ERROR_MESSAGES;

public class Utility {
		
	public static Properties testDrivingPropertyFile = new Properties();
	
	private static HashMap<String,String> tempMap = new HashMap<String, String>();
	
	ResourceManager rManager = null;
	
	public Utility(ResourceManager rm){
		this.rManager = rm;
	}
	
	public void loadPropertiesDefinedForExecution() throws Exception {
		try {
			testDrivingPropertyFile.load(new FileInputStream(rManager.getUIAutoamtionPropertyFileLocation()));

		} catch (Exception e) {
			throw e;
		}
	}
	
	
	
	public static String getCurrentTimeStampInAlphaNumericFormat(){
		Date currentDate = new Date();
		return currentDate.toString().replace(":", "");
	}
	
	public static  String getTenDigitUniqueNumberInString(){
		return String.valueOf(System.currentTimeMillis()).substring(0, 10);
	}
	
	public static void showAllTestEnginePropertiesOnConsole() 
	{
		try{
			String[] relevantProprties = null;
			
			String valueForKey = System.getProperties().getProperty("sun.java.command");
			if (valueForKey.toLowerCase().contains("-d")) {
				relevantProprties = valueForKey.split("-d");
			}
			
			if(relevantProprties!=null){
				for(int i=1;i<relevantProprties.length;i++){
					String key =relevantProprties[i].split("=")[0];
					String value = relevantProprties[i].split("=")[1];
					tempMap.put(key, value);
				}
			}
			
			System.out.println("<-----------------  Test proprties for exectuion  ------------------------>");
			Set<String> keySet = tempMap.keySet();
			for(String key : keySet){
				String value = Property.globalVarMap.get(key);
				System.out.println(key + " : " + value);
			}
			System.out.println("<----------------------- Test proprties ends  ---------------------------->");
		}
		catch (Exception e) {
			
		}
	}

	/**
	 * Populate Global hash map with the Key/Value given as property for framework.
	 * @throws Exception
	 */
	public static void populateGlobalVarMapWithPropertiesDefinedInPropertiesFile() throws Exception {
		try {
			Enumeration enumOfPropertyKeys = testDrivingPropertyFile.keys();
			
			Set setOfKeysInPropertyFile = testDrivingPropertyFile.keySet();
			
			Object[] keys = setOfKeysInPropertyFile.toArray();
			
			int indexOfKeysInPropertyFile = 0;
			
			while (enumOfPropertyKeys.hasMoreElements()) {
				
				String element = (String) enumOfPropertyKeys.nextElement();	
				String propertyKey = keys[indexOfKeysInPropertyFile].toString();
				
				propertyKey = propertyKey.toLowerCase();
				
				String propertyValueForKey = testDrivingPropertyFile.getProperty(element);
				Property.globalVarMap.put(propertyKey,propertyValueForKey);
				indexOfKeysInPropertyFile++;
			
			}
			
			Property.globalVarMap.put("timestamp", getCurrentTimeStampInAlphaNumericFormat());
			Property.globalVarMap.put("unique", getTenDigitUniqueNumberInString());
			
			tempMap.putAll(Property.globalVarMap);
			
		} catch (Exception e) {
			throw e;
		}
	}

	public static void setKeyValueToGlobalVarMap(String Key, String Value) throws Exception {
		try {
				Key = Key.toLowerCase();
			
				Property.globalVarMap.put(Key, Value);
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static void addObjectToGlobalObjectCollection(String Key, Object object) throws Exception{
		try{
			Key = Key.toLowerCase();
			Property.globalObjectCollections.put(Key, object);
		}
		catch(Exception e){
			throw e;
		}
	}
	
	public static Object getObjectFromGlobalObjectCollection(String Key){
		try{
			Key = Key.toLowerCase();
			return Property.globalObjectCollections.get(Key);
		}
		catch(Exception e){
			return null;
		}
	}

	public static String getValueForKeyFromGlobalVarMap(String Key){
		try {
			
			Key = Key.toLowerCase();
			
			return Property.globalVarMap.get(Key);
			
		} catch (Exception e) {
			return Key;
		}
	}

	public static boolean matchContentsBasedOnStrategyDefinedForTestStep(String ExpectedValue,
			String ActualValue) {
		
			return ActualValue.contains(ExpectedValue);
		
	}

	
	/**
	 * Replace all the occurrences of string like '{$KeyVar}' to its actual value stored in global map.
	 * For eg. if string is 'Test {$KeyVar} and KeyVar has value 'ABC' then resulting string would be 
	 * like Test ABC. If no value is present for a key then string returned as it is.
	 * @param dataValue string that may contain format '{$KeyVar} 
	 * @return Resultant string with all occurrence of '{$KeyVar} replaced with its value.
	 */
	public static String replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(String dataValue) {
		try {

			for (int v = 0;; v++) {

				if (dataValue.contains("{$")) {
					int stindex = dataValue.lastIndexOf("{$");
					int endindex = dataValue.indexOf("}", stindex);
					if (stindex < 0 || endindex < 0) {
						break;
					}
					String keyVariable = dataValue.substring((stindex + 2),
							endindex);
					String value = getValueForKeyFromGlobalVarMap(keyVariable);
					if (!value.equalsIgnoreCase(keyVariable)) {
						dataValue = dataValue.replace("{$" + keyVariable + "}",
								value);
					}

				} else {
					break;
				}
			}

		} catch (Exception e) {
			// Nothing to throw.
		}

		return dataValue;

	}
	
	public static String getCurrentDateAndTimeInStringFormat(){
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		Date date = new Date();
		
		return dateFormat.format(date);
	}
	
	public static StopWatch getAndStartStopWatch(){
		StopWatch stopWatch = new StopWatch();
				
		stopWatch.reset();
		
		stopWatch.start();
		
		return stopWatch;
	}
	
	public static String haltAndGetTotalTimeRecordedInStopWatch(StopWatch stopWatch){
		stopWatch.split();
		stopWatch.stop();
		return String.valueOf(stopWatch.getSplitTime() / 1000);
	}
	public static void storeSystemPropertiesToGlobalVarMap(Properties systemProperties){		
		
		try {
			 Set keySetFromSystemProperties = systemProperties.keySet();
						 
			 Object[] systemPropertiesKeysArray = keySetFromSystemProperties.toArray();			 
			 
			 for (Object key : systemPropertiesKeysArray) {
				String keyString = (String) key;
				
				String valueForKey = systemProperties.getProperty(keyString);
							
				 setKeyValueToGlobalVarMap(keyString, valueForKey);
				
			}
			
		} catch (Exception e) {
			
		}
	}
	
	public static HashMap<String, String[]> getTestGroupAndItsTestSuiteMapFromLocal(Properties localGroupPropertyFile) throws Exception{
		
		HashMap<String, String[]> testGroupMap = new HashMap<String, String[]>();
		
		Set<Object> testGroupNames = localGroupPropertyFile.keySet();
		
		for (Object testGroupName : testGroupNames) {
			
			String testGroupNameString = testGroupName.toString();
			String testSuitesForTheTestGroup =  localGroupPropertyFile.getProperty(testGroupNameString);
			String[] listOfTestSuitesForTheTestGroup = testSuitesForTheTestGroup.split(",");
			testGroupMap.put(testGroupNameString.toLowerCase(), listOfTestSuitesForTheTestGroup);
		}
		
		return testGroupMap;
		
	}
	public static Boolean decideToExecuteTestStepOnTheBasisOfConditionSpecifiedForTestStep(String conditionsSpecified, ArrayList<String> variablesUsed){
		
		
			if(variablesUsed.contains(conditionsSpecified)){
				return true;
			}
			else{
				return false;
			}
		
	}
	
	public static String getAbsolutePath(String pathName){
		File file = null;
		try {
			file =  new File(pathName);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return file.getAbsolutePath();
	}
	
	public static String executeJava(String javaSnippet) throws Exception{
		
		javaSnippet = javaSnippet.replace("\n", "");
		javaSnippet = javaSnippet.replace("%%", ";");
				
		String javaSnippetResult = "";
	 
		try{
		Binding binding = new Binding();
		
		GroovyShell shell = new GroovyShell(binding);
		
		Object result = shell.evaluate(javaSnippet);
		
		javaSnippetResult = result.toString();
		}
		catch(Exception e){
			String errMessage = Property.ERROR_MESSAGES.ER_EXECUTING_JAVA_SNIPPET.getErrorMessage().replace("{JAVA_SNIPPET}", javaSnippet);
			throw new Exception(errMessage);
		}
		return javaSnippetResult;
	}
		
	 
    public static void reportUrlsStatus(HashMap<String, String> UrlStatusMap,String reportFileLocation)throws Exception{
    	try{    		
			
			FileWriter writer = new FileWriter(reportFileLocation);
			
			writer.append("URLS");		    
			writer.append(',');
		    writer.append("REMARKS");
		    
			writer.append('\n');
		    
			for (String Url : UrlStatusMap.keySet()) {
		    	String remarks = UrlStatusMap.get(Url);
				writer.append(Url);
			    writer.append(',');
			    writer.append(remarks);
			    writer.append('\n');
			}
			
			writer.flush();
		    writer.close();
    	}
    	catch(Exception e){
    		throw e;
    	}
    }
    
    public static void deleteFile(String  filePath){
    	try{
    		File file = new File(filePath);
    		if(file.exists())
    		{
    			file.delete();
    		}
    	}
    	catch(Exception e){
    		
    	}
    }
    
    public static ArrayList<String> getPageUrlsInListFormatFromCSV(String fileName) throws Exception{
    	ArrayList<String> webPageUrls = new ArrayList<String>();
    	try{
    		String line;
    		BufferedReader br = new BufferedReader(new FileReader(fileName));
    		while ((line = br.readLine()) != null) {
    			String[] lineContent = line.split(",");
    			String URL = "";
    			try{
    			 URL = lineContent[0].trim();
    			}
    			catch(Exception e){
    				
    			}
    			webPageUrls.add(URL);
    		}
    	}
    	catch(Exception e){
    		throw new Exception(ERROR_MESSAGES.ERR_IN_READING_URL_SOURCE.getErrorMessage() + "--" + e.getMessage());
    	}
    	return webPageUrls;
    }
	}
	
