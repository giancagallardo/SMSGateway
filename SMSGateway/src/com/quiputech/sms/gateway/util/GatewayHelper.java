package com.quiputech.sms.gateway.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.quiputech.sms.messages.AuthorizationType;
import com.quiputech.sms.messages.PostSmsRequestType;
import com.quiputech.sms.messages.SmsType;
import com.quiputech.sms.messages.StatusInfoType;

public class GatewayHelper {

	private static final String ACKNOWLEDGED = "ACK";
	private static final String QUEUE_URL = "https://queue.amazonaws.com/382063515626/post-sms-queue";
	
	static {
		System.setProperty("aws.accessKeyId", "AKIAJUNR2HH5SXZ6NONA");
		System.setProperty("aws.secretKey", "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");
	}
	
	public static String enqueuePost(String token, int customerId, String origin, String destination, String message) throws JAXBException {

		// build post
		PostSmsRequestType smsrequest = new PostSmsRequestType();
		// auth
		AuthorizationType auth = new AuthorizationType();
		auth.setCustomerId(customerId);
		auth.setToken(token);
		smsrequest.setAuthorization(auth);
		// sms
		SmsType sms = new SmsType();
		sms.setOrigin(origin);
		sms.setDestination(destination);
		sms.setMessage(message);
		smsrequest.setSms(sms);		
		// info
		String uuid = UUID.randomUUID().toString();
		StatusInfoType status = new StatusInfoType();
		status.setUuid(uuid);
		status.setStatus(ACKNOWLEDGED);
		smsrequest.setInfo(status);
		
		
		// send to queue
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { PostSmsRequestType.class, StatusInfoType.class });

		return enqueueToSQS(ctx, smsrequest, status);
	}
	
	public static String enqueuePost(InputStream is) throws JAXBException {
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { PostSmsRequestType.class, StatusInfoType.class });
		
		Unmarshaller um = ctx.createUnmarshaller();
		PostSmsRequestType smsrequest = (PostSmsRequestType) um.unmarshal(is);
		
		// add info
		String uuid = UUID.randomUUID().toString();
		StatusInfoType status = new StatusInfoType();
		status.setUuid(uuid);
		status.setStatus(ACKNOWLEDGED);
		smsrequest.setInfo(status);
		
		return enqueueToSQS(ctx, smsrequest, status);
		
	}
	
	
	private static String enqueueToSQS(JAXBContext ctx, PostSmsRequestType smsrequest, StatusInfoType status) throws JAXBException {

		// marshall modified request
		Marshaller ma = ctx.createMarshaller();
		// marshall request
		StringWriter request = new StringWriter();
		ma.marshal(smsrequest, request);
		// marshall response
		StringWriter response = new StringWriter();
		ma.marshal(status, response);

		AmazonSQS sqs = new AmazonSQSClient();
		SendMessageRequest smr = new SendMessageRequest();
		smr.setQueueUrl(QUEUE_URL);
		smr.setMessageBody(request.toString());
		sqs.sendMessage(smr);
		
		return response.toString();
		
		
	}
}
