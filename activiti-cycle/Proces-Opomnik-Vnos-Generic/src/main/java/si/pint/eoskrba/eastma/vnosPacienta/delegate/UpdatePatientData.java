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
package si.pint.eoskrba.eastma.vnosPacienta.delegate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.log4j.MDC;
import org.openehr.validation.ValidationError;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.VnosPacientaArcheSrv;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.User;

@Log4j
/**
 * Popravljanje podatkov, ki se hranijo v eXist-u. Podatkov, ki so tudi v LDAP:
 * - uid (username oz. email)
 * - Ime Priimek
 * - datum rojstva
 * - email
 * - zdr. ustanova
 * - telefonska stevilka
 * - izpolnjuje pogoje
 * 
 * @author Blaz
 *
 */
public class UpdatePatientData implements JavaDelegate {

	//attribute base number for calculating asthma 
	public static final int ACT_BASE_ATT = 73;
	
	//archetype id for ACT
	public static final String ACT_ARCHETYPE_ID = "openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1";
	
	//template path for 'included in study'
	public static final String TEMPLATE_PATH_INCLUDED_IN_STUDY = "/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0095]/value";

	
	

	public void execute(DelegateExecution execution) throws Exception {
		
		VnosPacientaArcheSrv archeService = new VnosPacientaArcheSrv();
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
			keyR = keyR.replaceAll("_", "!");
//			log.info("key:" + key);
//			log.info("keyR:" + keyR);
			Object var = execution.getVariable(keyR);
			Set s = execution.getVariableNames();
			
			if (var != null && !var.equals("")) {
				userInputsMap.put(new ArcheDataPath(key), var);
				if (key.indexOf(ACT_ARCHETYPE_ID) >= 0){
					vsotaACT += (Integer) archeService.getValue(key, (String) var);
				}
			}
		}

		// v userInputs dam tudi vsoto in komentar
		userInputsMap
				.put(new ArcheDataPath(
						"/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0066]/value"),
						vsotaACT);
		
		//asthma index
		String attAsthmaIndex = "at00" + (ACT_BASE_ATT + vsotaACT);
		
		//urejenost astme
		userInputsMap
		.put(new ArcheDataPath(
				"/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0073]/value"),
				 attAsthmaIndex); //at0073 dejansko ne obstaja, ke pa max at0098
				
		//already included in study
		userInputsMap.put(new ArcheDataPath(TEMPLATE_PATH_INCLUDED_IN_STUDY), "true");
		
		User u = SelectClinicalTrialGroupDelegate.createUserFromContext(execution, archeService);
		
		log.info("Oseba " + ((String) execution.getVariable(Constants.VAR_USERNAME_APP)) + " popravlja podatke o pacientu: " + u.getUsername());
		
		//archetype data ok
		boolean patientDataParseOk = false;
		
		// data validation
		if (userInputsMap.isEmpty()	|| !archeService.setAndValidataData(userInputsMap)) {
			if(archeService.getErrorList() != null){
				for(ValidationError ve :archeService.getErrorList()){
					log.error("path:" + ve.getArchetypePath() + " napaka:" + ve.getErrorType().toString());
				}
			}
		} else {
			patientDataParseOk = true;
		}
		
		String appUserId = ((String) execution.getVariable(Constants.VAR_USERNAME_APP)); 
		String appUserRole = UserRegistryFactory.getUserFromLDAP(appUserId).getRole().getName();
		
		MDC.put("user", appUserId);
		MDC.put("userRole", appUserRole);		
		MDC.put("taskContent", "");				
		MDC.put("taskType", EMPLOYEE_TYPE_ENUM.ASTMA.toDomainName());
		Map<String, Object> allVarsFromWeb = execution.getVariables();
		
		if(allVarsFromWeb.containsKey("newVisit") && (Boolean)allVarsFromWeb.get("newVisit")){//NEW VISIT TO THE DOCTOR
			MDC.put("task", "Dodajanje novega obiska");
			log.info("Oseba " + appUserId + " dodaja nov obisk za pacienta: " + u.getUsername());
			String visitFileName = null;
			if (patientDataParseOk) {// save data in XML form to DB - for all patients
				visitFileName = archeService.saveInputReturnFilename(SystemType.EOSKRBAPROCESSENGINE,appUserId,u.getUsername());			
				
				log.info("Dodajanje novega obiska za pacienta: " + u.getUsername() + " filename="+visitFileName);
			}
			// nastavim spremenljivk skupinaEksperiment, skupinaKontrolna ali
			// notSatisfyingConditions
			if (!patientDataParseOk) {
				log.info("Obisk za pacienta " + u.getUsername() + " ni uspešno dodan");
				return;
			}
			DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).addNewVisit(appUserId, appUserRole,u.getUsername(),visitFileName,"",EMPLOYEE_TYPE_ENUM.ASTMA.toDomainName());
			log.info("Nov obisk s strani osebe " + ((String) execution.getVariable(Constants.VAR_USERNAME_APP)) + " je dodan za pacienta: " + u.getUsername());
		
		} else {//UPDATE OF THE LATEST DATA (also the latest visit)
			MDC.put("task", "Posodabljanje osebnih podatkov pacienta (podatkov prvega pregleda)");
			if (patientDataParseOk) {// save data in XML form to DB - for all patients
				archeService.saveInput(SystemType.EOSKRBAPROCESSENGINE,appUserId,u.getUsername());
			}
			
			// nastavim spremenljivk skupinaEksperiment, skupinaKontrolna ali
			// notSatisfyingConditions
			if (!patientDataParseOk) {
				log.info("Pacientu " + u.getUsername() + " se niso pravilno posodobili podatki");
				return;
			}
			log.info("Oseba " + ((String) execution.getVariable(Constants.VAR_USERNAME_APP)) + " je popravila podatke o pacientu: " + u.getUsername());
		}
		

	}
	

}
