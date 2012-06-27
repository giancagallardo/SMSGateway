package com.quiputech.sms.gateway.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jboss.logging.Logger;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.quiputech.sms.messages.AuthorizationType;
import com.quiputech.sms.messages.PostSmsRequestType;
import com.quiputech.sms.messages.SmsType;
import com.quiputech.sms.messages.StatusInfoType;

public class GatewayHelper {

	private static final String ACKNOWLEDGED = "ACK";
	//private static final String DEFAULT_QUEUE_URL = "https://queue.amazonaws.com/382063515626/post-sms-queue";
	private static final String DEFAULT_QUEUE_URL = "https://queue.amazonaws.com/382063515626/desa-post-sms-queue";
	private static Logger log = Logger.getLogger(GatewayHelper.class);
	private static Properties props = new Properties();
	
	static {
		if(System.getProperty("aws.accessKeyId") == null)
			System.setProperty("aws.accessKeyId", "AKIAJUNR2HH5SXZ6NONA");
		if(System.getProperty("aws.secretKey") == null)
			System.setProperty("aws.secretKey", "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");
		// pressumed to be deployed in JBoss
		String baseDir = System.getProperty("jboss.home.dir");
		if(baseDir == null)
			baseDir = System.getProperty("user.dir");
		try {
			File propsFile = new File(baseDir + "/standalone/configuration/gateway.properties");
			log.info("Looking for SMSGateway props in: " + propsFile.getPath());
			if(propsFile.exists())
				props.load(new FileInputStream(propsFile));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
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
		String queue = props.getProperty("post-sms-queue", DEFAULT_QUEUE_URL);
		smr.setQueueUrl(queue);
		smr.setMessageBody(request.toString());
		sqs.sendMessage(smr);
		
		return response.toString();
		
		
	}
}
