package com.paypal.cookie.utils;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;


public class ServerCookieData {

	//Plain header
	@JsonProperty("h")
	private CookieHeaders headers = new CookieHeaders();
	
	//hash value of email.
	@JsonProperty("doctype")
	private String doctype="xppp";
	
	//Encrypted body
	@JsonProperty("b")
	private String body;	
	
	
	public CookieHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(CookieHeaders headers) {
		this.headers = headers;
	}
	

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	/**
	 * @return the doctype
	 */
	public String getDoctype() {
		return doctype;
	}

	/**
	 * @param doctype the doctype to set
	 */
	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}
	
	
}

