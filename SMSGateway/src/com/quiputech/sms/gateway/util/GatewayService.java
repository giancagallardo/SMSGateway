package com.quiputech.sms.gateway.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.jboss.logging.Logger;

import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.quiputech.sms.model.PostSmsRequestLog;
import com.quiputech.sms.model.SmsStatus;
import com.quiputech.sms.xml.AuthorizationType;
import com.quiputech.sms.xml.PostSmsRequestType;
import com.quiputech.sms.xml.PostSmsResponseType;
import com.quiputech.sms.xml.SmsType;

public class GatewayService {

	private static final Logger log = Logger.getLogger(GatewayService.class);
	private static final String ACKNOWLEDGED = "ACK";
	private static final JAXBContext ctx = initContext();

	private static JAXBContext initContext() {
		try {
			return JAXBContext.newInstance(PostSmsRequestType.class,
					PostSmsResponseType.class);
		} catch (JAXBException e) {
			log.error(e.getLocalizedMessage(), e);
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	private static boolean logPostSmsRequest(DynamoDBMapper mapper,
			String uuid, int customerId, String requestXml,
			GregorianCalendar calendar, String remoteIp, String remoteHostname) {
		PostSmsRequestLog postSmsRequest = new PostSmsRequestLog();
		postSmsRequest.setUuid(uuid);
		postSmsRequest.setCustomerId(customerId);
		postSmsRequest.setRequestTime(calendar.getTime());
		postSmsRequest.setRemoteIp(remoteIp);
		postSmsRequest.setRemoteHostname(remoteHostname);
		postSmsRequest.setRequestXml(requestXml);
		mapper.save(postSmsRequest);
		return true;
	}

	private static boolean logInitialSmsStatus(DynamoDBMapper mapper,
			String uuid, int customerId, String sender, String destination,
			String message) {
		SmsStatus smsStatus = new SmsStatus();
		smsStatus.setUuid(uuid);
		smsStatus.setCustomerId(customerId);
		smsStatus.setStatus(ACKNOWLEDGED);
		smsStatus.setSender(sender);
		smsStatus.setDestination(destination);
		smsStatus.setMessage(message);
		smsStatus.setCreationTime(new Date());
		smsStatus.setDoneTime(null);
		smsStatus.setCreditsSpent(0);
		mapper.save(smsStatus);
		return true;
	}

	private static String buildResponseXml(Marshaller marshaller, String uuid,
			GregorianCalendar calendar) throws DatatypeConfigurationException,
			JAXBException {
		// prepare response
		PostSmsResponseType response = new PostSmsResponseType();
		response.setUuid(uuid);
		response.setTimestamp(DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(calendar));
		response.setStatus(ACKNOWLEDGED);

		StringWriter responseXml = new StringWriter();
		marshaller.marshal(response, responseXml);
		return responseXml.toString();
	}

	private static String buildRequestXml(Marshaller marshaller,
			PostSmsRequestType request) throws JAXBException {
		StringWriter requestXml = new StringWriter();
		marshaller.marshal(request, requestXml);
		return requestXml.toString();
	}

	private static boolean enqueueToSQS(AmazonSQS sqs, String uuid) {
		SendMessageRequest smr = new SendMessageRequest();
		String queue = ConfigurationService.getInstance().getProperty(
				"post-sms-queue", ConfigurationService.DEFAULT_POST_QUEUE_URL);
		smr.setQueueUrl(queue);
		smr.setMessageBody(uuid);
		sqs.sendMessage(smr);
		return true;
	}

	public static PostSmsRequestType buildPostSmsRequest(String token,
			int customerId, String sender, String destination, String message) {

		PostSmsRequestType request = new PostSmsRequestType();
		// set authorization
		AuthorizationType authorization = new AuthorizationType();
		authorization.setCustomerId(customerId);
		authorization.setToken(token);
		request.setAuthorization(authorization);
		// set sms
		SmsType sms = new SmsType();
		sms.setSender(sender);
		sms.setDestination(destination);
		sms.setMessage(message);
		request.setSms(sms);
		// return
		return request;
	}

	public static PostSmsRequestType buildPostSmsRequest(InputStream input)
			throws JAXBException {
		Unmarshaller um = ctx.createUnmarshaller();
		PostSmsRequestType postSmsRequest = (PostSmsRequestType) um
				.unmarshal(input);
		return postSmsRequest;
	}

	public static String enqueuePostSmsRequest(PostSmsRequestType request,
			String remoteIp, String remoteHostname)
			throws DatatypeConfigurationException, JAXBException {

		// set drivers
		AmazonSQS sqs = new AmazonSQSClient();
		AmazonDynamoDBClient dynamoDb = new AmazonDynamoDBClient();
		DynamoDBMapper mapper = new DynamoDBMapper(dynamoDb);
		Marshaller marshaller = ctx.createMarshaller();

		// create uuid
		String uuid = UUID.randomUUID().toString();
		GregorianCalendar calendar = new GregorianCalendar();

		// process request
		// log request to DB
		String requestXml = buildRequestXml(marshaller, request);
		logPostSmsRequest(mapper, uuid, request.getAuthorization()
				.getCustomerId(), requestXml, calendar, remoteIp,
				remoteHostname);
		// write smsStatus
		logInitialSmsStatus(mapper, uuid, request.getAuthorization()
				.getCustomerId(), request.getSms().getSender(), request
				.getSms().getDestination(), request.getSms().getMessage());
		// write to queue
		enqueueToSQS(sqs, uuid);

		return buildResponseXml(marshaller, uuid, calendar);
	}

}
