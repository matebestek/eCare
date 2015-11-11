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
package si.pint.eoskrba.eastma.vnosPacienta;

import java.util.HashMap;
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
public class ProcesVnosPacientaTestCase extends ActivitiTestCase {

	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService
				.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds
				+ ") does not contain " + activityId,
				activeActivityIds.contains(activityId));
	}
	
	@Deployment(resources = {
			"diagrams/Proces-Vkljucitev-Paceinta-V-Studijo.bpmn20.xml",
			"diagrams/NOTIFIER-Zdravnik.bpmn20.xml",
			"diagrams/NOTIFIER-CM.bpmn20.xml",
			"diagrams/NOTIFIER-CM-Zdravnik.bpmn20.xml" })
	public void testPatientIncludedInStudy() {
		Map<String, Object> variables = new HashMap<String, Object>();
		
		//get user information
		variables = getTestSubject(variables);
		variables = getProcessVars(variables);		
		variables = getActAppropriateForStudy(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Vkljucitev-Pacienta-V-Studijo", getActAppropriateForStudy(variables));
		String id = processInstance.getId();
		log.info("Started process instance id " + id);

		//tmp
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
		
		// 1. inspect first task name - doctor
		Task task = taskList.get(0);
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Novak ustreza kriterijem za študijo", task.getName().trim());

		// 1.1 complete
		taskService.complete(task.getId(), variables);

		// 2. inspect first task name - CM
		task = taskList.get(1);
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Novak ustreza kriterijem za študijo", task.getName().trim());

		//save user for later
		String userName = (String) variables.get("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0081]?value");
		
		// 2.1 complete
		taskService.complete(task.getId(), variables);
		
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
			"diagrams/Proces-Vkljucitev-Paceinta-V-Studijo.bpmn20.xml",
			"diagrams/NOTIFIER-Zdravnik.bpmn20.xml",
			"diagrams/NOTIFIER-CM.bpmn20.xml",
			"diagrams/NOTIFIER-CM-Zdravnik.bpmn20.xml" })
	public void testPatientNotIncludedInStudy() {
		Map<String, Object> variables = new HashMap<String, Object>();
		
		//get user information
		variables = getTestSubject(variables);
		variables = getProcessVars(variables);
		variables = getActNotAppropriateForStudy(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Vkljucitev-Pacienta-V-Studijo", getActNotAppropriateForStudy(variables));
		String id = processInstance.getId();
		log.info("Started process instance id " + id);

		// 1. inspect first task name - doctor
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Novak ne ustreza kriterijem za študijo!", task.getName().trim());

		// 1.1 complete
		taskService.complete(task.getId(), variables);

		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);
			

		log.info("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
	}	

	private Map<String, Object> getTestSubject(Map<String, Object> variables) {

		// fill user form data (vnosPacienta.form)
	
		//'external' variable from eOskrba-webApp
		variables.put(Constants.VAR_USERNAME_APP, "sestra.di");

		//vars for doctor&CM notification sub processes
//		variables.put("notifyDoctorForm_1", null);
//		variables.put("listenerDoctorObvestiloCreate_1", "");
//		variables.put("listenerDoctorObvestiloComplete_1", "");
//		variables.put("notifyCaremanagerForm", "");
//		variables.put("caremanagerEmail", "");
//		variables.put("listenerCaremanagerObvestiloComplete", "");
//		variables.put("listenerCaremanagerObvestiloCreate", "");

		return variables;
	}
	
	public Map<String, Object> getProcessVars(Map<String, Object> variables) {
		
		//ime
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0004]?value", "Janko");
		//priimek
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0075]?value", "Novak");
		//bid
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0011]?value", "bid.bkuren");
		//spol
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0076]?value", "at0077");
		//datum rojstva
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0080]?value", "01.06.1980");
		//email
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0081]?value", "EMAIL@DOMAIN.COM");
		//GSM
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0082]?value", "054845754");
		//zdravstvena ustanova
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0083]?value", "KC-A02");
		//vlazno stanovanje
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0086]?items[at0087]?value", "at0088");
		//vsakodnevno delo v hlevu
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0086]?items[at0090]?value", "at0092");
		
		
		// <------ telesna masa ----->
		//teza
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1 and name?value='Telesna masa']?data[at0002]?events[at0003]?data[at0001]?items[at0004 and name?value='Teža']?value", "85");
		
		// <------ telesna visina ----->
		variables.put("?items[openEHR-EHR-OBSERVATION.height.v1 and name?value='Telesna višina']?data[at0001]?events[at0002]?data[at0003]?items[at0004 and name?value='Višina']?value", "191");
		
		// <------ druga zdravila ----->
		variables.put("?items[openEHR-EHR-EVALUATION.terapija.v1]?data[at0001]?items[at0478]?value", "tobrex4");
		
		//pljucne funkcije 
		//FEV1
		variables.put("?items[openEHR-EHR-OBSERVATION.pljucne!funkcije!eo!as.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", "5.3");
		
		//VC
		variables.put("?items[openEHR-EHR-OBSERVATION.pljucne!funkcije!eo!as.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0006]?value", "5.4");
		
		//izpolnjuje ostale vkljucitvene pogoje
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0094]?value", "FALSE");
		
		return variables;
	}
	
	public Map<String, Object> getActAppropriateForStudy(Map<String, Object> variables) {
		
		
		// <------ ACT - ok for study ----->
		variables.put("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0034]?value", "at0039");
		variables.put("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0035]?value", "at0044");
		variables.put("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0036]?value", "at0049");
		variables.put("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0037]?value", "at0054");
		variables.put("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0038]?value", "at0063");
				
		return variables;
	}
	
	public Map<String, Object> getActNotAppropriateForStudy(Map<String, Object> variables) {
		
		//ACT vprašalnik - ACT not for study
		variables.put("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0034]?value", "at0039");
		variables.put("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0035]?value", "at0048");
		variables.put("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0036]?value", "at0053");
		variables.put("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0037]?value", "at0058");
		variables.put("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0038]?value", "at0063");		
		
		return variables;
	}	



}
