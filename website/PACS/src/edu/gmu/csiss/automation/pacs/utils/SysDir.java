/**
#******************************************************************************
#
# PACS online system
# ---------------------------------------------------------
# Parameterless automatic classification system.
#
#******************************************************************************
*/
package edu.gmu.csiss.automation.pacs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 *Class SysDir.java
 *@author ziheng
 *@time Aug 10, 2019 4:18:19 PM
 *Original aim is to support PACS.
 */
public class SysDir {
	
	public static int worknumber = 1;
	public static String instantiationservletaddress0 = null; //VDP instantiation servlet
	public static String instantiationservletaddress = null; //WorkflowCore instantiation servlet
	public static String executionservletaddress = null;
	public static String registrationaddress = null;
	public static String NOTIFICATION_EMAIL  = null;
    public static String NOTIFICATION_EMAIL_SERVICE_URL = null;
	
	static{
		//initialize from config file
		try {
			BaseTool t = new BaseTool();
			String configfile = t.getClassPath()+File.separator+"config.properties";
			FileInputStream ferr;
			ferr = new FileInputStream(configfile);
			Properties p = new Properties();	
			p.load(ferr);
			ferr.close();
			String number = p.getProperty("workernumber");
			SysDir.worknumber = Integer.parseInt(number);
			instantiationservletaddress0 = p.getProperty("instantiationservletaddress0");
			instantiationservletaddress = p.getProperty("instantiationservletaddress");
			executionservletaddress = p.getProperty("executionservletaddress");
			registrationaddress = p.getProperty("registrationaddress");
			NOTIFICATION_EMAIL = p.getProperty("notify");
			NOTIFICATION_EMAIL_SERVICE_URL = p.getProperty("notificationserviceaddress");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
