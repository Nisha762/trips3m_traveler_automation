package com.auto.solution.Common;

import java.util.ArrayList;

public class RecoveryObjectsMapper {

	private ArrayList<String> testObjectNamesToSkipDuringRecovery = new ArrayList<String>();

	public void setTestObjectNamesToSkipInRecovery(ArrayList<String> listOfTestObjectsName){
		this.testObjectNamesToSkipDuringRecovery = listOfTestObjectsName;
	}
	
	public ArrayList<String> getTestObjectNamesToSkipInRecovery(){
		return this.testObjectNamesToSkipDuringRecovery;
	}
}
