package com.quiputech.sms.gateway;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

import com.quiputech.sms.gateway.util.GatewayService;
import com.quiputech.sms.xml.PostSmsRequestType;


/**
 * Servlet implementation class PostSms
 */
@WebServlet("/PostSms")
public class PostSmsServlet extends HttpServlet {
	
	private static Logger log = Logger.getLogger(PostSmsServlet.class);
	private static final String ERROR_RESPONSE = "<error><code>-1</code><message>Something happened. Please try again later.</message></error>";
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostSmsServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// log incoming
		PrintWriter out = response.getWriter();
		log.info("Incoming GET request: " +  request.getQueryString()); 
		try {
			// read params
			String token = request.getParameter("token");
			int customerId = Integer.parseInt(request.getParameter("customerId"));
			String sender = request.getParameter("sender");
			String destination = request.getParameter("destination");
			String message = request.getParameter("message");

			// create instance
			PostSmsRequestType postSmsRequest = GatewayService.buildPostSmsRequest(token, customerId, sender, destination, message);
			// enqueueRequest
			String responseXml = GatewayService.enqueuePostSmsRequest(postSmsRequest, request.getRemoteAddr(), request.getRemoteHost());
			response.setContentType("application/xml");
			out.print(responseXml);
			log.info(responseXml);
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
			out.print(ERROR_RESPONSE);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// enqueue
		log.info("Incoming POST request"); 
		PrintWriter out = response.getWriter();
		try {
			// unmarshall
			PostSmsRequestType postSmsRequest = GatewayService.buildPostSmsRequest(request.getInputStream());
			// enqueueRequest
			String responseXml = GatewayService.enqueuePostSmsRequest(postSmsRequest, request.getRemoteAddr(), request.getRemoteHost());
			response.setContentType("application/xml");
			out.print(responseXml);
			log.info(responseXml);
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
			out.print(ERROR_RESPONSE);
		}
	}

	
	
	
}
