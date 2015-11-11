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

import org.codehaus.groovy.grails.web.json.JSONElement;

class FacebookResourcesController {
	
	public static final String HEADER_PRAGMA = "Pragma"
	public static final String HEADER_CACHEC = "Cache-Control"
	public static final String HEADER_EXPIRE = "Expires"
	
	// Service
	def facebookService

	// Channel datoteka, ki jo zahteva Facebook JS API zato, da poteka komunikacija
	// bolj gladko
    def channel = {
		
		// Želimo, da je veljavnost cache-a tega fajla čim daljša
		// maxAge je število sekund v 1 letu
		long secondsInYear = 31556926;
		
		// Potrebno je nastaviti tudi datum, ko cache ni več veljaven
		System.setProperty('user.timezone', 'Europe/Ljubljana')
		def cal = new GregorianCalendar()
		long expiresEpoch = cal.getTimeInMillis() + (secondsInYear*1000)
		
		// Nastavimo header atribute odgovora
		response.setHeader(HEADER_PRAGMA, "public")
		response.setHeader(HEADER_CACHEC, "max-age="+secondsInYear)
		response.setDateHeader(HEADER_EXPIRE, expiresEpoch)
		
	}
	
	// Majhen košček HTML-ja, ki pove, ali je uporabnikov račun že povezan
	// z njegovim Facebook računom ali ne
	def connectionWidget = {
		
		// Pripravi map za view
		Map r = [:]
	
		// Poglej v bazo, ali je uporabnik povezan
		FacebookUser fbdata = FacebookUser.findByUserId(session.user)
		
		if(fbdata == null) r["isFacebookConnected"] = false
		else {
			r["isFacebookConnected"] = true
			r["facebookUserData"] = fbdata
		}
		
		// V view
		return r
		
	}
	
	// Prejme JSON objekt, ki ga vrne Facebook API funkcija FB.Login po uspešno
	// kreirani povezavi
	def saveConnectionData = {
		
		// Pripravi map za view
		Map r = [:]
		
		// Kreiraj nov FacebookUser domenski objekt
		FacebookUser newFBUser = new FacebookUser(
			userId:      session.user,
			me_id:       params.id,
			me_name:     params.name,
			me_link:     params.link,
			me_username: params.username
		)
		
		// Shrani
		newFBUser.save()
		session.facebookConnected = true
		
		// Vrni flash message odgovor
		redirect(
			controller:"publicContent", action:"actionReport",
			params: [
				type:    "success",
				message:["Povezava ustvarjena!","Vaš račun je sedaj povezan z vašim Facebook računom"]
			]
		)
		
	}
	
	// Iz baze izbriše uporabnikov FacebookUser domenski objekt kar povzroči to,
	// da uporabnikov račun ni več povezan s Facebook računom
	def deleteConnectionData = {
		
		// Pripravi map za view
		Map r = [:]
		
		// Izbriši domesnki objekt
		FacebookUser fbUser = FacebookUser.findByUserId(session.user)
		if(fbUser != null) fbUser.delete()
		session.facebookConnected = false
		
		// Za json odgovor
		r["success"] = "true"
		r["timestamp"] = (new GregorianCalendar()).getTimeInMillis()
		
		// Vrni JSON objekt kot odgovor
		render(view:"deleteConnectionData",contentType:"text/json",model:r)
		
	}
	
	// Omogoča javni prikaz podrobnosti o neki objavi, ki je bila dodana na
	// Facebook zid. Podatki o vadbi, ki so prikazani, so pridobljeni iz
	// parametrov in ne iz baze ali eXista.
	// Opomba: Dostop do te akcije je omogočen vsem, tudi neprijavljenim
	// uporabnikom
	def activityDetails = {
		
		// Pripravi map za view
		Map r = [:]
		
		// Uporabnik
		r["id"]     = params.id
		FacebookMapping fbm = FacebookMapping.get(Long.parseLong(params.id))
		
		if(fbm != null) {

			FacebookUser fbu = FacebookUser.findByMe_id(fbm.facebookUserId)
			r["fbuser"] = fbu
	
			// Podrobnosti o aktivnosti
			r["date"]  = fbm.date
			r["time"]  = fbm.time
			r["type"]  = fbm.type
			r["typen"] = fbm.typen
			r["inte"]  = fbm.inte
			r["come"]  = fbm.come
			
			// OpenGraph URL
			r["ogurl"] = grailsApplication.config.grails.serverURL + "/facebookResources/activityDetails/" + params.id
			r["ogimage"] = grailsApplication.config.fb.imgHost + "/sports/1/" + fbm.type + ".png"
			
			// V view
			return r
			
		} else {
			render "Podan id ni veljaven."
		}
		
	}
	
}
