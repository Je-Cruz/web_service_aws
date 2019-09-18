package com.soaint.ejercicioSpring.services;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaint.ejercicioSpring.model.eloqua.ContactsEloqua;
import com.soaint.ejercicioSpring.security.PropertiesReader;
import com.soaint.ejercicioSpring.services.connection.ConnectionHttp;
import com.soaint.ejercicioSpring.utils.UriReplace;

import repository.InterfaceServices;

/**
 * 
 * @author jcruz
 *
 */
@Service
public class Eloqua implements InterfaceServices {
	
	ConnectionHttp connectionHttp = new ConnectionHttp();
	
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
	public int getContactIdByEmail(String email) {
		HttpGet request = new HttpGet(QueryId(email));
        credential(request);
        try {
        	String jsonResponse = connectionHttp.ConnectionResponse(request);    	    
    		ObjectMapper om = new ObjectMapper();
    		JsonNode nodes = om.readTree(jsonResponse).get(PropertiesReader.stringElements());
    		return nodes.get(0).get("id").asInt();
        }catch (ClientProtocolException e) {
        	return 0;
        }catch (IOException e) {
        	return 0;
		}catch (NullPointerException e) {
        	return 0;
		}
	}
	
	// POST SAVE CONTACT
	public void postContactSave(String person) throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(PropertiesReader.urlEloqua() + PropertiesReader.postUrlEloqua());
        credential(request);
        StringEntity entity = new StringEntity(serializeContact(person),
                ContentType.APPLICATION_JSON);
        connectionHttp.ConnectionResponse(request, entity);
	}
    
	// DELETE CONTACT
    public void deleteContact(int id) throws ClientProtocolException, IOException {
        HttpDelete request = new HttpDelete(UrlGet(id));
        credential(request);
        connectionHttp.ConnectionResponse(request);
    }
    
	public String serializeContact(String person) throws IOException {
		ObjectMapper om = new ObjectMapper();
		JsonNode actualObj = om.readTree(person);
		ContactsEloqua contact = new ContactsEloqua(UriReplace.JsonTransformer(actualObj.get(PropertiesReader.stringNombre()).asText()),
				UriReplace.JsonTransformer(actualObj.get(PropertiesReader.stringApellidos()).asText()),
				UriReplace.JsonTransformer(actualObj.get(PropertiesReader.stringCorreo()).asText()));
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
	public ResponseEntity<String> checkExistenceForCreateByJson (String contactJson) {
    	ObjectMapper om = new ObjectMapper();
		JsonNode nodes;
		try {
			nodes = om.readTree(contactJson).get(PropertiesReader.stringCorreo());
			String email = UriReplace.JsonTransformer(nodes.asText());
	    	
	     	if (getContactIdByEmail(email) > 0) {
	     		return new ResponseEntity<>("Ya existe en Eloqua<br>", HttpStatus.OK);
	     	} else { 
	     		postContactSave(contactJson);
	     		return new ResponseEntity<>("Creado en Eloqua<br>", HttpStatus.CREATED);
	     	}
			
		} catch (IOException e) {
			return new ResponseEntity<>("Error al crear en Eloqua<br>", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
    
}