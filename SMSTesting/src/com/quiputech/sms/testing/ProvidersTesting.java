package com.quiputech.sms.testing;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.quiputech.sms.providers.ems.EMSServiceProvider;




public class ProvidersTesting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Logger log = Logger.getLogger("main");

		try {
			EMSServiceProvider ems = new EMSServiceProvider();
//			String out = ems.sendSms("TIGO", "50499925594", "Hola cara de loca!");
			String out = ems.checkStatus("18772738");
			log.info(out);
		} catch(Exception e) {
			log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		

	}

}
