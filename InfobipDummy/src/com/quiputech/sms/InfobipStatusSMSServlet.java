package com.quiputech.sms;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

/**
 * Servlet implementation class InfobipStatus
 */
@WebServlet("/dlrpull")
public class InfobipStatusSMSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int MAX_SLEEP_MILLIS = 200;
	
	private static Logger log = Logger.getLogger(InfobipSendSMSServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InfobipStatusSMSServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			
			log.info(request.getQueryString());
			
			// create false sleep
			Random rnd = new Random();
			int sleep = rnd.nextInt(MAX_SLEEP_MILLIS);
			if(sleep > 0)
				Thread.sleep(sleep);
			
			
			double chance = rnd.nextDouble();
			String status = "DELIVERED";
			if(chance < 0.075)
				status = "SENT";
			else if(chance < 0.20)
				status = "NOT_DELIVERED";

			String messageId = request.getParameter("messageid");
			
			String output = String.format("<DeliveryReport><message id=\"%s\" sentdate=\"%s\" donedate=\"%s\" status=\"%s\" /></DeliveryReport>", messageId, new Date(), new Date(), status);
			
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
