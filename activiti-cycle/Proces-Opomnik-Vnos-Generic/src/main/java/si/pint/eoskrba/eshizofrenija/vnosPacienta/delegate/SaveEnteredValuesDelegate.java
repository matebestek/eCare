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
package si.pint.eoskrba.eshizofrenija.vnosPacienta.delegate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lombok.extern.apachecommons.CommonsLog;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.openehr.validation.ValidationError;

import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.EnterDiValuesArcheSrc;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;

@CommonsLog
public class SaveEnteredValuesDelegate implements JavaDelegate {
//TODO: TOLE JE TREBA IMPLEMENTRAT ZA ESHIZOFRENIJO, kER JE TALE KODA ZA DIABETES!!!!	
	// wonca archetype
	public static final String WONCA_ARCHETYPE_ID = "openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo.v1";
	//wonca sum path
	public static final String WONCA_POINTS_SUM = "/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0047]/value";
//	//wonca comment
//	public static final String WONCA_COMMENT = "/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0048]/value";
	
	public void execute(DelegateExecution execution) throws Exception {
		
		EnterDiValuesArcheSrc archeService = new EnterDiValuesArcheSrc();
		if (!archeService.initInput())
			return;

		// user input data
		Map<ArcheDataPath, Object> userInputsMap = new HashMap<ArcheDataPath, Object>();
		
		// look for all possible attributes on session
		Map allAtributes = archeService.getInputsMap();
		Iterator it = allAtributes.keySet().iterator();
		int sumWonca = 0;
		boolean satisfyingConditions = false;

		while (it.hasNext()) {
			String key = (String) it.next();
			String keyR = key.replaceAll("/", "?");
			keyR = keyR.replaceAll("_", "!");
			
			Object var = execution.getVariable(keyR);
			Set s = execution.getVariableNames();

			if (var != null && !var.equals("")) {
				userInputsMap.put(new ArcheDataPath(key), var);
				if (key.indexOf(WONCA_ARCHETYPE_ID) >= 0) {
					sumWonca += (Integer) archeService.getValue(key, (String) var);
				}
			}
		}
		
		// v userInputs dam tudi vsoto in komentar
		if (sumWonca > 0) {
			userInputsMap.put(new ArcheDataPath(WONCA_POINTS_SUM), sumWonca);		
		}
		
		// data validation
		if (!archeService.setAndValidataData(userInputsMap)) {
			satisfyingConditions = false;
			if (archeService.getErrorList() != null) {
				for (ValidationError ve : archeService.getErrorList()) {
					log.error("path:" + ve.getArchetypePath() + " napaka:" + ve.getErrorType().toString());
				}
			}
			return;
		}
		
		archeService.setAndValidataData(userInputsMap); //repopulate RMObject
		String user = (String) execution.getVariable(Constants.VAR_USERNAME_APP);
		archeService.saveInput(SystemType.EOSKRBAPROCESSENGINE,((String) execution.getVariable(Constants.VAR_USERNAME_APP)),user);
		
		
	}
}
