package redirection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.util.concurrent.Service;

import jxl.read.biff.BiffException;

public class Helper {
	
      
	 WebDriver driver=null;
	 
	 List<String> suburls=new ArrayList<String>();
	 Map<String, String> failedurls=new HashMap<String, String>();
	
	public String[][] getExcelData(String fileName, String sheetName) {
		String[][] arrayExcelData = null;
		
		try {
			String filepath=System.getProperty("user.dir") + File.separator + "src"
					+ File.separator + "main" + File.separator + "Resource"
					+ File.separator + fileName + ".xls";
			FileInputStream fs = new FileInputStream(filepath);
			jxl.Workbook wb = jxl.Workbook.getWorkbook(fs);
			jxl.Sheet sh = wb.getSheet(sheetName);

			int totalNoOfCols = sh.getColumns();
			int totalNoOfRows = sh.getRows();
			
			arrayExcelData = new String[totalNoOfRows-1][totalNoOfCols];
			
			for (int i= 1 ; i < totalNoOfRows; i++) {

				for (int j=0; j < totalNoOfCols; j++) {
					arrayExcelData[i-1][j] = sh.getCell(j, i).getContents();
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		}
		return arrayExcelData;
	}
	
	public int getRequest(String url) throws URISyntaxException {
        HttpResponse response=null; 
        int st=0;
        HttpGet get = new HttpGet(url);
        HttpParams params = get.getParams();
        params.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
        get.setParams(params);
        HttpClient client = HttpClientBuilder.create().build();
       
        try {
            response = client.execute(get);
             st = response.getStatusLine().getStatusCode();
            
            if (st != 301) {
                //Assert.fail("request if not 200 OK");
                System.out.println("Please Check This Api"+"===="+url);
                //org.testng.Assert.fail("request if not 200 OK");
                failedurls.put(String.valueOf(st), url);
                
            }
            else{
            	System.out.println(st);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return st;
    }
	
	public Map getfailedurls(){
		return failedurls;
	}
	
	public WebDriver launchchrome(){
		String path=System.getProperty("user.dir") + File.separator + "src"
				+ File.separator + "main" + File.separator + "Resource"
				+ File.separator + "chromedriver" + ".exe";
		File file = new File(path);
		System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
		if(driver==null){
		 driver= new ChromeDriver();
		}
		return driver;
		 
	}
	public List<String> navigatetourl(String url){
		launchchrome().get(url);
		//launchchrome().manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
		
		//List<WebElement> elements=driver.findElements(By.xpath("//a[contains(@href,'/')]"));
		findattributes("//a[contains(@href,'/')]");
		findattributes("//script[contains(@src,'/')]");
		findattributes("//img[contains(@src,'/')]");
		findattributes("//meta[contains(@content,'/')]");
		
		//launchchrome().close();
		return suburls;
		
	}
	
	synchronized public void findattributes(String xpath){
		List<WebElement> elements;
		//WebDriverWait wait=new WebDriverWait(launchchrome(), 50);
			elements=driver.findElements(By.xpath(xpath));
		
		
		if(xpath.contains("href")){
			for(WebElement s:elements){
				//wait.until(ExpectedConditions.stalenessOf(s));
				try {
					String s1=s.getAttribute("href");
					suburls.add(s1);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			
		}
		else if(xpath.contains("script")){
			for(WebElement s:elements){
				try {
					suburls.add(s.getAttribute("src"));
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		}
		else if(xpath.contains("img")){
			for(WebElement s:elements){
				try {
					suburls.add(s.getAttribute("src"));
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		}
		else if(xpath.contains("meta")){
			for(WebElement s:elements){
				try {
					suburls.add(s.getAttribute("content"));
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		}
	}
	
	public void generatereport(List<String> list) throws IOException {
		String[] columns = {"Urls"};
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

    /* CreationHelper helps us create instances for various things like DataFormat, 
       Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
    CreationHelper createHelper = workbook.getCreationHelper();

    // Create a Sheet
    Sheet sheet = workbook.createSheet("Report");

    // Create a Font for styling header cells
    Font headerFont = workbook.createFont();
    headerFont.setBold(true);
    headerFont.setFontHeightInPoints((short) 14);
    headerFont.setColor(IndexedColors.RED.getIndex());

    // Create a CellStyle with the font
    CellStyle headerCellStyle = workbook.createCellStyle();
    headerCellStyle.setFont(headerFont);

    // Create a Row
    Row headerRow = sheet.createRow(0);

    // Creating cells
    for(int i = 0; i < columns.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(columns[i]);
        cell.setCellStyle(headerCellStyle);
    }

    // Create Cell Style for formatting Date
    /*CellStyle dateCellStyle = workbook.createCellStyle();
    dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
*/
    // Create Other rows and cells with employees data
    int rowNum = 1;
    for(String url: list) {
        Row row = sheet.createRow(rowNum++);

        row.createCell(0)
                .setCellValue(url);

        
    }

	// Resize all columns to fit the content size
    for(int i = 0; i < columns.length; i++) {
        sheet.autoSizeColumn(i);
    }

    // Write the output to a file
    FileOutputStream fileOut = new FileOutputStream("poi-generated-file.xlsx");
    workbook.write(fileOut);
    fileOut.close();

    // Closing the workbook
    workbook.close();}

}
