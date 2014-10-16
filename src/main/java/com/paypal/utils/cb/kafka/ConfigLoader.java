package com.paypal.utils.cb.kafka;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
 
public class ConfigLoader {
	private static Properties configprops=null;
	private static Properties kafkaconfigprops=null;
	private static Map<String,String> msgConverterMap=null;
	private static void init(){
		try {
			
			configprops=new Properties();
			kafkaconfigprops=new Properties();
			File jarPath=new File(ConfigLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		    String propertiesPath=jarPath.getParentFile().getAbsolutePath();

			configprops.load(new FileInputStream(new File(propertiesPath+"/"+Constants.RESOURCEFILE)));
			kafkaconfigprops.load(new FileInputStream(new File(propertiesPath+"/"+Constants.RESOURCEFILE_KAFKA)));
			
			/*
			if(Boolean.parseBoolean(ConfigLoader.getProp(Constants.ENABLETRANSFORMATION))){
				msgConverterMap=new HashMap<String,String>();
				String converterAndKeys=configprops.getProperty(Constants.CBMESSAGECONVERTER);
				if(converterAndKeys!=null){
					String[] keys=converterAndKeys.split(",");
					for(String key:keys){
						String[] msgkeys=key.split(":");
						msgConverterMap.put(msgkeys[0], msgkeys[1]);
					}
				}
			}*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getProp(String key){
		if(configprops==null) init();
		return configprops.getProperty(key);
	}
	
	public static String getKafkaProp(String key){
		if(kafkaconfigprops==null) init();
		return kafkaconfigprops.getProperty(key);
	}
	
	public static String getProp(String key,String defaultVal){
		if(configprops==null) init();
		String propertyVal= getProp( key);
		if(propertyVal==null) {
			propertyVal= defaultVal;
		}
		return propertyVal;
	}
	
	public static Properties getConfigProps(){
		if(configprops==null) init();
		return configprops;
	}

	public static Properties getKafkaConfigProps(){
		if(kafkaconfigprops==null) init();
		return kafkaconfigprops;
	}
	
	public static Map<String, String> getMsgConverterMap() {
		return msgConverterMap;
	}
	
	
	
}
