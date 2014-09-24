package com.paypal.utils.cb.kafka;

public class Constants {
	
	//------COUCHBASE PROPERTIES------
	public static final String BUCKET="cb.bucket";
	public static final String CBSERVER="cb.cbserver";
	public static final String PASSWORD="cb.password";
	public static final String STREAMNAME="cb.streamname";
	public static final String STARTDATE = "cb.startdate";
	public static final String ISFULLDUMP = "cb.fulldump";

	
	//------KAFKA PROPERTIES--------------
	public static final String METADATA_BROKER_LIST="metadata.broker.list";
	public static final String SERIALIZER_CLASS="serializer.class";
	public static final String PARTITIONER_CLASS="partitioner.class";
	public static final String TOPIC_NAME="cookie.topic";
	public static final String REQUEST_REQUIRED_ACKS="request.required.acks";
	public static final String KAFKA_MESSAGE="{\"KEY\":\"<CBKEY>\",\"VALUE\":<CBVALUE>}\n";
	
	public static final String RESOURCEFILE="./config.properties";
	
	//----KEY FOR FILTERING
	public static final String KEYPREFIXFILTER="keyprefixfilter";
	
	//-----Message Converter factory
	public static final String CBMESSAGECONVERTER="CBMessageConverter";
	public static final String ENABLETRANSFORMATION="enableTransformation";
	public static final String ENABLEAVROENCODING="avroEncoding";
	
}
