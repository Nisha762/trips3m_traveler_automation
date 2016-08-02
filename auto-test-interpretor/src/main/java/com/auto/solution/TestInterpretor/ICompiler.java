package com.auto.solution.TestInterpretor;

import java.io.FileNotFoundException;

public interface ICompiler {

	public void setStepDefenitionToCompile(String stepDefenition);
	
	public String getStepAction() throws FileNotFoundException;
	
	public String getObjectDefenition();
	
	public String getTestData();
	
	public String getStrategyApplied();
	
	public String getSubTestCaseInvockedInTestStep();
	
	public String getIterations();	
	
}
