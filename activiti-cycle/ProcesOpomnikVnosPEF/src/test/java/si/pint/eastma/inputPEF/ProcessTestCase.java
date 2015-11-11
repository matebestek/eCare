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
package si.pint.eastma.inputPEF;

import java.util.HashMap;
import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;

public class ProcessTestCase extends ActivitiTestCase {
	  
	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds + ") does not contain " + activityId, activeActivityIds.contains(activityId));
	}
	
	private HashMap<String, Object> getTestSubject() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(Constants.VAR_PATIENT_OBJ, "{\"sex\":\"MALE\",\"height\":\"183\",\"weight\":\"85\",\"username\":\"pacient.as\",\"firstNameLastName\":\"Janko Bozic\",\"eMail\":\"EMAIL@DOMAIN.COM\",\"role\":\"1\",\"birthDate\":\"01.01.1980\",\"birpisId\":\"ivan.pacient\",\"mobilePhoneNum\":\"PHONE-NUMBER\"}");
		
		return variables;
	}

	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-PEF.bpmn20.xml"})
	public void testPefOK() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		    
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ProcesOpomnikVnosPEF", getTestSubject());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "Pacient_naj_vnese_PEF_vrednost_");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());
		
		//1.1 claim & complete
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0057]?items[at0058]?value", "700");
		
		taskService.complete(task.getId(), variables);
		
		//2. inspect second task
		task = taskService.createTaskQuery().singleResult();
		assertEquals("Wrong task name!", "Vrednost PEF je normalna", task.getName().trim());
		
//		//2.1 claim & complete
		taskService.complete(task.getId());
		
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
    }
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-PEF.bpmn20.xml"})
	public void testPefNotOK() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		    
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ProcesOpomnikVnosPEF", getTestSubject());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "Pacient_naj_vnese_PEF_vrednost_");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());
		
		//1.1 claim & complete
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0057]?items[at0058]?value", "550");
	    
		taskService.complete(task.getId(), variables);
		
		//2. check next 3 tasks
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
		assertNotNull("Task list shouldn't be null!", taskList);
		assertEquals("Task list size should be two", 2, taskList.size());
		
		assertEquals("Wrong task name!", "Bolnik " + "Janko Bozic" + " ima mejno vrednost PEF", taskList.get(0).getName().trim());
		assertEquals("Wrong task name!", "Mejna vrednost PEF", taskList.get(1).getName().trim());
		
		taskService.complete(taskList.get(0).getId(), variables);		
		taskService.complete(taskList.get(1).getId(), variables);		
	
				
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
    }
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-PEF.bpmn20.xml"})
	public void testPefCritical() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		    
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ProcesOpomnikVnosPEF", getTestSubject());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "Pacient_naj_vnese_PEF_vrednost_");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());
		
		//1.1 claim & complete
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0057]?items[at0058]?value", "300");
	    
		taskService.complete(task.getId(), variables);
		
		//2. check next 3 tasks
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
		assertNotNull("Task list shouldn't be null!", taskList);
		assertEquals("Task list size should be three", 3, taskList.size());
		
		assertEquals("Wrong task name!", "Bolnik Janko Bozic ima kritično vrednost PEF!", taskList.get(0).getName().trim());
		assertEquals("Wrong task name!", "Bolnik Janko Bozic ima kritično vrednost PEF!", taskList.get(1).getName().trim());
		assertEquals("Wrong task name!", "Poslabšanje vrednosti PEF", taskList.get(2).getName().trim());
		
		taskService.complete(taskList.get(0).getId(), variables);		
		taskService.complete(taskList.get(1).getId(), variables);		
		taskService.complete(taskList.get(2).getId(), variables);	
				
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
    }
	
	
	/**
	 * If PEF value:
	 * - negative
	 * - to high
	 * - not given
	 */
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-PEF.bpmn20.xml"})
	public void testPefNotValid() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		    
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ProcesOpomnikVnosPEF", getTestSubject());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "Pacient_naj_vnese_PEF_vrednost_");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());
		
		//1.1 claim & complete
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0057]?items[at0058]?value", "1001");
	    
		taskService.complete(task.getId(), variables);
		
		//2. execute task again - this thime with right input 
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());
		
		//2.1 claim & complete
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0057]?items[at0058]?value", "700");
	    
		taskService.complete(task.getId(), variables);		
		
		//3. inspect second task
		List l = taskService.createTaskQuery().list();
		task = taskService.createTaskQuery().singleResult();
		assertEquals("Wrong task name!", "Vrednost PEF je normalna", task.getName().trim());
		
		//3.1 claim & complete
		taskService.complete(task.getId());
		
		//runtimeService.signal(id); (za to rabimo waitState?)
 		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
    }
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-PEF.bpmn20.xml"})
	public void testPatientDidntRespondInThreeDays() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		    
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ProcesOpomnikVnosPEF", getTestSubject());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "Pacient_naj_vnese_PEF_vrednost_");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());
		
	    // Manually execute the job - 1.pass
	    Job timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());
	    
	    //2. second occurence for 'Vnesite PEF vrednost' task
	    task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());

	    // Manually execute the job - 2.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());

	    //3. third occurence for 'Vnesite PEF vrednost' task
	    task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());

	    // Manually execute the job - 3.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());
	    
	    //4. inspect task name (Obvesti_CM_o_nevnosu_vrednosti_PEF)
	    task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Bozic že tri dni ni vnesel vrednosti PEF", task.getName().trim());
		variables.put("patientStopCoopSignal", "false"); 
		taskService.complete(task.getId(), variables);
	    
		
		//5. inspect first task name
		task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());
		
		//5.1 claim & complete
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0057]?items[at0058]?value", "900");
		
		taskService.complete(task.getId(), variables);
		
		//6. inspect second task
		task = taskService.createTaskQuery().singleResult();
		assertEquals("Wrong task name!", "Vrednost PEF je normalna", task.getName().trim());
		
//		//6.1 claim & complete
		taskService.complete(task.getId());
		
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
		
    }	
	
	
	@Deployment(resources = {"diagrams/Proces-Opomnik-Vnos-PEF.bpmn20.xml"})
	public void testPatientDidntRespondAndStopped() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		    
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ProcesOpomnikVnosPEF", getTestSubject());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "Pacient_naj_vnese_PEF_vrednost_");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());
		
	    // Manually execute the job - 1.pass
	    Job timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());
	    
	    //2. second occurence for 'Vnesite PEF vrednost' task
	    task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());

	    // Manually execute the job - 2.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());

	    //3. third occurence for 'Vnesite PEF vrednost' task
	    task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Vnesite vrednost PEF", task.getName().trim());

	    // Manually execute the job - 3.pass
	    timer = managementService.createJobQuery().singleResult();
	    managementService.executeJob(timer.getId());
	    
	    //4. inspect task name (Obvesti_CM_o_nevnosu_vrednosti_PEF)
	    task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Bozic že tri dni ni vnesel vrednosti PEF", task.getName().trim());
		variables.put("patientStopCoopSignal", "true");
		variables.put("?description[at0001]?items[at0002]?value", "šel na potovanje v azijo");
		taskService.complete(task.getId(), variables);		
	    
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
		
    }	
	
}
