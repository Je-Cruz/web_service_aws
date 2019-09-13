package com.soaint.ejercicioSpring.services.connection;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
	public String ConnectionResponse (HttpGet request) throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
	    HttpResponse response = client.execute(request);
	    System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
	    return EntityUtils.toString(response.getEntity()); 
	}
	// POST
	public HttpResponse ConnectionResponse (HttpPost request, StringEntity entity) throws ClientProtocolException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        request.setEntity(entity);
		return httpClient.execute(request);
	}
	// DELETE
	public void ConnectionResponse (HttpDelete request) throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
	}
}
