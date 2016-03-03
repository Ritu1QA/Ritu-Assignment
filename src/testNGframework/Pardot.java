package testNGframework;

import static org.junit.Assert.assertEquals;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * 
 * Pardot test
 * 
 * @author Ritu Raj
 *
 */



public class Pardot {
	private WebDriver driver;
	private String baseUrl;
	public static WebDriverWait block;
	
	
	@Parameters({"url"})
	@BeforeClass(alwaysRun = true)
	public void setUp(String url) throws Exception { 
		 // launch firefox and direct it to the Base URL
		 driver = new FirefoxDriver();
		 block = new WebDriverWait(driver, 15);
		 baseUrl = url;
		 driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		 driver.get(baseUrl + "/");
	}

	@Parameters({"username","password","listName"})
	@Test(priority=1)
	public void verifyDuplicateListMsg(String username,String password,String listName) throws Exception
	{	
		/*
		* Log in to Pardot (https://pi.pardot.com, Username: pardot.applicant@pardot.com, Password: Applicant2012)
		*/
		login(username,password);
		

		/*
		* Create a list with a random name (Marketing > Segmentation > Lists)
		*/
		navigateToLists();
		addList(listName);
		
		/*
		* Attempt to create another list with that same name and ensure the system correctly gives a validation failure
		*/
		navigateToLists();
		addList(listName);
		String actual = getValidationErrMsg();
		String expected ="Please correct the errors below and re-submit";
		Assert.assertEquals(actual, expected);
		
		/*
		* Rename the original list
		*/
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	    RenameLists(listName);
	    
	    
	    /*
		* Ensure the system allows the creation of another list with the original name now that the original list is renamed
		*/
		navigateToLists();
		addList(listName);
	}
	
	
	@Parameters({"username","password","listName"})
	@Test(priority=2)
	//(dependsOnMethods={"verifyDuplicateListMsg"},alwaysRun=true)
	public void verifyNewProspectCreation(String username,String password,String listName)
	{
		/*
		* Create a new prospect (Prospect > Prospect List
		*/
		
		createNewProspect(listName);
		
	}
	
	
	/*
	* Send a text only email to the list (Marketing > Emails)
	*/
	
	@Parameters({"username","password","listName"})
	@Test (priority=3)
	public void verifyEmailCreation(String username,String password,String listName)
	{
		
		createEmail(listName);
		testLogout();

	}

	
	
	public void login(String username,String password)
	{ 
		//Set email id
		driver.findElement(By.id("email_address")).clear();
		//Set Password
		driver.findElement(By.id("email_address")).sendKeys("pardot.applicant@pardot.com");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("Applicant2012");
		driver.findElement(By.name("commit")).click();
		driver.manage().window().maximize();
	}
	
	
	public void navigateToLists() 
	{
		//Navigate too Lists page
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.get(baseUrl + "/list");

	}
	
	
	public void addList(String listName)
	{
        //Add List
		driver.findElement(By.cssSelector("i.icon-plus")).click();
		driver.findElement(By.id("name")).clear();
		driver.findElement(By.id("name")).sendKeys(listName);
		driver.findElement(By.xpath("(//span[@onclick='AssetChooserApp.chooseFolder(jQuery(this).parent())'])[2]")).click();
		driver.findElement(By.cssSelector("span[title=\"Email Test\"]")).click();
		driver.findElement(By.id("select-asset")).click();

		try{
			driver.findElement(By.id("save_information")).click();
			driver.findElement(By.linkText("Cancel")).click();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	

    public void RenameLists(String listName){
    	//Rename Original list
		driver.findElement(By.id("listx_table_filter")).sendKeys(listName);
		driver.findElement(By.xpath("//a[text()='"+ listName +"']")).click();
		driver.findElement(By.xpath("//a[text()='Edit']")).click();		
		// Wait until list information modal appears
		WebElement infoList = block.until(ExpectedConditions.visibilityOfElementLocated(By.id("information_modal")));
				
		// Locate text field for name, clear the original name and create a new name
		infoList.findElement(By.id("name")).clear();
		infoList.findElement(By.id("name")).sendKeys(listName + "123");
		infoList.findElement(By.id("save_information")).click();	
    
    }
    
    
	public void createNewProspect(String listName)
	{
		
		driver.get(baseUrl + "/prospect");
		driver.findElement(By.cssSelector("#pr_link_create")).click();
		driver.findElement(By.id("default_field_3361")).clear();
		//Enter first
		driver.findElement(By.id("default_field_3361")).sendKeys("Test");
		driver.findElement(By.id("default_field_3371")).clear();
		//Enter Last name
		driver.findElement(By.id("default_field_3371")).sendKeys("Name");
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys(listName + "@testmail.com");
		new Select(driver.findElement(By.id("campaign_id"))).selectByVisibleText("Adil Yellow Jackets");
		new Select(driver.findElement(By.id("profile_id"))).selectByVisibleText("Adil Yellow Jackets 2");
		driver.findElement(By.name("commit")).click();
		
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
		driver.findElement(By.linkText("Select a list to add...")).click();
		driver.findElement(By.cssSelector("a.chzn-single.chzn-default > span")).click();
		driver.findElement(By.xpath("//div[@id='selKN0_chzn']/a/span")).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElement(By.cssSelector("div.chzn-search > input[type=\"text\"]")).clear();
		driver.findElement(By.cssSelector("div.chzn-search > input[type=\"text\"]")).sendKeys(listName);
		
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElement(By.id("selKN0_chzn_o_138")).click();
		driver.findElement(By.name("commit")).click();
		
		
	}
	
		public void createEmail(String listName)
	{
		driver.get(baseUrl + "/email/draft/edit");
		WebElement Email = block.until(ExpectedConditions.visibilityOfElementLocated(By.id("information_modal")));	
		
		//add email title
		Email.findElement(By.id("name")).sendKeys(listName);
		
		//Select an email campaign
		Email.findElement(By.xpath("//div[@data-placeholder-text='Choose a Campaign']")).click();
		
		WebElement campaignDropDown= block.until(ExpectedConditions.visibilityOfElementLocated(By.id("asset-chooser-app-modal")));		
		
		campaignDropDown.findElement(By.className("filter-by")).sendKeys("Adil Yellow Jackets");
		
		WebElement campaign = block.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h4[text() = ' " + "Adil Yellow Jackets" + "']")));
		campaign.click();
		
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);
		campaignDropDown.findElement(By.cssSelector("#select-asset")).click();
	    
		//Select radio button to set email type to text only
		Email.findElement(By.id("email_type_text_only")).click();
				
		//Disable template option
		Email.findElement(By.id("from_template")).click();
				
		//Save the information
		Email.findElement(By.id("save_information")).click();
		
	}


	

	
	public String getValidationErrMsg()
	{

		return driver.findElement(By.cssSelector("#li_form_update > div.alert.alert-error")).getText();

	}

	@Test
	public void testLogout()
	{
		WebElement menu= driver.findElement(By.id("acct-tog"));
		Actions action = new Actions(driver);
		action.moveToElement(menu).perform();
		action.build().perform();
		driver.findElement(By.linkText("Sign Out")).click();
	}
	
	@AfterClass(alwaysRun = true)
	public void tearDown(String url) throws Exception {
		driver.findElement(By.linkText("Sign Out")).click();
		driver.close();
	}
	
}
