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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.archetypes.exceptions.LdapException;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.Role;

@Log4j
public class ProcesEnterPatientHuTestCase extends ActivitiTestCase {

	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService
				.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds
				+ ") does not contain " + activityId,
				activeActivityIds.contains(activityId));
	}
	
	/**
	 * User who is admissing new patient is NOT the same as assigned medical person on input form.
	 * 
	 */
	@Deployment(resources = {
			"diagrams/Hujsanje-Proces-Vkljucitev-Pacienta-V-Studijo.bpmn20.xml",
			"diagrams/NOTIFIER-CM.bpmn20.xml",
			"diagrams/NOTIFIER-CM-Zdravnik.bpmn20.xml",
			"diagrams/NOTIFIER-Zdravnik.bpmn20.xml"})
	public void testPatientIncludedInStudy() {
		Map<String, Object> variables = new LinkedHashMap<String, Object>();
		
		//get user information
		variables = getLoggedUserAndAssignedCoordinator(variables);
		variables = getProcessVars(variables);		
		variables = getValuesAppropriateForStudy(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Vkljucitev-Pacienta-V-Studijo", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);

		//tmp
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
		
		// 1. inspect first task name - CM
		Task task = taskList.get(0);
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Novak ustreza kriterijem za študijo", task.getName().trim());

		// 1.1 complete
		taskService.complete(task.getId(), variables);
		
		// 2. inspect first task name - CM
		task = taskList.get(1);
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Novak ustreza kriterijem za študijo", task.getName().trim());

		// 2.2 complete
		taskService.complete(task.getId(), variables);

		//save user for later
		String userName = (String) variables.get("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0007]?value");
		
		
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);
		
		//delete user from LDAP
		try {
			if (userName != null && UserRegistryFactory.getUserFromLDAP(userName, Role.PATIENT) != null) {
				UserRegistryFactory.removeUser(userName);
			}
		} catch (LdapException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	/**
	 * User who is admissing new patient is the same as assigned medical person on input form.
	 */
	@Deployment(resources = {
			"diagrams/Hujsanje-Proces-Vkljucitev-Pacienta-V-Studijo.bpmn20.xml",
			"diagrams/NOTIFIER-CM.bpmn20.xml",
			"diagrams/NOTIFIER-CM-Zdravnik.bpmn20.xml",
			"diagrams/NOTIFIER-Zdravnik.bpmn20.xml"})
	public void testPatientIncludedInStudySameCmUser() {
		Map<String, Object> variables = new LinkedHashMap<String, Object>();
		
		//get user information
		variables = getLoggedUser(variables);
		variables = getProcessVars(variables);		
		variables = getValuesAppropriateForStudy(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Vkljucitev-Pacienta-V-Studijo", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);

		//tmp
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
		
		// 1. inspect first task name - CM
		Task task = taskList.get(0);
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Novak ustreza kriterijem za študijo", task.getName().trim());

		// 1.1 complete
		taskService.complete(task.getId(), variables);
		
		//save user for later
		String userName = (String) variables.get("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0007]?value");
		
		
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);
		
		//delete user from LDAP
		try {
			if (userName != null && UserRegistryFactory.getUserFromLDAP(userName, Role.PATIENT) != null) {
				UserRegistryFactory.removeUser(userName);
			}
		} catch (LdapException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	@Deployment(resources = {
			"diagrams/Hujsanje-Proces-Vkljucitev-Pacienta-V-Studijo.bpmn20.xml",
			"diagrams/NOTIFIER-CM.bpmn20.xml"})
	public void testPatientNotIncludedInStudy() {
		Map<String, Object> variables = new LinkedHashMap<String, Object>();
		
		//get user information
		variables = getLoggedUser(variables);
		variables = getProcessVars(variables);		
		variables = getValuesNotAppropriateForStudy(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Vkljucitev-Pacienta-V-Studijo", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);

		//tmp
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
		
		// 1. inspect first task name - doctor
		Task task = taskList.get(0);
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Novak ne ustreza kriterijem za študijo!", task.getName().trim());

		// 1.1 complete
		taskService.complete(task.getId(), variables);
		
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);
		
	}	
	
	


	private Map<String, Object> getLoggedUserAndAssignedCoordinator(Map<String, Object> variables) {

		//'external' variable from eOskrba-webApp
		variables.put(Constants.VAR_USERNAME_APP, "sestra.hu.vnos");
		
		//dodeljeni koordinator
		variables.put("assignedCoordinator", "sestra.hu");
		
		return variables;
	}
	
	private Map<String, Object> getLoggedUser(Map<String, Object> variables) {

		//'external' variable from eOskrba-webApp
		variables.put(Constants.VAR_USERNAME_APP, "sestra.hu");
		
		//dodeljeni koordinator
		variables.put("assignedCoordinator", "sestra.hu");
		
		return variables;
	}
	
	public Map<String, Object> getProcessVars(Map<String, Object> variables) {
		
		// <----- 1. osnovni podatki ------->
		
		//ime
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0002]?value", "Janko");
		
		//priimek
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0003]?value", "Novak");
		
		//bis
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0004]?value", "bid.janko.novak");
		
		//spol
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0005]?value", "at0013"); //moski
		
		//datum rojstva
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0006]?value", "01.07.1980");
		
		//email
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0007]?value", "EMAIL@DOMAIN.COM");
		
		//GSM
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0008]?value", "PHONE-NUMBER");
		
		//zdravstvena ustanova
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0009]?value", "ZD Novo Mesto");
		
		//poklic
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0010]?value", "tesar");
		
		//datum pricetka delavnice
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0016]?value", "20.02.2012");
				
//		//vkljucen v studijo
//		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0012]?value", "FALSE");
		
		// visina
		variables.put("?items[openEHR-EHR-OBSERVATION.height.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "185");
			
		
		// <----- 3. anamneza ------->
		//teza
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", "88.5");
		
		//indeks telesne mase
		variables.put("?items[openEHR-EHR-OBSERVATION.body!mass!index.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "9");

		//alkohol ---------->>>>
		
		//pogostost pitja
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0006");
		
		//kolicina pijace
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0008]?value", "at0012");
		
		//Pogostost pitja vecjih kolicin
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0014]?value", "at0016");
		
		//stevilo dosezenih tock
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0020]?value", "9");
		
		//ocena ogrozenosti
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0022]?value", "at0024");
		
		//komentar
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0021]?value", "komentar za alko");
		
		//kajenje ---------->>>>
		
		//kadilski status
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0015");
		
		//dnevno stevilo cigaret
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0007]?value", "6");
		
		//datum zacetka uzivanja
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0009]?value", "1990-01-01");
		
		//starost ob zacetku uzivanja
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0011]?value", "14");
		
		//poskus prenehanja
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0016]?items[at0017]?value", "at0019");
		
		//datum prenehanja
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0016]?items[at0010]?value", "2000-01-01");
		
		//starost ob prenehanju
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0016]?items[at0012]?value", "22");
		
		//stopnja ogrozenosti
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0022]?value", "at0023");
		
		//komentar
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0013]?value", "kadilski komentar");
		
		//telesna dejavnost ---------->>>>
		
		//visoko intenzivna vadba
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0005");
		
		//zmerno intenzivna vadba
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0012]?value", "at0015");
		
		//stevilo dosezenih tock
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0022]?value", "8");
		
		//ogrozenost
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0024]?value", "at0026");
		
		//komentar
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0023]?value", "komentar za telesno dejavnost");
			
		
		// <----- 5. krvni tlak ------->
		
		//sistolicni
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "120");
		
		//diastolicni
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", "80");
		
		//opombe
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0006]?value", "opomba za krvni tlak");
		
		//dnevna aktivnost ------>>>>>>
		
		//Stopnja dnevne aktivnosti
		variables.put("?items[openEHR-EHR-EVALUATION.dnevna!aktivnost!eo!hu.v1]?data[at0001]?items[at0003]?value", "at0005");
		
		//komentar
		variables.put("?items[openEHR-EHR-EVALUATION.dnevna!aktivnost!eo!hu.v1]?data[at0001]?items[at0009]?value", "to je komentar");	
		
		//-->>> Laboratorij
		
		//glukoza v krvi na tesce
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", new Integer("16"));
				
		//holesterol
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0009]?value", new Integer("19"));
		
		//trigliceridi
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0010]?value", new Integer("20"));
		
		//hdl
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0011]?value", new Integer("21"));
		
		//ldl
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0012]?value", new Integer("22"));
		
		//-->>> Telesna sestava
		
		//odstotek telesne mascobe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!sestava.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "6.5");
		
		//trebusna mascoba
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!sestava.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", "6.6");		
		
		//skeletno misicevje
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!sestava.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0006]?value", "6.7");
		
		//poraba metabolizma v mirovanju
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!sestava.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0007]?value", "6.8");		
		
		return variables;
	}
	
	public Map<String, Object> getValuesAppropriateForStudy(Map<String, Object> variables) {
		
		//izpolnjuje ostale vkljucitvene pogoje
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0011]?value", "TRUE");
				
		return variables;
	}
	
	public Map<String, Object> getValuesNotAppropriateForStudy(Map<String, Object> variables) {
		
		//izpolnjuje ostale vkljucitvene pogoje
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0011]?value", "FALSE");
		
		return variables;
	}	
}
