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

import si.pint.database.exist.*
import si.pint.database.exist.DBManager.SystemType
import eoskrba.webapp.common.LogEnabledController
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM

class PatientStatusWidgetController extends LogEnabledController{
	
    def showLast = {
		
		// Log
		doLogInfo(
			log, "Prikaz statusa pacienta", session.user,
			session.role,"","",session.employeeType
		);

		// Pridobi podatke iz eXist-a
		String[][] table = DBRead.readLast(SystemType.EOSKRBAWEBAPP, session.user, session.employeeType, params.what)
		
		// Pridobi paciente iz prave študije
		List matches = MainLdapSchema.findAll(directory: "mainLdap",base: "ou=people") {
			and {
				eq "employeeType", session.employeeType
				eq "memberAtt", "patient"
				or {
					eq "healthcareInstitutionAtt", session.ldap.healthcareInstitutionAtt
					eq "employeeType", EMPLOYEE_TYPE_ENUM.ASTMA.eval()
					eq "employeeType", EMPLOYEE_TYPE_ENUM.SHIZOFRENIJA.eval()
				}
			}
		}
		
		
		def data = []
		
		// Id v tabeli podatkov zamenjaj s polnim imenom
		for(int i = 0; i < table.length; i++) {
			String uidAtt = table[i][0]
			def ldapo = matches.find {
				it.uidatt == uidAtt
			}
			if(ldapo != null) {
				data << [patientData: ldapo, value: table[i][1], timestamp: table[i][2]]
			} 

		}
		
		// Preparseaj timestamp-e
		data.each {
			
			try {
			
				def tsn = ""
				StringTokenizer strtok = new StringTokenizer(it.timestamp,"- :")
				String tok = ""
				
				tok = strtok.nextToken();
				if(tok.size() < 2) tok = "0" + tok;
				tsn += tok + "-"
				
				tok = strtok.nextToken();
				if(tok.size() < 2) tok = "0" + tok;
				tsn += tok + "-"
				
				tok = strtok.nextToken();
				if(tok.size() < 2) tok = "0" + tok;
				tsn += tok + " "
				
				tok = strtok.nextToken();
				if(tok.size() < 2) tok = "0" + tok;
				tsn += tok + ":"
				
				tok = strtok.nextToken();
				if(tok.size() < 2) tok = "0" + tok;
				tsn += tok + ":"
				
				tok = strtok.nextToken();
				if(tok.size() < 2) tok = "0" + tok;
				tsn += tok
	
				it.timestamp = tsn
			
			} catch (Exception e) {
				
			it.timestamp = "1970-01-01 00:00:00"
			
			}
			
		}
		
		// Presortiraj po vrednosti
		data = data.sort { a, b ->
			Double.valueOf(a.value) <=> Double.valueOf(b.value)
		}
		
		[data:data,what:params.what]
		
	}
	
	def showLastTwin = {
		
		// Log
		doLogInfo(
			log, "Prikaz statusa pacienta",session.user,
			session.role,"","",session.employeeType
		);

		// Pridobi podatke iz exist-a
		String[][] table1 = DBRead.readLast(SystemType.EOSKRBAWEBAPP, session.user,session.employeeType, params.what1)
		String[][] table2 = DBRead.readLast(SystemType.EOSKRBAWEBAPP, session.user,session.employeeType, params.what2)
		
		// Najdi osebe, ki so del študije
		List matches = MainLdapSchema.findAll(directory: "mainLdap",base: "ou=people") {
			and {
				eq "employeeType", session.employeeType
				eq "memberAtt", "patient"
				eq "healthcareInstitutionAtt", session.ldap.healthcareInstitutionAtt
			}
		}
		
		def data = [:]

		// Prefiltiraj
		for(int i = 0; i < table1.length; i++) {
			String uidAtt = table1[i][0]
			def ldapo = matches.find {
				it.uidatt == uidAtt
			}
			if(ldapo != null) {
				data[uidAtt] = [patientData: ldapo, value1: table1[i][1], value2: null, timestamp1: table1[i][2], timestamp2: null]
			}
		}
		for(int i = 0; i < table2.length; i++) {
			String uidAtt = table2[i][0]
			def ldapo = matches.find {
				it.uidatt == uidAtt
			}
			if(ldapo != null) {
				if(data[uidAtt] == null) {
					data[uidAtt] = [patientData: ldapo, value1: null, value2: table2[i][1], timestamp2: table2[i][2]]
				} else {
					data[uidAtt].value2 = table2[i][1]
					data[uidAtt].timestamp2 = table2[i][2]
				}
			} 
		}
		
		// Parse-aj timestamp
		data.each {
			
			try{
				
				if(it.value.timestamp2 != null) {
					
					def tsn = ""
					StringTokenizer strtok = new StringTokenizer(it.value.timestamp2,"- :")
					String tok = ""
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok + "-"
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok + "-"
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok + " "
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok + ":"
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok + ":"
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok
		
					it.value.timestamp2 = tsn
				}
				
				if(it.value.timestamp1 != null) {
					
					def tsn = ""
					StringTokenizer strtok = new StringTokenizer(it.value.timestamp1,"- :")
					String tok = ""
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok + "-"
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok + "-"
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok + " "
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok + ":"
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok + ":"
					
					tok = strtok.nextToken();
					if(tok.size() < 2) tok = "0" + tok;
					tsn += tok
		
					it.value.timestamp1 = tsn
				}
			
			}  catch(Exception e) {
			
				it.value.timestamp2 = "1970-01-01 00:00:00"
			
			}
			
		}
		
		// Sortiraj po prvi vrednosti
		data = data.sort { a, b ->
			Double.valueOf(a.value.value1 != null ? a.value.value1 : "0") <=> Double.valueOf(b.value.value1 != null ? b.value.value1 : "0")
		}
		
		[data:data,what1:params.what1,what2:params.what2]
		
	}
	
}
