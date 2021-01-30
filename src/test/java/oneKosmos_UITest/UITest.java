package oneKosmos_UITest;

import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import oneKosmos_UITest.Library;

public class UITest {
	protected Library loadlibrary = new Library();
	JSONObject myPediaTestData;

	@BeforeClass(alwaysRun = true)
	public void beforeClassPediaTest(ITestContext context) {
		// Before method to load the resource file
		try {
			System.out.println("Before Class started");
			JSONObject requiredFiles[] = { loadlibrary.getJson("/oneKosmos_UITest/ElementObject.json"),
					loadlibrary.getJson("/oneKosmos_UITest/Data.json") };
			myPediaTestData = loadlibrary.mergeJsonFiles(requiredFiles);
			System.setProperty((String) myPediaTestData.get("chromeDriver"),
					(String) myPediaTestData.get("driverLocation"));
			driver = new ChromeDriver();
			driver.get((String) myPediaTestData.get("URL"));
		} catch (Exception ex) {
			takeScreenShotonFailure(driver, context);
			System.out.println("Got Exception in Before Class : " + ex);
		}
	}

	public WebDriver driver;

	@Test()
	public void myPediaTest(ITestContext context) {
		/*
		 * Test case execute following steps
		 * 1. Load the URL
		 * 2. Check if page is loaded
		 * 3. Click on Language Dropdown
		 * 4. Change language to Hindi and assert Continue button
		 * 5. Change language to Spanish and assert Continue button
		 * 6. Change language to English and assert Continue button
		 * 7. Click on Set Up Parent Support Link
		 * 8. Click on Create New Account
		 * 9. Verify if create Account button is disbaled.
		 * 10. Enter all required fields.
		 * 11. Verify if create Account button is disabled.
		 * */
		try {
			System.out.println("myPedia Test Started");
			WebDriverWait wait = new WebDriverWait(driver, 20);
			wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.name((String) myPediaTestData.get("loginEmail"))));
			driver.navigate().refresh();
			System.out.println("Clicking on Langugae dropdown");
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			driver.findElement(By.xpath((String) myPediaTestData.get("languageDropDown"))).click();
			int languageSize = driver.findElements(By.xpath((String) myPediaTestData.get("languageList"))).size();
			assertEquals(languageSize, 3, "Expected to have 3 language");
			changeLangauge(driver,context, "हिंदी", "अग्रसर रहें");
			changeLangauge(driver,context, "Español", "Continuar");
			changeLangauge(driver,context, "English", "Continue");
			createNewUser(driver,context);
		} catch (Exception ex) {
			takeScreenShotonFailure(driver, context);
			System.out.println("Got Exception in Test Method : " + ex);
		}
	}

	public void changeLangauge(WebDriver driver, ITestContext context,String languageDropDown, String continueText) {
		// Method to change langauge drop down and assert the continue button
		try {
			System.out.println("Change Langauge to - "+languageDropDown);
			int languageDropDownSelected = driver
					.findElements(By.xpath((String) myPediaTestData.get("verifyLanguageDropDownVisibile"))).size();
			if ((languageDropDownSelected) == 0) {
				driver.findElement(By.xpath((String) myPediaTestData.get("languageDropDown"))).click();
			}
			driver.findElement(By.xpath("//span//div[text()='"+languageDropDown+"']")).click();
			System.out.println("Assert Continue Button for Language - "+languageDropDown);
			assertTrue(driver.findElements(By.xpath("//div[text()='"+continueText+"']")).size() > 0);
		} catch (Exception ex) {
			takeScreenShotonFailure(driver, context);
			System.out.println("Got Exception in Change Language Method : " + ex);
		}
	}

	public void createNewUser(WebDriver driver, ITestContext context) {
		// Method to create New User in Set Up Parent Support page.
		try {
			System.out.println("Create New Parant Support");
			driver.findElement(By.linkText((String) myPediaTestData.get("setUpParentSupportLink"))).click();
			driver.findElement(By.xpath((String) myPediaTestData.get("createNewAccountLink"))).click();
			assertFalse(driver.findElements(By.xpath((String) myPediaTestData.get("createAccountButtonVisibile")))
					.size() > 0);
			driver.findElement(By.xpath((String) myPediaTestData.get("firstNameInput")))
					.sendKeys((String) myPediaTestData.get("firstName"));
			driver.findElement(By.xpath((String) myPediaTestData.get("lastNameInput")))
					.sendKeys((String) myPediaTestData.get("lastName"));
			driver.findElement(By.xpath((String) myPediaTestData.get("emailAddressInput")))
					.sendKeys((String) myPediaTestData.get("email"));
			driver.findElement(By.xpath((String) myPediaTestData.get("createParentUserNameInput")))
					.sendKeys((String) myPediaTestData.get("userName"));
			driver.findElement(By.xpath((String) myPediaTestData.get("createParentPasswordInput")))
					.sendKeys((String) myPediaTestData.get("password"));
			driver.findElement(By.xpath((String) myPediaTestData.get("confirmPasswordInput")))
					.sendKeys((String) myPediaTestData.get("password"));
			System.out.println("Assert Create Account Button");
			assertFalse(driver.findElements(By.xpath((String) myPediaTestData.get("createAccountButtonVisibile")))
					.size() > 0);
		} catch (Exception ex) {
			takeScreenShotonFailure(driver, context);
			System.out.print("Got Exception in Create New User Method : " + ex);
		}
	}
	
	public void takeScreenShotonFailure(WebDriver driver, ITestContext context)
	{
		// Method to take screenshot on failure
		try {
		TakesScreenshot screenshot = (TakesScreenshot) driver;
		File src = screenshot.getScreenshotAs(OutputType.FILE);
		System.out.println("Storing Screenshot at : " + "/tmp/Screenshots/" +context.getName()+ ".png");
		FileUtils.copyFile(src, new File("/tmp/Screenshots/" +context.getName()+ ".png"));
		System.out.println("Successfully captured a screenshot");
		}
		catch (Exception ex) {
			System.out.print("Got Exception in screenshot: " + ex);
		}
	}

	@AfterTest
	public void after_test() {
		driver.quit();
	}
}
