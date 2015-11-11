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
import groovy.json.JsonBuilder;

import org.apache.http.*
import org.apache.http.client.entity.*
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*
import org.apache.http.protocol.HTTP;
import org.codehaus.groovy.grails.web.json.JSONElement;

class FacebookService {
	
	def restService
	def grailsApplication
	
	static String accessToken = null

	/**
	 * Metoda, ki na zid (wall) Facebook uporabnika doda objavo.
	 * 
	 * @param profileId Facebook ID (številka) uporabnika
	 * @param message Sporočilo objave (vsebina)
	 * @param link URL naslov povezave, ki je pripeta objavi
	 * @param name Ime povezave, ki je pripeta objavi
	 * @param caption Naslov, ki se pojavi pod imenom povezave, ki je pripeta objavi
	 * @param description Opis,  ki se pojavi pod imenom povezave, ki je pripeta objavi
	 * @param picture URL naslov slike, ki je pripeta povezavi
	 * @param source URL naslov Flash datoteke ali videa, ki je pripet objavi
	 * @param place Glej https://developers.facebook.com/docs/reference/api/post/
	 * @param tags Glej https://developers.facebook.com/docs/reference/api/post/
	 * @return Facebookov HTTP odgovor na POST zahtevo
	 */
    String publishToWall(String profileId, String message = null, String link = null, String name = null, String caption = null, String description = null, String picture = null, String source = null, String place = null, String tags = null) {

		// Pripravi HTTP client
		DefaultHttpClient client = new DefaultHttpClient()
		HttpPost poster = new HttpPost( "https://graph.facebook.com/" + profileId + "/feed" )
		
		// Procesiraj parametre
		List<NameValuePair>     nameValuePairs = new ArrayList<NameValuePair>();
		                        nameValuePairs.add(new BasicNameValuePair("access_token", getAccessToken()));
		if(message != null)     nameValuePairs.add(new BasicNameValuePair("message",     message));
		if(link != null)        nameValuePairs.add(new BasicNameValuePair("link",        link));
		if(name != null)        nameValuePairs.add(new BasicNameValuePair("name",        name));
		if(caption != null)     nameValuePairs.add(new BasicNameValuePair("caption",     caption));
		if(description != null) nameValuePairs.add(new BasicNameValuePair("description", description));
		if(picture != null)     nameValuePairs.add(new BasicNameValuePair("picture",     picture));
		if(source != null)      nameValuePairs.add(new BasicNameValuePair("source",      source));
		if(place != null)       nameValuePairs.add(new BasicNameValuePair("place",       place));
		if(tags != null)        nameValuePairs.add(new BasicNameValuePair("tags",        tags));
		poster.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		
		// Izvedi POST zahtevo, pridobi odgovor
		HttpResponse gotten = client.execute(poster);
		
		return gotten.getEntity().getContent().getText()
		
    }
	
	String getAccessToken() {
		
		if(accessToken == null) {
			DefaultHttpClient client = new DefaultHttpClient()
			HttpGet httpget = new HttpGet(
				"https://graph.facebook.com/oauth/access_token?client_id=" + 
				grailsApplication.config.fb.appId +
				"&client_secret=" +
				grailsApplication.config.fb.appSecret +
				"&grant_type=client_credentials")
			HttpResponse httpr = client.execute(httpget)
			accessToken = (httpr.getEntity().getContent().getText()).replaceAll("access_token=", "")
			return accessToken
		} else {
			return accessToken
		}
		
	}
	
}
