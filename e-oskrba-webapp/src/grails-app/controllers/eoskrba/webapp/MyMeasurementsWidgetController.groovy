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
import si.eoskrba.xmlbeans.MedsXmlReader;
import eoskrba.webapp.common.LogEnabledController

	

class MyMeasurementsWidgetController extends LogEnabledController {

//	Logger log4j = Logger.getLogger(MyMeasurementsWidgetController.class);
	
	public static final String DIABETES_PROCES_VNOS_VREDNOST_ID = "Diabetes-Proces-Vnos-Vrednosti"
	public static final String HUJSANJE_PROCES_VNOS_VREDNOST_ID = "Hujsanje-Proces-Vnos-Vrednosti"
	public static final String SPORT_PROCES_VNOS_VREDNOST_ID = "Sport-Proces-Vnos-Vrednosti"
	
	public static final String FB_VNOS_DATUM = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0019]?value"
	public static final String FB_VNOS_TRAJANJE = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0006]?value"
	public static final String FB_VNOS_PANOGA = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value"
	public static final String FB_VNOS_INTENZIVNOST_3ST = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.intenzivnost!opisno!3st!eo.v1]?items[at0001]?value"
	public static final String FB_VNOS_INTENZIVNOST_5ST = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.intenzivnost!opisno!5st!eo.v1]?items[at0001]?value"
	public static final String FB_VNOS_KOMENTAR = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0018]?value"
	
	def activitiService
	def facebookService
	def existService
	
    def show = {
		doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
	}
	
	def showInputForm = {
		doLogInfo(log, "",session.user,session.role,"","",session.employeeType);
		
		def processId 
		if (session.employeeType == "D")
			processId = DIABETES_PROCES_VNOS_VREDNOST_ID
		else if (session.employeeType == "H")
			processId = HUJSANJE_PROCES_VNOS_VREDNOST_ID
		else if (session.employeeType == "P")
			processId = SPORT_PROCES_VNOS_VREDNOST_ID
			
		def form = activitiService.getProcesDefinitionForm(processId)
		
		
		//map form parameter
		[form:form]	
	}
	
	def showInputFormDo = {
		
		doLogInfo(log, "",session.user,session.role,"","",session.employeeType);
		
		// Nastavi spremenljivke
		def paramsToRedirect = [:]
		
		// Objavi na Facebook, če je treba
		if(params["myMeasurements_showInputForm_fbpublish"]) {
			
			// Preberi vnešene podatke
			String form_date = params[FB_VNOS_DATUM]
			String form_time = params[FB_VNOS_TRAJANJE]
			String form_type = params[FB_VNOS_PANOGA]
			String form_inte = "neznana"
			if(session.employeeType == "H") form_inte = params[FB_VNOS_INTENZIVNOST_3ST]
			if(session.employeeType == "P") form_inte = params[FB_VNOS_INTENZIVNOST_5ST]
			String form_come = params[FB_VNOS_KOMENTAR]
			
			if(form_date.isEmpty()) form_date = "pravkar"
			if(form_time.isEmpty()) form_time = "nedoločeno"
			String form_type_nice = existService.domainTranslate(existService.DOMAIN_SPORT_INPUT_EXERCISE_TYPE, existService.DOMAIN_SPORT_DESC_EXERCISE_TYPE, form_type)
			form_inte = fb_activityIntensityCodeToString(form_inte, ((session.employeeType=="P")?5:3) )
			if(form_come.isEmpty()) form_come = ""
			
			// Najdi uporabnika
			FacebookUser fu = FacebookUser.findByUserId(session.user)
			
			// Prapravi facebook mapping objekt in ga shrani
			FacebookMapping fbm = new FacebookMapping(
				facebookUserId: fu["me_id"],
				date: form_date,
				time: form_time,
				type: form_type,
				typen: form_type_nice,
				inte: form_inte,
				come: form_come
			)
			fbm.save(flush:true)
			
			// Objavi na Facebook
			facebookService.publishToWall(
				fu.me_id,
				form_come,
				grailsApplication.config.grails.serverURL + "/facebookResources/activityDetails/" + fbm.id,
				fu.me_name + ": " + form_type_nice,
				"eOskba: Opravljena vadba",
				fu.me_name + " je " + form_date + " opravil/a " + form_time + " minut dolgo vadbo športne panoge " + form_type_nice
			)
				
		}
		
		// Pridobi seznam taskov, ki so dodeljeni uporabniku
		def tasksBeforeSubmit = activitiService.getUserTasks(session.user,1000)
		def tasksIdsBeforeSubmit = []
		tasksBeforeSubmit.data.each { task ->
			tasksIdsBeforeSubmit.add(task.id as int)
		}
		
		// Oddaj activiti obrazec
		def processId
		if (session.employeeType == "D")
			processId = DIABETES_PROCES_VNOS_VREDNOST_ID
		else if (session.employeeType == "H")
			processId = HUJSANJE_PROCES_VNOS_VREDNOST_ID
		else if (session.employeeType == "P")
			processId = SPORT_PROCES_VNOS_VREDNOST_ID
			
		HashMap dataToSubmit = activitiService.getParameters(request)
		dataToSubmit.usernameApp = session.user
		
		activitiService.startProces(processId, dataToSubmit)
		
		// Počakaj nekaj časa, da smo ahko prepričani, da je Activiti že ustvaril
		// nov task, če je potreben
		Thread.currentThread().sleep(MyTasksWidgetController.ACTIVITI_WAITING_TIME)
		
		// Zopet pridobi seznam taskov, ki so dodeljeni uporabniku
		def tasksAfterSubmit = activitiService.getUserTasks(session.user,1000)
		
		// Najdi razliko med seznamoma
		def newTasks = []
		tasksAfterSubmit.data.each { task ->
			def taskId = task.id as int
			if( !tasksIdsBeforeSubmit.contains(taskId) ) {
				newTasks.add(task)
			}
		}
		
		// Če obstaja natanko 1 nov task, potem uporabnika takoj preusmeri k njemu
		// Če jih obstaja več, potem uporabnika preusmeri k prvemu
		if(newTasks.size() >= 1) {
			paramsToRedirect["pw"] = "myTasksWidget"
			paramsToRedirect["pa"] = "form"
			paramsToRedirect["pp"] = "id" + "%3D" + newTasks[0].id + "%2B" + "showNoticeNewTask" + "%3D" + "1"
			flash.success = [
				"Hvala!",
				"Prosimo nadaljujte z reševanjem spodnje naloge."
			]
		}
		
		// Če novih taskov ni, ukrepaj
		else {
			
			// Če obstaja kak odprt task, preusmeri uporabnika k njemu in
			// ga opozori, da je ta task še ostal od prej
			if(tasksAfterSubmit.total > 0) {
				paramsToRedirect["pw"] = "myTasksWidget"
				paramsToRedirect["pa"] = "form"
				paramsToRedirect["pp"] = "id" + "%3D" + tasksAfterSubmit.data[0].id + "%2B" + "showNoticeOldTask" + "%3D" + "1"
				flash.success = [
					"Hvala!",
					"Prosimo nadaljujte z reševanjem spodnje naloge."
				]
			}
			
		}
		
		// Preusmeri uporabnika
		def redirectTo = request.getCookie("eOskrba_layout_currentTab")
		redirectTo = redirectTo.split("_")
		redirect(
			controller:redirectTo[0],
			action:redirectTo[1],
			params:paramsToRedirect
		)
		return false
		
	}
	
	def entryFinished = {

	}
	
	static String fb_activityIntensityCodeToString(String activityIntensityCode, int levels = 3) {
		if(levels == 3) {
			if(activityIntensityCode == "at0002") return "nizka"
			if(activityIntensityCode == "at0003") return "srednja"
			if(activityIntensityCode == "at0004") return "visoka"
		}
		else if(levels == 5) {
			if(activityIntensityCode == "at0002") return "zelo nizka"
			if(activityIntensityCode == "at0003") return "nizka"
			if(activityIntensityCode == "at0004") return "zmerna"
			if(activityIntensityCode == "at0005") return "visoka"
			if(activityIntensityCode == "at0006") return "zelo visoka"
		}
		return "neznana"
	}

}
