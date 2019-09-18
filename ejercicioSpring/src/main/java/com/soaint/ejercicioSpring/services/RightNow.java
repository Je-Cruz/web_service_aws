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
import com.soaint.ejercicioSpring.model.rightnow.AddressType;
import com.soaint.ejercicioSpring.model.rightnow.ContactsRightNow;
import com.soaint.ejercicioSpring.model.rightnow.Emails;
import com.soaint.ejercicioSpring.model.rightnow.Name;
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
public class RightNow implements InterfaceServices {
    
	
	ConnectionHttp connectionHttp = new ConnectionHttp();
		
	// CREDENCIALES DE ELOQUA
    private HttpGet credential(HttpGet request) {
    	request.setHeader(HttpHeaders.AUTHORIZATION,
    			PropertiesReader.getCredRightNow());
    	return request;
    }
    private HttpPost credential(HttpPost request) {
    	request.setHeader(HttpHeaders.AUTHORIZATION,
    			PropertiesReader.getCredRightNow());
    	return request;
    }
    private HttpDelete credential(HttpDelete request) {
    	request.setHeader(HttpHeaders.AUTHORIZATION,
    			PropertiesReader.getCredRightNow());
    	return request;
    }
	
	// QUERY DE ORACLE SERVICE CLOUD (RIGHT NOW)
	private String QueryId(String email) {
		return PropertiesReader.urlRightNow().concat(PropertiesReader.getUrlQueryRightNow()).concat("'").concat(email).concat("'");
	}
	
	// URLS DE ORACLE SERVICE CLOUD (RIGHT NOW)
	private String UrlGet(int id) {
		return PropertiesReader.urlRightNow().concat(PropertiesReader.getUrlRightNow()) + id;
	}
	private String UrlPost() {
		return PropertiesReader.urlRightNow().concat(PropertiesReader.postUrlRightNow());
	}
	//------------------------
	
	// GET ID DE CONTACTS
	public int getContactIdByEmail(String email) {
        HttpGet request = new HttpGet(QueryId(email));
        credential(request);
        try {
    	    String jsonResponse = connectionHttp.ConnectionResponse(request);    	    
    		ObjectMapper om = new ObjectMapper();
    		JsonNode nodes = om.readTree(jsonResponse).get(PropertiesReader.stringItems());
    		return nodes.get(0).get(PropertiesReader.stringId()).asInt();
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
		HttpPost request = new HttpPost(UrlPost());
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
		ObjectMapper obj = new ObjectMapper();
		JsonNode actualObj = obj.readTree(person);
		Name name = new Name(UriReplace.JsonTransformer(actualObj.get(PropertiesReader.stringNombre()).asText()),
				UriReplace.JsonTransformer(actualObj.get(PropertiesReader.stringApellidos()).asText()));
		AddressType addressType = new AddressType(0);
		Emails email = new Emails(UriReplace.JsonTransformer(actualObj.get(PropertiesReader.stringCorreo()).asText()),addressType);
		ContactsRightNow contact = new ContactsRightNow(name,email);
		
		return obj.writeValueAsString(contact).toString();
	}
	
	public String checkExistenceForDeleteByEmail (String email) throws ClientProtocolException, IOException {
		int idRightNow = getContactIdByEmail(email);
		if (idRightNow > 0) {
     		deleteContact(idRightNow);
     		return "ELIMINADO de Service Cloud<br>";
     	} else {
     		return "No existe en Service Cloud<br>";
     	}
	}
	public ResponseEntity<String> checkExistenceForCreateByJson (String contactJson) {
    	ObjectMapper om = new ObjectMapper();
		JsonNode nodes;
		try {
			nodes = om.readTree(contactJson).get(PropertiesReader.stringCorreo());
			String email = UriReplace.JsonTransformer(nodes.asText());
	    	
	    	if (getContactIdByEmail(email) > 0) {
	    		return new ResponseEntity<>("Ya existe en Service Cloud<br>", HttpStatus.OK);
	     	} else {
	     		postContactSave(contactJson);
	     		return new ResponseEntity<>("Creado en Service Cloud<br>", HttpStatus.CREATED);
	     	}
		} catch (IOException e) {
			return new ResponseEntity<>("Error al crear en Service Cloud<br>", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}