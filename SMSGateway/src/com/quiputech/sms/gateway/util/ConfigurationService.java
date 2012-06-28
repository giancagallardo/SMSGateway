package com.quiputech.sms.gateway.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.jboss.logging.Logger;

public class ConfigurationService {

	private static final String NO_PROPERTIES = "No properties found! Application will use defaults.";
	private static final String LOOKING_FOR_CONFIGURATION = "Looking for configuration file in ";
	private static final String PROPERTIES_PATH = "/configuration/smsgateway.properties";
	private static final String JBOSS_HOME_DIR = "jboss.home.dir";
	private static final String AWS_ACCESSKEYID = "aws.accessKeyId";
	private static final String AWS_SECRETKEY = "aws.secretKey";
	private static final String FORMAT_ENTRY = "{%s}: {%s}";
	
	public static final String DEFAULT_POST_QUEUE_URL = "https://queue.amazonaws.com/382063515626/post-sms-queue";
	public static final String DEFAULT_STATUS_QUEUE_URL = "https://queue.amazonaws.com/382063515626/status-sms-queue";
	
	public static final String DEFAULT_EMS_USER = "miguel";
	public static final String DEFAULT_EMS_PASS = "demo1234";
	public static final String DEFAULT_EMS_SEND_ENDPOINT = "http://182.72.103.86/websmpp/websms";
	public static final String DEFAULT_EMS_STATUS_ENDPOINT = "http://182.72.103.86/websmpp/websmsstatus";

	public static final String DEFAULT_INFOBIP_USER = "jose.ordonez";
	public static final String DEFAULT_INFOBIP_PASS = "qwerty123";
	public static final String DEFAULT_INFOBIP_SEND_ENDPOINT1 = "http://api2.infobip.com/api/sendsms/plain";
	public static final String DEFAULT_INFOBIP_SEND_ENDPOINT2 = "http://api.infobip.com/api/sendsms/plain";
	public static final String DEFAULT_INFOBIP_STATUS_ENDPOINT = "http://api2.infobip.com/api/dlrpull";
	
	
	
	private Logger log = Logger.getLogger(ConfigurationService.class);
	
	private Properties properties = new Properties();
	
	private static final ConfigurationService instance = new ConfigurationService();
	
	public static ConfigurationService getInstance() {
		return instance;
	}

	public ConfigurationService() {

		
		// setting global AWS Credentials
		System.setProperty(AWS_ACCESSKEYID, "AKIAJUNR2HH5SXZ6NONA");
		System.setProperty(AWS_SECRETKEY, "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");
		
		String configurationFilePath = System.getProperty(JBOSS_HOME_DIR) + PROPERTIES_PATH;
		log.info(LOOKING_FOR_CONFIGURATION + configurationFilePath);
		
		File configurationFile = new File(configurationFilePath);
		if(configurationFile.exists()) {
			try {
				properties.load(new FileInputStream(configurationFile));
				
				for(Entry<Object, Object> entry : properties.entrySet()) {
					log.info(String.format(FORMAT_ENTRY, entry.getKey(), entry.getValue()));
				}
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		} else {
			log.info(NO_PROPERTIES);
		}
	}

	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
}
