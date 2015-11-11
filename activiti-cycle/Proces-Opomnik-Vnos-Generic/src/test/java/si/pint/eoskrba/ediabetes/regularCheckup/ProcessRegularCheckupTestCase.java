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
package si.pint.eoskrba.ediabetes.regularCheckup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.messages.MessageRepo;

public class ProcessRegularCheckupTestCase extends ActivitiTestCase {

	private Map<String, Object> getTestSubject(Map<String, Object> variables) {

		// 'external' variable from eOskrba-webApp
//		variables.put(Constants.VAR_PATIENT_OBJ, 
//				      "{\"" + Constants.VAR_MOBILE_TEL_NUM + "\":\"PHONE-NUMBER\"," +
//				      		"\"" + Constants.VAR_MESSAGE_SMS_TEXT + "\":\"Prosimo, da cimveckrat uporabljate aplikacijo eOskrba za nadzorovanje urejenosti diabetesa.\"," +
//				      		 "\"" + Constants.VAR_MESSAGE_EMAIL_TEXT + "\":\"Prosimo, da čimvečkrat uporabljate aplikacijo eOskrba za nadzorovanje urejenosti diabetesa.\"," +
//				      		 "\"" + Constants.VAR_eMAIL + "\":\"EMAIL@DOMAIN.COM\"}");
		
		variables.put(Constants.VAR_MOBILE_TEL_NUM, "PHONE-NUMBER");
		variables.put(Constants.VAR_MESSAGE_SMS_TEXT, MessageRepo.DIABETES_PATIENT_SMS_REMINDER_CHECKUP);
		variables.put(Constants.VAR_MESSAGE_EMAIL_TEXT,  MessageRepo.DIABETES_PATIENT_MAIL_REMINDER_CHECKUP);
		variables.put(Constants.VAR_MESSAGE_EMAIL_GREET, MessageRepo.DIABETES_PATIENT_MAIL_REMINDER_GREET);
		variables.put(Constants.VAR_eMAIL, "EMAIL@DOMAIN.COM");
		variables.put(Constants.VAR_USERNAME, "ivan.pacient");
		variables.put(Constants.VAR_USERNAME_APP, "ivan.pacient");
		
		return variables;
	}
	
	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService
				.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds
				+ ") does not contain " + activityId,
				activeActivityIds.contains(activityId));
	}

	@Deployment(resources = {"diagrams/Diabetes-Proces-Opomnik-Redni-Pregled.bpmn20.xml"})
	public void testPatientGeneralNotifier() {
		Map<String, Object> variables = new HashMap<String, Object>();

		// get user information
		variables = getTestSubject(variables);

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Diabetes-Proces-Opomnik-Redni-Pregled", variables);
		String id = processInstance.getId();

		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);

	}
	
}
