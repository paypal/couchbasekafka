package com.paypal.utils.cb.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.commons.codec.binary.StringUtils;

import com.paypal.cookie.schema.CookiePPLog;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
/**
 * CBKafkaProducer uses TAP Client to connect to Couchbase, consume message and push to Kafka.
 * TODO: Make it client agnostic. Should be easier to switch to URP when it gets replaced.
 * 
 * @author ssudhakaran
 *
 */
public class CBKafkaProducer {
	private static ProducerConfig producerConfig=null;
	private static Producer<String, String> producer =null;
			 
	private static void init(){
		 if(producerConfig==null)
			 producerConfig=new ProducerConfig(ConfigLoader.getConfigProps());
		 if(producer==null)
			 producer = new Producer<String, String>(producerConfig);
	}
	public static void publishMessage(String key,String msg) throws IOException{
		init();
		String message=null;
		try {
			//If we need to make any Transformation on the message.
			if(Boolean.parseBoolean(ConfigLoader.getProp(Constants.ENABLETRANSFORMATION))){
				//Use factory class to find the transformation class from properties file. 
				message = CBMessageTransformerFactory.getInstance().createCBMessageConverter().convert(key, msg);
			}else {
				message=msg;
			}
		} catch (ClassNotFoundException e) {
			//If no converter is found, perform no conversion
			message=msg;
		}

		if(message !=null){
			
			if(Boolean.parseBoolean(ConfigLoader.getProp(Constants.ENABLEAVROENCODING))){
			
				CookiePPLog cookiePPLog=CookiePPLog.newBuilder().setTimestamp((new Date()).getTime()).setBody(new String(message)).build();
				
				GenericDatumWriter<CookiePPLog> serveWriter = new GenericDatumWriter<CookiePPLog>(cookiePPLog.getSchema());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				Encoder encoder=EncoderFactory.get().binaryEncoder(out,null);
				serveWriter.write(cookiePPLog,encoder);
				encoder.flush();
				
				
				
				String cbmessage=Constants.KAFKA_MESSAGE.replaceAll("<CBKEY>", key);
				cbmessage=cbmessage.replaceAll("<CBVALUE>", StringUtils.newStringUtf8(out.toByteArray()));
				
				//System.out.println("MESSAGE: "+cbmessage);
				//push to Kafka only if not null.
				KeyedMessage<String, String> data = new KeyedMessage<String, String>(ConfigLoader.getProp(Constants.TOPIC_NAME), key, cbmessage);
			//	System.out.println("Pushing >> "+data);
				producer.send(data);
			}else{
				String cbmessage=Constants.KAFKA_MESSAGE.replaceAll("<CBKEY>", key);
				cbmessage=cbmessage.replaceAll("<CBVALUE>", message);
				
				//System.out.println("MESSAGE: "+cbmessage);

				//System.out.println("Pushing "+message);
				KeyedMessage<String, String> data = new KeyedMessage<String, String>(ConfigLoader.getProp(Constants.TOPIC_NAME), key, cbmessage);
				producer.send(data);
				
			}
			
		}
		
		
	}
	public static void closeProducer(){
		producer.close();
	}
}
