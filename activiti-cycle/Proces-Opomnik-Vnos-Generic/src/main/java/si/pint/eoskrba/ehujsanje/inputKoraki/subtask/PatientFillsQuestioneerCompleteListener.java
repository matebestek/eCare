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
import org.apache.velocity.VelocityContext;

import si.pint.archetypes.exceptions.EmptyParametersException;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.ArchetypeService;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.listeners.APatientEntersValueCompleteListener;
import si.pint.eoskrba.model.User;
import si.pint.eoskrba.model.VelocityContextSer;
import si.pint.eoskrba.utils.ArchetypeServiceUtils;

@Log4j
public class PatientFillsQuestioneerCompleteListener extends
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
	
	//<<------Q 11 -------->
	//template root name
	public static final String Q11_TEMPLATE_ROOT = "/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]";
	
	//Cvrenje
	public static final String Q11_SECOND_QUESTION_NOT_OK = "/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]/items[at0007]/value";
	//Kuhanje z veliko soli
	public static final String Q11_FIFTH_QUESTION_NOT_OK = "/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]/items[at0010]/value";
	//Kuhanje z maslom in smetano
	public static final String Q11_EIGHT_QUESTION_NOT_OK = "/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]/items[at0013]/value";
	
	//<<------Q 15 -------->
	//template root name
	public static final String Q15_TEMPLATE_ROOT = "/items[openEHR-EHR-EVALUATION.hu_koraki_15.v1]/data[at0001]/items[at0003]";

	//Ali imajo zaščitne snovi (vitamini, minerali...) v prehranskih dopolnilih enak učinek v preventivi pred boleznimi kot te snovi iz hrane?
	public static final String Q15_FIRST_QUESTION_NOT_OK = "/items[openEHR-EHR-EVALUATION.hu_koraki_15.v1]/data[at0001]/items[at0003]/items[at0006]/value";
	
	
	
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
		
		VelocityContextSer context = new VelocityContextSer();
		
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
					if (anwserCorect != null) {
						sumOfAllAnswers += anwserCorect;
						context.put(convertPathToVelocityFormat(keyR), anwserCorect + "");
					}
				}		
			}
		}
		
		// data validation
		if (!service.setAndValidataData(userInputsMap))		
			new EmptyParametersException("Validation for hujšanje values was not ok!");
		
		boolean answersCorrect = sumOfAllAnswers == 0;
		
		boolean booleanAnswersCorrect = checkBooleanAnswers(userInputsMap, patient.getProcessStep(), context);
		
		execution.setVariable(Constants.VAR_ANSWERS_CORRECT, answersCorrect && booleanAnswersCorrect);
				
		// Naloga se preskoči samo, če jo je pacient rešil pravilno
		if(answersCorrect && booleanAnswersCorrect) {
			// Shrani podatek, da je podnalogo 6 dokončal
			ArchetypeServiceUtils.updateCompletedSubTasksNo(
				6,
				delegateTask.getExecution(),
				patient.getUsername(),
				patient.getUsername()
			);
		}
		
//		execution.setVariable(Constants.VAR_GENERIC_RESULT, map); //trenutno ne rabimo rezultatov 
		
		// save data in XML form to DB
		MDC.put("user", patient.getUsername());
		MDC.put("userRole",patient.getRole().toString());
		MDC.put("task", "PatientFillsQuestioneerCompleteListener");
		MDC.put("taskContent", "izpolnitev vprasalnika hujsanje, korak " + patient.getProcessStep());
		MDC.put("taskType", "H");		

		log.info("Pacient " + patient.getUsername() + " je izpolnil vprasalnik za korak " + patient.getProcessStep());
		
		if (!(answersCorrect && booleanAnswersCorrect)) {		
			context.put("patient",patient);	
			execution.setVariable(Constants.VAR_HUJSANJE_ANSWERS_CONTEXT, context);
		}
				
	}


	/**
	 * Some questioneers have answers in boolean values. For those we have to manualy check correct answers.
	 * 
	 * @param userInputsMap
	 * @return
	 */
	private boolean checkBooleanAnswers(Map<ArcheDataPath, Object> userInputsMap, String processStep, VelocityContext context) {
		Iterator it = userInputsMap.keySet().iterator();
		boolean allOk = true;

		if (processStep.equals("11") || processStep.equals("15")) {
			while (it.hasNext()) {
				ArcheDataPath dataPath = (ArcheDataPath) it.next();
				if (!dataPath.getPath().contains(Q11_TEMPLATE_ROOT) && !dataPath.getPath().contains(Q15_TEMPLATE_ROOT))
					continue;
				
				Boolean anwser = Boolean.parseBoolean(userInputsMap.get(dataPath).toString());
				
				if (dataPath.getPath().equals(Q11_SECOND_QUESTION_NOT_OK) || //these questions should be anwsered with 'no'
					dataPath.getPath().equals(Q11_FIFTH_QUESTION_NOT_OK) ||
					dataPath.getPath().equals(Q11_EIGHT_QUESTION_NOT_OK) ||
					dataPath.getPath().equals(Q15_FIRST_QUESTION_NOT_OK)) {
						
					if (anwser) {
						context.put(convertPathToVelocityFormat(dataPath.getPath().replaceAll("/", "?").replaceAll("_", "!")), "false");
						allOk = false;
					}
					else {
						context.put(convertPathToVelocityFormat(dataPath.getPath().replaceAll("/", "?").replaceAll("_", "!")), "true");
					}
				}
				else {
					if (!anwser) {
						context.put(convertPathToVelocityFormat(dataPath.getPath().replaceAll("/", "?").replaceAll("_", "!")), "false");
						allOk = false;
					}
					else {
						context.put(convertPathToVelocityFormat(dataPath.getPath().replaceAll("/", "?").replaceAll("_", "!")), "true");
					}
				}
				
			}
			
		}

		return allOk;
	}
	
	/**
	 * Converts path to appropriate format for Velocity.
	 *  '[',']','!','?','.' -->> '_'
	 * 
	 * e.g. input:  ?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0009]?items[at0017]?value
	 *      output:  items_openEHR-EHR-EVALUATION_hu_koraki_01_v1__data_at0001__items_at0009__items_at0017__value
	 *      		_items_openEHR-EHR-EVALUATION_hu_koraki_01_v1__data_at0001__items_at0009__items_at0017__value
	 * @param path
	 * @return
	 */
	private String convertPathToVelocityFormat(String path) {
		if (path == null || path.length() == 0)
			return path;
		
		//remove '?'
		path = path.substring(path.indexOf('?') + 1);
		
		//replace
		path = path.replaceAll("\\[", "_");
		path = path.replaceAll("\\]", "_");
		path = path.replaceAll("\\?", "_");
		path = path.replaceAll("\\!", "_");
		path = path.replaceAll("\\.", "_");
		
		
		return path;
	}


}
