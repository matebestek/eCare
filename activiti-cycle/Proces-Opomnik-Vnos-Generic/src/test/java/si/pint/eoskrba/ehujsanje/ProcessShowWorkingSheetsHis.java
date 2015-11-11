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
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;

@Log4j
public class ProcessShowWorkingSheetsHis extends ActivitiTestCase {

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
			"diagrams/Hujsanje-Proces-Delovni-List-Prikaz.bpmn20.xml"
		})
	public void testPatientIncludedInStudy() {
		Map<String, Object> variables = new LinkedHashMap<String, Object>();
		
		//get user information
		variables = getLoggedUser(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Delovni-List-Prikaz", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);

		//tmp
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
		
		// 1. inspect first task name - CM
		Task task = taskList.get(0);
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Delovni list za korak " + variables.get(Constants.VAR_PROCESS_STEP), task.getName().trim());

	    
		//execute job or manually terminate process
		// 1.1 complete
		taskService.complete(task.getId(), variables);				
    			
		
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);
		
	}
	
	private Map<String, Object> getLoggedUser(Map<String, Object> variables) {

		//'external' variable from eOskrba-webApp
		variables.put(Constants.VAR_USERNAME_APP, "pacient.hu");
		
		//'external' variable from eOskrba-webApp
		variables.put(Constants.VAR_PROCESS_STEP, "3");
				
		return variables;
	}
}
