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

import eoskrba.webapp.common.LogEnabledController

class MyCalendarWidgetController extends LogEnabledController{
	
	// Servisi
	def existService

    def show = {
		
		// Log
		doLogInfo(
			log, "",session.user,session.role,
			"","",session.employeeType
		);
	
		// Doloci casovni pas
		System.setProperty('user.timezone', 'Europe/Ljubljana')
		
		// Preveri pravilnost parametrov in parsaj v int
		if( params.month == null || params.year == null) {
			render("<p>Napaka: Nekateri zahtevani parametri niso bili podani.</p>")
			return false
		}
		def month
		def year
		try {
			month = Integer.parseInt(params.month)
			year = Integer.parseInt(params.year)
		} catch(Exception e) {
			render("<p>Napaka: Neveljaven format parametrov.</p>")
			return false
		}
		
		// Pripravi GregorianCalendar
		def cal = new GregorianCalendar(year, month - 1, 1)
		cal.firstDayOfWeek = Calendar.MONDAY
		cal.time;
		def numberOfDays = cal.getActualMaximum(Calendar.DATE)
		def startingDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK)) - 1
		if(startingDayOfWeek == 0) startingDayOfWeek = 7
		
		// Poišči vsa opravila za izbran mesec
		def notices = [:]
		def noticesByDay = [:]
		def c = CalendarNotice.createCriteria()
		def noticesList = c.list {
			and {
				eq("ownerID",session.user)
				eq("year",year)
				eq("month",month)
			}
		}
		noticesList.each {
			// Shrani število opravkov na dan
			if(notices[it.day] != null ) notices[it.day]++
			else notices.put(it.day,1)
			// Podrobnosti o opravkih
			if(noticesByDay[it.day] == null) noticesByDay[it.day] = []
			noticesByDay[it.day].add(it)
		}
		
		// Slovenska imena mesecev
		def monthLabels = ['januar','februar','marec','april','maj','junij','julij','avgust','september','oktober','november','december']
		def monthLabel = monthLabels[month-1]
		
		// Današnji dan
		def calToday = new GregorianCalendar();
		calToday.time
		def today_day        = calToday.get(Calendar.DAY_OF_MONTH)
		def today_month      = calToday.get(Calendar.MONTH) + 1
		def today_monthLabel = monthLabels[today_month - 1]
		def today_year       = calToday.get(Calendar.YEAR)
		
		// Če gre za eŠport, poišči še kateri dnevi v mesecu imajo planirano vadbo po programu.
		List programNotices = []
		if(session.employeeType == "P") {
			programNotices = existService.getExerciseDatesInMonth(session.user, month, year)
			if(programNotices == null) programNotices = []
		}
		
		// V view
		return [
			today_day: today_day,
			today_month: today_month,
			today_monthLabel: today_monthLabel,
			today_year: today_year,
			year: year,
			month: month,
			days: numberOfDays,
			starting: startingDayOfWeek,
			notices: notices,
			noticesByDay: noticesByDay,
			programNotices: programNotices,
			monthLabel: monthLabel
		]
		
	}
	
	def details = {
		
		// Log
		doLogInfo(
			log, "",session.user,session.role,
			"","",session.employeeType
		);
	
		// Nastavi casovni pas
		System.setProperty('user.timezone', 'Europe/Ljubljana')
		
		// Preveri ustreznost zapisa datuma in pretvori v int
		if( params.day == null || params.month == null || params.year == null) {
			render("<p>Napaka: Nekateri zahtevani parametri niso bili pdoani.</p>")
			return false
		}
		def day
		def month
		def year
		try {
			day = Integer.parseInt(params.day)
			month = Integer.parseInt(params.month)
			year = Integer.parseInt(params.year)
		} catch(Exception e) {
			render("<p>Napaka: Neveljaven format parametrov.</p>")
			return false
		}
		
		// Skonstruiraj še GregorianCalendar objekt
		def dayMonthYear = new GregorianCalendar(year,month-1,day)
		
		// Določi slovensko ime trenutnemu mesecu
		def monthLabels = ['januar','februar','marec','april','maj','junij','julij','avgust','september','oktober','november','december']
		def monthLabel = monthLabels[month-1]
		
		// Pridobi opravila za izbran dan
		def c = CalendarNotice.createCriteria()
		def noticesList = c.list {
			and {
				eq("ownerID",session.user)
				eq("year",year)
				eq("month",month)
				eq("day",day)
			}
		}
		
		// Sortiraj po uri začetka
		noticesList = noticesList.sort { it.startHour*60+it.startMinutes }
		
		// Če gre za eŠport, poišči še podatke o vadbi po programu
		Map programExercise = null
		if(session.employeeType == "P") {
			programExercise = existService.getExerciseData(
				session.user,
				year + "-" + ((month<10)?("0"+month):(month)) + "-" + ((day<10)?("0"+day):(day)) + "T00"
			)
		}
		
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
		
		// V view
		return [
			day:day, month:month,year:year, dayMonthYear:dayMonthYear,
			monthLabel:monthLabel,noticesList:noticesList,doubleDigitsHours:doubleDigitHours,
			doubleDigitsMinutes:doubleDigitMinutes,noticeTypes:noticeTypes,
			programExercise:programExercise
		]
		
	}
	
	def addNotice = {
		
		// Log
		doLogInfo(
			log, "",session.user,session.role,
			"","",session.employeeType
		);
	
		// Nastavi casovni pas
		System.setProperty('user.timezone', 'Europe/Ljubljana')
		
		// Preveri veljavnost datuma in pretvori v int
		if( params.datumDay == null || params.datumMonth == null || params.datumYear == null || params.content == null) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Nekatera obvezna polja niso bila izpolnjena!"]])
			return false
		}
		def datumDay
		def datumMonth
		def datumYear
		def fromHour
		def fromMinutes
		def tillHour
		def tillMinutes
		def content
		def noticeType
		try {
			datumDay = Integer.parseInt(params.datumDay)
			datumMonth = Integer.parseInt(params.datumMonth)
			datumYear = Integer.parseInt(params.datumYear)
			content = params.content
			fromHour = params.fromHour
			fromMinutes = params.fromMinutes
			tillHour = params.tillHour
			tillMinutes = params.tillMinutes
			noticeType = Integer.parseInt(params.noticeType)
			if(fromHour == "no" || fromMinutes == "no" || tillHour == "no" || tillMinutes == "no") {
				fromHour    = 0
				fromMinutes = 0
				tillHour    = 0
				tillMinutes = 0
			} else {
				fromHour    = Integer.parseInt(params.fromHour)
				fromMinutes = Integer.parseInt(params.fromMinutes)
				tillHour    = Integer.parseInt(params.tillHour)
				tillMinutes = Integer.parseInt(params.tillMinutes)
			}
			if(noticeType == null) noticeType = 0
		} catch(Exception e) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Neveljaven format parametrov."]])
			return false
		}
		CalendarNotice newNotice = new CalendarNotice(
			ownerID: session.user,
			year: datumYear,
			month: datumMonth,
			day: datumDay,
			content: content,
			startHour: fromHour,
			startMinutes: fromMinutes,
			endHour: tillHour,
			endMinutes: tillMinutes,
			noticeType: noticeType
		);
		if(!newNotice.validate()) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Podani parametri niso veljavni."]])
			println(newNotice.errors)
			return false
		}
		newNotice.save()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Opomba je bila dodana v koledar."]])
		
	}
	
	def removeNotice = {
		doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
		// Set time-region
		System.setProperty('user.timezone', 'Europe/Ljubljana')
		// Calculate
		def noticeID
		try {
			noticeID = Integer.parseInt(params.id)
		} catch(Exception e) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Neveljaven format parametrov."]])
			return false
		}
		def noticeToDelete = CalendarNotice.get(noticeID)
		if(noticeToDelete.ownerID != session.user) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Dostop do izbranega objekta ni dovoljen."]])
			return false
		}
		noticeToDelete.delete()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Opomba je bila odstranjena."]])
		
	}
	
	def showInfoBox = {
		doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
		// Set time-region
		System.setProperty('user.timezone', 'Europe/Ljubljana')
		
		// Get current date
		def cal = new GregorianCalendar()
		cal.time
		
		// Get notices for today
		def notices = [:]
		def c = CalendarNotice.createCriteria()
		def noticesList = c.list {
			and {
				eq("ownerID",session.user)
				eq("year",cal.get(Calendar.YEAR))
				eq("month",cal.get(Calendar.MONTH) + 1)
				eq("day",cal.get(Calendar.DAY_OF_MONTH))
			}
		}
		
		int additionalNotices = 0
		
		// Ali ima uporabnik eŠporta kako opravilo po programu za današnji dan?
		if(session.employeeType == "P" && session.role == "patient") {
			
			// Sestavi današnji timestamp datum
			GregorianCalendar gc = new GregorianCalendar();
			int year  = gc.get(GregorianCalendar.YEAR);
			int month = gc.get(GregorianCalendar.MONTH) + 1;
			int day   = gc.get(GregorianCalendar.DAY_OF_MONTH);
			String today = year + "-" + ((month<10)?("0"+month):(month)) + "-" + ((day<10)?("0"+day):(day)) + "T00"
			
			// Ali smo danes to že preverili
			if(session.sportProgramLastCheck == today) {
				// Danes smo to že preverili
				additionalNotices = session.sportAdditionalNotices
			} else {
				// Preveri za danes
				if(existService.getExerciseData(session.user, today) == null) {
					// Danes ni vadbe po programu
					session.sportAdditionalNotices = 0
					additionalNotices = 0
				} else {
					// Danes je vadba po programu
					session.sportAdditionalNotices = 1
					additionalNotices = 1
				}
				// Zapiši si, da smo danes že preverili
				session.sportProgramLastCheck = today
			}
			
		}
		
		[noticesCount: noticesList.size() + additionalNotices]
		
	}
	
	def addNoticeToPatient = {
		
		println(params.myCalendarWidget_addNoticeToPatient_date_day)
		println(params.myCalendarWidget_addNoticeToPatient_date_month)
		println(params.myCalendarWidget_addNoticeToPatient_date_year)
		println(params.myCalendarWidget_addNoticeToPatient_content)
		println(params.myCalendarWidget_addNoticeToPatient_from_hour)
		println(params.myCalendarWidget_addNoticeToPatient_from_minutes)
		println(params.myCalendarWidget_addNoticeToPatient_till_hour)
		println(params.myCalendarWidget_addNoticeToPatient_till_minutes)
		println(params.myCalendarWidget_addNoticeToPatient_type)

		// Log
		doLogInfo(
			log, "",session.user,session.role,
			"","",session.employeeType
		);
	
		// Nastavi casovni pas
		System.setProperty('user.timezone', 'Europe/Ljubljana')
		
		// Preveri veljavnost datuma in pretvori v int
		if( params.myCalendarWidget_addNoticeToPatient_date_day == null || params.myCalendarWidget_addNoticeToPatient_date_month == null || params.myCalendarWidget_addNoticeToPatient_date_year == null || params.myCalendarWidget_addNoticeToPatient_content == null) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Nekatera obvezna polja niso bila izpolnjena!"]])
			return false
		}
		def datumDay
		def datumMonth
		def datumYear
		def fromHour
		def fromMinutes
		def tillHour
		def tillMinutes
		def content
		def noticeType
		try {
			datumDay = Integer.parseInt(params.myCalendarWidget_addNoticeToPatient_date_day)
			datumMonth = Integer.parseInt(params.myCalendarWidget_addNoticeToPatient_date_month)
			datumYear = Integer.parseInt(params.myCalendarWidget_addNoticeToPatient_date_year)
			content = params.myCalendarWidget_addNoticeToPatient_content
			fromHour = params.myCalendarWidget_addNoticeToPatient_from_hour
			fromMinutes = params.myCalendarWidget_addNoticeToPatient_from_minutes
			tillHour = params.myCalendarWidget_addNoticeToPatient_till_hour
			tillMinutes = params.myCalendarWidget_addNoticeToPatient_till_minutes
			noticeType = Integer.parseInt(params.myCalendarWidget_addNoticeToPatient_type)
			if(fromHour == "no" || fromMinutes == "no" || tillHour == "no" || tillMinutes == "no") {
				fromHour    = 0
				fromMinutes = 0
				tillHour    = 0
				tillMinutes = 0
			} else {
				fromHour    = Integer.parseInt(params.myCalendarWidget_addNoticeToPatient_from_hour)
				fromMinutes = Integer.parseInt(params.myCalendarWidget_addNoticeToPatient_from_minutes)
				tillHour    = Integer.parseInt(params.myCalendarWidget_addNoticeToPatient_till_hour)
				tillMinutes = Integer.parseInt(params.myCalendarWidget_addNoticeToPatient_till_minutes)
			}
			if(noticeType == null) noticeType = 0
		} catch(Exception e) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Neveljaven format parametrov."]])
			return false
		}
		CalendarNotice newNotice = new CalendarNotice(
			ownerID: params.to,
			year: datumYear,
			month: datumMonth,
			day: datumDay,
			content: content,
			startHour: fromHour,
			startMinutes: fromMinutes,
			endHour: tillHour,
			endMinutes: tillMinutes,
			noticeType: noticeType
		);
		if(!newNotice.validate()) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Podani parametri niso veljavni."]])
			println(newNotice.errors)
			return false
		}
		newNotice.save()
		
		// Notify user by mail
		def subjectString = "eOskrba: Novo opravilo je bilo dodano v Vaš koledar "
		def bodyString = "Spoštovani!<br /><br />"
		bodyString += "Oseba " + session.firstName + " " + session.lastName + " Vam je v Vaš koledar dodala naslednje opravilo:<br /><br />"
		bodyString += "[ " + params.myCalendarWidget_addNoticeToPatient_date_day + "." + params.myCalendarWidget_addNoticeToPatient_date_month + "." + params.myCalendarWidget_addNoticeToPatient_date_year + " ] "
		bodyString += content + "<br /><br />"
		bodyString += "Prosimo, prijavite se v aplikacijo eOskrba in preglejte podrobnosti novega opravila.<br /><br />Lep pozdrav,<br />eOskrba"
		
		def newEmailSendRequest = new EmailSendRequest(
			timestamp: new Date(),
			fromId: session.user,
			toId:   params.to,
			title: subjectString,
			content: bodyString,
			sent: false,
			attempt: 0
		)
		newEmailSendRequest.save()
		
		flash.success = ["Hvala!","Opravilo je bilo dodano v pacientov koledar."]
		redirect(controller:"redirects",action:"toMyTasks")
		
	}
	
}
