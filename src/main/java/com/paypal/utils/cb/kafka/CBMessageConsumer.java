package com.paypal.utils.cb.kafka;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.naming.ConfigurationException;

import org.apache.commons.codec.binary.StringUtils;
import org.couchbase.mock.CouchbaseMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.TapClient;

import net.spy.memcached.tapmessage.ResponseMessage;
import net.spy.memcached.tapmessage.TapStream;

/**
 * CBMessage Consumer knows how to connect to CB and extract data. It uses TAP client to incrementally fetch new messages from Couchbase.
 * @author ssudhakaran
 *
 */
public class CBMessageConsumer {
	private static final Logger logger = LoggerFactory.getLogger(CBMessageConsumer.class);
	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetDecoder decoder = charset.newDecoder();
	private TapClient tapClient=null;	
	private long startdate=0L;
	private  List<URI> uri=null;
	private  String bucket="";
	private  String password="";
	private  String streamname="";
	private  String filterPattern="";
	private boolean fullDump=false;
	boolean filterEnabled=false;

	/**
	 * Inititalize with connection parameters
	 * @param uri
	 * @param bucket
	 * @param password
	 */
	public CBMessageConsumer(){
		this.uri = new ArrayList<URI>();
		String servername=ConfigLoader.getProp(Constants.CBSERVER);
		this.uri.add(URI.create(servername));	
		this.bucket=ConfigLoader.getProp(Constants.BUCKET);
		this.streamname=ConfigLoader.getProp(Constants.STREAMNAME);
		this.startdate=Long.valueOf(ConfigLoader.getProp(Constants.STARTDATE));
		this.fullDump=Boolean.parseBoolean(ConfigLoader.getProp(Constants.ISFULLDUMP));
		initTapClient();
	}
	
	public CBMessageConsumer(String host,String bucket,boolean fulldump){
		this.uri = new ArrayList<URI>();
		String servername=host;
		this.uri.add(URI.create(servername));	
		this.bucket=bucket;
		this.fullDump=fulldump;
		initTapClient();
	}
	
	private void initTapClient(){
		System.out.println("INIT "+uri.get(0).getHost() +", bucket :"+bucket+", streamname:"+streamname+",startdate:"+startdate+",filterpattern :"+filterPattern);
		tapClient=new TapClient(uri, bucket, password);
		try {
			System.out.println("initTapClient : start date"+startdate);
			TapStream stream=null;
			
			if(fullDump){
				tapClient.tapDump(streamname);
			}else{
					tapClient.tapBackfill(null,startdate,0, TimeUnit.MINUTES);
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * read messages from CB TAP 
	 * @return
	 * @throws IOException 
	 */
	public void read() {
		int iCounter=0;
		while(tapClient.hasMoreMessages()){
			
			
			ResponseMessage resmessage=tapClient.getNextMessage();

			if(resmessage!=null ) {
				//System.out.println("tap client has messages.."+resmessage.getValue());
				if(resmessage.getValue()!=null)iCounter++;
				/*if(iCounter%1000==0){
					System.out.println("Pushed "+iCounter+" messages to topic: "+ConfigLoader.getProp(Constants.TOPIC_NAME));
				}*/
				//Publish message to Kafka.
				try{
				CBKafkaProducer.publishMessage(resmessage.getKey(),StringUtils.newStringUtf8(resmessage.getValue()));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException{
		CBMessageConsumer cbConsumer=new CBMessageConsumer();
		cbConsumer.read();
	}


}
