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
package si.pint.general.periodicTrig.delegate;

import java.util.Iterator;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.util.json.JSONObject;

import si.pint.activiti.rest.utils.HttpClientUtils;
import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.messages.MessageRepo;
import si.pint.eoskrba.model.User;

@Log4j
public class StartDiabetesReminderRegularCheckup implements JavaDelegate {
	
	//process ACT definition id
	public static final String PROCESS_ID = "Diabetes-Proces-Opomnik-Redni-Pregled";	
	
	public void execute(DelegateExecution execution) throws Exception {
		
		//get LDAP patients
		List<User> patientsInStudyList = UserRegistryFactory.getPatientsFromLDAP(EMPLOYEE_TYPE_ENUM.DIABETES);  

		//find last process definition ID with key 'ProcesOpomnikVnosPEF'
		String procDefList = HttpClientUtils.doGet("process-definitions?sort=version&order=desc&size=10000");
		JSONObject procDefListJSON = new JSONObject(procDefList);
		
		String procId = StartProcessUtils.findProcId(PROCESS_ID, procDefListJSON);
		
		Iterator<User> it = patientsInStudyList.iterator();
		while (it.hasNext()) {
			User u = it.next();

			if (!StartProcessUtils.isTimeForReminder(u.getUsername(), EMPLOYEE_TYPE_ENUM.DIABETES))
				continue;
			
			log.info("Prozenje procesa obvescanja za pacienta: " + u.getUsername() + ", opomnik: " + PROCESS_ID);

			JSONObject obj = new JSONObject();
			JSONObject externalObj = new JSONObject();
			
		    externalObj.put("processDefinitionId", procId);
		    externalObj.put("businessKey", PROCESS_ID);
		    
		    //user data
		    externalObj.put(Constants.VAR_eMAIL, u.getEmail());
		    externalObj.put(Constants.VAR_MESSAGE_SMS_TEXT, MessageRepo.DIABETES_PATIENT_SMS_REMINDER_CHECKUP);
		    externalObj.put(Constants.VAR_MESSAGE_EMAIL_TEXT, MessageRepo.DIABETES_PATIENT_MAIL_REMINDER_CHECKUP);
		    externalObj.put(Constants.VAR_MESSAGE_EMAIL_GREET, MessageRepo.DIABETES_PATIENT_MAIL_REMINDER_GREET);
		    externalObj.put(Constants.VAR_MOBILE_TEL_NUM, u.getMobilePhone());
		    externalObj.put(Constants.VAR_USERNAME, u.getUsername());
			  
			String result = HttpClientUtils.doPost("process-instance", externalObj.toString());
			JSONObject resultJSON = new JSONObject(result);
			
			Iterator itResult = resultJSON.keys();
			boolean errorFound = false;
			while (itResult.hasNext()) {
				String key = (String) itResult.next();
				if (key.equals("message")) { //if 'message' returned, error was detected
					log.info("Napaka pri posiljanju opomnika uporabniku " + u.getUsername() + "za proces " + PROCESS_ID + ":\n" + ((String) resultJSON.get("message")));
					errorFound = true;
					break;
				}
			}
			if (!errorFound)
				log.info("Prozenje procesa obvescanja za pacienta: " + u.getUsername() + " uspesno zakljuceno, opomnik: " + PROCESS_ID);
			else
				log.info("Napaka pri prozenju procesa obvescanja za pacienta: " + u.getUsername() + " neuspesno zakljuceno, opomnik: " + PROCESS_ID);
		}		

		
	}	

}
