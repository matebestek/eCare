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


import eoskrba.webapp.common.LogEnabledController
import org.apache.commons.codec.binary.Base64;
import grails.converters.JSON
import si.pint.database.exist.DBManager
import si.pint.database.exist.DBManager.SystemType
import si.pint.eoskrba.model.User
import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM
import si.pint.utils.OpkpUtils;
import org.codehaus.groovy.grails.web.json.JSONElement;
import java.text.MessageFormat;

class MyTasksWidgetController extends LogEnabledController {

	public static final String WEBAPP_RENAME_SPORT_ENTER_VALUES_TASK = "[WEBAPP_RENAME:eSport-enterValues:"
		
	public static final String FIND_TASK_IDS_XQUERY = "let \$d := collection(\"/db/eHujsanje/{0}\")//*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1\"]/*[name()=\"data\" and @archetype_node_id=\"at0001\"]/*[name()=\"items\" and @archetype_node_id=\"at0020\"]/*[name() = \"value\"]/*[name()=\"value\"]/text()[contains(., \"{1}\")] let \$foonode := \$d/../../../../*[name()=\"items\" and @archetype_node_id=\"at0019\"]/*[name() = \"value\"]/*[name()=\"value\"]/text() return \$foonode";
	public static final int ACTIVITI_WAITING_TIME = 500; // TODO: Zmanjšati na čim-manj
	
	// Servisi
	def activitiService
	def existService
	def facebookService
		
    def show = {
		
		// Log
		doLogInfo(
			log, "Pregled lastnih taskov",session.user,session.role,
			"","",session.employeeType
		);
	
		// Pridobi Activiti taske
		def jsonr = activitiService.getUserTasks(session.user, 1000)
		session.myTasksNumber = jsonr.total;
		
		boolean unassignedTicketsExist = false
		List ticketsToAnswer = []
		
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
				if(tickets.size() > 0) {
					unassignedTicketsExist = true
					session.myTasksNumber++
				}
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
					ticketsToAnswer.add(ticket)
					session.myTasksNumber++
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
					ticketsToAnswer.add(ticket)
					session.myTasksNumber++
				}
			}
		
		}
		
		// Preglej Activiti task-e in ugotovi, ali je treba katerega izmed njih preimenovati
		jsonr.data.each { task ->
			
			if( ((String)task.name).startsWith(WEBAPP_RENAME_SPORT_ENTER_VALUES_TASK)) {
				String exerciseDate = ((String)task.name) - WEBAPP_RENAME_SPORT_ENTER_VALUES_TASK
				exerciseDate = exerciseDate.replace("]", "")
				Map exerciseData = existService.getExerciseData(session.user,exerciseDate)
				task.name = "Vnos rezultatov vadbe " + exerciseData.type + " (" + exerciseData.date + ")"
			}
			
		}
		
		[jsonr:jsonr,unassignedTicketsExist:unassignedTicketsExist,ticketsToAnswer:ticketsToAnswer]
		
	}
	
	def form = {
		
		if(params.id) {
			// Get task info
			
			def taskInfo
			try {
				taskInfo = activitiService.getUserTask(params.id)
			} catch(Exception e) {
				forward(controller:"panelWidget",action:"show")
				return false
			}
			if(taskInfo.code == 500) {
				forward(controller:"panelWidget",action:"show")
				return false
			} else if(taskInfo.containsKey("message") && taskInfo.message.contains("Exception")){
				forward(controller:"panelWidget",action:"show")
				return false
			} else if (taskInfo.containsKey("assignee") && taskInfo.assignee != session.user){
				forward(controller:"panelWidget",action:"show")
				return false
			}
			
			def isHujsanjeTask = isHujsanjeStepsProcess(taskInfo);
			
			//če imam opkp integracijo, potem preverim še ali imam za trenutni taskId pripravljen response
			if(taskInfo.description=='TaskIzpolniOPKP'){
				//ustvarim data element
				User patient = UserRegistryFactory.getUserFromLDAP(session.user);
				patient = getWeightAndHeightFromDB(patient, EMPLOYEE_TYPE_ENUM.HUJSANJE);

				
				String urlData = OpkpUtils.prepareOPKPUrlData(patient);
				String result = null;
				try {					
					Object[] xqueryParams = [session.user, params.id + ""] as Object[];
					String xqueryFinal = MessageFormat.format(FIND_TASK_IDS_XQUERY, xqueryParams);
					result = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).getSingleValue(
												   session.user, 
												   session.user, 
												   EMPLOYEE_TYPE_ENUM.HUJSANJE, 
												   xqueryFinal);	
								
				} catch (NullPointerException e) {
					log.info("Ni pacientov!");
					forward(controller:"panelWidget",action:"show")
					return false
				} catch (Exception e) {
					log.error("Napaka: " + e.getMessage(), e);
					forward(controller:"panelWidget",action:"show")
					return false
				}
				
				if(result != null){
					taskInfo.opkpResponse = JSON.parse(result);
					session.showResults = true; 
				}
				
				doLogInfo(log, "Pridobitev forme za task"+params.id,session.user,session.role,
					"","",session.employeeType);
				return [taskInfo:taskInfo,taskId:params.id,data:urlData,isHujsanjeTask:isHujsanjeTask]
								
			}
			
			// Get form
			def raw = activitiService.getFormStatus(params.id)
			if(raw != 404) {
				raw = activitiService.getForm(params.id)
			}
			
			// Pripravi posebne spremenljivke
			def shouldFillWithData = false
			def dataToFillFormWith = ""
			
			// Preglej, če je treba task preimenovati in mu nastaviti nekatere vrednosti
			def taskOriginalName = ((String)taskInfo.name)
			if( ((String)taskInfo.name).startsWith(WEBAPP_RENAME_SPORT_ENTER_VALUES_TASK)) {
				
				String exerciseDate = ((String)taskInfo.name) - WEBAPP_RENAME_SPORT_ENTER_VALUES_TASK
				exerciseDate = exerciseDate.replace("]", "")
				Map exerciseData = existService.getExerciseData(session.user,exerciseDate)
				taskInfo.name = "Vnos rezultatov vadbe " + exerciseData.type + " (" + exerciseData.date + ")"
				
				// Prevedi iz domene programa v domeno obrazca
				exerciseData.intensityCode = existService.domainTranslate(
					existService.DOMAIN_SPORT_CALENDAR_EXERCISE_INTENSITY,
					existService.DOMAIN_SPORT_INPUT_EXERCISE_INTENSITY,
					exerciseData.intensityCode
				)
				exerciseData.typeCode = existService.domainTranslate(
					existService.DOMAIN_SPORT_CALENDAR_EXERCISE_TYPE,
					existService.DOMAIN_SPORT_INPUT_EXERCISE_TYPE,
					exerciseData.typeCode
				)
				
				shouldFillWithData = true
				dataToFillFormWith = "["
				dataToFillFormWith += "['?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0019]?value','${exerciseData.date}'],"
				dataToFillFormWith += "['?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value','${exerciseData.typeCode}'],"
				dataToFillFormWith += "['?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0006]?value','${exerciseData.duration}'],"
				dataToFillFormWith += "['?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.intenzivnost!opisno!5st!eo.v1]?items[at0001]?value','${exerciseData.intensityCode}']"
				dataToFillFormWith += "]"
				
			}
			
			doLogInfo(log, "Pridobitev forme za task"+params.id,session.user,session.role,
				"","",session.employeeType);
			
			return [raw:raw,taskInfo:taskInfo,taskId:params.id,taskOriginalName:taskOriginalName,isHujsanjeTask:isHujsanjeTask,shouldFillWithData:shouldFillWithData,dataToFillFormWith:dataToFillFormWith]
			
		} else {
			doLogInfo(log, "Obvestilo o potrebni izbiri naloge",session.user,session.role,"","",session.employeeType);
			render("<center><p>Prosimo, najprej izberite nalogo, pri kateri želite izpolniti obrazec ali jo potrditi.</p></center>");
		}
		
	}
	
	public static final String FB_VNOS_DATUM = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0019]?value"
	public static final String FB_VNOS_TRAJANJE = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0006]?value"
	public static final String FB_VNOS_PANOGA = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value"
	public static final String FB_VNOS_INTENZIVNOST_3ST = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.intenzivnost!opisno!3st!eo.v1]?items[at0001]?value"
	public static final String FB_VNOS_INTENZIVNOST_5ST = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.intenzivnost!opisno!5st!eo.v1]?items[at0001]?value"
	public static final String FB_VNOS_KOMENTAR = "?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0018]?value"
	
	def completeWithForm = {
		
		// Nastavi spremenljivke
		def paramsToRedirect = [:]
		
		// Objavi na Facebook, če je treba
		if(params["myTasks_form_fbpublish"]) {
			
			// Preberi vnešene podatke
			String form_date = params[FB_VNOS_DATUM]
			String form_time = params[FB_VNOS_TRAJANJE]
			String form_type = params[FB_VNOS_PANOGA]
			String form_inte = params[FB_VNOS_INTENZIVNOST_5ST]
			String form_come = params[FB_VNOS_KOMENTAR]
			
			if(form_date.isEmpty()) form_date = "pravkar"
			if(form_time.isEmpty()) form_time = "nedoločeno"
			String form_type_nice = existService.domainTranslate(existService.DOMAIN_SPORT_INPUT_EXERCISE_TYPE, existService.DOMAIN_SPORT_DESC_EXERCISE_TYPE, form_type)
			form_inte = existService.domainTranslate(existService.DOMAIN_SPORT_CALENDAR_EXERCISE_INTENSITY, existService.DOMAIN_SPORT_INPUT_EXERCISE_INTENSITY, form_inte)
			if(form_inte == "at0002") form_inte = "zelo nizka"
			if(form_inte == "at0003") form_inte = "nizka"
			if(form_inte == "at0004") form_inte = "zmerna"
			if(form_inte == "at0005") form_inte = "visoka"
			if(form_inte == "at0006") form_inte = "zelo visoka"
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
		
		// Potrdi task (oddaj izpolnjene podatke)
		HashMap dataToSubmit = activitiService.getParameters(request)
		dataToSubmit.usernameApp = session.user
		def jsonr = activitiService.completeTask(params.id, dataToSubmit)
		
		// Počakaj nekaj časa, da smo lahko prepričani, da je Activiti že ustvaril
		// nov task, če je potreben
		Thread.currentThread().sleep(ACTIVITI_WAITING_TIME)
		
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
		
		// Pripravi odgovor o uspehu oddajanja obrazca
		session.showResults = false;
		if(jsonr.success) {
			doLogInfo(
				log, "Obrazec uspešno oddan:"+params.id,session.user,session.role,
				"", "", session.employeeType
			);
			flash.success = [
				"Hvala!",
				"Obrazec je bil uspešno oddan in naloga je bila zaključena!"
			]
		} else {
			doLogInfo(
				log, "Napaka pri oddaji obrazca:"+params.id,session.user,session.role,
				"","",session.employeeType
			);
			flash.error = [
				"Napaka!",
				"Obrazec ni bil oddan! Preverite, ali so bila izpolnjena vsa obvezna polja."
			]
			paramsToRedirect["pw"] = "panelWidget"
			paramsToRedirect["pa"] = "show"
		}
		
		// Če je bil obrazec uspešno oddan, ukrepaj glede na to, ali obstaja kak nov task
		if(jsonr.success) {
			
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
			
		}
		
		// Zahtevaj osvežitev info-box-a
		session.updatePatientProgress = true
		
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
	
	// Special JSON action
	// calculates user's Activiti user tasks, stores number in a session variable and returns
	// a JSON object containing the new task number and a boolean stating if number has changed
	def updateMyTasksNumberJSON = {
		
		def oldMyTasksNumber = session.myTasksNumber
		def newMyTasksNumber = activitiService.getUserTasksNumber(session)
		
		def changed = false
		def risen = false
		
		if(oldMyTasksNumber < newMyTasksNumber) {
			risen = true
		}
		if(oldMyTasksNumber != newMyTasksNumber) {
			changed = true
			session.myTasksNumber = newMyTasksNumber
		}
		if(risen || changed){
			doLogInfo(log, "Update stevila nalog:"+newMyTasksNumber+" risen="+risen+" changed="+changed,session.user,session.role,"","",session.employeeType);
		}
		
		render(view:"updateMyTasksNumberJSON",contentType:"text/json",model:[risen:risen,changed:changed,number:newMyTasksNumber])
	}
	
	
	//path to weight & height in eXist XML (ASTMA)
	public static final String WEIGHT_EXIST_PATH_ASTMA = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	public static final String HEIGHT_EXIST_PATH_ASTMA = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";

	//template name for first visit (ASTMA)
	public static final String FIRST_ENCOUNTER_TEMPLATE_ASTMA = "openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1";
	
	//template name for first visit (DIABETES)
	public static final String FIRST_ENCOUNTER_TEMPLATE_DIABETES = "openEHR-EHR-SECTION.vkljucitev_eo.v1";

	//path to weight & height in eXist XML (DIABETES)
	public static final String WEIGHT_EXIST_PATH_DIABETES = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	public static final String HEIGHT_EXIST_PATH_DIABETES = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	
	//template name for first visit (SHIZOFRENIJA)
	public static final String FIRST_ENCOUNTER_TEMPLATE_SHIZOFRENIJA = "openEHR-EHR-SECTION.vkljucitev_eo.v1";
	
	//path to weight & height in eXist XML (SHIZOFRENIJA)
	public static final String WEIGHT_EXIST_PATH_SHIZOFRENIJA = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	public static final String HEIGHT_EXIST_PATH_SHIZOFRENIJA = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";

	//template name for first visit (DIABETES)
	public static final String FIRST_ENCOUNTER_TEMPLATE_HUJSANJE = "openEHR-EHR-SECTION.vkljucitev_eo.v1";
	
	//path to weight & height in eXist XML (HUJSANJE)
	public static final String WEIGHT_EXIST_PATH_HUJSANJE = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	public static final String HEIGHT_EXIST_PATH_HUJSANJE = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	
	
	/**
	* Looks for last entry of template 'VkljucitevVStudijo'.
	*
	* @param u
	* @return
	*/
   public static User getWeightAndHeightFromDB(User u, EMPLOYEE_TYPE_ENUM employeeType) {
	   try {
		   String weightString = null;
		   String heightString = null;
		   
		   if (employeeType.equals(EMPLOYEE_TYPE_ENUM.ASTMA)) {
			   weightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_ASTMA, WEIGHT_EXIST_PATH_ASTMA);
			   heightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_ASTMA, HEIGHT_EXIST_PATH_ASTMA);
		   }
		   else if (employeeType.equals(EMPLOYEE_TYPE_ENUM.DIABETES)) {
			   weightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_DIABETES, WEIGHT_EXIST_PATH_DIABETES);
			   heightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_DIABETES, HEIGHT_EXIST_PATH_DIABETES);
		   }
		   else if (employeeType.equals(EMPLOYEE_TYPE_ENUM.SHIZOFRENIJA)){
			   weightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_SHIZOFRENIJA, WEIGHT_EXIST_PATH_SHIZOFRENIJA);
			   heightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_SHIZOFRENIJA, HEIGHT_EXIST_PATH_SHIZOFRENIJA);
		   } else if (employeeType.equals(EMPLOYEE_TYPE_ENUM.HUJSANJE)){
			   weightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_HUJSANJE, WEIGHT_EXIST_PATH_HUJSANJE);
			   heightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_HUJSANJE, HEIGHT_EXIST_PATH_HUJSANJE);
		   }

		   
		   if (weightString == null || heightString == null) {
			   throw new NullPointerException("Za uporabnika " + u.getUsername() + " manjkajo podatki o tezi ali visini!");
		   }
		   u.setWeight(new BigDecimal(weightString));
		   u.setHeight(new BigDecimal(heightString));
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	   return u;
   }
   
   /**
    * Checks if given task is part of 'Hujsanje' steps 1-16.
    * 
    * @param taskInfo
    * @return
    */
   private static boolean isHujsanjeStepsProcess(JSONElement taskInfo) {
	   def tasksHujsanjeArray = ["TaskPreberiDokumentacijo", 
		                         "TaskIzpolniOPKP", 
								 "TaskVnosMeritev", 
								 "TaskContent", 
								 "TaskWorkingSheets", 
								 "TaskQuestioneer", 
								 "TaskQuestioneerNotCorrect",
								 "TaskKeepUp"]
	   
	   if (taskInfo == null || taskInfo.description == null)
	       return false;
		   
	   if (taskInfo.description in tasksHujsanjeArray)
	   	   return true;
			  
	   return false;
   }
   
   public static String getJSON(User patient){
	   //vzamem najvišjo vrednost med vsemi različnimi vrednostmi - ponavadi bo itak vedno ista vrednost TODO: popravi da bo vzel zadnjo meritev
	   String query = "max(collection(\"/db/eHujsanje/" + patient.getUsername() + "\")//*[name()=\"content\" and @archetype_node_id=\"openEHR-EHR-SECTION.vkljucitev_eo.v1\"]/*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-OBSERVATION.telesna_sestava.v1\"]/*[name()=\"data\" and @archetype_node_id=\"at0001\"]/*[name()=\"events\" and @archetype_node_id=\"at0002\"]/*[name()= \"data\" and @archetype_node_id=\"at0003\"]/*[name()=\"items\" and @archetype_node_id=\"at0007\"]/*[name() = \"value\"]/*[name()=\"magnitude\"]/text())";//bazalni metabolizem
	   String bm = null;
	   try {
		   bm = (String)DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).getSingleValue(patient.getUsername(),patient.getUsername(),EMPLOYEE_TYPE_ENUM.DIABETES,query);
	   } catch (Exception e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		   //log.error("Napaka pri pridobivanju bazalnega metabolizma za pacienta:"+patient.getUsername() +": " + e.getMessage());
	   }
	   
	   
	   String json =
	   "{\"ID\":\"" + patient.getUsername()+ "\"," +
	   "\"EMAIL\":\" "+ patient.getEmail() +" \"," +
	   "\"FIRST_NAME\":\" " + patient.getFirstNameLastName() + "\"," +
	   "\"LAST_NAME\":\" " + patient.getFirstNameLastName() + "\"," +
	   "\"SEX\":\"" +patient.getSex().toString() + "\"," +
	   "\"BDATE\":\"" + patient.getDateOfBirth() + "\"," +
	   "\"HEIGHT\":\"" +patient.getHeight() + "\"," +
	   "\"WEIGHT\":\"" + patient.getWeight() + "\"," +
	   "\"BM\":\"" + bm + "\"" +
	   "}";
	   
	   return json;
   }
	
}
