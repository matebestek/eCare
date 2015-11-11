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

import java.util.HashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.User;
import si.pint.eoskrba.utils.ArchetypeServiceUtils;
import si.pint.eoskrba.utils.ArchetypeServiceUtils.CompletedSubTasksData;

public class CheckCompletedSubTasksDelegate implements JavaDelegate {
  
	public void execute(DelegateExecution execution) throws Exception {
		
		// Ugotovi zadnjo dokončano podnalogo v celoti
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
		CompletedSubTasksData lastCompletedSubTaskData = ArchetypeServiceUtils.getCompletedSubTasksData(
			patient.getUsername(),
			patient.getUsername()
		);
		
		int lastCompletedSubTask = lastCompletedSubTaskData.taskNo;
		
		// V subTaskVisitArray imamo sedaj polje 7-ih booleanov, ki določajo,
		// v katere userTask-e naj se spusti process flow.
		// Preverimo, katere izmed teh userTaskov je pacient že rešil in te
		// nastavimo na false, zato da se jih preskoči.
		Boolean[] subTaskVisitArray = (Boolean[]) execution.getVariable(Constants.VAR_SUBTASK_VISIT_ARRAY);
		
		// Opomba: posamezni koraki so
		// 1 = uvod
		// 2 = opkp
		// 3 = vnos meritev
		// 4 = izobraževanje
		// 5 = delovni listi
		// 6 = vprašalnik
		// 7 = spodbuda
		
		// Popravi subTaskVisitArray
		for(int i = 0; i < lastCompletedSubTask; i++) {
			subTaskVisitArray[i] = false;
		}
		execution.setVariable(Constants.VAR_SUBTASK_VISIT_ARRAY, subTaskVisitArray);
		
		// Nastavimo še nekatere procesne spremenljivke, če so bile shranjene
		// v existu
		if(!lastCompletedSubTaskData.bodyWeightIsNull) {
			execution.setVariable(Constants.VAR_INPUT_BODY_WEIGHT,lastCompletedSubTaskData.bodyWeight);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Constants.VAR_INPUT_BODY_WEIGHT, lastCompletedSubTaskData.bodyWeight);
			execution.setVariable(Constants.VAR_GENERIC_RESULT, map);
		}
		if(!lastCompletedSubTaskData.categoryNotOkIsNull) {
			execution.setVariable(Constants.VAR_CATEGORY_NOT_OK,lastCompletedSubTaskData.categoryNotOk);
		}
    
	}
  
}
