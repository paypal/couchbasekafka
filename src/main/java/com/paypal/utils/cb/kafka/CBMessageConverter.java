package com.paypal.utils.cb.kafka;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import net.spy.memcached.tapmessage.ResponseMessage;

import com.paypal.cookie.utils.ServerCookieData;
import com.paypal.cookie.utils.CookieHeaders;;

/**
 * Converts Couchbase message into flume friendly Event format. * 
 * @author ssudhakaran
 *
 */
public abstract class CBMessageConverter {
	public String convert(String key,String message){
		return message;
	}
}
