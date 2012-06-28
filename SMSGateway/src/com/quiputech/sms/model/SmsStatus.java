package com.quiputech.sms.model;

import java.util.Date;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "SmsStatus")
public class SmsStatus {

	private String uuid;
	private int customerId;
	private String sender;
	private String destination;
	private String message;
	private String status;
	private Date creationTime;
	private Date doneTime;
	private double creditsSpent;

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

	@DynamoDBAttribute(attributeName = "sender")	
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	@DynamoDBAttribute(attributeName = "destination")	
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@DynamoDBAttribute(attributeName = "message")	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@DynamoDBAttribute(attributeName = "status")	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@DynamoDBAttribute(attributeName = "creationTime")	
	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	@DynamoDBAttribute(attributeName = "doneTime")	
	public Date getDoneTime() {
		return doneTime;
	}

	public void setDoneTime(Date doneTime) {
		this.doneTime = doneTime;
	}

	@DynamoDBAttribute(attributeName = "creditsSpent")	
	public double getCreditsSpent() {
		return creditsSpent;
	}

	public void setCreditsSpent(double creditsSpent) {
		this.creditsSpent = creditsSpent;
	}
}
