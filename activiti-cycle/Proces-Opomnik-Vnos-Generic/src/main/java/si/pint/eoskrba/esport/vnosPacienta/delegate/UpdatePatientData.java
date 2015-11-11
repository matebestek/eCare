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
package si.pint.eoskrba.esport.vnosPacienta.delegate;

import java.util.Iterator;
import java.util.LinkedHashMap;
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
import si.pint.archetypes.service.VnosPacientaSportArcheSrv;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.ehujsanje.enterPatient.delegate.SelectClinicalTrialGroupDelegate;
import si.pint.eoskrba.model.User;

@Log4j
public class UpdatePatientData implements JavaDelegate {
	
	// xPath poti do raznih delov template-a
	public static final String TEMPLATE_PATH_INCLUDED_IN_STUDY = "/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0012]/value";

	public void execute(DelegateExecution execution) throws Exception {

		// Pridobi archetype service (archetype-api.jar)
		VnosPacientaSportArcheSrv archeService = new VnosPacientaSportArcheSrv();
		if (!archeService.initInput())
			return;

		// Pridobi vse spremenljivke, ki so bile podane
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		Map allArcheAtts = archeService.getInputsMap();
		Map allAtributes = execution.getVariables();
		Iterator it = allAtributes.keySet().iterator();

		while (it.hasNext()) {
			
			// Dekodiraj ime spremenljivke
			String key = (String) it.next();
			String keyR = key.replaceAll("\\?", "/");
			keyR = keyR.replaceAll("!", "_");
			
			Object var = execution.getVariable(key);
			Set s = execution.getVariableNames();

			// Shrani spremenljivko v map
			if (var != null && !var.equals("")) {
				if (allArcheAtts.containsKey(keyR)) {
					userInputsMap.put(new ArcheDataPath(keyR), var);
				}
			}
			
		}
		
		//already included in study
		userInputsMap.put(new ArcheDataPath(TEMPLATE_PATH_INCLUDED_IN_STUDY), "true");

		// Pridobi uporabnika (pacienta)
		User u = SelectClinicalTrialGroupDelegate.createUserFromContext(execution, archeService);
		
		// Pripravi spremenljivke
		boolean patientDataParseOk = false;

		// Preveri ustreznost podatkov
		if (userInputsMap.isEmpty()	|| !archeService.setAndValidataData(userInputsMap)) {
			if (archeService.getErrorList() != null) {
				for (ValidationError ve : archeService.getErrorList()) {
					log.error("path:" + ve.getArchetypePath() + " napaka:"	+ ve.getErrorType().toString());
				}
			}
		} else {
			patientDataParseOk = true;
		}
		
		String appUserId = ((String) execution.getVariable(Constants.VAR_USERNAME_APP)); 
		String appUserRole = UserRegistryFactory.getUserFromLDAP(appUserId).getRole().getName();
		
		// Log
		MDC.put("user", appUserId);
		MDC.put("userRole", appUserRole);		
		MDC.put("taskContent", "");
		MDC.put("taskType", EMPLOYEE_TYPE_ENUM.DIABETES.toDomainName());
		
		// Če gre za nov obisk, potem prilagodimo log-anje in v bazo shranimo metapodatke
		if(allAtributes.containsKey("newVisit") && (Boolean)allAtributes.get("newVisit")){
			
			// Log
			MDC.put("task", "Dodajanje novega obiska");
			log.info("Oseba " + ((String) execution.getVariable(Constants.VAR_USERNAME_APP)) + " dodaja nov obisk za pacienta: " + u.getUsername());
			
			// Zapiši metapodatke v bazo v xml obliki, če so podatki ok
			String visitFileName = null;
			if (patientDataParseOk) {
				
				visitFileName = archeService.saveInputReturnFilename(
					SystemType.EOSKRBAPROCESSENGINE,
					((String) execution.getVariable(Constants.VAR_USERNAME_APP)),
					u.getUsername()
				);			
				log.info("Dodajanje novega obiska za pacienta: " + u.getUsername() + " filename="+visitFileName);
				
			} else {
				
				// Podatki niso ok
				log.info("Obisk za pacienta " + u.getUsername() + " ni uspešno dodan");
				return;
				
			}
			
			// Zapiši v eXist
			DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).addNewVisit(
				appUserId,
				appUserRole,
				u.getUsername(),
				visitFileName,
				"",
				EMPLOYEE_TYPE_ENUM.SPORT.toDomainName()
			);
			log.info("Nov obisk s strani osebe " + ((String) execution.getVariable(Constants.VAR_USERNAME_APP)) + " je dodan za pacienta: " + u.getUsername());

		} else {
			
			// Log
			MDC.put("task", "Posodabljanje osebnih podatkov pacienta (podatkov prvega pregleda)");
			log.info("Oseba " + ((String) execution.getVariable(Constants.VAR_USERNAME_APP)) + " popravlja podatke o pacientu: " + u.getUsername());
			
			if (patientDataParseOk) {
				
				archeService.saveInput(
					SystemType.EOSKRBAPROCESSENGINE,
					((String) execution.getVariable(Constants.VAR_USERNAME_APP)),
					u.getUsername()
				);			
				log.info("Posodabljanje osebnih podatkov pacienta: " + u.getUsername());
				
			} else {
				log.info("Pacientu " + u.getUsername() + " se niso pravilno posodobili podatki");
				return;
			}
			
			log.info("Oseba " + ((String) execution.getVariable(Constants.VAR_USERNAME_APP)) + " je popravila podatke o pacientu: " + u.getUsername());
			
		}

	}

}
