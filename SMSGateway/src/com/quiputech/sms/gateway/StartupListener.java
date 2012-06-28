package com.quiputech.sms.gateway;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


import com.quiputech.sms.gateway.util.ConfigurationService;

/**
 * Application Lifecycle Listener implementation class StartupListener
 *
 */
@WebListener
public class StartupListener implements ServletContextListener {

	
    /**
     * Default constructor. 
     */
    public StartupListener() {
    	
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	
    	ConfigurationService.getInstance();
    	
    	
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
    }
	
}
