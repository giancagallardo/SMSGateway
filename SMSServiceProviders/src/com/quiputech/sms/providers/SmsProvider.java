package com.quiputech.sms.providers;

import com.quiputech.sms.providers.common.Customer;
import com.quiputech.sms.providers.common.Sms;
import com.quiputech.sms.providers.common.SmsStatus;

public abstract class SmsProvider {

	
	public abstract SmsStatus sendSms(Customer customer, Sms sms);
	
	protected void log(Customer customer, Sms sms) {
		
		
		
		
	}
}
