package com.quiputech.sms.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.client.ClientProtocolException;
import org.jboss.logging.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.PutItemResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.quiputech.sms.messages.PostSmsRequestType;
import com.quiputech.sms.providers.ems.EMSServiceProvider;

@Stateless
public class Sender {

	private static Logger log = Logger.getLogger(Sender.class);
	private static Properties props = new Properties();

	private static final int DELAY = 60;
	private static final String DEFAULT_POST_QUEUE_URL = "https://queue.amazonaws.com/382063515626/desa-post-sms-queue";
	private static final String DEFAULT_STATUS_QUEUE_URL ="https://queue.amazonaws.com/382063515626/desa-status-sms-queue";
	
	static {
		if(System.getProperty("aws.accessKeyId") == null)
			System.setProperty("aws.accessKeyId", "AKIAJUNR2HH5SXZ6NONA");
		if(System.getProperty("aws.secretKey") == null)
			System.setProperty("aws.secretKey", "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");
		
		String baseDir = System.getProperty("jboss.home.dir");
		if(baseDir == null)
			baseDir = System.getProperty("user.dir");
		
		try {
			File propsFile = new File(baseDir + "/standalone/configuration/ejb-sender.properties");
			log.info("Looking for EJB Sender props in: " + propsFile.getPath());
			if(propsFile.exists())
				props.load(new FileInputStream(propsFile));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
		
	}
	
	
    /**
     * Default constructor. 
     */
    public Sender() {
    	
    }
	
	@SuppressWarnings("unused")
	@Schedule(second="*/10", minute="*", hour="*", dayOfWeek="*",
      dayOfMonth="*", month="*", year="*", info="SMSSender")
    private void scheduledTimeout(final Timer t) {
		
		try {
			
			JAXBContext ctx = JAXBContext.newInstance(PostSmsRequestType.class);
			
			
			AmazonSQS sqs = new AmazonSQSClient();
			
			String queue = props.getProperty("post-sms-queue", DEFAULT_POST_QUEUE_URL);
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queue);
			
			int maxNumberOfMessages = Integer.parseInt(props.getProperty("max.number.messages", "10"));
			int visibilityTimeout = Integer.parseInt(props.getProperty("visibility.timeout", "30"));

			receiveMessageRequest.setMaxNumberOfMessages(maxNumberOfMessages);
			receiveMessageRequest.setVisibilityTimeout(visibilityTimeout);

			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			
			for(Message message : messages) {

				String body = message.getBody();

				String response = sendSms(ctx, body);
				
				
				
				String receiptHandle = message.getReceiptHandle();
				sqs.deleteMessage(new DeleteMessageRequest(queue, receiptHandle));
			}
			
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
			
		}

		
    }
	
	
	private String sendSms(JAXBContext ctx, String body) throws JAXBException, ClientProtocolException, IOException {
		
		log.info(body);
		Unmarshaller um = ctx.createUnmarshaller();
		StringReader reader = new StringReader(body);
		PostSmsRequestType request = (PostSmsRequestType) um.unmarshal(reader);

		
		return sendThroughEMS(request, body);
	}
	
	private String sendThroughEMS(PostSmsRequestType request, String body) throws ClientProtocolException, IOException, JAXBException {
		
		String sender = request.getSms().getOrigin();
		String destination = request.getSms().getDestination();
		String message = request.getSms().getMessage();
		
		String uuid = request.getInfo().getUuid();
		String customerId = String.valueOf(request.getAuthorization().getCustomerId());
		
		EMSServiceProvider ems = new EMSServiceProvider();
		String output = ems.sendSms(sender, destination, message);
		
		String[] tokens = output.split("::");
		String responseId = tokens.length > 1 ? tokens[1] : null;
		
		
		SimpleDateFormat sdf = new SimpleDateFormat();
		Date now = new Date();
		AmazonDynamoDBClient dynamoDB = new AmazonDynamoDBClient();
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("uuid", new AttributeValue(uuid));
		item.put("customerId", new AttributeValue(customerId));
		item.put("status", new AttributeValue("SENT"));
		item.put("sent-time", new AttributeValue(sdf.format(now)));
		item.put("status-time", new AttributeValue(sdf.format(now)));
		item.put("sender", new AttributeValue(sender));
		item.put("destination", new AttributeValue(destination));
		item.put("message", new AttributeValue(message));
		item.put("original-request", new AttributeValue(body));
		item.put("provider", new AttributeValue("EMS"));
		item.put("provider-status", new AttributeValue("CP"));
		item.put("provider-message-id", new AttributeValue(tokens[1].trim()));
		item.put("provider-response", new AttributeValue(output));
		
		
        PutItemRequest putItemRequest = new PutItemRequest("MessageLog", item);
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);		
        log.info(output + putItemResult);
        
        
        // enqueue status request
        log.info("enqueue status " + uuid);
        String statusQueue = props.getProperty("status-sms-queue", DEFAULT_STATUS_QUEUE_URL);
		AmazonSQS sqs = new AmazonSQSClient();
		SendMessageRequest smr = new SendMessageRequest();
		smr.setQueueUrl(statusQueue);
		smr.setDelaySeconds(DELAY);
		smr.setMessageBody(uuid);
		sqs.sendMessage(smr);        
		return responseId;
		
	}
	
}