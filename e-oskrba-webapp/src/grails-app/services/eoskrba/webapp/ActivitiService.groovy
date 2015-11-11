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

import eoskrba.webapp.RestService
import eoskrba.webapp.RestService.RestClient
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

import org.codehaus.groovy.grails.web.json.JSONArray;
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject;

import grails.util.Environment

class ActivitiService {
	
    static transactional = true
	
	// STATIC reference to the ONLY REST client (rc) used
	static RestClient rc = null
	static String activitiUsername = "USERNAME"
	static String activitiPassword = "PASSWORD"
	static boolean initialized = false
	static boolean initializing = false
	
	// STATIC method that creates a single instance of rc and sotres it internally
	static void init() {
		
		initializing = true
		
		// Create rc instance
		System.out.println("[eoskrba-webapp] Initializing Activiti service...")
		switch(Environment.current) {
			case Environment.DEVELOPMENT:
				// this.rc = RestService.createClient("http","localhost",8080,"activiti-rest/service")
				this.rc = RestService.createClient("http","88.200.63.187",8080,"activiti-rest/service")
			break
			case Environment.TEST:
				this.rc = RestService.createClient("http","localhost",8080,"activiti-rest/service")
			break
			case Environment.PRODUCTION:
				this.rc = RestService.createClient("http","localhost",8080,"activiti-rest/service")
			break
		}
		this.rc.setCredentials(this.activitiUsername,this.activitiPassword)
		
		// Try authenticating to Activiti
		def jsonr
		try {
			jsonr = this.rc.doPost("login",[
				"userId": this.activitiUsername,
				"password": this.activitiPassword
			])
		} catch (Exception ex) {
			System.out.println("[eoskrba-webapp] ERROR: Could not authenticate to Activiti.")
			ex.printStackTrace()
			System.exit(1)
		}
		
		System.out.println("[eoskrba-webapp] Activiti service initialized.")
		
		initialized = true
		initializing = false
		
	}

	// Vrne število dodeljenih nalog (Activiti & ne-Activiti)
    int getUserTasksNumber(HttpSession session) {

		// Pridobi Activiti task-e
		def jsonr = this.rc.doGet("tasks?assignee=" + session.user + "&size=100")
		def tasksNumber = jsonr.total
		
		// Dodatni ne-Activiti taski
		if(session.role == "doctor" || session.role == "caremanager") {
			
			// Pridobi paciente moje institucije
			List myPatients = MainLdapSchema.findAll(directory: "mainLdap",base: "ou=people") {
				and {
					eq "employeeType", session.employeeType
					eq "memberAtt", "patient"
					eq "healthcareInstitutionAtt", session.ldap.healthcareInstitutionAtt
				}
			}
			
			//*************************************************//
			//*          1) Nedodeljenea vprašanja            *//
			//*************************************************//

			// Pridobi vsa ne-dodeljena vprašanja za mojo institucijo
			if(myPatients.size() > 0) {
				def c = Ticket.createCriteria()
				def tickets = c {
					and {
						eq("closed",false)
						eq("assigned",false)
						eq("patientGroup",session.employeeType)
						'in'("authorId",myPatients.collect{it.uidatt})
					}
					order("dateAdded","asc")
				}
				
				// Če obstaja kako neprevzeto vprašanje, pokaži to kot nalogo
				if(tickets.size() > 0) tasksNumber++
			}
			
			//*************************************************//
			//*        2) Prevzeta vprašanja - osebje         *//
			//*************************************************//
			
			// Pridobi vsa odprta vprašanja, ki sem jih prevzel in je zadnji odgovor
			// zapisal pacient. Torej vprašanja, ki čakajo na moj odgovor
			def c = Ticket.createCriteria()
			def tickets = c {
				and {
					eq("closed",false)
					eq("assigned",true)
					eq("assigneeId",session.user)
					eq("patientGroup",session.employeeType)
				}
				order("dateAdded","asc")
			}
			
			// Ali je zadnji odgovor moj?
			tickets.each { ticket ->
				TicketEntry lastEntry = ((TicketEntry)(((Ticket)ticket).ticketEntries.sort{it.dateAdded}.reverse().get(0)))
				if(lastEntry.authorId != session.user) {
					tasksNumber++
				}
			}
			
		} else if(session.role == "patient") {
		
			//*************************************************//
			//*      3) Nedodeljenea vprašanja - pacient      *//
			//*************************************************//
		
			// Pridobi vsa moja odprta vprašanja in je zadnji odgovor
			// zapisal zdravnik. Torej vprašanja, ki čakajo na moj odgovor
			def c = Ticket.createCriteria()
			def tickets = c {
				and {
					eq("closed",false)
					eq("assigned",true)
					eq("authorId",session.user)
					eq("patientGroup",session.employeeType)
				}
				order("dateAdded","asc")
			}
			
			// Ali je zadnji odgovor moj?
			tickets.each { ticket ->
				TicketEntry lastEntry = ((TicketEntry)(((Ticket)ticket).ticketEntries.sort{it.dateAdded}.reverse().get(0)))
				if(lastEntry.authorId != session.user) {
					tasksNumber++		
				}
			}
		
		}
		
		return tasksNumber
		
    }
	
	// Returns a JSONElement with all user tasks assigned to user
	JSONElement getUserTasks(String user, int quantity) {
		def jsonr = this.rc.doGet("tasks?assignee=" + user + "&size=" + quantity)
		return jsonr
	}
	
	// Returns single user task.
	// taskId is usually an integer, but it should be passed as a string.
	JSONElement getUserTask(String taskId) {
		return this.rc.doGet("task/" + taskId)
	}
	
	// Returns user info.
	JSONElement getUserInfo(String user) {
		return this.rc.doGet("user/" + user)
	}
	
	// Returns user groups.
	JSONElement getUserGroups(String user) {
		return this.rc.doGet("user/" + user + "/groups?type=assignment")
	}
	
	// Tries getting form and returns HTTP status
	int getFormStatus(String formId) {
		return this.rc.doGetStatus("task/" + formId + "/form")
	}
	
	// Returns raw form content (HTML as a string)
	String getForm(String formId) {
		return this.rc.doGetRaw("task/" + formId + "/form")
	}
	
	// Completes user task with input data.
	// If user tasks requires no input data, dataMap should be an empty HashMap.
	JSONElement completeTask(String taskId, HashMap dataMap) {
		return this.rc.doPut("task/" + taskId + "/complete",dataMap)
	}
	
	// Returns proces definition form
	String getProcesDefinitionForm(String processDefinitionKey) {
		
		String processDefinitionId = null
		JSONElement processes = this.rc.doGet("process-definitions?sort=version&order=desc&size=10000")
		processDefinitionId = getLatestVersionProcessID(processes.data, processDefinitionKey)

		return this.rc.doGetRaw("process-definition/" + processDefinitionId + "/form")
		
	}
	
	// Sterts new proces by its key and submits input data
	// If user tasks requires no input data, dataMap should be an empty HashMap.
	JSONElement startProces(String processDefinitionKey, HashMap dataMap) {
		
		String processDefinitionId = null
		JSONElement processes = this.rc.doGet("process-definitions?sort=version&order=desc&size=10000")
		processDefinitionId = getLatestVersionProcessID(processes.data, processDefinitionKey)
		
		dataMap['processDefinitionId'] = processDefinitionId
		return this.rc.doPost("process-instance",dataMap)
		
	}
	
	// Tries to log in to Activiti
	// Returns true or false
	boolean authenticate(String username, String password) {
		JSONElement jsonr = this.rc.doPost("login",[
			userId:username.trim(),
			password:password.trim()
		])
		if(jsonr.success == true) return true
		else return false
	}  
	
	// Gets request parameters and puts them in Map
	// Returns Map
	Map getParameters(HttpServletRequest req) {
		
		def data = [:]
		def parameterMap = req.getParameterMap()
		parameterMap.each {
			if(it.key != "action" && it.key != "controller" && it.key != "id") {
				if(it.value.size() == 1)
					data.put(it.key.toString(),it.value[0].toString())
				else
					data.put(it.key.toString(),it.value.toString())
			}
		}
		return data
		
	}
	
	// Iz JSON Array-a definicij activiti procesov vrne ID najnovejše verzije procesa,
	// ki je določen z imenom key
	String getLatestVersionProcessID(JSONArray data, String processDefinitionKey) {
		
		String processDefinitionId = null
		
		for(int i = 0; i < data.length(); i++) {
			JSONObject obj = (JSONObject) data.getJSONObject(i)
			if(processDefinitionKey.equals(obj.getString("key"))) {
				processDefinitionId = obj.getString("id")
				// Debug TODO: Odstrani
				System.out.println("[eOskrba-webapp] [ActivitiService] ID najnovejse verzije procesa '" + processDefinitionKey + "' je " + processDefinitionId)
				// Najnovejsa verzija procesa je najdena, nehaj s preiskovanjem
				break
			}
		}
		
		return processDefinitionId
		
	}
	
}
