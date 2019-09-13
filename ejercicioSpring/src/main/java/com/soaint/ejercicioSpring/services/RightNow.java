package com.soaint.ejercicioSpring.services;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaint.ejercicioSpring.model.rightnow.AddressType;
import com.soaint.ejercicioSpring.model.rightnow.ContactsRightNow;
import com.soaint.ejercicioSpring.model.rightnow.Emails;
import com.soaint.ejercicioSpring.model.rightnow.Name;
import com.soaint.ejercicioSpring.security.PropertiesReader;
import com.soaint.ejercicioSpring.services.connection.ConnectionHttp;
import com.soaint.ejercicioSpring.utils.UriReplace;

public class RightNow {
    
	ConnectionHttp connectionHttp = new ConnectionHttp();
	UriReplace uriReplace = new UriReplace();
		
	// QUERY DE ORACLE SERVICE CLOUD (RIGHT NOW)
	private String QueryId(String email) {
		return "https://" + PropertiesReader.getCredRightNow() + "@" + 
				PropertiesReader.urlRightNow() + PropertiesReader.getUrlQueryRightNow() + "'" + email + "'";
	}
	
	// URL DE ORACLE SERVICE CLOUD (RIGHT NOW)
	private String UrlGet(int id) {
		return "https://" + PropertiesReader.getCredRightNow() + "@" + 
				PropertiesReader.urlRightNow() + PropertiesReader.getUrlRightNow() + id;
	}
	private String UrlPost() {
		return "https://" + PropertiesReader.getCredRightNow() + "@" + 
				PropertiesReader.urlRightNow() + PropertiesReader.postUrlRightNow();
	}
		
	// HTTP GET "OBJECT" request
	public int getContactByEmail(String email) {
        HttpGet request = new HttpGet(QueryId(email));
        
        try {
    	    String jsonResponse = connectionHttp.ConnectionResponse(request);    	    
    		ObjectMapper om = new ObjectMapper();
    		JsonNode nodes = om.readTree(jsonResponse).get("items");
    		return nodes.get(0).get("id").asInt();
        }catch(Exception e) {
        	return 0;
        }
	}
	
	public String serializeContact(String person) throws IOException {
		ObjectMapper obj = new ObjectMapper();
		JsonNode actualObj = obj.readTree(person);
		Name namern = new Name(uriReplace.JsonTransformer(actualObj.get("nombre").asText()),
				uriReplace.JsonTransformer(actualObj.get("apellidos").asText()));
		AddressType addressType = new AddressType(0);
		Emails emailsrn = new Emails(uriReplace.JsonTransformer(actualObj.get("correo").asText()),addressType);
		ContactsRightNow contact = new ContactsRightNow(namern,emailsrn);
		
		return obj.writeValueAsString(contact).toString();
	}
	
	// HTTP POST para crear contacto
	public void postContactSave(String person) throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(UrlPost());
        StringEntity entity = new StringEntity(serializeContact(person),
                ContentType.APPLICATION_JSON);
        HttpResponse response = connectionHttp.ConnectionResponse(request, entity);
        System.out.println(response.getStatusLine().getStatusCode());
	}
	
	// HTTP DELETE request
    public void deleteContact(int id) throws ClientProtocolException, IOException {
    	HttpDelete request = new HttpDelete(UrlGet(id));
        connectionHttp.ConnectionResponse(request);	
    }
    
}