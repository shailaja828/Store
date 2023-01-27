package com.classplus.basepages;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ClassPlusLoginPage extends ClassplusBasePage{

	public ClassPlusLoginPage(WebDriver driver) {
		super(driver);
	PageFactory.initElements(driver, this);	
	} 
	
	private static final Logger logger = Logger.getLogger(ClassPlusLoginPage.class.getName());

	

	
	

}
