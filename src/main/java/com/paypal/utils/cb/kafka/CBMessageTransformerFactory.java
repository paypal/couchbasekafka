package com.paypal.utils.cb.kafka;

/**
 * CBMessageTransformerFactory - Singleton class, reads message from properties file. constructs the Object and return
 * @author ssudhakaran
 *
 */
public enum CBMessageTransformerFactory {
	INSTANCE;
	
	private CBMessageConverter converterClassObj =null;
	
	/**
	 * return the custom converter for the message.
	 * @return
	 */
	public CBMessageConverter createCBMessageConverter(){
		try {
			if(converterClassObj ==null){
				converterClassObj=(CBMessageConverter) Class.forName(ConfigLoader.getProp(Constants.CBMESSAGECONVERTER)).newInstance();
			}
			return  converterClassObj;
		}catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
