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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.log4j.MDC;
import si.pint.archetypes.exceptions.EmptyParametersException;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.ArchetypeService;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.listeners.APatientEntersValueCompleteListener;
import si.pint.eoskrba.model.User;
import si.pint.eoskrba.utils.ArchetypeServiceUtils;

@Log4j
public class PatientEntersWorkingSheetsCompleteListener extends
		APatientEntersValueCompleteListener implements Serializable {


	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -652573758553452378L;
	
	//questineer 1 root name
	public static final String Q_ROOT = "openEHR-EHR-EVALUATION.hu_koraki_";
	
	//class name
	public static final String PACKAGE = "si.pint.archetypes.service";
	public static final String SERVICE_CLASS_NAME_START = "HujsanjeStep";
	public static final String SERVICE_CLASS_NAME_END = "ArcheSrv";	
	
	@Override
	public void notify(DelegateTask delegateTask) {
		
		//get patient
		DelegateExecution execution = delegateTask.getExecution();		
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);

		//modify number of digits for process step '9' -> '09'
		String processStep = patient.getProcessStep();
		if (Integer.parseInt(processStep) < 10)
			processStep = "0" + processStep;
		
		//compose service class name
		String className = PACKAGE + "." + SERVICE_CLASS_NAME_START + processStep + SERVICE_CLASS_NAME_END;
		Class c = null;
		ArchetypeService service = null;
		try {
			c = Class.forName(className);
			service = (ArchetypeService) c.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if (service == null || !service.initInput())
			return;

		// user input data
		Map<ArcheDataPath, Object> userInputsMap = new HashMap<ArcheDataPath, Object>();
		
//		//parameters for calculating ok/notOk/critical categories
//		HashMap<String, String> map = (HashMap<String, String>) execution.getVariable(Constants.VAR_GENERIC_RESULT);
		
		// look for all possible attributes on session
		Map allAtributes = service.getInputsMap();
		Iterator it = allAtributes.keySet().iterator();
		int sumOfAllAnswers = 0; //if sum stays 0, then all answers are correct
		while (it.hasNext()) {
			String key = (String) it.next();
			String keyR = key.replaceAll("/", "?");
			keyR = keyR.replaceAll("_", "!");
			Object var = execution.getVariable(keyR);
			if (var != null && !var.toString().equals("")) {
				userInputsMap.put(new ArcheDataPath(key), var);
				
				//<<-------all questions ------->
				if (key.indexOf(Q_ROOT) >= 0) {
					Integer anwserCorect = (Integer) service.getValue(key, (String) var);
					if (anwserCorect != null)
						sumOfAllAnswers += anwserCorect;
				}		
			}
		}
		
		// data validation
		if (!service.setAndValidataData(userInputsMap))		
			new EmptyParametersException("Validation for hujšanje values was not ok!");
		
		boolean answersCorrect = sumOfAllAnswers == 0;
					
		MDC.put("user", patient.getUsername());
		MDC.put("userRole", patient.getRole().toString());
		MDC.put("task", "PatientEntersWorkingSheetsCompleteListener");
		MDC.put("taskContent", "vnos delovnih listov, korak " + patient.getProcessStep());
		MDC.put("taskType", "H");		
			
		service.saveInput(SystemType.EOSKRBAPROCESSENGINE,((String) execution.getVariable(Constants.VAR_USERNAME_APP)), patient.getUsername());	
		
		// Shrani podatek, da je podnalogo 5 dokončal
		ArchetypeServiceUtils.updateCompletedSubTasksNo(
			5,
			delegateTask.getExecution(),
			patient.getUsername(),
			patient.getUsername()
		);
		
		log.info("Pacient " + patient.getUsername() + " je izpolnil vprasalnik znanja za korak " + patient.getProcessStep());
		
	}
	

}
