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

@Log4j
public class PatientAnswersWrongCompleteListener extends
		APatientEntersValueCompleteListener implements Serializable {


	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -652573758553452378L;
	
	
	@Override
	public void notify(DelegateTask delegateTask) {
		
		
		DelegateExecution execution = delegateTask.getExecution();

//		HashMap map = (HashMap) execution.getVariable(Constants.VAR_GENERIC_RESULT);
//		
//		//clear old data
//		map.remove(Constants.VAR_INPUT_QUESTION_1);
//		map.remove(Constants.VAR_INPUT_QUESTION_2);
//		map.remove(Constants.VAR_INPUT_QUESTION_3);
//		
//		execution.setVariable(Constants.VAR_GENERIC_RESULT, map);

		// save data in XML form to DB
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
		MDC.put("user", patient.getUsername());
		MDC.put("userRole",patient.getRole().toString());
		MDC.put("task", "PatientAnswersWrongCompleteListener");
		MDC.put("taskContent", "vnos vrednosti hujsanje, korak " + patient.getProcessStep());
		MDC.put("taskType", "H");		

		//patient shouldn't fill working sheets twice
		execution.setVariable(Constants.VAR_SKIP_WORKING_SHEETS, true);
		
		//reset sutask number to 3
		execution.setVariable(Constants.VAR_SUBTASK_INDEX, 3);
		
		// Nastavi subTaskVisitArray tako, da se korak 4 ne preskoči
		Boolean[] visitArray = (Boolean[]) execution.getVariable(Constants.VAR_SUBTASK_VISIT_ARRAY);
		visitArray[3] = true;
		execution.setVariable(Constants.VAR_SUBTASK_VISIT_ARRAY, visitArray);
			
		log.info("Pacient " + patient.getUsername() + " je prebral obvestilo o napacno izpolnjenem vprasalniku za korak " + patient.getProcessStep());
		
	}
	
}
