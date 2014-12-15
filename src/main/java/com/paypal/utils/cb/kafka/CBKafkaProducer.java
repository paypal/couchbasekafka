package com.paypal.utils.cb.kafka;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * CBKafkaProducer uses TAP Client to connect to Couchbase, 
 * consume message and push to Kafka.
 *
 * @author ssudhakaran
 *
 */
public final class CBKafkaProducer {
	private static final Logger LOGGER = LoggerFactory.getLogger(CBKafkaProducer.class);
	
	//
	private static int MessageCounter=0;
	/**
	 * @link http://kafka.apache.org/api-docs/0.6/kafka/producer/ProducerConfig.html
	 */
	private static ProducerConfig producerConfig;
	
	/**
	 * @link http://people.apache.org/~joestein/kafka-0.7.1-incubating-docs/kafka/producer/Producer.html
	 */
	private static Producer<String, String> producer;

	private static void init(){
		 if(producerConfig==null) { producerConfig=new ProducerConfig(ConfigLoader.getKafkaConfigProps());}
		 if(producer==null) { producer = new Producer<String, String>(producerConfig);}
		 
	}
	
	
	
	/**
	 * Check if we have a valid Kafka producer
	 * @return
	 */
	public static boolean isValidProducer(){
		if(producer !=null) return true;
		else {
			init();
			if(producer !=null) return true;
			else return false;
		}
	}
	
	/**
	 * Public Message to Kafka Queue
	 * @param key - Key to Couchbase Document
	 * @param msg - Body of Couchbase Document.
	 * @throws IOException
	 */
	public static void publishMessage(final String key,final String message) throws IOException{
		CBMessage cbObjMessage=null;
		try {
			
			
				
			//If we need to make any Transformation on the message.
			if(Boolean.parseBoolean(ConfigLoader.getProp(Constants.ENABLETRANSFORMATION))){
				cbObjMessage = CBMessageTransformerFactory.INSTANCE.createCBMessageConverter().convert(key, message);
			}else{
				cbObjMessage=new CBMessageConverter().convert(key, message);
			}
			
			if(LOGGER.isInfoEnabled()){ 
				LOGGER.info("Publishing "+key+" to Topic "+cbObjMessage.getTopic());}

		} catch (Exception e) {
			//If any exception, perform no conversion
		}
		
		if(cbObjMessage.getMessage()!=null && cbObjMessage.getMessage().trim().length()>0){
			
			//We need a unique message id for every 1M messages. used for dedup the messages for that duration(hour). Increment MAX_MSG_COUNTER if we receive > 1M message per hour.
			if(MessageCounter++ >= Constants.MAX_MSG_COUNTER){
				MessageCounter=0;
			}
			
			//Wrap KEY/VALUE in JSON -format {\"KEY\":\"<CBKEY>\",\"VALUE\":<CBVALUE>}
			String cbmessage=Constants.KAFKA_MESSAGE.replace("[CBID]", MessageCounter+"");
			cbmessage=cbmessage.replace("[CBKEY]", key);
			cbmessage=cbmessage.replace("[CBVALUE]", cbObjMessage.getMessage());
			
		//	KeyedMessage<String, String> data = new KeyedMessage<String, String>(ConfigLoader.getKafkaConfigProps().getProperty(Constants.TOPIC_NAME), key, cbmessage);
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(cbObjMessage.getTopic(), key, cbmessage);
			
			//property producer.type indicates async/sync message
			if(data!=null) producer.send(data);
		}
	}
	/**
	 * close producer
	 */
	public static void closeProducer(){
		producer.close();
	}
}
