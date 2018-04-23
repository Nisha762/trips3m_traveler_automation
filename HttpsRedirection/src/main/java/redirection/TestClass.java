package redirection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;



public class TestClass {
	
	Helper helper=new Helper();
	List<String> list=new ArrayList<String>();
	
	
	
	
	@DataProvider(name="urls", parallel=true)
	public Object[][] pogids() {
		Object[][] arrayObject =helper.getExcelData("Redirection_urls","urls");
		return arrayObject;
	}
	
  @Test(dataProvider="urls")
  public void test(String endpoint) throws URISyntaxException {
	  //String hostname="http://"+System.getProperty("environment_url")+".ttdev.in";
	 String hostname="http://browsing-qa1.ttdev.in";
	  String uri=hostname+endpoint;
	  int st=helper.getRequest(uri);
	  
	  if(st==301){
		  List<String> urls;
	 urls= helper.navigatetourl(uri);
	 // String href="";
	  /*for(int i=0;i<elements.size();i++){
		  href=elements.get(i).getAttribute("href");
		  if(!href.contains("https://")){
			  System.out.println(href+ " ===========> not moved to https, parent page >>"+uri);
		  }
	  }*/
	  for(String testurl:urls){
		  if(!testurl.contains("https://")){
			  System.out.println(testurl+ " ===========> not moved to https, parent page >>"+uri);
			  list.add(testurl+ " ===========> not moved to https, parent page >>"+uri);
		  }
	  }
	  }
	  else{
		  list.add(uri);
	  }
  }
  
  @AfterTest
  public void closebrowser(){
	  helper.launchchrome().close();
  }
  
 @AfterClass
  public void createReport() throws IOException{
	  helper.generatereport(list);
	// List<String> testlist=list;
  }
}
