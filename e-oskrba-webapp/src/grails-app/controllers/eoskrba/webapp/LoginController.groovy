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

import org.apache.log4j.Logger
import eoskrba.webapp.common.LogEnabledController


class LoginController extends LogEnabledController {

	// Konstante
	public static final int SESSION_LOGIN_FORM_TTL  = 300   // 5 minut
	public static final int SESSION_USER_LOGGED_TTL = 10800 // 3 ure

	// Servisi
	def activitiService

    def index = { }
	
	// Prikaže prijavni obrazec
	def form = {
		
		// Če je uporabnik že prijavljen, ga najprej odjavimo
		if(session.logged == true) {
			int timestamp = new Date().time
			redirect(action:'logout', id: timestamp)
		}
		
		// Pobriši cookie-je, ki so zadolženi za prikaz vsebine v panel-u
		def regx = ~/(.*_pw)|(.*_pa)|(.*_pp)|(.*_w)/
		request.cookies.each {
			if(regx.matcher(it.name).matches()) {
				it.setPath("/")
				it.setMaxAge(0)
				response.addCookie(it)
			}
		}
		
		// Seji skrajšamo čas življenja
		session.setMaxInactiveInterval(SESSION_LOGIN_FORM_TTL)
		
	}
	
	// Poskusi prijaviti uporabnika
	def loginDo = {
		
		// Pridobi vpisane podatke
		def paramsUsername = params.username.trim()
		def paramsPassword = params.password.trim();
		
		// Preveri prijavo tako, da se poskusi avtenticirati preko Activiti-ja,
		// ki uporablja LDAP račune
		def loginSuccess = activitiService.authenticate(
			paramsUsername,
			paramsPassword
		)
		
		// Če uporabnik obstaja in geslo je pravino
		if(loginSuccess) {
			
			// V sejo zapišemo podatek o tem, da je uporabnik prijavljen
			session.logged = true
			
			// Seji podaljšamo čas življenja
			session.setMaxInactiveInterval(SESSION_USER_LOGGED_TTL)
			
			// Pridobi osnovne podatke preko Activiti-ja
			def jsonr = activitiService.getUserInfo(paramsUsername)
			session.user      = jsonr.id
			session.firstName = jsonr.firstName
			session.lastName  = jsonr.lastName
			session.email     = jsonr.email
			
			// Pridobi ostale podatke iz LDAP-a
			session.ldap = MainLdapSchema.find(
			    directory: "mainLdap",
			    base: "ou=people",
			    filter: "(uidatt=" + session.user + ")"
			)
			
			// Določi vlogo v role (patient, caremanager, doctor) in
			// določi študijo v employeeType (A, D, S, H, P, ...)
			session.role = session.ldap.memberAtt
			session.employeeType = session.ldap.employeeType.toString()

			// Pri pacientih naredimo še poizvedbo o dodeljeni sestrin in zdravniku
			if(session.role == "patient") {
				def docLdap = MainLdapSchema.find(
					directory: "mainLdap",
					base: "ou=people",
					filter: "(uidatt=" + session.ldap.patientsDoctorAtt + ")"
				)
				session.docUid =   session.ldap.patientsDoctorAtt
				session.docName = docLdap.cnPatient
				def nameAndSurname = docLdap.cnPatient.split()
				if(nameAndSurname.length > 1) {
					session.docNameShort = nameAndSurname[0][0] + ". " + nameAndSurname[1]
				} else {
					session.docNameShort = session.docName
				}
				if(docLdap.sexAtt == "MALE" || docLdap.sexAtt == "M") session.docMale = true;
				else session.docMale = false;
			}
			
			// Pridobi še podatke, ki jih hrani webapp o osebi
			def ud = UserData.findByUserId(session.user)
			
			// Če se uporabnik prijavlja prvič, skreiramo nov objekt UserData, v katerega
			// bo webapp hranil določene podatke za svoje potrebe
			if(ud == null) {
				ud = new UserData(userId: session.user, avatar:null)
				ud.lastLoginIP = request.getRemoteAddr()
				ud.lastLoginTime = new Date()
				ud.personalMessage = "Svojega pozdravnega sporočila še niste spremenili. Prosimo, kliknite tukaj in spremenite to sporočilo tako, da ga boste naslednjič prepoznali kot svojega."
			}

			// Nekatere podatke iz UserData naredimo sejno dostopne
			session.lastLoginIP = ud.lastLoginIP
			session.lastLoginTime = ud.lastLoginTime
			ud.lastLoginIP = request.getRemoteAddr()
			ud.lastLoginTime = new Date()
			session.personalMessage = ud.personalMessage
			
			// Shranimo spremembe v UserData
			ud.save()
			
			// Preveri, ali je uporabnikov račun povezan s Facebook računom
			FacebookUser fbc = FacebookUser.findByUserId(session.user)
			if(fbc == null) session.facebookConnected = false
			else session.facebookConnected = true
			
			// Določi sejno spremenljivko, ki zahteva, da se podatki v info-box-u
			// uporabnika osvežijo
			session.updatePatientProgress = true
			
			// Določi flash spremenljivko, ki zahteva, da se uporabniku prikaže
			// pozdravni obrazec
			flash.afterLogin = true;
			
			// Log
			doLogInfo(
				log,
				"Uspešen login",
				session.user,
				session.role,
				"",
				"",
				session.employeeType
			);
			
			// Preusmerimo uporabnika na prednastavljen zavihek
			if(session.role == "patient") redirect(controller:"redirects",action:"toMyTasks")
			else if(session.role == "caremanager") redirect(controller:"redirects",action:"toMyTasks")
			else if(session.role == "doctor") redirect(controller:"redirects",action:"toMyTasks")
			else redirect(controller:"about",action:"underConstruction")
			
		}
		
		// Če uporabnik ne obstaja ali geslo ni pravilno
		else {
			
			// log
			doLogInfo(
				log,
				"Neuspešen login",
				session.user,
				session.role,
				"",
				"",
				session.employeeType
			);
		
			// Pošlji nazaj na začetek
			redirect(controller:"login",action:"form",params:[wrong:true])
			
		}
		
	}
	
	// Odjavi uporabnika
	def logout = {
		
		// Log
		doLogInfo(
			log, "", session.user, session.role,
			"","",session.employeeType
		);
	
		// Razveljavi sejo
		session.invalidate()
		
		// Preusmeri na vstopni obrazec
		redirect(controller:"login",action:"form")
		
	}
	
}
