package com.quiputech.sms.model;

import java.util.Date;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "PostSmsRequestLog")
public class PostSmsRequestLog {

	private String uuid;
	private int customerId;
	private Date requestTime;
	private String requestXml;
	private String remoteIp;
	private String remoteHostname;
	
	@DynamoDBHashKey(attributeName="uuid")
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@DynamoDBAttribute(attributeName = "customerId")	
	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	@DynamoDBAttribute(attributeName = "requestTime")	
	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	@DynamoDBAttribute(attributeName = "requestXml")
	public String getRequestXml() {
		return requestXml;
	}

	public void setRequestXml(String requestXml) {
		this.requestXml = requestXml;
	}

	@DynamoDBAttribute(attributeName = "remoteIp")	
	public String getRemoteIp() {
		return remoteIp;
	}

	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	@DynamoDBAttribute(attributeName = "remoteHostname")	
	public String getRemoteHostname() {
		return remoteHostname;
	}

	public void setRemoteHostname(String remoteHostname) {
		this.remoteHostname = remoteHostname;
	}

}
