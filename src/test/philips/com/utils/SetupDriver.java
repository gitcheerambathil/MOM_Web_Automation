
package test.philips.com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import test.philips.com.listeners.VideoRecord;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

public class SetupDriver extends Init {
	public static  WebDriver driver=null;
	public static ExtentReports report;
    public static ExtentTest parent;
    public static ExtentTest child1,child2,child3,child4,child5,child6,child7,child8,child9,child10,child11,child12,child13,child14,child15,child16,child17,child18;
    public static String videoPath=null;
    public static String screenshotPath=null;
    public static String reportPath=null;
    public static String logPath=null;
    public static String reportExcelPath=null;
	public static VideoRecord  videoReord = new VideoRecord();
	public static FileInputStream src;
	public static HSSFWorkbook wb;
	public static Logger logger;
	public static Object testRunId = null;
	public static Object testPointId = null;
	public static String reportName = null;
	
	@BeforeSuite
	public  void setUp() throws IOException
	{
		try{
  	      	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
  	      	videoPath = config.getProperty("videoPath")+ dateFormat.format(new Date());
  	      	screenshotPath = config.getProperty("screenshotPath")+ dateFormat.format(new Date());
  	      	reportPath = config.getProperty("reportPath")+dateFormat.format(new Date());

  	      	//Start of html report
    		File file = new File(reportPath); 
	        if (!file.exists())
	        	file.mkdirs();
	        reportName=file+"\\"+"Test Report_"+dateFormat.format(new Date())+".html";
	        reportExcelPath = file+"\\"+"Test Report_"+dateFormat.format(new Date())+".xls";
  	      	report=new ExtentReports(reportName); 
			parent=report.startTest("MOM Web Automation Test Suite");			
			//End
			
			//Start of Excel report
  			src=new FileInputStream(config.getProperty("testCasePath"));
  			wb = new HSSFWorkbook(src);
  			//End
  			
  			//Start of Log
  	      	logPath = file+"\\"+"TFS Upload Exception Log_"+dateFormat.format(new Date())+".log";
  		    logger = Logger.getLogger("MyLog");  
  		    FileHandler fh1;  
	        fh1 = new FileHandler(logPath);  
	        logger.addHandler(fh1);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh1.setFormatter(formatter);  
	        //End
	    	
  			startDriver();
  			
		}catch(Exception e){			
			e.printStackTrace();
        }
	}
	
	public static void startDriver(){
		String browser=Init.config.getProperty("browser");			
		if(browser.equalsIgnoreCase("FF") || browser.equalsIgnoreCase("firefox") ){
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\libs\\ffdriver.exe");
			driver=new FirefoxDriver();
		}			
		else if(browser.equalsIgnoreCase("chrome")){
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\libs\\chromedriver.exe");
			driver=new ChromeDriver();
		}			
		else if(browser.equalsIgnoreCase("IE")){
			System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\libs\\IEDriverServer.exe");	
			driver=new InternetExplorerDriver();
		}
	  driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
	}
	
	@AfterSuite
	public void tearDown(){
		try{
			driver.quit();			
			report.endTest(parent);
			report.flush();
  			FileOutputStream src1=new FileOutputStream(reportExcelPath);
  			wb.write(src1);
  			src1.close();
		} catch(Exception e)
          {
			System.out.println("Setup exception");
			e.printStackTrace();
			report.endTest(parent);
			report.flush();
          }
	}

}

