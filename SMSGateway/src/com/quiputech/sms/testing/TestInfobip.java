package com.quiputech.sms.testing;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quiputech.sms.providers.InfobipServiceProvider;

/**
 * Servlet implementation class TestInfobip
 */
@WebServlet("/TestInfobip")
public class TestInfobip extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestInfobip() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String sender = request.getParameter("sender");
		String destination = request.getParameter("destination");
		String message = request.getParameter("message");
		
		InfobipServiceProvider infobip = new InfobipServiceProvider();
		String result = infobip.sendSms(UUID.randomUUID().toString(), sender, destination, message);
		
		PrintWriter out = response.getWriter();
		out.println(result);
	}

}
