package com.quiputech.sms.providers;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.logging.Logger;

import com.quiputech.sms.gateway.util.ConfigurationService;

public class InfobipServiceProvider {


	private static Logger log = Logger.getLogger(EMSServiceProvider.class);
	
	public String sendSms(String uuid, String sender, String destination, String message) throws ClientProtocolException, IOException {

		ConfigurationService config = ConfigurationService.getInstance();
		String user = config.getProperty("infobip.user", ConfigurationService.DEFAULT_INFOBIP_USER);
		String pass = config.getProperty("infobip.pass", ConfigurationService.DEFAULT_INFOBIP_PASS);
		String endpoint = config.getProperty("infobip.send.endpoint", ConfigurationService.DEFAULT_INFOBIP_SEND_ENDPOINT1);
		
		String uri = String.format("%s?user=%s&password=%s&sender=%s&SMSText=%s&GSM=%s", endpoint, user, pass, sender, URLEncoder.encode(message, "UTF-8"), destination);
		HttpGet get = new HttpGet(uri);
		log.info(uri);
		
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		String responseTxt = IOUtils.toString(response.getEntity().getContent());
		log.info(responseTxt);
		return responseTxt;
	}
	
	
	public String checkStatus(String responseId) throws ClientProtocolException, IOException {
		ConfigurationService config = ConfigurationService.getInstance();
		String user = config.getProperty("infobip.user", ConfigurationService.DEFAULT_INFOBIP_USER);
		String pass = config.getProperty("infobip.pass", ConfigurationService.DEFAULT_INFOBIP_PASS);
		String endpoint = config.getProperty("infobip.status.endpoint", ConfigurationService.DEFAULT_INFOBIP_STATUS_ENDPOINT);
		String uri = String.format("%s?user=%s&password=%s&messageid=%s", endpoint, user, pass, responseId);
		HttpGet get = new HttpGet(uri);
		log.info(uri);

		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		String responseTxt = IOUtils.toString(response.getEntity().getContent());
		log.info(responseTxt);
		return responseTxt;
	}
	
	
	
	
}
