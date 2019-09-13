package com.soaint.ejercicioSpring.services;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaint.ejercicioSpring.model.eloqua.ContactsEloqua;
import com.soaint.ejercicioSpring.security.PropertiesReader;
import com.soaint.ejercicioSpring.services.connection.ConnectionHttp;
import com.soaint.ejercicioSpring.utils.UriReplace;

/**
 * 
 * @author jcruz
 *
 */
public class Eloqua {
	
	ConnectionHttp connectionHttp = new ConnectionHttp();
	UriReplace uriReplace = new UriReplace();
	
	// CREDENCIALES DE ELOQUA
    private HttpGet credential(HttpGet request) {
    	request.setHeader(HttpHeaders.AUTHORIZATION,
    			PropertiesReader.getCredEloqua());
    	return request;
    }
    private HttpPost credential(HttpPost request) {
    	request.setHeader(HttpHeaders.AUTHORIZATION,
    			PropertiesReader.getCredEloqua());
    	return request;
    }
    private HttpDelete credential(HttpDelete request) {
    	request.setHeader(HttpHeaders.AUTHORIZATION,
    			PropertiesReader.getCredEloqua());
    	return request;
    }
    
	// QUERY DE ELOQUA
    private String QueryId(String email) {
		return PropertiesReader.urlEloqua() + PropertiesReader.getUrlQueryEloqua() + email;
	}
    
	// URL DE ELOQUA
    private String UrlGet(int id) {
		return PropertiesReader.urlEloqua() + PropertiesReader.getUrlEloqua() + id;
	}

	// GET ID DE CONTACTS
	public int getContactByEmail(String email) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(QueryId(email));
        credential(request);
        try {
        	String jsonResponse = connectionHttp.ConnectionResponse(request);    	    
    		ObjectMapper om = new ObjectMapper();
    		JsonNode nodes = om.readTree(jsonResponse).get("elements");
    		return nodes.get(0).get("id").asInt();
        }catch(Exception e) {
        	return 0;
        }
	}
	
	// HTTP POST para crear contacto
	public void postContactSave(String person) throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(PropertiesReader.urlEloqua() + PropertiesReader.postUrlEloqua());
        credential(request);
        StringEntity entity = new StringEntity(serializeContact(person),
                ContentType.APPLICATION_JSON);        
        HttpResponse response = connectionHttp.ConnectionResponse(request, entity);
        System.out.println(response.getStatusLine().getStatusCode());
	}
    
	// HTTP DELETE request
    public void deleteContact(int id) throws ClientProtocolException, IOException {
        HttpDelete request = new HttpDelete(UrlGet(id));
        credential(request);
        connectionHttp.ConnectionResponse(request);
    }
    
	public String serializeContact(String person) throws IOException {
		ObjectMapper om = new ObjectMapper();
		JsonNode actualObj = om.readTree(person);
		ContactsEloqua contact = new ContactsEloqua(uriReplace.JsonTransformer(actualObj.get("nombre").asText()),
				uriReplace.JsonTransformer(actualObj.get("apellidos").asText()),
				uriReplace.JsonTransformer(actualObj.get("correo").asText()));
		return om.writeValueAsString(contact).toString();
	}
    
}