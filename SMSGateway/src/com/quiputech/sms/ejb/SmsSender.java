package com.quiputech.sms.ejb;

import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.jboss.logging.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.quiputech.sms.gateway.util.ConfigurationService;

@Stateless
public class SmsSender {

	private static final Logger log = Logger.getLogger(SmsSender.class);
	
    /**
     * Default constructor. 
     */
    public SmsSender() {

    }
	
	@SuppressWarnings("unused")
	@Schedule(second="*/10", minute="*", hour="*", dayOfWeek="*",
      dayOfMonth="*", month="*", year="*", info="SMS Sender")
    private void scheduledTimeout(final Timer t) {
		
		try {
						
			
			AmazonSQS sqs = new AmazonSQSClient();
			ConfigurationService config = ConfigurationService.getInstance();
			String queue = config.getProperty("post-sms-queue", ConfigurationService.DEFAULT_POST_QUEUE_URL);
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queue);
			int maxNumberOfMessages = Integer.parseInt(config.getProperty("max.number.messages", "10"));
			int visibilityTimeout = Integer.parseInt(config.getProperty("visibility.timeout", "30"));

			receiveMessageRequest.setMaxNumberOfMessages(maxNumberOfMessages);
			receiveMessageRequest.setVisibilityTimeout(visibilityTimeout);

			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			
			for(Message message : messages) {

				String uuid = message.getBody();
				

				String receiptHandle = message.getReceiptHandle();
				sqs.deleteMessage(new DeleteMessageRequest(queue, receiptHandle));
			}
			
        } catch (AmazonServiceException ase) {
            log.error("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            log.error("Error Message:    " + ase.getMessage());
            log.error("HTTP Status Code: " + ase.getStatusCode());
            log.error("AWS Error Code:   " + ase.getErrorCode());
            log.error("Error Type:       " + ase.getErrorType());
            log.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
        	log.error("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
        	log.error("Error Message: " + ace.getMessage());
        } catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
		
		
		
    }
}