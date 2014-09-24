package com.paypal.utils.cb.kafka;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
 
public class ConfigLoader {
	private static Properties configprops=null;
	private static void init(){
		try {
			
			configprops=new Properties();
			File jarPath=new File(ConfigLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		    String propertiesPath=jarPath.getParentFile().getAbsolutePath();
		    System.out.println(" propertiesPath-"+propertiesPath+"/"+Constants.RESOURCEFILE);       
		    InputStream inputStream = new FileInputStream(new File(propertiesPath+"/"+Constants.RESOURCEFILE));
			configprops.load(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getProp(String key){
		if(configprops==null) init();
		return configprops.getProperty(key);
	}
	
	public static Properties getConfigProps(){
		if(configprops==null) init();
		return configprops;
	}
	
	
}
