package com.quiputech.sms.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jboss.logging.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.GetItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemResult;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.quiputech.sms.dal.MessageLog;
import com.quiputech.sms.messages.PostSmsRequestType;
import com.quiputech.sms.messages.RequestType;
import com.quiputech.sms.messages.StatusInfoType;
import com.quiputech.sms.providers.ems.EMSServiceProvider;

@Stateless
public class Status {

	private static Logger log = Logger.getLogger(Status.class);
	private static Properties props = new Properties();

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
			File propsFile = new File(baseDir + "/standalone/configuration/ejb-status.properties");
			log.info("Looking for EJB Status props in: " + propsFile.getPath());
			if(propsFile.exists())
				props.load(new FileInputStream(propsFile));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
		
	}
	
    /**
     * Default constructor. 
     */
    public Status() {
    	
    }
 
	@SuppressWarnings("unused")
	@Schedule(second="*/10", minute="*", hour="*", dayOfWeek="*",
      dayOfMonth="*", month="*", year="*", info="SMSSender")
    private void scheduledTimeout(final Timer t) {
		
		try {
			
			JAXBContext ctx = JAXBContext.newInstance(PostSmsRequestType.class);
			
			AmazonDynamoDBClient dynamoDB = new AmazonDynamoDBClient();
			AmazonSQS sqs = new AmazonSQSClient();
			
			String queue = props.getProperty("status-sms-queue", DEFAULT_STATUS_QUEUE_URL);
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queue);
			
			int maxNumberOfMessages = Integer.parseInt(props.getProperty("max.number.messages", "10"));
			int visibilityTimeout = Integer.parseInt(props.getProperty("visibility.timeout", "30"));

			receiveMessageRequest.setMaxNumberOfMessages(maxNumberOfMessages);
			receiveMessageRequest.setVisibilityTimeout(visibilityTimeout);

			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			
			for(Message message : messages) {

				String uuid = message.getBody();
				log.info(uuid);
	            GetItemRequest getItemRequest = new GetItemRequest()
					.withTableName("MessageLog")
					.withKey(new Key().withHashKeyElement(new AttributeValue(uuid)).
										withRangeKeyElement(new AttributeValue("1")));
	            
            
				GetItemResult item = dynamoDB.getItem(getItemRequest);
				log.info(item);
				
				
				//
				DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
				MessageLog msglog = mapper.load(MessageLog.class, uuid, "1");
				
				if(msglog.getProvider().equals("EMS")) {
					EMSServiceProvider ems = new EMSServiceProvider();
					String status = ems.checkStatus(msglog.getProviderMessageId());
					log.info(status);
					
					SimpleDateFormat sdf = new SimpleDateFormat();
					
					if(status.contains("DELIVRD")) {
						// save delivered
						msglog.setStatusTime(sdf.format(new Date()));
						msglog.setProviderStatus(status);
						msglog.setStatus("DELIVERED");
						mapper.save(msglog);
					} else if(status.contains("UNDELIV")) {
						// save undelivered
						msglog.setStatusTime(sdf.format(new Date()));
						msglog.setProviderStatus(status);
						msglog.setStatus("UNDELIVERED");
						mapper.save(msglog);
						// sent to gateway again, add related
						String requestxml = msglog.getOriginalRequest();
						Unmarshaller um = ctx.createUnmarshaller();
						PostSmsRequestType request = (PostSmsRequestType) um.unmarshal(new StringReader(requestxml));
						reenqueue(request, uuid);
						
					} else if(status.contains("ATES")) {
						// reenqueue
					}
				}
				
				
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
	
	
	
	public void reenqueue(PostSmsRequestType request, String uuid) throws JAXBException {
		RequestType old = new RequestType();
		old.setAttempt(1);
		old.setProvider("EMS");
		old.setStatus("UNDELIVERED");
		old.setUuid(uuid);

		request.getRelated().getRequest().add(old);
		
		String newuuid = UUID.randomUUID().toString();
		StatusInfoType status = new StatusInfoType();
		status.setUuid(newuuid);
		status.setStatus("ACK");
		request.setInfo(status);
		

		JAXBContext ctx = JAXBContext.newInstance(new Class[] { PostSmsRequestType.class, StatusInfoType.class });
		// marshall modified request
		Marshaller ma = ctx.createMarshaller();
		// marshall request
		StringWriter writer = new StringWriter();
		ma.marshal(request, writer);

		
		
		AmazonSQS sqs = new AmazonSQSClient();
		SendMessageRequest smr = new SendMessageRequest();
		String queue = props.getProperty("post-sms-queue", DEFAULT_POST_QUEUE_URL);
		smr.setQueueUrl(queue);
		smr.setMessageBody(request.toString());
		sqs.sendMessage(smr);
	}
	
	
}