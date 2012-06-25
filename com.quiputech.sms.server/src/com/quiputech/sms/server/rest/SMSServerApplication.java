package com.quiputech.sms.server.rest;

import java.util.Set;
import java.util.HashSet;
import javax.ws.rs.core.Application;

public class SMSServerApplication extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	public SMSServerApplication(){
	     singletons.add(new SMSService());
	}
	@Override
	public Set<Class<?>> getClasses() {
	     return empty;
	}
	@Override
	public Set<Object> getSingletons() {
	     return singletons;
	}
}
