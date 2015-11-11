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

//import org.apache.log4j.Logger;

import eoskrba.webapp.common.LogEnabledController;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;

class TicketsWidgetController extends LogEnabledController{

    def showPending = {
		
		// Log
		doLogInfo(
			log, "Prikaz čakajočih nalog",session.user,session.role,
			"","",session.employeeType
		);
	
		// Pridobi paciente moje institucije
		List myPatients = MainLdapSchema.findAll(directory: "mainLdap",base: "ou=people") {
			and {
				eq "employeeType", session.employeeType
				eq "memberAtt", "patient"
				eq "healthcareInstitutionAtt", session.ldap.healthcareInstitutionAtt
			}
		}
	
		// Pridobi vsa ne-dodeljena vprašanja za mojo institucijo
		def tickets = []
		if(myPatients.size() > 0) {
			def c = Ticket.createCriteria()
			tickets = c {
				and {
					eq("closed",false)
					eq("assigned",false)
					eq("patientGroup",session.employeeType)
					'in'("authorId",myPatients.collect{it.uidatt})
				}
				order("dateAdded","asc")
			}
		}
		
		[tickets:tickets]
		
	}
	
	def showPendingDetails = {
		
		// Log
		doLogInfo(
			log, "Prikaz čakajočih nalog (podrobno)",session.user,session.role,
			"","",session.employeeType
		);
	
		// Pridobi paciente moje institucije
		List myPatients = MainLdapSchema.findAll(directory: "mainLdap",base: "ou=people") {
			and {
				eq "employeeType", session.employeeType
				eq "memberAtt", "patient"
				eq "healthcareInstitutionAtt", session.ldap.healthcareInstitutionAtt
			}
		}
	
		// Pridobi vsa ne-dodeljena vprašanja za mojo institucijo
		def tickets
		if(myPatients.size() > 0) {
			def c = Ticket.createCriteria()
			tickets = c {
				and {
					eq("closed",false)
					eq("assigned",false)
					eq("patientGroup",session.employeeType)
					'in'("authorId",myPatients.collect{it.uidatt})
				}
				order("dateAdded","asc")
			}
		}
		
		[tickets:tickets]
		
	}
	
	def showPendingConversation = {
		doLogInfo(log, "Prikaz čakajočega pogovora:"+params.id,session.user,session.role,"","",session.employeeType);
		def ticketToShow = Ticket.get(params.id)
		def entries = ticketToShow.ticketEntries.toList().sort { a,b -> a.dateAdded <=> b.dateAdded}
		
		return [ticket:ticketToShow,entries:entries]
	}
	
	def forceCloseTicket = {
		doLogInfo(log, "Zapiranje ticket-a:"+params.id,session.user,session.role,"","",session.employeeType);
		def ticketToClose = Ticket.get(params.id)
		// add reply to tell why ticket is closed
		def newReply = new TicketEntry(
			content: "<p>" + params.reason + "</p><p><i>Vaše vprašanje je bilo zaprto.</i></p>",
			authorId: session.user,
			authorFullName: session.firstName + " " + session.lastName,
			dateAdded: new Date()
		);
		ticketToClose.addToTicketEntries(newReply)
		newReply.save()
		// mark ticket as assigned
		ticketToClose.assigned = true;
		ticketToClose.assigneeId = session.user;
		// mark ticket as closed
		ticketToClose.closed = true;
		// save
		ticketToClose.save()
		
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Vprašanje je bilo prisilno zaprto!"]])
    }
	
	def addReply = {
		
		// Log
		doLogInfo(
			log, "Dodajanje odgovora za ticket:"+params.id,
			session.user, session.role, "", "", session.employeeType
		);
	
		// Dodaj odgovor v bazo
		Ticket ticket = Ticket.get(params.id)
		def newReply = new TicketEntry(
			content: params.content,
			authorId: session.user,
			authorFullName: session.firstName + " " + session.lastName,
			dateAdded: new Date()
		);
		ticket.addToTicketEntries(newReply)
		if( !newReply.validate() ) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Odgovor ni bil oddan. Morda ste poskusili oddati odgovor brez besedila."]])
			return false
		}
		newReply.save()
		
		// Če je to prvi odgovor, potem je zdravnik prevzel vprašanje
		if(ticket.assigned == false) {
			ticket.assigned = true
			ticket.assigneeId = session.user
		}
		ticket.save()
		
		// Pripravi vsebino email-a
		String emailBody = "Spoštovani ${ticket.authorFullName}!<br /><br />"
		emailBody += "Dne ${ticket.dateAdded.format('dd.MM.yyyy')} ste odprili naslednje vprašanje na temo <b>${ticket.topic}</b>:<br /><br />"
		emailBody += "<div style=\"padding:12px;margin-left:24px;background-color:#E7EBF2;border:1px solid #ccc;\">"
		emailBody += ((TicketEntry)(ticket.ticketEntries.sort{it.dateAdded}.get(0))).content
		emailBody += "</div><br />"
		emailBody += "Vprašanju je oseba <b>${session.firstName} ${session.lastName}</b> pravkar dodala naslednji odgovor:<br /><br />"
		emailBody += "<div style=\"padding:12px;margin-left:24px;background-color:#E7EBF2;border:1px solid #ccc;\">"
		emailBody += params.content
		emailBody += "</div><br />"
		emailBody += "Prosimo, <b>prijavite se</b> v <a href=\"https://eoskrba.pint.upr.si/eOskrba-webapp\">spletno aplikacijo</a> in bodisi tudi sami dodajte odgovor ali <b>zaprite vprašanje</b>, če ste s prejetim odgovorom zadovoljni."
		
		// Obvesti pacienta po e-pošti, da je prejel odgovor
		EmailSendRequest esr = new EmailSendRequest(
			timestamp: new Date(),
			fromId: session.user,
			toId: ticket.authorId,
			title: "eOskrba: Prejeli ste odgovor na Vaše odprto vprašanje",
			content: emailBody,
			sent: false,
			attempt: 0
		)
		esr.save()
		
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Vaš odgovor je bil oddan!"]])
    }
	
	def showMyOpened = {
		doLogInfo(log, "Prikaz odprtih ticketov",session.user,session.role,"","",session.employeeType);
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

		//filter tickets depending on HC instituion (for diabetes)
		if (session.employeeType == EMPLOYEE_TYPE_ENUM.DIABETES.eval() || session.employeeType == EMPLOYEE_TYPE_ENUM.HUJSANJE.eval()) {
			List myPatients = MainLdapSchema.findAll(directory: "mainLdap",base: "ou=people") {
				and {
					eq "employeeType", session.employeeType
					eq "memberAtt", "patient"
					eq "healthcareInstitutionAtt", session.ldap.healthcareInstitutionAtt
				}
			}

			def listFiltered = []
			for (ticket in tickets) {
				if (isMyPatient(ticket.authorId, myPatients)) {
					listFiltered.add(ticket)
				}
			}
			tickets = listFiltered
		}
		[tickets:tickets]
		
	}
	
	def showConversation = {
		
		doLogInfo(log, "Prikaz ticket-a:"+params.id,session.user,session.role,"","",session.employeeType);
		def ticketToShow = Ticket.get(params.id)
		def entries = ticketToShow.ticketEntries.toList().sort { a,b -> a.dateAdded <=> b.dateAdded}
		
		return [ticket:ticketToShow,entries:entries]
		
	}
	
	def reassignToDoctor = {
	
		// Najdi vprašanje
		def ticketToReassign = Ticket.get(params.id)
		
		// Pripravi vsebino email-a
		String emailBody = "Spoštovani!<br /><br />"
		emailBody += "Dne ${ticketToReassign.dateAdded.format('dd.MM.yyyy')} je oseba <b>${ticketToReassign.authorFullName}</b> odprla vprašanje na temo <b>${ticketToReassign.topic}</b>:<br /><br />"
		emailBody += "<div style=\"padding:12px;margin-left:24px;background-color:#E7EBF2;border:1px solid #ccc;\">"
		emailBody += ((TicketEntry)(ticketToReassign.ticketEntries.sort{it.dateAdded}.get(0))).content
		emailBody += "</div><br />"
		emailBody += "Vprašanje je najprej prevzela oseba <b>${session.firstName} ${session.lastName}</b>, vendar ga je kasneje dodelila Vam.<br /><br />"
		emailBody += "Prosimo, <b>prijavite se</b> v <a href=\"https://eoskrba.pint.upr.si/eOskrba-webapp\">spletno aplikacijo</a> in odgovorite na vprašanje."
		
		// Obvesti pacienta po e-pošti, da je prejel odgovor
		EmailSendRequest esr = new EmailSendRequest(
			timestamp: new Date(),
			fromId: session.user,
			toId: params.uid,
			title: "eOskrba: Dodeljeno Vam je bilo vprašanje pacienta",
			content: emailBody,
			sent: false,
			attempt: 0
		)
		esr.save()
		
		// Prepiši vprašanje
		ticketToReassign.assigned = true;
		ticketToReassign.assigneeId = params.uid;
 
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Vprašanje je bilo dodeljeno izbranemu zdravniku!"]])
		
	}
	
	def isMyPatient(authorId, myPatientList) {
		for (pat in myPatientList) {
			if (pat.uidatt == authorId)
				return true;
		}
		return false;
	}
	
}
