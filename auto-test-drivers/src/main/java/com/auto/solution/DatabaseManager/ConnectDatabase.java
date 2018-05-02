package com.auto.solution.DatabaseManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.Common.Utility;
import com.auto.solution.Common.Property.ERROR_MESSAGES;



public class ConnectDatabase {
	
	ResourceManager rManager = null;
	public static HashMap <String,HashMap<String,String>> dbConnectionStrings = null;


	public ConnectDatabase(ResourceManager rm){
		this.rManager = rm;
		
	}
	
	public void loadDBFile() throws Exception {
		try {
			dbConnectionStrings = new HashMap <String,HashMap<String,String>>();
			BufferedReader br = null;
			String line = "";
			String dbFile =rManager.getDBFileLocation();
			br = new BufferedReader(new FileReader(dbFile));
	            while ((line = br.readLine()) != null) {
	            	String[] dbConnectionLine = line.split(",");
	            	HashMap<String,String> dbDetails =  new HashMap<String,String>();
	            	dbDetails.put("dbtype", dbConnectionLine[1]);
	            	dbDetails.put("driver", dbConnectionLine[2]);
	            	dbDetails.put("connectionstring", dbConnectionLine[3]);
	            	dbConnectionStrings.put(dbConnectionLine[0], dbDetails);
	            }
	            if(br!=null)
	            	br.close();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static Connection connectionVerificationAndEstablishment(String dbName) throws Exception{
		Connection conn = (Connection) Utility.getObjectFromGlobalObjectCollection(dbName);
		try {
			if(Utility.getValueForKeyFromGlobalVarMap("dbconnectionrequired")==null || 
					Utility.getValueForKeyFromGlobalVarMap("dbconnectionrequired").equalsIgnoreCase("false")){
				throw new IOException(Property.ERROR_MESSAGES.ER_CONNECT_TO_DB_NOT_OPTED.getErrorMessage());
			}
			if(conn == null||conn.isClosed()){
				connectToDatabase(dbName);
			}
		}
		catch(IOException ie){
			throw ie;
		}
		catch (Exception e) {
			throw new Exception(Property.ERROR_MESSAGES.ERR_DATABASE_CONNECTION_TIMEOUT.getErrorMessage());
		}
		return conn;	
	} 
	
	public static void connectToAllDatabase() throws Exception{
		try{
			String  dbNameList= Property.globalVarMap.get("dbname");
			if(dbNameList == null) {
					throw new Exception(ERROR_MESSAGES.ER_DB_NAME_NOT_PROVIDED.getErrorMessage());
			}
			String[] dbNames = dbNameList.split(",");
			for(String key:dbNames){
					makeDBConnection(key);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw new Exception(Property.ERROR_MESSAGES.ERR_DATABASE_CONNECTION_ERROR.getErrorMessage());
		}
		
	}
	
	public static void connectToDatabase(String dbName) throws Exception{
		makeDBConnection(dbName);
	}
	
	private static void makeDBConnection(String dbName) throws Exception{
		Connection conn = null;
		
		try{		
			String connectionDriver = dbConnectionStrings.get(dbName).get("driver");
			String connectionStringUrlNameAndPassword = dbConnectionStrings.get(dbName).get("connectionstring");
			Class.forName(connectionDriver);
			conn = DriverManager.getConnection(connectionStringUrlNameAndPassword);
			
			try {
				Utility.addObjectToGlobalObjectCollection(dbName, conn);
			} 
			catch (Exception e) {
				e.printStackTrace();;
			}
		
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw new Exception(Property.ERROR_MESSAGES.ERR_DATABASE_CONNECTION_ERROR.getErrorMessage());
		}
	}
	
//	public static String[] fetchDriverAndconnectionDetailFromConnectionString(String driverUrl){
//		String[] identifierValues = driverUrl.split("###");
//		return identifierValues;
//	}
	
	/**
	 * This method executes the query provided by user on given database. Data
	 * format in test management tool should be like: @D
	 * "ResultObject:=query#dbname" (EmployeeResultSetVar:=select name from
	 * employee#boblive)
	 * 
	 * @param query - query to execute
	 * @param dbName - database on which query to execute
	 * @throws Exception
	 */
	public static void executeQuery(String query,String dbName) throws Exception{
		
		Connection conn;
		ResultSet rs;
		Statement stmt;
		int totalColumns;
		List<HashMap<String, Object>> queryResult = new ArrayList<HashMap<String, Object>>();

		connectionVerificationAndEstablishment(dbName);
		conn = (Connection) Utility.getObjectFromGlobalObjectCollection(dbName);
	
		try 
		{
			stmt = conn.createStatement();
			if(query.toLowerCase().contains("select"))
			{
				String queryKeyValue[] = query.split(":=");
				stmt.execute(queryKeyValue[1]);
				rs = stmt.getResultSet();
				ResultSetMetaData rsmd = rs.getMetaData();
				totalColumns = rsmd.getColumnCount();
				
				while (rs.next()) {
					HashMap<String, Object> rowData = new HashMap<String, Object>();
					for (int column = 0; column < totalColumns; column++) {
						rowData.put(rsmd.getColumnName(column + 1),	rs.getObject(column + 1));
					}
					queryResult.add(rowData);
				}
				 
				 Utility.addObjectToGlobalObjectCollection(queryKeyValue[0], queryResult);
			}
			else{
				stmt.execute(query);
			}	
		}
		
		catch (Exception e) {
			throw new Exception(Property.ERROR_MESSAGES.ER_MISSING_TESTDATA.getErrorMessage());	
		}
		
	}
	

}
