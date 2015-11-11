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

import grails.orm.HibernateCriteriaBuilder;
import java.util.Date;

class MyCalendarService {
	
	// Servisi
	def existService

	// Vsem udeležencem e-Oskrbe preveri, ali imajo na današnji dan kakšna opravila 
	// izvzemajoč "Vadba po programu" pri eŠortu (ta se pošlje po drugi poti)
	// in če se izkaže, da jih imajo, jim pošlje 1 mail z naštetimi vsemi opravili
    def sendDailyNotifiers() {
		
		// Današnji datum
		System.setProperty('user.timezone', 'Europe/Ljubljana')
		GregorianCalendar gc = new GregorianCalendar()
		int year  = gc.get(GregorianCalendar.YEAR)
		int month = gc.get(GregorianCalendar.MONTH) + 1
		int day   = gc.get(GregorianCalendar.DAY_OF_MONTH)

		// Najdi vsa opravila za današnji dan
		HibernateCriteriaBuilder hcb = CalendarNotice.createCriteria()
		List calendarNotices = hcb.list {
			and {
				eq("year",  year)
				eq("month", month)
				eq("day",   day)
			}
		}
		
		// Sestavi množico vseh uporabnikov in njihovih opravil
		Map noticesByOwnersMap = calendarNotices.groupBy { ((CalendarNotice)it).ownerID }
		
		// Najdi še vse paciente, ki nimajo opravil
		List matches = MainLdapSchema.findAll(directory: "mainLdap",base: "ou=people") {
			and {
				eq "memberAtt", "patient"
			}
		}
		matches.each { patient ->
			if( !(noticesByOwnersMap.containsKey(patient.uidatt)) ) {
				noticesByOwnersMap.put(patient.uidatt, [])
			}
		}
		
		// Zabeleži trenutni čas
		System.out.println("[eOskrba-webapp] Trenutni čas in datum: " + new Date().toString());
		
		// Za vsakega uporabnika sestavi mail in ga pošlji
		noticesByOwnersMap.each { noticeByOwner ->
			
			String ownerId = noticeByOwner.key
			List   notices = ((List)(noticeByOwner.value)).sort { ((CalendarNotice)it).startHour }
			
			String cont = ""
			cont += "Spoštovani!<br /><br />"
			cont += "V svojem koledarju imate za današnji dan vpisana naslednja opravila:<br /><br />"
			cont += "<table style=\"border-collapse:collapse;margin:12px;width:100%;\">"
			cont += "<thead><tr>"
			cont += "<th style=\"padding:6px;border-bottom:1px solid #ccc;\">Pričetek</th>"
			cont += "<th style=\"padding:6px;border-bottom:1px solid #ccc;\">Konec</th>"
			cont += "<th style=\"padding:6px;border-bottom:1px solid #ccc;\">Vrsta</th>"
			cont += "<th style=\"padding:6px;border-bottom:1px solid #ccc;\">Opis</th>"
			cont += "</tr></thead>"
			cont += "<tbody>"

			notices.each { noticeIter ->
				cont += "<tr>"
				CalendarNotice notice = ((CalendarNotice)noticeIter)
				if( (notice.startHour > notice.endHour) || ((notice.startHour == notice.endHour) && (notice.startMinutes >= notice.endMinutes)) ) {
					cont += "<td style=\"padding:6px;\">/</td>"
					cont += "<td style=\"padding:6px;\">/</td>"
				} else {
					cont += "<td style=\"padding:6px;\">" + ((notice.startHour<10)?("0"+notice.startHour):(notice.startHour)) + ":" + ((notice.startMinutes<10)?("0"+notice.startMinutes):(notice.startMinutes)) + "</td>"
					cont += "<td style=\"padding:6px;\">" + ((notice.endHour<10)?("0"+notice.endHour):(notice.endHour)) + ":" + ((notice.endMinutes<10)?("0"+notice.endMinutes):(notice.endMinutes)) + "</td>"
				}
				cont += "<td style=\"padding:6px;\">" + noticeTypeToString(notice.noticeType) + "</td>"
				cont += "<td style=\"padding:6px;\">" + notice.content + "</td>"
				cont += "</tr>"
			}
			cont += "</tbody>"
			cont += "</table>"
			cont += "<br />"
			cont += "Lep dan še naprej Vam želi Vaše zdravstveno osebje!"
					
			if(notices.size() > 0) {
				EmailSendRequest esr = new EmailSendRequest(
					timestamp: new Date(),
					fromId: ownerId,
					toId:   ownerId,
					title: "eOskrba: Opravila danes",
					content: cont,
					sent: false,
					attempt: 0
				)
				esr.save()
				// log
				System.out.println("[eOskrba-webapp] Poslan mail 'eOskrba: Opravila danes' uporabniku " + ownerId + " z številom opravil " + notices.size());
			}
		}
		
    }
	
	// Pretvori noticeType v besedo, ki opisuje tip
	static String noticeTypeToString(int noticeType) {
		if(noticeType == 0) return "opravilo"
		if(noticeType == 1) return "vadba"
		if(noticeType == 2) return "prehranski napotek"
		else return "neznano"
	}
	
}
