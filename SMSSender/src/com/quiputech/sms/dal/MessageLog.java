package com.quiputech.sms.dal;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="MessageLog")
public class MessageLog {
	
	private String uuid;
	private String customerId;
	private String providerStatus;
	private String status;
	private String providerResponse;
	private String sentTime;
	private String provider;
	private String destination;
	private String sender;
	private String message;
	private String providerMessageId;
	private String originalRequest;
	private String statusTime;
	
	@DynamoDBHashKey(attributeName="uuid")  
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@DynamoDBRangeKey(attributeName="customerId")
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	@DynamoDBAttribute(attributeName="provider-status")
	public String getProviderStatus() {
		return providerStatus;
	}
	public void setProviderStatus(String providerStatus) {
		this.providerStatus = providerStatus;
	}

	@DynamoDBAttribute(attributeName="status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@DynamoDBAttribute(attributeName="provider-response")
	public String getProviderResponse() {
		return providerResponse;
	}
	public void setProviderResponse(String providerResponse) {
		this.providerResponse = providerResponse;
	}

	@DynamoDBAttribute(attributeName="sent-time")
	public String getSentTime() {
		return sentTime;
	}
	public void setSentTime(String sentTime) {
		this.sentTime = sentTime;
	}
	@DynamoDBAttribute(attributeName="provider")
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	@DynamoDBAttribute(attributeName="destination")
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	@DynamoDBAttribute(attributeName="sender")
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	@DynamoDBAttribute(attributeName="message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@DynamoDBAttribute(attributeName="provider-message-id")
	public String getProviderMessageId() {
		return providerMessageId;
	}
	public void setProviderMessageId(String providerMessageId) {
		this.providerMessageId = providerMessageId;
	}
	@DynamoDBAttribute(attributeName="original-request")
	public String getOriginalRequest() {
		return originalRequest;
	}
	public void setOriginalRequest(String originalRequest) {
		this.originalRequest = originalRequest;
	}
	
	@DynamoDBAttribute(attributeName="status-time")
	public String getStatusTime() {
		return statusTime;
	}
	public void setStatusTime(String statusTime) {
		this.statusTime = statusTime;
	}
}
 