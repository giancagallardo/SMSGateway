package com.quiputech.sms;

import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

/**
 * Servlet implementation class InfobipDummyServlet
 */
@WebServlet("/sendsms")
public class InfobipSendSMSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final int MAX_SLEEP_MILLIS = 200;
	
	private static Logger log = Logger.getLogger(InfobipSendSMSServlet.class);
	
	private static int availableCredits = 100000;
	
	private static final int ERROR = -4;
	
	private static final int SENT_OK = 1;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InfobipSendSMSServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
						
			// create false sleep
			Random rnd = new Random();
			int sleep = rnd.nextInt(MAX_SLEEP_MILLIS);
			if(sleep > 0)
				Thread.sleep(sleep);
			
			int returnStatus = rnd.nextDouble() > 0.20 ? SENT_OK : ERROR;
			
			
			if(returnStatus > 0)
				availableCredits--;
			String output = String.format("<RESPONSE><status>%s</status><credits>%s</credits></RESPONSE>", returnStatus, availableCredits);
			
			response.setContentType("application/xml");
			Writer out = response.getWriter();
			out.write(output);
			out.flush();
			out.close();
			log.info(output);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
