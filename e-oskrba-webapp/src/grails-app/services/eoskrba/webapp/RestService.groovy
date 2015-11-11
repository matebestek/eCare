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
package eoskrba.webapp

import grails.converters.JSON;

import java.io.InputStream;

import org.apache.http.*
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicHeader
import org.apache.http.protocol.HTTP;
import org.codehaus.groovy.grails.web.json.JSONElement
import java.io.InputStreamReader;

class RestService {

	public static class RestClient {
		
		public DefaultHttpClient httpClient
		public String serverProtocol
		public String serverIP
		public int    serverPort
		public String serverPath
		public String baseUrl
		public String username
		public String password
		
		public RestClient(String serverProtocol, String serverIP, int serverPort, String serverPath) {
			this.httpClient = new DefaultHttpClient();
			this.serverProtocol = serverProtocol
			this.serverIP = serverIP
			this.serverPort = serverPort
			this.serverPath = serverPath
			this.baseUrl = "http://" + serverIP + ":" + serverPort + "/" + serverPath + "/"
			this.username = ""
			this.password = ""
		}
		
		public void setCredentials(String username, String password) {
			
			this.username = username
			this.password = password
			
			this.httpClient.getCredentialsProvider().setCredentials(
				new AuthScope(this.serverIP, this.serverPort),
				new UsernamePasswordCredentials(this.username, this.password)
			);
			
		}
		
		public JSONElement doGet(String path) {
			
			JSONElement jsonel
			
			try {
				HttpGet      getter  = new HttpGet(this.baseUrl + path)
				HttpResponse gotten  = this.httpClient.execute(getter)
				HttpEntity   entity  = gotten.getEntity()
				InputStream  stream  = entity.getContent()
				String       jsonraw = readToString(stream)
							 jsonel  = JSON.parse(jsonraw)
			} catch(java.lang.IllegalStateException e) {
				this.sleep(100);
				jsonel = this.doGet(path)
			}
			
			return jsonel
			
		}
		
		public String doGetRaw(String path) {
			
			String jsonraw
			
			try {
				HttpGet      getter  = new HttpGet(this.baseUrl + path)
				HttpResponse gotten  = this.httpClient.execute(getter)
				HttpEntity   entity  = gotten.getEntity()
				InputStream  stream  = entity.getContent()
				             jsonraw = readToString(stream)
			} catch(java.lang.IllegalStateException e) {
				this.sleep(100);
				jsonraw = this.doGetRaw(path)
			}
			
			return jsonraw
			
		}
		
		public int doGetStatus(String path) {
			
			int statusNumber
			
			try {
				HttpGet      getter  = new HttpGet(this.baseUrl + path)
				HttpResponse gotten  = this.httpClient.execute(getter)
				statusNumber         = gotten.getStatusLine().getStatusCode()
				HttpEntity   entity  = gotten.getEntity()
				InputStream  stream  = entity.getContent()
				String       jsonraw = readToString(stream)
			} catch(java.lang.IllegalStateException e) {
				this.sleep(100);
				statusNumber = this.doGetStatus(path)
			}
			
			return statusNumber
			
		}
		
		public JSONElement doPost(String path, HashMap data) {
			
			//System.out.println("[RestService] CONVERTING HASH MAP\n\t" + data);
			
			JSONElement jsonel
			
			try {
				String   jsonString  = hashMapToJsonString(data)
				//System.out.println("[RestService] SENT POST " + path + "\n\t" + jsonString);
				HttpPost     poster  = new HttpPost(this.baseUrl + path)
				poster.addHeader("Accept", "application/json")
				poster.addHeader("Content-Type", "application/json")
				StringEntity entity  = new StringEntity(jsonString,"UTF-8")
				entity.setContentType("application/json");
				poster.setEntity(entity);
				HttpResponse gotten  = this.httpClient.execute(poster);
				InputStream  stream  = gotten.getEntity().getContent();
				String       jsonraw = readToString(stream)
				             jsonel  = JSON.parse(jsonraw)
				//System.out.println("[RestService] RETURNED POST " + path + "\n\t" + jsonraw);
			} catch(java.lang.IllegalStateException e) {
				this.sleep(100);
				jsonel = this.doPost(path, data)
			}
						 
			return jsonel
			
		}
		
		public JSONElement doPut(String path, HashMap data) {
			
			JSONElement jsonel
			
			try {
				String   jsonString  = hashMapToJsonString(data)
				HttpPut      putter  = new HttpPut(this.baseUrl + path);
				putter.addHeader("Accept", "application/json");
				putter.addHeader("Content-Type", "application/json");
				StringEntity entity  = new StringEntity(jsonString, "UTF-8");
				entity.setContentType("application/json");
				putter.setEntity(entity);
				HttpResponse gotten  = this.httpClient.execute(putter);
				InputStream  stream  = gotten.getEntity().getContent();
				String       jsonraw = readToString(stream)
				             jsonel  = JSON.parse(jsonraw)
			} catch(java.lang.IllegalStateException e) {
				this.sleep(100);
				jsonel = this.doPut(path, data)
			}
			
			return jsonel
			
		}
		
		// public JSONElement doDelete(String path)
		// TODO: Implement doDelete
		
		public void shutdown() {
			
			this.httpClient.getConnectionManager().shutdown();
			
		}
		
		public static String readToString(InputStream inStream) {
			
			StringBuilder sb = new StringBuilder();
			BufferedReader r = new BufferedReader(new InputStreamReader(inStream), 1000);
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				sb.append(line);
			}
			inStream.close();
			return sb.toString();
			
		}
		
		public static String hashMapToJsonString(HashMap data) {
			
			int dataSize = data.size()
			int iterator = 0
			
			String jsonString = "{"
			data.each {
				jsonString += "\"" + it.key + "\":"
				if(it.value instanceof HashMap) {
					jsonString += hashMapToJsonString(it.value)
				} else if(it.value instanceof List) {
					jsonString += listToJsonString(it.value)
				} else if(it.value instanceof Number) {
					jsonString += it.value.toString()
				} else if(it.value instanceof Boolean) {
					jsonString += it.value.toString()
				} else if(it.value == null) {
					jsonString += "null"
				} else {
					jsonString += "\"" + it.value.toString() + "\""
				}
				iterator++
				if(iterator < dataSize) {
					jsonString += ","
				}
			}
			jsonString += "}"
			
			return jsonString
			
		}
		
		public static String listToJsonString(List data) {
			
			int listSize = data.size()
			int iterator = 0
			
			String listString = "["
			data.each {
				if(it instanceof HashMap) {
					listString += hashMapToJsonString(it)
				} else if(it instanceof List) {
					listString += listToJsonString(it)
				} else if(it instanceof Number) {
					listString += it.toString()
				} else if(it instanceof Boolean) {
					listString += it.toString()
				} else if(it == null) {
					listString += "null"
				} else {
					listString += "\"" + it.toString() + "\""
				}
				iterator++
				if(iterator < listSize) {
					listString += ","
				}
			}
			listString += "]"
			
			return listString
			
		}
		
	}
	
    static transactional = true

    static RestClient createClient(String serverProtocol, String serverIP, int serverPort, String serverPath) {

		return new RestClient(serverProtocol, serverIP, serverPort, serverPath)
		
    }
	
}
