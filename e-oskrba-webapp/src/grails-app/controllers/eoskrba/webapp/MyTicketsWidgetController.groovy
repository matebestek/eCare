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

class MyTicketsWidgetController extends LogEnabledController {
//	Logger log4j = Logger.getLogger(MyTicketsWidgetController.class);
    def showOpen = {
		
		def lastTicketEntries = [];
		def c = Ticket.createCriteria()
		def tickets = c {
			and {
				eq("authorId",session.user)
				eq("closed",false)
			}
			order("dateAdded","desc")
		}
		tickets.each {
			def lastTicketEntry = (it.ticketEntries.toList().sort { a,b -> b.dateAdded <=> a.dateAdded})[0]
			lastTicketEntries << lastTicketEntry
		}
		doLogInfo(log, "Prikaz odprtih ticketov",session.user,session.role,"","",session.employeeType);
		return [entries:lastTicketEntries]
	}
	
	def showClosed = {
		def lastTicketEntries = [];
		def c = Ticket.createCriteria()
		def tickets = c {
			and {
				eq("authorId",session.user)
				eq("closed",true)
			}
			order("dateAdded","desc")
		}
		tickets.each {
			def lastTicketEntry = (it.ticketEntries.toList().sort { a,b -> b.dateAdded <=> a.dateAdded})[0]
			lastTicketEntries << lastTicketEntry
		}
		doLogInfo(log, "Prikaz zaprtih ticketov",session.user,session.role,"","",session.employeeType);
		return [entries:lastTicketEntries]
	}
	
	def newTicketForm = { }
	
	def newTicketSubmit = {
		def newTicket;
		def newEntry;
		doLogInfo(log, "Submit novega ticketa",session.user,session.role,"","",session.employeeType);
		newTicket = new Ticket(
			dateAdded: new Date(),
			topic: params.topic,
			authorId: session.user,
			authorFullName: session.firstName + " " + session.lastName,
			assigneeId: "",
			patientGroup: session.employeeType,
			closed: false,
			assigned: false
		)
		if( !newTicket.validate() ) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Vprašanje ni bilo oddano. Polje <b>'Tema (kratek opis)'</b> je obvezno!"]])
			return false
		}
		newEntry = new TicketEntry(
			content: params.question,
			authorId: session.user,
			authorFullName: session.firstName + " " + session.lastName,
			dateAdded: new Date()
		)
		newTicket.addToTicketEntries(newEntry)
		if( !newEntry.validate() ) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Vprašanje ni bilo oddano. Polje <b>'Vprašanje'</b> je obvezno!"]])
			return false
		}
		newTicket.save()
		newEntry.save()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Vaše vprašanje je bilo oddano!"]])
	}
	
	def showConversation = {
		doLogInfo(log, "prikaz pogovora",session.user,session.role,"","",session.employeeType);
		if(params.id != null) {
			def ticketToShow = Ticket.get(params.id)
			def entries = ticketToShow.ticketEntries.toList().sort { a,b -> a.dateAdded <=> b.dateAdded}
			return [ticket:ticketToShow,entries:entries]
		} else {
			render("<center><p>Za prikaz pogovora, pritisnite na gumb <b>'Poglej celoten pogovor'</b>.</p></center>")
		}
	}
	
	def replySubmit = {
		
		// Log
		doLogInfo(
			log, "Odgovor na ticket:"+params.id,session.user,
			session.role,"","",session.employeeType
		);
	
		// Pridobi vprašanje
		def ticket = Ticket.get(params.id)
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
		ticket.save()
		
		if(ticket.assigned) {
		
			// Pripravi vsebino email-a
			String emailBody = "Spoštovani!<br /><br />"
			emailBody += "Na odprto vprašanje pacienta <b>${ticket.authorFullName}</b> na temo <b>${ticket.topic}</b> je pacient dodal naslednji odgovor:<br /><br />"
			emailBody += "<div style=\"padding:12px;margin-left:24px;background-color:#E7EBF2;border:1px solid #ccc;\">"
			emailBody += params.content
			emailBody += "</div><br />"
			emailBody += "Prosimo, <b>prijavite se</b> v <a href=\"https://eoskrba.pint.upr.si/eOskrba-webapp\">spletno aplikacijo</a> in bodisi tudi sami dodajte odgovor ali prisilno <b>zaprite vprašanje</b>, če ste mnenja, da je vprašanje razrešeno."
			
			// Obvesti pacienta po e-pošti, da je prejel odgovor
			EmailSendRequest esr = new EmailSendRequest(
				timestamp: new Date(),
				fromId: session.user,
				toId: ticket.assigneeId,
				title: "eOskrba: Dodan odgovor na vprašanje, ki ste ga prevzeli",
				content: emailBody,
				sent: false,
				attempt: 0
			)
			esr.save()
			
		}
		
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Vaš odgovor je bil oddan!"]])
	}
	
	def closeSubmit = {
		try {
			doLogInfo(log, "Zapiranje ticketa:"+params.id,session.user,session.role,"","",session.employeeType);
			def ticketToClose = Ticket.get(params.id);
			ticketToClose.closed = true;
			ticketToClose.save()
			
			redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Vprašanje je bilo zaprto!"]])
			
		} catch (Exception e) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Vprašanje ni bilo zaprto!"]])
		}
    }
	
}
