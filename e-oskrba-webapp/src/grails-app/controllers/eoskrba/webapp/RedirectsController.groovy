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

class RedirectsController extends LogEnabledController{

//	Logger log4j = Logger.getLogger(RedirectsController.class);
	
    def toMyTasks = {
		flash.afterLogin  = flash.afterLogin
		flash.success  = flash.success
		flash.error    = flash.error
		flash.warrning = flash.warning
		doLogInfo(log, "Na moje naloge",session.user,session.role,"","",session.employeeType);
		if(session.role == 'patient') {
			redirect(
				controller: "patientTabs",
				action: "asthmaAndMe",
				params: [
					w: "myTasksWidget_show"
				]
			)
		} else {
			redirect(
				controller: session.role + "Tabs",
				action: "tasks",
				params: [
					w: "myTasksWidget_show"
				]
			)
		}
	}
	
	def toReportingGeneric = {
		
		flash.success  = flash.success
		flash.error    = flash.error
		flash.warrning = flash.warning
		doLogInfo(log, "Na poročilo o KS",session.user,session.role,"","",session.employeeType);
		// get today
		def cal = new GregorianCalendar()
		cal.time
		def month = cal.get(Calendar.MONTH) + 1
		def year = cal.get(Calendar.YEAR)
		def pp = URLEncoder.encode(
			"what="      + params.id + "+" +
			"fromMonth=" + month + "+" +
			"fromYear="  + year  + "+" +
			"tillMonth=" + month + "+" +
			"tillYear="  + year
		)
		if(session.role == 'patient') {
			redirect(
				controller: "patientTabs",
				action: "asthmaAndMe",
				params: [
					w: "reportingWidget_patientPicker",
					pw: "reportingWidget",
					pa: "patientShow",
					pp: pp
				]
			)
		} else {
			redirect(
				controller: "patientTabs",
				action: "reports",
				params: [
					w: "reportingWidget_patientPicker",
					pw: "reportingWidget",
					pa: "patientShow",
					pp: pp
				]
			)
		}
		
	}
	
}
