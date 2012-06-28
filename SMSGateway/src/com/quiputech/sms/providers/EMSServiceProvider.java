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


public class EMSServiceProvider {

	private static Logger log = Logger.getLogger(EMSServiceProvider.class);
	
	public String sendSms(String sender, String destination, String message) throws ClientProtocolException, IOException {

		ConfigurationService config = ConfigurationService.getInstance();
		String user = config.getProperty("ems.user", ConfigurationService.DEFAULT_EMS_USER);
		String pass = config.getProperty("ems.pass", ConfigurationService.DEFAULT_EMS_PASS);
		String endpoint = config.getProperty("ems.send.endpoint", ConfigurationService.DEFAULT_EMS_SEND_ENDPOINT);
		
		String uri = String.format("%s?user=%s&pass=%s&sid=%s&mno=%s&text=%s&type=1&esm=0&dcs=0", endpoint, user, pass, sender, destination, URLEncoder.encode(message, "UTF-8"));
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
		String endpoint = config.getProperty("ems.status.endpoint", ConfigurationService.DEFAULT_EMS_STATUS_ENDPOINT);
		String uri = String.format("%s?respid=%s", endpoint, responseId);
		HttpGet get = new HttpGet(uri);
		log.info(uri);

		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);		
		String responseTxt = IOUtils.toString(response.getEntity().getContent());
		log.info(responseTxt);
		return responseTxt;
	}
	
	
}
