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
package si.pint.eoskrba.ehujsanje.inputKoraki.subtask;

import java.io.Serializable;
import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.log4j.MDC;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.listeners.APatientEntersValueCompleteListener;
import si.pint.eoskrba.model.User;
import si.pint.eoskrba.utils.ArchetypeServiceUtils;

@Log4j
public class PatientEntersOPKPValuesCompleteListener extends
		APatientEntersValueCompleteListener implements Serializable {


	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -652573758553452378L;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		
		DelegateExecution execution = delegateTask.getExecution();
		
//		EnterHujsanjeKorakiArcheSrv service = new EnterHujsanjeKorakiArcheSrv();
//		if (!service.initInput())
//			return;
//
//		// user input data
//		Map<ArcheDataPath, Object> userInputsMap = new HashMap<ArcheDataPath, Object>();
		
		//parameters for calculating ok/notOk/critical categories
//		HashMap<String, String> map = new HashMap<String, String>();
		
//		Iterator it2 = execution.getVariableNames().iterator();
//		while (it2.hasNext()) {
//			Object object = (Object) it2.next();
//			System.out.println("kljuc: " + object + ", objekt: " + execution.getVariable(object + "").getClass().getName());
//			
//		}
//		// look for all possible attributes on session
//		Map allAtributes = service.getInputsMap();
//		Iterator it = allAtributes.keySet().iterator();
//		while (it.hasNext()) {
//			String key = (String) it.next();
//			String keyR = key.replaceAll("/", "?");
//			keyR = keyR.replaceAll("_", "!");
//			Object var = execution.getVariable(keyR);
//			if (var != null) {
//				userInputsMap.put(new ArcheDataPath(key), var);
//								
//			}
//		}
//
//		
//		// data validation
//		if (!service.setAndValidataData(userInputsMap))		
//			new EmptyParametersException("Validation for hujšanje values was not ok!");
		
//		execution.setVariable(Constants.VAR_GENERIC_RESULT, map);
		
		// save data in XML form to DB
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
		MDC.put("user", patient.getUsername());
		MDC.put("userRole", patient.getRole().toString());
		MDC.put("task", "PatientEntersOPKPValuesCompleteListener");
		MDC.put("taskContent", "vnos vrednosti OPKP , korak " + patient.getProcessStep());
		MDC.put("taskType", "H");
		
		// Shrani podatek, da je podnalogo 2 dokončal
		ArchetypeServiceUtils.updateCompletedSubTasksNo(
			2,
			delegateTask.getExecution(),
			patient.getUsername(),
			patient.getUsername()
		);
			
//		service.saveInput(patient.getUsername());	
			
		log.info("Pacient " + patient.getUsername() + " je izpolnil OPKP vprasalnik za korak " + patient.getProcessStep());
		
	}
	

}
