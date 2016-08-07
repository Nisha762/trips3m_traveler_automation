package com.auto.solution.TestDrivers;


import java.net.MalformedURLException;

import org.openqa.selenium.NoSuchElementException;


public interface TestDrivers {

	public void injectTestObjectDetail(TestObjectDetails testObjectDetails);
	
	public void initializeApp(String endpoint) throws MalformedURLException,Exception;
	
	public void sendKey(String text) throws NoSuchElementException,Exception;
	
	public void isTextPresent(String text) throws Exception;
	
	public void click() throws Exception;
	
	public void shutdown() throws Exception;
	
	public void closeAllBrowsersWindow() throws Exception;
	
	public void isObjectThere() throws Exception;
	
	public void sleep(long timeInMilliseconds);
	
	public String saveSnapshotAndHighlightTarget(boolean highlight);	
}
