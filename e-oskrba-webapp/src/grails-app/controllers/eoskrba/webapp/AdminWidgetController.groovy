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

//import org.apache.commons.logging.*;
//import org.apache.log4j.Logger;

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import eoskrba.webapp.RestService.RestClient;
import eoskrba.webapp.common.LogEnabledController;
import si.eoskrba.xmlbeans.MedsXmlReader;

class AdminWidgetController extends LogEnabledController{

	
	
//	Logger log4j = Logger.getLogger(AdminWidgetController.class);
	
	//insert medicine list (diabetes)
	public static final String MEDICINE_1_ELEMENT_DIABETES = "<select name=\"adminWidget_regFormMeds_gen\" id=\"adminWidget_regFormMeds_gen\">"
	//insert staff list (hujsanje)
	public static final String COORDINATOR_1_ELEMENT_HUJSANJE = "<select name=\"assignedCoordinator\" class=\"activiti-size-short activiti-validation-req\">";
		
	def activitiService
	def existService
	def nsLookupService

	def asthmaRegForm = {
		
		doLogInfo(log, "Pridobivam formo za vnos novega pacienta ",session.user,session.role,
			"","",session.employeeType);
		

		def form = activitiService.getProcesDefinitionForm(returnProcessId(session.employeeType))

		
		//insert medicine list in form (from eXist)
		if (session.employeeType == EMPLOYEE_TYPE_ENUM.DIABETES.eval()) {
			def htmlSegments = [MEDICINE_1_ELEMENT_DIABETES]
			form = MedsXmlReader.insertDropdown(session.user,session.user,EMPLOYEE_TYPE_ENUM.eval(session.employeeType), form, htmlSegments);
		}
		//insert list of caremanagers in dropdown (from LDAP)
		else if (session.employeeType == EMPLOYEE_TYPE_ENUM.HUJSANJE.eval()) {
			//rabiš samo paciente, ki pripadajo eni od kliničnih študij
			List staff = MainLdapSchema.findAll(directory: "mainLdap",base: "ou=people") {
				and {
					eq "employeeType", session.employeeType				
					or {
						eq "memberAtt", "doctor"
						eq "memberAtt", "caremanager"
					}
				}
			}
			
			String firstPart = form.substring(0, form.indexOf(COORDINATOR_1_ELEMENT_HUJSANJE) + COORDINATOR_1_ELEMENT_HUJSANJE.length());
			String secondPart = form.substring(form.indexOf(COORDINATOR_1_ELEMENT_HUJSANJE) + COORDINATOR_1_ELEMENT_HUJSANJE.length());
			
			for (person in staff) {			
				firstPart = firstPart + "<option value=\"" + person.uidatt + "\">" + person.uidatt + "</option>";		
			}			
			form = firstPart + secondPart; 	 	
		}
		List formData = []

		// Prednastavi zdravniško ustanovo
		if(session.employeeType == "A") formData << [name:"?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0083]?value",val:session.ldap.healthcareInstitutionAtt]
		else formData << [name:"?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0009]?value",val:session.ldap.healthcareInstitutionAtt]

		[form:form,formData:formData, params:params]
		
	}
	
	def startProcessWithForm = {
		
		// Nastavi spremenljivke
		def paramsToRedirect = [:]
		
		// Pridobi seznam taskov, ki so dodeljeni uporabniku
		def tasksBeforeSubmit = activitiService.getUserTasks(session.user,1000)
		def tasksIdsBeforeSubmit = []
		tasksBeforeSubmit.data.each { task ->
			tasksIdsBeforeSubmit.add(task.id as int)
		}
		
		// Startaj activiti proces
		def jsonr = activitiService.startProces(params.processId, params.dataToSubmit)
		
		// Počakaj nekaj časa, da smo lahko prepričani, da je Activiti že ustvaril
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
		
		// Pripravi odgovor o uspehu
		if(jsonr) {
			flash.success = params.msgOnSucces
		} else {
			flash.error = params.msgOnError
		}
			
		// Če obstaja natanko 1 nov task, potem uporabnika takoj preusmeri k njemu
		// Če jih obstaja več, potem uporabnika preusmeri k prvemu
		if(newTasks.size() >= 1) {
			paramsToRedirect["pw"] = "myTasksWidget"
			paramsToRedirect["pa"] = "form"
			paramsToRedirect["pp"] = "id" + "%3D" + newTasks[0].id + "%2B" + "showNoticeNewTask" + "%3D" + "1"
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

	def asthmaRegFormDo = {
		
		// Log
		doLogInfo(
			log, "Startam proces za vnos pacienta",session.user,session.role,
			"","",session.employeeType
		);
		
		// Pripravi podatke
		HashMap dataToSubmit = activitiService.getParameters(request)
		dataToSubmit.usernameApp = session.user
		params.dataToSubmit = dataToSubmit
		params.processId = returnProcessId(session.employeeType)
		params.msgOnSucces = ["Hvala!","Obrazec je bil uspešno oddan!"]
		params.msgOnError  = ["Obrazec ni bil oddan","Pri obdelavi obrazca je prišlo do napake"]
		
		// Oddaj zadolženi akciji
		forward(
			controller: "adminWidget",
			action: "startProcessWithForm",
			params: params
		)
		
	}

	def newRegButton = { }

	def startPeriodicButton = { }

	def periodicForm = {
		doLogInfo(log, "Pridobivam formo za periodicno prozenje",session.user,session.role,
			"","",session.employeeType);
		def form = activitiService.getProcesDefinitionForm(
				"ProcesPeriodicnoProzenje"
				)

		[form:form]
		
	}

	def periodicFormDo = {
		
		// Log
		doLogInfo(
			log, "Startam proces za periodicno prozenje",session.user,session.role,
			"","",session.employeeType
		);

		// Pripravi podatke
		HashMap dataToSubmit = activitiService.getParameters(request)
		dataToSubmit.usernameApp = session.user
		params.dataToSubmit = dataToSubmit
		params.processId = "ProcesPeriodicnoProzenje"
		params.msgOnSucces = ["Hvala!","Obrazec je bil uspešno oddan!"]
		params.msgOnError  = ["Obrazec ni bil oddan","Pri obdelavi obrazca je prišlo do napake"]
		
		// Oddaj zadolženi akciji
		forward(
			controller: "adminWidget",
			action: "startProcessWithForm",
			params: params
		)
		
	}

	def incompleteForms = { }

	def checkIfEmailAvailableJSON = {

		String uri = request.forwardUri;
		
		doLogInfo(log, "Preverjam obstoj emailov",session.user,session.role,
			"","",session.employeeType);
		
		// Get everybody from eXist
		def users = existService.getAllEmails(session.user,session.user);
		
		// Get everybody from ldap
		def usersLdap = MainLdapSchema.findAll(
			directory: "mainLdap",
			base: "ou=people",
			filter: "(uidatt=*)"
		)
		usersLdap.each {
			users.add(it.eMailAtt.toString())
		}

		def available = true
		def reason = "OK"
		users.each {
			if(it == params.email) {
				available = false
				reason = "Vnešeni e-poštni naslov je že v uporabi! Prosimo, izberite drugega."
			}
		}
		
		if(available) {
			// Preveri še, ali poštni strežnik sploh obstaja
			def hostName = (params.email.split("@"))
			if(hostName.size() < 2) {
				available = false;
				reason = "Vnešeni e-poštni naslov ni veljavne oblike."
			} else {
			hostName = hostName[1]
				def nMailServers = nsLookupService.mxLookup(hostName)
				if(nMailServers == 0) {
					available = false;
					reason = "Na vnešenem e-poštnem naslovu ni bilo najdenih poštnih strežnikov."
				}
			}
		}
		
		render(view:"checkIfEmailAvailableJSON",contentType:"text/json",model:[available:available,reason:reason])
		
	}
	
	def checkIfEmailServerExistsJSON = {
		
		def available = true
		def reason = "OK"
		def hostName = (params.email.split("@"))
		if(hostName.size() < 2) {
			available = false;
			reason = "Vnešeni e-poštni naslov ni veljavne oblike."
		} else {
			hostName = hostName[1]
			def nMailServers = nsLookupService.mxLookup(hostName)
			if(nMailServers == 0) {
				available = false;
				reason = "Na vnešenem e-poštnem naslovu ni bilo najdenih poštnih strežnikov."
			}
		}
		
		render(view:"checkIfEmailAvailableJSON",contentType:"text/json",model:[available:available,reason:reason])
		
	}
	
	def asthmaRegFormDone = { }
	
	def editPatientTab = { }
	
	def asthmaEditForm = {
		
		doLogInfo(log, "Pridobivam formo za ogled in popravek podatkov pacienta ",session.user,session.role,
			"","",session.employeeType);

		def form = activitiService.getProcesDefinitionForm(returnProcessIdEditPatient(session.employeeType))
		
		// get form data
		Map xmlData = existService.getParsedXML(
			session.user,
			EMPLOYEE_TYPE_ENUM.eval(session.employeeType).toDomainName(),
			params.uid,
			returnProcessTemplateId(session.employeeType)
		);
		
		List formData = xmlData.pairs
		List medsData = xmlData.meds
	
		//insert medicine list in form (from eXist)
		if (session.employeeType == EMPLOYEE_TYPE_ENUM.DIABETES.eval()) {
			def htmlSegments = [MEDICINE_1_ELEMENT_DIABETES]
			form = MedsXmlReader.insertDropdown(session.user,params.uid,EMPLOYEE_TYPE_ENUM.eval(session.employeeType), form, htmlSegments);
		}
		//insert list of caremanagers in dropdown (from LDAP)
		else if (session.employeeType == EMPLOYEE_TYPE_ENUM.HUJSANJE.eval()) {
			
			MainLdapSchema match = MainLdapSchema.find(
			    directory: "mainLdap",
			    base: "ou=people",
			    filter: "(uidatt=" + params.uid + ")"
			)
			formData << [name:"assignedCoordinator",val:match.patientsDoctorAtt]
			
			List staff = MainLdapSchema.findAll(directory: "mainLdap",base: "ou=people") {
				and {
					eq "employeeType", session.employeeType
					or {
						eq "memberAtt", "doctor"
						eq "memberAtt", "caremanager"
					}
				}
			}
			
			String firstPart = form.substring(0, form.indexOf(COORDINATOR_1_ELEMENT_HUJSANJE) + COORDINATOR_1_ELEMENT_HUJSANJE.length());
			String secondPart = form.substring(form.indexOf(COORDINATOR_1_ELEMENT_HUJSANJE) + COORDINATOR_1_ELEMENT_HUJSANJE.length());
			
			for (person in staff) {
				firstPart = firstPart + "<option value=\"" + person.uidatt + "\">" + person.uidatt + "</option>";
			}
			form = firstPart + secondPart;
		}
		
		formData << [name:"?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0009]?value",val:session.ldap.healthcareInstitutionAtt]
		
		[form:form,formData:formData,medsData: medsData]
		
	}
	
	def asthmaEditFormDo = {
		
		// Log
		doLogInfo(
			log, "Posodabljanje podatkov za pacienta",session.user,session.role,
			"","",session.employeeType
		);
		
		// Pripravi podatke
		HashMap dataToSubmit = activitiService.getParameters(request)
		dataToSubmit.usernameApp = session.user
		params.dataToSubmit = dataToSubmit
		params.processId = returnProcessIdEditPatient(session.employeeType)
		params.msgOnSucces = ["Hvala!","Podatki so bili uspešno posodobljeni!"]
		params.msgOnError  = ["Podatki niso bili posodobljeni","Pri obdelavi obrazca je prišlo do napake"]
		
		// Oddaj zadolženi akciji
		forward(
			controller: "adminWidget",
			action: "startProcessWithForm",
			params: params
		)
		
	}
	
	def newVisitTab = { }
	/**
	 * Nov obisk pacienta
	 * */
	def newVisitForm = {
		
		doLogInfo(log, "Nov obisk pacienta ",session.user,session.role,
			"","",session.employeeType);

		//vzamem kar formo iz Edit, kjer so nekatera polja samo predizpolnjena in jih ni mogoče spreminjati. Vsi podatki se tudi napolnijo iz zadnjega veljavnega xml-a
		def form = activitiService.getProcesDefinitionForm(returnProcessIdEditPatient(session.employeeType))
			
		// get form data
		Map xmlData = existService.getParsedXML(session.user,
			EMPLOYEE_TYPE_ENUM.eval(session.employeeType).toDomainName(),
			params.uid,
			returnProcessTemplateId(session.employeeType)
		);
		
		List formData = xmlData.pairs
		List medsData = xmlData.meds
	
		//insert medicine list in form (from eXist)
		if (session.employeeType == EMPLOYEE_TYPE_ENUM.DIABETES.eval()) {
			def htmlSegments = [MEDICINE_1_ELEMENT_DIABETES]
			form = MedsXmlReader.insertDropdown(session.user,params.uid,EMPLOYEE_TYPE_ENUM.eval(session.employeeType), form, htmlSegments);
		} else if(session.employeeType == EMPLOYEE_TYPE_ENUM.HUJSANJE.eval()) {
			// eHujsanje - koordinatorji
			MainLdapSchema match = MainLdapSchema.find(
			    directory: "mainLdap",
			    base: "ou=people",
			    filter: "(uidatt=" + params.uid + ")"
			)
			formData << [name:"assignedCoordinator",val:match.patientsDoctorAtt]
			List staff = MainLdapSchema.findAll(directory: "mainLdap",base: "ou=people") {
				and {
					eq "employeeType", session.employeeType
					or {
						eq "memberAtt", "doctor"
						eq "memberAtt", "caremanager"
					}
				}
			}
			String firstPart = form.substring(0, form.indexOf(COORDINATOR_1_ELEMENT_HUJSANJE) + COORDINATOR_1_ELEMENT_HUJSANJE.length());
			String secondPart = form.substring(form.indexOf(COORDINATOR_1_ELEMENT_HUJSANJE) + COORDINATOR_1_ELEMENT_HUJSANJE.length());
			
			for (person in staff) {
				firstPart = firstPart + "<option value=\"" + person.uidatt + "\">" + person.uidatt + "</option>";
			}
			form = firstPart + secondPart;
		}
		
		formData << [name:"?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0009]?value",val:session.ldap.healthcareInstitutionAtt]
		
		[form:form,formData:formData,medsData: medsData]
		
	}
	
	def newVisitFormDo = {
		
		// Log
		doLogInfo(
			log, "Dodajanje novega obiska pacienta",session.user,session.role,
			"","",session.employeeType
		);
	
		// Pripravi podatke
		HashMap dataToSubmit = activitiService.getParameters(request)
		dataToSubmit.usernameApp = session.user
		dataToSubmit.newVisit = true; //na procesni stroj pošljem spremenljivko, ki pove da moram shranit metapodatke o obisku v bazo
		params.dataToSubmit = dataToSubmit
		params.processId = returnProcessIdEditPatient(session.employeeType)
		params.msgOnSucces = ["Hvala!","Nov obisk je vnešen v sistem"]
		params.msgOnError  = ["Obisk ni bil vnešen","Pri obdelavi obrazca je prišlo do napake"]
		
		// Oddaj zadolženi akciji
		forward(
			controller: "adminWidget",
			action: "startProcessWithForm",
			params: params
		)
		
	}
	
	def newVisitFormDone = {
	
	}
	
	/**
	 * Za prikaz polja za vpis UID v panelu levo ob izbiri pregleda seznama obiskov
	 * */
	def listVisitsTab = {
		
	}	
	
	/**
	 * Prikaz seznama obiskov za izbranega pacienta
	 * */
	def listVisits = {
		doLogInfo(log, "Seznam obiskov pacienta ",session.user,session.role,
			"","",session.employeeType);
		List visits = Visit.findAllByPatientIdLike(params.uid+"%")//,[sort: "timestamp", order: "desc"]
		
		[visits:visits]
	}
	
	/**
	 * Prikaz enega obiska izbranega pacienta
	 * */
	def showVisit = {
		doLogInfo(log, "Prikaz obiska pacienta ",session.user,session.role,
			"","",session.employeeType);
		doLogInfo(log, "Pridobivam formo za ogled in popravek podatkov pacienta ",session.user,session.role,
			"","",session.employeeType);

		def form = activitiService.getProcesDefinitionForm(returnProcessIdEditPatient(session.employeeType))
			
		// get form data
		Map xmlData = existService.getParsedXML(session.user,
			EMPLOYEE_TYPE_ENUM.eval(session.employeeType).toDomainName(),
			params.patientId,params.visitFileName,
			returnProcessTemplateId(session.employeeType)
		);
		
		List formData = xmlData.pairs
		List medsData = xmlData.meds
	
		//insert medicine list in form (from eXist)
		if (session.employeeType == EMPLOYEE_TYPE_ENUM.DIABETES.eval()) {
			def htmlSegments = [MEDICINE_1_ELEMENT_DIABETES]
			form = MedsXmlReader.insertDropdown(session.user,params.patientId,EMPLOYEE_TYPE_ENUM.eval(session.employeeType), form, htmlSegments);
		} else if(session.employeeType == EMPLOYEE_TYPE_ENUM.HUJSANJE.eval()) {
			// eHujsanje - koordinatorji
			MainLdapSchema match = MainLdapSchema.find(
			    directory: "mainLdap",
			    base: "ou=people",
			    filter: "(uidatt=" + params.uid + ")"
			)
			formData << [name:"assignedCoordinator",val:match.patientsDoctorAtt]
			List staff = MainLdapSchema.findAll(directory: "mainLdap",base: "ou=people") {
				and {
					eq "employeeType", session.employeeType
					or {
						eq "memberAtt", "doctor"
						eq "memberAtt", "caremanager"
					}
				}
			}
			String firstPart = form.substring(0, form.indexOf(COORDINATOR_1_ELEMENT_HUJSANJE) + COORDINATOR_1_ELEMENT_HUJSANJE.length());
			String secondPart = form.substring(form.indexOf(COORDINATOR_1_ELEMENT_HUJSANJE) + COORDINATOR_1_ELEMENT_HUJSANJE.length());
			
			for (person in staff) {
				firstPart = firstPart + "<option value=\"" + person.uidatt + "\">" + person.uidatt + "</option>";
			}
			form = firstPart + secondPart;
		}
		
		formData << [name:"?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0009]?value",val:session.ldap.healthcareInstitutionAtt]
		
		[form:form,formData:formData,medsData: medsData,patientId:params.patientId,visitFileName:params.visitFileName]
			
	}
	
	def listPatients = {
		
		def patientsTestGroup = []
		def patientsControlGroup = []
		
		def patients = existService.getAllPatients(
			session.user,
			EMPLOYEE_TYPE_ENUM.eval(session.employeeType)
		)
		
		patients.each {
			if(
				it.healthcareInstitution == session.ldap.healthcareInstitutionAtt &&
				it.otherRequirements == "true"
			) {
				MainLdapSchema match = MainLdapSchema.find(
				    directory: "mainLdap",
				    base: "ou=people",
				    filter: "(uidatt=" + it.uid + ")"
				)
				if(match != null) patientsTestGroup.add(it)
				else patientsControlGroup.add(it)
				
			}
		}
		
		patientsTestGroup = patientsTestGroup.sort { it.lastName.toLowerCase() }
		patientsControlGroup = patientsControlGroup.sort { it.lastName.toLowerCase() }
		
		[patientsTestGroup:patientsTestGroup,patientsControlGroup:patientsControlGroup]
		
	}
	
	/**
	 * Returns process id for commiting patient (first visit) to the study.
	 * 
	 * @param employeeType
	 * @return
	 */
	def returnProcessId(employeeType) {

		if (employeeType == "A")
			return "Proces-Vkljucitev-Pacienta-V-Studijo"
		else if (employeeType == "D")
			return "Diabetes-Proces-Vkljucitev-Pacienta-V-Studijo"
		else if (employeeType == "S")
			return "Shizofrenija-Proces-Vkljucitev-Pacienta-V-Studijo"
		else if (employeeType == "H")
			return "Hujsanje-Proces-Vkljucitev-Pacienta-V-Studijo"
		else if (employeeType == "P")
			return "Sport-Proces-Vkljucitev-Pacienta-V-Studijo"
	}
	
	/**
	* Returns process id for commiting patient (first visit) to the study.
	*
	* @param employeeType
	* @return
	*/
   def returnProcessIdEditPatient(employeeType) {
	   if (employeeType == "A")
		   return "Astma-Proces-Sprememba-Podatkov"
	   else if (employeeType == "D")
		   return "Diabetes-Proces-Sprememba-Podatkov"
	   else if (employeeType == "S")
		   return "Shizofrenija-Proces-Sprememba-Podatkov"
	   else if (employeeType == "H")
		   return "Hujsanje-Proces-Sprememba-Podatkov"
	   else if (employeeType == "P")
		   return "Sport-Proces-Sprememba-Podatkov"
   }
	
	/**
	 * Returns template id (directory in eXist) for first visit patient's data.
	 * 
	 * @param employeeType
	 * @return
	 */
	def returnProcessTemplateId(employeeType) {
		if (employeeType == "A")
			return "openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1"
		else if (employeeType == "D")
			return "openEHR-EHR-SECTION.vkljucitev_eo.v1" 
		else if (employeeType == "S")
			return "openEHR-EHR-SECTION.vkljucitev_eo.v1"
		else if (employeeType == "H")
			return "openEHR-EHR-SECTION.vkljucitev_eo.v1"
		else if (employeeType == "P")
			return "openEHR-EHR-SECTION.vkljucitev_eo.v1"
	}

}
