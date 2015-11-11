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
package si.pint.eoskrba.esport.inputSport.delegate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.log4j.MDC;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.archetypes.exceptions.EmptyParametersException;
import si.pint.archetypes.exceptions.LdapException;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.EnterSpValuesArcheSrv;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.listeners.APatientEntersValueCompleteListener;
import si.pint.eoskrba.model.User;

@Log4j
public class PatientEntersSportValuesCompleteListener extends
		APatientEntersValueCompleteListener implements Serializable {

	// Pot do telesne mase
	private static final String BODY_WEIGHT_ARCH_ID = "/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value";
	
	private static final long serialVersionUID = -652573758553452378L;

	@Override
	public void notify(DelegateTask delegateTask) {
	
		saveInputAndSetCalcData(delegateTask.getExecution());
	}
	
	public static boolean saveInputAndSetCalcData(DelegateExecution execution) {
		
		EnterSpValuesArcheSrv service = new EnterSpValuesArcheSrv();
		if (!service.initInput())
			return false;

		// user input data
		Map<ArcheDataPath, Object> userInputsMap = new HashMap<ArcheDataPath, Object>();
		
		//parameters for calculating ok/notOk/critical categories
		HashMap<String, String> map = new HashMap<String, String>();
		
		// look for all possible attributes on session
		Map allAtributes = service.getInputsMap();
		Iterator it = allAtributes.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String keyR = key.replaceAll("/", "?");
			keyR = keyR.replaceAll("_", "!");
			Object var = execution.getVariable(keyR);
			//System.out.println(key);
			//System.out.println("null");
			if (var != null && !var.equals("")) {
				
				if (key.indexOf(BODY_WEIGHT_ARCH_ID) >= 0) {
					map.put(Constants.VAR_INPUT_BODY_WEIGHT, (String) var);
				}

				userInputsMap.put(new ArcheDataPath(key), var);		
			}
		}

		
		// data validation
		if (!service.setAndValidataData(userInputsMap)) 
			//throw new WrappedException(new EmptyParametersException("Validation for diabetes values was not ok!"));
			new EmptyParametersException("Validation for diabetes values was not ok!");
			
		
		execution.setVariable(Constants.VAR_GENERIC_RESULT, map);

		// save data in XML form to DB
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
		MDC.put("user", patient.getUsername());
		MDC.put("userRole",patient.getRole().toString());
		MDC.put("task", "PatientEntersSportValuesCompleteListener");
		MDC.put("taskContent", "vnos vrednosti sport (periodicni)");
		MDC.put("taskType", "P");		
		if (patient != null && patient.getUsername() != null
				&& !patient.getUsername().equals("")) {
			
			try {
				execution.setVariable(Constants.VAR_USERNAME_APP,UserRegistryFactory.findPatientsDoctor(patient.getUsername()));
			} catch (LdapException e) {
				return false;
			}

			service.saveInput(SystemType.EOSKRBAPROCESSENGINE,((String) execution.getVariable(Constants.VAR_USERNAME_APP)),patient.getUsername());
			
			log.info("Udeleženec " + patient.getUsername() + " je izpolnil obrazec za šport");
			return true;
		}			
		
		return false;
	}

}
