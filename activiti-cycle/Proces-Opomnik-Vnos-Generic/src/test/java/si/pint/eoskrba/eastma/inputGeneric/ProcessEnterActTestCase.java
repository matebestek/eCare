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
package si.pint.eoskrba.eastma.inputGeneric;

import java.util.HashMap;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.GenericProcessId.GENERIC_PROCESS_ID;

@Log4j
public class ProcessEnterActTestCase extends ActivitiTestCase {

	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService
				.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds
				+ ") does not contain " + activityId,
				activeActivityIds.contains(activityId));
	}
	
	private HashMap<String, Object> getTestUser() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(Constants.VAR_PATIENT_OBJ, "{\"sex\":\"MALE\",\"height\":\"183\",\"weight\":\"85\",\"username\":\"pacient.as\",\"firstNameLastName\":\"Janko Bozic\",\"eMail\":\"EMAIL@DOMAIN.COM\",\"role\":\"1\",\"birthDate\":\"01.01.1980\",\"birpisId\":\"ivan.pacient\",\"mobilePhoneNum\":\"PHONE-NUMBER\"}");
		
		variables.put(Constants.VAR_genericProcId, GENERIC_PROCESS_ID.ASTMA_ACT.eval());
		return variables;
	}	
	
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml",  
							 "diagrams/NOTIFIER-ALL.bpmn20.xml",
							 "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
							 "diagrams/NOTIFIER-CM.bpmn20.xml",
							 "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
							 "diagrams/REMINDER-Pacient.bpmn20.xml",
							 "diagrams/NOTIFIER-Pacient.bpmn20.xml",
							 "diagrams/VnosMeritev.bpmn20.xml"})
	public void testActOK() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		    
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Opomnik-Vnos-Generic", getTestUser());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-VnosVrednostiPacienta");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite vprašalnik ACT", task.getName().trim());
		
		//1.1 claim & complete
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0034]?value", "at0043");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0035]?value", "at0048");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0036]?value", "at0053");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0037]?value", "at0058");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0038]?value", "at0062");
		
		taskService.complete(task.getId(), variables);
		
		//2. inspect second task
		task = taskService.createTaskQuery().singleResult();
		assertEquals("Wrong task name!", "Vrednost vašega ACT točkovnika je normalna", task.getName().trim());
		
//		//2.1 claim & complete
		taskService.complete(task.getId());
		
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
    }	
	
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml", 
			 "diagrams/NOTIFIER-CM.bpmn20.xml", 
			 "diagrams/NOTIFIER-ALL.bpmn20.xml",
			 "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
			 "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
			 "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			 "diagrams/REMINDER-Pacient.bpmn20.xml",
			 "diagrams/NOTIFIER-Pacient.bpmn20.xml",
			 "diagrams/VnosMeritev.bpmn20.xml",
			 "diagrams/NOTIFIER-Zdravnik.bpmn20.xml"})
	public void testActNotOK() {
		HashMap<String, Object> variables = new HashMap<String, Object>();

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Opomnik-Vnos-Generic", getTestUser());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-VnosVrednostiPacienta");

		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite vprašalnik ACT", task.getName().trim());

		//1.1 claim & complete
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0034]?value", "at0041");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0035]?value", "at0046");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0036]?value", "at0051");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0037]?value", "at0056");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0038]?value", "at0060");
		
		taskService.complete(task.getId(), variables);

		//2. inspect task for patient & cm
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
		assertNotNull("Task list shouldn't be null!", taskList);
		assertEquals("Task list size should be two", 2, taskList.size());
		
		assertEquals("Wrong task name!", "Bolnik Janko Bozic ima mejno vrednost ACT točkovnika", taskList.get(0).getName().trim());
		assertEquals("Wrong task name!", "Mejna vrednost ACT točkovnika", taskList.get(1).getName().trim());
		
		taskService.complete(taskList.get(0).getId(), variables);		
		taskService.complete(taskList.get(1).getId(), variables);

		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
	}		
	
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml", 
			 "diagrams/NOTIFIER-CM.bpmn20.xml", 
			 "diagrams/NOTIFIER-ALL.bpmn20.xml",
			 "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
			 "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
			 "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			 "diagrams/REMINDER-Pacient.bpmn20.xml",
			 "diagrams/NOTIFIER-Pacient.bpmn20.xml",
			 "diagrams/VnosMeritev.bpmn20.xml",
			 "diagrams/NOTIFIER-Zdravnik-Ext.bpmn20.xml",
			 "diagrams/NOTIFIER-Zdravnik.bpmn20.xml"})
	public void testActCritical() {
		HashMap<String, Object> variables = new HashMap<String, Object>();

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Opomnik-Vnos-Generic", getTestUser());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-VnosVrednostiPacienta");

		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite vprašalnik ACT", task.getName().trim());

		//1.1 claim & complete
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0034]?value", "at0039");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0035]?value", "at0044");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0036]?value", "at0049");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0037]?value", "at0055");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0038]?value", "at0062");
		
		taskService.complete(task.getId(), variables);

		//2. inspect task for patient & cm
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
		assertNotNull("Task list shouldn't be null!", taskList);
		assertEquals("Task list size should be three", 3, taskList.size());
		
		assertEquals("Wrong task name!", "Bolnik Janko Bozic ima kritično vrednost ACT točkovnika!", taskList.get(0).getName().trim());
		assertEquals("Wrong task name!", "Bolnik Janko Bozic ima kritično vrednost ACT točkovnika!", taskList.get(1).getName().trim());
		assertEquals("Wrong task name!", "Poslabšanje vrednosti ACT točkovnika", taskList.get(2).getName().trim());
		
		taskService.complete(taskList.get(0).getId(), variables);		
		taskService.complete(taskList.get(1).getId(), variables);
		taskService.complete(taskList.get(2).getId(), variables);

		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
	}	
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml", 
			 "diagrams/NOTIFIER-CM-Ext.bpmn20.xml", 
			 "diagrams/NOTIFIER-ALL.bpmn20.xml",
			 "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
			 "diagrams/NOTIFIER-CM.bpmn20.xml",
			 "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			 "diagrams/REMINDER-Pacient.bpmn20.xml",
			 "diagrams/NOTIFIER-Pacient.bpmn20.xml",
			 "diagrams/VnosMeritev.bpmn20.xml"})
	public void testPatientDidntRespondInThreeDays() {
		HashMap<String, Object> variables = new HashMap<String, Object>();

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Opomnik-Vnos-Generic", getTestUser());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-VnosVrednostiPacienta");

		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite vprašalnik ACT", task.getName().trim());
		
	    // Manually execute the job - 1.pass
	    Job timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());
	    
		//2. inspect second task name
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite vprašalnik ACT", task.getName().trim());
		
	    // Manually execute the job - 2.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());		
	    
		//3. inspect third task name
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite vprašalnik ACT", task.getName().trim());
		
	    // Manually execute the job - 3.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());	  
	    
	    //4. inspect task name (Obvesti_CM_o_nevnosu_vrednosti_PEF)
	    task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Bozic že tri dni ni izpolnil vprašalnika ACT", task.getName().trim());
		variables.put(Constants.VAR_PATIENT_COOPERATES, "true");
		taskService.complete(task.getId(), variables);	    

		//5. start from the beginning
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite vprašalnik ACT", task.getName().trim());
		
		//1.1 claim & complete
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0034]?value", "at0043");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0035]?value", "at0048");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0036]?value", "at0053");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0037]?value", "at0058");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0038]?value", "at0062");

		taskService.complete(task.getId(), variables);

		//2. inspect second task
		task = taskService.createTaskQuery().singleResult();
		assertEquals("Wrong task name!", "Vrednost vašega ACT točkovnika je normalna", task.getName().trim());

		////2.1 claim & complete
		taskService.complete(task.getId());

		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
	}	
	
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml", 
			 "diagrams/NOTIFIER-CM-Ext.bpmn20.xml", 
			 "diagrams/NOTIFIER-ALL.bpmn20.xml",
			 "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
			 "diagrams/NOTIFIER-CM.bpmn20.xml",
			 "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			 "diagrams/REMINDER-Pacient.bpmn20.xml",
			 "diagrams/NOTIFIER-Pacient.bpmn20.xml",
			 "diagrams/VnosMeritev.bpmn20.xml"})
	public void testPatientDidntRespondAndStopped() {
		HashMap<String, Object> variables = new HashMap<String, Object>();

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Opomnik-Vnos-Generic", getTestUser());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "CallActivity-VnosVrednostiPacienta");

		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite vprašalnik ACT", task.getName().trim());
		
	    // Manually execute the job - 1.pass
	    Job timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());
	    
		//2. inspect second task name
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite vprašalnik ACT", task.getName().trim());
		
	    // Manually execute the job - 2.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());		
	    
		//3. inspect third task name
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Izpolnite vprašalnik ACT", task.getName().trim());
		
	    // Manually execute the job - 3.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());	  
	    
	    //4. inspect task name (Obvesti_CM_o_nevnosu_vrednosti_PEF)
	    task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Bozic že tri dni ni izpolnil vprašalnika ACT", task.getName().trim());
		variables.put(Constants.VAR_PATIENT_COOPERATES, "false");
		taskService.complete(task.getId(), variables);	    

		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
	}		
	
	
}
