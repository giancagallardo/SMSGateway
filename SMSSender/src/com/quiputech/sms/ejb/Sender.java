package com.quiputech.sms.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.client.ClientProtocolException;
import org.jboss.logging.Logger;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.quiputech.sms.messages.PostSmsRequestType;
import com.quiputech.sms.providers.ems.EMSServiceProvider;

@Stateless
public class Sender {

	private static Logger log = Logger.getLogger(Sender.class);
	private static Properties props = new Properties();

	private static final String QUEUE_URL = "https://queue.amazonaws.com/382063515626/post-sms-queue";
	
	static {
		if(System.getProperty("aws.accessKeyId") == null)
			System.setProperty("aws.accessKeyId", "AKIAJUNR2HH5SXZ6NONA");
		if(System.getProperty("aws.secretKey") == null)
			System.setProperty("aws.secretKey", "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");
		
		String baseDir = System.getProperty("jboss.home.dir");
		if(baseDir == null)
			baseDir = System.getProperty("user.dir");
		
		try {
			File propsFile = new File(baseDir + "/ems.properties");
			log.info("Looking for EMS props in: " + propsFile.getPath());
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
	@Schedule(second="*/5", minute="*", hour="*", dayOfWeek="*",
      dayOfMonth="*", month="*", year="*", info="SMSSender")
    private void scheduledTimeout(final Timer t) {
		
		try {

			AmazonSQS sqs = new AmazonSQSClient();
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(QUEUE_URL);
			
			int maxNumberOfMessages = Integer.parseInt(props.getProperty("max.number.messages", "10"));
			int visibilityTimeout = Integer.parseInt(props.getProperty("visibility.timeout", "30"));

			receiveMessageRequest.setMaxNumberOfMessages(maxNumberOfMessages);
			receiveMessageRequest.setVisibilityTimeout(visibilityTimeout);

			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			
			for(Message message : messages) {

				String body = message.getBody();

				sendSms(body);
				
				String receiptHandle = message.getReceiptHandle();
				sqs.deleteMessage(new DeleteMessageRequest(QUEUE_URL, receiptHandle));
			}
			
			
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
			
		}

		
    }
	
	
	private String sendSms(String body) throws JAXBException, ClientProtocolException, IOException {
		
		log.info(body);
		JAXBContext ctx = JAXBContext.newInstance(PostSmsRequestType.class);
		Unmarshaller um = ctx.createUnmarshaller();
		StringReader reader = new StringReader(body);
		PostSmsRequestType request = (PostSmsRequestType) um.unmarshal(reader);

		String sender = request.getSms().getOrigin();
		String destination = request.getSms().getDestination();
		String message = request.getSms().getMessage();
		
		EMSServiceProvider ems = new EMSServiceProvider();
		
		String output = ems.sendSms(sender, destination, message);
		log.info(output);
		return output;
	}
}