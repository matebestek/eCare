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
package si.pint.eoskrba.ediabetes.inputDiab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.apachecommons.CommonsLog;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.GenericProcessId.GENERIC_PROCESS_ID;


@CommonsLog
public class ProcessEnterDiabValuesPeriodicTestCase extends ActivitiTestCase {

	private Map<String, Object> getTestSubject(Map<String, Object> variables) {

		// 'external' variable from eOskrba-webApp
		variables.put(Constants.VAR_PATIENT_OBJ, "{\"sex\":\"MALE\",\"height\":\"183\",\"weight\":\"85\",\"username\":\"pacient.di\",\"firstNameLastName\":\"Janko Bozic\",\"eMail\":\"EMAIL@DOMAIN.COM\",\"role\":\"1\",\"birthDate\":\"01.01.1980\",\"birpisId\":\"ivan.pacient\",\"mobilePhoneNum\":\"PHONE-NUMBER\"}");
		variables.put(Constants.VAR_USERNAME_APP, "pacient.di");
		variables.put(Constants.VAR_genericProcId, GENERIC_PROCESS_ID.DIABETES_VALUES.eval());		
		return variables;
	}

	public Map<String, Object> getProcessVars(Map<String, Object> variables) {
		
//		// --- teza ---
//		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value",	new Integer("85"));
//		
//		// --- krvni tlak ---
//		//sistolicni		
//		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "80");		
//		
//		//diastolicni		
//		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", "120");
		
		//opomba
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0006]?value", "to je opomba");
		
		//--- krvni sladkor ----
//		//krvni sladkor
//		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!sladkor!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "5");
		
		//opomba
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!sladkor!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", "to je opomba za krvni sladkor");
		
		
		// ---- dietni preksrki ----
		variables.put("?items[openEHR-EHR-OBSERVATION.prehrana!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", "torta");
		
		// ---- prehrana
		variables.put("?items[openEHR-EHR-OBSERVATION.prehrana!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "kruh");
		
//		// Kako pogosto si oseba sama meri telesno težo
//		userInputsMap
//				.put(new ArcheDataPath(
//						"?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!di.v1]?data[at0001]?items[at0006]?items[at0011]?value",
//						"at0027");
//		// Kako pogosto si oseba sama meri krvni sladkor
//		userInputsMap
//				.put(new ArcheDataPath(
//						"?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!di.v1]?data[at0001]?items[at0006]?items[at0007]?value",
//						"at0020");
//		// Kako pogosto si oseba sama meri krvni tlak?<?p><?td>
//		userInputsMap
//				.put(new ArcheDataPath(
//						"?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!di.v1]?data[at0001]?items[at0006]?items[at0015]?value",
//						"at0032");		
		
		// <------ WONCA vprašalnik------>
		variables
				// čustva
				.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0005]?value",
						"at0025");
		variables
				// Telesno počutje
				.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0006]?value",
						"at0018");

		variables
				// Družabno življenje
				.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0007]?value",
						"at0036");

		variables
				// Vsakodenevna opravila
				.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0008]?value",
						"at0030");

		variables
				// Bolečina
				.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0009]?value",
						"at0016");

		variables
				// sprememba zdravja
				.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0010]?value",
						"at0038");
		variables
				// Splošno stanje
				.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0011]?value",
						"at0045");
		// število točk
		variables
				.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0047]?value",
						"15");
		// komentar
		variables
				.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0048]?value",
						"tole je komentar za wonca vprašalanik");
		
		//terapija
		//ali ste danes vzeli vsa predpisana zdravila
		variables
		.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0007]?items[at0019]?value",
				"TRUE");
		
		
		//telesna dejavnost
		
		//sportna panoga
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0004]?value", "at0038");
		
		//intenzivnost vadbe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0012]?value", "at0014");
		
		//trajanje vadbe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0006]?value", "00:20");
		
		//komentar
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0018]?value", "komentar dif");
		
		//Kolikokrat na teden ste bili v zadnjih 14 dnevih gibalno aktivni (30 min zmerne ali visoke intenzivnosti)?
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0023]?value", "at0027");
		
		// <----- laboratorij ------->
		
		//Hb1ac
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0015]?value", new Integer("15"));
		
		//glukoza v krvi na tesce
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", new Integer("16"));
		
		//albumin
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0007]?value", new Integer("17"));
		
		//aceton
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0008]?value", new Integer("18"));
		
		//holesterol
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0009]?value", new Integer("19"));
		
		//trigliceridi
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0010]?value", new Integer("20"));
		
		//hdl
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0011]?value", new Integer("21"));
		
		//ldl
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0012]?value", new Integer("22"));
		
		//kreatinin
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0013]?value", new Integer("23"));
		
		//alt
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0014]?value", new Integer("24"));
		
		
		return variables;
	}

	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService
				.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds
				+ ") does not contain " + activityId,
				activeActivityIds.contains(activityId));
	}

	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml",  
			 				 "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			 				 "diagrams/REMINDER-Pacient.bpmn20.xml",
			 				 "diagrams/NOTIFIER-Pacient.bpmn20.xml",
			 				 "diagrams/VnosMeritev.bpmn20.xml"})
	public void testPatientEntersValuesOk() {
		Map<String, Object> variables = new HashMap<String, Object>();

		// get user information
		variables = getTestSubject(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Opomnik-Vnos-Generic", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);
		
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-VnosVrednostiPacienta");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite obrazec za vnos meritev", task.getName().trim());
		
		//1.1 claim & complete
		variables = getProcessVars(variables);
		
		//teza
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", "85");
		
		//krvni sladkor
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!sladkor!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "5");
		
		//krvni tlak
		//diastolicni		
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", "80");
		
		//sistolicni		
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "120");		
		
		
		
		taskService.complete(task.getId(), variables);
		
		//2. inspect second task
		task = taskService.createTaskQuery().singleResult();
		assertEquals("Wrong task name!", "Vaše vrednosti (krvni sladkor, krvni tlak in telesna teža) na obrazcu za vnos meritev so normalne", task.getName().trim());
		
//		//2.1 claim & complete
		taskService.complete(task.getId());
		
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");

	}
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml",  
							 "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
			 			     "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
			 			     "diagrams/NOTIFIER-CM.bpmn20.xml",
			 			     "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			 			     "diagrams/NOTIFIER-Pacient.bpmn20.xml",
			 			     "diagrams/REMINDER-Pacient.bpmn20.xml",
			 				 "diagrams/VnosMeritev.bpmn20.xml"})	
	public void testPatientEntersValuesNotOk() {
		Map<String, Object> variables = new HashMap<String, Object>();

		// get user information
		variables = getTestSubject(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Opomnik-Vnos-Generic", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);
		
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-VnosVrednostiPacienta");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite obrazec za vnos meritev", task.getName().trim());
		
		//1.1 claim & complete
		variables = getProcessVars(variables);
		
		//teza
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", "85");
		
		//krvni sladkor
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!sladkor!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "5");
		
		//krvni tlak
		//diastolicni		
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", "90");
		
		//sistolicni		
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "150");		
		
		
		
		taskService.complete(task.getId(), variables);
		
		//2. inspect tasks for CM & patient
		List<Task> list = taskService.createTaskQuery().orderByTaskName().asc().list();
		assertTrue("Task list size should be 2", list != null && list.size() == 2);
				
		assertEquals("Wrong task name!", "Bolnik Janko Bozic ima mejno eno ali več od vnešenih vrednosti (krvni sladkor, krvni tlak ali telesna teža)", list.get(0).getName().trim());
		assertEquals("Wrong task name!", "Mejna vrednost ene ali več vnešenih vrednosti (krvni sladkor, krvni tlak ali telesna teža)", list.get(1).getName().trim());
		
//		//2.1 claim & complete
		taskService.complete(list.get(0).getId());
		taskService.complete(list.get(1).getId());
		
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");

	}	
	
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml",
							 "diagrams/NOTIFIER-ALL.bpmn20.xml",
							 "diagrams/NOTIFIER-Zdravnik.bpmn20.xml",
							 "diagrams/NOTIFIER-Zdravnik-Ext.bpmn20.xml",
			 			     "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
			 			     "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
			 			     "diagrams/NOTIFIER-CM.bpmn20.xml",
			 			     "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			 			     "diagrams/NOTIFIER-Pacient.bpmn20.xml",
			 			     "diagrams/REMINDER-Pacient.bpmn20.xml",
			 				 "diagrams/VnosMeritev.bpmn20.xml"})		
	public void testPatientEntersValuesCritical() {
		Map<String, Object> variables = new HashMap<String, Object>();

		// get user information
		variables = getTestSubject(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Opomnik-Vnos-Generic", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);
		
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-VnosVrednostiPacienta");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite obrazec za vnos meritev", task.getName().trim());
		
		//1.1 claim & complete
		variables = getProcessVars(variables);
		
		//teza
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", "85");
		
		//krvni sladkor
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!sladkor!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "8");
		
		//krvni tlak
		//diastolicni		
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", "80");
		
		//sistolicni		
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "120");		
		
		
		
		taskService.complete(task.getId(), variables);
		
		//2. inspect tasks for CM & patient
		List<Task> list = taskService.createTaskQuery().orderByTaskName().asc().list();
		assertTrue("Task list size should be 2", list != null && list.size() == 3);
				
		assertEquals("Wrong task name!", "Bolnik Janko Bozic ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)", list.get(0).getName().trim());
		assertEquals("Wrong task name!", "Bolnik Janko Bozic ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)", list.get(1).getName().trim());
		assertEquals("Wrong task name!", "Poslabšanje vaše urejenosti diabetesa", list.get(2).getName().trim());
		
//		//2.1 claim & complete
		taskService.complete(list.get(0).getId());
		taskService.complete(list.get(1).getId());
		taskService.complete(list.get(2).getId());
		
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");

	}		
	
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml",
							 "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
							 "diagrams/NOTIFIER-CM.bpmn20.xml",			
			 				 "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			 				 "diagrams/REMINDER-Pacient.bpmn20.xml",
			 				 "diagrams/NOTIFIER-Pacient.bpmn20.xml",
			 				 "diagrams/VnosMeritev.bpmn20.xml"})	
	public void testPatientDidntRespondInTreeDaysAndContinues() {
		Map<String, Object> variables = new HashMap<String, Object>();

		// get user information
		variables = getTestSubject(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Opomnik-Vnos-Generic", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);
		
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-VnosVrednostiPacienta");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite obrazec za vnos meritev", task.getName().trim());
		
	    // Manually execute the job - 1.pass
	    Job timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());		
	    
		//2. inspect first task name
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite obrazec za vnos meritev", task.getName().trim());
		
	    // Manually execute the job - 2.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());
	    
		//3. inspect first task name
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite obrazec za vnos meritev", task.getName().trim());
		
	    // Manually execute the job - 3.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());	 
	    
	    //4. inspect task name (Obvesti_CM_o_nevnosu_vrednosti_PEF)
	    task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Bozic že tri dni ni vnesel vrednosti za diabetes", task.getName().trim());
		variables.put(Constants.VAR_PATIENT_COOPERATES, "true");
		taskService.complete(task.getId(), variables);	    

		//5. start from the beginning
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite obrazec za vnos meritev", task.getName().trim());
		
		
		//1.1 claim & complete
		variables = getProcessVars(variables);
		
		//teza
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", "85");
		
		//krvni sladkor
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!sladkor!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "5");
		
		//krvni tlak
		//diastolicni		
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", "80");
		
		//sistolicni		
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "120");		
		
		
		
		taskService.complete(task.getId(), variables);
		
		//2. inspect second task
		task = taskService.createTaskQuery().singleResult();
		assertEquals("Wrong task name!", "Vaše vrednosti (krvni sladkor, krvni tlak in telesna teža) na obrazcu za vnos meritev so normalne", task.getName().trim());
		
//		//2.1 claim & complete
		taskService.complete(task.getId());
		
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");

	}	
	
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml",
			 				 "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
			 				 "diagrams/NOTIFIER-CM.bpmn20.xml",			
			 				 "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			 				 "diagrams/REMINDER-Pacient.bpmn20.xml",
			 				 "diagrams/NOTIFIER-Pacient.bpmn20.xml",
			 				 "diagrams/VnosMeritev.bpmn20.xml"})		
	public void testPatientDidntRespondInTreeDaysAndStops() {
		Map<String, Object> variables = new HashMap<String, Object>();

		// get user information
		variables = getTestSubject(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Opomnik-Vnos-Generic", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);
		
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-VnosVrednostiPacienta");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite obrazec za vnos meritev", task.getName().trim());
		
	    // Manually execute the job - 1.pass
	    Job timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());		
	    
		//2. inspect first task name
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite obrazec za vnos meritev", task.getName().trim());
		
	    // Manually execute the job - 2.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());
	    
		//3. inspect first task name
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite obrazec za vnos meritev", task.getName().trim());
		
	    // Manually execute the job - 3.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());	 
	    
	    //4. inspect task name (Obvesti_CM_o_nevnosu_vrednosti_PEF)
	    task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Bozic že tri dni ni vnesel vrednosti za diabetes", task.getName().trim());
		variables.put(Constants.VAR_PATIENT_COOPERATES, "false");
		taskService.complete(task.getId(), variables);	    

	
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");

	}	
	
	
	/**
	 * Generate new ExecutionListener from fully qualified name.
	 * 
	 * @param fqn
	 * @return
	 */
	private static Object getObjectFromClassName(String fqn) {
		Class c = null;
		try {
			c = Class.forName(fqn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Object cObj = null;
		try {
			cObj = c.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return cObj;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
