package com.soaint.ejercicioSpring.services;

import java.io.IOException;

import org.apache.http.HttpHeaders;
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
		return PropertiesReader.urlEloqua().concat(PropertiesReader.getUrlQueryEloqua()).concat(email);
	}
    
	// URL DE ELOQUA
    private String UrlGet(int id) {
		return PropertiesReader.urlEloqua().concat(PropertiesReader.getUrlEloqua()) + id;
	}

	// GET ID DE CONTACTS
	public int getContactIdByEmail(String email) throws ClientProtocolException, IOException {
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
        connectionHttp.ConnectionResponse(request, entity);
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
	
	public String checkExistenceForDeleteByEmail (String email) throws ClientProtocolException, IOException {
		int idEloqua = getContactIdByEmail(email);
		if (idEloqua > 0) {
     		deleteContact(idEloqua);
     		return "ELIMINADO de Eloqua<br>";
     	} else {
     		return "No existe en Eloqua<br>";
     	}
	}
	public String checkExistenceForCreateByJson (String contactJson) throws ClientProtocolException, IOException {
    	ObjectMapper om = new ObjectMapper();
		JsonNode nodes = om.readTree(contactJson).get("correo");
		String email = uriReplace.JsonTransformer(nodes.asText());
    	
     	if (getContactIdByEmail(email) > 0) {
     		return "Ya existe en Eloqua<br>";
     	} else {
     		postContactSave(contactJson);
     		return "Creado en Eloqua<br>";
     	}
	}
    
}