package com.paypal.utils.cb.kafka;

/**
 * CBMessageTransformerFactory - Singleton class, reads message from properties file. constructs the Object and return
 * @author ssudhakaran
 *
 */
public class CBMessageTransformerFactory {
	private Class<CBMessageConverter> converterClass =null;
	private static CBMessageTransformerFactory cbMessageTransformerFactory=null;
	
	@SuppressWarnings("unchecked")
	private CBMessageTransformerFactory() throws ClassNotFoundException{
		
		String messageImplClass=ConfigLoader.getProp(Constants.CBMESSAGECONVERTER);
		converterClass=(Class<CBMessageConverter>) Class.forName(messageImplClass);
	}
	
	/**
	 * get Instance method with double check locking.
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static CBMessageTransformerFactory getInstance() throws ClassNotFoundException{
		
		if(cbMessageTransformerFactory==null){
			synchronized(CBMessageTransformerFactory.class){
				if(cbMessageTransformerFactory==null)
					cbMessageTransformerFactory=new CBMessageTransformerFactory();
			}
		}
		return cbMessageTransformerFactory;
	}
	/**
	 * return the custom converter for the message.
	 * @return
	 */
	public CBMessageConverter createCBMessageConverter(){
		try {
			return converterClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
}
