package com.quiputech.sms.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class SendSMSController
 */
@WebServlet("/SendSMSController")
public class SendSMSController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	private static Logger log = Logger.getLogger(SendSMSController.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendSMSController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("SEND POST!");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String url = "http://facebook.latribuna.hn/com.quiputech.sms.server/sendSingleSMS";
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String from = request.getParameter("from");
		String to = request.getParameter("to");
		String message = request.getParameter("message");
		
		SingleSMSRequest reqaux = new SingleSMSRequest();
		reqaux.setFrom(from);
		reqaux.setTo(to);
		reqaux.setMessage(message);
		reqaux.setToken("12345678");
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		post.setEntity(new StringEntity(gson.toJson(reqaux)));
		HttpResponse respaux = client.execute(post);

		log.info(respaux.getStatusLine().toString());
		
		PrintWriter out = response.getWriter();	
		
		String aux = IOUtils.toString(respaux.getEntity().getContent()); 
		log.info(aux);
		out.println(aux);
	}

}
