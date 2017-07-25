package com.layout.components;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import org.openqa.selenium.Dimension;

public class GenericProperty {

	public static String MOBILE_KEYWORD = "mobile";
	
	public static String TABLET_KEYWORD = "tablet";
	
	public static String DESKTOP_KEYWORD = "desktop";
	
	public static String CHROME_EXECUTABLE_SH = "chromedriver";
	
	public static String CHROME_EXECUTABLE = "chromedriver.exe";
	
	public static String correct_input_format = "-D device_details={\"mobile\"}{\"desktop\"_\"1024\"_\"800\"}";
	
	public static String OS_STRING = System.getProperty("os.name").toLowerCase();
	
	public static String REMOTE_IP = System.getProperty("remote_ip");
	
	
	public static String getBaseDirectoryLocation(){
		String working_directory = System.getProperty("user.dir");
		return working_directory;
	}
	
	public static String getEnvUrl(){
		String env_url = System.getProperty("environment_url");
		return env_url;
	}

	public static Object[][] getDeviceInfo() throws Exception{
		Object[][] deviceDetails = null;
		try{
		
			String device_details_specified = System.getProperty("layout_device_details");

			deviceDetails =  fetchDeviceInfo(device_details_specified);
		}
		catch(Exception e){
			throw (e);
		}
		return deviceDetails;
	}
	
	public static class TestDevice {
		
        private final String name;
        
        private final Dimension screenSize;
        
        private final List<String> tags;

        public TestDevice(String name, Dimension screenSize, List<String> tags) {
            this.name = name;
            this.screenSize = screenSize;
            this.tags = tags;
        }

        public String getName() {
            return name;
        }

        public Dimension getScreenSize() {
            return screenSize;
        }

        public List<String> getTags() {
            return tags;
        }

        @Override
        public String toString() {
        	String deviceString = "";
        	deviceString = (screenSize != null) ? String.format("%s %dx%d", name, screenSize.width, screenSize.height) : String.format("%s %dx%d", name, null , null);
                 return deviceString;  }
    }
	
	private static Object[][] fetchDeviceInfo(String devicesInfoAsString) throws Exception{
		
		String devicename = "";
		
		String x_cord = "";
		
		String y_cord = "";
		
		Dimension dimension = null;
		
		ArrayList<TestDevice> devices = new ArrayList<TestDevice>();
		
		if(devicesInfoAsString == ""){
			throw new Exception("Input = " + devicesInfoAsString + "Please provide device info, correct format is = " + correct_input_format);
		}
		else{
			if(!devicesInfoAsString.contains("{") || !devicesInfoAsString.contains("}")){
				throw new Exception("Input = " + devicesInfoAsString + "Device info is incorrect, correct format is = " + correct_input_format);
			}
			
			String temporaryDevicesInfoAsString = devicesInfoAsString;
			
			String[] splitted_Content = temporaryDevicesInfoAsString.split("}");
			

			for (String detail : splitted_Content) {
				
				String[] even_more_splitted = detail.split("_");
				
				if(even_more_splitted.length >= 3){
					devicename = even_more_splitted[0];
					x_cord = even_more_splitted[1];
					y_cord = even_more_splitted[2];
				}
				else if(even_more_splitted.length == 1){
					devicename = even_more_splitted[0];
				}
				
				devicename = devicename.replace("{", "");
				
				devicename = devicename.replace("\"","");
				
				x_cord = x_cord.replace("\"","");
				
				y_cord = y_cord.replace("\"","");
				
				if(devicename == ""){throw new Exception("Input = " + devicesInfoAsString + "Device info is incorrect, correct format is = " + correct_input_format);}
				
				
				if(!(x_cord == "" || y_cord == "")){
					dimension = new Dimension(Integer.parseInt(x_cord), Integer.parseInt(y_cord));
				}
				
				TestDevice deviceInstance = new TestDevice(devicename,dimension,asList(devicename));
				
				devices.add(deviceInstance);
			}
		}
		
		Object [][] devicesInObjectArray = new Object[devices.size()][];

		 for(int i=0;i< devices.size();i++){
			 devicesInObjectArray[i] = new Object[1];
			 devicesInObjectArray[i][0] = devices.get(i);
		 } 
		 
		 return devicesInObjectArray;
	}	
}
