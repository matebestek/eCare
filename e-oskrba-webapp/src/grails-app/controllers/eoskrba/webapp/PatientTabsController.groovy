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

import java.util.HashMap;
import java.util.LinkedHashMap
import java.util.Map

import org.codehaus.groovy.grails.web.json.*

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.model.ArcheDataPath
import si.pint.archetypes.service.OpkpReturnArcheSrv
import si.pint.database.exist.DBManager.SystemType
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBRead;
import si.pint.eoskrba.model.User;
import si.pint.utils.SecureCodec
import eoskrba.webapp.common.LogEnabledController
import grails.converters.JSON
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.BasicConfigurator;

class PatientTabsController extends LogEnabledController{

//	Logger log4j = Logger.getLogger(PatientTabsController.class);
	def activitiService
	
	static allowedMethods =
	[opkpSave:'POST']
	
	def opkpSave = {
		
		// Log
		doLogInfo(
			log, "Opkp callback","opkp","opkp",
			"","","opkp"
		);
	
		// Pripravi spremenljivke
		def taskId = params.taskId;		
		def data = params.data;
		def taskInfo = activitiService.getUserTask(taskId);
		
		// Parsaj JSON
		SecureCodec sc = new SecureCodec();
		def s = sc.decode(data).trim();
		def json = JSON.parse(s);//JSONElement

		// Pripravi podatke za eXist
  		Map<ArcheDataPath, Object> m = new LinkedHashMap<ArcheDataPath, Object>();
  		if(json.containsKey("energy")){
  			m.put(new ArcheDataPath("/data[at0001]/items[at0003]/value"), json.energy.toString());
  		}
  		if(json.containsKey("activity_energy")){
  			m.put(new ArcheDataPath("/data[at0001]/items[at0004]/value"), json.activity_energy.toString());
  		}
  		if(json.containsKey("recommended_energy")){
  			m.put(new ArcheDataPath("/data[at0001]/items[at0018]/value"), json.recommended_energy.toString());
  		}
  		
  		for (unit in json.units) { 
  			def val = unit.value;
  			m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), unit.key);  //koda
  			
  			if(val.containsKey("name")){
  				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), val.name);  //ime
  			}
  			if(val.containsKey("val")){
  				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"),((int)val.val).toString());  //zaužite enote
  			}
  			if(val.containsKey("guide")){
  				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), val.guide.toString());  //priporočilo
  			}
  		}
  		for(w in json.warnings){ 
  			  //rezultat
  			if(w.containsKey("result")){
  				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0004]/value"),w.result);
  			}
  			  //opis
  			if(w.containsKey("desc")){
  				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0005]/value"),w.desc);
  			}
  			  //high:
  			if(w.containsKey("high")){
  				def high = w.high;
  				  //items
  				if(high.contains("items")){
  					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0001]/value"),high.items);
  				}
  				  //freq
  				if(high.contains("freq")){
  					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0002]/value"),high.freq);
  				}
  				  //weight
  				if(high.contains("weight")){
  					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0003]/value"),high.weight);
  				}
  			}
  		} 
		m.put(new ArcheDataPath("/data[at0001]/items[at0019]/value"), s);
		m.put(new ArcheDataPath("/data[at0001]/items[at0020]/value"), taskId);
		
		//PROT, FAT, CHOT, ALC, sugar
		if (json.containsKey("components")) {
			if (json.components.containsKey("PROT"))
				m.put(new ArcheDataPath("/data[at0001]/items[at0021]/value"), json.components.PROT.val.toString());
			if (json.components.containsKey("FAT"))
				m.put(new ArcheDataPath("/data[at0001]/items[at0022]/value"), json.components.FAT.val.toString());
			if (json.components.containsKey("CHOT"))
				m.put(new ArcheDataPath("/data[at0001]/items[at0023]/value"), json.components.CHOT.val.toString());
			if (json.components.containsKey("ALC"))
				m.put(new ArcheDataPath("/data[at0001]/items[at0024]/value"), json.components.ALC.val.toString());
			if (json.components.containsKey("SSUGAR"))
				m.put(new ArcheDataPath("/data[at0001]/items[at0025]/value"), json.components.SSUGAR.val.toString());
		}
		
		// Zapiši podatke v eXist
  		try {
			  OpkpReturnArcheSrv service = new OpkpReturnArcheSrv();
			  service.initInput();
			  boolean validateData = service.setAndValidataData(m);
			  if(!validateData){
				  //System.out0.println("not validated");
				  doLogInfo(log, "OpkpSave NAPAKA json=" +s ,"OPKP","OPKP","","","H"); 
				  def errors = service.getErrorList();
				  if(errors != null){
					  for(e in errors){
						  doLogInfo(log,"NAPAKA: " + e.getMessage(), "OPKP","OPKP","","","H");
					  }
				  }			
			  } else {
  			  		doLogInfo(log, "OpkpSave","OPKP","OPKP","json","opkpResult, taskID: " + taskId,"H");
					service.saveInput(SystemType.EOSKRBAWEBAPP, taskInfo.assignee,taskInfo.assignee);
			  }
  		}
  		catch (Exception e) {
  			doLogInfo(log, e.getMessage(),"OPKP","OPKP","json",s,"H");
  		}
		
		// Redirect gre na stran z nalogami. Simulirat moram gumb za odprtje naloge.
		flash.success = ["Hvala!","Obrazec je bil uspešno oddan. Čez nekaj trenutkov se prikažejo rezulati!"]
		redirect(controller:"patientTabs",action:"asthmaAndMe",params:[showResults:true])
		return false

	}
	
	
	def tasks = {
		
		// Get tasks for user
		def jsonr = activitiService.getUserTasks(session.user,100)

		doLogInfo(log, "Pacientove naloge:" + jsonr.total,session.user,session.role,"","",session.employeeType);
		[jsonr:jsonr]
		
	}
	def reports = { }
	def news = {
		doLogInfo(log, "Prikaz novic:"+params.id,session.user,session.role,"","",session.employeeType);
		[show:params.show,id:params.id]
	}
	def learn = { }
	def tickets = { }
	def help = { }
	def settings = { }
	
	def asthmaAndMe = {
		
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

	}
	
	def myPlanner = { }
	
}
