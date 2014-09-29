package com.paypal.utils.cb.kafka;

import java.io.IOException;
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
		String msg=null;
		try {
			
			//If we need to make any Transformation on the message.
			if(Boolean.parseBoolean(ConfigLoader.getProp(Constants.ENABLETRANSFORMATION))){
				msg = CBMessageTransformerFactory.INSTANCE.createCBMessageConverter().convert(key, message);
			}else{
				msg=message;
			}
		} catch (Exception e) {
			//If any exception, perform no conversion
		}
		
		if(msg!=null && msg.trim().length()>0){
			//Wrap KEY/VALUE in JSON -format {\"KEY\":\"<CBKEY>\",\"VALUE\":<CBVALUE>}
			String cbmessage=Constants.KAFKA_MESSAGE.replace("[CBKEY]", key);
			cbmessage=cbmessage.replace("[CBVALUE]", msg);
			
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(ConfigLoader.getKafkaConfigProps().getProperty(Constants.TOPIC_NAME), key, cbmessage);
			
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
