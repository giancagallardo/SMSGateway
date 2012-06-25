package com.quiputech.sms.providers;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quiputech.sms.providers.common.Customer;
import com.quiputech.sms.providers.common.Sms;
import com.quiputech.sms.providers.common.SmsStatus;

public class InfobipProvider extends SmsProvider {

	private static Logger log = Logger.getLogger(InfobipProvider.class.getSimpleName());
	private static final String JBOSS_CONFIG_DIR = "jboss.server.config.dir";
	private static final String USER_DIR ="user.dir";
	private static final String PROPERTIES = "infobip.properties";
	
	private static Properties props = new Properties();

	static {

		try {
			// find properties file
			Properties sysProps = System.getProperties();
			String configPath = sysProps.getProperty(JBOSS_CONFIG_DIR) != null ? 
					String.format("%s/%s", sysProps.getProperty(JBOSS_CONFIG_DIR), PROPERTIES) :
					String.format("%s/%s", sysProps.getProperty(USER_DIR), PROPERTIES);

			File configFile = new File(configPath);
			FileInputStream fis = new FileInputStream(configFile);
			props.load(fis);
			
			for(Entry<Object, Object> entry : props.entrySet()) {
				log.info(entry.getKey() + "[" + entry.getValue() + "]");
			}
			
		} catch(Exception e) {
			log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	
	public InfobipProvider() {
		
	}

	@Override
	public SmsStatus sendSms(Customer customer, Sms sms) {
		
		return null;
	}

}
