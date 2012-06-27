package com.quiputech.sms.providers.ems;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class EMSServiceProvider {

	private static Logger log = Logger.getLogger("EMSServiceProvider");
	private static Properties props = new Properties();
	
	private static final String DEFAULT_USER = "miguel";
	private static final String DEFAULT_PASS = "demo1234";
	
	//private static final String DEFAULT_SEND_ENDPOINT = "http://182.72.103.86/websmpp/websms";
	private static final String DEFAULT_SEND_ENDPOINT = "http://desa2.quiputech.com:8080/EMSDummy/websms";

	//private static final String DEFAULT_STATUS_ENDPOINT = "http://182.72.103.86/websmpp/websmsstatus";
	private static final String DEFAULT_STATUS_ENDPOINT = "http://desa2.quiputech.com:8080/EMSDummy/websmsstatus";
	
	static {
		// pressumed to be deployed in JBoss
		String baseDir = System.getProperty("jboss.home.dir");
		if(baseDir == null)
			baseDir = System.getProperty("user.dir");
		try {
			File propsFile = new File(baseDir + "/standalone/configuration/ems.properties");
			log.info("Looking for EMS props in: " + propsFile.getPath());
			if(propsFile.exists())
				props.load(new FileInputStream(propsFile));
		} catch (Exception e) {
			log.log(Level.SEVERE,e.getLocalizedMessage(), e);
		}
		
	}
	
	
	
	
	public String sendSms(String sender, String destination, String message) throws ClientProtocolException, IOException {

		String user = props.getProperty("ems.user", DEFAULT_USER);
		String pass = props.getProperty("ems.pass", DEFAULT_PASS);
		String endpoint = props.getProperty("ems.endpoint", DEFAULT_SEND_ENDPOINT);
		
		String uri = String.format("%s?user=%s&pass=%s&sid=%s&mno=%s&text=%s&type=1&esm=0&dcs=0", endpoint, user, pass, sender, destination, URLEncoder.encode(message, "UTF-8"));
		HttpGet get = new HttpGet(uri);
		log.info(uri);

		
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		
		return IOUtils.toString(response.getEntity().getContent());
		
	}
	
	public String checkStatus(String responseId) throws ClientProtocolException, IOException {
		String endpoint = props.getProperty("ems.endpoint", DEFAULT_STATUS_ENDPOINT);
		String uri = String.format("%s?respid=%s", endpoint, responseId);
		HttpGet get = new HttpGet(uri);
		log.info(uri);

		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		
		return IOUtils.toString(response.getEntity().getContent());
		
	}
	
	
}
