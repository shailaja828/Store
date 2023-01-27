package com.classplus.basetests;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import com.classplus.basepages.ClassplusBasePage;

public class ClassplusLoginTest extends ClassplusBaseTest{
	
	private static final Logger logger = Logger.getLogger(ClassplusLoginTest.class.getName());

	@BeforeClass
	@Parameters({ "driveURL", "browser" })
	public void initLogin(String driveURL, String browser) throws Exception {
		logger.info("Starting of initBoodmo method in leaveTest");

		this.driver = this.getWebDriver(browser, WEB_DRIVER.LOGIN_DRIVER);

		this.goToSite(driveURL, driver);
      
		this.classplusbasepage = new ClassplusBasePage(this.driver);
		
		logger.info("Ending of initBoodmo method in leaveTest");

	}
	

}
