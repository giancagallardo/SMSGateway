package com.quiputech.sms;

import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet("/InfobipPost")
public class InfobipDummyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final int MAX_SLEEP_MILLIS = 200;
	
	private static Logger log = Logger.getLogger(InfobipDummyServlet.class);
	
	private static int availableCredits = 100000;
	
	private static final int ERROR = -4;
	
	private static final int SENT_OK = 1;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InfobipDummyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doWork(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doWork(request, response);
	}

	protected void doWork(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// create false sleep
			Random rnd = new Random();
			int max = rnd.nextInt(MAX_SLEEP_MILLIS);
			if(max > 0)
				Thread.sleep(max);
			
			int returnStatus = rnd.nextDouble() > 0.30 ? SENT_OK : ERROR;
			if(returnStatus > 0)
				availableCredits--;
			String status = String.format("<RESPONSE><status>%s</status><credits>%s</credits></RESPONSE>", returnStatus, availableCredits);
			
			response.setContentType("application/xml");
			Writer out = response.getWriter();
			out.write(status);
			out.flush();
			out.close();
			
			log.info(status);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
