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
package si.pint.eoskrba.ehujsanje;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.apachecommons.CommonsLog;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.messages.MessageRepo;

@CommonsLog
public class ProcessEnterValuesTestCase extends ActivitiTestCase {

	private Map<String, Object> getTestSubject(Map<String, Object> variables) {

		// 'external' variable from eOskrba-webApp
//		variables.put(Constants.VAR_PATIENT_OBJ, "{\"sex\":\"MALE\",\"height\":\"183\",\"weight\":\"85\",\"username\":\"pacient.di\",\"firstNameLastName\":\"Janko Bozic\",\"eMail\":\"EMAIL@DOMAIN.COM\",\"role\":\"1\",\"birthDate\":\"01.01.1980\",\"birpisId\":\"ivan.pacient\",\"mobilePhoneNum\":\"PHONE-NUMBER\"}");
		variables.put(Constants.VAR_USERNAME_APP, "EMAIL@DOMAIN.COM");
		
		return variables;
	}

	public Map<String, Object> getProcessVars(Map<String, Object> variables) {
		
		//indeks telesne mase - se sam izracuna
//		variables.put("?items[openEHR-EHR-OBSERVATION.body!mass!index.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "9");

		//obseg pasu
		variables.put("?items[openEHR-EHR-OBSERVATION.obseg!pasu!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "110");

		//<<<----telesna dejavnost---->>>>>>
		
		//datum vadbe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0019]?value", "01.07.1980");
		//sportna panoga
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "at0035");
		//intenzivnost vadbe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.intenzivnost!opisno!3st!eo.v1]?items[at0001]?value", "at0004");
		//trajanje vadbe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0006]?value", "00:20");
		//komentar
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0018]?value", "komentar dif");

		
		
		return variables;
	}
	
	public Map<String, Object> getVarsOk(Map<String, Object> variables) {
		
		//teza
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", "87");
	
		
		return variables;
	}
	
	public Map<String, Object> getVarsNotOk(Map<String, Object> variables) {
		
		//teza
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", "89");
				
		return variables;
	}
	
//	public Map<String, Object> getVarsCritical(Map<String, Object> variables) {
//		
//		//teza
//		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", "86");		
//		
//		return variables;
//	}


	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService
				.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds
				+ ") does not contain " + activityId,
				activeActivityIds.contains(activityId));
	}

	@Deployment(resources = {"diagrams/Hujsanje-Proces-Vnos-Vrednosti.bpmn20.xml",  
			 				 "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			 				 "diagrams/NOTIFIER-Pacient.bpmn20.xml"})
	public void testPatientEntersValuesOk() {
		Map<String, Object> variables = new HashMap<String, Object>();

		// get user information
		variables = getTestSubject(variables);
		variables = getProcessVars(variables);

		//values OK
		variables = getVarsOk(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Vnos-Vrednosti", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);
		
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-NotifierPacient");
		    		
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();

		task = taskService.createTaskQuery().singleResult();
		assertEquals("Wrong task name!", "Vaša telesna teža je normalna", task.getName().trim());
		
		//2.1 claim & complete
		taskService.complete(task.getId());
		
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");

	}
	
	@Deployment(resources = {"diagrams/Hujsanje-Proces-Vnos-Vrednosti.bpmn20.xml",  
							 "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
			 			     "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
			 			     "diagrams/NOTIFIER-CM.bpmn20.xml",
			 			     "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			 			     "diagrams/NOTIFIER-Pacient.bpmn20.xml"})	
	public void testPatientEntersValuesNotOk() {
		Map<String, Object> variables = new HashMap<String, Object>();

		// get user information
		variables = getTestSubject(variables);
		variables = getProcessVars(variables);
		
		//values not OK
		variables = getVarsNotOk(variables);

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Vnos-Vrednosti", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);
		
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-NotifierCmPacient");
		    
		
		//1. inspect tasks for CM & patient
		List<Task> list = taskService.createTaskQuery().orderByTaskName().asc().list();
		assertTrue("Task list size should be 2", list != null && list.size() == 2);
				
		assertEquals("Wrong task name!", "Bolnik Pacient Hujsanje ima spremenjeno telesno težo", list.get(0).getName().trim());
		assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NOT_OK, list.get(1).getName().trim());
		
		//1.1 claim & complete
		taskService.complete(list.get(0).getId());
		taskService.complete(list.get(1).getId());
		
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");

	}	
	
	
//	@Deployment(resources = {"diagrams/Diabetes-Proces-Vnos-Vrednosti.bpmn20.xml",
//							 "diagrams/NOTIFIER-ALL.bpmn20.xml",
//							 "diagrams/NOTIFIER-Zdravnik.bpmn20.xml",
//							 "diagrams/NOTIFIER-Zdravnik-Ext.bpmn20.xml",
//			 			     "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
//			 			     "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
//			 			     "diagrams/NOTIFIER-CM.bpmn20.xml",
//			 			     "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
//			 			     "diagrams/NOTIFIER-Pacient.bpmn20.xml"})		
//	public void testPatientEntersValuesCritical() {
//		Map<String, Object> variables = new HashMap<String, Object>();
//
//		// get user information
//		variables = getTestSubject(variables);
//		variables = getProcessVars(variables);
//		
//		//values critical
//		variables = getVarsCritical(variables);
//
//		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Diabetes-Proces-Vnos-Vrednosti", variables);
//		String id = processInstance.getId();
//		log.info("Started process instance id " + id);
//		
//		System.out.println("Started process instance id " + id);
//		assertInActivity(id, "CallActivity-NotifierAll");
//		    
//		//1. inspect tasks for CM & patient
//		List<Task> list = taskService.createTaskQuery().orderByTaskName().asc().list();
//		assertTrue("Task list size should be 2", list != null && list.size() == 3);
//				
//		assertEquals("Wrong task name!", "Bolnik Pacient Diabetes ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)", list.get(0).getName().trim());
//		assertEquals("Wrong task name!", "Bolnik Pacient Diabetes ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)", list.get(1).getName().trim());
//		assertEquals("Wrong task name!", "Poslabšanje vaše urejenosti diabetesa", list.get(2).getName().trim());
//		
//		//1.1 claim & complete
//		taskService.complete(list.get(0).getId());
//		taskService.complete(list.get(1).getId());
//		taskService.complete(list.get(2).getId());
//		
//		//runtimeService.signal(id); (za to rabimo waitState?)
//		assertProcessEnded(id);
//
//		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
//		assertNotNull(historicProcessInstance);
//
//		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
//
//	}		

	
}
