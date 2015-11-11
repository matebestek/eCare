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
package si.pint.eoskrba.esport.vnosPacienta;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;

@Log4j
public class ProcesEnterPatient3monSpTestCase extends ActivitiTestCase {

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
			"diagrams/Sport-Proces-Ponovni-Pacienta-V-Studijo.bpmn20.xml",
			"diagrams/NOTIFIER-CM.bpmn20.xml",
			"diagrams/NOTIFIER-CM-Zdravnik.bpmn20.xml",
			"diagrams/NOTIFIER-Zdravnik.bpmn20.xml"})
	public void testPatientIncludedInStudy() {
		Map<String, Object> variables = new LinkedHashMap<String, Object>();
		
		//get user information
		variables = getLoggedUserAndAssignedCoordinator(variables);
		variables = getProcessVars(variables);		
		//variables = getValuesAppropriateForStudy(variables);
System.out.println(variables);	
variables.put("username", "pacient.sp");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Sport-Proces-Ponovni-Pacienta-V-Studijo", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);
System.out.println("step0");
		//tmp
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
System.out.println(taskList);		
		// 1. inspect first task name - CM
		Task task = taskList.get(0);
System.out.println(task.getName());
		assertNotNull("Task shouldn't be null!", task);
		//assertEquals("Wrong task name!", "Bolnik Janko Novak ustreza kriterijem za študijo", task.getName().trim());
System.out.println("step1");	
		// 1.1 complete
		taskService.complete(task.getId(), variables);
System.out.println("step2");		
		// 2. inspect first task name - CM
		task = taskList.get(1);
		assertNotNull("Task shouldn't be null!", task);
		//assertEquals("Wrong task name!", "Bolnik Janko Novak ustreza kriterijem za študijo", task.getName().trim());
System.out.println("step3");
		// 2.2 complete

		taskService.complete(task.getId(), variables);
		
		
		assertProcessEnded(id);
System.out.println("step4");
		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);
		
		//delete user from LDAP
System.out.println("step5");
	}
	
	
	/**
	 * User who is admissing new patient is the same as assigned medical person on input form.
	 */
	@Deployment(resources = {
			"diagrams/Sport-Proces-Vkljucitev-Pacienta-V-Studijo.bpmn20.xml",
			"diagrams/NOTIFIER-CM.bpmn20.xml",
			"diagrams/NOTIFIER-CM-Zdravnik.bpmn20.xml",
			"diagrams/NOTIFIER-Zdravnik.bpmn20.xml"})
	public void testPatientIncludedInStudySameCmUser() {
		Map<String, Object> variables = new LinkedHashMap<String, Object>();
		
		//get user information
		variables = getLoggedUser(variables);
		variables = getProcessVars(variables);		
		//variables = getValuesAppropriateForStudy(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Sport-Proces-Vkljucitev-Pacienta-V-Studijo", variables);
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

	}
	
	
	@Deployment(resources = {
			"diagrams/Sport-Proces-Vkljucitev-Pacienta-V-Studijo.bpmn20.xml",
			"diagrams/NOTIFIER-CM.bpmn20.xml"})
	public void testPatientNotIncludedInStudy() {
		Map<String, Object> variables = new LinkedHashMap<String, Object>();
		
		//get user information
		variables = getLoggedUser(variables);
		variables = getProcessVars(variables);		
		//variables = getValuesNotAppropriateForStudy(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Sport-Proces-Vkljucitev-Pacienta-V-Studijo", variables);
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
		variables.put(Constants.VAR_USERNAME_APP, "sestra.sp");
		
		//dodeljeni koordinator
		variables.put("assignedCoordinator", "sestra.sp");
		
		return variables;
	}
	
	private Map<String, Object> getLoggedUser(Map<String, Object> variables) {

		//'external' variable from eOskrba-webApp
		variables.put(Constants.VAR_USERNAME_APP, "sestra.sp");
		
		//dodeljeni koordinator
		variables.put("assignedCoordinator", "sestra.sp");
		
		return variables;
	}
	
	public Map<String, Object> getProcessVars(Map<String, Object> variables) {
		
		// <----- 1. osnovni podatki ------->
		
		/*variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0007]?value", "bobo@gugu.com");
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0003]?value", "Povh");
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0009]?value", "KC-A01");
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0002]?value", "Boštjan");
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0008]?value", "041888888");
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0011]?value", "true");
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0006]?value", "10.07.2012");
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0010]?value", "Programer");
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0004]?value", "234");
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0005]?value", "at0013");
		*/
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!sestava.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0006]?value", new Integer("5"));
		variables.put("?items[openEHR-EHR-OBSERVATION.height.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", new Integer("180"));
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!sestava.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0007]?value", new Integer("5"));
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0053]?items[at0054]?value", "at0056");
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0007");
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0003");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0002]?value", "at0003");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0016]?value", "at0003");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!moc.v1]?items[at0002]?items[at0009]?value", new Integer("6"));
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!vzdrzljivost.v1]?items[at0001]?value", new Integer("150"));
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0017]?value", "at0003");
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0023]?value", "gfgfgfgfg");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!ravnotezje.v1]?items[at0003]?items[at0002]?value", "12:12");
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0022]?value", "at0025");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0053]?items[at0021]?value", "at0004");
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0011]?value", new Integer("11"));
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0022]?value", "at0026");
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!sestava.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", new Integer("5"));
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0008]?value", "18.07.2012");
		variables.put("?items[openEHR-EHR-EVALUATION.dnevna!aktivnost!eo!hu.v1]?data[at0001]?items[at0009]?value", "Nimam izgovora");
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", new Integer("89"));
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!vzdrzljivost.v1]?items[at0002]?value", "10.12");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!ravnotezje.v1]?items[at0003]?items[at0004]?value", new Integer("120"));
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!sestava.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", new Integer("5"));
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0020]?value", "at0003");
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0012]?value", "at0014");
		variables.put("?items[openEHR-EHR-EVALUATION.dnevna!aktivnost!eo!hu.v1]?data[at0001]?items[at0003]?value", "at0005");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0009]?value", "fdfdfdf");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0007]?value", "at0003");
		variables.put("?items[openEHR-EHR-OBSERVATION.antropometrija.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0008]?value", new Integer("110"));
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0016]?items[at0017]?value", "at0019");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0053]?items[at0055]?value", "at0058");
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0004");
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0007]?value", new Integer("20"));
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0018]?value", "at0003");
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0014]?value", "at0019");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!moc.v1]?items[at0002]?items[at0004]?value", new Integer("120"));
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!moc.v1]?items[at0002]?items[at0007]?value", new Integer("12"));
		//variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0005]?value", "at0013");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!moc.v1]?items[at0002]?items[at0008]?value", new Integer("45"));
		variables.put("?items[openEHR-EHR-OBSERVATION.antropometrija.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0007]?value", new Integer("120"));
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!moc.v1]?items[at0001]?items[at0006]?value", new Integer("120"));
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0051]?value", new Integer("21"));
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0019]?value", "at0003");
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0008]?value", "at0013");
		variables.put("?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!moc.v1]?items[at0002]?items[at0005]?value", new Integer("110"));
		return variables;
	}
	
	
}
