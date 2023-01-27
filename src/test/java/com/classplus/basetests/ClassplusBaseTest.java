package com.classplus.basetests;

import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.BeforeSuite;

import com.classplus.basepages.ClassplusBasePage;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ClassplusBaseTest {
	
	private static Map<WEB_DRIVER, WebDriver> webDriverPool = new Hashtable<WEB_DRIVER, WebDriver>();
	protected static Properties testDataProp = null;
	protected static Properties expectedAssertionsProp = null;
	protected static final Logger logger = Logger.getLogger(ClassplusBaseTest.class);
	protected static String browserDriverPath = null;
	protected WebDriver driver = null;
	protected ClassplusBasePage classplusbasepage = null;
		
	protected enum WEB_DRIVER {

		LOGIN_DRIVER, FORGOT_PASSWORD_DRIVER, BUY_INVESTMENT_DRIVER;
	}

	@BeforeSuite
	public void initTestData() {
		
		
		if (testDataProp == null) {
			FileReader testDataReader = null;
			FileReader assertionsReader = null;

			try {
				testDataReader = new FileReader("src/main/resources/testdata.properties");
				assertionsReader = new FileReader("src/main/resources/expectedassertion.properties");

				testDataProp = new Properties();
				testDataProp.load(testDataReader);

				expectedAssertionsProp = new Properties();
				expectedAssertionsProp.load(assertionsReader);

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {

					testDataReader.close();
					assertionsReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected synchronized void quitDriver(WEB_DRIVER webDriver) {
		
		logger.info("Starting of method quitDriver in BaseAutomationTest ");

		WebDriver driver = webDriverPool.get(webDriver);
		
		try {
			if (driver != null) {
				driver.quit();
				driver = null;
				webDriverPool.remove(webDriver);
				logger.debug(webDriver + " Web driver quit successfully in BaseAutomationTest ");
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			driver = null;
		}
		logger.info("Ending of method quitDriver in BaseAutomationTest");
	}

	// @AfterSuite
	protected synchronized void quitAllDrivers() {
		logger.info("Starting of method quitAllDrivers in BaseAutomationTest ");

		WebDriver driver = null;

		synchronized (webDriverPool) {
			if (!webDriverPool.isEmpty()) {
				for (WEB_DRIVER driverKey : webDriverPool.keySet()) {
					logger.debug("Quitting driver key: " + driverKey);
					synchronized (webDriverPool) {
						driver = webDriverPool.get(driverKey);
						try {

							if (driver != null) {
								Thread.sleep(3000);
								driver.quit();
								driver = null;
								webDriverPool.remove(driverKey);
								Thread.sleep(3000);
								logger.debug("Driver quit successfully in BaseAutomationTest ");
							}
						} catch (Exception ex) {
							logger.error(ex.getMessage());
							driver = null;
						}
					}

				}

			}
		}
		logger.info("Ending of method quitAllDrivers in BaseAutomationTest");
	}

	/**
	 * This method is used for get driver
	 * 
	 * @param webDriver
	 * @return
	 */

	protected synchronized WebDriver getWebDriver(String browser, WEB_DRIVER webDriver) {
		logger.info("Starting of method getWebDriver");

		WebDriver driver = webDriverPool.get(webDriver);

		String osPath = System.getProperty("os.name");

		// Use existing driver
		if (driver != null) {
			logger.debug("Using existing web driver " + webDriver);
			return driver;
		}

		
		if (osPath.contains("Linux")) {
			browserDriverPath = "/usr/bin/chromedriver";
			logger.debug("######### In linux condition Using existing web driver Linux ###### ");
			if (browser.equalsIgnoreCase("Firefox")) {
				WebDriverManager.firefoxdriver().setup();
				FirefoxOptions options = new FirefoxOptions();
				options.setHeadless(true);
				options.addArguments("--no-sandbox");
				driver = new FirefoxDriver(options);
			} else {
				logger.debug("######### In Else Chrome browser condition Using existing web driver Linux ###### ");
				/*
				 * WebDriverManager.chromedriver().setup(); ChromeOptions options = new
				 * ChromeOptions(); options.setHeadless(true);
				 * options.addArguments("--no-sandbox"); driver = new ChromeDriver(options);
				 */
				System.setProperty("webdriver.chrome.driver", browserDriverPath);
				ChromeOptions options = new ChromeOptions();
				options.setHeadless(true);
				options.addArguments("--no-sandbox");
				driver = new ChromeDriver(options);

				logger.debug("######### Driver is here  ###### " + driver);

			}
		} else if (osPath.contains("Mac OS X")) {
			browserDriverPath = "/usr/bin/safaridriver";

			if (browserDriverPath.contains("safaridriver")) {
				System.setProperty("webdriver.safari.driver", browserDriverPath);
				driver = new SafariDriver();

				logger.debug("Safari driver path " + browserDriverPath);
			}
		} else {

			if (browser.equalsIgnoreCase("Chrome")) {
				WebDriverManager.chromedriver().setup();
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.addArguments("disable-notifications");
				chromeOptions.addArguments("disable-geolocation");
				driver = new ChromeDriver(chromeOptions);
				
				

			} else if (browser.equalsIgnoreCase("Firefox")) {
				WebDriverManager.firefoxdriver().setup();
				driver = new FirefoxDriver();

			} else if (browser.equalsIgnoreCase("Chromium")) {
				WebDriverManager.chromiumdriver().setup();
				driver = new EdgeDriver();

			} else if (browser.equalsIgnoreCase("IEDriverServer")) {
				WebDriverManager.iedriver().setup();
				driver = new InternetExplorerDriver();

			}
		}

		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);

		logger.info("***************** Driver Successfully Created **************** " + driver.getTitle());

		logger.info("End of method getWebDriver");

		webDriverPool.put(webDriver, driver);

		return driver;
	}

	/**
	 * This method is used for returning chrome browser version.
	 * 
	 * @param driverInfo
	 * @return
	 */

	public String getChromeDriverVersion(String driverInfo) {
		logger.info("Starting of getChromeDriverVersion method ");

		String tVersion = driverInfo.split("is")[2];
		tVersion = tVersion.split("with")[0];
		tVersion = (tVersion.split("\\.")[0]).trim();
		logger.debug("Chrome browser Version : " + tVersion);

		logger.info("Ending of getChromeDriverVersion method ");

		return tVersion;
	}

//	protected void siteLogin(String siteURL, String userName, String password, WebDriver driver) throws Exception {
//		logger.debug("Login URL " + siteURL);
//
//		driver.get(siteURL);
//
//		this.valyuAiLoginPage.loginToValyuAI,(userName, password);
//	}

	protected void goToSite(String siteURL, WebDriver driver) throws Exception {

		driver.get(siteURL);

	}



}
