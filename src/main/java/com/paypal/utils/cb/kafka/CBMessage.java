package com.paypal.utils.cb.kafka;

public class CBMessage {
	String message;
	String topic;
	public CBMessage(){}
	
	public CBMessage(String topic,String message){
		this.topic=topic;
		this.message=message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
}
