package com.quiputech.sms.gateway;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

import com.quiputech.sms.gateway.util.GatewayHelper;

/**
 * Servlet implementation class PostSms
 */
@WebServlet("/PostSms")
public class PostSmsServlet extends HttpServlet {
	
	private static Logger log = Logger.getLogger(PostSmsServlet.class);
	
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
		
		// read params
		String token = request.getParameter("token");
		int customerId = Integer.parseInt(request.getParameter("customer"));
		String origin = request.getParameter("origin");
		String destination = request.getParameter("destination");
		String message = request.getParameter("message");

		// enqueue
		log.info("Incoming GET request: " +  request.getQueryString()); 
		PrintWriter out = response.getWriter();
		try {
			String rsp = GatewayHelper.enqueuePost(token, customerId, origin, destination, message);
			response.setContentType("application/xml");
			out.print(rsp);
			log.info(rsp);
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
			out.print("<error><code>-1</code><message>Something happened. Please try again later.</message></error>");
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
			String rsp = GatewayHelper.enqueuePost(request.getInputStream());
			response.setContentType("application/xml");
			out.print(rsp);
			log.info(rsp);
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
			out.print("<error><code>-1</code><message>Something happened. Please try again later.</message></error>");
		}
	}

	
	
	
}
