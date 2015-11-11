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
package si.pint.eoskrba.utils;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;

import lombok.extern.log4j.Log4j;

import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.WeightLossStepCompletedSubTasksNoArcheSrv;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;

@Log4j
public class ArchetypeServiceUtils {
	
	public static final String HU_SUBTASK_NUMBER_TEMPLATE_PATH  = "/data[at0001]/items[at0002]/value";
	public static final String HU_SUBTASK_INPUTBODYWEIGHT_TEMPLATE_PATH  = "/data[at0001]/items[at0003]/value";
	public static final String HU_SUBTASK_CATEGORYNOTOK_TEMPLATE_PATH  = "/data[at0001]/items[at0004]/value";
	public static final String HU_SUBTASK_TEMPLATE_ROOT  = "openEHR-EHR-EVALUATION.hu_stevilka_podnaloge.v1";
	public static final String HU_SUBTASK_NUMBER_TEMPLATE_QUERY = "//items[@archetype_node_id='openEHR-EHR-EVALUATION.hu_stevilka_podnaloge.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0002']/value/magnitude/text()";
	public static final String HU_SUBTASK_INPUTBODYWEIGHT_TEMPLATE_QUERY = "//items[@archetype_node_id='openEHR-EHR-EVALUATION.hu_stevilka_podnaloge.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0003']/value/value/text()";
	public static final String HU_SUBTASK_CATEGORYNOTOK_TEMPLATE_QUERY = "//items[@archetype_node_id='openEHR-EHR-EVALUATION.hu_stevilka_podnaloge.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/value/text()";

	public static void updateCompletedSubTasksNo(int subTaskNo, DelegateExecution execution , String userId, String patientId) {
		
		// TODO: Pobriši stare xml-je, da se ne nabirajo
		
		// Obdelaj podatke
		String inputBodyWeight;
		@SuppressWarnings("unchecked")
		HashMap<String, String> genericResult_currVal = (HashMap<String, String>)execution.getVariable(Constants.VAR_GENERIC_RESULT);
		if(genericResult_currVal != null) {
			inputBodyWeight = genericResult_currVal.get(Constants.VAR_INPUT_BODY_WEIGHT);
		} else {
			inputBodyWeight = null;
		}
		if(inputBodyWeight == null) inputBodyWeight = "null";
		String categoryNotOk = (execution.getVariable(Constants.VAR_CATEGORY_NOT_OK)==null)?"null":(((Boolean)execution.getVariable(Constants.VAR_CATEGORY_NOT_OK))?"1":"0");
		
		// v eXist ustvari nov zapis o zadnji dokončani nalogi
		WeightLossStepCompletedSubTasksNoArcheSrv service;
		service = new WeightLossStepCompletedSubTasksNoArcheSrv();
		service.initInput();
		Map<ArcheDataPath, String> userInputsMap = new HashMap<ArcheDataPath, String>();
		userInputsMap.put(new ArcheDataPath(HU_SUBTASK_NUMBER_TEMPLATE_PATH), subTaskNo + "" );
		userInputsMap.put(new ArcheDataPath(HU_SUBTASK_INPUTBODYWEIGHT_TEMPLATE_PATH), inputBodyWeight);
		userInputsMap.put(new ArcheDataPath(HU_SUBTASK_CATEGORYNOTOK_TEMPLATE_PATH), categoryNotOk);
		service.setAndValidataData(userInputsMap);
		service.saveInput(SystemType.EOSKRBAPROCESSENGINE, userId, patientId);
		
	}
	
	public static CompletedSubTasksData getCompletedSubTasksData(String userId,String patientId) {
		
		// Nastavi spremenljivke
		String taskNoStr;
		String bodyWeightStr;
		String categoryNotOkStr;
		
		// Pridobi vrednosti iz exista
		try {
			
			taskNoStr = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(
				DBManager.ENGINE_USER_ID,
				patientId,
				HU_SUBTASK_TEMPLATE_ROOT,
				HU_SUBTASK_NUMBER_TEMPLATE_QUERY
			);
			bodyWeightStr = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(
				DBManager.ENGINE_USER_ID,
				patientId,
				HU_SUBTASK_TEMPLATE_ROOT,
				HU_SUBTASK_INPUTBODYWEIGHT_TEMPLATE_QUERY
			);
			categoryNotOkStr = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(
				DBManager.ENGINE_USER_ID,
				patientId,
				HU_SUBTASK_TEMPLATE_ROOT,
				HU_SUBTASK_CATEGORYNOTOK_TEMPLATE_QUERY
			);
			
		} catch(Exception e) {
			log.info("Napaka pri branju stevilke podnaloge pri pacientu " + patientId + ".");
			e.printStackTrace();
			return null;
		}
		
		if(taskNoStr != null){
		
			int taskNo = Integer.parseInt(taskNoStr);
			return new CompletedSubTasksData(taskNo,bodyWeightStr,categoryNotOkStr);
			
		} else {
			return null;
		}
		
	}
	
	public static class CompletedSubTasksData {
		
		public int taskNo;
		public String bodyWeight;
		public boolean bodyWeightIsNull;
		public boolean categoryNotOk;
		public boolean categoryNotOkIsNull;
		
		public CompletedSubTasksData(int taskNo, String bodyWeight, String categoryNotOk) {
			
			this.taskNo = taskNo;

			if(bodyWeight.equals("null")) this.bodyWeightIsNull = true;
			else this.bodyWeightIsNull = false;
			this.bodyWeight = bodyWeight;
			
			if(categoryNotOk.equals("null")) this.categoryNotOkIsNull = true;
			else {
				this.categoryNotOkIsNull = false;
				int boolVal = Integer.parseInt(categoryNotOk);
				this.categoryNotOk = (boolVal==1);
			}
			
		}
		
	}
	
}
