package com.quiputech.sms.server.rest;

import java.io.IOException;
import java.util.UUID;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quiputech.sms.server.xml.RESPONSE;

@Path("/sendSingleSMS")
public class SMSService {

	public static Logger log = Logger.getLogger(SMSService.class);
		
	public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public static final String apiuri = "http://api2.infobip.com/api/sendsms/xml";
	
	@POST()
	@Produces("text/plain")
	public String sendSingleSMS(String request) throws ClientProtocolException, IOException, JAXBException {
		
		log.info(request);
		
		SingleSMSRequest sms = gson.fromJson(request, SingleSMSRequest.class);
		String uuid = UUID.randomUUID().toString();
		String rawpost = String.format("XML=<SMS><authentification><username>jose.ordonez</username><password>qwerty123</password></authentification><message><sender>%s</sender><text>%s</text></message><recipients><gsm messageId=\"%s\">%s</gsm></recipients></SMS>", sms.getFrom(), sms.getMessage(), uuid, sms.getTo());

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(apiuri);
		post.setEntity(new StringEntity(rawpost));
		HttpResponse response = client.execute(post);
		
		log.info(response.getStatusLine().toString());				

		JAXBContext ctx = JAXBContext.newInstance(RESPONSE.class);
		Unmarshaller um = ctx.createUnmarshaller();
		RESPONSE aux = (RESPONSE) um.unmarshal(response.getEntity().getContent());

		SingleSMSResponse ssr = new SingleSMSResponse();
		ssr.setAvailable(aux.getCredits());
		ssr.setUuid(uuid);
		
		int status = Integer.parseInt(aux.getStatus());
		switch(status){
		case -1: 
			ssr.setStatus("AUTH_FAILED");
			break;
		case -2:
			ssr.setStatus("XML_ERROR");
			break;
		case -3:
			ssr.setStatus("NOT_ENOUGH_CREDITS");
			break;
		case -4:
			ssr.setStatus("NO_RECIPIENTS");
			break;
		case -5:
			ssr.setStatus("GENERAL_ERROR");
			break;
		default:
			ssr.setStatus("SENT_OK");
			break;
		}
		return gson.toJson(ssr);
		
	}
}
