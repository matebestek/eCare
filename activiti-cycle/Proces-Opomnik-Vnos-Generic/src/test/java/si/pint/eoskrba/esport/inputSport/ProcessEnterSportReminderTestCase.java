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
package si.pint.eoskrba.esport.inputSport;

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
public class ProcessEnterSportReminderTestCase extends ActivitiTestCase {

	private Map<String, Object> getTestSubject(Map<String, Object> variables) {

		// 'external' variable from eOskrba-webApp
		variables.put(Constants.VAR_PATIENT_OBJ, "{\"sex\":\"MALE\",\"height\":\"183\",\"weight\":\"85\",\"usernameApp\":\"sestra.sp\",\"username\":\"bobo@gugu.com\",\"firstNameLastName\":\"Janko Bozic\",\"eMail\":\"EMAIL@DOMAIN.COM\",\"role\":\"1\",\"birthDate\":\"01.01.1980\",\"birpisId\":\"ivan.pacient\",\"mobilePhoneNum\":\"PHONE-NUMBER\"}");
		
		variables.put(Constants.VAR_genericProcId, GENERIC_PROCESS_ID.SPORT_VALUES.eval());		
		return variables;
	}

	public Map<String, Object> getProcessVars(Map<String, Object> userInputsMap) {
		

		// Teža
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", "130");
		// Komentar
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0024]?value", "blb bla");
		// Stanje oblačil
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?state[at0008]?items[at0009]?value", "at0011");
		// Drugi dejavniki
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?state[at0008]?items[at0025]?value", "dddddd");
		// Datum vadbe
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0019]?value", "12.05.2012");
		// Športna panoga
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "at0022");
		// Trajanje vadbe (format HH:MM)
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0006]?value", "02:12");
		// Razdalja (v m)
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0055]?value", "600");
		// Porabljena energija (v kcal)
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0057]?value", "800");
		// Intenzivnost vadbe
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.intenzivnost!opisno!5st!eo.v1]?items[at0001]?value", "at0002");
		// Počutje med vadbo
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.opis!pocutja.v1]?items[at0001]?value", "at0002");
		// Vadba opravljena
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0021]?value", "true");
		// Opombe
		userInputsMap.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!enkratna!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0018]?value", "Ni kaj za komentirat");

		
		return userInputsMap;
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
		
		variables = getProcessVars(variables);
		
		
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
				
		assertEquals("Wrong task name!", "Udeleženec Janko Bozic ima mejno eno ali več od vnešenih vrednosti (krvni sladkor, krvni tlak ali telesna teža)", list.get(0).getName().trim());
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
				
		assertEquals("Wrong task name!", "Udeleženec Janko Bozic ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)", list.get(0).getName().trim());
		assertEquals("Wrong task name!", "Udeleženec Janko Bozic ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)", list.get(1).getName().trim());
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
		assertEquals("Wrong task name!", "Udeleženec Janko Bozic že tri dni ni vnesel vrednosti za diabetes", task.getName().trim());
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
		assertEquals("Wrong task name!", "Udeleženec Janko Bozic že tri dni ni vnesel vrednosti za diabetes", task.getName().trim());
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
