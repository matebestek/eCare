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

import java.sql.Timestamp

import org.apache.log4j.Logger

import eoskrba.webapp.common.LogEnabledController
import groovy.json.StringEscapeUtils;

class LiveChatWidgetController extends LogEnabledController{
//	Logger log4j = Logger.getLogger(LiveChatWidgetController.class);
    def patientButton = { }
	
	// Ustvari novo sejo za pacienta (nova zahteva za pogovor)
	def patientWait = {
		
		// Log
		doLogInfo(
			log, "patientWait",session.user,session.role,
			"","",session.employeeType
		);
	
		// Poišči ping v bazi
		def myPing = LiveChatPing.findByPersonId(session.user)
		
		// Če ping-a ni, ustvari novega
		if(myPing == null) {
			myPing = new LiveChatPing(
				personId: session.user,
				timestamp: new Date()
			)
			myPing = myPing.merge()
		}
		// Če ping obstaja, ga osveži
		else {
			myPing.timestamp = new Date()
			myPing = myPing.merge()
		}
		
		// Pripravi spremenljivke za chat sejo
		def chatSessionId
		def oldSession
		def chatSession = LiveChatSession.findByEndedAndPatientId(false,session.user)
		
		// Če nedokončana chat seja že obstaja, nadaljuj pogovor
		if(chatSession != null) {
			chatSessionId = chatSession.id
			oldSession    = true
		} 
		
		// Če nedokončanih sej ni, ustvari novo sejo (in nov chat)
		else {
			chatSession = new LiveChatSession(
				patientId: session.user,
				patientFullName: session.firstName + " " + session.lastName,
				patientGroup: session.employeeType,
				assigneeId: "",
				assigneeFullName: "",
				dateStarted: new Date(),
				assigned: false,
				ended: false
			)
			chatSession = chatSession.merge()
			chatSessionId = chatSession.id
			oldSession    = false
		}
		
		[chatSessionId:chatSessionId, oldSession:oldSession]
		
	}
	
	// Pridobi seznam pogovorov, ki čakajo na sogovornika
	def showPending = {
		
		// Log
		doLogInfo(log, "showPending",session.user,session.role,
			"","",session.employeeType);
		def block   = false
		
		// Check if an assigned chat session already exists
		def chatSession = LiveChatSession.findByEndedAndAssigneeId(false,session.user)
		if(chatSession != null) {
			// Force block
			block = true
		}
		if(params.block == 'true') {
			block = true
		}
		// Get pending requests
		def pending = LiveChatSession.findAllByAssignedAndPatientGroup(false,session.employeeType)
		[pending:pending,block:block]
		
	}
	
	// Pridruži zdravnika ali sestro k pogovoru
	def startChat = {
		doLogInfo(log, "startChat",session.user,session.role,
			"","",session.employeeType);
		// get chat session
		def chatSession = LiveChatSession.get(params.id)
		// prepare variables for model
		def chatOtherId
		def chatOtherFullName
		def startingMessage
		// if user is not patient, assign session
		if(session.role != 'patient') {
			// send ping
			def myPing = LiveChatPing.findByPersonId(session.user)
			if(myPing == null) {
				// create ping for the first time
				myPing = new LiveChatPing(
					personId: session.user,
					timestamp: new Date()
				)
				myPing = myPing.merge()
			} else {
				// update ping
				myPing.timestamp = new Date()
				myPing = myPing.merge()
			}
			if(chatSession.assigned == false) {
				chatSession.assigned = true
				chatSession.assigneeId = session.user
				chatSession.assigneeFullName = session.firstName + " " + session.lastName
				chatSession = chatSession.merge()
			} else {
				render("<p>Oprostite, zahtevo za pogovor je nekaj trenutkov pred Vami prevzel nekdo drug.</p>")
				return false
			}
			chatOtherId = chatSession.patientId
			chatOtherFullName = chatSession.patientFullName
			startingMessage = "Prevzeli ste zahtevo za pogovor v živo s pacientom " + chatOtherFullName + "."
		} else {
			chatOtherId = chatSession.assigneeId
			chatOtherFullName = chatSession.assigneeFullName
			startingMessage = "Oseba " + chatOtherFullName + " je prevzela Vašo zahtevo za pogovor v živo."
		}
		[chatOtherId:chatOtherId,chatOtherFullName:chatOtherFullName,chatSessionId:chatSession.id,startingMessage:startingMessage]
		
	}
	
	// Prikaži zgodovino pogovorov (pacientov pogled)
	def patientHistory = {
		doLogInfo(log, "patientHistory",session.user,session.role,
			"","",session.employeeType);
		def c = LiveChatSession.createCriteria()
		def chatSessions = c {
			and {
				eq("patientId",session.user)
				eq("ended",true)
				eq("assigned",true)
			}
			if(params.showMore != "true") {
				maxResults(5)
			}
			order("dateStarted","desc")
		}
		[chatSessions:chatSessions,showMore:params.showMore]
		
	}
	
	// Prikaži zgodovino pogovorov (zdravnikov in sestrin pogled)
	def history = {
		doLogInfo(log, "history",session.user,session.role,
			"","",session.employeeType);
		def c = LiveChatSession.createCriteria()
		def chatSessions = c {
			and {
				eq("assigneeId",session.user)
				eq("ended",true)
				eq("assigned",true)
			}
			if(params.showMore != "true") {
				maxResults(10)
			}
			order("dateStarted","desc")
		}
		[chatSessions:chatSessions,showMore:params.showMore]
		
	}
	
	// Prikaži tok že končanega pogovora
	def showConversation = {
		doLogInfo(log, "showConversation",session.user,session.role,
			"","",session.employeeType);
		def chatSession = LiveChatSession.get(params.id)
		def chatEntries = chatSession.chatEntries.toList().sort { a,b -> a.timestamp <=> b.timestamp}
		[chatSession:chatSession,chatEntries:chatEntries]
		
	}
	
	// invisible actions
	
	// creates or updates own ping entry to let system know user is still online
	def sendPing = {
		doLogInfo(log, "sendPing",session.user,session.role,
			"","",session.employeeType);
		def myPing = LiveChatPing.findByPersonId(session.user)
		if(myPing == null) {
			// create ping for the first time
			myPing = new LiveChatPing(
				personId: session.user,
				timestamp: new Date()	
			)
			myPing = myPing.merge()
		} else {
			// update ping
			myPing.timestamp = new Date()
			myPing = myPing.merge()
		}
		render(view:"sendPing",contentType:"text/json")
		
	}
	
	// check if user params.personId is still online
	def checkPing = {
		
		// Log
		doLogInfo(
			log, "checkPing",session.user,session.role,
			"","",session.employeeType
		);
	
		def online
		def userPing = LiveChatPing.findByPersonId(params.personId)
		if(userPing == null) {
			// user not online
			online = false
		} else {
			def longDate_now = (new Date()).getTime()
			def longDate_usr = (userPing.timestamp).getTime()
			def millisecondsPassed = longDate_now - longDate_usr
			if(millisecondsPassed >= 15000) {
				// user not online
				online = false
			} else {
				// user online
				online = true
			}
		}
		render(view:"checkPing",contentType:"text/json",model:[online:online])
		
	}
	
	def checkIfAssigned = {
		
		// Log
		doLogInfo(
			log, "checkIfAssigned",session.user,session.role,
			"","",session.employeeType
		);
	
		def assigned
		def chatSession = LiveChatSession.get(params.id)
	
		if(chatSession.assigned) {
			assigned = true
		} else {
			assigned = false
		}
		
		render(view:"checkIfAssigned",contentType:"text/json",model:[assigned:assigned])
		
	}
	
	def checkIfEnded = {
		doLogInfo(log, "checkIfEnded",session.user,session.role,
			"","",session.employeeType);
		def ended
		def chatSession = LiveChatSession.get(params.id)
		if(chatSession.ended) {
			ended = true
		} else {
			ended = false
		}
		render(view:"checkIfEnded",contentType:"text/json",model:[ended:ended])
		
	}
	
	def cancelChatSession = {
		doLogInfo(log, "cancelChatSession",session.user,session.role,
			"","",session.employeeType);
		def chatSession = LiveChatSession.get(params.id)
		chatSession.delete()
		render(view:"cancelChatSession",contentType:"text/json")
		
	}
	
	def checkPending = {
		
		def pending = LiveChatSession.findAllByAssignedAndPatientGroup(false,session.employeeType)
		def pendingNum = pending.size()
		if(pendingNum > 0){
			doLogInfo(log, "checkPending:" + pendingNum,session.user,session.role,
				"","",session.employeeType);
		}
		render(view:"checkPending",contentType:"text/json",model:[pending:pendingNum])
		
	}
	
	def jsonGetMessages = {
		doLogInfo(log, "jsonGetMessages",session.user,session.role,
			"","",session.employeeType);
		def chatSession = LiveChatSession.get(params.id)
		def c = LiveChatEntry.createCriteria()
		def msgs = c {
			and {
				eq("chatSession",chatSession)
				gt("timestamp",new Timestamp(params.after as long))
			}
			order("timestamp","asc")
		}
		render(view:"jsonGetMessages",contentType:"text/json",model:[msgs:msgs])
		
	}
	
	def postMessage = {
		
		// Log
		doLogInfo(
			log, "postMessage",session.user,session.role,
			"","",session.employeeType
		);
		
		def chatSession = LiveChatSession.get(params.id)
		def newTimestamp = (new Date()).toTimestamp()
		
		def newMessage = new LiveChatEntry(
			chatSession: chatSession,
			authorId: session.user,
			authorFullName: session.firstName + " " + session.lastName,
			content: params.content,
			timestamp: newTimestamp
		)
		chatSession.addToChatEntries(newMessage)
		chatSession.save()
		render(view:"postMessage",contentType:"text/json",model:[timestamp:newTimestamp])
		
	}
	
	def endChatSession = {
		doLogInfo(log, "endChatSession",session.user,session.role,
			"","",session.employeeType);
		def chatSession = LiveChatSession.get(params.id)
		chatSession.ended = true
		chatSession.save()
		render(view:"endChatSession",contentType:"text/json")
		
	}
	
}
