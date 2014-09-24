package com.paypal.utils.cb.kafka.test;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.spy.memcached.CASValue;
import net.spy.memcached.internal.OperationFuture;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.couchbase.mock.BucketConfiguration;
import org.couchbase.mock.CouchbaseMock;
import org.couchbase.mock.Bucket.BucketType;
import org.couchbase.mock.client.*;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import com.paypal.utils.cb.kafka.CBMessageConsumer;
public class CBMessageConsumerTest {
	 protected final BucketConfiguration bucketConfiguration = new BucketConfiguration();
	    protected MockClient mockClient;
	    protected CouchbaseMock couchbaseMock;

	    protected final CouchbaseConnectionFactoryBuilder cfb = new CouchbaseConnectionFactoryBuilder();
	    protected CouchbaseClient client;
	    protected CouchbaseConnectionFactory connectionFactory;
	    
	    static {
	        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SunLogger");
	        Logger.getLogger("net.spy.memcached").setLevel(Level.WARNING);
	        Logger.getLogger("com.couchbase.client").setLevel(Level.WARNING);
	        Logger.getLogger("com.couchbase.client.vbucket").setLevel(Level.WARNING);
	    }
	    
	    protected void createMock(@NotNull String name, @NotNull String password) throws Exception {
	        bucketConfiguration.numNodes = 10;
	        bucketConfiguration.numReplicas = 3;
	        bucketConfiguration.name = name;
	        bucketConfiguration.type = BucketType.COUCHBASE;
	        bucketConfiguration.password = password;
	        ArrayList<BucketConfiguration> configList = new ArrayList<BucketConfiguration>();
	        configList.add(bucketConfiguration);
	        couchbaseMock = new CouchbaseMock(0, configList);
	        couchbaseMock.start();
	        couchbaseMock.waitForStartup();

	    }

	    protected void createClients() throws Exception {
	        mockClient = new MockClient(new InetSocketAddress("localhost", 0));
	        couchbaseMock.setupHarakiriMonitor("localhost:" + mockClient.getPort(), false);
	        mockClient.negotiate();

	        List<URI> uriList = new ArrayList<URI>();
	        uriList.add(new URI("http", null, "localhost", couchbaseMock.getHttpPort(), "/pools", "", ""));
	        connectionFactory = cfb.buildCouchbaseConnection(uriList, bucketConfiguration.name, bucketConfiguration.password);
	        client = new CouchbaseClient(connectionFactory);
	    }
	    public void testBulkOperations() throws Exception {
	        Set<String> keyList = new HashSet<String>();
	        Map<String,Long> casMap = new HashMap<String, Long>();
	        List<OperationFuture> futures = new ArrayList<OperationFuture>();

	        for (int ii = 0; ii < 100; ii++) {
	            String s = "Key_" + ii;
	            keyList.add(s);
	            futures.add(client.set(s, s));
	        }

	        for (OperationFuture f : futures) {
	            f.get();
	            assertTrue(f.getStatus().isSuccess());
	            long cas = f.getCas();
	            assertTrue(keyList.contains(f.getKey()));
	            assertTrue(cas != 0);
	            casMap.put(f.getKey(), cas);
	        }

	        Map<String,Object> bulkResult = client.getBulk(keyList);
	        assertEquals(bulkResult.size(), keyList.size());
	        for (Map.Entry<String,Object> kv : bulkResult.entrySet()) {
	            assertTrue(keyList.contains(kv.getKey()));
	            assertEquals(kv.getKey(), kv.getValue());
	            OperationFuture<CASValue<Object>> gFuture = client.asyncGets(kv.getKey());
	            assertTrue(gFuture.get().getCas() == casMap.get(kv.getKey()));
	        }

	        futures.clear();
	        for (String s : keyList) {
	            futures.add(client.delete(s));
	        }

	        for (OperationFuture f : futures) {
	            f.get();
	            assertTrue(f.getStatus().isSuccess());
	            assertNull(client.get(f.getKey()));
	        }
	    }
	    
	@Test
	public void test() throws Exception {
		init();
		testBulkOperations();
		//CBMessageConsumer cbConsumer=new CBMessageConsumer("http://localhost:"+couchbaseMock.getHttpPort()+"/pools","default",true);
		//cbConsumer.read();
	}
	
	public void init() throws Exception{
		 createMock("default", "");
	     createClients();
	}
	
	public void shutdown(){
		 if (client != null) {
	            client.shutdown();
	        }
	        if (couchbaseMock != null) {
	            couchbaseMock.stop();
	        }
	        if (mockClient != null) {
	            mockClient.shutdown();
	        }
	}

}
