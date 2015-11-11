/***
 * Copyright (c) 2013.
 * University of Primorska, Andrej Marušič Institute. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * Project eOskrba (http://eOskrba.si) was financed by Slovenian Research
 * Agency and Ministry of Health of Republic of Slovenia.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package si.pint.activiti.rest.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import si.pint.eoskrba.conf.SystemConstant;

public class HttpClientUtils {
	
	static final String SERVER_IP = SystemConstant.ACTIVITI_REST_HOST_NAME.toString();
	static final String SERVER_PORT = "8080";
	static final String SERVER_URL = "http://" + SERVER_IP + ":" + SERVER_PORT + "/activiti-rest/service/";
	static final String USERNAME = SystemConstant.ACTIVITI_REST_USERNAME.toString();
	static final String PASSWORD = SystemConstant.ACTIVITI_REST_PASSWORD.toString();
	public static final int HTTP_OK = 200;

	private static DefaultHttpClient initHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(SERVER_IP, Integer.parseInt(SERVER_PORT)),
                new UsernamePasswordCredentials(USERNAME, PASSWORD));		
		
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
		return httpClient;
	}
	
	public static String doGet(final String url) throws HttpException, IOException, URISyntaxException {

		DefaultHttpClient httpClient = initHttpClient();
		HttpGet httpget = new HttpGet(SERVER_URL + url);
		HttpResponse response = httpClient.execute(httpget);
		HttpEntity entity = response.getEntity();
		InputStream instream = entity.getContent();
		return read(instream);
	}
	
	public static String doPost(final String url, final String POSTText) throws URISyntaxException, HttpException, IOException {
		DefaultHttpClient httpClient = initHttpClient();
		
		HttpPost httpPost = new HttpPost(SERVER_URL + url);
		StringEntity entity = new StringEntity(POSTText, "UTF-8");
		BasicHeader basicHeader = new BasicHeader(HTTP.CONTENT_TYPE, "application/json");
		httpPost.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		entity.setContentType(basicHeader);
		httpPost.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPost);
		InputStream instream = response.getEntity().getContent();
		return read(instream);
	}	

	
	public static boolean doPut(final String url, final String PUTText) throws URISyntaxException, HttpException, IOException {
		DefaultHttpClient httpClient = initHttpClient();

		HttpPut httpPut = new HttpPut(SERVER_URL + url);
		httpPut.addHeader("Accept", "application/json");
		httpPut.addHeader("Content-Type", "application/json");
		StringEntity entity = new StringEntity(PUTText, "UTF-8");
		entity.setContentType("application/json");
		httpPut.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPut);
		int statusCode = response.getStatusLine().getStatusCode();
		return statusCode == HTTP_OK ? true : false;
	}
	
	public static boolean doDelete(final String url) throws HttpException, IOException, URISyntaxException {
		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpDelete httpDelete = new HttpDelete(SERVER_URL + url);
		httpDelete.addHeader("Accept", "text/html, image/jpeg, *; q=.2, */*; q=.2");
		HttpResponse response = httpClient.execute(httpDelete);
		int statusCode = response.getStatusLine().getStatusCode();
		return statusCode == HTTP_OK ? true : false;
	}	
	
	private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}
}
