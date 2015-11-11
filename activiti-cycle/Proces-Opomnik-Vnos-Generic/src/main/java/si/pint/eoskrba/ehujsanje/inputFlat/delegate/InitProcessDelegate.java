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
package si.pint.eoskrba.ehujsanje.inputFlat.delegate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.log4j.MDC;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.archetypes.exceptions.EmptyParametersException;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.EnterWeightLossFreeEntryArcheSrc;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.delegate.AInitProcessDelegate;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

@Log4j
public class InitProcessDelegate extends AInitProcessDelegate implements
		Serializable {
	
	//weight path in archetype
	private static final String BODY_WEIGHT_ARCH_ID = "/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value";
	
	//weight path in archetype
	private static final String BODY_MASS_INDEX_ARCH_ID = "/items[openEHR-EHR-OBSERVATION.body_mass_index.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value";
	
	//comment path (weight) in archetype
	private static final String BODY_WEIGHT_COMMENT_ARCH_ID = "/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0024]/value";
	private static final String BODY_WEIGHT_COMMENT_VALUE = "prosti vnos";
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7450885070699274734L;

	@Override
	@SuppressWarnings("unused")
	public void execute(DelegateExecution execution) throws Exception {
		
		// get user (patient) from session
		String subjectString = (String) execution.getVariable(Constants.VAR_USERNAME_APP);
		if (subjectString == null) {
			throw new RuntimeException(
					"User on proces 'Hujsanje-Proces-Vnos-Vrednosti' not defined (hujsanje)!");
		}
	
		//load patient from LDAP
		User patient = UserRegistryFactory.getUserFromLDAP(subjectString, Role.PATIENT);
		patient.setPatientsCM(UserRegistryFactory.findPatientsDoctor(patient.getUsername())); //specific for eHusjanje: assigned coordinator is written in LDAP atribute 'patientsDoctorAtt'); //specific for eHusjanje: assigned coordinator is written in LDAP atribute 'patientsDoctorAtt'
		execution.setVariable(Constants.VAR_PATIENT, patient);
		
		
		EnterWeightLossFreeEntryArcheSrc service = new EnterWeightLossFreeEntryArcheSrc();
		if (!service.initInput())
			return;

		// user input data
		Map<ArcheDataPath, Object> userInputsMap = new HashMap<ArcheDataPath, Object>();
		
		//parameters for calculating ok/notOk/critical categories
		HashMap<String, String> map = new HashMap<String, String>();
		
		// look for all possible attributes on session
		Map allAtributes = service.getInputsMap();
		Iterator it = allAtributes.keySet().iterator();
		String bodyWeight = null;
		while (it.hasNext()) {
			String key = (String) it.next();
			String keyR = key.replaceAll("/", "?");
			keyR = keyR.replaceAll("_", "!");
			Object var = execution.getVariable(keyR);
			if (var != null && !var.equals("")) {
				userInputsMap.put(new ArcheDataPath(key), var);
				
				if (key.indexOf(BODY_WEIGHT_ARCH_ID) >= 0) {
					map.put(Constants.VAR_INPUT_BODY_WEIGHT, (String) var);
					bodyWeight = (String) var;
				}
			}
		}

		//calculate body mass index
		BigDecimal bodyHeightFirst = null;
		if (bodyWeight != null && !bodyWeight.equals("")) {
			bodyHeightFirst = CalcCategoryWeightLossValuesDelegate.getFirstBodyWeightFromDB(((String) execution.getVariable(Constants.VAR_USERNAME_APP)),patient.getUsername(), CalcCategoryWeightLossValuesDelegate.BODY_HEIGHT_EXIST_PATH);
			bodyHeightFirst = bodyHeightFirst.divide(new BigDecimal("100")); //convert to meters
			
			BigDecimal bodyHeightSquare = bodyHeightFirst.multiply(bodyHeightFirst);
			BigDecimal bodyWeightBD = new BigDecimal(bodyWeight); 
			BigDecimal bodyMassIndex = bodyWeightBD.divide(bodyHeightSquare, 2, RoundingMode.FLOOR);
			
			userInputsMap.put(new ArcheDataPath(BODY_MASS_INDEX_ARCH_ID), bodyMassIndex.doubleValue() + "");		
		}
		
		//add comment - to distinct this entry from periodic process
		userInputsMap.put(new ArcheDataPath(BODY_WEIGHT_COMMENT_ARCH_ID), BODY_WEIGHT_COMMENT_VALUE);		
		
		// data validation
		if (!service.setAndValidataData(userInputsMap))
			//throw new WrappedException(new EmptyParametersException("Validation for weight loss values was not ok!"));			
			new EmptyParametersException("Validation for weight loss values was not ok!");
		
		execution.setVariable(Constants.VAR_GENERIC_RESULT, map);

		// save data in XML form to DB
		MDC.put("user", patient.getUsername());
		MDC.put("userRole",patient.getRole().toString());
		MDC.put("task", "InitProcessDelegate");
		MDC.put("taskContent", "vnos vrednosti hujsanje");
		MDC.put("taskType", "H");		
		if (patient != null && patient.getUsername() != null
				&& !patient.getUsername().equals("")) {
			service.saveInput(SystemType.EOSKRBAPROCESSENGINE,((String) execution.getVariable(Constants.VAR_USERNAME_APP)),patient.getUsername());
			
			log.info("Pacient " + patient.getUsername() + " je izpolnil obrazec za hujsanje");
		}

	}


}
