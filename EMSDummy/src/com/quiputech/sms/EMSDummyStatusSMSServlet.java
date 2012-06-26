package com.quiputech.sms;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

/**
 * Servlet implementation class EMSDummyStatusSMSServlet
 */
@WebServlet("/websmsstatus")
public class EMSDummyStatusSMSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(EMSDummySendSMSServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EMSDummyStatusSMSServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			log.info("Incoming: " + request.getQueryString());
			Random rnd = new Random();
			int sleep = rnd.nextInt(200);
			if(sleep > 0)
				Thread.sleep(sleep);
			
			String respid = request.getParameter("respid");
			
			double chance = rnd.nextDouble();
			String status = "DELIVRD";
			if(chance < 0.075)
				status = "ATES";
			else if(chance < 0.20) 
				status = "UNDELIV";
			String uuid = UUID.randomUUID().toString();
			PrintWriter out = response.getWriter();
			String aux = String.format("Message ID : %s Status :%s  Error_Code: 0   SMSC ResponseID: %s", respid, status, uuid);
			response.setContentType("text/plain");
			out.println(aux);
			log.info(aux);
		} catch (InterruptedException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		
	}

}
