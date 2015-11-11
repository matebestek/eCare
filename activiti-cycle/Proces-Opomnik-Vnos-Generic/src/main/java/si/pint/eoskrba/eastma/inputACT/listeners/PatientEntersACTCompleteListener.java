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
package si.pint.eoskrba.eastma.inputACT.listeners;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.log4j.MDC;

import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.ACTArcheSrv;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.listeners.APatientEntersValueCompleteListener;
import si.pint.eoskrba.model.User;

@Log4j
public class PatientEntersACTCompleteListener extends
		APatientEntersValueCompleteListener implements Serializable {
	private static final long serialVersionUID = -5117486868901358150L;

	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();

		
		
		ACTArcheSrv archeService = new ACTArcheSrv();
		if (!archeService.initInput())
			return;

		// user input data
		Map<ArcheDataPath, Object> userInputsMap = new HashMap<ArcheDataPath, Object>();

		// look for all possible attributes on session
		Map allAtributes = archeService.getInputsMap();
		Iterator it = allAtributes.keySet().iterator();
		int vsotaACT = 0;
		while (it.hasNext()) {
			String key = (String) it.next();
			String keyR = key.replaceAll("/", "?");
			Object var = execution.getVariable(keyR);
			if (var != null) {
				userInputsMap.put(new ArcheDataPath(key), var);
				vsotaACT += (Integer) archeService.getValue(key, (String) var);
				try {
				} catch (ClassCastException e) {
					// ne naredim nič, ker na ta način preskočim komentar, ki je
					// String
				}
			}

		}

		// v userInputs dam tudi vsoto in komentar
		userInputsMap
				.put(new ArcheDataPath(
						"/data[at0001]/events[at0002]/data[at0003]/items[at0066]/value"),
						vsotaACT);
		userInputsMap
		.put(new ArcheDataPath(
				"/data[at0001]/events[at0002]/data[at0003]/items[at0073]/value"),
				"at00" + (73+vsotaACT));//at0073 dejansko ne obstaja, ke pa max at0098
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0064]/value"), "");
		
		// data validation
		if (userInputsMap.isEmpty()
				|| !archeService.setAndValidataData(userInputsMap)) {

			// return;
		} else {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Constants.VAR_ACT_RESULT, vsotaACT + "");
			execution.setVariable(Constants.VAR_GENERIC_RESULT, map);
		}

		// save data in XML form to DB
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
		MDC.put("user", patient.getUsername());
		MDC.put("userRole",patient.getRole().toString());
		MDC.put("task", "PatientEntersACTCompleteListener");
		MDC.put("taskContent", "vnosACT");
		MDC.put("taskType", "A");		
		if (patient != null && patient.getUsername() != null
				&& !patient.getUsername().equals("")) {
			archeService.saveInput(SystemType.EOSKRBAPROCESSENGINE,patient.getUsername(),patient.getUsername());
			log.info("Pacient " + patient.getUsername() + " je izpolnil ACT");
		}
		
	}
}
