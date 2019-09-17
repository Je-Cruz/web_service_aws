package com.soaint.ejercicioSpring.services.connection;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author jcruz
 *
 */
public class ConnectionHttp {
	
	// GET
	public String ConnectionResponse (HttpGet request) {
		HttpClient client = HttpClientBuilder.create().build();
	    HttpResponse response;
		try {
			response = client.execute(request);
			return EntityUtils.toString(response.getEntity()); 
		} catch (IOException e) {
			e.printStackTrace();
			return null; 
		}
	    
	}
	
	// POST
	public HttpResponse ConnectionResponse (HttpPost request, StringEntity entity) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        request.setEntity(entity);
		try {
			return httpClient.execute(request);
		} catch (IOException e) {
			e.printStackTrace();
			return null; 
		}
	}
	// DELETE
	public void ConnectionResponse (HttpDelete request) {
		HttpClient client = HttpClientBuilder.create().build();
        try {
			HttpResponse response = client.execute(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
