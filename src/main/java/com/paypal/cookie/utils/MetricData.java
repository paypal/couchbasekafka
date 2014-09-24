package com.paypal.cookie.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Metics collected for a Cookie.
 * @author ssudhakaran
 *
 */
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties({"cookieMetrics"})
public class MetricData{
	
	/*
	public MetricData(String cookieName){
		//keyCookieName is a combination of cookiename with date/time/upto hour.
		String keyCookieName=CookieServConstants.COOKIE_ANALYTICS_HEADER+"_"+cookieName+"_"+getCurrentHourUTC();
		this.cookieName=keyCookieName;
		
	}*/
	
	public MetricData(){
		//dummy constuctor for json deserialization
	}
	//CookieName 
	@JsonProperty("cookieName")
	private String cookieName;
	
	//Total Count - Total number of Get operations.
	@JsonProperty("totalGet")
	private AtomicInteger totalGetCount = new AtomicInteger(0);
	
	//Total Count - Total number of Put operations.
	@JsonProperty("totalPut")
	private AtomicInteger totalPutCount = new AtomicInteger(0);
	
	//Total Count - Total number of Put operations.
	@JsonProperty("totalKilled")
	private AtomicInteger totalKilledCookieCount = new AtomicInteger(0);
	
	//Count by Flow getmap - Indicates how many times a particular cookie is fetched by a flow.
	@JsonProperty("flowGetMap")
	private ConcurrentHashMap<String, AtomicInteger> flowgetmap = new ConcurrentHashMap<String, AtomicInteger>();
	//private Map<String, AtomicInteger> flowgetmap = new HashMap<String, AtomicInteger>();
	
	//Count by Flow putmap- Indicates how many times a particular cookie is updated by a flow.
	@JsonProperty("flowPutMap")
	private ConcurrentHashMap<String, AtomicInteger> flowputmap = new ConcurrentHashMap<String, AtomicInteger>();
	//private Map<String, AtomicInteger> flowputmap = new HashMap<String, AtomicInteger>();
	
	//Count by Flow putmap- Indicates how many times a particular cookie is updated by a flow.
	@JsonProperty("flowKillMap")
	private ConcurrentHashMap<String, AtomicInteger> flowkilledcookiemap = new ConcurrentHashMap<String, AtomicInteger>();
	//private Map<String, AtomicInteger> flowkilledcookiemap = new HashMap<String, AtomicInteger>();

	@JsonIgnore
	public Set<String> fetchflowGetMapKeys(){
		return flowgetmap.keySet();
	}
	
	public Map<String, AtomicInteger> getflowGetMap(){
		return flowgetmap;
	}
	
	public Map<String, AtomicInteger> getflowPutMap(){
		return flowputmap;
	}
	
	@JsonIgnore
	public Set<String> fetchflowPutMapKeys(){
		return flowputmap.keySet();
	}


	public AtomicInteger getTotalKilledCookieCount() {
		return totalKilledCookieCount;
	}

	public void setTotalKilledCookieCount(AtomicInteger totalKilledCookieCount) {
		this.totalKilledCookieCount = totalKilledCookieCount;
	}

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName=cookieName;
	}
	
	public void assignCookieName(String cookieName) {
		String keyCookieName="ca_"+cookieName+"_"+getCurrentHourUTC();
		this.cookieName=keyCookieName;
	}
	
	/**
	 * current hour in yyMMddHH format (UTC)
	 * @return
	 */
	private String getCurrentHourUTC(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date());
	}

	public void flush(){
		totalGetCount.set(0);
		totalPutCount.set(0);
		flowgetmap.clear();
		flowputmap.clear();
	}
	
	/**
	 * Increment flow/get count by 1.
	 * @param flowName
	 */
	public void incrementflowGetCount(String flowName){
		if(flowName !=null){
			if(flowgetmap.containsKey(flowName)){
    			AtomicInteger flowCounter=flowgetmap.get(flowName);
    			if(flowCounter!=null)
    				flowCounter.incrementAndGet();
			}else{
				//Initialize with number 1
				flowgetmap.put(flowName, new AtomicInteger(1));
			}
		}
	}
	
	/**
	 * get current flow/get count
	 * @param flowName
	 */
	public int getflowGetCount(String flowName){
		return flowgetmap.get(flowName)!=null?flowgetmap.get(flowName).get():0;
	}
	
	
	/**
	 * Increment flow/get count by 1.
	 * @param flowName
	 */
	public void incrementKilledCookieCount(String flowName){
		if(flowName !=null){
			if(flowkilledcookiemap.containsKey(flowName)){
    			AtomicInteger flowCounter=flowkilledcookiemap.get(flowName);
    			if(flowCounter!=null)
    				flowCounter.incrementAndGet();
			}else{
				//Initialize with number 1
				flowkilledcookiemap.put(flowName, new AtomicInteger(1));
			}
		}
	}
	
	/**
	 * get current flow/get count
	 * @param flowName
	 */
	public int getflowKilledCookieCount(String flowName){
		return flowkilledcookiemap.get(flowName)!=null?flowkilledcookiemap.get(flowName).get():0;
	}
	
	/**
	 * Increment flow/put count
	 * @param flowName
	 */
	public void incrementflowPutCount(String flowName){
		if(flowName !=null){
			if(flowputmap.containsKey(flowName)){
    			AtomicInteger flowCounter=flowputmap.get(flowName);
    			if(flowCounter!=null)
    				flowCounter.incrementAndGet();
			}else{
				//Initialize with number 1
				flowputmap.put(flowName, new AtomicInteger(1));
			}
		}
	}
	
	/**
	 * Get flow/put count
	 * @param flowName
	 * @return
	 */
	public int getflowPutCount(String flowName){
		return flowputmap.get(flowName)!=null?flowputmap.get(flowName).get():0;
	}
	
	/**
	 * Increment Total Get Count
	 */
	public int incrementTotalGetCount() {
		return totalGetCount.incrementAndGet();
    }

	/**
	 * Get Total Get Count
	 * @return
	 */
    public int valueTotalGetCount() {
        return totalGetCount.get();
    }
    
    
    public int incrementTotalKilledCount(){
    	return totalKilledCookieCount.incrementAndGet();
    }
    /**
     * Increment total number of times this cookie is updated.
     * @return
     */
    public int incrementTotalPutCount(){
    	return totalPutCount.incrementAndGet();
    }
    
    /**
     * 
     * @return The number of times this cookie is updated.
     */
    public int valueTotalPutCount(){
    	return totalPutCount.get();
    }
    
    
    public ConcurrentMap<String, AtomicInteger> valueFlowGetMap(){
    //public Map<String, AtomicInteger> valueFlowGetMap(){
    	return flowgetmap;
    }
    
    public ConcurrentMap<String, AtomicInteger> valueFlowPutMap(){
    //public Map<String, AtomicInteger> valueFlowPutMap(){
    	return flowputmap;
    }

	public MetricData merge(MetricData metricData) {
		// TODO Auto-generated method stub
		this.totalGetCount.addAndGet(metricData.totalGetCount.intValue());
		this.totalPutCount.addAndGet(metricData.totalPutCount.intValue());
		
		Set<String> keys = new HashSet<String>();
		keys.addAll(flowgetmap.keySet());
		keys.addAll(metricData.fetchflowGetMapKeys());
		System.out.println("Get Keys:"+keys);
		
		for(String flowName:keys){
			//flowgetmap.replace(flowName,new AtomicInteger(this.getflowGetCount(flowName)+ metricData.getflowGetCount(flowName)));
			flowgetmap.put(flowName,new AtomicInteger(this.getflowGetCount(flowName)+ metricData.getflowGetCount(flowName)));
		}
		
		Set<String> keys2 = new HashSet<String>();
		keys2.addAll(flowputmap.keySet());
		keys2.addAll(metricData.fetchflowPutMapKeys());

		System.out.println("Get Keys:"+keys2);
		
		for(String flowName:keys2){
			//flowputmap.replace(flowName,new AtomicInteger(this.getflowPutCount(flowName)+ metricData.getflowPutCount(flowName)));
			flowputmap.put(flowName,new AtomicInteger(this.getflowPutCount(flowName)+ metricData.getflowPutCount(flowName)));
		}
		return this;
	}
}

