package test.philips.com.testsuite;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import test.philips.com.utils.ReusableMethods;
import test.philips.com.utils.SetupDriver;
/**
 * This class contains all the test scenarios to be tested related to to login.
 * @author Maneesh Cheerambathil
 *
 */
public class LoginTestCases extends SetupDriver{
	String reportOrder="child1";
	int numberOfTestScenarios=0;
	String excelTabName = "Login";
	int row = 0;
	String testCaseName = "Default";
	String expectedDataLocators[]={"LOGIN_USERNAME_VALIDATION","LOGIN_PASSWORD_VALIDATION","LOGIN_GENERAL_VALIDATION",
			"ADD_HERO_BTN","SUP_PAGE_LOCATOR"};
	@BeforeTest
	private void beforeTest()throws Exception{
		 child1 =report.startTest("Login Test Cases");
		 numberOfTestScenarios=ReusableMethods.getNumberOfTestScenarios(excelTabName); 
	}
	
	@Test
	private void loginTest(){
		for(row=1;row<2;row++){
			 try{
				 ReusableMethods.initiateTest(excelTabName, row);
				 ReusableMethods.loginToApplication();
				 ReusableMethods.verifyData(expectedDataLocators,reportOrder,excelTabName,row);
				 if(ReusableMethods.TEST_DATA.get("TYPE").equalsIgnoreCase("VerifyPage"))
					 ReusableMethods.logoutOfApplication();
			 }catch(Exception e){
				 e.printStackTrace();	
				 try{
					 ReusableMethods.suspendTest(excelTabName, row, reportOrder, e);
				 }catch(Exception e2){
					 e2.printStackTrace();	
				 }
			 }	
		}
	 }	

	@AfterTest
	private void afterTest()throws Exception{
		ReusableMethods.appendChild(reportOrder); 
		driver.close();
	}
}


