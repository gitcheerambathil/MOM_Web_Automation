package test.philips.com.utils;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import test.philips.com.network.HttpUtils;

import com.relevantcodes.extentreports.LogStatus;

/**
 * This class contains all the methods that can be reused in all the test case classes. 
 * @author Maneesh Cheerambathil
 *
 */
public class ReusableMethods extends SetupDriver {
	
	public static Map<String,String> TEST_DATA=new HashMap<String,String>();
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
    public static String screenshotFileName = null;
    public static String videoFileName = null;
    public static String videoFilePath = null;
    public static String screenshotFilePath = null;
		
  	/**
  	 * This function will find the number of test scenarios from the test data excel file. Based on the excel file sheet/tab name passed as parameter, it will open the file and find number of test scearios in that.
  	 * @param excelSheetTabName  - Sheet/Tab name inside the test data excel file where the corresponding test case is written.
  	 * @return
  	 * @throws Exception
  	 */
  	public static int getNumberOfTestScenarios(String excelSheetTabName) throws Exception 
  	{
  			int rowCount=0;
  			File src=new File(config.getProperty("testDataPath"));
  		    Workbook workbook=Workbook.getWorkbook(src);
  		    Sheet worksheet=workbook.getSheet(excelSheetTabName);
  		    rowCount=worksheet.getRows();
  		    return rowCount;
  	}
  	
  	/**
  	 * This function will fetch all the test data in test scenario under the a particular row of excel tab name and this will write the data into a hash map. 
  	   Data will be written in hash map under the title of  column headers in the excel. For  eg: If we call Test_data.get("ID"), it will fin the id under title ID.
  	 * @param excelSheetTabName -  Excel sheet/ tab name under which the test scenario resides. 
  	 * @param excelSheetRowId - Row number of particular test scenario in the excel
  	 * @throws Exception
  	 */
  	public static void getTestData(String excelSheetTabName,int excelSheetRowId) throws Exception{
  			Map<String,String> dataSource=new HashMap<String,String>();
  		    String col,row = null;
  			File src=new File(config.getProperty("testDataPath"));
  		    Workbook workbook=Workbook.getWorkbook(src);
  		    Sheet worksheet=workbook.getSheet(excelSheetTabName);
  				  for (int j=0;j<worksheet.getColumns();j++){
  						jxl.Cell cell1=worksheet.getCell(j,0);
  						jxl.Cell cell2=worksheet.getCell(j,excelSheetRowId);	        
  				        row= cell1.getContents();
  				        col= cell2.getContents();
  				        dataSource.put(row, col);
  				        //System.out.println(TEST_DATA);
  				        TEST_DATA=dataSource; 
  				  }        
  	  	}

  	/**
  	 * This function will write the test results to excel report. It will clear the actual results and test data fields and write the actual results based on the test.
  	 * This function will also color the actual result and status fields as Red/Green based on the status.
  	 * @param excelSheetTabName -  Excel sheet/ tab name under which the test scenario resides. 
  	 * @param excelSheetRowId - Row number of particular test scenario in the excel
  	 * @param tesStatus - Test results whether Pass or Fail
  	 * @param actualResult - Actual result description of the test if pass or if failed then log files
  	 * @throws Exception
  	 */
  	public static void writeTestResultsToExcel(String excelSheetTabName,int excelSheetRowId,String tesStatus,String actualResult) throws Exception{
  			HSSFSheet sheet=wb.getSheet(excelSheetTabName);
  			Row r= sheet.getRow(excelSheetRowId);
  			int lastColumnNumber= sheet.getRow(0).getLastCellNum();
  			Cell c=r.createCell(lastColumnNumber-1);
  			c.setCellValue(tesStatus); 
  			CellStyle s = c.getSheet().getWorkbook().createCellStyle();	
  			Cell c1=r.createCell(lastColumnNumber-2);
  			c1.setCellValue(actualResult); 
  			CellStyle s1 = c1.getSheet().getWorkbook().createCellStyle();
  			
  			s.setBorderBottom((short) 1);
  			s.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
  			s.setBorderTop((short) 1);
  			s.setBorderRight((short) 1);
  			s.setBorderLeft((short) 1);
  			s.setVerticalAlignment((short) 0);
  			s.setWrapText(true);
  			
  			s1.setBorderBottom((short) 1);
  			s1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
  			s1.setBorderTop((short) 1);
  			s1.setBorderRight((short) 1);
  			s1.setBorderLeft((short) 1);
  			s1.setVerticalAlignment((short) 0);
  			s1.setWrapText(true);
  			
  			if(tesStatus.equalsIgnoreCase("Pass")){
  	  			s.setFillForegroundColor(HSSFColor.GREEN.index);
  	  			s1.setFillForegroundColor(HSSFColor.GREEN.index);
  			}
  			else{
  	  			s.setFillForegroundColor(HSSFColor.RED.index);
  	  			s1.setFillForegroundColor(HSSFColor.RED.index);
  			}
  			
  			r.getCell(lastColumnNumber-1).setCellStyle(s);
  			r.getCell(lastColumnNumber-2).setCellStyle(s1);
  	  	}
  	
  	/**
  	 * This function will login to the application with the credentials matching the keyword given from excel.
  	 * @param usernameTestDataField - Keyword of the username test data in excel and repository
  	 * @param passwordTestDataField - Keyword of the password test data in excel and repository
  	 * @throws Exception
  	 */
  	public static void loginToApplication() throws Exception {
  		inputTextData("LOGIN_PAGE_TXT_USERNAME");
		inputTextData("LOGIN_PAGE_TXT_PASSWORD");
		getElement("LOGIN_PAGE_BUTTON_SIGNIN").click();		
	} 	
  	
    /**
     * This function will launch the application based on the URL given in the config properties file. This will also maximize the window.
     * @throws Exception
     */
	public static void launchApplication() throws Exception {
		driver.navigate().to(Init.config.getProperty("URL"));
		driver.manage().window().maximize();		
	} 
	
	/**
	 * This function is required to restart the browser if any expceptions comes in between the test and closed the browser without logout. 
	 * @throws Exception
	 */
	public static void restartDriver() throws Exception {
		driver.quit();
		startDriver();
	} 
	
	/**
	 * This function will logout of the application.
	 * @throws Exception
	 */
	public static void logoutOfApplication() throws Exception{
		click("LOGOUT_ARROW");
		click("LOGOUT_BUTTON");
	}
	
	/**
	 * This function will fetch the locator path given in the object repository and it will fetch the element based on whether it is a xpath, id ,name etc...
	   User has to just pass the keyword and this function will return the element. Also it wait explicitly for 30 seconds to find the element.
	 * @param elementORKey - Keyword to fetch the locator from object repository
	 * @return - Actual element found based on the keyword.
	 * @throws Exception
	 */
	public static WebElement getElement(String elementORKey)throws Exception{
		String locatorData,locateBy,locator;
		WebElement element = null;
		locatorData=Init.objectRepository.getProperty(elementORKey);
		String temp[]=locatorData.split("~");
		locateBy=temp[0];
		locator=temp[1];
		WebDriverWait wait = new WebDriverWait(driver, 30);			
		if(locateBy.equalsIgnoreCase("XPATH")){
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
			element= SetupDriver.driver.findElement(By.xpath(locator));	
		}
		else if(locateBy.equalsIgnoreCase("CSS")){
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(locator)));
			element= SetupDriver.driver.findElement(By.cssSelector(locator));
		}
		else if(locateBy.equalsIgnoreCase("ID")){
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(locator)));
			element= SetupDriver.driver.findElement(By.id(locator));
		}
		else if(locateBy.equalsIgnoreCase("linktext")){
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(locator)));
			element= SetupDriver.driver.findElement(By.linkText(locator));
		}
		else if(locateBy.equalsIgnoreCase("name")){
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(locator)));
			element= SetupDriver.driver.findElement(By.name(locator));	
		}
		return element;
	}
	
	/**
	 * This function will fetch the locator path given in the object repository and it will find the elements as list based on whether it is a xpath, id ,name etc...
	   User has to just pass the keyword and this function will return the element. Also it wait explicitly for 30 seconds to find the element.
	   This function is required if there are multiple line of fields under a locator - For example Listbox in web ui.
	 * @param elementORKey - Keyword to fetch the locator from object repository
	 * @return - Actual element found based on the keyword.
	 * @throws Exception
	 */
	public static List<WebElement> getElements(String elementORKey)throws Exception{
		String locatorData,locateBy,locator;
		List<WebElement> element = null;
		locatorData=Init.objectRepository.getProperty(elementORKey);
		String temp[]=locatorData.split("~");
		locateBy=temp[0];
		locator=temp[1];
		WebDriverWait wait = new WebDriverWait(driver, 30);		
		if(locateBy.equalsIgnoreCase("XPATH")){
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
			element= SetupDriver.driver.findElements(By.xpath(locator));	
		}
		else if(locateBy.equalsIgnoreCase("CSS")){
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(locator)));
			element= SetupDriver.driver.findElements(By.cssSelector(locator));
		}
		else if(locateBy.equalsIgnoreCase("ID")){
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(locator)));
			element= SetupDriver.driver.findElements(By.id(locator));
		}
		else if(locateBy.equalsIgnoreCase("linktext")){
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(locator)));
			element= SetupDriver.driver.findElements(By.linkText(locator));
		}
		else if(locateBy.equalsIgnoreCase("name")){
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(locator)));
			element= SetupDriver.driver.findElements(By.name(locator));		
		}
		return element;
	}
	/**
	 * This function will fetch the data from testdata excel file,get the element from web UI matching the elements keyword given and compare the data. 
	   If data in excel and web UI is not matching it will fail the test, else pass the test. This function will also write the test result in Excel and html format. 
	 * @param expectedDataArray - List of expected keywords of locators with that code can fetch matching data from Excel and Object Repository. User can pass all the data to be verified
	   at one shot and this function will verify the data one by one.
	 * @param reportOrder - The Order in which the test report will display in html. If child1, then that will be the first in report.
	 * @param excelTabName - The sheet name of test data excel to fetch the data from each row of that sheet.
	 * @param row - The row number of the test case in excel sheet tab.
	 * @throws Exception
	 */
	public static void verifyData(String expectedDataArray[],String reportOrder,String excelTabName,int row) throws Exception{
		ArrayList<String> actualresult = new ArrayList<String>();
		boolean status=true;
		boolean elementDataMatched=true;
		
		String testCaseName= TEST_DATA.get("TestCase_ID")+"_"+ TEST_DATA.get("STEPNAME");
		for(int i=0;i<expectedDataArray.length;i++){
			int j=0;
			String actualText = null;
			if(!TEST_DATA.get(expectedDataArray[i]).isEmpty()&&!TEST_DATA.get(expectedDataArray[i]).equals(null)&&!TEST_DATA.get(expectedDataArray[i]).equals("")){
				do{
					WebElement we=getElement(expectedDataArray[i]);
					actualText=we.getText();
					System.out.println(actualText);
					
					if(!actualText.equals(TEST_DATA.get(expectedDataArray[i]))){
						elementDataMatched=false;
					}
					else{
						elementDataMatched=true;
						break;
					}
					j++;
					Thread.sleep(500);
				}while(j<10);
			}
			else{
				String locatorData=Init.objectRepository.getProperty(expectedDataArray[i]);
				String temp[]=locatorData.split("~");
				String locator=temp[1];		
				try{
					if(driver.findElement(By.xpath(locator)).isDisplayed()){
						if(!driver.findElement(By.xpath(locator)).getText().isEmpty()) {
							elementDataMatched=false;
							actualresult.add(driver.findElement(By.xpath(locator)).getText());
						}
					}
				}catch(Exception e){
					continue;
				}
			}
			if(elementDataMatched==false){
				status=false;
				actualresult.add(actualText);
			}
		}
		
		if(status==true){
			ReusableMethods.stopRecordingAndCaptureScreenshot(testCaseName);
			if(config.getProperty("htmlReportFlag").equalsIgnoreCase("true"))
				resultPass(reportOrder,testCaseName,"EXPECTED_RESULT",TEST_DATA.get("ACTUAL_RESULT"));
			if(config.getProperty("excelReportFlag").equalsIgnoreCase("true"))
				writeTestResultsToExcel(excelTabName, row,"Pass",TEST_DATA.get("ACTUAL_RESULT"));
			if(config.getProperty("tfsResultsUploadFlag").equalsIgnoreCase("true"))
				uploadResultsToTfs(TEST_DATA.get("ACTUAL_RESULT"), "Passed");
			if(config.getProperty("tfsImageUploadFlag").equalsIgnoreCase("true")&&config.getProperty("tfsResultsUploadFlag").equalsIgnoreCase("true"))
				uploadAttachmentToTfs("image");
			if(config.getProperty("tfsVideoUploadFlag").equalsIgnoreCase("true")&& config.getProperty("tfsResultsUploadFlag").equalsIgnoreCase("true"))
				uploadAttachmentToTfs("video");
		}
		else{
			ReusableMethods.stopRecordingAndCaptureScreenshot(testCaseName);
			if(config.getProperty("htmlReportFlag").equalsIgnoreCase("true"))
				resultFail(reportOrder,testCaseName,"EXPECTED_RESULT",actualresult.toString()+ "was displayed");
			if(config.getProperty("excelReportFlag").equalsIgnoreCase("true"))
				writeTestResultsToExcel(excelTabName, row,"Fail",actualresult.toString()+ "was displayed");
			if(config.getProperty("tfsResultsUploadFlag").equalsIgnoreCase("true"))
				uploadResultsToTfs(actualresult.toString()+ "was displayed", "Failed");
			if(config.getProperty("tfsImageUploadFlag").equalsIgnoreCase("true")&&config.getProperty("tfsResultsUploadFlag").equalsIgnoreCase("true"))
				uploadAttachmentToTfs("image");
			if(config.getProperty("tfsVideoUploadFlag").equalsIgnoreCase("true")&& config.getProperty("tfsResultsUploadFlag").equalsIgnoreCase("true"))
				uploadAttachmentToTfs("video");
		}

	}
		
	/**
	 * This function will fetch the data from hash map file based on the keyword passed and it will click on the text field based on the keyword given, clear the field and enter the data given.
	   This function will also check whether the data is empty or not, if empty then it does nothing and pass to next field.
	 * @param elementKeyWord - The keyword given in the Object repository matching the locator of web UI
	 * @param testDataKeyWord - The keyword given in the test data excel column as title
	 * @throws Exception
	 */
	public static void inputTextData(String elementKeyWord) throws Exception{
		if(!TEST_DATA.get(elementKeyWord).isEmpty()){
			WebElement textField=getElement(elementKeyWord);
			click(textField);
			textField.clear();
			textField.sendKeys(TEST_DATA.get(elementKeyWord));
		}
	}
	

	/**
	 * This function will fetch the data from hash map file based on the keyword passed and it will click on the list field based on the keyword given.
	   Then this will find the element in the list based on the second keyword passed in the function and click on the list
	 * @param textFiledElementKeyWord - The keyword of text field (where list will display) given in the Object repository matching the locator of web UI
	 * @param listElementKeyWord - The keyword of list field given in the Object repository matching the locator of web UI
	 * @param testDataField - The keyword given in the test data excel column as title
	 * @throws Exception
	 */
	public static void inputListData(String textFiledElementKeyWord,String listElementKeyWord) throws Exception{
		if(!TEST_DATA.get(textFiledElementKeyWord).isEmpty()){
			inputTextData(textFiledElementKeyWord);
			List<WebElement> list = getElements(listElementKeyWord);
			for (WebElement listField : list) {
				String listValue = listField.getText();
				if (listValue.equalsIgnoreCase(TEST_DATA.get(textFiledElementKeyWord))){
					click(listField);		
				}
			}
		}
	}

	/**
	 * This function will fetch the data from hash map file based on the keyword passed and it will click on the combo box field based on the keyword given.
	   Then this will find the element in the options based on the second keyword passed in the function and click on the list
	 * @param comboFiledElementKeyWord - The keyword of combo field (where combo box will display) given in the Object repository matching the locator of web UI
	 * @param optionsElementKeyWord - The keyword of options field given in the Object repository matching the locator of web UI
	 * @param testDataField - The keyword given in the test data excel column as title
	 * @throws Exception
	 */
	public static void inputComboboxData(String comboFiledElementKeyWord,String optionsElementKeyWord) throws Exception{
		if(!TEST_DATA.get(comboFiledElementKeyWord).isEmpty()){
			click(comboFiledElementKeyWord);
			List<WebElement> list = getElements(optionsElementKeyWord);
			for (WebElement comboField : list) {
				String comboValue = comboField.getText();
				if (comboValue.equalsIgnoreCase(TEST_DATA.get(comboFiledElementKeyWord))){
					click(comboField);
				}
			}
		}
	}
	/**
	 * This function will click on a button based on the keywords passed.It will find the button locator from the object repository.It is possible to use �lick'funnction directly, This function is added to keep the uniformity in calling methods.
	 * @param elementKeyWord - The keyword given in the Object repository matching the locator of web UI
	 * @throws Exception
	 */
	public static void clickButton(String elementKeyWord) throws Exception{
		click(elementKeyWord);
	}
	
	/**
	 * This function will convert the exception caught to a string and return the exception as string.
	 * @param exception - Actual exception caught from the main class
	 * @return - It will return the exception as string value
	 * @throws Exception
	 */
	public static String getExceptionString(Exception exception) throws Exception{
		StringWriter errors = new StringWriter();
		exception.printStackTrace(new PrintWriter(errors));
		return(errors.toString());
	}
	
	/**
	 * This function will take the screenshot of the UI and stop the recording of video after the test.
	 * @param testCaseName - Name of the test case 
	 * @throws Exception
	 */
	public static void stopRecordingAndCaptureScreenshot(String testCaseName) throws Exception{
		 Thread.sleep(2000);
		 ReusableMethods.takeScreenshot(testCaseName);
		 SetupDriver.videoReord.stopRecording();
	}
	/**
	 * This function will log the test result as pass to the html report.
	 * @param reportOrder - Order in which the test case is displayed in the html report
	 * @param testCaseName - Name of the test case
	 * @param expectedResultField - Expected result field keyword in excel so taht it will fetch the data and display in the html report
	 * @param actualResult - Actual result after the execution
	 * @throws Exception
	 */
	public static void resultPass(String reportOrder,String testCaseName,String expectedResultField, String actualResult)throws Exception{
		switch(reportOrder){
		case "child1":child1.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
		case "child2":child2.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child3":child3.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child4":child4.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child5":child5.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;	
        case "child6":child6.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;	
        case "child7":child7.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child8":child8.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child9":child9.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child10":child10.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child11":child11.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child12":child12.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child13":child13.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child14":child14.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child15":child15.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child16":child16.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child17":child17.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child18":child18.log(LogStatus.PASS,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
		}
	}
	
	/**
	 * This function will log the test result as fail to the html report.
	 * @param reportOrder - Order in which the test case is displayed in the html report
	 * @param testCaseName - Name of the test case
	 * @param expectedResultField - Expected result field keyword in excel so taht it will fetch the data and display in the html report
	 * @param actualResult - Actual result after the execution
	 * @throws Exception
	 */
	public static void resultFail(String reportOrder,String testCaseName,String expectedResultField, String actualResult) throws Exception{
		switch(reportOrder){
		case "child1":child1.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
		case "child2":child2.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
		case "child3":child3.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
		case "child4":child4.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child5":child5.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult); break;
        case "child6":child6.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child7":child7.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child8":child8.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child9":child9.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child10":child10.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child11":child11.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child12":child12.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child13":child13.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child14":child14.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child15":child15.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child16":child16.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child17":child17.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
        case "child18":child18.log(LogStatus.FAIL,testCaseName," Expected="+TEST_DATA.get(expectedResultField)+" and Actual="+actualResult);break;
		}
	}
	
	/**
	 * This function will add the test case result to the html report under the test suite
	 * @param reportOrder - Order in which the test case is displayed in the html report
	 * @throws Exception
	 */
	public static void appendChild(String reportOrder)throws Exception{
		switch(reportOrder){
		case "child1":parent.appendChild(child1); break;
		case "child2":parent.appendChild(child2); break;
		case "child3":parent.appendChild(child3); break;
		case "child4":parent.appendChild(child4); break;
		case "child5":parent.appendChild(child5); break;
		case "child6":parent.appendChild(child6); break;
		case "child7":parent.appendChild(child7); break;
		case "child8":parent.appendChild(child8); break;
		case "child9":parent.appendChild(child9); break;
		case "child10":parent.appendChild(child10); break;
		case "child11":parent.appendChild(child11); break;
		case "child12":parent.appendChild(child12); break;
		case "child13":parent.appendChild(child13); break;
		case "child14":parent.appendChild(child14); break;
		case "child15":parent.appendChild(child15); break;
		case "child16":parent.appendChild(child16); break;
		case "child17":parent.appendChild(child17); break;
		case "child18":parent.appendChild(child18); break;
		}
	}
	
	/**
	 * This function will take the screenshot of UI and name it with the test case name
	 * @param testCaseName - Name of the test case.
	 * @throws Exception
	 */
	public static void takeScreenshot(String testCaseName) throws Exception{
		File file = new File(SetupDriver.screenshotPath); 
        if (!file.exists())
        	file.mkdirs();
		BufferedImage image = new Robot().createScreenCapture(
     		 new Rectangle(Toolkit.
     		 getDefaultToolkit().
     		 getScreenSize()));
			 screenshotFilePath = file+"\\"+testCaseName+"-"+dateFormat.format(new Date())+".png";
			 screenshotFileName = testCaseName+"-"+dateFormat.format(new Date())+".png";
     		 ImageIO.write(image, "png", new File(screenshotFilePath));	
	}
	
	/**
	 * This function will click on any element passed based on the key word. This will take care of the selenium exceptions occurring based on the delays, unknown errors etc.
	 * There is a while loop given just to avoid such delay exceptions. It is necessary to avoid such erros.
	 * @param elementKeyWord - Keyword of the element given in object repo and excel.
	 * @throws Exception
	 */
	public static void click(String elementKeyWord) throws Exception{
		WebElement we =getElement(elementKeyWord);		
        boolean clicked = false;
        int i=0;
        do{
            try {
            	we.click();
            	i++;
            	clicked=true;
            	System.out.println("Normal :Count"+i);
            } catch (Exception e) {
            	i++;
                Thread.sleep(500);
                if(i>40)
                	throw e;
                continue;     
            }         	        
        } while (clicked ==false && i<40);
	}
	
	/**
	 * This function will generate random key value based on the length and format given and return the data
	 * @param length - length of string to be generated.
	 * @return
	 */
	public static String generateRandomKey(int length){
		String alphabet = new String("0123456789abcdefghijklmnopqrstuvwxyz");
		int n = alphabet.length();

		String result = new String(); 
		Random r = new Random();

		for (int i=0; i<length; i++)
		    result = result + alphabet.charAt(r.nextInt(n));
		return result;
	}
	
	/**
	 * This function will generate name based on the length and format given and return the data
	 * @param length - length of string to be generated.
	 * @return
	 */
	public static String generateRandomName(int length){
		String alphabet = new String("abcdefghijklmnopqrstuvwxyz");
		int n = alphabet.length();
		String result = new String(); 
		Random r = new Random();

		for (int i=0; i<length; i++)
		    result = result + alphabet.charAt(r.nextInt(n));
			String randomName = result.substring(0, 1).toUpperCase() + result.substring(1);
			return randomName;
	}
	
	/**
	 * This function will generate number based on the length and format given and return the data
	 * @param length - length of number to be generated.
	 * @return
	 */
	public static String generateRandomNumber(int length){
		String alphabet = new String("123456789");
		int n = alphabet.length();
		String result = new String(); 
		Random r = new Random();

		for (int i=0; i<length; i++)
		    result = result + alphabet.charAt(r.nextInt(n));
			return result;
	}
	
	/**
	 * Overridden function - This function will click on any element passed based on the actual element passed. This will take care of the selenium exceptions occurring based on the delays, unknown errors etc.
	 * There is a while loop given just to avoid such delay exceptions. It is necessary to avoid such errors. This is required in some cases where we have to fetch.
	 * @param elementKeyWord - Keyword of the element given in object repo and excel.
	 * @throws Exception
	 */
	public static void click(WebElement element) throws Exception{	
        boolean clicked = false;
        int i=0;
        do{
            try {
            	element.click();
            	clicked=true;
            } catch (Exception e) {
            	i++;
            	if(i<500)
            		continue;
            	else
            		throw e;
            } 
        } while (!clicked);
	}
	
	/**
	 * This function will click on the gender button based on the gender keyword in excel sheet.
	 * @param radioButtonKeyword1 - Gender Male radio button keyword
	 * @param radioButtonKeyword2 - Gender female radio button keyword
	 * @param radioButtonKeyword3 - Gender Other radio button keyword
	 * @throws Exception
	 */
	public static void selectGender(String radioButtonKeyword1,String radioButtonKeyword2,String radioButtonKeyword3)throws Exception{
  		if(ReusableMethods.TEST_DATA.get("GENDER").equalsIgnoreCase("Male"))
  			ReusableMethods.clickButton(radioButtonKeyword1);
  		else if(ReusableMethods.TEST_DATA.get("GENDER").equalsIgnoreCase("Female"))
  			ReusableMethods.clickButton(radioButtonKeyword2);
  		else if(ReusableMethods.TEST_DATA.get("GENDER").equalsIgnoreCase("Other"))
  			ReusableMethods.clickButton(radioButtonKeyword3);
	}
	
	/**
	 * This function will click on the image button based on the gimage key number in excel sheet.
	 * @param image1Keyword - Image 1 keyword
	 * @param image2Keyword - Image 2 keyword
	 * @param image3Keyword - Image 3 keyword
	 * @param image4Keyword - Image 4 keyword
	 * @param image5Keyword - Image 5 keyword
	 * @throws Exception
	 */
	public static void selectProfileImage(String image1Keyword,String image2Keyword,String image3Keyword,String image4Keyword,String image5Keyword)throws Exception{
  		if(ReusableMethods.TEST_DATA.get("IMAGE").equalsIgnoreCase("1"))
  			ReusableMethods.clickButton(image1Keyword);
  		else if(ReusableMethods.TEST_DATA.get("IMAGE").equalsIgnoreCase("2"))
  			ReusableMethods.clickButton(image2Keyword);
  		else if(ReusableMethods.TEST_DATA.get("IMAGE").equalsIgnoreCase("3"))
  			ReusableMethods.clickButton(image3Keyword);
  		else if(ReusableMethods.TEST_DATA.get("IMAGE").equalsIgnoreCase("4"))
  			ReusableMethods.clickButton(image4Keyword);
  		else if(ReusableMethods.TEST_DATA.get("IMAGE").equalsIgnoreCase("5"))
  			ReusableMethods.clickButton(image5Keyword);
	}
	
	/**
	 * This function will get the test data from excel, launch the application and start video recording. This should be the first function to be called in all the test cases.
	 * @param excelTabName - The sheet name of test data excel to fetch the data from each row of that sheet.
	 * @param row - The row number in the excel where test scenario exists
	 * @throws Exception
	 */
	public static void initiateTest(String excelTabName,int row) throws Exception{
		 getTestData(excelTabName,row);
		 String testCaseName = ReusableMethods.TEST_DATA.get("TestCase_ID")+"_"+ ReusableMethods.TEST_DATA.get("STEPNAME");
		 videoReord.startRecording(testCaseName);
		 launchApplication();
	}
	
	/**
	 * This function will fail the test case , write results to excel, html and then it will stop the video recording and also capture the screenshot.
	   This function should be called in the main class exception block and it will restart the driver.
	 * @param excelTabName - The sheet name of test data excel to fetch the data from each row of that sheet.
	 * @param row - The row number in the excel where test scenario exists
	 * @param reportOrder - The order in which the test result should be added to html report
	 * @param exception - Exception caught from the main class.
	 * @throws Exception
	 */
	public static void suspendTest(String excelTabName, int row,String reportOrder,Exception exception) throws Exception{
		 String testCaseName = TEST_DATA.get("TestCase_ID")+"_"+ TEST_DATA.get("STEPNAME");	
		 stopRecordingAndCaptureScreenshot(testCaseName);
		if(config.getProperty("htmlReportFlag").equalsIgnoreCase("true"))
			resultFail(reportOrder,testCaseName,"EXPECTED_RESULT",getExceptionString(exception));
		if(config.getProperty("excelReportFlag").equalsIgnoreCase("true"))
			writeTestResultsToExcel(excelTabName, row,"Fail",getExceptionString(exception));
		if(config.getProperty("tfsResultsUploadFlag").equalsIgnoreCase("true"))
			 uploadResultsToTfs(getExceptionString(exception),"Failed");
		if(config.getProperty("tfsImageUploadFlag").equalsIgnoreCase("true")&&config.getProperty("tfsResultsUploadFlag").equalsIgnoreCase("true"))
			uploadAttachmentToTfs("image");
		if(config.getProperty("tfsVideoUploadFlag").equalsIgnoreCase("true")&& config.getProperty("tfsResultsUploadFlag").equalsIgnoreCase("true"))
			uploadAttachmentToTfs("video");
	}
	
	/**
	 * This function will internally create the json structure for uploading the results into TFS and upload the actual and status.
	   This function will create a http connection and and call the Rest API of Tfs to update the result.
	 * @param uploadType - Mention whether is an update or a new test
	 * @param runId - Test Run Id Generated in TFS
	 * @param testId - Test Case Id Generated in TFS
	 * @param actualResult - Actual result of the test
	 * @param status - Status of the test (Use enums "Passed, Failed, Blocked" only
	 * @throws Exception
	 */
	public static void uploadResultsToTfs(String actualResult, String status){
		Object jsonBody1 =null;	
		Object jsonBody2 =null;	
		try{
			URL url = new URL(HttpUtils.getAbsoluteUrl("getTestPointUrl"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));	
			JSONObject response1Json=new JSONObject(br.readLine());
			testPointId=response1Json.getJSONArray("value").getJSONObject(0).get("id");
			conn.disconnect();
			
			URL url1 = new URL(HttpUtils.getAbsoluteUrl("startTestRun"));
			HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
			conn1.setDoOutput(true);
			conn1.setRequestMethod("POST");
			conn1.setRequestProperty("Content-Type", "application/json");
			jsonBody1 = HttpUtils.createTestRunJson(testPointId);
			
			DataOutputStream wr = new DataOutputStream(conn1.getOutputStream());
			wr.write(jsonBody1.toString().getBytes("UTF-8"));
			BufferedReader br1 = new BufferedReader(new InputStreamReader((conn1.getInputStream())));
			JSONObject responseJson=new JSONObject(br1.readLine());
			testRunId=responseJson.get("id");
			conn1.disconnect();
			
			URL url2 = new URL(HttpUtils.getAbsoluteUrl("updateTestStepResult"));
			HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
			conn2.setDoOutput(true);
			conn2.setRequestMethod("POST");
			conn2.setRequestProperty("Content-Type", "text/plain");
			jsonBody2 = HttpUtils.createStepUpdateJson(testPointId,testRunId,actualResult,status);
			
			DataOutputStream wr1 = new DataOutputStream(conn2.getOutputStream());
			wr1.write(jsonBody2.toString().getBytes("UTF-8"));
			System.out.println(url2);
			System.out.println("Final : "+conn2.getResponseCode()+conn2.getResponseMessage());
			conn2.disconnect();
			
			}catch(Exception e){
				try {
					String message = "TFS Upload Failed for "+ TEST_DATA.get("TestCase_ID")+"_"+testRunId+". Exception : " + getExceptionString(e);
					logger.info(message);  
					e.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	}
/**
 * This function will upload the screenshot or video taken for this test case to Tfs based on the type of file
 * @param type : Type of file whether it is image or video. For screenshot use  keyword "image" and for video use keyword "video"
 * @throws Exception
 */
	public static void uploadAttachmentToTfs(String type) throws Exception{	
		try{
			Thread.sleep(1000);
			String charset = "UTF-8";
			File file=null;
			URL url = null;
			if(type.equalsIgnoreCase("image")){
				file = new File(screenshotFilePath);
				url = new URL(HttpUtils.getAbsoluteUrl("uploadFileUrl")+screenshotFileName);
			}
			else if(type.equalsIgnoreCase("video")){
				file = new File(videoFilePath);
				url = new URL(HttpUtils.getAbsoluteUrl("uploadFileUrl")+videoFileName);
			}			
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			url = uri.toURL();
			
			System.out.println(url);
			
			URLConnection connection = new URL(url.toString()).openConnection();
			connection.setDoOutput(true);
		    
			OutputStream output = connection.getOutputStream();
		    PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
		    writer.append("Content-Transfer-Encoding: binary");
		    
		    InputStream is = new FileInputStream(file);
		    IOUtils.copy(is, output);	    
		    output.flush(); 
		    writer.close();

			int responseCode = ((HttpURLConnection) connection).getResponseCode();
			System.out.println(responseCode); 

		}catch(Exception e){
			try {
				String message = "TFS Image/video Upload Failed for "+ TEST_DATA.get("TestCase_ID")+"_"+testRunId+". Exception : " + getExceptionString(e);
				logger.info(message);  
				e.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}	
	}
	
}