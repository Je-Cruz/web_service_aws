package com.soaint.ejercicioSpring.services;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;

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
import com.soaint.ejercicioSpring.model.salescloud.ContactsOSC;
import com.soaint.ejercicioSpring.model.salescloud.lead.SalesLead;
import com.soaint.ejercicioSpring.security.PropertiesReader;
import com.soaint.ejercicioSpring.services.connection.ConnectionHttp;
import com.soaint.ejercicioSpring.utils.UriReplace;

/**
 * 
 * @author jcruz
 *
 */
public class SalesCloud {

	ConnectionHttp connectionHttp = new ConnectionHttp();
	UriReplace uriReplace = new UriReplace();

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
	public ArrayList<Integer> getContactPartyNumberByEmail(String email) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(QueryContactId(email));
		credential(request);

		try {
			String jsonResponse = connectionHttp.ConnectionResponse(request);
			ObjectMapper om = new ObjectMapper();
			JsonNode nodes = om.readTree(jsonResponse).get("items");
			ArrayList<Integer> partyNumbers = new ArrayList<Integer>();

			if (nodes.isArray()) {
				for (JsonNode objNode : nodes) {
					partyNumbers.add(objNode.get("PartyNumber").asInt());
				}
			}

			return partyNumbers;
		} catch (IOException e) {
			return null;
		}
	}

	// GET LEAD-ID DE SALES LEAD
	public ArrayList<String> getLeadByContactPartyNumber(String email)
			throws ClientProtocolException, IOException {

		HttpGet request = new HttpGet(QueryLeadId(email));
		credential(request);
		
		try {
			String jsonResponse = connectionHttp.ConnectionResponse(request);
			ObjectMapper om = new ObjectMapper();
			JsonNode nodes = om.readTree(jsonResponse).get("items");
			ArrayList<String> leadsIdArrayList = new ArrayList<String>();
			if (nodes.isArray()) {
				for (JsonNode objNode : nodes) {
					leadsIdArrayList.add(objNode.get("LeadId").asText());
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
		HttpResponse response = connectionHttp.ConnectionResponse(request, entity);
		System.out.println(response.getStatusLine().getStatusCode());
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
		HttpResponse response = connectionHttp.ConnectionResponse(request, entity);
		System.out.println(response.getStatusLine().getStatusCode());
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
		ContactsOSC contact = new ContactsOSC(uriReplace.JsonTransformer(actualObj.get("nombre").asText()), 
				uriReplace.JsonTransformer(actualObj.get("apellidos").asText()),
				uriReplace.JsonTransformer(actualObj.get("correo").asText()));
		return obj.writeValueAsString(contact).toString();
	}
	
	public String serializeLead(String jsonLead) throws IOException {
		ObjectMapper obj = new ObjectMapper();
		JsonNode actualObj = obj.readTree(jsonLead);
		SalesLead contact = new SalesLead(actualObj.get("ContactPartyNumber").asInt());
		return obj.writeValueAsString(contact).toString();
	}
	
	public String checkExistenceForDeleteByEmail (String email) throws ClientProtocolException, IOException {
		ArrayList<Integer> idSalesCloud = getContactPartyNumberByEmail(email);
     	if (!idSalesCloud.isEmpty()) {
     		deleteLead(getLeadByContactPartyNumber(email));
     		deleteContact(idSalesCloud);
     		return "ELIMINADO de Sales Cloud<br>";
     	} else {
     		return "No existe en Sales Cloud<br>";
     	}
	}
	public String checkExistenceForCreateByJson (String contactJson) throws ClientProtocolException, IOException {
    	ObjectMapper om = new ObjectMapper();
		JsonNode nodes = om.readTree(contactJson).get("correo");
		String email = uriReplace.JsonTransformer(nodes.asText());
    	String leadExist = ", con Lead asociado";
		if (!getContactPartyNumberByEmail(email).isEmpty()) {
			//******
			
			
			
			if(getLeadByContactPartyNumber(email).isEmpty()) {
				postSalesLeadSave(email);
				leadExist = ", se crea y asocia Lead";
			}
			
			
			
			
			//******
     		return "Ya existe en Sales Cloud"+ leadExist +"<br>";
     	} else {
     		postContactSave(contactJson);
     		postSalesLeadSave(email);
     		return "Creado en Sales Cloud y añadido Lead<br>";
     	}
	}
	
	
	
	
}