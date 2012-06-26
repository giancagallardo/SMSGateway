package com.quiputech.sms;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

/**
 * Servlet implementation class EMSDummyGetServlet
 */
@WebServlet("/websms")
public class EMSDummySendSMSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static int responseId = 100000;
	
	private static Logger log = Logger.getLogger(EMSDummySendSMSServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EMSDummySendSMSServlet() {
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
			PrintWriter out = response.getWriter();
			String aux = String.format("Response Id::%s", responseId++);
			response.setContentType("text/plain");
			out.println(aux);
			log.info(aux);
		} catch (InterruptedException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
	
	

}
