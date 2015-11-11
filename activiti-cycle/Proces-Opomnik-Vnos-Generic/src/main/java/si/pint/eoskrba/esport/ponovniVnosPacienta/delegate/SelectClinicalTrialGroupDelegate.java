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
package si.pint.eoskrba.esport.ponovniVnosPacienta.delegate;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.log4j.MDC;
import org.openehr.validation.ValidationError;

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.PonovniVnosSportArcheSrv;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;


@Log4j
public class SelectClinicalTrialGroupDelegate implements JavaDelegate {

	// template path for 'included in study'
	public static final String TEMPLATE_PATH_INCLUDED_IN_STUDY = "/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0012]/value";
	// 'other conditions' template path
	public static final String TEMPLATE_PATH_OTHER_CONDITIONS = "/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0011]/value";
	//archetype id for 'basic patient data'
	public static final String BASIC_DATA_ARCHETYPE_ID = "openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1";
	

	public void execute(DelegateExecution execution) throws Exception {

		PonovniVnosSportArcheSrv archeService = new PonovniVnosSportArcheSrv();
		if (!archeService.initInput())
			return;

		// user input data
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();

		// look for all possible attributes on session
		Map allArcheAtts = archeService.getInputsMap();
		Iterator it = allArcheAtts.keySet().iterator();
		


		while (it.hasNext()) {
			String key = (String) it.next();
			String keyR = key.replaceAll("/", "?");
			keyR = keyR.replaceAll("_", "!");
			//String keyR = key.replaceAll("\\?", "/");
			//keyR = keyR.replaceAll("!", "_");
			
			Object var = execution.getVariable(keyR);

			if (var != null && !var.equals("")) {
				//System.out.println("variables.put(\""+keyR+"\", new Integer(\""+var+"\"));");
				userInputsMap.put(new ArcheDataPath(key), var);
			}
		}
		

		//User u = createUserFromContext(execution, archeService);
		// archetype data ok
		boolean patientDataParseOk = false;

		//User loggedMedical = UserRegistryFactory.getUserFromLDAP(u.getPatientsCM()); //logged user

		// data validation
		//data not OK
		if (userInputsMap.isEmpty()	|| !archeService.setAndValidataData(userInputsMap)) {
			if (archeService.getErrorList() != null) {
				for (ValidationError ve : archeService.getErrorList()) {
					log.error("path:" + ve.getArchetypePath() + " napaka:"	+ ve.getErrorType().toString());
				}
			}
		//data OK
		} else {
			patientDataParseOk = true;
		}
		
				
		//logging
		MDC.put("user", execution.getVariable(Constants.VAR_USERNAME_APP));
		//MDC.put("userRole", loggedMedical.getRole().getName());
		MDC.put("task", "Ponovni vnos pacienta v študijo");
		MDC.put("taskContent", "");
		MDC.put("taskType", EMPLOYEE_TYPE_ENUM.SPORT.toDomainName());
		
		if (patientDataParseOk) {// save data in XML form to DB - for all patients
		
			archeService.setAndValidataData(userInputsMap); // repopulate RMObject
			archeService.saveInput(SystemType.EOSKRBAPROCESSENGINE,((String) execution.getVariable(Constants.VAR_USERNAME_APP)),((String) execution.getVariable(Constants.VAR_USERNAME)));			
			
			log.info("Ponovno so bili vneseni podatki za Šport za klienta: " + ((String) execution.getVariable(Constants.VAR_USERNAME)));
		}

	}
	
	public static void main(String[] argv) {
		//putMedicineValueToList(null, "", null);
	}
}
