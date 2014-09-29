package com.paypal.utils.cb.kafka;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.naming.ConfigurationException;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.TapClient;

import net.spy.memcached.tapmessage.ResponseMessage;
import net.spy.memcached.tapmessage.TapStream;

/**
 * CBMessage Consumer knows how to connect to CB and extract data. 
 * It uses TAP client to incrementally fetch new messages from Couchbase.
 * @author ssudhakaran
 *
 */
public class CBMessageConsumer {
	/**
	 * Logger 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CBMessageConsumer.class);

	/**
	 * Couchbase TAP client used to read messages from Couchbase.
	 */
	private static TapClient tapClient;	

	/**
	 * Date from which messages should be read from couchbase.
	 */
	private static final long STARTDATE=Long.valueOf(ConfigLoader.getProp(Constants.STARTDATE));

	/**
	 * Couchbase connect URI
	 */
	private transient final List<URI> CBURI;

	/**
	 * Couchbase bucket info
	 */
	private final String bucket;

	/**
	 * Named stream to fetch data from couchbase.
	 */
	private final String streamname;

	/**
	 * Full dump from Coucbase?
	 */
	private boolean fullDump=false;

	/**
	 * Inititalize with connection parameters
	 * @param uri
	 * @param bucket
	 * @param password
	 */
	public CBMessageConsumer(){
		this.CBURI = new ArrayList<URI>();
		String servername=ConfigLoader.getProp(Constants.CBSERVER);
		this.CBURI.add(URI.create(servername));	
		this.bucket=ConfigLoader.getProp(Constants.BUCKET);
		this.streamname=ConfigLoader.getProp(Constants.STREAMNAME);
		this.fullDump=Boolean.parseBoolean(ConfigLoader.getProp(Constants.ISFULLDUMP));
		//initTapClient();
	}

	/**
	 * Overloaded constructor to pass fulldump variable.
	 * @param host
	 * @param bucket
	 * @param fulldump
	 */
	public CBMessageConsumer(String host,String bucket,boolean fulldump){
		this.CBURI = new ArrayList<URI>();
		String servername=host;
		this.CBURI.add(URI.create(servername));	
		this.bucket=bucket;
		this.streamname=ConfigLoader.getProp(Constants.STREAMNAME);
		this.fullDump=fulldump;
		//initTapClient();
	}

	/**
	 * Init TAP Client.
	 */
	private void initTapClient(){
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("INIT "+CBURI.get(0).getHost() +", bucket :"+bucket+", streamname:"+streamname+",STARTDATE:"+STARTDATE);
		}

		//Create TAP Client.
		tapClient=new TapClient(CBURI, bucket, "");
		
		try {
			if(LOGGER.isInfoEnabled()){ 
				LOGGER.info("initTapClient : start date"+STARTDATE);}

			//If we need full dump, get everything.
			if(fullDump){
				tapClient.tapDump(streamname);
			}else{
				//Get message starting from date STARTDATE
				tapClient.tapBackfill(null,-1,0, TimeUnit.MINUTES);
				//tapClient.tapBackfill(null,STARTDATE,0, TimeUnit.MINUTES);
			}
		} catch (ConfigurationException e) {
			if(LOGGER.isErrorEnabled()){ LOGGER.error(e.getExplanation()+e.getMessage());}
			tapClient=null;
		} catch (IOException e) {
			if(LOGGER.isErrorEnabled()){ LOGGER.error(e.getMessage());}
			tapClient=null;
		}
	}

	
	/**
	 * read messages from CB TAP 
	 * @return
	 * @throws IOException 
	 */
	public void run() {
		LOGGER.info("RUNNING Couchbase Consumer");
		
		//If TAP Client is not running.
		if(tapClient==null){
			try{
			initTapClient();
			}catch(com.couchbase.client.vbucket.ConfigurationException e){
				tapClient=null;
				if(LOGGER.isErrorEnabled()){ LOGGER.error("Not able to connect to Couchbase. Will retry in "+ConfigLoader.getProp(Constants.INTERVAL_SEC,Constants.INTERVAL_SEC_DEF)+" seconds.");}
				return;
		 	}
		}		
				
		//If a valid Kafka producer doesn't exist. Try after 2 minutes
		if(!CBKafkaProducer.isValidProducer()){ 
			LOGGER.info("No Kafka Connection. Retry after "+ConfigLoader.getProp(Constants.INTERVAL_SEC,Constants.INTERVAL_SEC_DEF)+" seconds.");
			return;
		}
		
		int iReadCounter=0;
		try{
			//Keep reading from Tap Client
			while(tapClient.hasMoreMessages()){

				//Read message from TAP client
				final ResponseMessage resmessage=tapClient.getNextMessage();

				if(resmessage!=null ) {
					if(resmessage.getValue()!=null){

						iReadCounter++;

						//Publish message to Kafka.
							CBKafkaProducer.publishMessage(resmessage.getKey(),StringUtils.newStringUtf8(resmessage.getValue()));
					
							if(iReadCounter%3==0){
								iReadCounter=0;
								if(LOGGER.isInfoEnabled()) LOGGER.info("TIME :"+new java.util.Date().getTime());
							}
							
					}
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
			if(LOGGER.isErrorEnabled()) LOGGER.error("EXCEPTION. TIME :"+new java.util.Date().getTime());

		}
	}

	public static void main(String[] args) throws IOException{
		CBMessageConsumer cbConsumer=new CBMessageConsumer();
		cbConsumer.run();
		//ScheduledExecutorService fScheduler=Executors.newScheduledThreadPool(Constants.NUM_THREADS); 
		//ScheduledFuture<?> cbConsumerFuture = fScheduler.scheduleWithFixedDelay(cbConsumer, Integer.valueOf(ConfigLoader.getProp(Constants.START_DELAY_SEC,"0")), Integer.valueOf(ConfigLoader.getProp(Constants.INTERVAL_SEC,Constants.INTERVAL_SEC_DEF)), TimeUnit.SECONDS);
	}
}
