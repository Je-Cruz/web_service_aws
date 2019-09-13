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

/**
 * 
 * @author jcruz
 *
 */
public class RightNow {
    
	ConnectionHttp connectionHttp = new ConnectionHttp();
	UriReplace uriReplace = new UriReplace();
		
	// QUERY DE ORACLE SERVICE CLOUD (RIGHT NOW)
	private String QueryId(String email) {
		return "https://" + PropertiesReader.getCredRightNow().concat("@")
				.concat(PropertiesReader.urlRightNow()).concat(PropertiesReader.getUrlQueryRightNow()).concat("'").concat(email).concat("'");
	}
	
	// URLS DE ORACLE SERVICE CLOUD (RIGHT NOW)
	private String UrlGet(int id) {
		return "https://" + PropertiesReader.getCredRightNow().concat("@")
				.concat(PropertiesReader.urlRightNow()).concat(PropertiesReader.getUrlRightNow()) + id;
	}
	private String UrlPost() {
		return "https://" + PropertiesReader.getCredRightNow().concat("@")
				.concat(PropertiesReader.urlRightNow()).concat(PropertiesReader.postUrlRightNow());
	}
	//------------------------
	
	// GET ID DE CONTACTS
	public int getContactIdByEmail(String email) {
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
	
	// POST SAVE CONTACT
	public void postContactSave(String person) throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(UrlPost());
        StringEntity entity = new StringEntity(serializeContact(person),
                ContentType.APPLICATION_JSON);
        connectionHttp.ConnectionResponse(request, entity);
	}
	
	// DELETE CONTACT
    public void deleteContact(int id) throws ClientProtocolException, IOException {
    	HttpDelete request = new HttpDelete(UrlGet(id));
        connectionHttp.ConnectionResponse(request);	
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
	
	public String checkExistenceForDeleteByEmail (String email) throws ClientProtocolException, IOException {
		int idRightNow = getContactIdByEmail(email);
		if (idRightNow > 0) {
     		deleteContact(idRightNow);
     		return "ELIMINADO de Right Now<br>";
     	} else {
     		return "No existe en Right Now<br>";
     	}
	}
	public String checkExistenceForCreateByJson (String contactJson) throws ClientProtocolException, IOException {
    	ObjectMapper om = new ObjectMapper();
		JsonNode nodes = om.readTree(contactJson).get("correo");
		String email = uriReplace.JsonTransformer(nodes.asText());
    	
    	if (getContactIdByEmail(email) > 0) {
    		return "Ya existe en Right Now<br>";
     	} else {
     		postContactSave(contactJson);
     		return "Creado en Right Now<br>";
     	}
	}
}