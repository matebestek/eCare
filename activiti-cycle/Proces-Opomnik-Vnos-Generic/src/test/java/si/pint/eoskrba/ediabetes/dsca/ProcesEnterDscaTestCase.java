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
package si.pint.eoskrba.ediabetes.dsca;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.GenericProcessId.GENERIC_PROCESS_ID;

@Log4j
public class ProcesEnterDscaTestCase extends ActivitiTestCase {

	private Map<String, Object> getTestSubject(Map<String, Object> variables) {

		// 'external' variable from eOskrba-webApp
		variables.put(Constants.VAR_PATIENT_OBJ, "{\"sex\":\"MALE\",\"height\":\"183\",\"weight\":\"85\",\"username\":\"pacient.di\",\"firstNameLastName\":\"Janko Bozic\",\"eMail\":\"EMAIL@DOMAIN.COM\",\"role\":\"1\",\"birthDate\":\"01.01.1980\",\"birpisId\":\"ivan.pacient\",\"mobilePhoneNum\":\"PHONE-NUMBER\"}");
		
		variables.put(Constants.VAR_genericProcId, GENERIC_PROCESS_ID.DIABETES_DSCA.eval());		
		return variables;
	}

	public Map<String, Object> getProcessVars(Map<String, Object> variables) {
		
		//Kolikokrat v zadnjih 7 dneh ste upoštevali priporočeno dieto?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0014]?value", "at0017");
		
		//Kolikokrat v zadnjih 7 dneh ste omejili vnos kalorij, kot je priporočeno v zdravi prehrani za sladkorne bolnike?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0023]?value", "at0018");
		
		//Kolikokrat v zadnjih 7 dneh ste jedli sadje in zelenjavo (vsaj 5 enot)?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0032]?value", "at0015");
		
		//Kolikokrat v zadnjih 7 dneh ste uživali hrano bogato z maščobami, kot je rdeče meso ipd.?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0041]?value", "at0016");
		
		//Kolikokrat v zadnjih 7 dneh ste opravljali aktivnost zmerne intenzivnosti vsaj 30 minut?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0005]?items[at0050]?value", "at0066");
		
		//Kolikokrat v zadnjih 7 dneh ste opravljali aktivnost visoke intenzivnosti vsaj 30 minut?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0005]?items[at0051]?value", "at0101");
		
		//Kolikokrat v zadnjih 7 dneh ste kontrolirali nivo vašega krvnega slakorja?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0006]?items[at0052]?value", "at0110");
		
		//Kolikokrat na teden je priporočilo merjenja krvnega sladkorja s strani vašega zdravnika?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0006]?items[at0053]?value", "at0111");
		
		//Kolikokrat v zadnjih 7 dneh ste kontrolirali  telesno težo?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0006]?items[at0054]?value", "at0096");
		
		//Kolikokrat v zadnjih 7 dneh ste kontrolirali krvni pritisk?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0006]?items[at0055]?value", "at0105");		
		
		//Kolikokrat v zadnjih 7 dneh ste natančno pregledali svoja stopala?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0007]?items[at0056]?value", "at0090");
		
		//Kolikokrat v zadnjih 7 dneh ste vzeli predpisano število tablet za kontrolo sladkorne bolezni? 
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0008]?items[at0057]?value", "at0091");
		
		//Kolikokrat v zadnjih 7 dneh ste prejeli predpisane inzulinske injekcije? (samo za bolnike z inzulinom!)
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0008]?items[at0058]?value", "at0100");
		
		//Ali ste v zadnjih 7 dneh kadili, četudi minimalno?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0009]?items[at0059]?value", "FALSE");
		
		//Če da, kolikšno je bilo v zadnjih 7 dnevih povprečje pokajenih cigaret (na dan)?
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0009]?items[at0060]?value", "6");
		
		//Komentar
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0133]?value", "to je komentar");		
		
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
	public void testPatientEntersValues() {
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
		assertEquals("Wrong task name!", "Izpolnite DSCA vprašalnik", task.getName().trim());
		
		//1.1 claim & complete
		variables = getProcessVars(variables);
		taskService.complete(task.getId(), variables);
		
		//2. inspect second task
		task = taskService.createTaskQuery().singleResult();
		assertEquals("Wrong task name!", "Hvala za vnos vprašalnika", task.getName().trim());
		
//		//2.1 claim & complete
		taskService.complete(task.getId());		

		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);

	}
	
}
