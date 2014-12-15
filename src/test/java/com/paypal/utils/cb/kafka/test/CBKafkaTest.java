package com.paypal.utils.cb.kafka.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import kafka.consumer.ConsumerConfig;

import com.couchbase.client.CouchbaseClient;

public class CBKafkaTest {

	private static final String KAFKA_TOPICNAME="default";
	private static final String KAFKA_BROKER="127.0.0.1";
	private static final int KAFKA_BROKERPORT=9092;
	private static final String BUCKETNAME="beer-sample";
	private static final String CBURL="http://stage2ck01.qa.paypal.com:8091/pools";
	private static final int NOMESSAGES=10;
	private static final String MESSAGEPREFIX="Vinoth_TEST1";
	

	public static void writeToCB() throws IOException, URISyntaxException, InterruptedException, ExecutionException{
		// Connect to the Cluster
		CouchbaseClient client = new CouchbaseClient(Arrays.asList(new URI(CBURL)), BUCKETNAME, "");
		for(int i=0;i<NOMESSAGES ; i++){
			client.set(MESSAGEPREFIX+i, i+"").get();
			if(i%50==0) Thread.sleep(1000);
		}
		client.shutdown();
	}

	private static void readFromKafka() throws Exception{
		/*
		Properties props = new Properties();
        props.put("zookeeper.connect",KAFKA_ZKEEPER );
        props.put("group.id", "group1");
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        ConsumerConfig consumerConfig= new ConsumerConfig(props);
        */
        List<String> seeds = new ArrayList<String>();
        seeds.add(KAFKA_BROKER);
        
        SimpleKafkaConsumer consumer=new SimpleKafkaConsumer();
        consumer.run(NOMESSAGES, KAFKA_TOPICNAME, 0, seeds, KAFKA_BROKERPORT);
        
     //   ConsumerGroupExample example = new ConsumerGroupExample("localhost:2181", "group1", KAFKA_TOPICNAME);
      //  example.run(1);
        

	}
	public static void main(String[] args) throws Exception{
		//Insert to Couchbase
		writeToCB();
		
		//Read from Kafka
		readFromKafka();
	}
}
