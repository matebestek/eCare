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
package si.pint.eoskrba.eastma.zdravila;

import java.util.HashMap;
import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;

public class OpomnikZdravilaTestCase extends ActivitiTestCase{

	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds + ") does not contain " + activityId, activeActivityIds.contains(activityId));
	}
	
	private HashMap<String, Object> getTestSubject() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(Constants.VAR_PATIENT_OBJ, "{\"sex\":\"MALE\",\"height\":\"183\",\"weight\":\"85\",\"username\":\"ivan.pacient\",\"firstNameLastName\":\"Janko Bozic\",\"eMail\":\"EMAIL@DOMAIN.COM\",\"role\":\"1\",\"birthDate\":\"01.01.1980\",\"birpisId\":\"ivan.pacient\",\"mobilePhoneNum\":\"PHONE-NUMBER\"}");
		
		return variables;
	}

	@Deployment(resources = {"diagrams/Proces-Opomnik-Zdravila.bpmn20.xml",
							 "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
							 "diagrams/NOTIFIER-Pacient.bpmn20.xml"})
	public void testOpomnikZdravila() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		    
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Proces-Opomnik-Zdravila", getTestSubject());
		String id = processInstance.getId();
		System.out.println("Started process instance id " + id);
		assertInActivity(id, "ProcesOpomnikVnosNotifierPacientExt");
		    
		//1. inspect first task name
		Task task = taskService.createTaskQuery().singleResult();
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Prosimo, da preverite vašo zalogo zdravil!", task.getName().trim());
				
		taskService.complete(task.getId(), variables);
				
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
    }
	
	
	public void testCalcDaysToNotify() {
		
		long nowTS = System.currentTimeMillis();
		for (long i = 0; i < 2000; i++) {  		
			
			long pastTS = nowTS - ((long) 1000 * (long) 60 * 60 * 24 * i);
			
			long diff = nowTS - pastTS;
		
			int days = (int) (diff/(1000 * 60 * 60 * 24));
		
			if (days != 0 && (days - 75) % 90 == 0) {
				System.out.println("imamo zadetek pri j: " + i + ", in days: " + days);
			}
		}
    }
	
}
