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

import org.apache.log4j.Logger;

import eoskrba.webapp.common.LogEnabledController;

class ShowPersonController extends LogEnabledController{
//	Logger log4j = Logger.getLogger(ShowPersonController.class);
    def show = {
		doLogInfo(log, "Prikaz osebe:"+params.id,session.user,session.role,"","",session.employeeType);
		def personId = params.id

		MainLdapSchema personLdap = MainLdapSchema.find(
			directory: "mainLdap",
			base: "ou=people",
			filter: "(uidatt=" + personId + ")"
		)
		
		if(personLdap.memberAtt == "patient") forward(action:'showPatient', id:personId)
		else if(personLdap.memberAtt == "caremanager")  forward(action:'showCaremanager', id:personId)
		else if(personLdap.memberAtt == "doctor")  forward(action:'showDoctor', id:personId)
		
	}
	
	def showPatient = {
		doLogInfo(log, "Prikaz pacienta:"+params.id,session.user,session.role,"","",session.employeeType);
		def personId = params.id
		def ud = UserData.findByUserId(personId)
		
		MainLdapSchema personLdap = MainLdapSchema.find(
			directory: "mainLdap",
			base: "ou=people",
			filter: "(uidatt=" + personId + ")"
		)
		
		def doubleDigitHours = [
			"00","01","02","03","04","05",
			"06","07","08","09","10","11",
			"12","13","14","15","16","17",
			"18","19","20","21","22","23"
		]
		
		def doubleDigitMinutes = [
			"00","01","02","03","04","05",
			"06","07","08","09","10","11",
			"12","13","14","15","16","17",
			"18","19","20","21","22","23",
			"24","25","26","27","28","29",
			"30","31","32","33","34","35",
			"36","37","38","39","40","41",
			"42","43","44","45","46","47",
			"48","49","50","51","52","53",
			"54","55","56","57","58","59"
		]
		
		def noticeTypes = [
			0: "Opravilo",
			1: "Vadba",
			2: "Prehranski napotek"
		]
		
		[personLdap:personLdap, ud:ud, doubleDigitsHours:doubleDigitHours, doubleDigitsMinutes:doubleDigitMinutes, noticeTypes:noticeTypes]
		
	}
	
	def showCaremanager = {
		doLogInfo(log, "Prikaz oskrbovalca:"+params.id,session.user,session.role,"","",session.employeeType);
		def personId = params.id
		def ud = UserData.findByUserId(personId)
		
		MainLdapSchema personLdap = MainLdapSchema.find(
			directory: "mainLdap",
			base: "ou=people",
			filter: "(uidatt=" + personId + ")"
		)
		
		[personLdap:personLdap, ud:ud]
		
	}
	
	def showDoctor = {
		doLogInfo(log, "Prikaz zdravnika:"+params.id,session.user,session.role,"","",session.employeeType);
		def personId = params.id
		def ud = UserData.findByUserId(personId)
		
		MainLdapSchema personLdap = MainLdapSchema.find(
			directory: "mainLdap",
			base: "ou=people",
			filter: "(uidatt=" + personId + ")"
		)
		
		[personLdap:personLdap, ud:ud]
		
	}
	
	def doctorExistsJSON = {
	
		doLogInfo(log, "Preverjam obstoj zdravnika",session.user,session.role,
			"","",session.employeeType);
		
		// Ger everybody from ldap
		def users = MainLdapSchema.findAll(
			directory: "mainLdap",
			base: "ou=people",
			filter: "(uidatt=*)"
		)

		def docExists = false
		users.each {
			// System.out.println(it.uidatt);
			if(it.uidatt == params.uid) docExists = true
		}

		render(view:"doctorExistsJSON",contentType:"text/json",model:[docExists:docExists])	
    
	}
	
}
