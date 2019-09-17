package com.soaint.ejercicioSpring.services;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaint.ejercicioSpring.model.salescloud.ContactsOSC;
import com.soaint.ejercicioSpring.model.salescloud.lead.SalesLead;
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
public class SalesCloud {

	ConnectionHttp connectionHttp = new ConnectionHttp();

	// CREDENCIALES DE SALES CLOUD
	private HttpGet credential(HttpGet request) {
		request.setHeader(HttpHeaders.AUTHORIZATION, PropertiesReader.getCredSalesCloud());
		return request;
	}

	private HttpPost credential(HttpPost request) {
		request.setHeader(HttpHeaders.AUTHORIZATION, PropertiesReader.getCredSalesCloud());
		return request;
	}

	private HttpDelete credential(HttpDelete request) {
		request.setHeader(HttpHeaders.AUTHORIZATION, PropertiesReader.getCredSalesCloud());
		return request;
	}
	//------------------------
	
	// QUERYS DE SALES CLOUD
	private String QueryContactId(String email) {
		return PropertiesReader.urlSalesCloud().concat(PropertiesReader.getUrlQuerySalesCloud()).concat(email);
	}
	private String QueryLeadId(String email) {
		return PropertiesReader.urlSalesCloud().concat(PropertiesReader.getUrlQuerySalesCloudLead()).concat(email);
	}
	//------------------------

	// URLS DE SALES CLOUD
	private String UrlGet(int id) {
		return PropertiesReader.urlSalesCloud().concat(PropertiesReader.getUrlSalesCloud()) + id;
	}
	private String UrlGetLead(String id) {
		return PropertiesReader.urlSalesCloud().concat(PropertiesReader.urlSalesCloudLead()).concat("/").concat(id);
	}
	private String UrlPostContact() {
		return PropertiesReader.urlSalesCloud().concat(PropertiesReader.postUrlSalesCloud());
	}
	private String UrlPostLead() {
		return PropertiesReader.urlSalesCloud().concat(PropertiesReader.urlSalesCloudLead());
	}
	//------------------------

	// GET PARTY NUMBERS DE CONTACTS
	public ArrayList<Integer> getContactPartyNumberByEmail(String email) {
		HttpGet request = new HttpGet(QueryContactId(email));
		credential(request);
		ArrayList<Integer> partyNumbers = new ArrayList<Integer>();
		try {
			String jsonResponse = connectionHttp.ConnectionResponse(request);
			ObjectMapper om = new ObjectMapper();
			JsonNode nodes = om.readTree(jsonResponse).get(PropertiesReader.stringItems());
			
			if (nodes.isArray()) {
				for (JsonNode objNode : nodes) {
					partyNumbers.add(objNode.get(PropertiesReader.stringPartyNumber()).asInt());
				}
			}

			return partyNumbers;
		} catch (IOException e) {
			return partyNumbers;
		} catch (NullPointerException e) {
				return partyNumbers;
			}
	}

	// GET LEAD-ID DE SALES LEAD
	public ArrayList<String> getLeadByContactPartyNumber(String email) {

		HttpGet request = new HttpGet(QueryLeadId(email));
		credential(request);
		
		try {
			String jsonResponse = connectionHttp.ConnectionResponse(request);
			ObjectMapper om = new ObjectMapper();
			JsonNode nodes = om.readTree(jsonResponse).get(PropertiesReader.stringItems());
			ArrayList<String> leadsIdArrayList = new ArrayList<String>();
			if (nodes.isArray()) {
				for (JsonNode objNode : nodes) {
					leadsIdArrayList.add(objNode.get(PropertiesReader.stringLeadId()).asText());
				}
			}
			return leadsIdArrayList;
		} catch (IOException e) {
			return null;
		}
	}

	// POST SAVE CONTACT
	public void postContactSave(String person) throws UnsupportedCharsetException, IOException {
		HttpPost request = new HttpPost(UrlPostContact());
		credential(request);
		StringEntity entity = new StringEntity(serializeContact(person), ContentType.APPLICATION_JSON);
		connectionHttp.ConnectionResponse(request, entity);
	}

	// POST SAVE SALES LEAD
	public void postSalesLeadSave(String email) throws ClientProtocolException, IOException {
		int lenghtArrayList = getContactPartyNumberByEmail(email).size() - 1;
		int lastPartyNumber = getContactPartyNumberByEmail(email).get(lenghtArrayList);
		HttpPost request = new HttpPost(UrlPostLead());
		credential(request);
		SalesLead salesCloudContact = new SalesLead(lastPartyNumber);
		ObjectMapper obj = new ObjectMapper();
		String jsonLead = obj.writeValueAsString(salesCloudContact).toString();
		StringEntity entity = new StringEntity(serializeLead(jsonLead), ContentType.APPLICATION_JSON);
		connectionHttp.ConnectionResponse(request, entity);
	}

	// DELETE CONTACT
	public void deleteContact(ArrayList<Integer> partynumberarray) throws ClientProtocolException, IOException {
		for (int i = 0; i < partynumberarray.size(); i++) {
			HttpDelete request = new HttpDelete(UrlGet(partynumberarray.get(i)));
			credential(request);
			connectionHttp.ConnectionResponse(request);
		}
	}
	// DELETE SALES LEAD
	public void deleteLead(ArrayList<String> leadsIdArrayList) throws ClientProtocolException, IOException {
		for (int i = 0; i < leadsIdArrayList.size(); i++) {
			HttpDelete request = new HttpDelete(UrlGetLead(leadsIdArrayList.get(i)));
			credential(request);
			connectionHttp.ConnectionResponse(request);
		}
	}
	
	public String serializeContact(String person) throws IOException {
		ObjectMapper obj = new ObjectMapper();
		JsonNode actualObj = obj.readTree(person);
		ContactsOSC contact = new ContactsOSC(UriReplace.JsonTransformer(actualObj.get(PropertiesReader.stringNombre()).asText()), 
				UriReplace.JsonTransformer(actualObj.get(PropertiesReader.stringApellidos()).asText()),
				UriReplace.JsonTransformer(actualObj.get(PropertiesReader.stringCorreo()).asText()));
		return obj.writeValueAsString(contact).toString();
	}
	
	public String serializeLead(String jsonLead) throws IOException {
		ObjectMapper obj = new ObjectMapper();
		JsonNode actualObj = obj.readTree(jsonLead);
		SalesLead contact = new SalesLead(actualObj.get(PropertiesReader.stringContactPartyNumber()).asInt());
		return obj.writeValueAsString(contact).toString();
	}
	
	public String checkExistenceForDeleteByEmail (String email) throws ClientProtocolException, IOException {
		ArrayList<Integer> idSalesCloud = getContactPartyNumberByEmail(email);
		String leadExist = ", no tenía ningún Lead asociado";
     	if (!idSalesCloud.isEmpty()) {
     		if(!getLeadByContactPartyNumber(email).isEmpty()) {
     			deleteLead(getLeadByContactPartyNumber(email));
     			leadExist = ", con Lead asociado";
     		}
     		deleteContact(idSalesCloud);
     		return "ELIMINADO de Sales Cloud".concat(leadExist).concat("<br>");
     	} else {
     		return "No existe en Sales Cloud<br>";
     	}
	}
	public String checkExistenceForCreateByJson (String contactJson){
    	ObjectMapper om = new ObjectMapper();
		JsonNode nodes;
		try {
			
			nodes = om.readTree(contactJson).get("correo");
			String email = UriReplace.JsonTransformer(nodes.asText());
	    	String leadExist = ", con Lead asociado";
			if (!getContactPartyNumberByEmail(email).isEmpty()) {
				if(getLeadByContactPartyNumber(email).isEmpty()) {
					postSalesLeadSave(email);
					leadExist = ", se crea y asocia Lead";
				}
	     		return "Ya existe en Sales Cloud".concat(leadExist).concat("<br>");
	     	} else {
	     		postContactSave(contactJson);
	     		postSalesLeadSave(email);
	     		return "Creado en Sales Cloud y añadido Lead<br>";
	     	}
		
		} catch (IOException e) {
			return "Error al crear en Sales Cloud<br>";
		}

	}
}