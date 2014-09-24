package com.paypal.cookie.utils;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Representation of cookie in the server.
 *
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class CookieHeaders {

	//fpti visitor id 
	@JsonProperty("vid")
	private String vid;
	
	//Cookie body length 
	@JsonProperty("clen")
	private long clen;
	
	// Version of the data (incremental number generated and maintained inside x-pp-p cookie)
	@JsonProperty("version")
	private long version;
	
	// Updated time stamp (UTC)
	@JsonProperty("updated")
	private long updated;
	
	// Version of the cookie configuration file used to write this document.
	@JsonProperty("configVersion")
	private String configVersion;
	
	@JsonProperty("docType")
	private String docType = "cookie";

	@JsonProperty("ua")
	private String userAgent;
	
	@JsonProperty("h")
	private String host;
	
	//
	@JsonProperty("ref")
	private String referer;
	
	//from where request came.
	@JsonProperty("ru")
	private String requestURL;
	
	//content-type
	@JsonProperty("a")
	private String accept;
	
	//Fetched from request
	@JsonProperty("al")
	private String acceptLanguage;
	
	//checksum of body
	@JsonProperty("cs")
	private long checksum;
	
	@JsonProperty("encr")
	private int encrypted;
	
	//hash of customer email.
	@JsonProperty("eml")
	private String email;
		
	//#of cookies we got from browser
	@JsonProperty("brcount")
	private int brcount;
		
	//#of cookies we got from CB
	@JsonProperty("sercount")
	private int sercount;
	
	//#of cookies added by app
	@JsonProperty("appcount")
	private int appcount;
		
	
	//Size of cookies we got from browser
	@JsonProperty("brsize")
	private int brsize;
		
	//Size of cookies we got from CB
	@JsonProperty("sersize")
	private int sersize;
	
	//Size of cookies added by app
	@JsonProperty("appsize")
	private int appsize;

	//ip address of the client
	@JsonProperty("ip")
	private String ip;
	
	/**
	 * @return the brcount
	 */
	public int getBrcount() {
		return brcount;
	}

	/**
	 * @param brcount the brcount to set
	 */
	public void setBrcount(int brcount) {
		this.brcount = brcount;
	}

	/**
	 * @return the sercount
	 */
	public int getSercount() {
		return sercount;
	}

	/**
	 * @param sercount the sercount to set
	 */
	public void setSercount(int sercount) {
		this.sercount = sercount;
	}

	/**
	 * @return the appcount
	 */
	public int getAppcount() {
		return appcount;
	}

	/**
	 * @param appcount the appcount to set
	 */
	public void setAppcount(int appcount) {
		this.appcount = appcount;
	}

	/**
	 * @return the brsize
	 */
	public int getBrsize() {
		return brsize;
	}

	/**
	 * @param brsize the brsize to set
	 */
	public void setBrsize(int brsize) {
		this.brsize = brsize;
	}

	/**
	 * @return the sersize
	 */
	public int getSersize() {
		return sersize;
	}

	/**
	 * @param sersize the sersize to set
	 */
	public void setSersize(int sersize) {
		this.sersize = sersize;
	}

	/**
	 * @return the appsize
	 */
	public int getAppsize() {
		return appsize;
	}

	/**
	 * @param appsize the appsize to set
	 */
	public void setAppsize(int appsize) {
		this.appsize = appsize;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public long getUpdated() {
		return updated;
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	}
	
	public String getConfigVersion() {
		return configVersion;
	}

	public void setConfigVersion(String configVersion) {
		this.configVersion = configVersion;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getHost() {
		return host;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public long getClen() {
		return clen;
	}

	public void setClen(long clen) {
		this.clen = clen;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}
	
	public long getChecksum() {
		return checksum;
	}

	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}

	public int getEncrypted() {
		return encrypted;
	}

	public void setEncrypted(int encrypted) {
		this.encrypted = encrypted;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		return  "version=" + version +
				",updated=" + updated +
				",clen=" + clen +
				",vid=" + vid +
				",configVersion=" + configVersion +
				",docType=" + docType +
				",userAgent=" + userAgent
				+ ", host=" + host
				+ ", referer=" + referer
				+ ", requestURL=" + requestURL
				+ ", accept=" + accept
				+ ", acceptLanguage=" + acceptLanguage
				+ ", ip=" + ip
			;
	}
	
	
}

